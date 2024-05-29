/**
 * 
 */
package it.pagopa.swclient.mil.auth.client;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.core.Response;

/**
 * 
 */
@QuarkusTest
@TestInstance(Lifecycle.PER_CLASS)
class PoyntClientTest {
	/*
	 * 
	 */
	private PoyntClient client;

	/**
	 * 
	 */
	@BeforeAll
	private void init() {
		client = new PoyntClient() {
			@Override
			public Uni<Response> getBusinessObject(String poyntToken, String businessId) {
				return null;
			}
		};
	}

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
	void testWithParamOk() {
		assertNotNull(client.withParam("POYNT-REQUEST-ID"));
	}

	/**
	 * 
	 */
	@Test
	void testWithParamKo() {
		assertThrows(IllegalArgumentException.class, () -> {
			client.withParam("POYNT-REQUEST-ID2");
		});
	}
}
