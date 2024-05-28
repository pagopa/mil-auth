/*
 * KeyNameAndVersionTest.java
 *
 * 14 set 2023
 */
package it.pagopa.swclient.mil.auth.azure.keyvault.bean;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import io.quarkus.test.junit.QuarkusTest;

/**
 * @author Antonio Tarricone
 */
@QuarkusTest
class KeyNameAndVersionTest {
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

	@Test
	void testIsValid1() {
		assertFalse(new KeyNameAndVersion(null, null).isValid());
	}

	@Test
	void testIsValid2() {
		assertFalse(new KeyNameAndVersion("", null).isValid());
	}

	@Test
	void testIsValid3() {
		assertFalse(new KeyNameAndVersion(null, "").isValid());
	}

	@Test
	void testIsValid4() {
		assertTrue(new KeyNameAndVersion("", "").isValid());
	}
}