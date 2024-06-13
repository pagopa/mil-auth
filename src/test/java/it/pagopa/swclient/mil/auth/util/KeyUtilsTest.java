/*
 * KeyUtilsTest.java
 *
 * 13 giu 2024
 */
package it.pagopa.swclient.mil.auth.util;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import io.quarkus.test.junit.QuarkusTest;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.JsonWebKey;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.JsonWebKeyOperation;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.JsonWebKeyType;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyAttributes;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyBundle;

/**
 * Additional tests.
 * 
 * @author Antonio Tarricone
 */
@QuarkusTest
@TestInstance(Lifecycle.PER_CLASS)
class KeyUtilsTest {
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
	void given_keyBundleWithSignVerifyUse_when_keyBundle2PublicKeyIsInvoked_then_returnedKeyHasSigUse() {
		long iat = Instant.now().minus(1, ChronoUnit.MINUTES).getEpochSecond();
		long exp = Instant.now().plus(9, ChronoUnit.MINUTES).getEpochSecond();
		
		KeyBundle keyBundle = new KeyBundle()
			.setAttributes(new KeyAttributes()
				.setCreated(iat)
				.setEnabled(true)
				.setExp(exp)
				.setNbf(iat))
			.setKey(new JsonWebKey()
				.setKty(JsonWebKeyType.RSA)
				.setE(new byte[0])
				.setN(new byte[0])
				.setKeyOps(List.of(
					JsonWebKeyOperation.SIGN,
					JsonWebKeyOperation.VERIFY))
				.setKid("https://keyvault/keys/key_name/key_version"))
			.setTags(Map.of(
				it.pagopa.swclient.mil.azureservices.keyvault.keys.util.KeyUtils.DOMAIN_KEY,
				KeyUtils.KEY_DOMAIN));
		
		assertEquals("sig", KeyUtils.keyBundle2PublicKey(keyBundle).getUse());
	}
	
	/**
	 * 
	 */
	@Test
	void given_keyBundleWithSignUseOnly_when_keyBundle2PublicKeyIsInvoked_then_returnedKeyDoesntHaveSigUse() {
		long iat = Instant.now().minus(1, ChronoUnit.MINUTES).getEpochSecond();
		long exp = Instant.now().plus(9, ChronoUnit.MINUTES).getEpochSecond();
		
		KeyBundle keyBundle = new KeyBundle()
			.setAttributes(new KeyAttributes()
				.setCreated(iat)
				.setEnabled(true)
				.setExp(exp)
				.setNbf(iat))
			.setKey(new JsonWebKey()
				.setKty(JsonWebKeyType.RSA)
				.setE(new byte[0])
				.setN(new byte[0])
				.setKeyOps(List.of(
					JsonWebKeyOperation.SIGN))
				.setKid("https://keyvault/keys/key_name/key_version"))
			.setTags(Map.of(
				it.pagopa.swclient.mil.azureservices.keyvault.keys.util.KeyUtils.DOMAIN_KEY,
				KeyUtils.KEY_DOMAIN));
		
		assertNull(KeyUtils.keyBundle2PublicKey(keyBundle).getUse());
	}
	
	/**
	 * 
	 */
	@Test
	void given_keyBundleWithVerifyUseOnly_when_keyBundle2PublicKeyIsInvoked_then_returnedKeyDoesntHaveSigUse() {
		long iat = Instant.now().minus(1, ChronoUnit.MINUTES).getEpochSecond();
		long exp = Instant.now().plus(9, ChronoUnit.MINUTES).getEpochSecond();
		
		KeyBundle keyBundle = new KeyBundle()
			.setAttributes(new KeyAttributes()
				.setCreated(iat)
				.setEnabled(true)
				.setExp(exp)
				.setNbf(iat))
			.setKey(new JsonWebKey()
				.setKty(JsonWebKeyType.RSA)
				.setE(new byte[0])
				.setN(new byte[0])
				.setKeyOps(List.of(
					JsonWebKeyOperation.VERIFY))
				.setKid("https://keyvault/keys/key_name/key_version"))
			.setTags(Map.of(
				it.pagopa.swclient.mil.azureservices.keyvault.keys.util.KeyUtils.DOMAIN_KEY,
				KeyUtils.KEY_DOMAIN));
		
		assertNull(KeyUtils.keyBundle2PublicKey(keyBundle).getUse());
	}
	
	/**
	 * 
	 */
	@Test
	void given_keyBundleWithEncryptUseOnly_when_keyBundle2PublicKeyIsInvoked_then_returnedKeyDoesntHaveSigUse() {
		long iat = Instant.now().minus(1, ChronoUnit.MINUTES).getEpochSecond();
		long exp = Instant.now().plus(9, ChronoUnit.MINUTES).getEpochSecond();
		
		KeyBundle keyBundle = new KeyBundle()
			.setAttributes(new KeyAttributes()
				.setCreated(iat)
				.setEnabled(true)
				.setExp(exp)
				.setNbf(iat))
			.setKey(new JsonWebKey()
				.setKty(JsonWebKeyType.RSA)
				.setE(new byte[0])
				.setN(new byte[0])
				.setKeyOps(List.of(
					JsonWebKeyOperation.ENCRYPT))
				.setKid("https://keyvault/keys/key_name/key_version"))
			.setTags(Map.of(
				it.pagopa.swclient.mil.azureservices.keyvault.keys.util.KeyUtils.DOMAIN_KEY,
				KeyUtils.KEY_DOMAIN));
		
		assertNull(KeyUtils.keyBundle2PublicKey(keyBundle).getUse());
	}
}
