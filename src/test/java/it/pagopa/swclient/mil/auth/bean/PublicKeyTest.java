/*
 * PublicKeyTest.java
 *
 * 1 giu 2023
 */
package it.pagopa.swclient.mil.auth.bean;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

/**
 * 
 * @author Antonio Tarricone
 */
@QuarkusTest
class PublicKeyTest {
	/**
	 * 
	 */
	@Test
	void equals1() {
		PublicKey publicKey = new PublicKey("1", KeyUse.SIG, "2", "3", KeyType.RSA, 0, 0);
		assertTrue(publicKey.equals(publicKey));
	}

	/**
	 * 
	 */
	@Test
	void equals2() {
		PublicKey publicKey = new PublicKey("1", KeyUse.SIG, "2", "3", KeyType.RSA, 0, 0);
		assertFalse(publicKey.equals(null));
	}

	/**
	 * 
	 */
	@Test
	void equals3() {
		PublicKey publicKey = new PublicKey("1", KeyUse.SIG, "2", "3", KeyType.RSA, 0, 0);
		assertFalse(publicKey.equals(new Object()));
	}

	/**
	 * 
	 */
	@Test
	void equals4() {
		PublicKey publicKey1 = new PublicKey("1", KeyUse.SIG, "2", "3", KeyType.RSA, 0, 0);
		PublicKey publicKey2 = new PublicKey("1", KeyUse.SIG, "2", "3", KeyType.RSA, 0, 0);
		assertTrue(publicKey1.equals(publicKey2));
	}

	/**
	 * @throws CloneNotSupportedException 
	 */
	@Test
	void equals5() throws CloneNotSupportedException {
		PublicKey publicKey1 = new PublicKey("1", KeyUse.SIG, "2", "3", KeyType.RSA, 0, 0);
		PublicKey publicKey2 = new PublicKey(publicKey1);
		publicKey2.setE("");
		assertFalse(publicKey1.equals(publicKey2));
	}
	
	/**
	 * @throws CloneNotSupportedException 
	 */
	@Test
	void equals6() throws CloneNotSupportedException {
		PublicKey publicKey1 = new PublicKey("1", KeyUse.SIG, "2", "3", KeyType.RSA, 0, 0);
		PublicKey publicKey2 = new PublicKey(publicKey1);
		publicKey2.setExp(1);
		assertFalse(publicKey1.equals(publicKey2));
	}
	
	/**
	 * @throws CloneNotSupportedException 
	 */
	@Test
	void equals7() throws CloneNotSupportedException {
		PublicKey publicKey1 = new PublicKey("1", KeyUse.SIG, "2", "3", KeyType.RSA, 0, 0);
		PublicKey publicKey2 = new PublicKey(publicKey1);
		publicKey2.setIat(1);
		assertFalse(publicKey1.equals(publicKey2));
	}
	
	/**
	 * @throws CloneNotSupportedException 
	 */
	@Test
	void equals8() throws CloneNotSupportedException {
		PublicKey publicKey1 = new PublicKey("1", KeyUse.SIG, "2", "3", KeyType.RSA, 0, 0);
		PublicKey publicKey2 = new PublicKey(publicKey1);
		publicKey2.setKid("");
		assertFalse(publicKey1.equals(publicKey2));
	}
	
	/**
	 * @throws CloneNotSupportedException 
	 */
	@Test
	void equals9() throws CloneNotSupportedException {
		PublicKey publicKey1 = new PublicKey("1", KeyUse.SIG, "2", "3", KeyType.RSA, 0, 0);
		PublicKey publicKey2 = new PublicKey(publicKey1);
		publicKey2.setKty(null);
		assertFalse(publicKey1.equals(publicKey2));
	}
	
	/**
	 * @throws CloneNotSupportedException 
	 */
	@Test
	void equals10() throws CloneNotSupportedException {
		PublicKey publicKey1 = new PublicKey("1", KeyUse.SIG, "2", "3", KeyType.RSA, 0, 0);
		PublicKey publicKey2 = new PublicKey(publicKey1);
		publicKey2.setN("");
		assertFalse(publicKey1.equals(publicKey2));
	}
	
	/**
	 * @throws CloneNotSupportedException 
	 */
	@Test
	void equals11() throws CloneNotSupportedException {
		PublicKey publicKey1 = new PublicKey("1", KeyUse.SIG, "2", "3", KeyType.RSA, 0, 0);
		PublicKey publicKey2 = new PublicKey(publicKey1);
		publicKey2.setUse(null);
		assertFalse(publicKey1.equals(publicKey2));
	}
}