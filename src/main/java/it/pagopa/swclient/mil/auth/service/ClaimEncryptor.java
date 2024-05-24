/*
 * ClaimEncryptor.java
 *
 * 24 mag 2024
 */
package it.pagopa.swclient.mil.auth.service;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
import it.pagopa.swclient.mil.auth.azure.keyvault.service.AzureKeyFinder;
import it.pagopa.swclient.mil.auth.util.EncryptedClaim;
import it.pagopa.swclient.mil.auth.util.UniGenerator;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.JsonWebKeyEncryptionAlgorithm;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.JsonWebKeyOperation;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.JsonWebKeyType;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyAttributes;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyCreateParameters;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyItem;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyOperationParameters;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyOperationResult;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.service.AzureKeyVaultKeysExtReactiveService;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.service.AzureKeyVaultKeysReactiveService;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.util.KeyUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 * 
 * @author Antonio Tarricone
 */
@ApplicationScoped
public class ClaimEncryptor {
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
	private AzureKeyVaultKeysExtReactiveService keysExtService;

	/*
	 * 
	 */
	private AzureKeyVaultKeysReactiveService keysService;

	/**
	 * 
	 * @param keysExtService
	 * @param keysService
	 */
	@Inject
	ClaimEncryptor(AzureKeyVaultKeysExtReactiveService keysExtService, AzureKeyVaultKeysReactiveService keysService) {
		this.keysExtService = keysExtService;
		this.keysService = keysService;
	}

	/**
	 * Creates a new key.
	 * 
	 * @return key id (kid)
	 */
	private Uni<String> createKey() {
		Log.trace("Create e new key");
		long now = Instant.now().getEpochSecond();
		return keysService.createKey(
			AzureKeyFinder.generateKeyName(),
			new KeyCreateParameters()
				.setAttributes(new KeyAttributes()
					.setCreated(now)
					.setEnabled(Boolean.TRUE)
					.setExp(now + cryptoperiod)
					.setExportable(Boolean.FALSE)
					.setNbf(now))
				.setKeyOps(List.of(JsonWebKeyOperation.ENCRYPT, JsonWebKeyOperation.DECRYPT))
				.setKeySize(keysize)
				.setKty(JsonWebKeyType.RSA))
			.map(keyBundle -> keyBundle.getKey().getKid());
	}

	/**
	 * Gets a key and if doesn't find it, creates a new one.
	 * 
	 * @return key id (kid)
	 */
	private Uni<String> retrieveKey() {
		Log.trace("Retrieve key");
		return keysExtService.getKeyWithLongestExp(
			AzureKeyFinder.KEY_NAME_PREFIX,
			List.of(JsonWebKeyOperation.ENCRYPT, JsonWebKeyOperation.DECRYPT),
			List.of(JsonWebKeyType.RSA))
			.chain(keyBundle -> {
				if (keyBundle == null) {
					Log.debug("No suitable key found");
					return createKey();
				} else {
					Log.trace("Suitable key found");
					return UniGenerator.item(keyBundle.getKey().getKid());
				}
			});
	}

	/**
	 * Encrypt UTF-8 encoding of a string.
	 * 
	 * @param kid
	 * @param value
	 * @return
	 */
	private Uni<KeyOperationResult> encrypt(String kid, String value) {
		Log.trace("Encrypt");
		String[] keyNameAndVersion = KeyUtils.getKeyNameVersion(new KeyItem().setKid(kid));
		return keysService.encrypt(
			keyNameAndVersion[0],
			keyNameAndVersion[1],
			new KeyOperationParameters()
				.setAlg(JsonWebKeyEncryptionAlgorithm.RSAOAEP256)
				.setValue(value.getBytes(StandardCharsets.UTF_8)));
	}

	/**
	 * 
	 * @param value
	 * @return
	 */
	public Uni<EncryptedClaim> encrypt(String value) {
		return retrieveKey()
			.chain(kid -> encrypt(kid, value))
			.map(keyOperationResult -> {
				String[] keyNameAndVersion = KeyUtils.getKeyNameVersion(new KeyItem().setKid(keyOperationResult.getKid()));
				String kid = keyNameAndVersion[0] + "/" + keyNameAndVersion[1];
				return new EncryptedClaim()
					.setAlg(JsonWebKeyEncryptionAlgorithm.RSAOAEP256)
					.setValue(keyOperationResult.getValue())
					.setKid(kid);
			});
	}
}