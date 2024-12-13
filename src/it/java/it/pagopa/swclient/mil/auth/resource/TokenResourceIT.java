/*
 * TokenResourceIT.java
 *
 * 5 dic 2024
 */
package it.pagopa.swclient.mil.auth.resource;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.nullValue;

import java.io.File;

import org.assertj.core.util.Files;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import com.atlassian.oai.validator.restassured.OpenApiValidationFilter;
import com.nimbusds.jose.util.StandardCharset;

import io.restassured.RestAssured;
import it.pagopa.swclient.mil.auth.bean.AuthFormParamName;
import it.pagopa.swclient.mil.auth.bean.AuthJsonPropertyName;
import it.pagopa.swclient.mil.auth.bean.GrantType;
import it.pagopa.swclient.mil.auth.bean.Scope;

/**
 * To run this from your workstation, connect to CSTAR-DEV by VPN and:
 * 
 * // @formatter:off
 * 
 * mvn verify \
 *   -DskipUTs=true \
 *   -DskipITs=false \
 *   -Dbase_uri=${base_uri} \
 *   -Dadmin_client_id=${admin_client_id} \
 *   -Dadmin_client_secret=${admin_client_secret} \
 *   -Dtoken_info_client_id=${token_info_client_id} \
 *   -Dtoken_info_client_secret=${token_info_client_secret} \
 *   -Dtest_username=${test_username} \
 *   -Dtest_password=${test_password}
 * 
 * // @formatter:on
 * 
 * @author Antonio Tarricone
 */
class TokenResourceIT {
	/*
	 * 
	 */
	private static String baseUri;
	
	/*
	 * 
	 */
	private static String adminClientId;
	private static String adminClientSecret;

	/*
	 * 
	 */
	private static String tokenInfoClientId;
	private static String tokenInfoClientSecret;

	/*
	 * 
	 */
	private static String testUsername;
	private static String testPassword;

	/**
	 * 
	 */
	@BeforeAll
	static void loadOpenApiDescriptor() {
		RestAssured.filters(
			new OpenApiValidationFilter(
				Files.contentOf(
					new File("src/main/resources/META-INF/openapi.yaml"),
					StandardCharset.UTF_8))/*,
			new RequestLoggingFilter(System.out),
			new ResponseLoggingFilter(System.out)*/);
		
		baseUri = System.getProperty("base_uri");

		adminClientId = System.getProperty("admin_client_id");
		adminClientSecret = System.getProperty("admin_client_secret");

		tokenInfoClientId = System.getProperty("token_info_client_id");
		tokenInfoClientSecret = System.getProperty("token_info_client_secret");

		testUsername = System.getProperty("test_username");
		testPassword = System.getProperty("test_password");
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
	void given_rightClientCredentials_when_theEndPointIsInvoked_then_getAccessToken() {
		given()
			.baseUri(baseUri)
			.formParam(AuthFormParamName.CLIENT_ID, adminClientId)
			.formParam(AuthFormParamName.GRANT_TYPE, GrantType.CLIENT_CREDENTIALS)
			.formParam(AuthFormParamName.CLIENT_SECRET, adminClientSecret)
			.when()
			.post("/token")
			.then()
			.statusCode(200)
			.body(AuthJsonPropertyName.REFRESH_TOKEN, nullValue());
	}
	
	/**
	 * 
	 */
	@Test
	void given_wrongClientId_when_theEndPointIsInvoked_then_getError() {
		given()
			.baseUri(baseUri)
			.formParam(AuthFormParamName.CLIENT_ID, "00000000-0000-0000-0000-000000000000")
			.formParam(AuthFormParamName.GRANT_TYPE, GrantType.CLIENT_CREDENTIALS)
			.formParam(AuthFormParamName.CLIENT_SECRET, adminClientSecret)
			.when()
			.post("/token")
			.then()
			.statusCode(401);
	}
	
	/**
	 * 
	 */
	@Test
	void given_wrongClientSecret_when_theEndPointIsInvoked_then_getError() {
		given()
			.baseUri(baseUri)
			.formParam(AuthFormParamName.CLIENT_ID, adminClientId)
			.formParam(AuthFormParamName.GRANT_TYPE, GrantType.CLIENT_CREDENTIALS)
			.formParam(AuthFormParamName.CLIENT_SECRET, "00000000-0000-0000-0000-000000000000")
			.when()
			.post("/token")
			.then()
			.statusCode(401);
	}
	
	/**
	 * 
	 */
	@Test
	void given_rightClientCredentialsAndOfflineAccessIsRequired_when_theEndPointIsInvoked_then_getAccessToken() {
		given()
			.baseUri(baseUri)
			.formParam(AuthFormParamName.CLIENT_ID, adminClientId)
			.formParam(AuthFormParamName.GRANT_TYPE, GrantType.CLIENT_CREDENTIALS)
			.formParam(AuthFormParamName.CLIENT_SECRET, adminClientSecret)
			.formParam(AuthFormParamName.SCOPE, Scope.OFFLINE_ACCESS)
			.when()
			.post("/token")
			.then()
			.statusCode(400);
	}
	
	/**
	 * 
	 */
	@Test
	void given_rightClientCredentialsAndFiscalCode_when_theEndPointIsInvoked_then_getAccessToken() {
		given()
			.baseUri(baseUri)
			.formParam(AuthFormParamName.CLIENT_ID, adminClientId)
			.formParam(AuthFormParamName.GRANT_TYPE, GrantType.CLIENT_CREDENTIALS)
			.formParam(AuthFormParamName.CLIENT_SECRET, adminClientSecret)
			.formParam(AuthFormParamName.FISCAL_CODE, "RSSMRA85T10A562S")
			.when()
			.post("/token")
			.then()
			.statusCode(200)
			.body(AuthJsonPropertyName.REFRESH_TOKEN, nullValue());
	}
}
