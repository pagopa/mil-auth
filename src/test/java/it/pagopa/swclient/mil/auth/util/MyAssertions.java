/*
 * MyAssertions.java
 *
 * 21 gen 2025
 */
package it.pagopa.swclient.mil.auth.util;

import static org.junit.jupiter.api.AssertionFailureBuilder.assertionFailure;

import java.util.Objects;

/**
 * 
 * @author Antonio Tarricone
 */
public class MyAssertions {
	/**
	 * 
	 */
	private MyAssertions() {
	}

	/**
	 * 
	 * @param failure
	 * @param expectedErrorCode
	 */
	public static void assertAuthError(Throwable failure, String expectedErrorCode) {
		if (failure instanceof AuthError error) {
			if (!Objects.equals(error.getCode(), expectedErrorCode)) {
				assertionFailure()
					.message("Expected error code " + expectedErrorCode + ", found " + error.getCode())
					.buildAndThrow();

			}
		} else {
			assertionFailure()
				.message("Expected AuthError, found " + failure)
				.buildAndThrow();
		}
	}

	/**
	 * 
	 * @param failure
	 * @param expectedErrorCode
	 */
	public static void assertAuthException(Throwable failure, String expectedErrorCode) {
		if (failure instanceof AuthException exception) {
			if (!Objects.equals(exception.getCode(), expectedErrorCode)) {
				assertionFailure()
					.message("Expected error code " + expectedErrorCode + ", found " + exception.getCode())
					.buildAndThrow();

			}
		} else {
			assertionFailure()
				.message("Expected AuthException, found " + failure.getClass())
				.buildAndThrow();
		}
	}
}
