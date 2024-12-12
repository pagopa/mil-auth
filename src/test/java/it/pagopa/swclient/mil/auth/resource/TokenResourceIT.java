/*
 * TokenResourceIT.java
 *
 * 5 dic 2024
 */
package it.pagopa.swclient.mil.auth.resource;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import java.io.File;

import org.assertj.core.util.Files;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import com.atlassian.oai.validator.restassured.OpenApiValidationFilter;
import com.nimbusds.jose.util.StandardCharset;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import it.pagopa.swclient.mil.auth.bean.AuthFormParamName;
import it.pagopa.swclient.mil.auth.bean.AuthJsonPropertyName;
import it.pagopa.swclient.mil.auth.bean.GrantType;
import it.pagopa.swclient.mil.auth.bean.Scope;
import it.pagopa.swclient.mil.auth.bean.TokenType;
import jakarta.ws.rs.core.MediaType;

/**
 * To run this from your workstation, connect to CSTAR-DEV by VPN and:
 * 
 * // @formatter:off
 * 
 * mvn verify \
  -DskipUTs=true \
  -DskipITs=false \
  -Dbase_uri=https://cstar-d-mcshared-auth-ca.blueforest-569cf489.westeurope.azurecontainerapps.io:443 \
  -Dadmin_client_id=f0ef1b15-c54a-4552-9e9a-2ca4d83260d7 \
  -Dadmin_client_secret=7fc345f5-0f6f-4df1-9f1c-21a6a8a95da0 \
  -Dtoken_info_client_id=null \
  -Dtoken_info_client_secret=null \
  -Dtest_username=null \
  -Dtest_password=null
 * 
 * // @formatter:on
 * 
 * @author antonio.tarricone
 */
@QuarkusTest
class TokenResourceIT {
	/*
	 * 
	 */
	@ConfigProperty(name = "base_uri")
	String baseUri;
	
	@ConfigProperty(name = "port", defaultValue = "443")
	int port;
	
	/*
	 * 
	 */
	@ConfigProperty(name = "admin_client_id")
	String adminClientId;

	@ConfigProperty(name = "admin_client_secret")
	String adminClientSecret;

	/*
	 * 
	 */
	@ConfigProperty(name = "token_info_client_id")
	String tokenInfoClientId;

	@ConfigProperty(name = "token_info_client_secret")
	String tokenInfoClientSecret;

	/*
	 * 
	 */
	@ConfigProperty(name = "test_username")
	String testUsername;

	@ConfigProperty(name = "test_password")
	String testPassword;

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
