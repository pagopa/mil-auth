/*
 * ClaimEncryptor.java
 *
 * 24 mag 2024
 */
package it.pagopa.swclient.mil.auth.service;

import java.nio.charset.StandardCharsets;
import java.util.List;

import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
import it.pagopa.swclient.mil.auth.AuthErrorCode;
import it.pagopa.swclient.mil.auth.bean.EncryptedClaim;
import it.pagopa.swclient.mil.auth.util.AuthError;
import it.pagopa.swclient.mil.auth.util.KeyUtils;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.JsonWebKeyEncryptionAlgorithm;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.JsonWebKeyOperation;
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
public class ClaimEncryptor extends KeyManCapabilities {
	/**
	 * 
	 */
	ClaimEncryptor() {
		super();
	}
	
	/**
	 * 
	 * @param keysExtService
	 * @param keysService
	 */
	@Inject
	ClaimEncryptor(AzureKeyVaultKeysExtReactiveService keysExtService, AzureKeyVaultKeysReactiveService keysService) {
		super(keysExtService, keysService);
	}

	/**
	 * Encrypt UTF-8 encoding of a string.
	 * 
	 * @param azureKid
	 * @param value
	 * @return
	 */
	private Uni<KeyOperationResult> encrypt(String azureKid, String value) {
		Log.tracef("Encrypt with kid = %s", azureKid);
		String[] keyNameAndVersion = KeyUtils.azureKid2KeyNameVersion(azureKid);
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
		return retrieveKey(List.of(JsonWebKeyOperation.ENCRYPT, JsonWebKeyOperation.DECRYPT))
			.chain(azureKid -> encrypt(azureKid, value))
			.map(keyOperationResult -> {
				String myKid = KeyUtils.azureKid2MyKid(keyOperationResult.getKid());
				return new EncryptedClaim()
					.setAlg(JsonWebKeyEncryptionAlgorithm.RSAOAEP256)
					.setValue(keyOperationResult.getValue())
					.setKid(myKid);
			})
			.onFailure()
			.transform(t -> {
				String message = String.format("[%s] Error encrypting claim", AuthErrorCode.ERROR_ENCRYPTING_CLAIM);
				Log.errorf(t, message);
				cleanCache();
				return new AuthError(AuthErrorCode.ERROR_ENCRYPTING_CLAIM, message);
			});
	}

	/**
	 * 
	 * @param encryptedClaim
	 * @return
	 */
	public Uni<String> decrypt(EncryptedClaim encryptedClaim) {
		Log.trace("Decrypt");
		String[] keyNameAndVersion = KeyUtils.myKid2KeyNameVersion(encryptedClaim.getKid());
		return keysService.decrypt(
			keyNameAndVersion[0],
			keyNameAndVersion[1],
			new KeyOperationParameters()
				.setAlg(encryptedClaim.getAlg())
				.setValue(encryptedClaim.getValue()))
			.map(keyOperationResult -> new String(keyOperationResult.getValue(), StandardCharsets.UTF_8))
			.onFailure()
			.transform(t -> {
				String message = String.format("[%s] Error decrypting claim", AuthErrorCode.ERROR_DECRYPTING_CLAIM);
				Log.errorf(t, message);
				return new AuthError(AuthErrorCode.ERROR_DECRYPTING_CLAIM, message);
			});
	}
}