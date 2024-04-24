/*
 * JwksResourceTest.java
 *
 * 14 set 2023
 */
package it.pagopa.swclient.mil.auth.resource;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItem;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import io.quarkus.test.InjectMock;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.mutiny.Uni;
import it.pagopa.swclient.mil.auth.AuthErrorCode;
import it.pagopa.swclient.mil.auth.bean.JsonPropertyName;
import it.pagopa.swclient.mil.auth.bean.KeyType;
import it.pagopa.swclient.mil.auth.bean.KeyUse;
import it.pagopa.swclient.mil.auth.bean.PublicKey;
import it.pagopa.swclient.mil.auth.bean.PublicKeys;
import it.pagopa.swclient.mil.auth.service.KeyFinder;
import jakarta.ws.rs.core.MediaType;

/**
 *
 * @author Antonio Tarricone
 */
@QuarkusTest
@TestHTTPEndpoint(JwksResource.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class JwksResourceTest {
	/*
	 * 
	 */
	@InjectMock
	KeyFinder keyFinder;

	@Test
	void testOk() {
		/*
		 * Setup.
		 */
		long now = Instant.now().getEpochSecond();
		long exp = now + 900;
		PublicKey publicKey1 = new PublicKey("exp1", KeyUse.sig, "kid1", "mod1", KeyType.RSA, exp, now);
		PublicKey publicKey2 = new PublicKey("exp2", KeyUse.sig, "kid2", "mod2", KeyType.RSA, exp + 10, now + 10);

		PublicKeys publicKeys = new PublicKeys(List.of(publicKey1, publicKey2));

		when(keyFinder.findPublicKeys())
			.thenReturn(Uni.createFrom().item(publicKeys));

		/*
		 * Test.
		 */
		PublicKeys response = given()
			.when()
			.get()
			.then()
			.log()
			.everything()
			.statusCode(200)
			.contentType(MediaType.APPLICATION_JSON)
			.header("Cache-Control", containsString("max-age"))
			.extract()
			.response()
			.as(PublicKeys.class);

		assertEquals(publicKeys, response);
	}

	@Test
	void testWithExpirationInAWhileOk() {
		/*
		 * Setup.
		 */
		long now = Instant.now().getEpochSecond();
		long exp = now + 5 * 60;
		PublicKey publicKey1 = new PublicKey("exp1", KeyUse.sig, "kid1", "mod1", KeyType.RSA, exp, now);

		PublicKeys publicKeys = new PublicKeys(List.of(publicKey1));

		when(keyFinder.findPublicKeys())
			.thenReturn(Uni.createFrom().item(publicKeys));

		/*
		 * Test.
		 */
		PublicKeys response = given()
			.when()
			.get()
			.then()
			.log()
			.everything()
			.statusCode(200)
			.contentType(MediaType.APPLICATION_JSON)
			.header("Cache-Control", containsString("no-cache"))
			.extract()
			.response()
			.as(PublicKeys.class);

		assertEquals(publicKeys, response);
	}

	@Test
	void testWithoutKeys() {
		/*
		 * Setup.
		 */
		PublicKeys publicKeys = new PublicKeys(List.of());

		when(keyFinder.findPublicKeys())
			.thenReturn(Uni.createFrom().item(publicKeys));

		/*
		 * Test.
		 */
		PublicKeys response = given()
			.when()
			.get()
			.then()
			.log()
			.everything()
			.statusCode(200)
			.contentType(MediaType.APPLICATION_JSON)
			.header("Cache-Control", containsString("no-cache"))
			.extract()
			.response()
			.as(PublicKeys.class);

		assertEquals(publicKeys, response);
	}

	@Test
	void testWithError() {
		/*
		 * Setup.
		 */
		when(keyFinder.findPublicKeys())
			.thenReturn(Uni.createFrom().failure(new Exception("synthetic exception")));

		/*
		 * Test.
		 */
		given()
			.when()
			.get()
			.then()
			.log()
			.everything()
			.statusCode(500)
			.contentType(MediaType.APPLICATION_JSON)
			.body(JsonPropertyName.ERRORS, hasItem(AuthErrorCode.ERROR_SEARCHING_FOR_KEYS));
	}
}
