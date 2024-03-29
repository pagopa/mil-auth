/*
 * AzureKeyFinderFindPublicKeysTest.java
 *
 * 28 mar 2024
 */
package it.pagopa.swclient.mil.auth.azure.service;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import com.azure.core.exception.HttpResponseException;
import com.azure.core.util.FluxUtil;
import com.azure.core.util.logging.ClientLogger;
import com.azure.security.keyvault.keys.KeyAsyncClient;
import com.azure.security.keyvault.keys.models.KeyProperties;
import com.azure.security.keyvault.keys.models.KeyVaultKey;

import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.mutiny.helpers.test.UniAssertSubscriber;
import it.pagopa.swclient.mil.auth.bean.PublicKey;
import it.pagopa.swclient.mil.auth.bean.PublicKeys;
import it.pagopa.swclient.mil.auth.util.AuthError;
import reactor.core.publisher.Mono;

/**
 * 
 * @author Antonio Tarricone
 */
@QuarkusTest
@TestInstance(Lifecycle.PER_CLASS)
class AzureKeyFinderFindPublicKeysTest extends AzureKeyVaultTest {
	/**
	 * 
	 */
	@Test
	void givenSetOfKeys_whenFindPublicKeys_thenReturnValidPublicKeys() {
		/*
		 * Data preparation.
		 */
		KeyBundle validKeyBundle11 = prepareValidKey("valid_key_name_1", "v1", 100);
		KeyVaultKey validKeyVaultKey11 = validKeyBundle11.getKeyVaultKey();
		PublicKey validPublicKey11 = validKeyBundle11.getPublicKey();

		KeyBundle validKeyBundle12 = prepareValidKey("valid_key_name_1", "v2", 0);
		KeyVaultKey validKeyVaultKey12 = validKeyBundle12.getKeyVaultKey();
		PublicKey validPublicKey12 = validKeyBundle12.getPublicKey();

		KeyVaultKey notEnabledKey1 = prepareNotEnabledKey("not_enabled_key_name", "v1");
		KeyVaultKey notEnabledKey2 = prepareNotEnabledKey("not_enabled_key_name", "v2");
		KeyVaultKey expiredKey1 = prepareExpiredKey("expired_key_name", "v1");
		KeyVaultKey expiredKey2 = prepareExpiredKey("expired_key_name", "v2");
		KeyVaultKey notYetEffectiveKey1 = prepareNotYetEffectiveKey("not_yet_effective_key_name", "v1");
		KeyVaultKey notYetEffectiveKey2 = prepareNotYetEffectiveKey("not_yet_effective_key_name", "v2");
		KeyVaultKey futureCreatedKey1 = prepareFutureCreatedKey("future_created_key_name", "v1");
		KeyVaultKey futureCreatedKey2 = prepareFutureCreatedKey("future_created_key_name", "v2");
		KeyVaultKey notRsaKey1 = prepareNotRsaKey("not_rsa_key_name", "v1");
		KeyVaultKey notRsaKey2 = prepareNotRsaKey("not_rsa_key_name", "v2");
		KeyVaultKey notSuitableForSigningKey1 = prepareNotSuitableForSigningKey("not_suitable_for_signing_key_name", "v1");
		KeyVaultKey notSuitableForSigningKey2 = prepareNotSuitableForSigningKey("not_suitable_for_signing_key_name", "v2");

		KeyBundle validKeyBundle21 = prepareValidKey("valid_key_name_2", "v1", 200);
		KeyVaultKey validKeyVaultKey21 = validKeyBundle21.getKeyVaultKey();
		PublicKey validPublicKey21 = validKeyBundle21.getPublicKey();

		KeyBundle validKeyBundle22 = prepareValidKey("valid_key_name_2", "v2", 200);
		KeyVaultKey validKeyVaultKey22 = validKeyBundle22.getKeyVaultKey();
		PublicKey validPublicKey22 = validKeyBundle22.getPublicKey();

		/*
		 * Expected value.
		 */
		PublicKeys expected = new PublicKeys(List.of(validPublicKey21, validPublicKey22, validPublicKey11, validPublicKey12));

		/*
		 * Setup mock.
		 */
		KeyAsyncClient keyClient = mock(KeyAsyncClient.class);

		/*
		 * Mock of KeyAsyncClient.listPropertiesOfKeys().
		 */
		when(keyClient.listPropertiesOfKeys())
			.thenReturn(new PagedFluxGenerator<KeyProperties>()
				.from(List.of(validKeyVaultKey12, notEnabledKey2, expiredKey2, notYetEffectiveKey2, futureCreatedKey2, notRsaKey2, notSuitableForSigningKey2, validKeyVaultKey22)
					.stream()
					.map(KeyVaultKey::getProperties)
					.toList()));

		/*
		 * Mock of KeyAsyncClient.listPropertiesOfKeyVersions(String).
		 */
		when(keyClient.listPropertiesOfKeyVersions(validKeyVaultKey12.getName()))
			.thenReturn(new PagedFluxGenerator<KeyProperties>()
				.from(List.of(validKeyVaultKey11, validKeyVaultKey12)
					.stream()
					.map(KeyVaultKey::getProperties)
					.toList()));

		when(keyClient.listPropertiesOfKeyVersions(notEnabledKey2.getName()))
			.thenReturn(new PagedFluxGenerator<KeyProperties>()
				.from(List.of(notEnabledKey1, notEnabledKey2)
					.stream()
					.map(KeyVaultKey::getProperties)
					.toList()));

		when(keyClient.listPropertiesOfKeyVersions(expiredKey2.getName()))
			.thenReturn(new PagedFluxGenerator<KeyProperties>()
				.from(List.of(expiredKey1, expiredKey2)
					.stream()
					.map(KeyVaultKey::getProperties)
					.toList()));

		when(keyClient.listPropertiesOfKeyVersions(notYetEffectiveKey2.getName()))
			.thenReturn(new PagedFluxGenerator<KeyProperties>()
				.from(List.of(notYetEffectiveKey1, notYetEffectiveKey2)
					.stream()
					.map(KeyVaultKey::getProperties)
					.toList()));

		when(keyClient.listPropertiesOfKeyVersions(futureCreatedKey2.getName()))
			.thenReturn(new PagedFluxGenerator<KeyProperties>()
				.from(List.of(futureCreatedKey1, futureCreatedKey2)
					.stream()
					.map(KeyVaultKey::getProperties)
					.toList()));

		when(keyClient.listPropertiesOfKeyVersions(notRsaKey2.getName()))
			.thenReturn(new PagedFluxGenerator<KeyProperties>()
				.from(List.of(notRsaKey1, notRsaKey2)
					.stream()
					.map(KeyVaultKey::getProperties)
					.toList()));

		when(keyClient.listPropertiesOfKeyVersions(notSuitableForSigningKey2.getName()))
			.thenReturn(new PagedFluxGenerator<KeyProperties>()
				.from(List.of(notSuitableForSigningKey1, notSuitableForSigningKey2)
					.stream()
					.map(KeyVaultKey::getProperties)
					.toList()));

		when(keyClient.listPropertiesOfKeyVersions(validKeyVaultKey22.getName()))
			.thenReturn(new PagedFluxGenerator<KeyProperties>()
				.from(List.of(validKeyVaultKey21, validKeyVaultKey22)
					.stream()
					.map(KeyVaultKey::getProperties)
					.toList()));

		/*
		 * Mock of KeyAsyncClient.getKey(String, String).
		 */
		List<KeyVaultKey> keyList = List.of(
			validKeyVaultKey11,
			validKeyVaultKey12,
			notEnabledKey1,
			notEnabledKey2,
			expiredKey1,
			expiredKey2,
			notYetEffectiveKey1,
			notYetEffectiveKey2,
			futureCreatedKey1,
			futureCreatedKey2,
			notRsaKey1,
			notRsaKey2,
			notSuitableForSigningKey1,
			notSuitableForSigningKey2,
			validKeyVaultKey21,
			validKeyVaultKey22);

		keyList.forEach(key -> {
			when(keyClient.getKey(key.getName(), key.getProperties().getVersion()))
				.thenReturn(Mono.just(key));
		});

		/*
		 * Test.
		 */
		AzureKeyFinder keyFinder = new AzureKeyFinder(CRYPTO_PERIOD, KEY_SIZE, keyClient);
		keyFinder.findPublicKeys()
			.subscribe().withSubscriber(UniAssertSubscriber.create())
			.awaitItem()
			.assertItem(expected);
	}

	/**
	 * 
	 */
	@Test
	void givenErrorFromKeyVault_whenFindPublicKeys_thenReturnFailure() {
		/*
		 * Setup mock.
		 */
		KeyAsyncClient keyClient = mock(KeyAsyncClient.class);
		when(keyClient.listPropertiesOfKeys())
			.thenReturn(FluxUtil.pagedFluxError(new ClientLogger(this.getClass()), new RuntimeException("error from azure")));

		/*
		 * Test.
		 */
		AzureKeyFinder keyFinder = new AzureKeyFinder(CRYPTO_PERIOD, KEY_SIZE, keyClient);
		keyFinder.findPublicKeys()
			.subscribe().withSubscriber(UniAssertSubscriber.create())
			.awaitFailure()
			.assertFailedWith(AuthError.class);
	}

	/**
	 * 
	 */
	@Test
	void givenHttpError_whenFindPublicKeys_thenReturnFailure() {
		/*
		 * Setup mock.
		 */
		KeyAsyncClient keyClient = mock(KeyAsyncClient.class);
		when(keyClient.listPropertiesOfKeys())
			.thenThrow(HttpResponseException.class);

		/*
		 * Test.
		 */
		AzureKeyFinder keyFinder = new AzureKeyFinder(CRYPTO_PERIOD, KEY_SIZE, keyClient);
		keyFinder.findPublicKeys()
			.subscribe().withSubscriber(UniAssertSubscriber.create())
			.awaitFailure()
			.assertFailedWith(AuthError.class);
	}
}