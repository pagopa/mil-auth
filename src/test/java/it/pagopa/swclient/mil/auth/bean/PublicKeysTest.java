/*
 * PublicKeysTest.java
 *
 * 1 giu 2023
 */
package it.pagopa.swclient.mil.auth.bean;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

/**
 * 
 * @author Antonio Tarricone
 */
@QuarkusTest
class PublicKeysTest {
	/**
	 * 
	 */
	@Test
	void equals1() {
		PublicKey publicKey = new PublicKey("1", KeyUse.SIG, "2", "3", KeyType.RSA, 0, 0);
		PublicKeys publicKeys = new PublicKeys(List.of(publicKey));
		assertTrue(publicKeys.equals(publicKeys));
	}
	
	/**
	 * 
	 */
	@Test
	void equals2() {
		PublicKey publicKey = new PublicKey("1", KeyUse.SIG, "2", "3", KeyType.RSA, 0, 0);
		PublicKeys publicKeys = new PublicKeys(List.of(publicKey));
		assertFalse(publicKeys.equals(null));
	}
	
	/**
	 * 
	 */
	@Test
	void equals3() {
		PublicKey publicKey = new PublicKey("1", KeyUse.SIG, "2", "3", KeyType.RSA, 0, 0);
		PublicKeys publicKeys = new PublicKeys(List.of(publicKey));
		assertFalse(publicKeys.equals(new Object()));
	}
	
	/**
	 * 
	 */
	@Test
	void equals4() {
		PublicKey publicKey1 = new PublicKey("1", KeyUse.SIG, "2", "3", KeyType.RSA, 0, 0);
		PublicKeys publicKeys1 = new PublicKeys(List.of(publicKey1));
		PublicKey publicKey2 = new PublicKey("1", KeyUse.SIG, "2", "3", KeyType.RSA, 0, 0);
		PublicKeys publicKeys2 = new PublicKeys(List.of(publicKey2));
		assertTrue(publicKeys1.equals(publicKeys2));
	}
}