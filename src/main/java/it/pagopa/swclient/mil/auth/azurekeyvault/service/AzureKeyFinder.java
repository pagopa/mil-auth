/*
 * AzureKeyFinder.java
 *
 * 26 lug 2023
 */
package it.pagopa.swclient.mil.auth.azurekeyvault.service;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import io.quarkus.logging.Log;
import io.smallrye.mutiny.Context;
import io.smallrye.mutiny.ItemWithContext;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import it.pagopa.swclient.mil.auth.AuthErrorCode;
import it.pagopa.swclient.mil.auth.azurekeyvault.bean.CreateKeyRequest;
import it.pagopa.swclient.mil.auth.azurekeyvault.bean.GetAccessTokenResponse;
import it.pagopa.swclient.mil.auth.azurekeyvault.bean.GetKeyResponse;
import it.pagopa.swclient.mil.auth.azurekeyvault.bean.GetKeyVersionsResponse;
import it.pagopa.swclient.mil.auth.azurekeyvault.bean.GetKeysResponse;
import it.pagopa.swclient.mil.auth.azurekeyvault.bean.Key;
import it.pagopa.swclient.mil.auth.azurekeyvault.bean.KeyAttributes;
import it.pagopa.swclient.mil.auth.azurekeyvault.bean.KeyDetails;
import it.pagopa.swclient.mil.auth.azurekeyvault.bean.KeyNameAndVersion;
import it.pagopa.swclient.mil.auth.azurekeyvault.util.KidUtil;
import it.pagopa.swclient.mil.auth.bean.KeyType;
import it.pagopa.swclient.mil.auth.bean.KeyUse;
import it.pagopa.swclient.mil.auth.bean.PublicKey;
import it.pagopa.swclient.mil.auth.bean.PublicKeys;
import it.pagopa.swclient.mil.auth.service.KeyFinder;
import it.pagopa.swclient.mil.auth.util.AuthError;
import it.pagopa.swclient.mil.auth.util.UniGenerator;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 * @author Antonio Tarricone
 */
@ApplicationScoped
public class AzureKeyFinder implements KeyFinder {
	/*
	 * Context keys.
	 */
	static final String TOKEN = "token";
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
	private static final String CURRENT_KEY_NAME_AND_VERSION = "name_and_version";
	/*
	 *
	 */
	@Inject
	AzureAuthService authService;
	/*
	 *
	 */
	@Inject
	AzureKeyVaultService keyVaultService;
	/*
	 *
	 */
	@Inject
	KidUtil kidUtil;
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

	/**
	 * @param key
	 * @return
	 */
	private boolean isKeyEnabled(Key key) {
		if (key.getAttributes().getEnabled() != null && key.getAttributes().getEnabled()) {
			Log.debugf("The key [%s] is enabled.", key.getKid());
			return true;
		} else {
			Log.warnf("The key [%s] is not enabled.", key.getKid());
			return false;
		}
	}

	/**
	 * @param key
	 * @return
	 */
	private boolean isKeyCreationTimestampCoherent(Key key) {
		long now = Instant.now().getEpochSecond();
		if (key.getAttributes().getCreated() != null && key.getAttributes().getCreated() <= now) {
			Log.debugf("The creation timestamp of [%s] is valid.", key.getKid());
			return true;
		} else {
			Log.warnf("The creation timestamp of [%s] is not valid. Found [%s], expected a value less than [%d].", key.getKid(), key.getAttributes().getCreated(), now);
			return false;
		}
	}

	/**
	 * @param key
	 * @return
	 */
	private boolean isKeyNotYetExpired(Key key) {
		long now = Instant.now().getEpochSecond();
		if (key.getAttributes().getExp() != null && key.getAttributes().getExp() > now) {
			Log.debugf("The key [%s] is not expired.", key.getKid());
			return true;
		} else {
			Log.warnf("The key [%s] is expired. Found [%s], expected a value greater than [%d].", key.getKid(), key.getAttributes().getExp(), now);
			return false;
		}
	}

	/**
	 * @param key
	 * @return
	 */
	private boolean isKeyNotBeforeMet(Key key) {
		long now = Instant.now().getEpochSecond();
		if (key.getAttributes().getNbf() != null && key.getAttributes().getNbf() <= now) {
			Log.debugf("The 'not before' timestamp of [%s] is valid.", key.getKid());
			return true;
		} else {
			Log.warnf("The 'not before' timestamp of [%s] is not valid. Found [%s], expected a value less than [%d].", key.getKid(), key.getAttributes().getNbf(), now);
			return false;
		}
	}

	/**
	 * @param key
	 * @return
	 */
	private boolean isKeyValid(Key key) {
		if (key.getAttributes() == null) {
			Log.errorf("The key [%s] has null attributes.", key.getKid());
			return false;
		} else {
			return isKeyEnabled(key)
				&& isKeyCreationTimestampCoherent(key)
				&& isKeyNotYetExpired(key)
				&& isKeyNotBeforeMet(key);
		}
	}

	/**
	 * @param key
	 * @return
	 */
	private boolean isKeyTypeRsa(KeyDetails key) {
		if (Objects.equals(key.getKty(), RSA)) {
			Log.debugf("The key type of [%s] is RSA.", key.getKid());
			return true;
		} else {
			Log.warnf("The key type of [%s] is not RSA. Found [%s].", key.getKid(), key.getKty());
			return false;
		}
	}

	/**
	 * @param key
	 * @return
	 */
	private boolean isKeySuitableForSignature(KeyDetails key) {
		String[] keyOps = key.getKeyOps();
		if (keyOps != null) {
			List<String> keyOpList = Arrays.asList(keyOps);
			if (keyOpList.contains(SIGN) && keyOpList.contains(VERIFY)) {
				Log.debugf("The key [%s] is suitable for signature.", key.getKid());
				return true;
			} else {
				Log.warnf("The key [%s] is not suitable for signature. Found [%s].", key.getKid(), keyOpList);
				return false;
			}
		} else {
			Log.errorf("The key [%s] has null ops.", key.getKid());
			return false;
		}
	}

	/**
	 * @param key
	 * @return
	 */
	private boolean isKeyValid(KeyDetails key) {
		return isKeyValid((Key) key) && isKeyTypeRsa(key) && isKeySuitableForSignature(key);
	}

	/**
	 * @param getKeyResponse
	 * @return
	 */
	private boolean isKeyValid(GetKeyResponse getKeyResponse) {
		if (getKeyResponse.getKey() != null) {
			return isKeyValid(getKeyResponse.getKey());
		} else {
			Log.warn("Received null key.");
			return false;
		}
	}

	/**
	 * @return
	 */
	private Uni<ItemWithContext<PublicKeys>> findPublicKeysWithContext() {
		Log.debug("Search for the keys.");
		Context context = Context.of();
		return authService.getAccessToken()
			.map(x -> {
				String t = x.getToken();
				if (t != null) {
					Log.debug("Successfully authenticated.");
					return t;
				} else {
					String message = String.format("[%s] Azure access token not valid.", AuthErrorCode.AZURE_ACCESS_TOKEN_IS_NULL);
					Log.error(message);
					throw new AuthError(AuthErrorCode.AZURE_ACCESS_TOKEN_IS_NULL, message);
				}
			}) // Getting the access token.
			.invoke(token -> context.put(TOKEN, token)) // Storing the access token in the context.
			.chain(token -> keyVaultService.getKeys(token)) // Retrieving the list of keys.
			.invoke(x -> Log.debugf("Keys retrieved: [%s]", x))
			.map(GetKeysResponse::getKeys) // Getting the list of keys from the response.
			.onItem().transformToMulti(keys -> Multi.createFrom().items(Arrays.stream(keys).filter(Objects::nonNull))) // Transforming the list of keys in a stream of events (one event for each key).
			.invoke(x -> Log.debugf("Processing of the key: [%s]", x))
			.map(key -> kidUtil.getNameFromAzureKid(key.getKid()))
			.filter(keyNameAndVersion -> {
				if (keyNameAndVersion.getName() != null) {
					Log.debugf("Key name: [%s]", keyNameAndVersion.getName());
					return true;
				} else {
					Log.warn("Key name is null.");
					return false;
				}
			}) // Filtering the key with invalid kid.
			.map(KeyNameAndVersion::getName)
			.onItem().transformToUniAndConcatenate(keyName -> keyVaultService
				.getKeyVersions(
					context.get(TOKEN),
					keyName)) // Retrieving the versions of the key.
			.invoke(x -> Log.debugf("Versions retrieved: [%s]", x))
			.map(GetKeyVersionsResponse::getKeys) // Getting the list of versions from the response.
			.onItem().transformToMultiAndConcatenate(keys -> Multi.createFrom().items(Arrays.stream(keys).filter(Objects::nonNull))) // Transforming the list of versions in a stream of events (one event for each version).
			.invoke(x -> Log.debugf("Processing of the version: [%s]", x))
			.filter(version -> {
				KeyNameAndVersion keyNameAndVersion = kidUtil.getNameAndVersionFromAzureKid(version.getKid());
				if (keyNameAndVersion.isValid()) {
					Log.debugf("Key name and version: [%s]", keyNameAndVersion);
					if (isKeyValid(version)) {
						context.put(CURRENT_KEY_NAME_AND_VERSION, keyNameAndVersion);
						return true;
					} else {
						return false;
					}
				} else {
					Log.warnf("Invalid key name and version: [%s]", version.getKid());
					return false;
				}
			}) // Filtering not valid versions.
			.onItem().transformToUniAndConcatenate(version -> {
				KeyNameAndVersion keyNameAndVersion = (KeyNameAndVersion) context.get(CURRENT_KEY_NAME_AND_VERSION);
				return keyVaultService
					.getKey(
						context.get(TOKEN),
						keyNameAndVersion.getName(),
						keyNameAndVersion.getVersion());
			}) // Retrieving version details.
			.invoke(x -> Log.debugf("Details retrieved: [%s]", x))
			.filter(this::isKeyValid) // Filtering not valid details.
			.map(GetKeyResponse::getKey) // Getting the details from the response.
			.map(key -> new PublicKey(
				key.getExponent(),
				KeyUse.sig,
				kidUtil.getMyKidFromNameAndVersion(context.get(CURRENT_KEY_NAME_AND_VERSION)),
				key.getModulus(),
				KeyType.RSA,
				key.getAttributes().getExp(),
				key.getAttributes().getCreated())) // Generating internal public key object.
			.invoke(x -> Log.debugf("Internal public key object: [%s]", x))
			.collect() // Collecting all internal public key objects.
			.asList() // Converting the events in an event that is the list of the collected internal public key objects.
			.invoke(x -> Log.debugf("Found [%d] valid key/s.", x.size()))
			.map(PublicKeys::new)
			.invoke(x -> Log.debug(x))
			.map(p -> new ItemWithContext<>(context, p))
			.onFailure(t -> !(t instanceof AuthError))
			.transform(t -> {
				String message = String.format("[%s] Error from Azure.", AuthErrorCode.ERROR_FROM_AZURE);
				Log.errorf(t, message);
				throw new AuthError(AuthErrorCode.ERROR_FROM_AZURE, message);
			});
	}

	/**
	 * Finds all valid public keys.
	 *
	 * @return
	 */
	@Override
	public Uni<PublicKeys> findPublicKeys() {
		return findPublicKeysWithContext().map(ItemWithContext::get);
	}

	/**
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
					KeyNameAndVersion keyNameAndVersion = kidUtil.getNameAndVersionFromAzureKid(key.getKid());
					if (keyNameAndVersion.isValid()) {
						return new PublicKey(
							key.getExponent(),
							KeyUse.sig,
							kidUtil.getMyKidFromNameAndVersion(keyNameAndVersion),
							key.getModulus(),
							KeyType.RSA,
							key.getAttributes().getExp(),
							key.getAttributes().getCreated());
					} else {
						String message = String.format("[%s] Error generating the key pair: kid doesn't contain name and version.", AuthErrorCode.ERROR_GENERATING_KEY_PAIR);
						Log.fatal(message);
						throw new AuthError(AuthErrorCode.ERROR_GENERATING_KEY_PAIR, message);
					}
				} else {
					String message = String.format("[%s] Error generating the key pair: invalid key pair has been generated.", AuthErrorCode.ERROR_GENERATING_KEY_PAIR);
					Log.fatal(message);
					throw new AuthError(AuthErrorCode.ERROR_GENERATING_KEY_PAIR, message);
				}
			})
			.onFailure(t -> !(t instanceof AuthError))
			.transform(t -> {
				String message = String.format("[%s] Error generating key pair.", AuthErrorCode.ERROR_GENERATING_KEY_PAIR);
				Log.errorf(t, message);
				throw new AuthError(AuthErrorCode.ERROR_GENERATING_KEY_PAIR, message);
			});
	}

	/**
	 * Finds the valid public key with the greatest expiration. If there are no valid key a new one is
	 * generated.
	 *
	 * @return
	 */
	Uni<ItemWithContext<PublicKey>> findValidPublicKeyWithGreatestExpiration() {
		Log.debug("Search for a valid key with greatest expiration.");
		return findPublicKeysWithContext()
			.chain(item -> {
				List<PublicKey> keys = item.get().getKeys();
				if (keys.isEmpty()) {
					/*
					 * There are no valid key: generating one.
					 */
					Log.debug("There are no valid key: generating one.");
					return createKey(item.context().get(TOKEN))
						.map(p -> new ItemWithContext<>(item.context(), p));
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

					return UniGenerator.item(new ItemWithContext<>(item.context(), keys.get(0)));
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
		Log.debugf("Search for the public key [%s].", kid);

		KeyNameAndVersion keyNameAndVersion = kidUtil.getNameAndVersionFromMyKid(kid);
		if (keyNameAndVersion.isValid()) {
			return authService.getAccessToken()
				.map(GetAccessTokenResponse::getToken) // Getting the access token.
				.chain(token -> keyVaultService.getKey(token, keyNameAndVersion.getName(), keyNameAndVersion.getVersion()))
				.map(GetKeyResponse::getKey)
				.map(k -> {
					Log.debugf("Key [%s] found.", kid);
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
						Log.warnf("Key [%s] is not valid.", kid);
						return Optional.empty();
					}
				});
		} else {
			Log.warnf("[%s] doesn't contain name and version.", kid);
			return UniGenerator.item(Optional.empty());
		}
	}
}
