/*
 * SecretTripletTest.java
 *
 * 30 lug 2024
 */
package it.pagopa.swclient.mil.auth.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mockStatic;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.mockito.MockedStatic;

import io.quarkus.test.junit.QuarkusTest;

/**
 * 
 * @author antonio.tarricone
 */
@QuarkusTest
class SecretTripletTest {
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
	 * Test method for {@link it.pagopa.swclient.mil.auth.util.SecretTriplet#verify()}.
	 */
	@Test
	void testVerify() {
		SecretTriplet triplet = new SecretTriplet(
			"3ebcd984-48b1-4df2-99d8-f5d550dbad02",
			"TSO2VIJixd6taCapX1Aq9bTIbTAEuDtXzLleB9A3W6NUgppiJkNbAnBX8CVYvpsPMpzJHGhK2ouHDONevrcVUg==",
			"gKWXj0IXDkeO5xvrozbm47tO+SXHNGN8pE5ql3W4Hgo=");

		assertTrue(triplet.verify());
	}

	/**
	 * Test method for {@link it.pagopa.swclient.mil.auth.util.SecretTriplet#generate()}.
	 */
	@Test
	void testGenerate() {
		SecretTriplet triplet = SecretTriplet.generate();
		assertNotNull(triplet.getHash());
		assertNotNull(triplet.getSalt());
		assertNotNull(triplet.getSecret());

		/*
		 * base64 len = bytes * 4 / 3 rounded up to a multiple of 4
		 */
		assertEquals(4 * Math.ceil(32.0 * 4 / 3 / 4), triplet.getHash().length());
		assertEquals(4 * Math.ceil(64.0 * 4 / 3 / 4), triplet.getSalt().length());

		assertEquals(36, triplet.getSecret().length());
	}

	/**
	 * Test method for {@link it.pagopa.swclient.mil.auth.util.SecretTriplet#generate()}.
	 */
	@Test
	void testGenerateWoSecureRandomGetInstanceStrong() {
		try (MockedStatic<SecureRandom> rnd = mockStatic(SecureRandom.class)) {
			rnd.when(() -> SecureRandom.getInstanceStrong())
				.thenThrow(NoSuchAlgorithmException.class);

			SecretTriplet triplet = SecretTriplet.generate();
			assertNotNull(triplet.getHash());
			assertNotNull(triplet.getSalt());
			assertNotNull(triplet.getSecret());

			/*
			 * base64 len = bytes * 4 / 3 rounded up to a multiple of 4
			 */
			assertEquals(4 * Math.ceil(32.0 * 4 / 3 / 4), triplet.getHash().length());
			assertEquals(4 * Math.ceil(64.0 * 4 / 3 / 4), triplet.getSalt().length());

			assertEquals(36, triplet.getSecret().length());
		}
	}
}
