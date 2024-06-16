/*
 * KeyIdCacheTest.java
 *
 * 14 giu 2024
 */
package it.pagopa.swclient.mil.auth.bean;

import static org.junit.jupiter.api.Assertions.assertFalse;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import io.quarkus.test.junit.QuarkusTest;

/**
 * Additional tests.
 * 
 * @author Antonio Tarricone
 */
@QuarkusTest
class KeyIdCacheTest {
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
	 * Test method for {@link it.pagopa.swclient.mil.auth.bean.KeyIdCache#isValid(long)}.
	 */
	@Test
	void given_nullKid_when_isValidIsInvoked_then_getFalse() {
		assertFalse(new KeyIdCache()
			.setExp(Instant.now().plus(15, ChronoUnit.MINUTES).toEpochMilli())
			.setKid(null)
			.setStoredAt(Instant.now().toEpochMilli())
			.isValid(0, 60));
	}

	/**
	 * Test method for {@link it.pagopa.swclient.mil.auth.bean.KeyIdCache#isValid(long)}.
	 */
	@Test
	void given_expiredKey_when_isValidIsInvoked_then_getFalse() {
		assertFalse(new KeyIdCache()
			.setExp(Instant.now().minus(15, ChronoUnit.MINUTES).getEpochSecond())
			.setKid("kid")
			.setStoredAt(Instant.now().getEpochSecond())
			.isValid(0, 60));
	}

	/**
	 * Test method for {@link it.pagopa.swclient.mil.auth.bean.KeyIdCache#isValid(long)}.
	 */
	@Test
	void given_expiredCache_when_isValidIsInvoked_then_getFalse() {
		assertFalse(new KeyIdCache()
			.setExp(Instant.now().plus(15, ChronoUnit.MINUTES).getEpochSecond())
			.setKid("kid")
			.setStoredAt(Instant.now().minus(5, ChronoUnit.MINUTES).getEpochSecond())
			.isValid(0, 60));
	}
}