/*
 * SecretResourceTest.java
 *
 * 29 jun 2023
 */
package it.pagopa.swclient.mil.auth.resource;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.any;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import com.nimbusds.jose.JOSEException;

import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;

/**
 * 
 * @author Antonio Tarricone
 */
@QuarkusTest
@TestHTTPEndpoint(SecretResource.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SecretResourceTest {
	/**
	 * 
	 */
	@Test()
	void generateSecret() {
		given()
			.when()
			.post()
			.then()
			.statusCode(any(Integer.class));
	}

	/**
	 * 
	 */
	@Test()
	void retrieveSecret() throws JOSEException {
		given()
			.when()
			.get()
			.then()
			.statusCode(any(Integer.class));
	}

	/**
	 * 
	 */
	@Test()
	void deleteSecret() {
		given()
			.when()
			.delete()
			.then()
			.statusCode(any(Integer.class));
	}
}