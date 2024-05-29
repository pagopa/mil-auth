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
import it.pagopa.swclient.mil.auth.AuthErrorCode;
import it.pagopa.swclient.mil.auth.azure.keyvault.bean.KeyNameAndVersion;
import it.pagopa.swclient.mil.auth.azure.keyvault.service.AzureKeyFinder;
import it.pagopa.swclient.mil.auth.azure.keyvault.util.KidUtil;
import it.pagopa.swclient.mil.auth.bean.EncryptedClaim;
import it.pagopa.swclient.mil.auth.util.AuthError;
import it.pagopa.swclient.mil.auth.util.UniGenerator;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.JsonWebKeyEncryptionAlgorithm;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.JsonWebKeyOperation;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.JsonWebKeyType;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyAttributes;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyCreateParameters;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyOperationParameters;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyOperationResult;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.service.AzureKeyVaultKeysExtReactiveService;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.service.AzureKeyVaultKeysReactiveService;
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

	/*
	 * 
	 */
	private KidUtil kidUtil;

	/**
	 * 
	 * @param keysExtService
	 * @param keysService
	 * @param kidUtil
	 */
	@Inject
	ClaimEncryptor(AzureKeyVaultKeysExtReactiveService keysExtService, AzureKeyVaultKeysReactiveService keysService, KidUtil kidUtil) {
		this.keysExtService = keysExtService;
		this.keysService = keysService;
		this.kidUtil = kidUtil;
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
				if (keyBundle.isEmpty()) {
					Log.debug("No suitable key found");
					return createKey();
				} else {
					Log.trace("Suitable key found");
					return UniGenerator.item(keyBundle.get().getKey().getKid());
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
		Log.tracef("Encrypt with kid = %s", kid);
		KeyNameAndVersion keyNameAndVersion = kidUtil.getNameAndVersionFromAzureKid(kid);
		return keysService.encrypt(
			keyNameAndVersion.getName(),
			keyNameAndVersion.getVersion(),
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
				String kid = kidUtil.getMyKidFromAzureOne(keyOperationResult.getKid());
				return new EncryptedClaim()
					.setAlg(JsonWebKeyEncryptionAlgorithm.RSAOAEP256)
					.setValue(keyOperationResult.getValue())
					.setKid(kid);
			})
			.onFailure()
			.transform(t -> {
				Log.errorf(t, "Error encrypting the claim [%s]", t, value);
				return new AuthError(AuthErrorCode.ERROR_ENCRYPTING_CLAIM, "Error encrypting claim");
			});
	}

	/**
	 * 
	 * @param encryptedClaim
	 * @return
	 */
	public Uni<String> decrypt(EncryptedClaim encryptedClaim) {
		Log.trace("Decrypt");
		KeyNameAndVersion keyNameAndVersion = kidUtil.getNameAndVersionFromMyKid(encryptedClaim.getKid());
		return keysService.decrypt(
			keyNameAndVersion.getName(),
			keyNameAndVersion.getVersion(),
			new KeyOperationParameters()
				.setAlg(encryptedClaim.getAlg())
				.setValue(encryptedClaim.getValue()))
			.map(keyOperationResult -> new String(keyOperationResult.getValue(), StandardCharsets.UTF_8))
			.onFailure()
			.transform(t -> {
				Log.errorf(t, "Error decrypting claim", t);
				return new AuthError(AuthErrorCode.ERROR_DECRYPTING_CLAIM, "Error decrypting claim");
			});
	}
}