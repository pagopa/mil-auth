/*
 * KeyManCapabilities.java
 *
 * 14 giu 2024
 */
package it.pagopa.swclient.mil.auth.service;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
import it.pagopa.swclient.mil.auth.bean.KeyIdCache;
import it.pagopa.swclient.mil.auth.util.KeyUtils;
import it.pagopa.swclient.mil.auth.util.UniGenerator;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.JsonWebKeyType;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyAttributes;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyCreateParameters;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.service.AzureKeyVaultKeysExtReactiveService;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.service.AzureKeyVaultKeysReactiveService;

/**
 * 
 * @author Antonio Tarricone
 */
abstract class KeyManCapabilities {
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
	protected AzureKeyVaultKeysExtReactiveService keysExtService;

	/*
	 * 
	 */
	protected AzureKeyVaultKeysReactiveService keysService;

	/*
	 * 
	 */
	private KeyIdCache keyIdCache;
	
	/**
	 * 
	 */
	KeyManCapabilities() {
	}
	
	/**
	 * 
	 * @param keysExtService
	 * @param keysService
	 */
	KeyManCapabilities(AzureKeyVaultKeysExtReactiveService keysExtService, AzureKeyVaultKeysReactiveService keysService) {
		this.keysExtService = keysExtService;
		this.keysService = keysService;
		keyIdCache = new KeyIdCache();
	}
	
	/**
	 * Creates a new key.
	 * 
	 * @param keyOps
	 * @return key id (kid)
	 */
	protected Uni<String> createKey(List<String> keyOps) {
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
				.setKty(JsonWebKeyType.RSA))
			.map(keyBundle -> {
				String kid = keyBundle.getKey().getKid();
				Log.debug("Cache the key ID");
				keyIdCache.setKid(kid)
					.setExp(keyBundle.getAttributes().getExp())
					.setStoredAt(Instant.now().getEpochSecond());
				return kid;
			});
	}
	
	/**
	 * Gets a key and if doesn't find it, creates a new one.
	 * 
	 * @param keyOps
	 * @return key id (kid)
	 */
	protected Uni<String> retrieveKey(List<String> keyOps) {
		Log.trace("Retrieve key");

		if (keyIdCache.isValid(0)) {
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
					return createKey(keyOps);
				} else {
					Log.trace("Suitable key found");
					return UniGenerator.item(keyBundle.get().getKey().getKid());
				}
			});
	}
}
