/*
 * TokenResourceTest.java
 *
 * 13 giu 2024
 */
package it.pagopa.swclient.mil.auth.resource;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.TestInstance;

import io.quarkus.test.InjectMock;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.mutiny.Uni;
import it.pagopa.swclient.mil.auth.bean.FormParamName;
import it.pagopa.swclient.mil.auth.bean.GetAccessTokenRequest;
import it.pagopa.swclient.mil.auth.bean.GetAccessTokenResponse;
import it.pagopa.swclient.mil.auth.bean.GrantType;
import it.pagopa.swclient.mil.auth.bean.HeaderParamName;
import it.pagopa.swclient.mil.auth.bean.JsonPropertyName;
import it.pagopa.swclient.mil.auth.bean.TokenType;
import it.pagopa.swclient.mil.auth.qualifier.ClientCredentials;
import it.pagopa.swclient.mil.auth.service.TokenByClientSecretService;
import it.pagopa.swclient.mil.auth.util.UniGenerator;
import it.pagopa.swclient.mil.bean.Channel;
import jakarta.ws.rs.core.MediaType;

/**
 *
 * @author Antonio Tarricone
 */
@QuarkusTest
@TestHTTPEndpoint(TokenResource.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TokenResourceTest {
	/*
	 * 
	 */
	private static final String REQUEST_ID = "00000000-0000-0000-0000-000000000000";
	private static final String ACQUIRER_ID = "4585625";
	private static final String MERCHANT_ID = "28405fHfk73x88D";
	private static final String TERMINAL_ID = "12345678";
	private static final String CHANNEL = Channel.POS;
	private static final String CLIENT_ID = "3965df56-ca9a-49e5-97e8-061433d4a25b";
	private static final String CLIENT_SECRET = "5ceef788-4115-43a7-a704-b1bcc9a47c86";

	/*
	 * 
	 */
	@InjectMock
	@ClientCredentials
	private TokenByClientSecretService tokenByClientSecretService;

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
	void given_requestToGetAccessToken_when_theEndPointIsInvoked_then_getAccessToken() {
		/*
		 * Setup
		 */
		GetAccessTokenRequest request = new GetAccessTokenRequest()
			.setAcquirerId(ACQUIRER_ID)
			.setChannel(CHANNEL)
			.setClientId(CLIENT_ID)
			.setClientSecret(CLIENT_SECRET)
			.setGrantType(GrantType.CLIENT_CREDENTIALS)
			.setMerchantId(MERCHANT_ID)
			.setRequestId(REQUEST_ID)
			.setTerminalId(TERMINAL_ID);

		when(tokenByClientSecretService.process(request))
			.thenReturn(UniGenerator.item(new GetAccessTokenResponse()
				.setAccessToken("access_token")
				.setExpiresIn(900)
				.setTokenType(TokenType.BEARER)));

		/*
		 * Test
		 */
		given()
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.header(HeaderParamName.REQUEST_ID, REQUEST_ID)
			.header(HeaderParamName.ACQUIRER_ID, ACQUIRER_ID)
			.header(HeaderParamName.CHANNEL, CHANNEL)
			.header(HeaderParamName.MERCHANT_ID, MERCHANT_ID)
			.header(HeaderParamName.TERMINAL_ID, TERMINAL_ID)
			.formParam(FormParamName.CLIENT_ID, CLIENT_ID)
			.formParam(FormParamName.GRANT_TYPE, GrantType.CLIENT_CREDENTIALS)
			.formParam(FormParamName.CLIENT_SECRET, CLIENT_SECRET)
			.when()
			.post()
			.then()
			.log()
			.everything()
			.statusCode(200)
			.contentType(MediaType.APPLICATION_JSON)
			.body(JsonPropertyName.ACCESS_TOKEN, notNullValue())
			.body(JsonPropertyName.TOKEN_TYPE, equalTo(TokenType.BEARER))
			.body(JsonPropertyName.EXPIRES_IN, notNullValue(Long.class))
			.body(JsonPropertyName.REFRESH_TOKEN, nullValue());
	}

	/**
	 * 
	 */
	@Test
	void given_requestToGetAccessToken_when_tokenServiceUnhandledExceptionOccurs_then_getFailure() {
		/*
		 * Setup
		 */
		GetAccessTokenRequest request = new GetAccessTokenRequest()
			.setAcquirerId(ACQUIRER_ID)
			.setChannel(CHANNEL)
			.setClientId(CLIENT_ID)
			.setClientSecret(CLIENT_SECRET)
			.setGrantType(GrantType.CLIENT_CREDENTIALS)
			.setMerchantId(MERCHANT_ID)
			.setRequestId(REQUEST_ID)
			.setTerminalId(TERMINAL_ID);

		when(tokenByClientSecretService.process(request))
			.thenReturn(Uni.createFrom().failure(new Exception("synthetic_exception")));

		/*
		 * Test
		 */
		given()
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.header(HeaderParamName.REQUEST_ID, REQUEST_ID)
			.header(HeaderParamName.ACQUIRER_ID, ACQUIRER_ID)
			.header(HeaderParamName.CHANNEL, CHANNEL)
			.header(HeaderParamName.MERCHANT_ID, MERCHANT_ID)
			.header(HeaderParamName.TERMINAL_ID, TERMINAL_ID)
			.formParam(FormParamName.CLIENT_ID, CLIENT_ID)
			.formParam(FormParamName.GRANT_TYPE, GrantType.CLIENT_CREDENTIALS)
			.formParam(FormParamName.CLIENT_SECRET, CLIENT_SECRET)
			.when()
			.post()
			.then()
			.log()
			.everything()
			.statusCode(500)
			.contentType(MediaType.APPLICATION_JSON)
			.body(JsonPropertyName.ERRORS, notNullValue());
	}

	/**
	 * 
	 */
	@Test
	void given_requestToGetAccessToken_when_tokenServiceHandledExceptionOccurs_then_getFailure() {
		/*
		 * Setup
		 */
		GetAccessTokenRequest request = new GetAccessTokenRequest()
			.setAcquirerId(ACQUIRER_ID)
			.setChannel(CHANNEL)
			.setClientId(CLIENT_ID)
			.setClientSecret(CLIENT_SECRET)
			.setGrantType(GrantType.CLIENT_CREDENTIALS)
			.setMerchantId(MERCHANT_ID)
			.setRequestId(REQUEST_ID)
			.setTerminalId(TERMINAL_ID);

		when(tokenByClientSecretService.process(request))
			.thenReturn(UniGenerator.exception("code", "string"));

		/*
		 * Test
		 */
		given()
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.header(HeaderParamName.REQUEST_ID, REQUEST_ID)
			.header(HeaderParamName.ACQUIRER_ID, ACQUIRER_ID)
			.header(HeaderParamName.CHANNEL, CHANNEL)
			.header(HeaderParamName.MERCHANT_ID, MERCHANT_ID)
			.header(HeaderParamName.TERMINAL_ID, TERMINAL_ID)
			.formParam(FormParamName.CLIENT_ID, CLIENT_ID)
			.formParam(FormParamName.GRANT_TYPE, GrantType.CLIENT_CREDENTIALS)
			.formParam(FormParamName.CLIENT_SECRET, CLIENT_SECRET)
			.when()
			.post()
			.then()
			.log()
			.everything()
			.statusCode(401)
			.contentType(MediaType.APPLICATION_JSON)
			.body(JsonPropertyName.ERRORS, notNullValue());
	}

	/**
	 * 
	 */
	@Test
	void given_requestToGetAccessToken_when_tokenServiceHandledErrorOccurs_then_getFailure() {
		/*
		 * Setup
		 */
		GetAccessTokenRequest request = new GetAccessTokenRequest()
			.setAcquirerId(ACQUIRER_ID)
			.setChannel(CHANNEL)
			.setClientId(CLIENT_ID)
			.setClientSecret(CLIENT_SECRET)
			.setGrantType(GrantType.CLIENT_CREDENTIALS)
			.setMerchantId(MERCHANT_ID)
			.setRequestId(REQUEST_ID)
			.setTerminalId(TERMINAL_ID);

		when(tokenByClientSecretService.process(request))
			.thenReturn(UniGenerator.error("code", "string"));

		/*
		 * Test
		 */
		given()
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.header(HeaderParamName.REQUEST_ID, REQUEST_ID)
			.header(HeaderParamName.ACQUIRER_ID, ACQUIRER_ID)
			.header(HeaderParamName.CHANNEL, CHANNEL)
			.header(HeaderParamName.MERCHANT_ID, MERCHANT_ID)
			.header(HeaderParamName.TERMINAL_ID, TERMINAL_ID)
			.formParam(FormParamName.CLIENT_ID, CLIENT_ID)
			.formParam(FormParamName.GRANT_TYPE, GrantType.CLIENT_CREDENTIALS)
			.formParam(FormParamName.CLIENT_SECRET, CLIENT_SECRET)
			.when()
			.post()
			.then()
			.log()
			.everything()
			.statusCode(500)
			.contentType(MediaType.APPLICATION_JSON)
			.body(JsonPropertyName.ERRORS, notNullValue());
	}
}