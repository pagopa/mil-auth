/*
 * ClaimEncryptorTest.java
 *
 * 24 mag 2024
 */
package it.pagopa.swclient.mil.auth.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.mutiny.Uni;
import it.pagopa.swclient.mil.auth.azure.keyvault.service.AzureKeyFinder;
import it.pagopa.swclient.mil.auth.util.EncryptedClaim;
import it.pagopa.swclient.mil.auth.util.UniGenerator;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.JsonWebKey;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.JsonWebKeyEncryptionAlgorithm;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.JsonWebKeyOperation;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.JsonWebKeyType;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyBundle;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyCreateParameters;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyOperationParameters;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyOperationResult;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.service.AzureKeyVaultKeysExtReactiveService;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.service.AzureKeyVaultKeysReactiveService;
import jakarta.inject.Inject;

/**
 * 
 * @author antonio.tarricone
 */
@QuarkusTest
class ClaimEncryptorTest {
	/*
	 * 
	 */
	@Inject
	ClaimEncryptor claimEncryptor;

	/*
	 * 
	 */
	@InjectMock
	AzureKeyVaultKeysExtReactiveService keysExtService;

	/*
	 * 
	 */
	@InjectMock
	AzureKeyVaultKeysReactiveService keysService;

	/**
	 * 
	 */
	@Test
	void given_claimToEncrypt_when_allIsOk_then_getEncryptedClaim() {
		/*
		 * 
		 */
		when(keysExtService.getKeyWithLongestExp(
			AzureKeyFinder.KEY_NAME_PREFIX,
			List.of(JsonWebKeyOperation.ENCRYPT, JsonWebKeyOperation.DECRYPT),
			List.of(JsonWebKeyType.RSA)))
			.thenReturn(UniGenerator.item(
				new KeyBundle()
					.setKey(new JsonWebKey()
						.setKid("https://dummy/keys/key_name/key_version"))));

		when(keysService.encrypt(
			eq("key_name"),
			eq("key_version"),
			any(KeyOperationParameters.class)))
			.thenReturn(UniGenerator.item(new KeyOperationResult()
				.setKid("https://dummy/keys/key_name/key_version")
				.setValue(new byte[0])));

		/*
		 * 
		 */
		claimEncryptor.encrypt("this is a test")
			.subscribe()
			.with(
				actual -> {
					assertThat(actual)
						.usingRecursiveComparison()
						.isEqualTo(new EncryptedClaim()
							.setAlg(JsonWebKeyEncryptionAlgorithm.RSAOAEP256)
							.setKid("key_name/key_version")
							.setValue(new byte[0]));
				},
				f -> {
				});
	}

	/**
	 * 
	 */
	@Test
	void given_claimToEncrypt_when_keyNotFound_then_createNewOneAndReturnEncryptedClaim() {
		/*
		 * 
		 */
		when(keysExtService.getKeyWithLongestExp(
			AzureKeyFinder.KEY_NAME_PREFIX,
			List.of(JsonWebKeyOperation.ENCRYPT, JsonWebKeyOperation.DECRYPT),
			List.of(JsonWebKeyType.RSA)))
			.thenReturn(Uni.createFrom().nullItem());

		when(keysService.createKey(
			eq(AzureKeyFinder.KEY_NAME_PREFIX),
			any(KeyCreateParameters.class)))
			.thenReturn(UniGenerator.item(
				new KeyBundle()
					.setKey(new JsonWebKey()
						.setKid("https://dummy/keys/key_name/key_version"))));

		when(keysService.encrypt(
			eq("key_name"),
			eq("key_version"),
			any(KeyOperationParameters.class)))
			.thenReturn(UniGenerator.item(new KeyOperationResult()
				.setKid("https://dummy/keys/key_name/key_version")
				.setValue(new byte[0])));

		/*
		 * 
		 */
		claimEncryptor.encrypt("this is a test")
			.subscribe()
			.with(
				actual -> {
					assertThat(actual)
						.usingRecursiveComparison()
						.isEqualTo(new EncryptedClaim()
							.setAlg(JsonWebKeyEncryptionAlgorithm.RSAOAEP256)
							.setKid("key_name/key_version")
							.setValue(new byte[0]));
				},
				f -> {
				});
	}
}
