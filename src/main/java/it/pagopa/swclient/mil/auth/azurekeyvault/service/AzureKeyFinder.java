/*
 * AzureKeyFinder.java
 *
 * 26 lug 2023
 */
package it.pagopa.swclient.mil.auth.azurekeyvault.service;

import static it.pagopa.swclient.mil.auth.ErrorCode.*;
import static it.pagopa.swclient.mil.auth.util.UniGenerator.item;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import io.quarkus.logging.Log;
import io.smallrye.mutiny.Context;
import io.smallrye.mutiny.ItemWithContext;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import it.pagopa.swclient.mil.auth.azurekeyvault.bean.CreateKeyRequest;
import it.pagopa.swclient.mil.auth.azurekeyvault.bean.GetAccessTokenResponse;
import it.pagopa.swclient.mil.auth.azurekeyvault.bean.GetKeyResponse;
import it.pagopa.swclient.mil.auth.azurekeyvault.bean.GetKeyVersionsResponse;
import it.pagopa.swclient.mil.auth.azurekeyvault.bean.GetKeysResponse;
import it.pagopa.swclient.mil.auth.azurekeyvault.bean.Key;
import it.pagopa.swclient.mil.auth.azurekeyvault.bean.KeyAttributes;
import it.pagopa.swclient.mil.auth.azurekeyvault.bean.KeyDetails;
import it.pagopa.swclient.mil.auth.azurekeyvault.bean.KeyNameAndVersion;
import it.pagopa.swclient.mil.auth.azurekeyvault.client.AzureAuthClient;
import it.pagopa.swclient.mil.auth.bean.KeyType;
import it.pagopa.swclient.mil.auth.bean.KeyUse;
import it.pagopa.swclient.mil.auth.bean.PublicKey;
import it.pagopa.swclient.mil.auth.bean.PublicKeys;
import it.pagopa.swclient.mil.auth.util.AuthError;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 * 
 * @author Antonio Tarricone
 */
@ApplicationScoped
public class AzureKeyFinder {
	/*
	 * 
	 */
	@RestClient
	AzureAuthClient authClient;

	/*
	 * 
	 */
	@Inject
	AzureKeyVaultService keyVaultService;

	/*
	 * 
	 */
	@ConfigProperty(name = "azure-auth-api.tenant-id")
	String tenantId;

	/*
	 * 
	 */
	@ConfigProperty(name = "azure-auth-api.client-id")
	String clientId;

	/*
	 * 
	 */
	@ConfigProperty(name = "azure-auth-api.client-secret")
	String clientSecret;

	/*
	 * Cryptoperiod of RSA keys in seconds.
	 */
	@ConfigProperty(name = "cryptoperiod", defaultValue = "86400")
	long cryptoperiod;

	/*
	 * Key size (modulus) of RSA keys in bits.
	 */
	@ConfigProperty(name = "keysize", defaultValue = "4096")
	int keysize;

	/*
	 * Grant types.
	 */
	private static final String CLIENT_CREDENTIALS = "client_credentials";

	/*
	 * Scope for authentication.
	 */
	private static final String VAULT = "https://vault.azure.net/.default";

	/*
	 * Key types.
	 */
	private static final String RSA = "RSA";

	/*
	 * Key operations.
	 */
	private static final String SIGN = "sign";
	private static final String VERIFY = "verify";
	private static final String[] OPS = new String[] {
		SIGN, VERIFY
	};

	/*
	 * Recovery levels.
	 */
	private static final String PURGEABLE = "Purgeable";

	/*
	 * Context keys.
	 */
	private static final String TOKEN = "token";
	private static final String KID = "kid";
	private static final String CURRENT_KEY_NAME_AND_VERSION = "name_and_version";

	/**
	 * 
	 * @param key
	 * @return
	 */
	private boolean isKeyEnabled(Key key) {
		boolean isKeyEnabled = key.getAttributes().getEnabled() != null && key.getAttributes().getEnabled();
		if (!isKeyEnabled)
			Log.warnf("The key %s is not enabled.", key.getKid());
		return isKeyEnabled;
	}

	/**
	 * 
	 * @param key
	 * @return
	 */
	private boolean isKeyCreationTimestampCoherent(Key key) {
		long now = Instant.now().getEpochSecond();
		boolean isKeyCreationTimestampCoherent = key.getAttributes().getCreated() != null && key.getAttributes().getCreated() <= now;
		if (!isKeyCreationTimestampCoherent)
			Log.warnf("The creation timestamp of %s is not valid. Found %s, expected a value less than %d.", key.getKid(), key.getAttributes().getCreated(), now);
		return isKeyCreationTimestampCoherent;
	}

	/**
	 * 
	 * @param key
	 * @return
	 */
	private boolean isKeyNotYetExpired(Key key) {
		long now = Instant.now().getEpochSecond();
		boolean isKeyNotYetExpired = key.getAttributes().getExp() != null && key.getAttributes().getExp() > now;
		if (!isKeyNotYetExpired)
			Log.warnf("The key %s is expired. Found %s, expected a value greater than %d.", key.getKid(), key.getAttributes().getExp(), now);
		return isKeyNotYetExpired;
	}

	/**
	 * 
	 * @param key
	 * @return
	 */
	private boolean isKeyNotBeforeMet(Key key) {
		long now = Instant.now().getEpochSecond();
		boolean isKeyNotBeforeMet = key.getAttributes().getNbf() != null && key.getAttributes().getNbf() <= now;
		if (!isKeyNotBeforeMet)
			Log.warnf("The 'not before' timestamp of %s is not valid. Found %s, expected a value less than %d.", key.getKid(), key.getAttributes().getNbf(), now);
		return isKeyNotBeforeMet;
	}

	/**
	 * 
	 * @param key
	 * @return
	 */
	private boolean areKeyAttributeValidKey(Key key) {
		if (key.getAttributes() == null) {
			Log.errorf("The key %s has null attributes.", key.getKid());
			return false;
		}
		return isKeyEnabled(key)
			&& isKeyCreationTimestampCoherent(key)
			&& isKeyNotYetExpired(key)
			&& isKeyNotBeforeMet(key);
	}

	/**
	 * 
	 * @param key
	 * @return
	 */
	private boolean isKeyValid(Key key) {
		if (key == null) {
			Log.error("The key is null.");
			return false;
		}
		return areKeyAttributeValidKey(key);
	}

	/**
	 * 
	 * @param key
	 * @return
	 */
	private boolean isKeyTypeRsa(KeyDetails key) {
		boolean isKeyTypeRsa = Objects.equals(key.getKty(), RSA);
		if (!isKeyTypeRsa)
			Log.warnf("The key type of %s is not a RSA. Found %s.", key.getKid(), key.getKty());
		return isKeyTypeRsa;
	}

	/**
	 * 
	 * @param key
	 * @return
	 */
	private boolean isKeySuitableForSignature(KeyDetails key) {
		String[] keyOps = key.getKeyOps();
		if (keyOps == null) {
			Log.errorf("The key %s has null ops.", key.getKid());
			return false;
		}

		List<String> keyOpList = Arrays.asList(keyOps);
		if (!keyOpList.contains(SIGN) || !keyOpList.contains(VERIFY)) {
			Log.warnf("The key %s is not suitable for signature. Found %s.", key.getKid(), keyOpList);
			return false;
		}

		return true;
	}

	/**
	 * 
	 * @param key
	 * @return
	 */
	private boolean isKeyValid(KeyDetails key) {
		boolean isKeyValid = isKeyValid((Key) key);
		if (!isKeyValid)
			return false;

		boolean isKeyTypeRsa = isKeyTypeRsa(key);
		if (!isKeyTypeRsa)
			return false;

		return isKeySuitableForSignature(key);
	}

	/**
	 * 
	 * @param key
	 * @return
	 */
	private boolean isKeyValid(GetKeyResponse getKeyResponse) {
		boolean isKeyValid = isKeyValid((Key) getKeyResponse.getKey());
		if (!isKeyValid)
			return false;

		boolean isKeyTypeRsa = isKeyTypeRsa(getKeyResponse.getKey());
		if (!isKeyTypeRsa)
			return false;

		return isKeySuitableForSignature(getKeyResponse.getKey());
	}

	/**
	 * 
	 * @param key
	 * @return
	 */
	private boolean checkThatKidContainsKeyName(Key key) {
		if (key.getKid() == null) {
			Log.error("Kid cannot be null.");
			return false;
		}

		if (key.getKid().split("/").length < 2) {
			Log.warnf("%s doesn't contain name.", key.getKid());
			return false;
		}

		return true;
	}

	/**
	 * 
	 * @param key
	 * @return
	 */
	private boolean checkThatKidContainsKeyNameAndVersion(Key key) {
		if (key.getKid() == null) {
			Log.error("Kid cannot be null.");
			return false;
		}

		if (key.getKid().split("/").length < 3) {
			Log.warnf("%s doesn't contain name and version.", key.getKid());
			return false;
		}

		return true;
	}

	/**
	 * 
	 * @param kid
	 * @return
	 */
	private String getKeyName(String kid) {
		String[] components = kid.split("/");
		return components[components.length - 1];
	}

	/**
	 * 
	 * @param kid
	 * @return
	 */
	private KeyNameAndVersion getKeyNameAndVersion(String kid) {
		String[] components = kid.split("/");
		return new KeyNameAndVersion(components[components.length - 2], components[components.length - 1]);
	}

	/**
	 * 
	 * @return
	 */
	private Uni<ItemWithContext<PublicKeys>> findPublicKeysWithContext() {
		Log.debug("Search for the keys.");
		Context context = Context.of();
		return authClient.getAccessToken(tenantId, CLIENT_CREDENTIALS, clientId, clientSecret, VAULT)
			.invoke(x -> Log.debug(x))
			.map(x -> {
				String t = x.getToken();
				if (t != null) {
					return x.getToken();
				} else {
					String message = String.format("[%s] Azure access token not valid.", AZURE_ACCESS_TOKEN_IS_NULL);
					Log.error(message);
					throw new AuthError(AZURE_ACCESS_TOKEN_IS_NULL, message);
				}
			}) // Getting the access token.
			.invoke(token -> context.put(TOKEN, token)) // Storing the access token in the context.
			.chain(token -> keyVaultService.getKeys(token)) // Retrieving the list of keys.
			.invoke(x -> Log.debug(x))
			.map(GetKeysResponse::getKeys) // Getting the list of keys from the response.
			.onItem().transformToMulti(keys -> Multi.createFrom().items(Arrays.stream(keys).filter(Objects::nonNull))) // Transforming the list of keys in a stream of events (one event for each key).
			.invoke(x -> Log.debug(x))
			.filter(this::checkThatKidContainsKeyName) // Filtering the key with invalid kid.
			.map(Key::getKid) // Getting the kid of the key.
			.invoke(kid -> context.put(KID, kid)) // Storing the kid in the context.
			.map(this::getKeyName) // Getting the name of the key from its kid.
			.invoke(x -> Log.debugf("Key name: %s", x))
			.onItem().transformToUniAndConcatenate(keyName -> keyVaultService
				.getKeyVersions(
					context.get(TOKEN),
					keyName)) // Retrieving the versions of the key.
			.invoke(x -> Log.debug(x))
			.map(GetKeyVersionsResponse::getKeys) // Getting the list of versions from the response.
			.onItem().transformToMultiAndConcatenate(keys -> Multi.createFrom().items(Arrays.stream(keys).filter(Objects::nonNull))) // Transforming the list of versions in a stream of events (one event for each version).
			.invoke(x -> Log.debug(x))
			.filter(this::isKeyValid) // Filtering not valid versions.
			.filter(this::checkThatKidContainsKeyNameAndVersion) // Filtering the version with invalid kid.
			.map(Key::getKid) // Getting kid of the version.
			.map(this::getKeyNameAndVersion) // Getting the name and the version from the kid.
			.invoke(x -> Log.debugf("Key name and version: %s", x))
			.invoke(keyNameAndVersion -> context.put(CURRENT_KEY_NAME_AND_VERSION, keyNameAndVersion)) // Storing the name and the version in the context.
			.onItem().transformToUniAndConcatenate(keyNameAndVersion -> keyVaultService
				.getKey(
					context.get(TOKEN),
					keyNameAndVersion.getName(),
					keyNameAndVersion.getVersion())) // Retrieving version details.
			.invoke(x -> Log.debug(x))
			.filter(this::isKeyValid) // Filtering not valid details.
			.map(GetKeyResponse::getKey) // Getting the details from the response.
			.invoke(x -> Log.debug(x))
			.map(key -> {
				KeyNameAndVersion keyNameAndVersion = context.get(CURRENT_KEY_NAME_AND_VERSION);
				return new PublicKey(
					key.getExponent(),
					KeyUse.sig,
					keyNameAndVersion.getName() + "/" + keyNameAndVersion.getVersion(),
					key.getModulus(),
					KeyType.RSA,
					key.getAttributes().getExp(),
					key.getAttributes().getCreated());
			}) // Generating internal public key object.
			.invoke(x -> Log.debug(x))
			.collect() // Collecting all internal public key objects.
			.asList() // Converting the events in an event that is the list of the collected internal public key objects.
			.invoke(x -> Log.debugf("Found %d valid key/s.", x.size()))
			.map(PublicKeys::new)
			.invoke(x -> Log.debug(x))
			.map(p -> new ItemWithContext<>(context, p))
			.onFailure(t -> !(t instanceof AuthError))
			.transform(t -> {
				String message = String.format("[%s] Error from Azure.", ERROR_FROM_AZURE);
				Log.errorf(t, message);
				throw new AuthError(ERROR_FROM_AZURE, message);
			});
	}

	/**
	 * 
	 * @return
	 */
	public Uni<PublicKeys> findPublicKeys() {
		return findPublicKeysWithContext().map(ItemWithContext::get);
	}

	/**
	 * 
	 * @param accessToken
	 * @return
	 */
	private Uni<PublicKey> createKey(String accessToken) {
		String keyName = UUID.randomUUID().toString().replace("-", "");
		long now = Instant.now().getEpochSecond();
		KeyAttributes attributes = new KeyAttributes(now, now + cryptoperiod, now, now, true, PURGEABLE, null, false);
		CreateKeyRequest createKeyRequest = new CreateKeyRequest(RSA, keysize, OPS, attributes);
		return keyVaultService.createKey(accessToken, keyName, createKeyRequest)
			.map(resp -> {
				if (isKeyValid(resp.getKey())) {
					KeyDetails key = resp.getKey();
					if (checkThatKidContainsKeyNameAndVersion(key)) {
						KeyNameAndVersion keyNameAndVersion = getKeyNameAndVersion(key.getKid());
						return new PublicKey(
							key.getExponent(),
							KeyUse.sig,
							keyNameAndVersion.getName() + "/" + keyNameAndVersion.getVersion(),
							key.getModulus(),
							KeyType.RSA,
							key.getAttributes().getExp(),
							key.getAttributes().getCreated());
					} else {
						String message = String.format("[%s] Error generating the key pair: kid doesn't contain name and version.", ERROR_GENERATING_KEY_PAIR);
						Log.fatal(message);
						throw new AuthError(ERROR_GENERATING_KEY_PAIR, message);
					}
				} else {
					String message = String.format("[%s] Error generating the key pair: invalid key pair has been generated.", ERROR_GENERATING_KEY_PAIR);
					Log.fatal(message);
					throw new AuthError(ERROR_GENERATING_KEY_PAIR, message);
				}
			})
			.onFailure(t -> !(t instanceof AuthError))
			.transform(t -> {
				String message = String.format("[%s] Error from Azure.", ERROR_FROM_AZURE);
				Log.errorf(t, message);
				throw new AuthError(ERROR_FROM_AZURE, message);
			});
	}

	/**
	 * Returns the valid key name and version with the greatest expiration. If there are no valid key a
	 * new one is generated.
	 * 
	 * @return
	 */
	public Uni<PublicKey> findValidPublicKeyWithGreatestExpiration() {
		Log.debug("Search for a valid key with greatest expiration.");
		return findPublicKeysWithContext()
			.chain(item -> {
				List<PublicKey> keys = item.get().getKeys();
				if (keys.isEmpty()) {
					/*
					 * There are no valid key: generating one.
					 */
					Log.debug("There are no valid key: generating one.");
					return createKey(item.context().get(TOKEN));
				} else {
					/*
					 * If there are valid keys, search for the key with the greatest expiration.
					 */
					Log.debug("Search for the key with the greatest expiration.");
					keys.sort((x, y) -> {
						if (x.getExp() < y.getExp()) {
							return 1;
						} else if (x.getExp() == y.getExp()) {
							return 0;
						} else {
							return -1;
						}
					});

					return item(keys.get(0));
				}
			});
	}

	/**
	 * 
	 * @return
	 */
	public Uni<Optional<PublicKey>> findPublicKey(String kid) {
		Log.debugf("Search for the public key %s.", kid);

		String[] components = kid.split("/");
		if (components.length < 2) {
			Log.warnf("%s doesn't contain name and version.", kid);
			return item(Optional.empty());
		}

		return authClient.getAccessToken(tenantId, CLIENT_CREDENTIALS, clientId, clientSecret, VAULT)
			.map(GetAccessTokenResponse::getToken) // Getting the access token.
			.chain(token -> keyVaultService.getKey(token, components[0], components[1]))
			.map(GetKeyResponse::getKey)
			.map(k -> {
				Log.debugf("Key %s found.", kid);
				if (isKeyValid(k)) {
					return Optional.of(new PublicKey(
						k.getExponent(),
						KeyUse.sig,
						kid,
						k.getModulus(),
						KeyType.RSA,
						k.getAttributes().getExp(),
						k.getAttributes().getCreated()));
				} else {
					Log.warnf("Key %s is not valid.", kid);
					return Optional.empty();
				}
			});
	}
}
