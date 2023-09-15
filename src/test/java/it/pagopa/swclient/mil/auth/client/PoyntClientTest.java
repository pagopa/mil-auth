/**
 * 
 */
package it.pagopa.swclient.mil.auth.client;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
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
