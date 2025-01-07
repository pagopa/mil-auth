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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.mockito.Mockito;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.util.Base64URL;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import io.quarkus.test.InjectMock;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.mutiny.Uni;
import it.pagopa.swclient.mil.auth.bean.AuthCookieParamName;
import it.pagopa.swclient.mil.auth.bean.AuthFormParamName;
import it.pagopa.swclient.mil.auth.bean.AuthJsonPropertyName;
import it.pagopa.swclient.mil.auth.bean.ClaimName;
import it.pagopa.swclient.mil.auth.bean.GetAccessTokenRequest;
import it.pagopa.swclient.mil.auth.bean.GetAccessTokenResponse;
import it.pagopa.swclient.mil.auth.bean.GrantType;
import it.pagopa.swclient.mil.auth.bean.Scope;
import it.pagopa.swclient.mil.auth.bean.TokenType;
import it.pagopa.swclient.mil.auth.qualifier.ClientCredentials;
import it.pagopa.swclient.mil.auth.qualifier.Password;
import it.pagopa.swclient.mil.auth.qualifier.RefreshToken;
import it.pagopa.swclient.mil.auth.service.RefreshTokensService;
import it.pagopa.swclient.mil.auth.service.TokenByClientSecretService;
import it.pagopa.swclient.mil.auth.service.TokenByPasswordService;
import it.pagopa.swclient.mil.auth.util.UniGenerator;
import it.pagopa.swclient.mil.bean.Channel;
import it.pagopa.swclient.mil.bean.HeaderParamName;
import jakarta.ws.rs.core.MediaType;

/**
 *
 * @author Antonio Tarricone
 */
@QuarkusTest
@TestHTTPEndpoint(TokenResource.class)
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
	private static final String USERNAME = "username";
	private static final String PASSWORD = "password";
	private static final String ACCESS_TOKEN = "eyJraWQiOiIzOGE1ZDA4ZGM4NzU0MGVhYjc3ZGViNGQ5ZWFiMjM4MC8zNzExY2U3NWFiYmI0MWM5YmZhOTEwMzM0Y2FiMDMzZSIsImFsZyI6IlJTMjU2In0.eyJzdWIiOiJOb2RvIiwiYXVkIjoibWlsLnBhZ29wYS5pdCIsImNsaWVudElkIjoiOTJmYWYzMTktNDIxOS00NTVmLTg0MWItYmI2OTI2ODQ2NzJhIiwiaXNzIjoiaHR0cHM6Ly9taWwtZC1hcGltLmF6dXJlLWFwaS5uZXQvbWlsLWF1dGgiLCJncm91cHMiOlsiTm9kbyJdLCJleHAiOjE3MzU5MDQ3MTIsImlhdCI6MTczNTkwMzgxMn0.m0bA-s-BQbjNtd3eXbux7tXyn0ITz-wPPPbThLlNQMVxr-erzLIGT0t3jTDoxRPuXe49tlio6ivMWugIKH74CQxQKe9fgmoJuiZ8h9cIQVyg1sFfdS0_EHOp3ubI40IEsvHa7zvoYU3QWB9ByZxupyNPRgfJXKmJwaHU-9sM4Wm381P54gu_CH2QEG7iyHZbCe1t9B3ILcfRozudw3v8_iE8hYZQsUU66gcXrW2Fqh3F_8y4F8FGkXR1bmlY18REpjqZlywTaY4nAts-nA9XQIK4dFriq9c6dVzDiX3RHjQLvCyW8ZeVY0pE5E8WgaEX7z4b-kgefAPasil9YkNoTw";
	private static final String REFRESH_TOKEN = "eyJraWQiOiIzOGE1ZDA4ZGM4NzU0MGVhYjc3ZGViNGQ5ZWFiMjM4MC8zNzExY2U3NWFiYmI0MWM5YmZhOTEwMzM0Y2FiMDMzZSIsImFsZyI6IlJTMjU2In0.eyJzdWIiOiI0NTg1NjI1LzI4NDA1ZkhmazczeDg4RC8wMTIzNDU2NyIsImF1ZCI6Im1pbC5wYWdvcGEuaXQiLCJjbGllbnRJZCI6IjUyNTRmMDg3LTEyMTQtNDVjZC05NGFlLWZkYTUzYzgzNTE5NyIsIm1lcmNoYW50SWQiOiIyODQwNWZIZms3M3g4OEQiLCJzY29wZSI6Im9mZmxpbmVfYWNjZXNzIiwiY2hhbm5lbCI6IlBPUyIsImlzcyI6Imh0dHBzOi8vbWlsLWQtYXBpbS5henVyZS1hcGkubmV0L21pbC1hdXRoIiwidGVybWluYWxJZCI6IjAxMjM0NTY3IiwiZXhwIjoxNzM1OTEwMTcxLCJhY3F1aXJlcklkIjoiNDU4NTYyNSIsImlhdCI6MTczNTkwNjU3MX0.Ztu8SlQCjXErum9xRsqUMOd0ucGvfeKhDHAjR3lzo9KV0KiRdy8RckcR-Zg6Yt1Pu4jIl59xlMIE0KZFoHBTFqIzJp0h6HiSvvus8fArJ6Fu5YfMmtOoq9yEkw1GfBWHiYXt-y4LMw9gfus5DA2fEttY6kQVK7mznDUL3eGzTM2OSQlS3rrrnJUuxVR_8RsS1bYVpsUmu36W0Uf0Jd49GvnuqCKakJpr4rzcyvt358NVWrNH4Qqtjg4dCAyXPkM_MHez4XtaMXRh6O8UkOym9DI9n7zkmkkmx-ZccHDkAMmsGJKwviaIMVyrQJ2S3RXzAbcXZS13nb3djskN-3XC5Q";

	/*
	 * 
	 */
	@InjectMock
	@ClientCredentials
	private TokenByClientSecretService tokenByClientSecretService;

	/*
	 * 
	 */
	@InjectMock
	@Password
	private TokenByPasswordService tokenByPasswordService;

	/*
	 * 
	 */
	@InjectMock
	@RefreshToken
	private RefreshTokensService refreshTokensService;

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
		Mockito.reset(tokenByClientSecretService, tokenByPasswordService, refreshTokensService);
	}

	/**
	 * 
	 * @throws ParseException
	 */
	@Test
	void given_requestToGetAccessToken_when_theEndPointIsInvoked_then_getAccessToken() throws ParseException {
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
			.setTerminalId(TERMINAL_ID);

		when(tokenByClientSecretService.process(request))
			.thenReturn(UniGenerator.item(new GetAccessTokenResponse()
				.setAccessToken(SignedJWT.parse(ACCESS_TOKEN))
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
			.formParam(AuthFormParamName.CLIENT_ID, CLIENT_ID)
			.formParam(AuthFormParamName.GRANT_TYPE, GrantType.CLIENT_CREDENTIALS)
			.formParam(AuthFormParamName.CLIENT_SECRET, CLIENT_SECRET)
			.when()
			.post()
			.then()
			.log()
			.everything()
			.statusCode(200)
			.contentType(MediaType.APPLICATION_JSON)
			.body(AuthJsonPropertyName.ACCESS_TOKEN, notNullValue())
			.body(AuthJsonPropertyName.TOKEN_TYPE, equalTo(TokenType.BEARER))
			.body(AuthJsonPropertyName.EXPIRES_IN, notNullValue(Long.class))
			.body(AuthJsonPropertyName.REFRESH_TOKEN, nullValue());
	}

	/**
	 * 
	 * @throws ParseException
	 */
	@Test
	void given_requestToGetAccessAndRefreshToken_when_theEndPointIsInvoked_then_getTokens() throws ParseException {
		/*
		 * Setup
		 */
		GetAccessTokenRequest request = new GetAccessTokenRequest()
			.setClientId(CLIENT_ID)
			.setGrantType(GrantType.PASSWORD)
			.setUsername(USERNAME)
			.setPassword(PASSWORD)
			.setScope(Scope.OFFLINE_ACCESS);

		when(tokenByPasswordService.process(request))
			.thenReturn(
				UniGenerator.item(
					new GetAccessTokenResponse()
						.setAccessToken(SignedJWT.parse(ACCESS_TOKEN))
						.setRefreshToken(SignedJWT.parse(REFRESH_TOKEN))
						.setExpiresIn(900)
						.setTokenType(TokenType.BEARER)));

		/*
		 * Test
		 */
		given()
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.header(HeaderParamName.REQUEST_ID, REQUEST_ID)
			.formParam(AuthFormParamName.CLIENT_ID, CLIENT_ID)
			.formParam(AuthFormParamName.GRANT_TYPE, GrantType.PASSWORD)
			.formParam(AuthFormParamName.USERNAME, USERNAME)
			.formParam(AuthFormParamName.PASSWORD, PASSWORD)
			.formParam(AuthFormParamName.SCOPE, Scope.OFFLINE_ACCESS)
			.when()
			.post()
			.then()
			.log()
			.everything()
			.statusCode(200)
			.contentType(MediaType.APPLICATION_JSON)
			.cookie(AuthCookieParamName.REFRESH_COOKIE, REFRESH_TOKEN)
			.body(AuthJsonPropertyName.ACCESS_TOKEN, equalTo(ACCESS_TOKEN))
			.body(AuthJsonPropertyName.TOKEN_TYPE, equalTo(TokenType.BEARER))
			.body(AuthJsonPropertyName.EXPIRES_IN, notNullValue(Long.class))
			.body(AuthJsonPropertyName.REFRESH_TOKEN, equalTo(REFRESH_TOKEN));
	}

	/**
	 * 
	 * @throws ParseException
	 */
	@Test
	void given_requestToRefreshTokensWithCookie_when_theEndPointIsInvoked_then_getTokens() throws ParseException {
		/*
		 * Setup
		 */
		Instant now = Instant.now();

		JWSHeader header = new JWSHeader(JWSAlgorithm.RS256, null, null, null, null, null, null, null, null, null, "key_id", true, null, null);

		JWTClaimsSet payload = new JWTClaimsSet.Builder()
			.subject("subject")
			.issueTime(new Date(now.toEpochMilli()))
			.expirationTime(new Date(now.plus(15, ChronoUnit.MINUTES).toEpochMilli()))
			.claim(ClaimName.CLIENT_ID, CLIENT_ID)
			.claim(ClaimName.SCOPE, Scope.OFFLINE_ACCESS)
			.build();

		SignedJWT refreshToken = new SignedJWT(header.toBase64URL(), payload.toPayload().toBase64URL(), Base64URL.from("AA"));

		when(refreshTokensService.process(any(GetAccessTokenRequest.class))) // equals method of GetAccessTokenRequest doesn't work properly due to SignedJWT fields
			.thenReturn(
				UniGenerator.item(
					new GetAccessTokenResponse()
						.setAccessToken(SignedJWT.parse(ACCESS_TOKEN))
						.setRefreshToken(SignedJWT.parse(REFRESH_TOKEN))
						.setExpiresIn(900)
						.setTokenType(TokenType.BEARER)));

		/*
		 * Test
		 */
		given()
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.header(HeaderParamName.REQUEST_ID, REQUEST_ID)
			.formParam(AuthFormParamName.CLIENT_ID, CLIENT_ID)
			.formParam(AuthFormParamName.GRANT_TYPE, GrantType.REFRESH_TOKEN)
			.cookie(AuthCookieParamName.REFRESH_COOKIE, refreshToken.serialize())
			.when()
			.post()
			.then()
			.log()
			.everything()
			.statusCode(200)
			.contentType(MediaType.APPLICATION_JSON)
			.cookie(AuthCookieParamName.REFRESH_COOKIE, REFRESH_TOKEN)
			.body(AuthJsonPropertyName.ACCESS_TOKEN, equalTo(ACCESS_TOKEN))
			.body(AuthJsonPropertyName.TOKEN_TYPE, equalTo(TokenType.BEARER))
			.body(AuthJsonPropertyName.EXPIRES_IN, notNullValue(Long.class))
			.body(AuthJsonPropertyName.REFRESH_TOKEN, equalTo(REFRESH_TOKEN));
	}

	/**
	 * 
	 * @throws ParseException
	 */
	@Test
	void given_requestToRefreshTokens_when_theEndPointIsInvoked_then_getTokens() throws ParseException {
		/*
		 * Setup
		 */
		Instant now = Instant.now();

		JWSHeader header = new JWSHeader(JWSAlgorithm.RS256, null, null, null, null, null, null, null, null, null, "key_id", true, null, null);

		JWTClaimsSet payload = new JWTClaimsSet.Builder()
			.subject("subject")
			.issueTime(new Date(now.toEpochMilli()))
			.expirationTime(new Date(now.plus(15, ChronoUnit.MINUTES).toEpochMilli()))
			.claim(ClaimName.CLIENT_ID, CLIENT_ID)
			.claim(ClaimName.SCOPE, Scope.OFFLINE_ACCESS)
			.build();

		SignedJWT refreshToken = new SignedJWT(header.toBase64URL(), payload.toPayload().toBase64URL(), Base64URL.from("AA"));

		when(refreshTokensService.process(any(GetAccessTokenRequest.class))) // equals method of GetAccessTokenRequest doesn't work properly due to SignedJWT fields
			.thenReturn(
				UniGenerator.item(
					new GetAccessTokenResponse()
						.setAccessToken(SignedJWT.parse(ACCESS_TOKEN))
						.setRefreshToken(SignedJWT.parse(REFRESH_TOKEN))
						.setExpiresIn(900)
						.setTokenType(TokenType.BEARER)));

		/*
		 * Test
		 */
		given()
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.header(HeaderParamName.REQUEST_ID, REQUEST_ID)
			.formParam(AuthFormParamName.CLIENT_ID, CLIENT_ID)
			.formParam(AuthFormParamName.GRANT_TYPE, GrantType.REFRESH_TOKEN)
			.formParam(AuthFormParamName.REFRESH_TOKEN, refreshToken.serialize())
			.when()
			.post()
			.then()
			.log()
			.everything()
			.statusCode(200)
			.contentType(MediaType.APPLICATION_JSON)
			.cookie(AuthCookieParamName.REFRESH_COOKIE, REFRESH_TOKEN)
			.body(AuthJsonPropertyName.ACCESS_TOKEN, equalTo(ACCESS_TOKEN))
			.body(AuthJsonPropertyName.TOKEN_TYPE, equalTo(TokenType.BEARER))
			.body(AuthJsonPropertyName.EXPIRES_IN, notNullValue(Long.class))
			.body(AuthJsonPropertyName.REFRESH_TOKEN, equalTo(REFRESH_TOKEN));
	}

	/**
	 * 
	 */
	@Test
	void given_requestToRefreshTokens_when_refreshTokenIsBad_then_getFailure() {
		/*
		 * Test
		 */
		given()
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.header(HeaderParamName.REQUEST_ID, REQUEST_ID)
			.formParam(AuthFormParamName.CLIENT_ID, CLIENT_ID)
			.formParam(AuthFormParamName.GRANT_TYPE, GrantType.REFRESH_TOKEN)
			.formParam(AuthFormParamName.REFRESH_TOKEN, "@.@.@")
			.when()
			.post()
			.then()
			.log()
			.everything()
			.statusCode(400)
			.contentType(MediaType.APPLICATION_JSON);
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
			.formParam(AuthFormParamName.CLIENT_ID, CLIENT_ID)
			.formParam(AuthFormParamName.GRANT_TYPE, GrantType.CLIENT_CREDENTIALS)
			.formParam(AuthFormParamName.CLIENT_SECRET, CLIENT_SECRET)
			.when()
			.post()
			.then()
			.log()
			.everything()
			.statusCode(500)
			.contentType(MediaType.APPLICATION_JSON)
			.body(AuthJsonPropertyName.ERRORS, notNullValue());
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
			.formParam(AuthFormParamName.CLIENT_ID, CLIENT_ID)
			.formParam(AuthFormParamName.GRANT_TYPE, GrantType.CLIENT_CREDENTIALS)
			.formParam(AuthFormParamName.CLIENT_SECRET, CLIENT_SECRET)
			.when()
			.post()
			.then()
			.log()
			.everything()
			.statusCode(401)
			.contentType(MediaType.APPLICATION_JSON)
			.body(AuthJsonPropertyName.ERRORS, notNullValue());
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
			.formParam(AuthFormParamName.CLIENT_ID, CLIENT_ID)
			.formParam(AuthFormParamName.GRANT_TYPE, GrantType.CLIENT_CREDENTIALS)
			.formParam(AuthFormParamName.CLIENT_SECRET, CLIENT_SECRET)
			.when()
			.post()
			.then()
			.log()
			.everything()
			.statusCode(500)
			.contentType(MediaType.APPLICATION_JSON)
			.body(AuthJsonPropertyName.ERRORS, notNullValue());
	}
}