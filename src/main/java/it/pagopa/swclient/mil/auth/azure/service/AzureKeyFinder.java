/*
 * AzureKeyFinder.java
 *
 * 23 mar 2024
 */
package it.pagopa.swclient.mil.auth.azure.service;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import com.azure.core.util.CoreUtils;

import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
import it.pagopa.swclient.mil.auth.azure.identity.bean.AccessToken;
import it.pagopa.swclient.mil.auth.azure.identity.bean.Scope;
import it.pagopa.swclient.mil.auth.azure.identity.service.AzureIdentityService;
import it.pagopa.swclient.mil.auth.azure.keyvaultkeys.bean.JsonWebKey;
import it.pagopa.swclient.mil.auth.azure.keyvaultkeys.bean.JsonWebKeyOperation;
import it.pagopa.swclient.mil.auth.azure.keyvaultkeys.bean.KeyAttributes;
import it.pagopa.swclient.mil.auth.azure.keyvaultkeys.bean.KeyBundle;
import it.pagopa.swclient.mil.auth.azure.keyvaultkeys.bean.KeyCreateParameters;
import it.pagopa.swclient.mil.auth.azure.keyvaultkeys.bean.KeyListResult;
import it.pagopa.swclient.mil.auth.azure.keyvaultkeys.client.AzureKeyVaultKeysClient;
import it.pagopa.swclient.mil.auth.azure.keyvaultkeys.service.AzureKeyVaultKeys;
import it.pagopa.swclient.mil.auth.azure.keyvaultkeys.util.KidUtils;
import it.pagopa.swclient.mil.auth.bean.AuthErrorCode;
import it.pagopa.swclient.mil.auth.bean.KeyType;
import it.pagopa.swclient.mil.auth.bean.KeyUse;
import it.pagopa.swclient.mil.auth.bean.PublicKey;
import it.pagopa.swclient.mil.auth.bean.PublicKeys;
import it.pagopa.swclient.mil.auth.service.crypto.KeyFinder;
import it.pagopa.swclient.mil.auth.util.AuthError;
import it.pagopa.swclient.mil.auth.util.AuthException;
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
	private AzureKeyVaultKeys keysService;

	/*
	 * Azure Identity service.
	 */
	private AzureIdentityService identityService;

	/**
	 * 
	 * @param cryptoperiod
	 * @param keysize
	 * @param keysService
	 * @param identityService
	 */
	@Inject
	AzureKeyFinder(
		@ConfigProperty(name = "cryptoperiod", defaultValue = "86400") long cryptoperiod,
		@ConfigProperty(name = "keysize", defaultValue = "4096") int keysize,
		AzureKeyVaultKeys keysService,
		AzureIdentityService identityService) {
		this.cryptoperiod = cryptoperiod;
		this.keysize = keysize;
		this.keysService = keysService;
		this.identityService = identityService;
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
	 * Converts a KeyBundle in PublicKey.
	 * 
	 * @param key
	 * @return
	 */
	private PublicKey toPublicKey(KeyBundle key) {
		JsonWebKey jwk = key.getKey();
		KeyAttributes props = key.getAttributes();
		return new PublicKey(
			jwk.getE(),
			KeyUse.SIG,
			KidUtils.toMilKid(jwk.getKid()),
			jwk.getN(),
			KeyType.RSA,
			props.getExp(),
			props.getCreated());
	}

	/**
	 * Finds all valid public keys and sort them desc for expiration.
	 *
	 * @return
	 */
	@Override
	public Uni<PublicKeys> findPublicKeys() {
		Log.debug("Search for valid keys");

		keysService.getKeys()
			.map(KeyListResult::getValue)
			.onItem().transformToMulti(keyItems -> Multi.createFrom().items(keyItems.stream()));
			/*
			.flatMap(list -> Multi.createFrom().items(list));
			.map(keyProperties -> {
				Log.debugf("Search for versions of key [%s]", keyProperties.getName());
				return keysService.listPropertiesOfKeyVersions(keyProperties.getName());
			})
			.flatMap(keyProperties -> {
				Log.debugf("Search for details of key [%s/%s]", keyProperties.getName(), keyProperties.getVersion());
				return keysService.getKey(keyProperties.getName(), keyProperties.getVersion());
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

		Log.errorf(e, AuthErrorCode.EXCEPTION_RETRIEVING_KEYS_DATA_MSG);
		*/
	}

	/**
	 * Creates a new RSA key pair and returns public key data.
	 * 
	 * @return
	 */
	private Uni<PublicKey> createKey() {
		String keyName = UUID.randomUUID().toString().replace("-", "");
		Log.debugf("Create new key: [%s]", keyName);

		OffsetDateTime now = OffsetDateTime.now();

		KeyCreateParameters keyCreateParameters = new KeyCreateParameters()
			.setKeySize(keysize)
			.setKeyOps(List.of(JsonWebKeyOperation.SIGN, JsonWebKeyOperation.VERIFY))
			.setAttributes(new KeyAttributes()
				.setEnabled(true)
				.setExportable(false)
				.setNbf(now.toEpochSecond())
				.setExp(now.plusSeconds(cryptoperiod).toEpochSecond()));

		return keysService.createKey(keyName, keyCreateParameters)
			.map(this::toPublicKey)
			.onFailure(t -> !(t instanceof AuthError || t instanceof AuthException))
			.transform(t -> {
				Log.errorf(t, AuthErrorCode.ERROR_CREATING_RSA_KEY_MSG);
				return new AuthError(AuthErrorCode.ERROR_CREATING_RSA_KEY_MSG, AuthErrorCode.ERROR_CREATING_RSA_KEY_MSG);
			});
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
				Mono<Optional<PublicKey>> publicKey = keysService.getKey(kidParts[0], kidParts[1])
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