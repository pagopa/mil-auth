/*
 * AzureKeyFinderTest.java
 *
 * 28 lug 2023
 */
package it.pagopa.swclient.mil.auth.azure.keyvault.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import com.azure.core.exception.HttpResponseException;
import com.azure.core.exception.ResourceNotFoundException;
import com.azure.core.util.FluxUtil;
import com.azure.core.util.logging.ClientLogger;
import com.azure.security.keyvault.keys.KeyAsyncClient;
import com.azure.security.keyvault.keys.implementation.KeyPropertiesHelper;
import com.azure.security.keyvault.keys.implementation.KeyVaultKeyHelper;
import com.azure.security.keyvault.keys.models.CreateRsaKeyOptions;
import com.azure.security.keyvault.keys.models.JsonWebKey;
import com.azure.security.keyvault.keys.models.KeyOperation;
import com.azure.security.keyvault.keys.models.KeyProperties;
import com.azure.security.keyvault.keys.models.KeyVaultKey;

import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.mutiny.helpers.test.UniAssertSubscriber;
import it.pagopa.swclient.mil.auth.bean.KeyType;
import it.pagopa.swclient.mil.auth.bean.KeyUse;
import it.pagopa.swclient.mil.auth.bean.PublicKey;
import it.pagopa.swclient.mil.auth.bean.PublicKeys;
import it.pagopa.swclient.mil.auth.util.AuthError;
import lombok.AllArgsConstructor;
import lombok.Getter;
import reactor.core.publisher.Mono;

/**
 * @author Antonio Tarricone
 */
@QuarkusTest
@TestInstance(Lifecycle.PER_CLASS)
class AzureKeyFinderTest {
	/*
	 * 
	 */
	private static final long CRYPTO_PERIOD = 86400;
	private static final int KEY_SIZE = 1024;

	/*
	 * Key pair generator.
	 */
	private KeyPairGenerator generator;

	/**
	 * 
	 * @throws NoSuchAlgorithmException
	 */
	@BeforeAll
	void setup() throws NoSuchAlgorithmException {
		generator = KeyPairGenerator.getInstance("RSA");
		generator.initialize(KEY_SIZE);
	}

	/**
	 * 
	 */
	@Getter
	@AllArgsConstructor
	private class KeyBundle {
		private KeyVaultKey keyVaultKey;
		private PublicKey publicKey;
	}

	/**
	 * 
	 * @param keyName
	 * @param keyVersion
	 * @param extraCryptoPeriod
	 * @return
	 */
	private KeyBundle prepareValidKey(String keyName, String keyVersion, int extraCryptoPeriod) {
		String kid = keyName + "/" + keyVersion;

		KeyPair keyPair = generator.generateKeyPair();
		RSAPublicKey rsaPublicKey = (RSAPublicKey) keyPair.getPublic();
		byte[] e = rsaPublicKey.getPublicExponent().toByteArray();
		byte[] n = rsaPublicKey.getModulus().toByteArray();

		OffsetDateTime createdOn = OffsetDateTime.now();
		OffsetDateTime expiresOn = createdOn.plusSeconds(CRYPTO_PERIOD + extraCryptoPeriod);
		OffsetDateTime notBefore = createdOn;

		JsonWebKey jsonWebKey = new JsonWebKey();
		jsonWebKey.setId(kid);
		jsonWebKey.setE(e);
		jsonWebKey.setN(n);
		jsonWebKey.setKeyType(com.azure.security.keyvault.keys.models.KeyType.RSA);
		jsonWebKey.setKeyOps(Arrays.asList(KeyOperation.SIGN, KeyOperation.VERIFY));

		KeyVaultKey keyVaultKey = KeyVaultKeyHelper.createKeyVaultKey(jsonWebKey);
		KeyProperties properties = keyVaultKey.getProperties();
		properties.setEnabled(Boolean.TRUE);
		properties.setExpiresOn(expiresOn);
		properties.setNotBefore(notBefore);
		KeyPropertiesHelper.setCreatedOn(properties, createdOn);
		KeyPropertiesHelper.setName(properties, keyName);
		KeyPropertiesHelper.setVersion(properties, keyVersion);
		KeyPropertiesHelper.setId(properties, kid);

		PublicKey publicKey = new PublicKey(e, KeyUse.sig, kid, n, KeyType.RSA, expiresOn.toEpochSecond(), createdOn.toEpochSecond());

		return new KeyBundle(keyVaultKey, publicKey);
	}

	/**
	 * 
	 * @param keyName
	 * @param keyVersion
	 * @return
	 */
	private KeyVaultKey prepareNotEnabledKey(String keyName, String keyVersion) {
		String kid = keyName + "/" + keyVersion;

		KeyPair keyPair = generator.generateKeyPair();
		RSAPublicKey rsaPublicKey = (RSAPublicKey) keyPair.getPublic();
		byte[] e = rsaPublicKey.getPublicExponent().toByteArray();
		byte[] n = rsaPublicKey.getModulus().toByteArray();

		OffsetDateTime createdOn = OffsetDateTime.now();
		OffsetDateTime expiresOn = createdOn.plusSeconds(CRYPTO_PERIOD);
		OffsetDateTime notBefore = createdOn;

		JsonWebKey jsonWebKey = new JsonWebKey();
		jsonWebKey.setId(kid);
		jsonWebKey.setE(e);
		jsonWebKey.setN(n);
		jsonWebKey.setKeyType(com.azure.security.keyvault.keys.models.KeyType.RSA);
		jsonWebKey.setKeyOps(Arrays.asList(KeyOperation.SIGN, KeyOperation.VERIFY));

		KeyVaultKey keyVaultKey = KeyVaultKeyHelper.createKeyVaultKey(jsonWebKey);
		KeyProperties properties = keyVaultKey.getProperties();
		properties.setEnabled(Boolean.FALSE);
		properties.setExpiresOn(expiresOn);
		properties.setNotBefore(notBefore);
		KeyPropertiesHelper.setCreatedOn(properties, createdOn);
		KeyPropertiesHelper.setName(properties, keyName);
		KeyPropertiesHelper.setVersion(properties, keyVersion);
		KeyPropertiesHelper.setId(properties, kid);

		return keyVaultKey;
	}

	/**
	 * 
	 * @param keyName
	 * @param keyVersion
	 * @return
	 */
	private KeyVaultKey prepareExpiredKey(String keyName, String keyVersion) {
		String kid = keyName + "/" + keyVersion;

		KeyPair keyPair = generator.generateKeyPair();
		RSAPublicKey rsaPublicKey = (RSAPublicKey) keyPair.getPublic();
		byte[] e = rsaPublicKey.getPublicExponent().toByteArray();
		byte[] n = rsaPublicKey.getModulus().toByteArray();

		OffsetDateTime createdOn = OffsetDateTime.now();
		OffsetDateTime expiresOn = createdOn.minusSeconds(100);
		OffsetDateTime notBefore = createdOn;

		JsonWebKey jsonWebKey = new JsonWebKey();
		jsonWebKey.setId(kid);
		jsonWebKey.setE(e);
		jsonWebKey.setN(n);
		jsonWebKey.setKeyType(com.azure.security.keyvault.keys.models.KeyType.RSA);
		jsonWebKey.setKeyOps(Arrays.asList(KeyOperation.SIGN, KeyOperation.VERIFY));

		KeyVaultKey keyVaultKey = KeyVaultKeyHelper.createKeyVaultKey(jsonWebKey);
		KeyProperties properties = keyVaultKey.getProperties();
		properties.setEnabled(Boolean.TRUE);
		properties.setExpiresOn(expiresOn);
		properties.setNotBefore(notBefore);
		KeyPropertiesHelper.setCreatedOn(properties, createdOn);
		KeyPropertiesHelper.setName(properties, keyName);
		KeyPropertiesHelper.setVersion(properties, keyVersion);
		KeyPropertiesHelper.setId(properties, kid);

		return keyVaultKey;
	}

	/**
	 * 
	 * @param keyName
	 * @param keyVersion
	 * @return
	 */
	private KeyVaultKey prepareNotYetEffectiveKey(String keyName, String keyVersion) {
		String kid = keyName + "/" + keyVersion;

		KeyPair keyPair = generator.generateKeyPair();
		RSAPublicKey rsaPublicKey = (RSAPublicKey) keyPair.getPublic();
		byte[] e = rsaPublicKey.getPublicExponent().toByteArray();
		byte[] n = rsaPublicKey.getModulus().toByteArray();

		OffsetDateTime createdOn = OffsetDateTime.now();
		OffsetDateTime expiresOn = createdOn.plusSeconds(CRYPTO_PERIOD);
		OffsetDateTime notBefore = createdOn.plusSeconds(100);

		JsonWebKey jsonWebKey = new JsonWebKey();
		jsonWebKey.setId(kid);
		jsonWebKey.setE(e);
		jsonWebKey.setN(n);
		jsonWebKey.setKeyType(com.azure.security.keyvault.keys.models.KeyType.RSA);
		jsonWebKey.setKeyOps(Arrays.asList(KeyOperation.SIGN, KeyOperation.VERIFY));

		KeyVaultKey keyVaultKey = KeyVaultKeyHelper.createKeyVaultKey(jsonWebKey);
		KeyProperties properties = keyVaultKey.getProperties();
		properties.setEnabled(Boolean.TRUE);
		properties.setExpiresOn(expiresOn);
		properties.setNotBefore(notBefore);
		KeyPropertiesHelper.setCreatedOn(properties, createdOn);
		KeyPropertiesHelper.setName(properties, keyName);
		KeyPropertiesHelper.setVersion(properties, keyVersion);
		KeyPropertiesHelper.setId(properties, kid);

		return keyVaultKey;
	}

	/**
	 * 
	 * @param keyName
	 * @param keyVersion
	 * @return
	 */
	private KeyVaultKey prepareFutureCreatedKey(String keyName, String keyVersion) {
		String kid = keyName + "/" + keyVersion;

		KeyPair keyPair = generator.generateKeyPair();
		RSAPublicKey rsaPublicKey = (RSAPublicKey) keyPair.getPublic();
		byte[] e = rsaPublicKey.getPublicExponent().toByteArray();
		byte[] n = rsaPublicKey.getModulus().toByteArray();

		OffsetDateTime now = OffsetDateTime.now();
		OffsetDateTime createdOn = now.plusSeconds(100);
		OffsetDateTime expiresOn = now.plusSeconds(CRYPTO_PERIOD);
		OffsetDateTime notBefore = now;

		JsonWebKey jsonWebKey = new JsonWebKey();
		jsonWebKey.setId(kid);
		jsonWebKey.setE(e);
		jsonWebKey.setN(n);
		jsonWebKey.setKeyType(com.azure.security.keyvault.keys.models.KeyType.RSA);
		jsonWebKey.setKeyOps(Arrays.asList(KeyOperation.SIGN, KeyOperation.VERIFY));

		KeyVaultKey keyVaultKey = KeyVaultKeyHelper.createKeyVaultKey(jsonWebKey);
		KeyProperties properties = keyVaultKey.getProperties();
		properties.setEnabled(Boolean.TRUE);
		properties.setExpiresOn(expiresOn);
		properties.setNotBefore(notBefore);
		KeyPropertiesHelper.setCreatedOn(properties, createdOn);
		KeyPropertiesHelper.setName(properties, keyName);
		KeyPropertiesHelper.setVersion(properties, keyVersion);
		KeyPropertiesHelper.setId(properties, kid);

		return keyVaultKey;
	}

	/**
	 * 
	 * @param keyName
	 * @param keyVersion
	 * @return
	 */
	private KeyVaultKey prepareNotRsaKey(String keyName, String keyVersion) {
		String kid = keyName + "/" + keyVersion;

		KeyPair keyPair = generator.generateKeyPair();
		RSAPublicKey rsaPublicKey = (RSAPublicKey) keyPair.getPublic();
		byte[] e = rsaPublicKey.getPublicExponent().toByteArray();
		byte[] n = rsaPublicKey.getModulus().toByteArray();

		OffsetDateTime createdOn = OffsetDateTime.now();
		OffsetDateTime expiresOn = createdOn.plusSeconds(CRYPTO_PERIOD);
		OffsetDateTime notBefore = createdOn;

		JsonWebKey jsonWebKey = new JsonWebKey();
		jsonWebKey.setId(kid);
		jsonWebKey.setE(e);
		jsonWebKey.setN(n);
		jsonWebKey.setKeyType(com.azure.security.keyvault.keys.models.KeyType.EC);
		jsonWebKey.setKeyOps(Arrays.asList(KeyOperation.SIGN, KeyOperation.VERIFY));

		KeyVaultKey keyVaultKey = KeyVaultKeyHelper.createKeyVaultKey(jsonWebKey);
		KeyProperties properties = keyVaultKey.getProperties();
		properties.setEnabled(Boolean.TRUE);
		properties.setExpiresOn(expiresOn);
		properties.setNotBefore(notBefore);
		KeyPropertiesHelper.setCreatedOn(properties, createdOn);
		KeyPropertiesHelper.setName(properties, keyName);
		KeyPropertiesHelper.setVersion(properties, keyVersion);
		KeyPropertiesHelper.setId(properties, kid);

		return keyVaultKey;
	}

	/**
	 * 
	 * @param keyName
	 * @param keyVersion
	 * @return
	 */
	private KeyVaultKey prepareNotSuitableForSigningKey(String keyName, String keyVersion) {
		String kid = keyName + "/" + keyVersion;

		KeyPair keyPair = generator.generateKeyPair();
		RSAPublicKey rsaPublicKey = (RSAPublicKey) keyPair.getPublic();
		byte[] e = rsaPublicKey.getPublicExponent().toByteArray();
		byte[] n = rsaPublicKey.getModulus().toByteArray();

		OffsetDateTime createdOn = OffsetDateTime.now();
		OffsetDateTime expiresOn = createdOn.plusSeconds(CRYPTO_PERIOD);
		OffsetDateTime notBefore = createdOn;

		JsonWebKey jsonWebKey = new JsonWebKey();
		jsonWebKey.setId(kid);
		jsonWebKey.setE(e);
		jsonWebKey.setN(n);
		jsonWebKey.setKeyType(com.azure.security.keyvault.keys.models.KeyType.RSA);
		jsonWebKey.setKeyOps(Arrays.asList(KeyOperation.ENCRYPT, KeyOperation.DECRYPT));

		KeyVaultKey keyVaultKey = KeyVaultKeyHelper.createKeyVaultKey(jsonWebKey);
		KeyProperties properties = keyVaultKey.getProperties();
		properties.setEnabled(Boolean.TRUE);
		properties.setExpiresOn(expiresOn);
		properties.setNotBefore(notBefore);
		KeyPropertiesHelper.setCreatedOn(properties, createdOn);
		KeyPropertiesHelper.setName(properties, keyName);
		KeyPropertiesHelper.setVersion(properties, keyVersion);
		KeyPropertiesHelper.setId(properties, kid);

		return keyVaultKey;
	}

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