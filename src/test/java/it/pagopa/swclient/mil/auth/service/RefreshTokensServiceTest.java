/*
 * RefreshTokensServiceTest.java
 *
 * 12 giu 2024
 */
package it.pagopa.swclient.mil.auth.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.util.Base64URL;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.mutiny.helpers.test.UniAssertSubscriber;
import it.pagopa.swclient.mil.auth.AuthErrorCode;
import it.pagopa.swclient.mil.auth.bean.ClaimName;
import it.pagopa.swclient.mil.auth.bean.GetAccessTokenRequest;
import it.pagopa.swclient.mil.auth.bean.GrantType;
import it.pagopa.swclient.mil.auth.bean.Scope;
import it.pagopa.swclient.mil.auth.dao.ClientEntity;
import it.pagopa.swclient.mil.auth.dao.SetOfRolesEntity;
import it.pagopa.swclient.mil.auth.qualifier.RefreshToken;
import it.pagopa.swclient.mil.auth.util.AuthError;
import it.pagopa.swclient.mil.auth.util.AuthException;
import it.pagopa.swclient.mil.auth.util.UniGenerator;
import jakarta.inject.Inject;

/**
 * 
 * @author Antonio Tarricone
 */
@QuarkusTest
class RefreshTokensServiceTest {
	/*
	 * 
	 */
	@InjectMock
	ClientVerifier clientVerifier;

	/*
	 * 
	 */
	@InjectMock
	RolesFinder roleFinder;

	/*
	 * 
	 */
	@InjectMock
	TokenSigner tokenSigner;

	/*
	 * 
	 */
	@Inject
	@RefreshToken
	RefreshTokensService refreshTokensService;

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
	 * @throws ParseException
	 */
	@Test
	void given_refreshToken_when_allGoesOk_then_getTokens() throws ParseException {
		/*
		 * Setup
		 */
		Instant now = Instant.now();

		JWSHeader header = new JWSHeader(JWSAlgorithm.RS256, null, null, null, null, null, null, null, null, null, "key_id", true, null, null);

		JWTClaimsSet payload = new JWTClaimsSet.Builder()
			.subject("subject")
			.issueTime(new Date(now.toEpochMilli()))
			.expirationTime(new Date(now.plus(15, ChronoUnit.MINUTES).toEpochMilli()))
			.claim(ClaimName.ACQUIRER_ID, "acquirer_id")
			.claim(ClaimName.CHANNEL, "channel")
			.claim(ClaimName.MERCHANT_ID, "merchant_id")
			.claim(ClaimName.CLIENT_ID, "client_id")
			.claim(ClaimName.TERMINAL_ID, "teminal_id")
			.claim(ClaimName.SCOPE, Scope.OFFLINE_ACCESS)
			.build();

		SignedJWT refreshToken = new SignedJWT(header.toBase64URL(), payload.toPayload().toBase64URL(), Base64URL.from("AA"));

		when(tokenSigner.verify(any(SignedJWT.class)))
			.thenReturn(UniGenerator.item(null));

		when(clientVerifier.verify("client_id", "channel", null))
			.thenReturn(UniGenerator.item(new ClientEntity()));

		when(roleFinder.findRoles("acquirer_id", "channel", "client_id", "merchant_id", "terminal_id"))
			.thenReturn(UniGenerator.item(new SetOfRolesEntity()
				.setRoles(List.of("role"))));

		SignedJWT signedJwt = SignedJWT.parse("eyJraWQiOiJrZXlfbmFtZS9rZXlfdmVyc2lvbiIsImFsZyI6IlJTMjU2In0.eyJzdWIiOiJjbGllbnRfaWQiLCJjbGllbnRJZCI6ImNsaWVudF9pZCIsImNoYW5uZWwiOiJjaGFubmVsIiwiaXNzIjoiaHR0cHM6Ly9taWwtYXV0aCIsImdyb3VwcyI6InJvbGUiLCJ0ZXJtaW5hbElkIjoidGVybWluYWxfaWQiLCJhdWQiOiJodHRwczovL21pbCIsIm1lcmNoYW50SWQiOiJtZXJjaGFudF9pZCIsInNjb3BlIjoic2NvcGUiLCJmaXNjYWxDb2RlIjoiZW5jX2Zpc2NhbF9jb2RlIiwiZXhwIjoxNzE3NjUyLCJhY3F1aXJlcklkIjoiYWNxdWlyZXJfaWQiLCJpYXQiOjE3MTc1OTJ9.AA");

		when(tokenSigner.sign(any(JWTClaimsSet.class)))
			.thenReturn(UniGenerator.item(signedJwt));

		/*
		 * Test
		 */
		GetAccessTokenRequest request = new GetAccessTokenRequest()
			.setAcquirerId("acquirer_id")
			.setChannel("channel")
			.setClientId("client_id")
			.setRefreshToken(refreshToken)
			.setGrantType(GrantType.REFRESH_TOKEN)
			.setMerchantId("merchant_id")
			.setTerminalId("terminal_id");

		refreshTokensService.process(request)
			.subscribe()
			.with(
				response -> {
					assertEquals(
						signedJwt,
						response.getAccessToken());
				},
				f -> fail(f));
	}

	/**
	 * 
	 * @throws ParseException
	 */
	@Test
	void given_refreshCookie_when_allGoesOk_then_getTokens() throws ParseException {
		/*
		 * Setup
		 */
		Instant now = Instant.now();

		JWSHeader header = new JWSHeader(JWSAlgorithm.RS256, null, null, null, null, null, null, null, null, null, "key_id", true, null, null);

		JWTClaimsSet payload = new JWTClaimsSet.Builder()
			.subject("subject")
			.issueTime(new Date(now.toEpochMilli()))
			.expirationTime(new Date(now.plus(15, ChronoUnit.MINUTES).toEpochMilli()))
			.claim(ClaimName.ACQUIRER_ID, "acquirer_id")
			.claim(ClaimName.CHANNEL, "channel")
			.claim(ClaimName.MERCHANT_ID, "merchant_id")
			.claim(ClaimName.CLIENT_ID, "client_id")
			.claim(ClaimName.TERMINAL_ID, "teminal_id")
			.claim(ClaimName.SCOPE, Scope.OFFLINE_ACCESS)
			.build();

		SignedJWT refreshToken = new SignedJWT(header.toBase64URL(), payload.toPayload().toBase64URL(), Base64URL.from("AA"));

		when(tokenSigner.verify(any(SignedJWT.class)))
			.thenReturn(UniGenerator.item(null));

		when(clientVerifier.verify("client_id", "channel", null))
			.thenReturn(UniGenerator.item(new ClientEntity()));

		when(roleFinder.findRoles("acquirer_id", "channel", "client_id", "merchant_id", "terminal_id"))
			.thenReturn(UniGenerator.item(new SetOfRolesEntity()
				.setRoles(List.of("role"))));

		SignedJWT signedJwt = SignedJWT.parse("eyJraWQiOiJrZXlfbmFtZS9rZXlfdmVyc2lvbiIsImFsZyI6IlJTMjU2In0.eyJzdWIiOiJjbGllbnRfaWQiLCJjbGllbnRJZCI6ImNsaWVudF9pZCIsImNoYW5uZWwiOiJjaGFubmVsIiwiaXNzIjoiaHR0cHM6Ly9taWwtYXV0aCIsImdyb3VwcyI6InJvbGUiLCJ0ZXJtaW5hbElkIjoidGVybWluYWxfaWQiLCJhdWQiOiJodHRwczovL21pbCIsIm1lcmNoYW50SWQiOiJtZXJjaGFudF9pZCIsInNjb3BlIjoic2NvcGUiLCJmaXNjYWxDb2RlIjoiZW5jX2Zpc2NhbF9jb2RlIiwiZXhwIjoxNzE3NjUyLCJhY3F1aXJlcklkIjoiYWNxdWlyZXJfaWQiLCJpYXQiOjE3MTc1OTJ9.AA");

		when(tokenSigner.sign(any(JWTClaimsSet.class)))
			.thenReturn(UniGenerator.item(signedJwt));

		/*
		 * Test
		 */
		GetAccessTokenRequest request = new GetAccessTokenRequest()
			.setAcquirerId("acquirer_id")
			.setChannel("channel")
			.setClientId("client_id")
			// .setRefreshToken(refreshToken)
			.setGrantType(GrantType.REFRESH_TOKEN)
			.setMerchantId("merchant_id")
			.setTerminalId("terminal_id")
			.setRefreshCookie(refreshToken);

		refreshTokensService.process(request)
			.subscribe()
			.with(
				response -> {
					assertEquals(
						signedJwt,
						response.getAccessToken());
				},
				f -> fail(f));
	}

	/**
	 * 
	 * @throws ParseException
	 */
	@Test
	void given_refreshCookieAndRefreshToken_when_allGoesOk_then_getTokens() throws ParseException {
		/*
		 * Setup
		 */
		Instant now = Instant.now();

		JWSHeader header = new JWSHeader(JWSAlgorithm.RS256, null, null, null, null, null, null, null, null, null, "key_id", true, null, null);

		JWTClaimsSet payload = new JWTClaimsSet.Builder()
			.subject("subject")
			.issueTime(new Date(now.toEpochMilli()))
			.expirationTime(new Date(now.plus(15, ChronoUnit.MINUTES).toEpochMilli()))
			.claim(ClaimName.ACQUIRER_ID, "acquirer_id")
			.claim(ClaimName.CHANNEL, "channel")
			.claim(ClaimName.MERCHANT_ID, "merchant_id")
			.claim(ClaimName.CLIENT_ID, "client_id")
			.claim(ClaimName.TERMINAL_ID, "teminal_id")
			.claim(ClaimName.SCOPE, Scope.OFFLINE_ACCESS)
			.build();

		SignedJWT refreshToken = new SignedJWT(header.toBase64URL(), payload.toPayload().toBase64URL(), Base64URL.from("AA"));

		when(tokenSigner.verify(any(SignedJWT.class)))
			.thenReturn(UniGenerator.item(null));

		when(clientVerifier.verify("client_id", "channel", null))
			.thenReturn(UniGenerator.item(new ClientEntity()));

		when(roleFinder.findRoles("acquirer_id", "channel", "client_id", "merchant_id", "terminal_id"))
			.thenReturn(UniGenerator.item(new SetOfRolesEntity()
				.setRoles(List.of("role"))));

		SignedJWT signedJwt = SignedJWT.parse("eyJraWQiOiJrZXlfbmFtZS9rZXlfdmVyc2lvbiIsImFsZyI6IlJTMjU2In0.eyJzdWIiOiJjbGllbnRfaWQiLCJjbGllbnRJZCI6ImNsaWVudF9pZCIsImNoYW5uZWwiOiJjaGFubmVsIiwiaXNzIjoiaHR0cHM6Ly9taWwtYXV0aCIsImdyb3VwcyI6InJvbGUiLCJ0ZXJtaW5hbElkIjoidGVybWluYWxfaWQiLCJhdWQiOiJodHRwczovL21pbCIsIm1lcmNoYW50SWQiOiJtZXJjaGFudF9pZCIsInNjb3BlIjoic2NvcGUiLCJmaXNjYWxDb2RlIjoiZW5jX2Zpc2NhbF9jb2RlIiwiZXhwIjoxNzE3NjUyLCJhY3F1aXJlcklkIjoiYWNxdWlyZXJfaWQiLCJpYXQiOjE3MTc1OTJ9.AA");

		when(tokenSigner.sign(any(JWTClaimsSet.class)))
			.thenReturn(UniGenerator.item(signedJwt));

		/*
		 * Test
		 */
		GetAccessTokenRequest request = new GetAccessTokenRequest()
			.setAcquirerId("acquirer_id")
			.setChannel("channel")
			.setClientId("client_id")
			.setRefreshToken(refreshToken)
			.setGrantType(GrantType.REFRESH_TOKEN)
			.setMerchantId("merchant_id")
			.setTerminalId("terminal_id")
			.setRefreshCookie(refreshToken);

		refreshTokensService.process(request)
			.subscribe()
			.with(
				response -> {
					assertEquals(
						signedJwt,
						response.getAccessToken());
				},
				f -> fail(f));
	}

	/**
	 * 
	 * @throws ParseException
	 */
	@Test
	void given_badRefreshToken_when_tokensRefreshIsRequestes_then_getFailure() throws ParseException {
		/*
		 * Setup
		 */
		JWSHeader header = new JWSHeader(JWSAlgorithm.RS256, null, null, null, null, null, null, null, null, null, "key_id", true, null, null);

		SignedJWT refreshToken = new SignedJWT(header.toBase64URL(), new Base64URL("dGVzdA=="), Base64URL.from("AA"));

		when(tokenSigner.verify(any(SignedJWT.class)))
			.thenReturn(UniGenerator.item(null));

		when(clientVerifier.verify("client_id", "channel", null))
			.thenReturn(UniGenerator.item(new ClientEntity()));

		when(roleFinder.findRoles("acquirer_id", "channel", "client_id", "merchant_id", "terminal_id"))
			.thenReturn(UniGenerator.item(new SetOfRolesEntity()
				.setRoles(List.of("role"))));

		SignedJWT signedJwt = SignedJWT.parse("eyJraWQiOiJrZXlfbmFtZS9rZXlfdmVyc2lvbiIsImFsZyI6IlJTMjU2In0.eyJzdWIiOiJjbGllbnRfaWQiLCJjbGllbnRJZCI6ImNsaWVudF9pZCIsImNoYW5uZWwiOiJjaGFubmVsIiwiaXNzIjoiaHR0cHM6Ly9taWwtYXV0aCIsImdyb3VwcyI6InJvbGUiLCJ0ZXJtaW5hbElkIjoidGVybWluYWxfaWQiLCJhdWQiOiJodHRwczovL21pbCIsIm1lcmNoYW50SWQiOiJtZXJjaGFudF9pZCIsInNjb3BlIjoic2NvcGUiLCJmaXNjYWxDb2RlIjoiZW5jX2Zpc2NhbF9jb2RlIiwiZXhwIjoxNzE3NjUyLCJhY3F1aXJlcklkIjoiYWNxdWlyZXJfaWQiLCJpYXQiOjE3MTc1OTJ9.AA");

		when(tokenSigner.sign(any(JWTClaimsSet.class)))
			.thenReturn(UniGenerator.item(signedJwt));

		/*
		 * Test
		 */
		GetAccessTokenRequest request = new GetAccessTokenRequest()
			.setAcquirerId("acquirer_id")
			.setChannel("channel")
			.setClientId("client_id")
			.setRefreshToken(refreshToken)
			.setGrantType(GrantType.REFRESH_TOKEN)
			.setMerchantId("merchant_id")
			.setTerminalId("terminal_id")
			.setRefreshCookie(refreshToken);

		refreshTokensService.process(request)
			.subscribe()
			.withSubscriber(UniAssertSubscriber.create())
			.assertFailedWith(AuthError.class);
	}

	/**
	 * 
	 */
	@Test
	void given_refreshCookieAndRefreshTokenBothNull_when_tokesRefreshIsRequested_then_getFailure() {
		/*
		 * Test
		 */
		GetAccessTokenRequest request = new GetAccessTokenRequest()
			.setAcquirerId("acquirer_id")
			.setChannel("channel")
			.setClientId("client_id")
			.setGrantType(GrantType.REFRESH_TOKEN)
			.setMerchantId("merchant_id")
			.setTerminalId("terminal_id");

		assertThrows(NullPointerException.class, () -> refreshTokensService.process(request));
	}

	/**
	 * 
	 * @throws ParseException
	 */
	@Test
	void given_refreshToken_when_scopeIsWrong_then_getFailure() throws ParseException {
		/*
		 * Setup
		 */
		Instant now = Instant.now();

		JWSHeader header = new JWSHeader(JWSAlgorithm.RS256, null, null, null, null, null, null, null, null, null, "key_id", true, null, null);

		JWTClaimsSet payload = new JWTClaimsSet.Builder()
			.subject("subject")
			.issueTime(new Date(now.toEpochMilli()))
			.expirationTime(new Date(now.plus(15, ChronoUnit.MINUTES).toEpochMilli()))
			.claim(ClaimName.ACQUIRER_ID, "acquirer_id")
			.claim(ClaimName.CHANNEL, "channel")
			.claim(ClaimName.MERCHANT_ID, "merchant_id")
			.claim(ClaimName.CLIENT_ID, "client_id")
			.claim(ClaimName.TERMINAL_ID, "teminal_id")
			.build();

		SignedJWT refreshToken = new SignedJWT(header.toBase64URL(), payload.toPayload().toBase64URL(), Base64URL.from("AA"));

		/*
		 * Test
		 */
		GetAccessTokenRequest request = new GetAccessTokenRequest()
			.setAcquirerId("acquirer_id")
			.setChannel("channel")
			.setClientId("client_id")
			.setRefreshToken(refreshToken)
			.setGrantType(GrantType.REFRESH_TOKEN)
			.setMerchantId("merchant_id")
			.setTerminalId("terminal_id");

		refreshTokensService.process(request)
			.subscribe()
			.withSubscriber(UniAssertSubscriber.create())
			.assertFailedWith(AuthException.class);
	}

	/**
	 * 
	 * @throws ParseException
	 */
	@Test
	void given_refreshToken_when_clientIdIsWrong_then_getFailure() throws ParseException {
		/*
		 * Setup
		 */
		Instant now = Instant.now();

		JWSHeader header = new JWSHeader(JWSAlgorithm.RS256, null, null, null, null, null, null, null, null, null, "key_id", true, null, null);

		JWTClaimsSet payload = new JWTClaimsSet.Builder()
			.subject("subject")
			.issueTime(new Date(now.toEpochMilli()))
			.expirationTime(new Date(now.plus(15, ChronoUnit.MINUTES).toEpochMilli()))
			.claim(ClaimName.ACQUIRER_ID, "acquirer_id")
			.claim(ClaimName.CHANNEL, "channel")
			.claim(ClaimName.MERCHANT_ID, "merchant_id")
			.claim(ClaimName.CLIENT_ID, "client_id")
			.claim(ClaimName.TERMINAL_ID, "teminal_id")
			.claim(ClaimName.SCOPE, Scope.OFFLINE_ACCESS)
			.build();

		SignedJWT refreshToken = new SignedJWT(header.toBase64URL(), payload.toPayload().toBase64URL(), Base64URL.from("AA"));

		/*
		 * Test
		 */
		GetAccessTokenRequest request = new GetAccessTokenRequest()
			.setAcquirerId("acquirer_id")
			.setChannel("channel")
			.setClientId("wrong_client_id")
			.setRefreshToken(refreshToken)
			.setGrantType(GrantType.REFRESH_TOKEN)
			.setMerchantId("merchant_id")
			.setTerminalId("terminal_id");

		refreshTokensService.process(request)
			.subscribe()
			.withSubscriber(UniAssertSubscriber.create())
			.assertFailedWith(AuthException.class);
	}

	/**
	 * 
	 * @throws ParseException
	 */
	@Test
	void given_refreshToken_when_tokenIsExpired_then_getFailure() throws ParseException {
		/*
		 * Setup
		 */
		Instant now = Instant.now();

		JWSHeader header = new JWSHeader(JWSAlgorithm.RS256, null, null, null, null, null, null, null, null, null, "key_id", true, null, null);

		JWTClaimsSet payload = new JWTClaimsSet.Builder()
			.subject("subject")
			.issueTime(new Date(now.minus(5, ChronoUnit.MINUTES).toEpochMilli()))
			.expirationTime(new Date(now.minus(15, ChronoUnit.MINUTES).toEpochMilli()))
			.claim(ClaimName.ACQUIRER_ID, "acquirer_id")
			.claim(ClaimName.CHANNEL, "channel")
			.claim(ClaimName.MERCHANT_ID, "merchant_id")
			.claim(ClaimName.CLIENT_ID, "client_id")
			.claim(ClaimName.TERMINAL_ID, "teminal_id")
			.claim(ClaimName.SCOPE, Scope.OFFLINE_ACCESS)
			.build();

		SignedJWT refreshToken = new SignedJWT(header.toBase64URL(), payload.toPayload().toBase64URL(), Base64URL.from("AA"));

		/*
		 * Test
		 */
		GetAccessTokenRequest request = new GetAccessTokenRequest()
			.setAcquirerId("acquirer_id")
			.setChannel("channel")
			.setClientId("client_id")
			.setRefreshToken(refreshToken)
			.setGrantType(GrantType.REFRESH_TOKEN)
			.setMerchantId("merchant_id")
			.setTerminalId("terminal_id");

		refreshTokensService.process(request)
			.subscribe()
			.withSubscriber(UniAssertSubscriber.create())
			.assertFailedWith(AuthException.class);
	}

	/**
	 * 
	 * @throws ParseException
	 */
	@Test
	void given_refreshToken_when_expirationIsNull_then_getFailure() throws ParseException {
		/*
		 * Setup
		 */
		Instant now = Instant.now();

		JWSHeader header = new JWSHeader(JWSAlgorithm.RS256, null, null, null, null, null, null, null, null, null, "key_id", true, null, null);

		JWTClaimsSet payload = new JWTClaimsSet.Builder()
			.subject("subject")
			.issueTime(new Date(now.minus(5, ChronoUnit.MINUTES).toEpochMilli()))
			.claim(ClaimName.ACQUIRER_ID, "acquirer_id")
			.claim(ClaimName.CHANNEL, "channel")
			.claim(ClaimName.MERCHANT_ID, "merchant_id")
			.claim(ClaimName.CLIENT_ID, "client_id")
			.claim(ClaimName.TERMINAL_ID, "teminal_id")
			.claim(ClaimName.SCOPE, Scope.OFFLINE_ACCESS)
			.build();

		SignedJWT refreshToken = new SignedJWT(header.toBase64URL(), payload.toPayload().toBase64URL(), Base64URL.from("AA"));

		/*
		 * Test
		 */
		GetAccessTokenRequest request = new GetAccessTokenRequest()
			.setAcquirerId("acquirer_id")
			.setChannel("channel")
			.setClientId("client_id")
			.setRefreshToken(refreshToken)
			.setGrantType(GrantType.REFRESH_TOKEN)
			.setMerchantId("merchant_id")
			.setTerminalId("terminal_id");

		refreshTokensService.process(request)
			.subscribe()
			.withSubscriber(UniAssertSubscriber.create())
			.assertFailedWith(AuthException.class);
	}

	/**
	 * 
	 * @throws ParseException
	 */
	@Test
	void given_refreshToken_when_issueTimeIsInTheFuture_then_getFailure() throws ParseException {
		/*
		 * Setup
		 */
		Instant now = Instant.now();

		JWSHeader header = new JWSHeader(JWSAlgorithm.RS256, null, null, null, null, null, null, null, null, null, "key_id", true, null, null);

		JWTClaimsSet payload = new JWTClaimsSet.Builder()
			.subject("subject")
			.issueTime(new Date(now.plus(5, ChronoUnit.MINUTES).toEpochMilli()))
			.expirationTime(new Date(now.plus(15, ChronoUnit.MINUTES).toEpochMilli()))
			.claim(ClaimName.ACQUIRER_ID, "acquirer_id")
			.claim(ClaimName.CHANNEL, "channel")
			.claim(ClaimName.MERCHANT_ID, "merchant_id")
			.claim(ClaimName.CLIENT_ID, "client_id")
			.claim(ClaimName.TERMINAL_ID, "teminal_id")
			.claim(ClaimName.SCOPE, Scope.OFFLINE_ACCESS)
			.build();

		SignedJWT refreshToken = new SignedJWT(header.toBase64URL(), payload.toPayload().toBase64URL(), Base64URL.from("AA"));

		/*
		 * Test
		 */
		GetAccessTokenRequest request = new GetAccessTokenRequest()
			.setAcquirerId("acquirer_id")
			.setChannel("channel")
			.setClientId("client_id")
			.setRefreshToken(refreshToken)
			.setGrantType(GrantType.REFRESH_TOKEN)
			.setMerchantId("merchant_id")
			.setTerminalId("terminal_id");

		refreshTokensService.process(request)
			.subscribe()
			.withSubscriber(UniAssertSubscriber.create())
			.assertFailedWith(AuthException.class);
	}

	/**
	 * 
	 * @throws ParseException
	 */
	@Test
	void given_refreshToken_when_issueIsNull_then_getFailure() throws ParseException {
		/*
		 * Setup
		 */
		Instant now = Instant.now();

		JWSHeader header = new JWSHeader(JWSAlgorithm.RS256, null, null, null, null, null, null, null, null, null, "key_id", true, null, null);

		JWTClaimsSet payload = new JWTClaimsSet.Builder()
			.subject("subject")
			.expirationTime(new Date(now.plus(15, ChronoUnit.MINUTES).toEpochMilli()))
			.claim(ClaimName.ACQUIRER_ID, "acquirer_id")
			.claim(ClaimName.CHANNEL, "channel")
			.claim(ClaimName.MERCHANT_ID, "merchant_id")
			.claim(ClaimName.CLIENT_ID, "client_id")
			.claim(ClaimName.TERMINAL_ID, "teminal_id")
			.claim(ClaimName.SCOPE, Scope.OFFLINE_ACCESS)
			.build();

		SignedJWT refreshToken = new SignedJWT(header.toBase64URL(), payload.toPayload().toBase64URL(), Base64URL.from("AA"));

		/*
		 * Test
		 */
		GetAccessTokenRequest request = new GetAccessTokenRequest()
			.setAcquirerId("acquirer_id")
			.setChannel("channel")
			.setClientId("client_id")
			.setRefreshToken(refreshToken)
			.setGrantType(GrantType.REFRESH_TOKEN)
			.setMerchantId("merchant_id")
			.setTerminalId("terminal_id");

		refreshTokensService.process(request)
			.subscribe()
			.withSubscriber(UniAssertSubscriber.create())
			.assertFailedWith(AuthException.class);
	}

	/**
	 * 
	 * @throws ParseException
	 */
	@Test
	void given_refreshToken_when_algIsWrong_then_getFailure() throws ParseException {
		/*
		 * Setup
		 */
		Instant now = Instant.now();

		JWSHeader header = new JWSHeader(JWSAlgorithm.Ed25519, null, null, null, null, null, null, null, null, null, "key_id", true, null, null);

		JWTClaimsSet payload = new JWTClaimsSet.Builder()
			.subject("subject")
			.issueTime(new Date(now.toEpochMilli()))
			.expirationTime(new Date(now.plus(15, ChronoUnit.MINUTES).toEpochMilli()))
			.claim(ClaimName.ACQUIRER_ID, "acquirer_id")
			.claim(ClaimName.CHANNEL, "channel")
			.claim(ClaimName.MERCHANT_ID, "merchant_id")
			.claim(ClaimName.CLIENT_ID, "client_id")
			.claim(ClaimName.TERMINAL_ID, "teminal_id")
			.claim(ClaimName.SCOPE, Scope.OFFLINE_ACCESS)
			.build();

		SignedJWT refreshToken = new SignedJWT(header.toBase64URL(), payload.toPayload().toBase64URL(), Base64URL.from("AA"));

		/*
		 * Test
		 */
		GetAccessTokenRequest request = new GetAccessTokenRequest()
			.setAcquirerId("acquirer_id")
			.setChannel("channel")
			.setClientId("client_id")
			.setRefreshToken(refreshToken)
			.setGrantType(GrantType.REFRESH_TOKEN)
			.setMerchantId("merchant_id")
			.setTerminalId("terminal_id");

		refreshTokensService.process(request)
			.subscribe()
			.withSubscriber(UniAssertSubscriber.create())
			.assertFailedWith(AuthException.class);
	}

	/**
	 * 
	 * @throws ParseException
	 */
	@Test
	void given_refreshToken_when_signatureIsWrong_then_getFailure() throws ParseException {
		/*
		 * Setup
		 */
		Instant now = Instant.now();

		JWSHeader header = new JWSHeader(JWSAlgorithm.RS256, null, null, null, null, null, null, null, null, null, "key_id", true, null, null);

		JWTClaimsSet payload = new JWTClaimsSet.Builder()
			.subject("subject")
			.issueTime(new Date(now.toEpochMilli()))
			.expirationTime(new Date(now.plus(15, ChronoUnit.MINUTES).toEpochMilli()))
			.claim(ClaimName.ACQUIRER_ID, "acquirer_id")
			.claim(ClaimName.CHANNEL, "channel")
			.claim(ClaimName.MERCHANT_ID, "merchant_id")
			.claim(ClaimName.CLIENT_ID, "client_id")
			.claim(ClaimName.TERMINAL_ID, "teminal_id")
			.claim(ClaimName.SCOPE, Scope.OFFLINE_ACCESS)
			.build();

		SignedJWT refreshToken = new SignedJWT(header.toBase64URL(), payload.toPayload().toBase64URL(), Base64URL.from("AA"));

		when(tokenSigner.verify(any(SignedJWT.class)))
			.thenReturn(UniGenerator.exception(AuthErrorCode.WRONG_SIGNATURE, ""));

		/*
		 * Test
		 */
		GetAccessTokenRequest request = new GetAccessTokenRequest()
			.setAcquirerId("acquirer_id")
			.setChannel("channel")
			.setClientId("client_id")
			.setRefreshToken(refreshToken)
			.setGrantType(GrantType.REFRESH_TOKEN)
			.setMerchantId("merchant_id")
			.setTerminalId("terminal_id");

		refreshTokensService.process(request)
			.subscribe()
			.withSubscriber(UniAssertSubscriber.create())
			.assertFailedWith(AuthException.class);
	}
}