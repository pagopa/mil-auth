/*
 * TokenResourceIT.java
 *
 * 5 dic 2024
 */
package it.pagopa.swclient.mil.auth.resource;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.notNullValue;

import java.io.File;

import org.assertj.core.util.Files;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import com.atlassian.oai.validator.restassured.OpenApiValidationFilter;
import com.nimbusds.jose.util.StandardCharset;

import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import it.pagopa.swclient.mil.auth.bean.AuthFormParamName;
import it.pagopa.swclient.mil.auth.bean.AuthJsonPropertyName;
import it.pagopa.swclient.mil.auth.bean.GetAccessTokenResponse;
import it.pagopa.swclient.mil.auth.bean.GrantType;
import it.pagopa.swclient.mil.auth.bean.Scope;
import it.pagopa.swclient.mil.bean.HeaderParamName;

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
 *   -Dsecretless_client_id=${secretless_client_id} \
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
	private static String secretlessClientId;

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
			new RequestLoggingFilter(),
			new ResponseLoggingFilter()*/);

		baseUri = System.getProperty("base_uri");

		adminClientId = System.getProperty("admin_client_id");
		adminClientSecret = System.getProperty("admin_client_secret");
		
		secretlessClientId = System.getProperty("secretless_client_id");

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
			.header(HeaderParamName.REQUEST_ID, "ffffffff-0000-0000-0000-000000000001")
			.formParam(AuthFormParamName.GRANT_TYPE, GrantType.CLIENT_CREDENTIALS)
			.formParam(AuthFormParamName.CLIENT_ID, adminClientId)
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
			.header(HeaderParamName.REQUEST_ID, "ffffffff-0000-0000-0000-000000000002")
			.formParam(AuthFormParamName.GRANT_TYPE, GrantType.CLIENT_CREDENTIALS)
			.formParam(AuthFormParamName.CLIENT_ID, "00000000-0000-0000-0000-000000000000")
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
			.header(HeaderParamName.REQUEST_ID, "ffffffff-0000-0000-0000-000000000003")
			.formParam(AuthFormParamName.GRANT_TYPE, GrantType.CLIENT_CREDENTIALS)
			.formParam(AuthFormParamName.CLIENT_ID, adminClientId)
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
	void given_rightClientCredentialsAndOfflineAccessIsRequired_when_theEndPointIsInvoked_then_getError() {
		given()
			.baseUri(baseUri)
			.header(HeaderParamName.REQUEST_ID, "ffffffff-0000-0000-0000-000000000004")
			.formParam(AuthFormParamName.GRANT_TYPE, GrantType.CLIENT_CREDENTIALS)
			.formParam(AuthFormParamName.CLIENT_ID, adminClientId)
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
			.header(HeaderParamName.REQUEST_ID, "ffffffff-0000-0000-0000-000000000005")
			.formParam(AuthFormParamName.GRANT_TYPE, GrantType.CLIENT_CREDENTIALS)
			.formParam(AuthFormParamName.CLIENT_ID, adminClientId)
			.formParam(AuthFormParamName.CLIENT_SECRET, adminClientSecret)
			.formParam(AuthFormParamName.FISCAL_CODE, "RSSMRA85T10A562S")
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
	void given_rightUsernameAndPassword_when_theEndPointIsInvoked_then_getAccessToken() {
		given()
			.baseUri(baseUri)
			.header(HeaderParamName.REQUEST_ID, "ffffffff-0000-0000-0000-000000000006")
			.formParam(AuthFormParamName.GRANT_TYPE, GrantType.PASSWORD)
			.formParam(AuthFormParamName.CLIENT_ID, secretlessClientId)
			.formParam(AuthFormParamName.USERNAME, testUsername)
			.formParam(AuthFormParamName.PASSWORD, testPassword)
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
	void given_wrongUsername_when_theEndPointIsInvoked_then_getAccessToken() {
		given()
			.baseUri(baseUri)
			.header(HeaderParamName.REQUEST_ID, "ffffffff-0000-0000-0000-000000000007")
			.formParam(AuthFormParamName.GRANT_TYPE, GrantType.PASSWORD)
			.formParam(AuthFormParamName.CLIENT_ID, secretlessClientId)
			.formParam(AuthFormParamName.USERNAME, "wrong_user_name")
			.formParam(AuthFormParamName.PASSWORD, testPassword)
			.when()
			.post("/token")
			.then()
			.statusCode(401);
	}

	/**
	 * 
	 */
	@Test
	void given_wrongPassword_when_theEndPointIsInvoked_then_getAccessToken() {
		given()
			.baseUri(baseUri)
			.header(HeaderParamName.REQUEST_ID, "ffffffff-0000-0000-0000-000000000008")
			.formParam(AuthFormParamName.GRANT_TYPE, GrantType.PASSWORD)
			.formParam(AuthFormParamName.CLIENT_ID, secretlessClientId)
			.formParam(AuthFormParamName.USERNAME, testUsername)
			.formParam(AuthFormParamName.PASSWORD, "wrong_password")
			.when()
			.post("/token")
			.then()
			.statusCode(401);
	}

	/**
	 * 
	 */
	@Test
	void given_rightUsernameAndPasswordAndOfflineAccessIsRequired_when_theEndPointIsInvoked_then_getAccessAndRefreshToken() {
		given()
			.baseUri(baseUri)
			.header(HeaderParamName.REQUEST_ID, "ffffffff-0000-0000-0000-000000000009")
			.formParam(AuthFormParamName.GRANT_TYPE, GrantType.PASSWORD)
			.formParam(AuthFormParamName.CLIENT_ID, secretlessClientId)
			.formParam(AuthFormParamName.USERNAME, testUsername)
			.formParam(AuthFormParamName.PASSWORD, testPassword)
			.formParam(AuthFormParamName.SCOPE, Scope.OFFLINE_ACCESS)
			.when()
			.post("/token")
			.then()
			.statusCode(200)
			.body(AuthJsonPropertyName.REFRESH_TOKEN, notNullValue());
	}

	/**
	 * 
	 */
	@Test
	void given_rightRefreshToken_when_theEndPointIsInvoked_then_getAccessAndRefreshToken() {
		/*
		 * Get refresh token.
		 */
		GetAccessTokenResponse getAccessTokenResponse = given()
			.baseUri(baseUri)
			.header(HeaderParamName.REQUEST_ID, "ffffffff-0000-0000-0000-00000000000a")
			.formParam(AuthFormParamName.GRANT_TYPE, GrantType.PASSWORD)
			.formParam(AuthFormParamName.CLIENT_ID, secretlessClientId)
			.formParam(AuthFormParamName.USERNAME, testUsername)
			.formParam(AuthFormParamName.PASSWORD, testPassword)
			.formParam(AuthFormParamName.SCOPE, Scope.OFFLINE_ACCESS)
			.when()
			.post("/token")
			.then()
			.statusCode(200)
			.body(AuthJsonPropertyName.REFRESH_TOKEN, notNullValue())
			.extract()
			.response()
			.as(GetAccessTokenResponse.class);

		String refreshToken = getAccessTokenResponse.getRefreshToken();

		/*
		 * Test
		 */
		given()
			.baseUri(baseUri)
			.header(HeaderParamName.REQUEST_ID, "ffffffff-0000-0000-0000-00000000000b")
			.formParam(AuthFormParamName.GRANT_TYPE, GrantType.REFRESH_TOKEN)
			.formParam(AuthFormParamName.CLIENT_ID, secretlessClientId)
			.formParam(AuthFormParamName.REFRESH_TOKEN, refreshToken)
			.when()
			.post("/token")
			.then()
			.statusCode(200)
			.body(AuthJsonPropertyName.REFRESH_TOKEN, notNullValue());
	}

	/**
	 * 
	 */
	@Test
	void given_wrongRefreshToken_when_theEndPointIsInvoked_then_getError() {
		given()
			.baseUri(baseUri)
			.header(HeaderParamName.REQUEST_ID, "ffffffff-0000-0000-0000-00000000000c")
			.formParam(AuthFormParamName.GRANT_TYPE, GrantType.REFRESH_TOKEN)
			.formParam(AuthFormParamName.CLIENT_ID, secretlessClientId)
			.formParam(AuthFormParamName.REFRESH_TOKEN, "eyJraWQiOiI5M2IxZjYxYy0yYTQ4LTQwNzYtOGRhNi1mNjEwZWNkNTc4M2UiLCJhbGciOiJSUzI1NiJ9.eyJzdWIiOiI1MjU0ZjA4Ny0xMjE0LTQ1Y2QtOTRhZS1mZGE1M2M4MzUxOTciLCJjbGllbnRJZCI6IjUyNTRmMDg3LTEyMTQtNDVjZC05NGFlLWZkYTUzYzgzNTE5NyIsIm1lcmNoYW50SWQiOiIyODQwNWZIZms3M3g4OEQiLCJzY29wZSI6Im9mZmxpbmVfYWNjZXNzIiwiY2hhbm5lbCI6IlBPUyIsInRlcm1pbmFsSWQiOiIwMTIzNDU2NyIsImV4cCI6MTY4NjA0MzQ1MiwiYWNxdWlyZXJJZCI6IjQ1ODU2MjUiLCJpYXQiOjE2ODYwMzk4NTJ9.RRrtFUL2fGmdjNCVlvBOCze9z3Wo2XSfM-c4dd4RV3fFvsqum2WgTXOACaX0RKJBiMj2SNSpdcQK2OgyXZx_z7j6c8PrziC2mJZaT0vmQ7pWEF_0sNYz_Pwulha3Ykx7wFhcRngMelCOb-PrtHuLoV4XwtBrEk3pzpeJJdmFauGsoQ_079NTBaDfDrpcc7armHiPQ4-7ZKRsOPu-FVtqiB4sdOisg-u1p0XvvoDGnxP0A-7c6N7pvrnTVCnIjrYPV0_-MgFzH1WhQ8baoNSr3lsPG3H9Fs1dVXVsfTA3hYnn7ezbIlETW6TXIiWRoZ1yvjP7NoGKgH_6_NHFgYDbMjPUQPdByu11WJ640fLILk3DF2Se7yHEQb7-N_QhOpx2SqeZzI56Y659d8BUk-IkgG20A2N2GWDWgGCcboPmfE9Np67yj2znAIMo8WhoGUD9cLuTEGBBFOjiZ8pDxfSsdGsO4rfjOAayJ_kbLtbc_Tj6ZPymv3vSMISXYAASteynrU3bv-Td-H9Wzs1ABJFHyLFLibSCqztNOkBn9iiWCKrR30iQUxcqmiypZoQT5fjkNlHCxKHF4S8QpQs9m0nq2j76_7ipzDaKgE2i8HtccLBi3XvOl88brmjnKilk49MpoNAPsgRVNz-DZ2pq9olQh7o_y--0_T4ht7zhto7a69I")
			.when()
			.post("/token")
			.then()
			.statusCode(401);
	}

	/**
	 * 
	 */
	@Test
	void given_rightRefreshTokenAndOfflineAccessIsRequired_when_theEndPointIsInvoked_then_getError() {
		/*
		 * Get refresh token.
		 */
		GetAccessTokenResponse getAccessTokenResponse = given()
			.baseUri(baseUri)
			.header(HeaderParamName.REQUEST_ID, "ffffffff-0000-0000-0000-00000000000d")
			.formParam(AuthFormParamName.GRANT_TYPE, GrantType.PASSWORD)
			.formParam(AuthFormParamName.CLIENT_ID, secretlessClientId)
			.formParam(AuthFormParamName.USERNAME, testUsername)
			.formParam(AuthFormParamName.PASSWORD, testPassword)
			.formParam(AuthFormParamName.SCOPE, Scope.OFFLINE_ACCESS)
			.when()
			.post("/token")
			.then()
			.statusCode(200)
			.body(AuthJsonPropertyName.REFRESH_TOKEN, notNullValue())
			.extract()
			.response()
			.as(GetAccessTokenResponse.class);

		String refreshToken = getAccessTokenResponse.getRefreshToken();

		/*
		 * Test
		 */
		given()
			.baseUri(baseUri)
			.header(HeaderParamName.REQUEST_ID, "ffffffff-0000-0000-0000-00000000000e")
			.formParam(AuthFormParamName.GRANT_TYPE, GrantType.REFRESH_TOKEN)
			.formParam(AuthFormParamName.CLIENT_ID, secretlessClientId)
			.formParam(AuthFormParamName.REFRESH_TOKEN, refreshToken)
			.formParam(AuthFormParamName.SCOPE, Scope.OFFLINE_ACCESS)
			.when()
			.post("/token")
			.then()
			.statusCode(400);
	}
}