/*
 * EncryptedClaimTest.java
 *
 * 28 mag 2024
 */
package it.pagopa.swclient.mil.auth.bean;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import io.quarkus.test.junit.QuarkusTest;

/**
 * 
 * @author Antonio Tarricone
 */
@QuarkusTest
class EncryptedClaimTest {
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
	 * Test method for {@link it.pagopa.swclient.mil.auth.bean.EncryptedClaim#fromMap(java.util.Map)}.
	 */
	@Test
	void given_mapWithNullValue_when_fromMapIsInvoked_then_getObjectWithNullValue() {
		Map<String, Object> map = new HashMap<>();
		map.put("alg", "this_is_the_alg");
		map.put("kid", "this_is_the_kid");
		map.put("value", null);

		EncryptedClaim actual = new EncryptedClaim().fromMap(map);
		EncryptedClaim expected = new EncryptedClaim()
			.setAlg("this_is_the_alg")
			.setKid("this_is_the_kid")
			.setValue(null);

		assertEquals(expected, actual);
	}

	/**
	 * Test method for {@link it.pagopa.swclient.mil.auth.bean.EncryptedClaim#fromMap(java.util.Map)}.
	 */
	@Test
	void given_mapWithBadValue_when_fromMapIsInvoked_then_getObjectWithNullValue() {
		Map<String, Object> map = new HashMap<>();
		map.put("alg", "this_is_the_alg");
		map.put("kid", "this_is_the_kid");
		map.put("value", "$");

		EncryptedClaim actual = new EncryptedClaim().fromMap(map);
		EncryptedClaim expected = new EncryptedClaim()
			.setAlg("this_is_the_alg")
			.setKid("this_is_the_kid")
			.setValue(null);

		assertEquals(expected, actual);
	}

	/**
	 * Test method for {@link it.pagopa.swclient.mil.auth.bean.EncryptedClaim#toMap()}.
	 */
	@Test
	void given_objectWOValue_when_toMapIsInvoked_then_getMapWithNullValue() {
		Map<String, Object> expected = new HashMap<>();
		expected.put("alg", null);
		expected.put("kid", null);
		expected.put("value", null);

		assertEquals(
			expected,
			new EncryptedClaim()
				.toMap());
	}
}
