/*
 * KidUtilTest.java
 *
 * 28 mag 2024
 */
package it.pagopa.swclient.mil.auth.azure.keyvault.util;

import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;

/**
 * Additional tests.
 * 
 * @author Antonio Tarricone
 */
@QuarkusTest
class KidUtilTest {
	/*
	 * 
	 */
	@Inject
	KidUtil kidUtil;

	/**
	 * Test method for
	 * {@link it.pagopa.swclient.mil.auth.azure.keyvault.util.KidUtil#getMyKidFromAzureOne(java.lang.String)}.
	 */
	@Test
	void given_invalidAzureKid_when_getMyKidFromAzureOneIsInvoked_then_getNull() {
		assertNull(kidUtil.getMyKidFromAzureOne("http://dummy/dummy"));
	}
}
