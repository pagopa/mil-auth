/*
 * OpenIdConfResourceTest.java
 *
 * 14 nov 2023
 */
package it.pagopa.swclient.mil.auth.resource;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import it.pagopa.swclient.mil.auth.bean.OpenIdConf;
import jakarta.ws.rs.core.MediaType;

/**
 *
 * @author Antonio Tarricone
 */
@QuarkusTest
@TestHTTPEndpoint(OpenIdConfResource.class)
class OpenIdConfResourceTest {
	/*
	 * 
	 */
	@ConfigProperty(name = "base-url")
	String baseUrl;

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
	void test() {
		String issuer = baseUrl.replaceAll("\\/$", "") + "/";
		OpenIdConf expected = new OpenIdConf(issuer, issuer + "token", issuer + ".well-known/jwks.json");

		OpenIdConf actual = given()
			.when()
			.get()
			.then()
			.log()
			.everything()
			.statusCode(200)
			.contentType(MediaType.APPLICATION_JSON)
			.extract()
			.response()
			.as(OpenIdConf.class);

		assertEquals(expected, actual);
	}
}
