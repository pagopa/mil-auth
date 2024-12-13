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
 * mvn test-compile -q exec:java -Pit2 -Dexec.classpathScope=test
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
public class TokenResourceIntegration {
	/*
	 * 
	 */
	private String baseUri;

	/*
	 * 
	 */
	private String adminClientId;
	private String adminClientSecret;

	/*
	 * 
	 */
	private String tokenInfoClientId;
	private String tokenInfoClientSecret;

	/*
	 * 
	 */
	private String testUsername;
	private String testPassword;

	/**
	 * 
	 */
	public TokenResourceIntegration() {
		RestAssured.filters(
			new OpenApiValidationFilter(
				Files.contentOf(
					new File("src/main/resources/META-INF/openapi.yaml"),
					StandardCharset.UTF_8)));

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
	 */
	private TokenResourceIntegration given_rightClientCredentials_when_theEndPointIsInvoked_then_getAccessToken() {
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
		return this;
	}

	/**
	 * 
	 */
	private TokenResourceIntegration given_wrongClientId_when_theEndPointIsInvoked_then_getError() {
		given()
			.baseUri(baseUri)
			.formParam(AuthFormParamName.CLIENT_ID, "00000000-0000-0000-0000-000000000000")
			.formParam(AuthFormParamName.GRANT_TYPE, GrantType.CLIENT_CREDENTIALS)
			.formParam(AuthFormParamName.CLIENT_SECRET, adminClientSecret)
			.when()
			.post("/token")
			.then()
			.statusCode(401);
		return this;
	}

	/**
	 * 
	 */
	private TokenResourceIntegration given_wrongClientSecret_when_theEndPointIsInvoked_then_getError() {
		given()
			.baseUri(baseUri)
			.formParam(AuthFormParamName.CLIENT_ID, adminClientId)
			.formParam(AuthFormParamName.GRANT_TYPE, GrantType.CLIENT_CREDENTIALS)
			.formParam(AuthFormParamName.CLIENT_SECRET, "00000000-0000-0000-0000-000000000000")
			.when()
			.post("/token")
			.then()
			.statusCode(401);
		return this;
	}

	/**
	 * 
	 */
	private TokenResourceIntegration given_rightClientCredentialsAndOfflineAccessIsRequired_when_theEndPointIsInvoked_then_getAccessToken() {
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
		return this;
	}

	/**
	 * 
	 */
	private TokenResourceIntegration given_rightClientCredentialsAndFiscalCode_when_theEndPointIsInvoked_then_getAccessToken() {
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
		return this;
	}

	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		new TokenResourceIntegration()
			.given_rightClientCredentials_when_theEndPointIsInvoked_then_getAccessToken()
			.given_wrongClientId_when_theEndPointIsInvoked_then_getError()
			.given_wrongClientSecret_when_theEndPointIsInvoked_then_getError()
			.given_rightClientCredentialsAndOfflineAccessIsRequired_when_theEndPointIsInvoked_then_getAccessToken()
			.given_rightClientCredentialsAndFiscalCode_when_theEndPointIsInvoked_then_getAccessToken();
	}
}
