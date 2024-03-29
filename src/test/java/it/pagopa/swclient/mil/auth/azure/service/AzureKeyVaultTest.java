/*
 * AzureKeyVaultTest.java
 *
 * 28 mar 2024
 */
package it.pagopa.swclient.mil.auth.azure.service;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.OffsetDateTime;
import java.util.Arrays;

import org.jboss.logging.MDC;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;

import com.azure.security.keyvault.keys.implementation.KeyPropertiesHelper;
import com.azure.security.keyvault.keys.implementation.KeyVaultKeyHelper;
import com.azure.security.keyvault.keys.models.JsonWebKey;
import com.azure.security.keyvault.keys.models.KeyOperation;
import com.azure.security.keyvault.keys.models.KeyProperties;
import com.azure.security.keyvault.keys.models.KeyVaultKey;

import it.pagopa.swclient.mil.auth.bean.KeyType;
import it.pagopa.swclient.mil.auth.bean.KeyUse;
import it.pagopa.swclient.mil.auth.bean.PublicKey;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 
 * @author Antonio Tarricone
 */
abstract class AzureKeyVaultTest {
	/*
	 * 
	 */
	protected static final long CRYPTO_PERIOD = 86400;
	protected static final int KEY_SIZE = 2048;

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
	 * @param testInfo
	 */
	@BeforeEach
	void setRequestId(TestInfo testInfo) {
		MDC.put("requestId", testInfo.getDisplayName());
	}

	/**
	 * 
	 */
	@Getter
	@AllArgsConstructor
	protected class KeyBundle {
		private KeyVaultKey keyVaultKey;
		private PublicKey publicKey;
		private RSAPrivateKey privateKey;
	}

	/**
	 * 
	 * @param keyName
	 * @param keyVersion
	 * @param extraCryptoPeriod
	 * @return
	 */
	protected KeyBundle prepareValidKey(String keyName, String keyVersion, int extraCryptoPeriod) {
		String kid = keyName + "/" + keyVersion;

		KeyPair keyPair = generator.generateKeyPair();
		RSAPublicKey rsaPublicKey = (RSAPublicKey) keyPair.getPublic();
		RSAPrivateKey rsaPrivateKey = (RSAPrivateKey) keyPair.getPrivate();
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

		return new KeyBundle(keyVaultKey, publicKey, rsaPrivateKey);
	}

	/**
	 * 
	 * @param keyName
	 * @param keyVersion
	 * @return
	 */
	protected KeyVaultKey prepareNotEnabledKey(String keyName, String keyVersion) {
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
	protected KeyVaultKey prepareExpiredKey(String keyName, String keyVersion) {
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
	protected KeyVaultKey prepareNotYetEffectiveKey(String keyName, String keyVersion) {
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
	protected KeyVaultKey prepareFutureCreatedKey(String keyName, String keyVersion) {
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
	protected KeyVaultKey prepareNotRsaKey(String keyName, String keyVersion) {
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
	protected KeyVaultKey prepareNotSuitableForSigningKey(String keyName, String keyVersion) {
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
}