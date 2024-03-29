/*
 * AzureKeyFinderFindPublicKeyTest.java
 *
 * 28 mar 2024
 */
package it.pagopa.swclient.mil.auth.azure.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import com.azure.core.exception.HttpResponseException;
import com.azure.security.keyvault.keys.KeyAsyncClient;
import com.azure.security.keyvault.keys.models.CreateRsaKeyOptions;
import com.azure.security.keyvault.keys.models.KeyProperties;
import com.azure.security.keyvault.keys.models.KeyVaultKey;

import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.mutiny.helpers.test.UniAssertSubscriber;
import it.pagopa.swclient.mil.auth.bean.PublicKey;
import it.pagopa.swclient.mil.auth.util.AuthError;
import reactor.core.publisher.Mono;

/**
 * @author Antonio Tarricone
 */
@QuarkusTest
@TestInstance(Lifecycle.PER_CLASS)
class AzureKeyFinderFindPublicKeyTest extends AzureKeyVaultTest {
	/**
	 * 
	 */
	@Test
	void givenSetOfKeys_whenFindPublicKey_thenReturnValidPublicKey() {
		/*
		 * Data preparation.
		 */
		KeyVaultKey validKeyVaultKey11 = prepareValidKey("valid_key_name_1", "v1", 0).getKeyVaultKey();

		KeyBundle validKeyBundle12 = prepareValidKey("valid_key_name_1", "v2", 100);
		KeyVaultKey validKeyVaultKey12 = validKeyBundle12.getKeyVaultKey();
		PublicKey validPublicKey12 = validKeyBundle12.getPublicKey();

		/*
		 * Expected value.
		 */
		PublicKey expected = validPublicKey12;

		/*
		 * Setup mock.
		 */
		KeyAsyncClient keyClient = mock(KeyAsyncClient.class);

		/*
		 * Mock of KeyAsyncClient.listPropertiesOfKeys().
		 */
		when(keyClient.listPropertiesOfKeys())
			.thenReturn(new PagedFluxGenerator<KeyProperties>()
				.from(List.of(validKeyVaultKey12)
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

		/*
		 * Mock of KeyAsyncClient.getKey(String, String).
		 */
		List<KeyVaultKey> keyList = List.of(
			validKeyVaultKey11,
			validKeyVaultKey12);

		keyList.forEach(key -> {
			when(keyClient.getKey(key.getName(), key.getProperties().getVersion()))
				.thenReturn(Mono.just(key));
		});

		/*
		 * Test.
		 */
		AzureKeyFinder keyFinder = new AzureKeyFinder(CRYPTO_PERIOD, KEY_SIZE, keyClient);
		keyFinder.findPublicKey()
			.subscribe().withSubscriber(UniAssertSubscriber.create())
			.awaitItem()
			.assertItem(expected);
	}

	/**
	 * 
	 */
	@Test
	void givenNoKeys_whenFindPublicKey_thenReturnNewPublicKey() {
		/*
		 * Data preparation.
		 */
		KeyBundle validKeyBundle = prepareValidKey("valid_key_name", "v1", 0);
		KeyVaultKey validKeyVaultKey = validKeyBundle.getKeyVaultKey();
		PublicKey validPublicKey = validKeyBundle.getPublicKey();

		/*
		 * Expected value.
		 */
		PublicKey expected = validPublicKey;

		/*
		 * Setup mock.
		 */
		KeyAsyncClient keyClient = mock(KeyAsyncClient.class);

		/*
		 * Mock of KeyAsyncClient.listPropertiesOfKeys().
		 */
		when(keyClient.listPropertiesOfKeys())
			.thenReturn(new PagedFluxGenerator<KeyProperties>().from(List.of()));

		/*
		 * Mock of KeyAsyncClient.createRsaKey(CreateRsaKeyOptions).
		 */
		when(keyClient.createRsaKey(any(CreateRsaKeyOptions.class)))
			.thenReturn(Mono.just(validKeyVaultKey));

		/*
		 * Test.
		 */
		AzureKeyFinder keyFinder = new AzureKeyFinder(CRYPTO_PERIOD, KEY_SIZE, keyClient);
		keyFinder.findPublicKey()
			.subscribe().withSubscriber(UniAssertSubscriber.create())
			.awaitItem()
			.assertItem(expected);
	}

	/**
	 * 
	 */
	@Test
	void givenNoKeysAndErrorFromAzure_whenFindPublicKey_thenReturnFailure() {
		/*
		 * Setup mock.
		 */
		KeyAsyncClient keyClient = mock(KeyAsyncClient.class);

		/*
		 * Mock of KeyAsyncClient.listPropertiesOfKeys().
		 */
		when(keyClient.listPropertiesOfKeys())
			.thenReturn(new PagedFluxGenerator<KeyProperties>().from(List.of()));

		/*
		 * Mock of KeyAsyncClient.createRsaKey(CreateRsaKeyOptions).
		 */
		when(keyClient.createRsaKey(any(CreateRsaKeyOptions.class)))
			.thenReturn(Mono.error(new Exception("error from azure")));

		/*
		 * Test.
		 */
		AzureKeyFinder keyFinder = new AzureKeyFinder(CRYPTO_PERIOD, KEY_SIZE, keyClient);
		keyFinder.findPublicKey()
			.subscribe().withSubscriber(UniAssertSubscriber.create())
			.awaitFailure()
			.assertFailedWith(AuthError.class);
	}

	/**
	 * 
	 */
	@Test
	void givenNoKeysAndHttError_whenFindPublicKey_thenReturnFailure() {
		/*
		 * Setup mock.
		 */
		KeyAsyncClient keyClient = mock(KeyAsyncClient.class);

		/*
		 * Mock of KeyAsyncClient.listPropertiesOfKeys().
		 */
		when(keyClient.listPropertiesOfKeys())
			.thenReturn(new PagedFluxGenerator<KeyProperties>().from(List.of()));

		/*
		 * Mock of KeyAsyncClient.createRsaKey(CreateRsaKeyOptions).
		 */
		when(keyClient.createRsaKey(any(CreateRsaKeyOptions.class)))
			.thenThrow(HttpResponseException.class);

		/*
		 * Test.
		 */
		AzureKeyFinder keyFinder = new AzureKeyFinder(CRYPTO_PERIOD, KEY_SIZE, keyClient);
		keyFinder.findPublicKey()
			.subscribe().withSubscriber(UniAssertSubscriber.create())
			.awaitFailure()
			.assertFailedWith(AuthError.class);
	}
}