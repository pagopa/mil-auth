/*
 * AzureKeyFinder.java
 *
 * 23 mar 2024
 */
package it.pagopa.swclient.mil.auth.azure.service;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import com.azure.core.exception.HttpResponseException;
import com.azure.core.exception.ResourceNotFoundException;
import com.azure.security.keyvault.keys.KeyAsyncClient;
import com.azure.security.keyvault.keys.models.CreateRsaKeyOptions;
import com.azure.security.keyvault.keys.models.JsonWebKey;
import com.azure.security.keyvault.keys.models.KeyOperation;
import com.azure.security.keyvault.keys.models.KeyProperties;
import com.azure.security.keyvault.keys.models.KeyVaultKey;

import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
import it.pagopa.swclient.mil.auth.bean.AuthErrorCode;
import it.pagopa.swclient.mil.auth.bean.KeyType;
import it.pagopa.swclient.mil.auth.bean.KeyUse;
import it.pagopa.swclient.mil.auth.bean.PublicKey;
import it.pagopa.swclient.mil.auth.bean.PublicKeys;
import it.pagopa.swclient.mil.auth.service.crypto.KeyFinder;
import it.pagopa.swclient.mil.auth.util.AuthError;
import it.pagopa.swclient.mil.auth.util.UniGenerator;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import mutiny.zero.flow.adapters.AdaptersToFlow;
import reactor.core.publisher.Mono;

/**
 * 
 * @author Antonio Tarricone
 */
@ApplicationScoped
public class AzureKeyFinder implements KeyFinder {
	/*
	 * Cryptoperiod of RSA keys in seconds.
	 */
	private long cryptoperiod;

	/*
	 * Key size (modulus) of RSA keys in bits.
	 */
	private int keysize;

	/*
	 * Azure Key Vault client.
	 */
	private KeyAsyncClient keyClient;

	/**
	 * 
	 * @param cryptoperiod
	 * @param keysize
	 * @param keyClient
	 */
	@Inject
	AzureKeyFinder(@ConfigProperty(name = "cryptoperiod", defaultValue = "86400") long cryptoperiod, @ConfigProperty(name = "keysize", defaultValue = "4096") int keysize, KeyAsyncClient keyClient) {
		this.keyClient = keyClient;
		this.cryptoperiod = cryptoperiod;
		this.keysize = keysize;
	}

	/**
	 * 
	 * @return
	 */
	KeyAsyncClient getKeyClient() {
		return keyClient;
	}

	/**
	 * Verifies that the key is enabled, not expired, effective, RSA, suitable to sign and verify.
	 * 
	 * @param key
	 * @return
	 */
	private boolean isValid(KeyVaultKey key) {
		OffsetDateTime now = OffsetDateTime.now();
		KeyProperties props = key.getProperties();
		boolean isValid = props.isEnabled()
			&& props.getExpiresOn().isAfter(now)
			&& props.getNotBefore().isBefore(now)
			&& props.getCreatedOn().isBefore(now)
			&& key.getKeyType().equals(com.azure.security.keyvault.keys.models.KeyType.RSA)
			&& key.getKeyOperations().containsAll(Arrays.asList(KeyOperation.SIGN, KeyOperation.VERIFY));
		if (isValid) {
			Log.debugf("Key [%s/%s] is valid", key.getName(), props.getVersion());
		} else {
			Log.warnf("Key [%s/%s] is not valid", key.getName(), props.getVersion());
		}
		return isValid;
	}

	/**
	 * Converts a KeyVaultKey in PublicKey.
	 * 
	 * @param key
	 * @return
	 */
	private PublicKey toPublicKey(KeyVaultKey key) {
		JsonWebKey jwk = key.getKey();
		KeyProperties props = key.getProperties();
		return new PublicKey(
			jwk.getE(),
			KeyUse.SIG,
			props.getName() + "/" + props.getVersion(),
			jwk.getN(),
			KeyType.RSA,
			props.getExpiresOn().toEpochSecond(),
			props.getCreatedOn().toEpochSecond());
	}

	/**
	 * Finds all valid public keys.
	 *
	 * @return
	 */
	@Override
	public Uni<PublicKeys> findPublicKeys() {
		Log.debug("Search for valid keys");

		try {
			Mono<PublicKeys> publicKeys = keyClient.listPropertiesOfKeys()
				.flatMap(keyProperties -> {
					Log.debugf("Search for versions of key [%s]", keyProperties.getName());
					return keyClient.listPropertiesOfKeyVersions(keyProperties.getName());
				})
				.flatMap(keyProperties -> {
					Log.debugf("Search for details of key [%s/%s]", keyProperties.getName(), keyProperties.getVersion());
					return keyClient.getKey(keyProperties.getName(), keyProperties.getVersion());
				})
				.onErrorMap(t -> {
					Log.errorf(t, AuthErrorCode.ERROR_RETRIEVING_KEYS_DATA_MSG);
					return new AuthError(AuthErrorCode.ERROR_RETRIEVING_KEYS_DATA, AuthErrorCode.ERROR_RETRIEVING_KEYS_DATA_MSG);
				})
				.filter(this::isValid)
				.map(this::toPublicKey)
				.collectSortedList((x, y) -> {
					if (x.getExp() < y.getExp()) {
						return 1;
					} else if (x.getExp() == y.getExp()) {
						return 0;
					} else {
						return -1;
					}
				})
				.map(PublicKeys::new);

			return Uni.createFrom().publisher(AdaptersToFlow.publisher(publicKeys));
		} catch (HttpResponseException e) {
			Log.errorf(e, AuthErrorCode.EXCEPTION_RETRIEVING_KEYS_DATA_MSG);
			return UniGenerator.error(AuthErrorCode.EXCEPTION_RETRIEVING_KEYS_DATA, AuthErrorCode.EXCEPTION_RETRIEVING_KEYS_DATA_MSG);
		}
	}

	/**
	 * Creates a new RSA key pair.
	 * 
	 * @return
	 */
	private Uni<PublicKey> createKey() {
		String keyName = UUID.randomUUID().toString().replace("-", "");
		Log.debugf("Create new key [%s]", keyName);

		OffsetDateTime now = OffsetDateTime.now();

		CreateRsaKeyOptions createRsaKeyOptions = new CreateRsaKeyOptions(keyName)
			.setKeySize(keysize)
			.setNotBefore(now)
			.setExpiresOn(now.plusSeconds(cryptoperiod))
			.setEnabled(true)
			.setExportable(false)
			.setKeyOperations(KeyOperation.SIGN, KeyOperation.VERIFY);

		try {
			Mono<PublicKey> publicKey = keyClient.createRsaKey(createRsaKeyOptions)
				.onErrorMap(t -> {
					Log.errorf(t, AuthErrorCode.ERROR_CREATING_RSA_KEY_MSG);
					return new AuthError(AuthErrorCode.ERROR_CREATING_RSA_KEY, AuthErrorCode.ERROR_CREATING_RSA_KEY_MSG);
				})
				.map(this::toPublicKey);

			return Uni.createFrom().publisher(AdaptersToFlow.publisher(publicKey));
		} catch (HttpResponseException | NullPointerException e) {
			Log.errorf(e, AuthErrorCode.EXCEPTION_CREATING_RSA_KEY_MSG);
			return UniGenerator.error(AuthErrorCode.EXCEPTION_CREATING_RSA_KEY, AuthErrorCode.EXCEPTION_CREATING_RSA_KEY_MSG);
		}
	}

	/**
	 * Finds valid public key with the greatest expiration. If there are no valid key a new one is
	 * created.
	 *
	 * @return
	 */
	Uni<PublicKey> findPublicKey() {
		Log.debug("Search for valid key with greatest expiration");
		return findPublicKeys()
			.chain(publicKeys -> {
				List<PublicKey> keys = publicKeys.getKeys();
				if (keys.isEmpty()) {
					/*
					 * There are no valid key.
					 */
					Log.debug("There are no valid key");
					return createKey();
				} else {
					/*
					 * If there are valid keys, return the first (the list is desc sorted).
					 */
					PublicKey key = keys.get(0);
					Log.infof("Valid key found [%s]", key.getKid());
					return UniGenerator.item(key);
				}
			});
	}

	/**
	 * Finds the public key having the given kid.
	 *
	 * @param kid
	 * @return
	 */
	@Override
	public Uni<Optional<PublicKey>> findPublicKey(String kid) {
		Log.debugf("Search for the public key [%s]", kid);
		String[] kidParts = kid.split("/");

		if (kidParts.length == 2) {
			try {
				Mono<Optional<PublicKey>> publicKey = keyClient.getKey(kidParts[0], kidParts[1])
					.onErrorMap(t -> {
						Log.errorf(t, AuthErrorCode.ERROR_RETRIEVING_KEY_MSG);
						return new AuthError(AuthErrorCode.ERROR_RETRIEVING_KEY, AuthErrorCode.ERROR_RETRIEVING_KEY_MSG);
					})
					.map(key -> {
						if (isValid(key)) {
							Log.debug("Key found");
							return Optional.of(toPublicKey(key));
						} else {
							Log.warnf("Key found but not valid [%s]", kid);
							return Optional.empty();
						}
					});

				return Uni.createFrom().publisher(AdaptersToFlow.publisher(publicKey));
			} catch (ResourceNotFoundException e) {
				Log.warnf(e, "Key not found [%s]", kid);
				return UniGenerator.item(Optional.empty());
			} catch (HttpResponseException e) {
				Log.errorf(e, AuthErrorCode.EXCEPTION_RETRIEVING_KEY_MSG);
				return UniGenerator.error(AuthErrorCode.EXCEPTION_RETRIEVING_KEY, AuthErrorCode.EXCEPTION_RETRIEVING_KEY_MSG);
			}
		} else {
			Log.warnf("Invalid kid [%s]", kid);
			return UniGenerator.item(Optional.empty());
		}
	}
}