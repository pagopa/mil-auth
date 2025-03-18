/*
 * KeyManCapabilities.java
 *
 * 14 giu 2024
 */
package it.pagopa.swclient.mil.auth.service;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletionException;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import io.quarkus.logging.Log;
import io.quarkus.vertx.ConsumeEvent;
import io.smallrye.mutiny.TimeoutException;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.core.eventbus.EventBus;
import it.pagopa.swclient.mil.auth.bean.KeyIdCache;
import it.pagopa.swclient.mil.auth.util.KeyUtils;
import it.pagopa.swclient.mil.auth.util.UniGenerator;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.JsonWebKeyType;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyAttributes;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyBundle;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyCreateParameters;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.service.AzureKeyVaultKeysExtReactiveService;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.service.AzureKeyVaultKeysReactiveService;

/**
 * 
 * @author Antonio Tarricone
 */
public abstract class KeyManCapabilities {
	/*
	 * 
	 */
	@ConfigProperty(name = "cryptoperiod", defaultValue = "86400")
	long cryptoperiod;

	/*
	 * 
	 */
	@ConfigProperty(name = "keysize", defaultValue = "4096")
	int keysize;

	/*
	 * 
	 */
	@ConfigProperty(name = "keyid-cache.expire-after-write", defaultValue = "3600")
	long keyidCacheExpireAfterWrite;

	/*
	 * ms
	 */
	@ConfigProperty(name = "timeout-for-deleting-expired-keys", defaultValue = "120000")
	long timeoutForDeletingExpiredKeys = 120000;

	/*
	 * 
	 */
	protected AzureKeyVaultKeysExtReactiveService keysExtService;

	/*
	 * 
	 */
	protected AzureKeyVaultKeysReactiveService keysService;

	/*
	 * Event bus to delete asynchronously expired keys.
	 */
	protected EventBus eventBus;

	/*
	 * 
	 */
	private KeyIdCache keyIdCache;

	/*
	 * 
	 */
	static final String DELETE_EXPIRED_KEYS_EVENT = "deleteExpiredKeys";

	/**
	 * 
	 */
	KeyManCapabilities() {
		keyIdCache = new KeyIdCache();
	}

	/**
	 * 
	 * @param keysExtService
	 * @param keysService
	 * @param eventBus
	 */
	KeyManCapabilities(AzureKeyVaultKeysExtReactiveService keysExtService, AzureKeyVaultKeysReactiveService keysService, EventBus eventBus) {
		this.keysExtService = keysExtService;
		this.keysService = keysService;
		this.eventBus = eventBus;
		keyIdCache = new KeyIdCache();
	}

	/**
	 * 
	 * @param keyBundle
	 */
	private void cacheKid(KeyBundle keyBundle) {
		Log.debug("Cache the key ID");
		keyIdCache.setKid(keyBundle.getKey().getKid())
			.setExp(keyBundle.getAttributes().getExp())
			.setStoredAt(Instant.now().getEpochSecond());
	}

	/**
	 * Deletes expired keys. This is triggered by means of event bus.
	 * 
	 * @param v
	 */
	@ConsumeEvent(value = DELETE_EXPIRED_KEYS_EVENT, blocking = true)
	void deleteExpiredKeys(Void v) {
		Log.trace("Delete expired keys");
		try {
			keysExtService.deleteExpiredKeys(KeyUtils.DOMAIN_VALUE)
				.collect()
				.asList()
				.invoke(l -> Log.debugf("Deleted %d expired key/s", l.size()))
				.await()
				.atMost(Duration.ofMillis(timeoutForDeletingExpiredKeys));
		} catch (CompletionException e) {
			Log.errorf(e, "Error deleting expired keys");
		} catch (TimeoutException e) {
			Log.errorf(e, "Deleting expired keys is taking too long");
		}
	}

	/**
	 * Creates a new key.
	 * 
	 * @param keyOps
	 * @return
	 */
	protected Uni<KeyBundle> createKey(List<String> keyOps) {
		Log.trace("Create e new key");
		long now = Instant.now().getEpochSecond();
		return keysService.createKey(
			KeyUtils.generateKeyName(),
			new KeyCreateParameters()
				.setAttributes(new KeyAttributes()
					.setCreated(now)
					.setEnabled(Boolean.TRUE)
					.setExp(now + cryptoperiod)
					.setExportable(Boolean.FALSE)
					.setNbf(now))
				.setTags(Map.of(it.pagopa.swclient.mil.azureservices.keyvault.keys.util.KeyUtils.DOMAIN_KEY, KeyUtils.DOMAIN_VALUE))
				.setKeyOps(keyOps)
				.setKeySize(keysize)
				.setKty(JsonWebKeyType.RSA));
	}

	/**
	 * Gets a key and if doesn't find it, creates a new one.
	 * 
	 * @param keyOps
	 * @return key id (kid)
	 */
	protected Uni<String> retrieveKey(List<String> keyOps) {
		Log.trace("Retrieve key");

		if (keyIdCache.isValid(0, keyidCacheExpireAfterWrite)) {
			Log.debug("Returned cached kid");
			return UniGenerator.item(keyIdCache.getKid());
		}

		return keysExtService.getKeyWithLongestExp(
			KeyUtils.DOMAIN_VALUE,
			keyOps,
			List.of(JsonWebKeyType.RSA))
			.chain(keyBundle -> {
				if (keyBundle.isEmpty()) {
					Log.debug("No suitable key found");
					/*
					 * There is no suitable key. Current keys could be expired, so start an asynchronous job which
					 * deletes them.
					 */
					eventBus.send(DELETE_EXPIRED_KEYS_EVENT, null);
					/*
					 * Then, create new key.
					 */
					return createKey(keyOps);
				} else {
					Log.trace("Suitable key found");
					return UniGenerator.item(keyBundle.get());
				}
			})
			.map(keyBundle -> {
				cacheKid(keyBundle);
				return keyBundle.getKey().getKid();
			});
	}

	/**
	 * 
	 */
	public void cleanCache() {
		keyIdCache.clean();
		Log.trace("Key ID cache cleaned");
	}
}
