/*
 * AzureKeyFinderFindPublicKeyByKidTest.java
 *
 * 28 mar 2024
 */
package it.pagopa.swclient.mil.auth.azure.keyvault.service;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import com.azure.core.exception.HttpResponseException;
import com.azure.core.exception.ResourceNotFoundException;
import com.azure.security.keyvault.keys.KeyAsyncClient;
import com.azure.security.keyvault.keys.models.KeyVaultKey;

import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.mutiny.helpers.test.UniAssertSubscriber;
import it.pagopa.swclient.mil.auth.bean.PublicKey;
import it.pagopa.swclient.mil.auth.util.AuthError;
import reactor.core.publisher.Mono;

/**
 * 
 * @author Antonio Tarricone
 */
@QuarkusTest
@TestInstance(Lifecycle.PER_CLASS)
class AzureKeyFinderFindPublicKeyByKidTest extends AzureKeyVaultTest {
	/**
	 * 
	 */
	@Test
	void givenKid_whenFindPublicKey_thenReturnPublicKey() {
		/*
		 * Data preparation.
		 */
		KeyBundle keyBundle = prepareValidKey("valid_key_name", "v1", 0);
		KeyVaultKey keyVaultKey = keyBundle.getKeyVaultKey();

		/*
		 * Expected value.
		 */
		Optional<PublicKey> expected = Optional.of(keyBundle.getPublicKey());

		/*
		 * Setup mock.
		 */
		KeyAsyncClient keyClient = mock(KeyAsyncClient.class);
		when(keyClient.getKey(keyVaultKey.getProperties().getName(), keyVaultKey.getProperties().getVersion()))
			.thenReturn(Mono.just(keyVaultKey));

		/*
		 * Test.
		 */
		AzureKeyFinder keyFinder = new AzureKeyFinder(CRYPTO_PERIOD, KEY_SIZE, keyClient);
		keyFinder.findPublicKey(keyVaultKey.getId())
			.subscribe().withSubscriber(UniAssertSubscriber.create())
			.awaitItem()
			.assertItem(expected);
	}

	/**
	 * 
	 */
	@Test
	void givenInvalidKid_whenFindPublicKey_thenReturnEmpty() {
		/*
		 * Test.
		 */
		AzureKeyFinder keyFinder = new AzureKeyFinder(CRYPTO_PERIOD, KEY_SIZE, null);
		keyFinder.findPublicKey("invalid_kid")
			.subscribe().withSubscriber(UniAssertSubscriber.create())
			.awaitItem()
			.assertItem(Optional.empty());
	}

	/**
	 * 
	 */
	@Test
	void givenNotExistingKid_whenFindPublicKey_thenReturnEmpty() {
		/*
		 * Data preparation.
		 */
		String keyName = "not_existing_key";
		String keyVersion = "v1";
		String kid = keyName + "/" + keyVersion;

		/*
		 * Setup mock.
		 */
		KeyAsyncClient keyClient = mock(KeyAsyncClient.class);
		when(keyClient.getKey(keyName, keyVersion))
			.thenThrow(ResourceNotFoundException.class);

		/*
		 * Test.
		 */
		AzureKeyFinder keyFinder = new AzureKeyFinder(CRYPTO_PERIOD, KEY_SIZE, keyClient);
		keyFinder.findPublicKey(kid)
			.subscribe().withSubscriber(UniAssertSubscriber.create())
			.awaitItem()
			.assertItem(Optional.empty());
	}

	/**
	 * 
	 */
	@Test
	void givenHttpError_whenFindPublicKey_thenReturnFailure() {
		/*
		 * Data preparation.
		 */
		String keyName = "key";
		String keyVersion = "v1";
		String kid = keyName + "/" + keyVersion;

		/*
		 * Setup mock.
		 */
		KeyAsyncClient keyClient = mock(KeyAsyncClient.class);
		when(keyClient.getKey(keyName, keyVersion))
			.thenThrow(HttpResponseException.class);

		/*
		 * Test.
		 */
		AzureKeyFinder keyFinder = new AzureKeyFinder(CRYPTO_PERIOD, KEY_SIZE, keyClient);
		keyFinder.findPublicKey(kid)
			.subscribe().withSubscriber(UniAssertSubscriber.create())
			.awaitFailure()
			.assertFailedWith(AuthError.class);
	}

	/**
	 * 
	 */
	@Test
	void givenErrorFromKeyVault_whenFindPublicKey_thenReturnFailure() {
		/*
		 * Data preparation.
		 */
		String keyName = "key";
		String keyVersion = "v1";
		String kid = keyName + "/" + keyVersion;

		/*
		 * Setup mock.
		 */
		KeyAsyncClient keyClient = mock(KeyAsyncClient.class);
		when(keyClient.getKey(keyName, keyVersion))
			.thenReturn(Mono.error(new Exception("error from azure")));

		/*
		 * Test.
		 */
		AzureKeyFinder keyFinder = new AzureKeyFinder(CRYPTO_PERIOD, KEY_SIZE, keyClient);
		keyFinder.findPublicKey(kid)
			.subscribe().withSubscriber(UniAssertSubscriber.create())
			.awaitFailure()
			.assertFailedWith(AuthError.class);
	}

	/**
	 * 
	 */
	@Test
	void givenInvalidKey_whenFindPublicKey_thenReturnEmpty() {
		/*
		 * Data preparation.
		 */
		KeyVaultKey keyVaultKey = prepareExpiredKey("expired_key", "v1");

		/*
		 * Setup mock.
		 */
		KeyAsyncClient keyClient = mock(KeyAsyncClient.class);
		when(keyClient.getKey(keyVaultKey.getProperties().getName(), keyVaultKey.getProperties().getVersion()))
			.thenReturn(Mono.just(keyVaultKey));

		/*
		 * Test.
		 */
		AzureKeyFinder keyFinder = new AzureKeyFinder(CRYPTO_PERIOD, KEY_SIZE, keyClient);
		keyFinder.findPublicKey(keyVaultKey.getId())
			.subscribe().withSubscriber(UniAssertSubscriber.create())
			.awaitItem()
			.assertItem(Optional.empty());
	}
}