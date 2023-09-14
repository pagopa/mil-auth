/*
 * RequestValidationTest.java
 *
 * 1 giu 2023
 */
package it.pagopa.swclient.mil.auth.resource;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import it.pagopa.swclient.mil.auth.bean.FormParamName;
import it.pagopa.swclient.mil.auth.bean.GrantType;
import it.pagopa.swclient.mil.auth.bean.HeaderParamName;
import it.pagopa.swclient.mil.auth.bean.JsonPropertyName;
import it.pagopa.swclient.mil.auth.bean.Scope;
import it.pagopa.swclient.mil.bean.Channel;
import jakarta.ws.rs.core.MediaType;

/**
 * @author Antonio Tarricone
 */
@QuarkusTest
@TestHTTPEndpoint(TokenResource.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RequestValidationTest {
	/*
	 *
	 */
	private static final String clientId = "5254f087-1214-45cd-94ae-fda53c835197";
	private static final String acquirerId = "4585625";
	private static final String merchantId = "28405fHfk73x88D";
	private static final String terminalId = "01234567";
	private static final String extToken = "eyJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJOZXhpIiwicG95bnQuZGlkIjoidXJuOnRpZDo1NTYyYjhlZC1lODljLTMzMmEtYThkYy1jYTA4MTcxMzUxMTAiLCJwb3ludC5kc3QiOiJEIiwicG95bnQub3JnIjoiMGU2Zjc4ODYtMDk1Ni00NDA1LWJjNDgtYzE5ODY4ZDdlZTIyIiwicG95bnQuc2N0IjoiVSIsImlzcyI6Imh0dHBzOlwvXC9zZXJ2aWNlcy1ldS5wb3ludC5uZXQiLCJwb3ludC51cmUiOiJPIiwicG95bnQua2lkIjozOTMyNDI1MjY4MDY5NDA5MjM0LCJwb3ludC5zY3YiOiJOZXhpIiwicG95bnQuc3RyIjoiZDNmZDNmZDMtMTg5ZC00N2M4LThjMzYtYjY4NWRkNjBkOTY0IiwiYXVkIjoidXJuOnRpZDo1NTYyYjhlZC1lODljLTMzMmEtYThkYy1jYTA4MTcxMzUxMTAiLCJwb3ludC51aWQiOjM3MzY1NzQsInBveW50LmJpeiI6IjRiN2ViOTRiLTEwYzktNGYxMS1hMTBlLTcyOTJiMjlhYjExNSIsImV4cCI6MTY4NDU3NTMzNiwiaWF0IjoxNjg0NDg4OTM2LCJqdGkiOiJmNzc5MjQ1OS00ODU1LTQ5YjMtYTZiYS05N2QzNzQ5NDQ2ZGIifQ.niR8AS3OHlmWg1-n3FD4DKoAWlY0nJyEJGBZSBFWHYCl01vjIIFYCmTCyBshZVEtDBKpTG1bWTmVctOCX2ybF5gQ0vBH1H3LFD13Tf73Ps439Ht5_u3Q-jHPf_arXDf2enOs_vKwp8TsdJNPRcxMhYZ91yyiAhbHERVypP2YPszwv5h6mMq_HWNzK9qjrLh8zQCGBEMkFfnSG1xOjzTZLJ4ROPazaDHJ9DSZReC4dY_jRqAlivbXVeLOnN3D4y_GatcHQO1_p_jYE-eXHjLP-wINeAqW57P57HmSe2n67q6UkQf5v5zKVHrJpTFAtHWpDVLxmhPKGurTX45yOvaDZw";
	private static final String addData = "4b7eb94b-10c9-4f11-a10e-7292b29ab115";
	private static final String username = "mariorossi";
	private static final String password = "dF@dkj$S73j#fjd7X!";
	private static final String clientSecret = "3674f0e7-d717-44cc-a3bc-5f8f41771fea";
	private static final String refreshToken = "eyJraWQiOiI2YmY3YWE2OC04MWJkLTQ0MmItYjY2Zi0xYmM1ZmNjZTViMmIiLCJhbGciOiJSUzI1NiJ9.eyJjbGllbnRJZCI6IjUyNTRmMDg3LTEyMTQtNDVjZC05NGFlLWZkYTUzYzgzNTE5NyIsIm1lcmNoYW50SWQiOiIyODQwNWZIZms3M3g4OEQiLCJzY29wZSI6Im9mZmxpbmVfYWNjZXNzIiwiY2hhbm5lbCI6IlBPUyIsInRlcm1pbmFsSWQiOiIwMTIzNDU2NyIsImV4cCI6MTY4NDQ4OTg0MiwiYWNxdWlyZXJJZCI6IjQ1ODU2MjUiLCJpYXQiOjE2ODQ0ODYyNDJ9.nzT6-WgTLoaogCKs83y1CWC8Xkivin9iB9mqigxNqqeNtzRqTQ3JkP5bHLHPscXSAGaiPzS9OVULQnBSxhMq5UFRdKpLulOZ0anfV67-GmF1D68tm2Uqc0RyQYeUO9I_4nfVyL1qRNHed-rcZUwV1m9J-paf9uZbWQCfHOthEcOyCVCyD07X5_yiQfcFSjxKYtxy-3sZyxCjTIHnKhwY-0OZnvfmQECCK39UF9cFBAr0nRW1Z_Ox8Sj5Kd9A-c7LFQDy27eiWQWfo3Ycgp9wvr7a9GPMGvAECDSKaTkMHmKQ946BSXz7zkvK0kpxu3jnKgC8-WRrtTLNroABbU4SAGxFFhwCBP4wk8aTPRJuphldzguU4z-SeGwLitKg9-h7i7_fXo6s0BWKaftk0WBvdcNrNK5XJf0cTCYkqYekJbl4q5A7P4r9dFh9CjggsM3BgG56Bn4jGAzxmJpbQf3u2xAbM5MQGJ4ahxnhB5hzCRz6mqOSbdrSIpV0Pra7aBVi0vqe_UbxslUZQwL-W9kzCPdE0FBKOIV_E33HES98LbH7VqrrmLPJaqj6ggP3txyM9DYm2E_qgbQl0-efawIYjazPbi3PV9XliiEVIvUOcTl1EXPu7vfkyOsUuckKcUKh6xoqn6oRpVb-nIdLfPql3PSEPpKa6G3VnAMBo74Mn7I";

	/**
	 * acquirerIdMustBeNull(getAccessToken)
	 */
	@Test
	void clientCredentials1() {
		/*
		 * Test
		 */
		given()
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.header(HeaderParamName.REQUEST_ID, "00000000-0000-0000-0000-100000000001")
			.header(HeaderParamName.ACQUIRER_ID, acquirerId)
			.header(HeaderParamName.MERCHANT_ID, merchantId)
			.header(HeaderParamName.TERMINAL_ID, terminalId)
			.formParam(FormParamName.GRANT_TYPE, GrantType.CLIENT_CREDENTIALS)
			.formParam(FormParamName.CLIENT_ID, clientId)
			.formParam(FormParamName.EXT_TOKEN, extToken)
			.formParam(FormParamName.ADD_DATA, addData)
			.formParam(FormParamName.REFRESH_TOKEN, refreshToken)
			.formParam(FormParamName.USERNAME, username)
			.formParam(FormParamName.PASSWORD, password)
			.formParam(FormParamName.SCOPE, Scope.OFFLINE_ACCESS)
			.when()
			.post()
			.then()
			.statusCode(400)
			.contentType(MediaType.APPLICATION_JSON)
			.body(JsonPropertyName.ERRORS, notNullValue());
	}

	/**
	 * scopedMustBeNull(getAccessToken)
	 */
	@Test
	void clientCredentials10() {
		/*
		 * Test
		 */
		given()
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.header(HeaderParamName.REQUEST_ID, "00000000-0000-0000-0000-100000000002")
			.formParam(FormParamName.GRANT_TYPE, GrantType.CLIENT_CREDENTIALS)
			.formParam(FormParamName.CLIENT_ID, clientId)
			.formParam(FormParamName.CLIENT_SECRET, clientSecret)
			.formParam(FormParamName.SCOPE, Scope.OFFLINE_ACCESS)
			.when()
			.post()
			.then()
			.statusCode(400)
			.contentType(MediaType.APPLICATION_JSON)
			.body(JsonPropertyName.ERRORS, notNullValue());
	}

	/**
	 * merchantIdMustBeNull(getAccessToken)
	 */
	@Test
	void clientCredentials2() {
		/*
		 * Test
		 */
		given()
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.header(HeaderParamName.REQUEST_ID, "00000000-0000-0000-0000-100000000003")
			.header(HeaderParamName.MERCHANT_ID, merchantId)
			.header(HeaderParamName.TERMINAL_ID, terminalId)
			.formParam(FormParamName.GRANT_TYPE, GrantType.CLIENT_CREDENTIALS)
			.formParam(FormParamName.CLIENT_ID, clientId)
			.formParam(FormParamName.EXT_TOKEN, extToken)
			.formParam(FormParamName.ADD_DATA, addData)
			.formParam(FormParamName.REFRESH_TOKEN, refreshToken)
			.formParam(FormParamName.USERNAME, username)
			.formParam(FormParamName.PASSWORD, password)
			.formParam(FormParamName.SCOPE, Scope.OFFLINE_ACCESS)
			.when()
			.post()
			.then()
			.statusCode(400)
			.contentType(MediaType.APPLICATION_JSON)
			.body(JsonPropertyName.ERRORS, notNullValue());
	}

	/**
	 * terminalIdMustBeNull(getAccessToken)
	 */
	@Test
	void clientCredentials3() {
		/*
		 * Test
		 */
		given()
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.header(HeaderParamName.REQUEST_ID, "00000000-0000-0000-0000-100000000004")
			.header(HeaderParamName.TERMINAL_ID, terminalId)
			.formParam(FormParamName.GRANT_TYPE, GrantType.CLIENT_CREDENTIALS)
			.formParam(FormParamName.CLIENT_ID, clientId)
			.formParam(FormParamName.EXT_TOKEN, extToken)
			.formParam(FormParamName.ADD_DATA, addData)
			.formParam(FormParamName.REFRESH_TOKEN, refreshToken)
			.formParam(FormParamName.USERNAME, username)
			.formParam(FormParamName.PASSWORD, password)
			.formParam(FormParamName.SCOPE, Scope.OFFLINE_ACCESS)
			.when()
			.post()
			.then()
			.statusCode(400)
			.contentType(MediaType.APPLICATION_JSON)
			.body(JsonPropertyName.ERRORS, notNullValue());
	}

	/**
	 * clientSecretMustNotBeNull(getAccessToken)
	 */
	@Test
	void clientCredentials4() {
		/*
		 * Test
		 */
		given()
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.header(HeaderParamName.REQUEST_ID, "00000000-0000-0000-0000-100000000005")
			.formParam(FormParamName.GRANT_TYPE, GrantType.CLIENT_CREDENTIALS)
			.formParam(FormParamName.CLIENT_ID, clientId)
			.formParam(FormParamName.EXT_TOKEN, extToken)
			.formParam(FormParamName.ADD_DATA, addData)
			.formParam(FormParamName.REFRESH_TOKEN, refreshToken)
			.formParam(FormParamName.USERNAME, username)
			.formParam(FormParamName.PASSWORD, password)
			.formParam(FormParamName.SCOPE, Scope.OFFLINE_ACCESS)
			.when()
			.post()
			.then()
			.statusCode(400)
			.contentType(MediaType.APPLICATION_JSON)
			.body(JsonPropertyName.ERRORS, notNullValue());
	}

	/**
	 * extTokenMustBeNull(getAccessToken)
	 */
	@Test
	void clientCredentials5() {
		/*
		 * Test
		 */
		given()
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.header(HeaderParamName.REQUEST_ID, "00000000-0000-0000-0000-100000000006")
			.formParam(FormParamName.GRANT_TYPE, GrantType.CLIENT_CREDENTIALS)
			.formParam(FormParamName.CLIENT_ID, clientId)
			.formParam(FormParamName.CLIENT_SECRET, clientSecret)
			.formParam(FormParamName.EXT_TOKEN, extToken)
			.formParam(FormParamName.ADD_DATA, addData)
			.formParam(FormParamName.REFRESH_TOKEN, refreshToken)
			.formParam(FormParamName.USERNAME, username)
			.formParam(FormParamName.PASSWORD, password)
			.formParam(FormParamName.SCOPE, Scope.OFFLINE_ACCESS)
			.when()
			.post()
			.then()
			.statusCode(400)
			.contentType(MediaType.APPLICATION_JSON)
			.body(JsonPropertyName.ERRORS, notNullValue());
	}

	/**
	 * addDataMustBeNull(getAccessToken)
	 */
	@Test
	void clientCredentials6() {
		/*
		 * Test
		 */
		given()
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.header(HeaderParamName.REQUEST_ID, "00000000-0000-0000-0000-100000000007")
			.formParam(FormParamName.GRANT_TYPE, GrantType.CLIENT_CREDENTIALS)
			.formParam(FormParamName.CLIENT_ID, clientId)
			.formParam(FormParamName.CLIENT_SECRET, clientSecret)
			.formParam(FormParamName.ADD_DATA, addData)
			.formParam(FormParamName.REFRESH_TOKEN, refreshToken)
			.formParam(FormParamName.USERNAME, username)
			.formParam(FormParamName.PASSWORD, password)
			.formParam(FormParamName.SCOPE, Scope.OFFLINE_ACCESS)
			.when()
			.post()
			.then()
			.statusCode(400)
			.contentType(MediaType.APPLICATION_JSON)
			.body(JsonPropertyName.ERRORS, notNullValue());
	}

	/**
	 * refreshTokenMustBeNull(getAccessToken)
	 */
	@Test
	void clientCredentials7() {
		/*
		 * Test
		 */
		given()
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.header(HeaderParamName.REQUEST_ID, "00000000-0000-0000-0000-100000000008")
			.formParam(FormParamName.GRANT_TYPE, GrantType.CLIENT_CREDENTIALS)
			.formParam(FormParamName.CLIENT_ID, clientId)
			.formParam(FormParamName.CLIENT_SECRET, clientSecret)
			.formParam(FormParamName.REFRESH_TOKEN, refreshToken)
			.formParam(FormParamName.USERNAME, username)
			.formParam(FormParamName.PASSWORD, password)
			.formParam(FormParamName.SCOPE, Scope.OFFLINE_ACCESS)
			.when()
			.post()
			.then()
			.statusCode(400)
			.contentType(MediaType.APPLICATION_JSON)
			.body(JsonPropertyName.ERRORS, notNullValue());
	}

	/**
	 * usernameMustBeNull(getAccessToken)
	 */
	@Test
	void clientCredentials8() {
		/*
		 * Test
		 */
		given()
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.header(HeaderParamName.REQUEST_ID, "00000000-0000-0000-0000-100000000009")
			.formParam(FormParamName.GRANT_TYPE, GrantType.CLIENT_CREDENTIALS)
			.formParam(FormParamName.CLIENT_ID, clientId)
			.formParam(FormParamName.CLIENT_SECRET, clientSecret)
			.formParam(FormParamName.USERNAME, username)
			.formParam(FormParamName.PASSWORD, password)
			.formParam(FormParamName.SCOPE, Scope.OFFLINE_ACCESS)
			.when()
			.post()
			.then()
			.statusCode(400)
			.contentType(MediaType.APPLICATION_JSON)
			.body(JsonPropertyName.ERRORS, notNullValue());
	}

	/**
	 * passwordMustBeNull(getAccessToken)
	 */
	@Test
	void clientCredentials9() {
		/*
		 * Test
		 */
		given()
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.header(HeaderParamName.REQUEST_ID, "00000000-0000-0000-0000-10000000000a")
			.formParam(FormParamName.GRANT_TYPE, GrantType.CLIENT_CREDENTIALS)
			.formParam(FormParamName.CLIENT_ID, clientId)
			.formParam(FormParamName.CLIENT_SECRET, clientSecret)
			.formParam(FormParamName.PASSWORD, password)
			.formParam(FormParamName.SCOPE, Scope.OFFLINE_ACCESS)
			.when()
			.post()
			.then()
			.statusCode(400)
			.contentType(MediaType.APPLICATION_JSON)
			.body(JsonPropertyName.ERRORS, notNullValue());
	}

	/**
	 *
	 */
	@Test
	void clientCredentialsAtm1() {
		/*
		 * Test
		 */
		given()
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.header(HeaderParamName.REQUEST_ID, "00000000-0000-0000-0000-10000000000b")
			.header(HeaderParamName.CHANNEL, Channel.ATM)
			.header(HeaderParamName.MERCHANT_ID, merchantId)
			.formParam(FormParamName.GRANT_TYPE, GrantType.CLIENT_CREDENTIALS)
			.formParam(FormParamName.CLIENT_ID, clientId)
			.formParam(FormParamName.EXT_TOKEN, extToken)
			.formParam(FormParamName.ADD_DATA, addData)
			.formParam(FormParamName.REFRESH_TOKEN, refreshToken)
			.formParam(FormParamName.USERNAME, username)
			.formParam(FormParamName.PASSWORD, password)
			.formParam(FormParamName.SCOPE, Scope.OFFLINE_ACCESS)
			.when()
			.post()
			.then()
			.statusCode(400)
			.contentType(MediaType.APPLICATION_JSON)
			.body(JsonPropertyName.ERRORS, notNullValue());
	}

	/**
	 *
	 */
	@Test
	void clientCredentialsAtm10() {
		given()
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.header(HeaderParamName.REQUEST_ID, "00000000-0000-0000-0000-10000000000c")
			.header(HeaderParamName.CHANNEL, Channel.ATM)
			.header(HeaderParamName.ACQUIRER_ID, acquirerId)
			.header(HeaderParamName.TERMINAL_ID, terminalId)
			.formParam(FormParamName.GRANT_TYPE, GrantType.CLIENT_CREDENTIALS)
			.formParam(FormParamName.CLIENT_ID, clientId)
			.formParam(FormParamName.CLIENT_SECRET, clientSecret)
			.formParam(FormParamName.SCOPE, Scope.OFFLINE_ACCESS)
			.when()
			.post()
			.then()
			.statusCode(400)
			.contentType(MediaType.APPLICATION_JSON)
			.body(JsonPropertyName.ERRORS, notNullValue());
	}

	/**
	 *
	 */
	@Test
	void clientCredentialsAtm2() {
		/*
		 * Test
		 */
		given()
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.header(HeaderParamName.REQUEST_ID, "00000000-0000-0000-0000-10000000000d")
			.header(HeaderParamName.CHANNEL, Channel.ATM)
			.header(HeaderParamName.ACQUIRER_ID, acquirerId)
			.header(HeaderParamName.MERCHANT_ID, merchantId)
			.formParam(FormParamName.GRANT_TYPE, GrantType.CLIENT_CREDENTIALS)
			.formParam(FormParamName.CLIENT_ID, clientId)
			.formParam(FormParamName.EXT_TOKEN, extToken)
			.formParam(FormParamName.ADD_DATA, addData)
			.formParam(FormParamName.REFRESH_TOKEN, refreshToken)
			.formParam(FormParamName.USERNAME, username)
			.formParam(FormParamName.PASSWORD, password)
			.formParam(FormParamName.SCOPE, Scope.OFFLINE_ACCESS)
			.when()
			.post()
			.then()
			.statusCode(400)
			.contentType(MediaType.APPLICATION_JSON)
			.body(JsonPropertyName.ERRORS, notNullValue());
	}

	/**
	 *
	 */
	@Test
	void clientCredentialsAtm3() {
		given()
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.header(HeaderParamName.REQUEST_ID, "00000000-0000-0000-0000-10000000000e")
			.header(HeaderParamName.CHANNEL, Channel.ATM)
			.header(HeaderParamName.ACQUIRER_ID, acquirerId)
			.formParam(FormParamName.GRANT_TYPE, GrantType.CLIENT_CREDENTIALS)
			.formParam(FormParamName.CLIENT_ID, clientId)
			.formParam(FormParamName.EXT_TOKEN, extToken)
			.formParam(FormParamName.ADD_DATA, addData)
			.formParam(FormParamName.REFRESH_TOKEN, refreshToken)
			.formParam(FormParamName.USERNAME, username)
			.formParam(FormParamName.PASSWORD, password)
			.formParam(FormParamName.SCOPE, Scope.OFFLINE_ACCESS)
			.when()
			.post()
			.then()
			.statusCode(400)
			.contentType(MediaType.APPLICATION_JSON)
			.body(JsonPropertyName.ERRORS, notNullValue());
	}

	/**
	 *
	 */
	@Test
	void clientCredentialsAtm4() {
		given()
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.header(HeaderParamName.REQUEST_ID, "00000000-0000-0000-0000-10000000000f")
			.header(HeaderParamName.CHANNEL, Channel.ATM)
			.header(HeaderParamName.ACQUIRER_ID, acquirerId)
			.header(HeaderParamName.TERMINAL_ID, terminalId)
			.formParam(FormParamName.GRANT_TYPE, GrantType.CLIENT_CREDENTIALS)
			.formParam(FormParamName.CLIENT_ID, clientId)
			.formParam(FormParamName.EXT_TOKEN, extToken)
			.formParam(FormParamName.ADD_DATA, addData)
			.formParam(FormParamName.REFRESH_TOKEN, refreshToken)
			.formParam(FormParamName.USERNAME, username)
			.formParam(FormParamName.PASSWORD, password)
			.formParam(FormParamName.SCOPE, Scope.OFFLINE_ACCESS)
			.when()
			.post()
			.then()
			.statusCode(400)
			.contentType(MediaType.APPLICATION_JSON)
			.body(JsonPropertyName.ERRORS, notNullValue());
	}

	/**
	 *
	 */
	@Test
	void clientCredentialsAtm5() {
		given()
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.header(HeaderParamName.REQUEST_ID, "00000000-0000-0000-0000-100000000010")
			.header(HeaderParamName.CHANNEL, Channel.ATM)
			.header(HeaderParamName.ACQUIRER_ID, acquirerId)
			.header(HeaderParamName.TERMINAL_ID, terminalId)
			.formParam(FormParamName.GRANT_TYPE, GrantType.CLIENT_CREDENTIALS)
			.formParam(FormParamName.CLIENT_ID, clientId)
			.formParam(FormParamName.CLIENT_SECRET, clientSecret)
			.formParam(FormParamName.EXT_TOKEN, extToken)
			.formParam(FormParamName.ADD_DATA, addData)
			.formParam(FormParamName.REFRESH_TOKEN, refreshToken)
			.formParam(FormParamName.USERNAME, username)
			.formParam(FormParamName.PASSWORD, password)
			.formParam(FormParamName.SCOPE, Scope.OFFLINE_ACCESS)
			.when()
			.post()
			.then()
			.statusCode(400)
			.contentType(MediaType.APPLICATION_JSON)
			.body(JsonPropertyName.ERRORS, notNullValue());
	}

	/**
	 *
	 */
	@Test
	void clientCredentialsAtm6() {
		given()
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.header(HeaderParamName.REQUEST_ID, "00000000-0000-0000-0000-100000000011")
			.header(HeaderParamName.CHANNEL, Channel.ATM)
			.header(HeaderParamName.ACQUIRER_ID, acquirerId)
			.header(HeaderParamName.TERMINAL_ID, terminalId)
			.formParam(FormParamName.GRANT_TYPE, GrantType.CLIENT_CREDENTIALS)
			.formParam(FormParamName.CLIENT_ID, clientId)
			.formParam(FormParamName.CLIENT_SECRET, clientSecret)
			.formParam(FormParamName.ADD_DATA, addData)
			.formParam(FormParamName.REFRESH_TOKEN, refreshToken)
			.formParam(FormParamName.USERNAME, username)
			.formParam(FormParamName.PASSWORD, password)
			.formParam(FormParamName.SCOPE, Scope.OFFLINE_ACCESS)
			.when()
			.post()
			.then()
			.statusCode(400)
			.contentType(MediaType.APPLICATION_JSON)
			.body(JsonPropertyName.ERRORS, notNullValue());
	}

	/**
	 *
	 */
	@Test
	void clientCredentialsAtm7() {
		given()
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.header(HeaderParamName.REQUEST_ID, "00000000-0000-0000-0000-100000000012")
			.header(HeaderParamName.CHANNEL, Channel.ATM)
			.header(HeaderParamName.ACQUIRER_ID, acquirerId)
			.header(HeaderParamName.TERMINAL_ID, terminalId)
			.formParam(FormParamName.GRANT_TYPE, GrantType.CLIENT_CREDENTIALS)
			.formParam(FormParamName.CLIENT_ID, clientId)
			.formParam(FormParamName.CLIENT_SECRET, clientSecret)
			.formParam(FormParamName.REFRESH_TOKEN, refreshToken)
			.formParam(FormParamName.USERNAME, username)
			.formParam(FormParamName.PASSWORD, password)
			.formParam(FormParamName.SCOPE, Scope.OFFLINE_ACCESS)
			.when()
			.post()
			.then()
			.statusCode(400)
			.contentType(MediaType.APPLICATION_JSON)
			.body(JsonPropertyName.ERRORS, notNullValue());
	}

	/**
	 *
	 */
	@Test
	void clientCredentialsAtm8() {
		given()
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.header(HeaderParamName.REQUEST_ID, "00000000-0000-0000-0000-100000000013")
			.header(HeaderParamName.CHANNEL, Channel.ATM)
			.header(HeaderParamName.ACQUIRER_ID, acquirerId)
			.header(HeaderParamName.TERMINAL_ID, terminalId)
			.formParam(FormParamName.GRANT_TYPE, GrantType.CLIENT_CREDENTIALS)
			.formParam(FormParamName.CLIENT_ID, clientId)
			.formParam(FormParamName.CLIENT_SECRET, clientSecret)
			.formParam(FormParamName.USERNAME, username)
			.formParam(FormParamName.PASSWORD, password)
			.formParam(FormParamName.SCOPE, Scope.OFFLINE_ACCESS)
			.when()
			.post()
			.then()
			.statusCode(400)
			.contentType(MediaType.APPLICATION_JSON)
			.body(JsonPropertyName.ERRORS, notNullValue());
	}

	/**
	 *
	 */
	@Test
	void clientCredentialsAtm9() {
		given()
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.header(HeaderParamName.REQUEST_ID, "00000000-0000-0000-0000-100000000014")
			.header(HeaderParamName.CHANNEL, Channel.ATM)
			.header(HeaderParamName.ACQUIRER_ID, acquirerId)
			.header(HeaderParamName.TERMINAL_ID, terminalId)
			.formParam(FormParamName.GRANT_TYPE, GrantType.CLIENT_CREDENTIALS)
			.formParam(FormParamName.CLIENT_ID, clientId)
			.formParam(FormParamName.CLIENT_SECRET, clientSecret)
			.formParam(FormParamName.PASSWORD, password)
			.formParam(FormParamName.SCOPE, Scope.OFFLINE_ACCESS)
			.when()
			.post()
			.then()
			.statusCode(400)
			.contentType(MediaType.APPLICATION_JSON)
			.body(JsonPropertyName.ERRORS, notNullValue());
	}

	/**
	 *
	 */
	@Test
	void clientCredentialsPos1() {
		/*
		 * Test
		 */
		given()
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.header(HeaderParamName.REQUEST_ID, "00000000-0000-0000-0000-100000000015")
			.header(HeaderParamName.CHANNEL, Channel.POS)
			.formParam(FormParamName.GRANT_TYPE, GrantType.CLIENT_CREDENTIALS)
			.formParam(FormParamName.CLIENT_ID, clientId)
			.formParam(FormParamName.EXT_TOKEN, extToken)
			.formParam(FormParamName.ADD_DATA, addData)
			.formParam(FormParamName.REFRESH_TOKEN, refreshToken)
			.formParam(FormParamName.USERNAME, username)
			.formParam(FormParamName.PASSWORD, password)
			.formParam(FormParamName.SCOPE, Scope.OFFLINE_ACCESS)
			.when()
			.post()
			.then()
			.statusCode(400)
			.contentType(MediaType.APPLICATION_JSON)
			.body(JsonPropertyName.ERRORS, notNullValue());
	}

	/**
	 * passwordMustBeNull(getAccessToken)
	 */
	@Test
	void clientCredentialsPos10() {
		/*
		 * Test
		 */
		given()
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.header(HeaderParamName.REQUEST_ID, "00000000-0000-0000-0000-100000000016")
			.header(HeaderParamName.CHANNEL, Channel.POS)
			.header(HeaderParamName.ACQUIRER_ID, acquirerId)
			.header(HeaderParamName.MERCHANT_ID, merchantId)
			.header(HeaderParamName.TERMINAL_ID, terminalId)
			.formParam(FormParamName.GRANT_TYPE, GrantType.CLIENT_CREDENTIALS)
			.formParam(FormParamName.CLIENT_ID, clientId)
			.formParam(FormParamName.CLIENT_SECRET, clientSecret)
			.formParam(FormParamName.SCOPE, Scope.OFFLINE_ACCESS)
			.when()
			.post()
			.then()
			.statusCode(400)
			.contentType(MediaType.APPLICATION_JSON)
			.body(JsonPropertyName.ERRORS, notNullValue());
	}

	/**
	 * acquirerIdMustNotBeNull(getAccessToken)
	 */
	@Test
	void clientCredentialsPos2() {
		/*
		 * Test
		 */
		given()
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.header(HeaderParamName.REQUEST_ID, "00000000-0000-0000-0000-100000000018")
			.header(HeaderParamName.CHANNEL, Channel.POS)
			.header(HeaderParamName.ACQUIRER_ID, acquirerId)
			.formParam(FormParamName.GRANT_TYPE, GrantType.CLIENT_CREDENTIALS)
			.formParam(FormParamName.CLIENT_ID, clientId)
			.formParam(FormParamName.EXT_TOKEN, extToken)
			.formParam(FormParamName.ADD_DATA, addData)
			.formParam(FormParamName.REFRESH_TOKEN, refreshToken)
			.formParam(FormParamName.USERNAME, username)
			.formParam(FormParamName.PASSWORD, password)
			.formParam(FormParamName.SCOPE, Scope.OFFLINE_ACCESS)
			.when()
			.post()
			.then()
			.statusCode(400)
			.contentType(MediaType.APPLICATION_JSON)
			.body(JsonPropertyName.ERRORS, notNullValue());
	}

	/**
	 * merchantIdMustNotBeNull(getAccessToken)
	 */
	@Test
	void clientCredentialsPos3() {
		/*
		 * Test
		 */
		given()
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.header(HeaderParamName.REQUEST_ID, "00000000-0000-0000-0000-100000000019")
			.header(HeaderParamName.CHANNEL, Channel.POS)
			.header(HeaderParamName.ACQUIRER_ID, acquirerId)
			.header(HeaderParamName.MERCHANT_ID, merchantId)
			.formParam(FormParamName.GRANT_TYPE, GrantType.CLIENT_CREDENTIALS)
			.formParam(FormParamName.CLIENT_ID, clientId)
			.formParam(FormParamName.EXT_TOKEN, extToken)
			.formParam(FormParamName.ADD_DATA, addData)
			.formParam(FormParamName.REFRESH_TOKEN, refreshToken)
			.formParam(FormParamName.USERNAME, username)
			.formParam(FormParamName.PASSWORD, password)
			.formParam(FormParamName.SCOPE, Scope.OFFLINE_ACCESS)
			.when()
			.post()
			.then()
			.statusCode(400)
			.contentType(MediaType.APPLICATION_JSON)
			.body(JsonPropertyName.ERRORS, notNullValue());
	}

	/**
	 * terminalIdMustNotBeNull(getAccessToken)
	 */
	@Test
	void clientCredentialsPos4() {
		/*
		 * Test
		 */
		given()
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.header(HeaderParamName.REQUEST_ID, "00000000-0000-0000-0000-10000000001a")
			.header(HeaderParamName.CHANNEL, Channel.POS)
			.header(HeaderParamName.ACQUIRER_ID, acquirerId)
			.header(HeaderParamName.MERCHANT_ID, merchantId)
			.header(HeaderParamName.TERMINAL_ID, terminalId)
			.formParam(FormParamName.GRANT_TYPE, GrantType.CLIENT_CREDENTIALS)
			.formParam(FormParamName.CLIENT_ID, clientId)
			.formParam(FormParamName.EXT_TOKEN, extToken)
			.formParam(FormParamName.ADD_DATA, addData)
			.formParam(FormParamName.REFRESH_TOKEN, refreshToken)
			.formParam(FormParamName.USERNAME, username)
			.formParam(FormParamName.PASSWORD, password)
			.formParam(FormParamName.SCOPE, Scope.OFFLINE_ACCESS)
			.when()
			.post()
			.then()
			.statusCode(400)
			.contentType(MediaType.APPLICATION_JSON)
			.body(JsonPropertyName.ERRORS, notNullValue());
	}

	/**
	 * clientSecretMustNotBeNull(getAccessToken)
	 */
	@Test
	void clientCredentialsPos5() {
		/*
		 * Test
		 */
		given()
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.header(HeaderParamName.REQUEST_ID, "00000000-0000-0000-0000-10000000001b")
			.header(HeaderParamName.CHANNEL, Channel.POS)
			.header(HeaderParamName.ACQUIRER_ID, acquirerId)
			.header(HeaderParamName.MERCHANT_ID, merchantId)
			.header(HeaderParamName.TERMINAL_ID, terminalId)
			.formParam(FormParamName.GRANT_TYPE, GrantType.CLIENT_CREDENTIALS)
			.formParam(FormParamName.CLIENT_ID, clientId)
			.formParam(FormParamName.CLIENT_SECRET, clientSecret)
			.formParam(FormParamName.EXT_TOKEN, extToken)
			.formParam(FormParamName.ADD_DATA, addData)
			.formParam(FormParamName.REFRESH_TOKEN, refreshToken)
			.formParam(FormParamName.USERNAME, username)
			.formParam(FormParamName.PASSWORD, password)
			.formParam(FormParamName.SCOPE, Scope.OFFLINE_ACCESS)
			.when()
			.post()
			.then()
			.statusCode(400)
			.contentType(MediaType.APPLICATION_JSON)
			.body(JsonPropertyName.ERRORS, notNullValue());
	}

	/**
	 * extTokenMustBeNull(getAccessToken)
	 */
	@Test
	void clientCredentialsPos6() {
		/*
		 * Test
		 */
		given()
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.header(HeaderParamName.REQUEST_ID, "00000000-0000-0000-0000-10000000001c")
			.header(HeaderParamName.CHANNEL, Channel.POS)
			.header(HeaderParamName.ACQUIRER_ID, acquirerId)
			.header(HeaderParamName.MERCHANT_ID, merchantId)
			.header(HeaderParamName.TERMINAL_ID, terminalId)
			.formParam(FormParamName.GRANT_TYPE, GrantType.CLIENT_CREDENTIALS)
			.formParam(FormParamName.CLIENT_ID, clientId)
			.formParam(FormParamName.CLIENT_SECRET, clientSecret)
			.formParam(FormParamName.ADD_DATA, addData)
			.formParam(FormParamName.REFRESH_TOKEN, refreshToken)
			.formParam(FormParamName.USERNAME, username)
			.formParam(FormParamName.PASSWORD, password)
			.formParam(FormParamName.SCOPE, Scope.OFFLINE_ACCESS)
			.when()
			.post()
			.then()
			.statusCode(400)
			.contentType(MediaType.APPLICATION_JSON)
			.body(JsonPropertyName.ERRORS, notNullValue());
	}

	/**
	 * addDataMustBeNull(getAccessToken)
	 */
	@Test
	void clientCredentialsPos7() {
		/*
		 * Test
		 */
		given()
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.header(HeaderParamName.REQUEST_ID, "00000000-0000-0000-0000-10000000001d")
			.header(HeaderParamName.CHANNEL, Channel.POS)
			.header(HeaderParamName.ACQUIRER_ID, acquirerId)
			.header(HeaderParamName.MERCHANT_ID, merchantId)
			.header(HeaderParamName.TERMINAL_ID, terminalId)
			.formParam(FormParamName.GRANT_TYPE, GrantType.CLIENT_CREDENTIALS)
			.formParam(FormParamName.CLIENT_ID, clientId)
			.formParam(FormParamName.CLIENT_SECRET, clientSecret)
			.formParam(FormParamName.REFRESH_TOKEN, refreshToken)
			.formParam(FormParamName.USERNAME, username)
			.formParam(FormParamName.PASSWORD, password)
			.formParam(FormParamName.SCOPE, Scope.OFFLINE_ACCESS)
			.when()
			.post()
			.then()
			.statusCode(400)
			.contentType(MediaType.APPLICATION_JSON)
			.body(JsonPropertyName.ERRORS, notNullValue());
	}

	/**
	 * refreshTokenMustBeNull(getAccessToken)
	 */
	@Test
	void clientCredentialsPos8() {
		/*
		 * Test
		 */
		given()
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.header(HeaderParamName.REQUEST_ID, "00000000-0000-0000-0000-10000000001e")
			.header(HeaderParamName.CHANNEL, Channel.POS)
			.header(HeaderParamName.ACQUIRER_ID, acquirerId)
			.header(HeaderParamName.MERCHANT_ID, merchantId)
			.header(HeaderParamName.TERMINAL_ID, terminalId)
			.formParam(FormParamName.GRANT_TYPE, GrantType.CLIENT_CREDENTIALS)
			.formParam(FormParamName.CLIENT_ID, clientId)
			.formParam(FormParamName.CLIENT_SECRET, clientSecret)
			.formParam(FormParamName.USERNAME, username)
			.formParam(FormParamName.PASSWORD, password)
			.formParam(FormParamName.SCOPE, Scope.OFFLINE_ACCESS)
			.when()
			.post()
			.then()
			.statusCode(400)
			.contentType(MediaType.APPLICATION_JSON)
			.body(JsonPropertyName.ERRORS, notNullValue());
	}

	/**
	 * usernameMustBeNull(getAccessToken)
	 */
	@Test
	void clientCredentialsPos9() {
		/*
		 * Test
		 */
		given()
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.header(HeaderParamName.REQUEST_ID, "00000000-0000-0000-0000-10000000001f")
			.header(HeaderParamName.CHANNEL, Channel.POS)
			.header(HeaderParamName.ACQUIRER_ID, acquirerId)
			.header(HeaderParamName.MERCHANT_ID, merchantId)
			.header(HeaderParamName.TERMINAL_ID, terminalId)
			.formParam(FormParamName.GRANT_TYPE, GrantType.CLIENT_CREDENTIALS)
			.formParam(FormParamName.CLIENT_ID, clientId)
			.formParam(FormParamName.CLIENT_SECRET, clientSecret)
			.formParam(FormParamName.PASSWORD, password)
			.formParam(FormParamName.SCOPE, Scope.OFFLINE_ACCESS)
			.when()
			.post()
			.then()
			.statusCode(400)
			.contentType(MediaType.APPLICATION_JSON)
			.body(JsonPropertyName.ERRORS, notNullValue());
	}

	/**
	 *
	 */
	@Test
	void password1() {
		/*
		 * Test
		 */
		given()
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.header(HeaderParamName.REQUEST_ID, "00000000-0000-0000-0000-100000000020")
			.header(HeaderParamName.CHANNEL, Channel.POS)
			.formParam(FormParamName.GRANT_TYPE, GrantType.PASSWORD)
			.formParam(FormParamName.CLIENT_ID, clientId)
			.formParam(FormParamName.CLIENT_SECRET, clientSecret)
			.formParam(FormParamName.EXT_TOKEN, extToken)
			.formParam(FormParamName.ADD_DATA, addData)
			.formParam(FormParamName.REFRESH_TOKEN, refreshToken)
			.formParam(FormParamName.SCOPE, Scope.OFFLINE_ACCESS)
			.when()
			.post()
			.then()
			.statusCode(400)
			.contentType(MediaType.APPLICATION_JSON)
			.body(JsonPropertyName.ERRORS, notNullValue());
	}

	/**
	 *
	 */
	@Test
	void password2() {
		/*
		 * Test
		 */
		given()
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.header(HeaderParamName.REQUEST_ID, "00000000-0000-0000-0000-100000000021")
			.header(HeaderParamName.CHANNEL, Channel.POS)
			.header(HeaderParamName.ACQUIRER_ID, acquirerId)
			.formParam(FormParamName.GRANT_TYPE, GrantType.PASSWORD)
			.formParam(FormParamName.CLIENT_ID, clientId)
			.formParam(FormParamName.CLIENT_SECRET, clientSecret)
			.formParam(FormParamName.EXT_TOKEN, extToken)
			.formParam(FormParamName.ADD_DATA, addData)
			.formParam(FormParamName.REFRESH_TOKEN, refreshToken)
			.formParam(FormParamName.SCOPE, Scope.OFFLINE_ACCESS)
			.when()
			.post()
			.then()
			.statusCode(400)
			.contentType(MediaType.APPLICATION_JSON)
			.body(JsonPropertyName.ERRORS, notNullValue());
	}

	/**
	 *
	 */
	@Test
	void password3() {
		/*
		 * Test
		 */
		given()
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.header(HeaderParamName.REQUEST_ID, "00000000-0000-0000-0000-100000000022")
			.header(HeaderParamName.CHANNEL, Channel.POS)
			.header(HeaderParamName.ACQUIRER_ID, acquirerId)
			.header(HeaderParamName.MERCHANT_ID, merchantId)
			.formParam(FormParamName.GRANT_TYPE, GrantType.PASSWORD)
			.formParam(FormParamName.CLIENT_ID, clientId)
			.formParam(FormParamName.CLIENT_SECRET, clientSecret)
			.formParam(FormParamName.EXT_TOKEN, extToken)
			.formParam(FormParamName.ADD_DATA, addData)
			.formParam(FormParamName.REFRESH_TOKEN, refreshToken)
			.formParam(FormParamName.SCOPE, Scope.OFFLINE_ACCESS)
			.when()
			.post()
			.then()
			.statusCode(400)
			.contentType(MediaType.APPLICATION_JSON)
			.body(JsonPropertyName.ERRORS, notNullValue());
	}

	/**
	 *
	 */
	@Test
	void password4() {
		/*
		 * Test
		 */
		given()
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.header(HeaderParamName.REQUEST_ID, "00000000-0000-0000-0000-100000000023")
			.header(HeaderParamName.CHANNEL, Channel.POS)
			.header(HeaderParamName.ACQUIRER_ID, acquirerId)
			.header(HeaderParamName.MERCHANT_ID, merchantId)
			.header(HeaderParamName.TERMINAL_ID, terminalId)
			.formParam(FormParamName.GRANT_TYPE, GrantType.PASSWORD)
			.formParam(FormParamName.CLIENT_ID, clientId)
			.formParam(FormParamName.CLIENT_SECRET, clientSecret)
			.formParam(FormParamName.EXT_TOKEN, extToken)
			.formParam(FormParamName.ADD_DATA, addData)
			.formParam(FormParamName.REFRESH_TOKEN, refreshToken)
			.formParam(FormParamName.SCOPE, Scope.OFFLINE_ACCESS)
			.when()
			.post()
			.then()
			.statusCode(400)
			.contentType(MediaType.APPLICATION_JSON)
			.body(JsonPropertyName.ERRORS, notNullValue());
	}

	/**
	 *
	 */
	@Test
	void password5() {
		/*
		 * Test
		 */
		given()
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.header(HeaderParamName.REQUEST_ID, "00000000-0000-0000-0000-100000000024")
			.header(HeaderParamName.CHANNEL, Channel.POS)
			.header(HeaderParamName.ACQUIRER_ID, acquirerId)
			.header(HeaderParamName.MERCHANT_ID, merchantId)
			.header(HeaderParamName.TERMINAL_ID, terminalId)
			.formParam(FormParamName.GRANT_TYPE, GrantType.PASSWORD)
			.formParam(FormParamName.CLIENT_ID, clientId)
			.formParam(FormParamName.EXT_TOKEN, extToken)
			.formParam(FormParamName.ADD_DATA, addData)
			.formParam(FormParamName.REFRESH_TOKEN, refreshToken)
			.formParam(FormParamName.SCOPE, Scope.OFFLINE_ACCESS)
			.when()
			.post()
			.then()
			.statusCode(400)
			.contentType(MediaType.APPLICATION_JSON)
			.body(JsonPropertyName.ERRORS, notNullValue());
	}

	/**
	 *
	 */
	@Test
	void password6() {
		/*
		 * Test
		 */
		given()
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.header(HeaderParamName.REQUEST_ID, "00000000-0000-0000-0000-100000000025")
			.header(HeaderParamName.CHANNEL, Channel.POS)
			.header(HeaderParamName.ACQUIRER_ID, acquirerId)
			.header(HeaderParamName.MERCHANT_ID, merchantId)
			.header(HeaderParamName.TERMINAL_ID, terminalId)
			.formParam(FormParamName.GRANT_TYPE, GrantType.PASSWORD)
			.formParam(FormParamName.CLIENT_ID, clientId)
			.formParam(FormParamName.ADD_DATA, addData)
			.formParam(FormParamName.REFRESH_TOKEN, refreshToken)
			.formParam(FormParamName.SCOPE, Scope.OFFLINE_ACCESS)
			.when()
			.post()
			.then()
			.statusCode(400)
			.contentType(MediaType.APPLICATION_JSON)
			.body(JsonPropertyName.ERRORS, notNullValue());
	}

	/**
	 *
	 */
	@Test
	void password7() {
		/*
		 * Test
		 */
		given()
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.header(HeaderParamName.REQUEST_ID, "00000000-0000-0000-0000-100000000026")
			.header(HeaderParamName.CHANNEL, Channel.POS)
			.header(HeaderParamName.ACQUIRER_ID, acquirerId)
			.header(HeaderParamName.MERCHANT_ID, merchantId)
			.header(HeaderParamName.TERMINAL_ID, terminalId)
			.formParam(FormParamName.GRANT_TYPE, GrantType.PASSWORD)
			.formParam(FormParamName.CLIENT_ID, clientId)
			.formParam(FormParamName.REFRESH_TOKEN, refreshToken)
			.formParam(FormParamName.SCOPE, Scope.OFFLINE_ACCESS)
			.when()
			.post()
			.then()
			.statusCode(400)
			.contentType(MediaType.APPLICATION_JSON)
			.body(JsonPropertyName.ERRORS, notNullValue());
	}

	/**
	 *
	 */
	@Test
	void password8() {
		/*
		 * Test
		 */
		given()
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.header(HeaderParamName.REQUEST_ID, "00000000-0000-0000-0000-100000000027")
			.header(HeaderParamName.CHANNEL, Channel.POS)
			.header(HeaderParamName.ACQUIRER_ID, acquirerId)
			.header(HeaderParamName.MERCHANT_ID, merchantId)
			.header(HeaderParamName.TERMINAL_ID, terminalId)
			.formParam(FormParamName.GRANT_TYPE, GrantType.PASSWORD)
			.formParam(FormParamName.CLIENT_ID, clientId)
			.formParam(FormParamName.SCOPE, Scope.OFFLINE_ACCESS)
			.when()
			.post()
			.then()
			.statusCode(400)
			.contentType(MediaType.APPLICATION_JSON)
			.body(JsonPropertyName.ERRORS, notNullValue());
	}

	/**
	 *
	 */
	@Test
	void password9() {
		/*
		 * Test
		 */
		given()
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.header(HeaderParamName.REQUEST_ID, "00000000-0000-0000-0000-100000000028")
			.header(HeaderParamName.CHANNEL, Channel.POS)
			.header(HeaderParamName.ACQUIRER_ID, acquirerId)
			.header(HeaderParamName.MERCHANT_ID, merchantId)
			.header(HeaderParamName.TERMINAL_ID, terminalId)
			.formParam(FormParamName.GRANT_TYPE, GrantType.PASSWORD)
			.formParam(FormParamName.CLIENT_ID, clientId)
			.formParam(FormParamName.USERNAME, username)
			.formParam(FormParamName.SCOPE, Scope.OFFLINE_ACCESS)
			.when()
			.post()
			.then()
			.statusCode(400)
			.contentType(MediaType.APPLICATION_JSON)
			.body(JsonPropertyName.ERRORS, notNullValue());
	}

	/**
	 *
	 */
	@Test
	void poynt1() {
		/*
		 * Test
		 */
		given()
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.header(HeaderParamName.REQUEST_ID, "00000000-0000-0000-0000-100000000029")
			.header(HeaderParamName.CHANNEL, Channel.POS)
			.formParam(FormParamName.GRANT_TYPE, GrantType.POYNT_TOKEN)
			.formParam(FormParamName.CLIENT_ID, clientId)
			.formParam(FormParamName.CLIENT_SECRET, clientSecret)
			.formParam(FormParamName.REFRESH_TOKEN, refreshToken)
			.formParam(FormParamName.USERNAME, username)
			.formParam(FormParamName.PASSWORD, password)
			.formParam(FormParamName.SCOPE, Scope.OFFLINE_ACCESS)
			.when()
			.post()
			.then()
			.statusCode(400)
			.contentType(MediaType.APPLICATION_JSON)
			.body(JsonPropertyName.ERRORS, notNullValue());
	}

	/**
	 *
	 */
	@Test
	void poynt2() {
		/*
		 * Test
		 */
		given()
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.header(HeaderParamName.REQUEST_ID, "00000000-0000-0000-0000-10000000002a")
			.header(HeaderParamName.CHANNEL, Channel.POS)
			.header(HeaderParamName.ACQUIRER_ID, acquirerId)
			.formParam(FormParamName.GRANT_TYPE, GrantType.POYNT_TOKEN)
			.formParam(FormParamName.CLIENT_ID, clientId)
			.formParam(FormParamName.CLIENT_SECRET, clientSecret)
			.formParam(FormParamName.REFRESH_TOKEN, refreshToken)
			.formParam(FormParamName.USERNAME, username)
			.formParam(FormParamName.PASSWORD, password)
			.formParam(FormParamName.SCOPE, Scope.OFFLINE_ACCESS)
			.when()
			.post()
			.then()
			.statusCode(400)
			.contentType(MediaType.APPLICATION_JSON)
			.body(JsonPropertyName.ERRORS, notNullValue());
	}

	/**
	 *
	 */
	@Test
	void poynt3() {
		/*
		 * Test
		 */
		given()
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.header(HeaderParamName.REQUEST_ID, "00000000-0000-0000-0000-10000000002b")
			.header(HeaderParamName.CHANNEL, Channel.POS)
			.header(HeaderParamName.ACQUIRER_ID, acquirerId)
			.header(HeaderParamName.MERCHANT_ID, merchantId)
			.formParam(FormParamName.GRANT_TYPE, GrantType.POYNT_TOKEN)
			.formParam(FormParamName.CLIENT_ID, clientId)
			.formParam(FormParamName.CLIENT_SECRET, clientSecret)
			.formParam(FormParamName.REFRESH_TOKEN, refreshToken)
			.formParam(FormParamName.USERNAME, username)
			.formParam(FormParamName.PASSWORD, password)
			.formParam(FormParamName.SCOPE, Scope.OFFLINE_ACCESS)
			.when()
			.post()
			.then()
			.statusCode(400)
			.contentType(MediaType.APPLICATION_JSON)
			.body(JsonPropertyName.ERRORS, notNullValue());
	}

	/**
	 *
	 */
	@Test
	void poynt4() {
		/*
		 * Test
		 */
		given()
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.header(HeaderParamName.REQUEST_ID, "00000000-0000-0000-0000-10000000002c")
			.header(HeaderParamName.CHANNEL, Channel.POS)
			.header(HeaderParamName.ACQUIRER_ID, acquirerId)
			.header(HeaderParamName.MERCHANT_ID, merchantId)
			.header(HeaderParamName.TERMINAL_ID, terminalId)
			.formParam(FormParamName.GRANT_TYPE, GrantType.POYNT_TOKEN)
			.formParam(FormParamName.CLIENT_ID, clientId)
			.formParam(FormParamName.CLIENT_SECRET, clientSecret)
			.formParam(FormParamName.REFRESH_TOKEN, refreshToken)
			.formParam(FormParamName.USERNAME, username)
			.formParam(FormParamName.PASSWORD, password)
			.formParam(FormParamName.SCOPE, Scope.OFFLINE_ACCESS)
			.when()
			.post()
			.then()
			.statusCode(400)
			.contentType(MediaType.APPLICATION_JSON)
			.body(JsonPropertyName.ERRORS, notNullValue());
	}

	/**
	 *
	 */
	@Test
	void poynt5() {
		/*
		 * Test
		 */
		given()
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.header(HeaderParamName.REQUEST_ID, "00000000-0000-0000-0000-10000000002d")
			.header(HeaderParamName.CHANNEL, Channel.POS)
			.header(HeaderParamName.ACQUIRER_ID, acquirerId)
			.header(HeaderParamName.MERCHANT_ID, merchantId)
			.header(HeaderParamName.TERMINAL_ID, terminalId)
			.formParam(FormParamName.GRANT_TYPE, GrantType.POYNT_TOKEN)
			.formParam(FormParamName.CLIENT_ID, clientId)
			.formParam(FormParamName.REFRESH_TOKEN, refreshToken)
			.formParam(FormParamName.USERNAME, username)
			.formParam(FormParamName.PASSWORD, password)
			.formParam(FormParamName.SCOPE, Scope.OFFLINE_ACCESS)
			.when()
			.post()
			.then()
			.statusCode(400)
			.contentType(MediaType.APPLICATION_JSON)
			.body(JsonPropertyName.ERRORS, notNullValue());
	}

	/**
	 *
	 */
	@Test
	void poynt6() {
		/*
		 * Test
		 */
		given()
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.header(HeaderParamName.REQUEST_ID, "00000000-0000-0000-0000-10000000002e")
			.header(HeaderParamName.CHANNEL, Channel.POS)
			.header(HeaderParamName.ACQUIRER_ID, acquirerId)
			.header(HeaderParamName.MERCHANT_ID, merchantId)
			.header(HeaderParamName.TERMINAL_ID, terminalId)
			.formParam(FormParamName.GRANT_TYPE, GrantType.POYNT_TOKEN)
			.formParam(FormParamName.CLIENT_ID, clientId)
			.formParam(FormParamName.EXT_TOKEN, extToken)
			.formParam(FormParamName.REFRESH_TOKEN, refreshToken)
			.formParam(FormParamName.USERNAME, username)
			.formParam(FormParamName.PASSWORD, password)
			.formParam(FormParamName.SCOPE, Scope.OFFLINE_ACCESS)
			.when()
			.post()
			.then()
			.statusCode(400)
			.contentType(MediaType.APPLICATION_JSON)
			.body(JsonPropertyName.ERRORS, notNullValue());
	}

	/**
	 *
	 */
	@Test
	void poynt7() {
		/*
		 * Test
		 */
		given()
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.header(HeaderParamName.REQUEST_ID, "00000000-0000-0000-0000-10000000002f")
			.header(HeaderParamName.CHANNEL, Channel.POS)
			.header(HeaderParamName.ACQUIRER_ID, acquirerId)
			.header(HeaderParamName.MERCHANT_ID, merchantId)
			.header(HeaderParamName.TERMINAL_ID, terminalId)
			.formParam(FormParamName.GRANT_TYPE, GrantType.POYNT_TOKEN)
			.formParam(FormParamName.CLIENT_ID, clientId)
			.formParam(FormParamName.EXT_TOKEN, extToken)
			.formParam(FormParamName.ADD_DATA, addData)
			.formParam(FormParamName.REFRESH_TOKEN, refreshToken)
			.formParam(FormParamName.USERNAME, username)
			.formParam(FormParamName.PASSWORD, password)
			.formParam(FormParamName.SCOPE, Scope.OFFLINE_ACCESS)
			.when()
			.post()
			.then()
			.statusCode(400)
			.contentType(MediaType.APPLICATION_JSON)
			.body(JsonPropertyName.ERRORS, notNullValue());
	}

	/**
	 *
	 */
	@Test
	void poynt8() {
		/*
		 * Test
		 */
		given()
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.header(HeaderParamName.REQUEST_ID, "00000000-0000-0000-0000-100000000030")
			.header(HeaderParamName.CHANNEL, Channel.POS)
			.header(HeaderParamName.ACQUIRER_ID, acquirerId)
			.header(HeaderParamName.MERCHANT_ID, merchantId)
			.header(HeaderParamName.TERMINAL_ID, terminalId)
			.formParam(FormParamName.GRANT_TYPE, GrantType.POYNT_TOKEN)
			.formParam(FormParamName.CLIENT_ID, clientId)
			.formParam(FormParamName.EXT_TOKEN, extToken)
			.formParam(FormParamName.ADD_DATA, addData)
			.formParam(FormParamName.USERNAME, username)
			.formParam(FormParamName.PASSWORD, password)
			.formParam(FormParamName.SCOPE, Scope.OFFLINE_ACCESS)
			.when()
			.post()
			.then()
			.statusCode(400)
			.contentType(MediaType.APPLICATION_JSON)
			.body(JsonPropertyName.ERRORS, notNullValue());
	}

	/**
	 *
	 */
	@Test
	void poynt9() {
		/*
		 * Test
		 */
		given()
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.header(HeaderParamName.REQUEST_ID, "00000000-0000-0000-0000-100000000031")
			.header(HeaderParamName.CHANNEL, Channel.POS)
			.header(HeaderParamName.ACQUIRER_ID, acquirerId)
			.header(HeaderParamName.MERCHANT_ID, merchantId)
			.header(HeaderParamName.TERMINAL_ID, terminalId)
			.formParam(FormParamName.GRANT_TYPE, GrantType.POYNT_TOKEN)
			.formParam(FormParamName.CLIENT_ID, clientId)
			.formParam(FormParamName.EXT_TOKEN, extToken)
			.formParam(FormParamName.ADD_DATA, addData)
			.formParam(FormParamName.PASSWORD, password)
			.formParam(FormParamName.SCOPE, Scope.OFFLINE_ACCESS)
			.when()
			.post()
			.then()
			.statusCode(400)
			.contentType(MediaType.APPLICATION_JSON)
			.body(JsonPropertyName.ERRORS, notNullValue());
	}

	/**
	 *
	 */
	@Test
	void refreshToken1() {
		/*
		 * Test
		 */
		given()
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.header(HeaderParamName.REQUEST_ID, "00000000-0000-0000-0000-100000000032")
			.header(HeaderParamName.CHANNEL, Channel.POS)
			.formParam(FormParamName.GRANT_TYPE, GrantType.REFRESH_TOKEN)
			.formParam(FormParamName.CLIENT_ID, clientId)
			.formParam(FormParamName.CLIENT_SECRET, clientSecret)
			.formParam(FormParamName.EXT_TOKEN, extToken)
			.formParam(FormParamName.ADD_DATA, addData)
			.formParam(FormParamName.USERNAME, username)
			.formParam(FormParamName.PASSWORD, password)
			.formParam(FormParamName.SCOPE, Scope.OFFLINE_ACCESS)
			.when()
			.post()
			.then()
			.statusCode(400)
			.contentType(MediaType.APPLICATION_JSON)
			.body(JsonPropertyName.ERRORS, notNullValue());
	}

	/**
	 *
	 */
	@Test
	void refreshToken2() {
		/*
		 * Test
		 */
		given()
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.header(HeaderParamName.REQUEST_ID, "00000000-0000-0000-0000-100000000033")
			.header(HeaderParamName.CHANNEL, Channel.POS)
			.header(HeaderParamName.ACQUIRER_ID, acquirerId)
			.formParam(FormParamName.GRANT_TYPE, GrantType.REFRESH_TOKEN)
			.formParam(FormParamName.CLIENT_ID, clientId)
			.formParam(FormParamName.CLIENT_SECRET, clientSecret)
			.formParam(FormParamName.EXT_TOKEN, extToken)
			.formParam(FormParamName.ADD_DATA, addData)
			.formParam(FormParamName.USERNAME, username)
			.formParam(FormParamName.PASSWORD, password)
			.formParam(FormParamName.SCOPE, Scope.OFFLINE_ACCESS)
			.when()
			.post()
			.then()
			.statusCode(400)
			.contentType(MediaType.APPLICATION_JSON)
			.body(JsonPropertyName.ERRORS, notNullValue());
	}

	/**
	 *
	 */
	@Test
	void refreshToken3() {
		/*
		 * Test
		 */
		given()
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.header(HeaderParamName.REQUEST_ID, "00000000-0000-0000-0000-100000000034")
			.header(HeaderParamName.CHANNEL, Channel.POS)
			.header(HeaderParamName.ACQUIRER_ID, acquirerId)
			.header(HeaderParamName.MERCHANT_ID, merchantId)
			.formParam(FormParamName.GRANT_TYPE, GrantType.REFRESH_TOKEN)
			.formParam(FormParamName.CLIENT_ID, clientId)
			.formParam(FormParamName.CLIENT_SECRET, clientSecret)
			.formParam(FormParamName.EXT_TOKEN, extToken)
			.formParam(FormParamName.ADD_DATA, addData)
			.formParam(FormParamName.USERNAME, username)
			.formParam(FormParamName.PASSWORD, password)
			.formParam(FormParamName.SCOPE, Scope.OFFLINE_ACCESS)
			.when()
			.post()
			.then()
			.statusCode(400)
			.contentType(MediaType.APPLICATION_JSON)
			.body(JsonPropertyName.ERRORS, notNullValue());
	}

	/**
	 *
	 */
	@Test
	void refreshToken4() {
		/*
		 * Test
		 */
		given()
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.header(HeaderParamName.REQUEST_ID, "00000000-0000-0000-0000-100000000035")
			.header(HeaderParamName.CHANNEL, Channel.POS)
			.header(HeaderParamName.ACQUIRER_ID, acquirerId)
			.header(HeaderParamName.MERCHANT_ID, merchantId)
			.header(HeaderParamName.TERMINAL_ID, terminalId)
			.formParam(FormParamName.GRANT_TYPE, GrantType.REFRESH_TOKEN)
			.formParam(FormParamName.CLIENT_ID, clientId)
			.formParam(FormParamName.CLIENT_SECRET, clientSecret)
			.formParam(FormParamName.EXT_TOKEN, extToken)
			.formParam(FormParamName.ADD_DATA, addData)
			.formParam(FormParamName.USERNAME, username)
			.formParam(FormParamName.PASSWORD, password)
			.formParam(FormParamName.SCOPE, Scope.OFFLINE_ACCESS)
			.when()
			.post()
			.then()
			.statusCode(400)
			.contentType(MediaType.APPLICATION_JSON)
			.body(JsonPropertyName.ERRORS, notNullValue());
	}

	/**
	 *
	 */
	@Test
	void refreshToken5() {
		/*
		 * Test
		 */
		given()
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.header(HeaderParamName.REQUEST_ID, "00000000-0000-0000-0000-100000000036")
			.header(HeaderParamName.CHANNEL, Channel.POS)
			.header(HeaderParamName.ACQUIRER_ID, acquirerId)
			.header(HeaderParamName.MERCHANT_ID, merchantId)
			.header(HeaderParamName.TERMINAL_ID, terminalId)
			.formParam(FormParamName.GRANT_TYPE, GrantType.REFRESH_TOKEN)
			.formParam(FormParamName.CLIENT_ID, clientId)
			.formParam(FormParamName.EXT_TOKEN, extToken)
			.formParam(FormParamName.ADD_DATA, addData)
			.formParam(FormParamName.USERNAME, username)
			.formParam(FormParamName.PASSWORD, password)
			.formParam(FormParamName.SCOPE, Scope.OFFLINE_ACCESS)
			.when()
			.post()
			.then()
			.statusCode(400)
			.contentType(MediaType.APPLICATION_JSON)
			.body(JsonPropertyName.ERRORS, notNullValue());
	}

	/**
	 *
	 */
	@Test
	void refreshToken6() {
		/*
		 * Test
		 */
		given()
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.header(HeaderParamName.REQUEST_ID, "00000000-0000-0000-0000-100000000037")
			.header(HeaderParamName.CHANNEL, Channel.POS)
			.header(HeaderParamName.ACQUIRER_ID, acquirerId)
			.header(HeaderParamName.MERCHANT_ID, merchantId)
			.header(HeaderParamName.TERMINAL_ID, terminalId)
			.formParam(FormParamName.GRANT_TYPE, GrantType.REFRESH_TOKEN)
			.formParam(FormParamName.CLIENT_ID, clientId)
			.formParam(FormParamName.ADD_DATA, addData)
			.formParam(FormParamName.USERNAME, username)
			.formParam(FormParamName.PASSWORD, password)
			.formParam(FormParamName.SCOPE, Scope.OFFLINE_ACCESS)
			.when()
			.post()
			.then()
			.statusCode(400)
			.contentType(MediaType.APPLICATION_JSON)
			.body(JsonPropertyName.ERRORS, notNullValue());
	}

	/**
	 *
	 */
	@Test
	void refreshToken7() {
		/*
		 * Test
		 */
		given()
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.header(HeaderParamName.REQUEST_ID, "00000000-0000-0000-0000-100000000038")
			.header(HeaderParamName.CHANNEL, Channel.POS)
			.header(HeaderParamName.ACQUIRER_ID, acquirerId)
			.header(HeaderParamName.MERCHANT_ID, merchantId)
			.header(HeaderParamName.TERMINAL_ID, terminalId)
			.formParam(FormParamName.GRANT_TYPE, GrantType.REFRESH_TOKEN)
			.formParam(FormParamName.CLIENT_ID, clientId)
			.formParam(FormParamName.USERNAME, username)
			.formParam(FormParamName.PASSWORD, password)
			.formParam(FormParamName.SCOPE, Scope.OFFLINE_ACCESS)
			.when()
			.post()
			.then()
			.statusCode(400)
			.contentType(MediaType.APPLICATION_JSON)
			.body(JsonPropertyName.ERRORS, notNullValue());
	}

	/**
	 *
	 */
	@Test
	void refreshToken8() {
		/*
		 * Test
		 */
		given()
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.header(HeaderParamName.REQUEST_ID, "00000000-0000-0000-0000-100000000039")
			.header(HeaderParamName.CHANNEL, Channel.POS)
			.header(HeaderParamName.ACQUIRER_ID, acquirerId)
			.header(HeaderParamName.MERCHANT_ID, merchantId)
			.header(HeaderParamName.TERMINAL_ID, terminalId)
			.formParam(FormParamName.GRANT_TYPE, GrantType.REFRESH_TOKEN)
			.formParam(FormParamName.CLIENT_ID, clientId)
			.formParam(FormParamName.REFRESH_TOKEN, refreshToken)
			.formParam(FormParamName.USERNAME, username)
			.formParam(FormParamName.PASSWORD, password)
			.formParam(FormParamName.SCOPE, Scope.OFFLINE_ACCESS)
			.when()
			.post()
			.then()
			.statusCode(400)
			.contentType(MediaType.APPLICATION_JSON)
			.body(JsonPropertyName.ERRORS, notNullValue());
	}

	/**
	 *
	 */
	@Test
	void refreshToken9() {
		/*
		 * Test
		 */
		given()
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.header(HeaderParamName.REQUEST_ID, "00000000-0000-0000-0000-10000000003a")
			.header(HeaderParamName.CHANNEL, Channel.POS)
			.header(HeaderParamName.ACQUIRER_ID, acquirerId)
			.header(HeaderParamName.MERCHANT_ID, merchantId)
			.header(HeaderParamName.TERMINAL_ID, terminalId)
			.formParam(FormParamName.GRANT_TYPE, GrantType.REFRESH_TOKEN)
			.formParam(FormParamName.CLIENT_ID, clientId)
			.formParam(FormParamName.REFRESH_TOKEN, refreshToken)
			.formParam(FormParamName.PASSWORD, password)
			.formParam(FormParamName.SCOPE, Scope.OFFLINE_ACCESS)
			.when()
			.post()
			.then()
			.statusCode(400)
			.contentType(MediaType.APPLICATION_JSON)
			.body(JsonPropertyName.ERRORS, notNullValue());
	}

	/**
	 *
	 */
	@Test
	void withoutValidator() {
		/*
		 * Test
		 */
		given()
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.header(HeaderParamName.REQUEST_ID, "00000000-0000-0000-0000-10000000003b")
			.formParam(FormParamName.GRANT_TYPE, GrantType.PASSWORD)
			.formParam(FormParamName.CLIENT_ID, clientId)
			.when()
			.post()
			.then()
			.statusCode(400)
			.contentType(MediaType.APPLICATION_JSON)
			.body(JsonPropertyName.ERRORS, notNullValue());
	}
}