/*
 * ClaimEncryptorTest.java
 *
 * 24 mag 2024
 */
package it.pagopa.swclient.mil.auth.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.helpers.test.UniAssertSubscriber;
import it.pagopa.swclient.mil.auth.bean.EncryptedClaim;
import it.pagopa.swclient.mil.auth.util.AuthError;
import it.pagopa.swclient.mil.auth.util.KeyUtils;
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
 * @author Antonio Tarricone
 */
@QuarkusTest
@TestInstance(Lifecycle.PER_CLASS)
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

	/*
	 *
	 */
	@ConfigProperty(name = "quarkus.rest-client.azure-key-vault-api.url")
	String vaultBaseUrl;

	/*
	 * 
	 */
	private String keyBaseUrl;

	/**
	 *
	 */
	@BeforeAll
	void setup() {
		keyBaseUrl = vaultBaseUrl + (vaultBaseUrl.endsWith("/") ? "keys/" : "/keys/");
	}

	/**
	 * 
	 * @param testInfo
	 */
	@BeforeEach
	void init(TestInfo testInfo) {
		String frame = "*".repeat(testInfo.getDisplayName().length() + 11);
		System.out.println(frame);
		System.out.printf("* %s: START *%n", testInfo.getDisplayName());
		System.out.println(frame);
	}

	/**
	 * 
	 */
	@Test
	void given_claimToEncrypt_when_allIsOk_then_getEncryptedClaim() {
		/*
		 * 
		 */
		when(keysExtService.getKeyWithLongestExp(
			KeyUtils.KEY_DOMAIN,
			List.of(JsonWebKeyOperation.ENCRYPT, JsonWebKeyOperation.DECRYPT),
			List.of(JsonWebKeyType.RSA)))
			.thenReturn(UniGenerator.item(
				Optional.of(new KeyBundle()
					.setKey(new JsonWebKey()
						.setKid(keyBaseUrl + "key_name/key_version")))));

		when(keysService.encrypt(
			eq("key_name"),
			eq("key_version"),
			any(KeyOperationParameters.class)))
			.thenReturn(UniGenerator.item(new KeyOperationResult()
				.setKid(keyBaseUrl + "key_name/key_version")
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
				f -> fail(f));
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
			KeyUtils.KEY_DOMAIN,
			List.of(JsonWebKeyOperation.ENCRYPT, JsonWebKeyOperation.DECRYPT),
			List.of(JsonWebKeyType.RSA)))
			.thenReturn(Uni.createFrom().item(Optional.empty()));

		when(keysService.createKey(
			anyString(),
			any(KeyCreateParameters.class)))
			.thenReturn(UniGenerator.item(
				new KeyBundle()
					.setKey(new JsonWebKey()
						.setKid(keyBaseUrl + "key_name/key_version"))));

		when(keysService.encrypt(
			eq("key_name"),
			eq("key_version"),
			any(KeyOperationParameters.class)))
			.thenReturn(UniGenerator.item(new KeyOperationResult()
				.setKid(keyBaseUrl + "key_name/key_version")
				.setValue(new byte[0])));

		/*
		 * 
		 */
		claimEncryptor.encrypt("this is a test")
			.subscribe()
			.withSubscriber(UniAssertSubscriber.create())
			.assertCompleted()
			.assertItem(new EncryptedClaim()
				.setAlg(JsonWebKeyEncryptionAlgorithm.RSAOAEP256)
				.setKid("key_name/key_version")
				.setValue(new byte[0]));
	}

	/**
	 * 
	 */
	@Test
	void given_claimToEncrypt_when_keyRetrievingGoesWrong_then_getFailure() {
		/*
		 * 
		 */
		when(keysExtService.getKeyWithLongestExp(
			KeyUtils.KEY_DOMAIN,
			List.of(JsonWebKeyOperation.ENCRYPT, JsonWebKeyOperation.DECRYPT),
			List.of(JsonWebKeyType.RSA)))
			.thenReturn(Uni.createFrom().failure(new Exception("synthetic_exception")));

		/*
		 * 
		 */
		claimEncryptor.encrypt("this is a test")
			.subscribe()
			.withSubscriber(UniAssertSubscriber.create())
			.assertFailedWith(AuthError.class);
	}

	/**
	 * 
	 */
	@Test
	void given_claimToDecrypt_when_allIsOk_then_getDecryptedClaim() {
		/*
		 * 
		 */
		when(keysService.decrypt(
			eq("key_name"),
			eq("key_version"),
			any(KeyOperationParameters.class)))
			.thenReturn(UniGenerator.item(new KeyOperationResult()
				.setKid(keyBaseUrl + "key_name/key_version")
				.setValue("clear_claim".getBytes(StandardCharsets.UTF_8))));

		/*
		 * 
		 */
		claimEncryptor.decrypt(new EncryptedClaim()
			.setAlg("alg")
			.setKid("key_name/key_version")
			.setValue(new byte[0]))
			.subscribe()
			.withSubscriber(UniAssertSubscriber.create())
			.assertCompleted()
			.assertItem("clear_claim");
	}

	/**
	 * 
	 */
	@Test
	void given_claimToDecrypt_when_decryptionWentWrong_then_getFailure() {
		/*
		 * 
		 */
		when(keysService.decrypt(
			eq("key_name"),
			eq("key_version"),
			any(KeyOperationParameters.class)))
			.thenReturn(Uni.createFrom().failure(new Exception("synthetic_exception")));

		/*
		 * 
		 */
		claimEncryptor.decrypt(new EncryptedClaim()
			.setAlg("alg")
			.setKid("key_name/key_version")
			.setValue(new byte[0]))
			.subscribe()
			.withSubscriber(UniAssertSubscriber.create())
			.assertFailedWith(AuthError.class);
	}
}
