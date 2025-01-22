/*
 * RefreshTokensServiceTest.java
 *
 * 12 giu 2024
 */
package it.pagopa.swclient.mil.auth.service;

import static it.pagopa.swclient.mil.auth.util.MyAssertions.assertAuthError;
import static it.pagopa.swclient.mil.auth.util.MyAssertions.assertAuthException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.mockito.Mockito;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator;
import com.nimbusds.jose.util.Base64URL;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.mutiny.Uni;
import it.pagopa.swclient.mil.auth.AuthErrorCode;
import it.pagopa.swclient.mil.auth.bean.ClaimName;
import it.pagopa.swclient.mil.auth.bean.GetAccessTokenRequest;
import it.pagopa.swclient.mil.auth.bean.GrantType;
import it.pagopa.swclient.mil.auth.bean.Scope;
import it.pagopa.swclient.mil.auth.dao.ClientEntity;
import it.pagopa.swclient.mil.auth.dao.RevokedRefreshTokenEntity;
import it.pagopa.swclient.mil.auth.dao.RevokedRefreshTokenRepository;
import it.pagopa.swclient.mil.auth.dao.RevokedRefreshTokensGenerationEntity;
import it.pagopa.swclient.mil.auth.dao.RevokedRefreshTokensGenerationRepository;
import it.pagopa.swclient.mil.auth.dao.SetOfRolesEntity;
import it.pagopa.swclient.mil.auth.qualifier.RefreshToken;
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
	@InjectMock
	RevokedRefreshTokensGenerationRepository revokedRefreshTokensGenerationRepository;

	/*
	 * 
	 */
	@InjectMock
	RevokedRefreshTokenRepository revokedRefreshTokenRepository;

	/*
	 * 
	 */
	@Inject
	@RefreshToken
	RefreshTokensService refreshTokensService;

	/*
	 * 
	 */
	private static String channel;
	private static String subject;
	private static String acquirerId;
	private static String merchantId;
	private static String terminalId;
	private static String clientId;
	private static String anotherClientId;
	private static List<String> roles;
	private static String generationId;
	private static int duration;
	private static String kid;
	private static JWSSigner signer;
	private static JWSVerifier verifier;

	/**
	 * 
	 * @param minLen
	 * @param maxLen
	 * @param symbols
	 * @return
	 * @throws NoSuchAlgorithmException
	 */
	private static String generateRandomString(int minLen, int maxLen, String symbols) throws NoSuchAlgorithmException {
		SecureRandom secureRandom = SecureRandom.getInstanceStrong();
		final int len = secureRandom.nextInt(minLen, maxLen + 1);
		return secureRandom.ints(len, 0, symbols.length())
			.map(symbols::charAt)
			.collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
			.toString();
	}

	/**
	 * 
	 * @throws NoSuchAlgorithmException
	 * @throws JOSEException
	 */
	@BeforeAll
	static void generateRandomData() throws NoSuchAlgorithmException, JOSEException {
		final List<String> values = List.of("ATM", "POS", "TOTEM", "CASH_REGISTER", "CSA");
		channel = values.get(SecureRandom.getInstanceStrong().nextInt(values.size()));
		subject = generateRandomString(16, 16, "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789");
		acquirerId = generateRandomString(1, 11, "0123456789");
		merchantId = generateRandomString(1, 15, "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789");
		terminalId = generateRandomString(1, 8, "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789");
		clientId = UUID.randomUUID().toString();
		anotherClientId = UUID.randomUUID().toString();
		roles = List.of("role");
		generationId = UUID.randomUUID().toString();
		duration = SecureRandom.getInstanceStrong().nextInt(10, 21);
		kid = generateRandomString(16, 16, "abcdefghijklmnopqrstuvwxyz0123456789");
		RSAKey rsaKey = new RSAKeyGenerator(2048)
			.keyID(kid)
			.generate();
		signer = new RSASSASigner(rsaKey);
		verifier = new RSASSAVerifier(rsaKey);
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
		Mockito.reset(
			clientVerifier,
			roleFinder,
			tokenSigner,
			revokedRefreshTokensGenerationRepository,
			revokedRefreshTokenRepository);
	}

	/**
	 * 
	 * @param algorithm
	 * @param kid
	 * @param jwtId
	 * @param subject
	 * @param issueTime
	 * @param expirationTime
	 * @param acquirerId
	 * @param channel
	 * @param merchantId
	 * @param clientId
	 * @param terminalId
	 * @param generationId
	 * @param scope
	 * @param returnedInTheCookie
	 * @return
	 * @throws JOSEException
	 */
	private SignedJWT generateRefreshToken(
		JWSAlgorithm algorithm,
		String kid,
		String jwtId,
		String subject,
		Date issueTime,
		Date expirationTime,
		String acquirerId,
		String channel,
		String merchantId,
		String clientId,
		String terminalId,
		String generationId,
		String scope,
		Object returnedInTheCookie) throws JOSEException {
		JWSHeader header = new JWSHeader(
			algorithm,
			null,
			null,
			null,
			null,
			null,
			null,
			null,
			null,
			null,
			kid,
			true,
			null,
			null);

		JWTClaimsSet payload = new JWTClaimsSet.Builder()
			.jwtID(jwtId)
			.subject(subject)
			.issueTime(issueTime)
			.expirationTime(expirationTime)
			.claim(ClaimName.ACQUIRER_ID, acquirerId)
			.claim(ClaimName.CHANNEL, channel)
			.claim(ClaimName.MERCHANT_ID, merchantId)
			.claim(ClaimName.CLIENT_ID, clientId)
			.claim(ClaimName.TERMINAL_ID, terminalId)
			.claim(ClaimName.GENERATION_ID, generationId)
			.claim(ClaimName.SCOPE, scope)
			.claim(ClaimName.RETURNED_IN_THE_COOKIE, returnedInTheCookie)
			.build();

		SignedJWT refreshToken = new SignedJWT(header, payload);
		refreshToken.sign(signer);

		return refreshToken;
	}

	/**
	 * 
	 * @param putInTheCookie
	 * @throws JOSEException
	 */
	private void given_refreshToken_when_allGoesOk_then_getTokens(Object putInTheCookie) throws JOSEException {
		/*
		 * Current refresh token
		 */
		Instant now = Instant.now();

		String jwtId = UUID.randomUUID().toString();

		SignedJWT currentRefreshToken = generateRefreshToken(
			JWSAlgorithm.RS256,
			kid,
			jwtId,
			subject,
			new Date(now.toEpochMilli()),
			new Date(now.plus(duration, ChronoUnit.MINUTES).toEpochMilli()),
			acquirerId,
			channel,
			merchantId,
			clientId,
			terminalId,
			generationId,
			Scope.OFFLINE_ACCESS,
			putInTheCookie);

		/*
		 * Mocks
		 */
		when(tokenSigner.verify(any(SignedJWT.class)))
			.thenReturn(UniGenerator.item(null));

		when(clientVerifier.verify(clientId, channel, null))
			.thenReturn(UniGenerator.item(new ClientEntity()));

		when(roleFinder.findRoles(acquirerId, channel, clientId, merchantId, terminalId))
			.thenReturn(UniGenerator.item(new SetOfRolesEntity()
				.setRoles(roles)));

		when(tokenSigner.sign(any(JWTClaimsSet.class)))
			.thenAnswer(i -> {
				JWSHeader newHeader = new JWSHeader(
					JWSAlgorithm.RS256,
					null,
					null,
					null,
					null,
					null,
					null,
					null,
					null,
					null,
					kid,
					true,
					null,
					null);

				JWTClaimsSet newPayload = i.getArgument(0);

				SignedJWT newRefreshToken = new SignedJWT(newHeader, newPayload);
				newRefreshToken.sign(signer);

				return UniGenerator.item(newRefreshToken);
			});

		when(revokedRefreshTokensGenerationRepository.findByGenerationId(generationId))
			.thenReturn(Uni.createFrom()
				.item(Optional.empty()));

		when(revokedRefreshTokenRepository.findByJwtId(jwtId))
			.thenReturn(Uni.createFrom()
				.item(Optional.empty()));

		RevokedRefreshTokenEntity revokedRefreshTokenEntity = new RevokedRefreshTokenEntity(null, jwtId);
		when(revokedRefreshTokenRepository.persist(revokedRefreshTokenEntity))
			.thenReturn(Uni.createFrom().item(revokedRefreshTokenEntity));

		/*
		 * Test
		 */
		GetAccessTokenRequest request = new GetAccessTokenRequest()
			.setAcquirerId(acquirerId)
			.setChannel(channel)
			.setMerchantId(merchantId)
			.setTerminalId(terminalId)
			.setClientId(clientId)
			.setGrantType(GrantType.REFRESH_TOKEN);

		if (putInTheCookie != null && putInTheCookie instanceof Boolean boolPutInTheCookie && boolPutInTheCookie) {
			request.setRefreshCookie(currentRefreshToken);
		} else {
			request.setRefreshToken(currentRefreshToken);
		}

		refreshTokensService.process(request)
			.subscribe()
			.with(
				response -> {
					SignedJWT accessToken = response.getAccessToken();
					try {
						assertTrue(accessToken.verify(verifier), "Wrong access token signature");
						assertEquals(JWSAlgorithm.RS256, accessToken.getHeader().getAlgorithm(), "Wrong access token signature algorithm");
						assertTrue(accessToken.getJWTClaimsSet().getIssueTime().before(new Date()), "Wrong access token issue time");
						assertTrue(accessToken.getJWTClaimsSet().getExpirationTime().after(new Date()), "Access token expired");
						assertNotNull(accessToken.getJWTClaimsSet().getJWTID(), "Null access token ID");
						assertNotNull(accessToken.getJWTClaimsSet().getSubject(), "Null access token subject");
						assertNotNull(accessToken.getJWTClaimsSet().getAudience(), "Null access token audience");
						assertNotNull(accessToken.getJWTClaimsSet().getIssuer(), "Null access token issuer");
						assertNull(accessToken.getJWTClaimsSet().getStringClaim(ClaimName.GENERATION_ID), "Not null access token generation ID");
						assertEquals(acquirerId, accessToken.getJWTClaimsSet().getStringClaim(ClaimName.ACQUIRER_ID), "Wrong access token acquirer ID");
						assertEquals(channel, accessToken.getJWTClaimsSet().getStringClaim(ClaimName.CHANNEL), "Wrong access token channel");
						assertEquals(merchantId, accessToken.getJWTClaimsSet().getStringClaim(ClaimName.MERCHANT_ID), "Wrong access token merchant ID");
						assertEquals(clientId, accessToken.getJWTClaimsSet().getStringClaim(ClaimName.CLIENT_ID), "Wrong access token client ID");
						assertEquals(terminalId, accessToken.getJWTClaimsSet().getStringClaim(ClaimName.TERMINAL_ID), "Wrong access token terminal ID");
						assertEquals(roles, accessToken.getJWTClaimsSet().getListClaim(ClaimName.GROUPS), "Wrong access token groups");
						assertNull(accessToken.getJWTClaimsSet().getStringListClaim(ClaimName.SCOPE), "Not null access token scope");
						assertNull(accessToken.getJWTClaimsSet().getClaim(ClaimName.FISCAL_CODE), "Not null access token fiscal code");

						SignedJWT newRefreshToken = response.getRefreshToken();
						assertTrue(newRefreshToken.verify(verifier), "Wrong refresh token signature");
						assertEquals(JWSAlgorithm.RS256, newRefreshToken.getHeader().getAlgorithm(), "Wrong refresh token signature algorithm");
						assertTrue(newRefreshToken.getJWTClaimsSet().getIssueTime().before(new Date()), "Wrong refresh token issue time");
						assertTrue(newRefreshToken.getJWTClaimsSet().getExpirationTime().after(new Date()), "Refresh token expired");
						assertNotNull(newRefreshToken.getJWTClaimsSet().getJWTID(), "Null refresh token ID");
						assertNotNull(newRefreshToken.getJWTClaimsSet().getSubject(), "Null refresh token subject");
						assertNotNull(newRefreshToken.getJWTClaimsSet().getAudience(), "Null refresh token audience");
						assertNotNull(newRefreshToken.getJWTClaimsSet().getIssuer(), "Null refresh token issuer");
						assertEquals(generationId, newRefreshToken.getJWTClaimsSet().getStringClaim(ClaimName.GENERATION_ID), "Wrong refresh token generation ID");
						assertEquals(acquirerId, newRefreshToken.getJWTClaimsSet().getStringClaim(ClaimName.ACQUIRER_ID), "Wrong refresh token acquirer ID");
						assertEquals(channel, newRefreshToken.getJWTClaimsSet().getStringClaim(ClaimName.CHANNEL), "Wrong refresh token channel");
						assertEquals(merchantId, newRefreshToken.getJWTClaimsSet().getStringClaim(ClaimName.MERCHANT_ID), "Wrong refresh token merchant ID");
						assertEquals(clientId, newRefreshToken.getJWTClaimsSet().getStringClaim(ClaimName.CLIENT_ID), "Wrong refresh token client ID");
						assertEquals(terminalId, newRefreshToken.getJWTClaimsSet().getStringClaim(ClaimName.TERMINAL_ID), "Wrong refresh token terminal ID");
						assertNull(newRefreshToken.getJWTClaimsSet().getStringListClaim(ClaimName.GROUPS), "Not null refresh token groups");
						assertEquals(List.of(Scope.OFFLINE_ACCESS), newRefreshToken.getJWTClaimsSet().getStringListClaim(ClaimName.SCOPE), "Wrong refresh token scope");
						assertNull(newRefreshToken.getJWTClaimsSet().getClaim(ClaimName.FISCAL_CODE), "Not null refresh token fiscal code");
					} catch (ParseException | JOSEException e) {
						fail(e);
					}
				},
				f -> fail(f));
	}

	/**
	 * 
	 * @throws JOSEException
	 */
	@Test
	void given_refreshTokenInTheBody_when_allGoesOk_then_getTokens() throws JOSEException {
		given_refreshToken_when_allGoesOk_then_getTokens(null);
	}

	/**
	 * 
	 * @throws JOSEException
	 */
	@Test
	void given_refreshTokenInTheCookie_when_allGoesOk_then_getTokens() throws JOSEException {
		given_refreshToken_when_allGoesOk_then_getTokens(true);
	}

	/**
	 * 
	 * @throws JOSEException
	 */
	@Test
	void given_refreshTokenReturnedInTheCookie_when_itIsPutInTheBody_then_getFailure() throws JOSEException {
		/*
		 * Current refresh token
		 */
		Instant now = Instant.now();

		String jwtId = UUID.randomUUID().toString();

		SignedJWT currentRefreshToken = generateRefreshToken(
			JWSAlgorithm.RS256,
			kid,
			jwtId,
			subject,
			new Date(now.toEpochMilli()),
			new Date(now.plus(duration, ChronoUnit.MINUTES).toEpochMilli()),
			acquirerId,
			channel,
			merchantId,
			clientId,
			terminalId,
			generationId,
			Scope.OFFLINE_ACCESS,
			true);

		/*
		 * Mocks
		 */
		when(tokenSigner.verify(any(SignedJWT.class)))
			.thenReturn(UniGenerator.item(null));

		when(clientVerifier.verify(clientId, channel, null))
			.thenReturn(UniGenerator.item(new ClientEntity()));

		when(roleFinder.findRoles(acquirerId, channel, clientId, merchantId, terminalId))
			.thenReturn(UniGenerator.item(new SetOfRolesEntity()
				.setRoles(roles)));

		when(tokenSigner.sign(any(JWTClaimsSet.class)))
			.thenAnswer(i -> {
				JWSHeader newHeader = new JWSHeader(
					JWSAlgorithm.RS256,
					null,
					null,
					null,
					null,
					null,
					null,
					null,
					null,
					null,
					kid,
					true,
					null,
					null);

				JWTClaimsSet newPayload = i.getArgument(0);

				SignedJWT newRefreshToken = new SignedJWT(newHeader, newPayload);
				newRefreshToken.sign(signer);

				return UniGenerator.item(newRefreshToken);
			});

		when(revokedRefreshTokensGenerationRepository.findByGenerationId(generationId))
			.thenReturn(Uni.createFrom()
				.item(Optional.empty()));

		when(revokedRefreshTokenRepository.findByJwtId(jwtId))
			.thenReturn(Uni.createFrom()
				.item(Optional.empty()));

		RevokedRefreshTokenEntity revokedRefreshTokenEntity = new RevokedRefreshTokenEntity(null, jwtId);
		when(revokedRefreshTokenRepository.persist(revokedRefreshTokenEntity))
			.thenReturn(Uni.createFrom().item(revokedRefreshTokenEntity));

		/*
		 * Test
		 */
		GetAccessTokenRequest request = new GetAccessTokenRequest()
			.setAcquirerId(acquirerId)
			.setChannel(channel)
			.setMerchantId(merchantId)
			.setTerminalId(terminalId)
			.setClientId(clientId)
			.setGrantType(GrantType.REFRESH_TOKEN)
			.setRefreshToken(currentRefreshToken);

		refreshTokensService.process(request)
			.subscribe()
			.with(
				i -> fail("Failure expected"),
				f -> assertAuthException(f, AuthErrorCode.WRONG_REFRESH_TOKEN_LOCATION));
	}
	
	/**
	 * 
	 * @throws JOSEException
	 */
	@Test
	void given_refreshTokenReturnedInTheCookie_when_itIsPutInTheBodyAndRevocationFails_then_getFailure() throws JOSEException {
		/*
		 * Current refresh token
		 */
		Instant now = Instant.now();

		String jwtId = UUID.randomUUID().toString();

		SignedJWT currentRefreshToken = generateRefreshToken(
			JWSAlgorithm.RS256,
			kid,
			jwtId,
			subject,
			new Date(now.toEpochMilli()),
			new Date(now.plus(duration, ChronoUnit.MINUTES).toEpochMilli()),
			acquirerId,
			channel,
			merchantId,
			clientId,
			terminalId,
			generationId,
			Scope.OFFLINE_ACCESS,
			true);

		/*
		 * Mocks
		 */
		when(tokenSigner.verify(any(SignedJWT.class)))
			.thenReturn(UniGenerator.item(null));

		when(clientVerifier.verify(clientId, channel, null))
			.thenReturn(UniGenerator.item(new ClientEntity()));

		when(roleFinder.findRoles(acquirerId, channel, clientId, merchantId, terminalId))
			.thenReturn(UniGenerator.item(new SetOfRolesEntity()
				.setRoles(roles)));

		when(tokenSigner.sign(any(JWTClaimsSet.class)))
			.thenAnswer(i -> {
				JWSHeader newHeader = new JWSHeader(
					JWSAlgorithm.RS256,
					null,
					null,
					null,
					null,
					null,
					null,
					null,
					null,
					null,
					kid,
					true,
					null,
					null);

				JWTClaimsSet newPayload = i.getArgument(0);

				SignedJWT newRefreshToken = new SignedJWT(newHeader, newPayload);
				newRefreshToken.sign(signer);

				return UniGenerator.item(newRefreshToken);
			});

		when(revokedRefreshTokensGenerationRepository.findByGenerationId(generationId))
			.thenReturn(Uni.createFrom()
				.item(Optional.empty()));

		when(revokedRefreshTokenRepository.findByJwtId(jwtId))
			.thenReturn(Uni.createFrom()
				.item(Optional.empty()));

		RevokedRefreshTokenEntity revokedRefreshTokenEntity = new RevokedRefreshTokenEntity(null, jwtId);
		when(revokedRefreshTokenRepository.persist(revokedRefreshTokenEntity))
			.thenReturn(Uni.createFrom().failure(new Exception("synthetic exception")));

		/*
		 * Test
		 */
		GetAccessTokenRequest request = new GetAccessTokenRequest()
			.setAcquirerId(acquirerId)
			.setChannel(channel)
			.setMerchantId(merchantId)
			.setTerminalId(terminalId)
			.setClientId(clientId)
			.setGrantType(GrantType.REFRESH_TOKEN)
			.setRefreshToken(currentRefreshToken);

		refreshTokensService.process(request)
			.subscribe()
			.with(
				i -> fail("Failure expected"),
				f -> assertAuthException(f, AuthErrorCode.WRONG_REFRESH_TOKEN_LOCATION));
	}

	/**
	 * 
	 * @throws ParseException
	 */
	@Test
	void given_refreshToken_when_itIsBad_then_getFailure() throws ParseException {
		/*
		 * Setup
		 */
		JWSHeader header = new JWSHeader(
			JWSAlgorithm.RS256,
			null,
			null,
			null,
			null,
			null,
			null,
			null,
			null,
			null,
			kid,
			true,
			null,
			null);

		SignedJWT refreshToken = new SignedJWT(
			header.toBase64URL(),
			new Base64URL("dGVzdA=="),
			Base64URL.from("AA"));

		/*
		 * Test
		 */
		GetAccessTokenRequest request = new GetAccessTokenRequest()
			.setAcquirerId(acquirerId)
			.setChannel(channel)
			.setClientId(clientId)
			.setRefreshToken(refreshToken)
			.setGrantType(GrantType.REFRESH_TOKEN)
			.setMerchantId(merchantId)
			.setTerminalId(terminalId);

		refreshTokensService.process(request)
			.subscribe()
			.with(
				i -> fail("Failure expected"),
				f -> assertAuthError(f, AuthErrorCode.ERROR_PARSING_TOKEN));
	}

	/**
	 * 
	 * @throws JOSEException
	 */
	@Test
	void given_refreshToken_when_scopeIsMissing_then_getFailure() throws JOSEException {
		/*
		 * Setup
		 */
		Instant now = Instant.now();

		String jwtId = UUID.randomUUID().toString();

		SignedJWT refreshToken = generateRefreshToken(
			JWSAlgorithm.RS256,
			kid,
			jwtId,
			subject,
			new Date(now.toEpochMilli()),
			new Date(now.plus(duration, ChronoUnit.MINUTES).toEpochMilli()),
			acquirerId,
			channel,
			merchantId,
			clientId,
			terminalId,
			generationId,
			null,
			false);

		/*
		 * Test
		 */
		GetAccessTokenRequest request = new GetAccessTokenRequest()
			.setAcquirerId(acquirerId)
			.setChannel(channel)
			.setClientId(clientId)
			.setRefreshToken(refreshToken)
			.setGrantType(GrantType.REFRESH_TOKEN)
			.setMerchantId(merchantId)
			.setTerminalId(terminalId);

		refreshTokensService.process(request)
			.subscribe()
			.with(
				i -> fail("Failure expected"),
				f -> assertAuthException(f, AuthErrorCode.WRONG_SCOPE));
	}

	/**
	 * 
	 * @throws JOSEException
	 */
	@Test
	void given_refreshToken_when_clientIdIsWrong_then_getFailure() throws JOSEException {
		/*
		 * Setup
		 */
		Instant now = Instant.now();

		String jwtId = UUID.randomUUID().toString();

		SignedJWT refreshToken = generateRefreshToken(
			JWSAlgorithm.RS256,
			kid,
			jwtId,
			subject,
			new Date(now.toEpochMilli()),
			new Date(now.plus(duration, ChronoUnit.MINUTES).toEpochMilli()),
			acquirerId,
			channel,
			merchantId,
			clientId,
			terminalId,
			generationId,
			Scope.OFFLINE_ACCESS,
			false);

		/*
		 * Test
		 */
		GetAccessTokenRequest request = new GetAccessTokenRequest()
			.setAcquirerId(acquirerId)
			.setChannel(channel)
			.setClientId(anotherClientId)
			.setRefreshToken(refreshToken)
			.setGrantType(GrantType.REFRESH_TOKEN)
			.setMerchantId(merchantId)
			.setTerminalId(terminalId);

		refreshTokensService.process(request)
			.subscribe()
			.with(
				i -> fail("Failure expected"),
				f -> assertAuthException(f, AuthErrorCode.WRONG_CLIENT_ID));
	}

	/**
	 * 
	 * @throws JOSEException
	 */
	@Test
	void given_refreshToken_when_tokenIsExpired_then_getFailure() throws JOSEException {
		/*
		 * Setup
		 */
		Instant now = Instant.now();

		String jwtId = UUID.randomUUID().toString();

		SignedJWT refreshToken = generateRefreshToken(
			JWSAlgorithm.RS256,
			kid,
			jwtId,
			subject,
			new Date(now.minus(10, ChronoUnit.MINUTES).toEpochMilli()),
			new Date(now.minus(5, ChronoUnit.MINUTES).toEpochMilli()),
			acquirerId,
			channel,
			merchantId,
			clientId,
			terminalId,
			generationId,
			Scope.OFFLINE_ACCESS,
			false);

		/*
		 * Test
		 */
		GetAccessTokenRequest request = new GetAccessTokenRequest()
			.setAcquirerId(acquirerId)
			.setChannel(channel)
			.setClientId(clientId)
			.setRefreshToken(refreshToken)
			.setGrantType(GrantType.REFRESH_TOKEN)
			.setMerchantId(merchantId)
			.setTerminalId(terminalId);

		refreshTokensService.process(request)
			.subscribe()
			.with(
				i -> fail("Failure expected"),
				f -> assertAuthException(f, AuthErrorCode.TOKEN_EXPIRED));
	}

	/**
	 * 
	 * @throws JOSEException
	 */
	@Test
	void given_refreshToken_when_expirationIsNull_then_getFailure() throws JOSEException {
		/*
		 * Setup
		 */
		Instant now = Instant.now();

		String jwtId = UUID.randomUUID().toString();

		SignedJWT refreshToken = generateRefreshToken(
			JWSAlgorithm.RS256,
			kid,
			jwtId,
			subject,
			new Date(now.toEpochMilli()),
			null,
			acquirerId,
			channel,
			merchantId,
			clientId,
			terminalId,
			generationId,
			Scope.OFFLINE_ACCESS,
			false);

		/*
		 * Test
		 */
		GetAccessTokenRequest request = new GetAccessTokenRequest()
			.setAcquirerId(acquirerId)
			.setChannel(channel)
			.setClientId(clientId)
			.setRefreshToken(refreshToken)
			.setGrantType(GrantType.REFRESH_TOKEN)
			.setMerchantId(merchantId)
			.setTerminalId(terminalId);

		refreshTokensService.process(request)
			.subscribe()
			.with(
				i -> fail("Failure expected"),
				f -> assertAuthException(f, AuthErrorCode.EXPIRATION_TIME_MUST_NOT_BE_NULL));
	}

	/**
	 * 
	 * @throws JOSEException
	 */
	@Test
	void given_refreshToken_when_issueTimeIsInTheFuture_then_getFailure() throws JOSEException {
		/*
		 * Setup
		 */
		Instant now = Instant.now();

		String jwtId = UUID.randomUUID().toString();

		SignedJWT refreshToken = generateRefreshToken(
			JWSAlgorithm.RS256,
			kid,
			jwtId,
			subject,
			new Date(now.plus(5, ChronoUnit.MINUTES).toEpochMilli()),
			new Date(now.plus(15, ChronoUnit.MINUTES).toEpochMilli()),
			acquirerId,
			channel,
			merchantId,
			clientId,
			terminalId,
			generationId,
			Scope.OFFLINE_ACCESS,
			false);

		/*
		 * Test
		 */
		GetAccessTokenRequest request = new GetAccessTokenRequest()
			.setAcquirerId(acquirerId)
			.setChannel(channel)
			.setClientId(clientId)
			.setRefreshToken(refreshToken)
			.setGrantType(GrantType.REFRESH_TOKEN)
			.setMerchantId(merchantId)
			.setTerminalId(terminalId);

		refreshTokensService.process(request)
			.subscribe()
			.with(
				i -> fail("Failure expected"),
				f -> assertAuthException(f, AuthErrorCode.WRONG_ISSUE_TIME));
	}

	/**
	 * 
	 * @throws JOSEException
	 */
	@Test
	void given_refreshToken_when_issueIsNull_then_getFailure() throws JOSEException {
		/*
		 * Setup
		 */
		Instant now = Instant.now();

		String jwtId = UUID.randomUUID().toString();

		SignedJWT refreshToken = generateRefreshToken(
			JWSAlgorithm.RS256,
			kid,
			jwtId,
			subject,
			null,
			new Date(now.plus(15, ChronoUnit.MINUTES).toEpochMilli()),
			acquirerId,
			channel,
			merchantId,
			clientId,
			terminalId,
			generationId,
			Scope.OFFLINE_ACCESS,
			false);

		/*
		 * Test
		 */
		GetAccessTokenRequest request = new GetAccessTokenRequest()
			.setAcquirerId(acquirerId)
			.setChannel(channel)
			.setClientId(clientId)
			.setRefreshToken(refreshToken)
			.setGrantType(GrantType.REFRESH_TOKEN)
			.setMerchantId(merchantId)
			.setTerminalId(terminalId);

		refreshTokensService.process(request)
			.subscribe()
			.with(
				i -> fail("Failure expected"),
				f -> assertAuthException(f, AuthErrorCode.ISSUE_TIME_MUST_NOT_BE_NULL));
	}

	/**
	 * 
	 * @throws JOSEException
	 */
	@Test
	void given_refreshToken_when_algIsWrong_then_getFailure() throws JOSEException {
		/*
		 * Setup
		 */
		Instant now = Instant.now();

		String jwtId = UUID.randomUUID().toString();

		SignedJWT refreshToken = generateRefreshToken(
			JWSAlgorithm.PS256,
			kid,
			jwtId,
			subject,
			new Date(now.toEpochMilli()),
			new Date(now.plus(duration, ChronoUnit.MINUTES).toEpochMilli()),
			acquirerId,
			channel,
			merchantId,
			clientId,
			terminalId,
			generationId,
			Scope.OFFLINE_ACCESS,
			false);

		/*
		 * Test
		 */
		GetAccessTokenRequest request = new GetAccessTokenRequest()
			.setAcquirerId(acquirerId)
			.setChannel(channel)
			.setClientId(clientId)
			.setRefreshToken(refreshToken)
			.setGrantType(GrantType.REFRESH_TOKEN)
			.setMerchantId(merchantId)
			.setTerminalId(terminalId);

		refreshTokensService.process(request)
			.subscribe()
			.with(
				i -> fail("Failure expected"),
				f -> assertAuthException(f, AuthErrorCode.WRONG_ALGORITHM));
	}

	/**
	 * 
	 * @throws JOSEException
	 */
	@Test
	void given_refreshToken_when_signatureIsWrong_then_getFailure() throws JOSEException {
		/*
		 * Setup
		 */
		Instant now = Instant.now();

		String jwtId = UUID.randomUUID().toString();

		SignedJWT refreshToken = generateRefreshToken(
			JWSAlgorithm.RS256,
			kid,
			jwtId,
			subject,
			new Date(now.toEpochMilli()),
			new Date(now.plus(duration, ChronoUnit.MINUTES).toEpochMilli()),
			acquirerId,
			channel,
			merchantId,
			clientId,
			terminalId,
			generationId,
			Scope.OFFLINE_ACCESS,
			false);

		when(tokenSigner.verify(any(SignedJWT.class)))
			.thenReturn(UniGenerator.exception(AuthErrorCode.WRONG_SIGNATURE, "Wrong signature"));

		/*
		 * Test
		 */
		GetAccessTokenRequest request = new GetAccessTokenRequest()
			.setAcquirerId(acquirerId)
			.setChannel(channel)
			.setClientId(clientId)
			.setRefreshToken(refreshToken)
			.setGrantType(GrantType.REFRESH_TOKEN)
			.setMerchantId(merchantId)
			.setTerminalId(terminalId);

		refreshTokensService.process(request)
			.subscribe()
			.with(
				i -> fail("Failure expected"),
				f -> assertAuthException(f, AuthErrorCode.WRONG_SIGNATURE));
	}

	/**
	 * 
	 * @throws JOSEException
	 */
	@Test
	void given_refreshToken_when_itIsRevoked_then_getFailure() throws JOSEException {
		/*
		 * Setup
		 */
		Instant now = Instant.now();

		String jwtId = UUID.randomUUID().toString();

		SignedJWT refreshToken = generateRefreshToken(
			JWSAlgorithm.RS256,
			kid,
			jwtId,
			subject,
			new Date(now.toEpochMilli()),
			new Date(now.plus(duration, ChronoUnit.MINUTES).toEpochMilli()),
			acquirerId,
			channel,
			merchantId,
			clientId,
			terminalId,
			generationId,
			Scope.OFFLINE_ACCESS,
			false);

		when(tokenSigner.verify(any(SignedJWT.class)))
			.thenReturn(UniGenerator.item(null));

		when(clientVerifier.verify(clientId, channel, null))
			.thenReturn(UniGenerator.item(new ClientEntity()));

		when(roleFinder.findRoles(acquirerId, channel, clientId, merchantId, terminalId))
			.thenReturn(UniGenerator.item(new SetOfRolesEntity()
				.setRoles(roles)));

		when(tokenSigner.sign(any(JWTClaimsSet.class)))
			.thenAnswer(i -> {
				JWSHeader newHeader = new JWSHeader(
					JWSAlgorithm.RS256,
					null,
					null,
					null,
					null,
					null,
					null,
					null,
					null,
					null,
					kid,
					true,
					null,
					null);

				JWTClaimsSet newPayload = i.getArgument(0);

				SignedJWT newRefreshToken = new SignedJWT(newHeader, newPayload);
				newRefreshToken.sign(signer);

				return UniGenerator.item(newRefreshToken);
			});

		when(revokedRefreshTokensGenerationRepository.findByGenerationId(generationId))
			.thenReturn(Uni.createFrom()
				.item(Optional.empty()));

		when(revokedRefreshTokenRepository.findByJwtId(jwtId))
			.thenReturn(Uni.createFrom()
				.item(Optional.of(new RevokedRefreshTokenEntity().setJwtId(jwtId))));

		RevokedRefreshTokensGenerationEntity revokedRefreshTokensGenerationEntity = new RevokedRefreshTokensGenerationEntity(null, generationId);
		when(revokedRefreshTokensGenerationRepository.persist(revokedRefreshTokensGenerationEntity))
			.thenReturn(Uni.createFrom().item(revokedRefreshTokensGenerationEntity));

		/*
		 * Test
		 */
		GetAccessTokenRequest request = new GetAccessTokenRequest()
			.setAcquirerId(acquirerId)
			.setChannel(channel)
			.setClientId(clientId)
			.setRefreshToken(refreshToken)
			.setGrantType(GrantType.REFRESH_TOKEN)
			.setMerchantId(merchantId)
			.setTerminalId(terminalId);

		refreshTokensService.process(request)
			.subscribe()
			.with(
				i -> fail("Failure expected"),
				f -> assertAuthException(f, AuthErrorCode.REFRESH_TOKEN_REVOKED));
	}
	
	/**
	 * 
	 * @throws JOSEException
	 */
	@Test
	void given_refreshToken_when_itIsRevokedAndGenerationRevocationFails_then_getFailure() throws JOSEException {
		/*
		 * Setup
		 */
		Instant now = Instant.now();

		String jwtId = UUID.randomUUID().toString();

		SignedJWT refreshToken = generateRefreshToken(
			JWSAlgorithm.RS256,
			kid,
			jwtId,
			subject,
			new Date(now.toEpochMilli()),
			new Date(now.plus(duration, ChronoUnit.MINUTES).toEpochMilli()),
			acquirerId,
			channel,
			merchantId,
			clientId,
			terminalId,
			generationId,
			Scope.OFFLINE_ACCESS,
			false);

		when(tokenSigner.verify(any(SignedJWT.class)))
			.thenReturn(UniGenerator.item(null));

		when(clientVerifier.verify(clientId, channel, null))
			.thenReturn(UniGenerator.item(new ClientEntity()));

		when(roleFinder.findRoles(acquirerId, channel, clientId, merchantId, terminalId))
			.thenReturn(UniGenerator.item(new SetOfRolesEntity()
				.setRoles(roles)));

		when(tokenSigner.sign(any(JWTClaimsSet.class)))
			.thenAnswer(i -> {
				JWSHeader newHeader = new JWSHeader(
					JWSAlgorithm.RS256,
					null,
					null,
					null,
					null,
					null,
					null,
					null,
					null,
					null,
					kid,
					true,
					null,
					null);

				JWTClaimsSet newPayload = i.getArgument(0);

				SignedJWT newRefreshToken = new SignedJWT(newHeader, newPayload);
				newRefreshToken.sign(signer);

				return UniGenerator.item(newRefreshToken);
			});

		when(revokedRefreshTokensGenerationRepository.findByGenerationId(generationId))
			.thenReturn(Uni.createFrom()
				.item(Optional.empty()));

		when(revokedRefreshTokenRepository.findByJwtId(jwtId))
			.thenReturn(Uni.createFrom()
				.item(Optional.of(new RevokedRefreshTokenEntity().setJwtId(jwtId))));

		RevokedRefreshTokensGenerationEntity revokedRefreshTokensGenerationEntity = new RevokedRefreshTokensGenerationEntity(null, generationId);
		when(revokedRefreshTokensGenerationRepository.persist(revokedRefreshTokensGenerationEntity))
			.thenReturn(Uni.createFrom().failure(new Exception("synthetic exception")));

		/*
		 * Test
		 */
		GetAccessTokenRequest request = new GetAccessTokenRequest()
			.setAcquirerId(acquirerId)
			.setChannel(channel)
			.setClientId(clientId)
			.setRefreshToken(refreshToken)
			.setGrantType(GrantType.REFRESH_TOKEN)
			.setMerchantId(merchantId)
			.setTerminalId(terminalId);

		refreshTokensService.process(request)
			.subscribe()
			.with(
				i -> fail("Failure expected"),
				f -> assertAuthException(f, AuthErrorCode.REFRESH_TOKEN_REVOKED));
	}

	/**
	 * 
	 * @throws JOSEException
	 */
	@Test
	void given_refreshToken_when_itsGenerationIsRevoked_then_getFailure() throws JOSEException {
		/*
		 * Setup
		 */
		Instant now = Instant.now();

		String jwtId = UUID.randomUUID().toString();

		SignedJWT refreshToken = generateRefreshToken(
			JWSAlgorithm.RS256,
			kid,
			jwtId,
			subject,
			new Date(now.toEpochMilli()),
			new Date(now.plus(duration, ChronoUnit.MINUTES).toEpochMilli()),
			acquirerId,
			channel,
			merchantId,
			clientId,
			terminalId,
			generationId,
			Scope.OFFLINE_ACCESS,
			false);

		when(tokenSigner.verify(any(SignedJWT.class)))
			.thenReturn(UniGenerator.item(null));

		when(clientVerifier.verify(clientId, channel, null))
			.thenReturn(UniGenerator.item(new ClientEntity()));

		when(roleFinder.findRoles(acquirerId, channel, clientId, merchantId, terminalId))
			.thenReturn(UniGenerator.item(new SetOfRolesEntity()
				.setRoles(roles)));

		when(tokenSigner.sign(any(JWTClaimsSet.class)))
			.thenAnswer(i -> {
				JWSHeader newHeader = new JWSHeader(
					JWSAlgorithm.RS256,
					null,
					null,
					null,
					null,
					null,
					null,
					null,
					null,
					null,
					kid,
					true,
					null,
					null);

				JWTClaimsSet newPayload = i.getArgument(0);

				SignedJWT newRefreshToken = new SignedJWT(newHeader, newPayload);
				newRefreshToken.sign(signer);

				return UniGenerator.item(newRefreshToken);
			});

		when(revokedRefreshTokensGenerationRepository.findByGenerationId(generationId))
			.thenReturn(Uni.createFrom()
				.item(Optional.of(new RevokedRefreshTokensGenerationEntity().setGenerationId(generationId))));

		when(revokedRefreshTokenRepository.findByJwtId(jwtId))
			.thenReturn(Uni.createFrom()
				.item(Optional.empty()));

		RevokedRefreshTokenEntity revokedRefreshTokenEntity = new RevokedRefreshTokenEntity(null, jwtId);
		when(revokedRefreshTokenRepository.persist(revokedRefreshTokenEntity))
			.thenReturn(Uni.createFrom().item(revokedRefreshTokenEntity));

		/*
		 * Test
		 */
		GetAccessTokenRequest request = new GetAccessTokenRequest()
			.setAcquirerId(acquirerId)
			.setChannel(channel)
			.setClientId(clientId)
			.setRefreshToken(refreshToken)
			.setGrantType(GrantType.REFRESH_TOKEN)
			.setMerchantId(merchantId)
			.setTerminalId(terminalId);

		refreshTokensService.process(request)
			.subscribe()
			.with(
				i -> fail("Failure expected"),
				f -> assertAuthException(f, AuthErrorCode.REFRESH_TOKEN_GENERATION_REVOKED));
	}

	/**
	 * 
	 * @throws JOSEException
	 */
	@Test
	void given_refreshToken_when_itsIdIsNull_then_getFailure() throws JOSEException {
		/*
		 * Setup
		 */
		Instant now = Instant.now();

		SignedJWT refreshToken = generateRefreshToken(
			JWSAlgorithm.RS256,
			kid,
			null,
			subject,
			new Date(now.toEpochMilli()),
			new Date(now.plus(duration, ChronoUnit.MINUTES).toEpochMilli()),
			acquirerId,
			channel,
			merchantId,
			clientId,
			terminalId,
			generationId,
			Scope.OFFLINE_ACCESS,
			false);

		when(tokenSigner.verify(any(SignedJWT.class)))
			.thenReturn(UniGenerator.item(null));

		when(clientVerifier.verify(clientId, channel, null))
			.thenReturn(UniGenerator.item(new ClientEntity()));

		/*
		 * Test
		 */
		GetAccessTokenRequest request = new GetAccessTokenRequest()
			.setAcquirerId(acquirerId)
			.setChannel(channel)
			.setClientId(clientId)
			.setRefreshToken(refreshToken)
			.setGrantType(GrantType.REFRESH_TOKEN)
			.setMerchantId(merchantId)
			.setTerminalId(terminalId);

		refreshTokensService.process(request)
			.subscribe()
			.with(
				i -> fail("Failure expected"),
				f -> assertAuthException(f, AuthErrorCode.REFRESH_TOKEN_ID_MUST_NOT_BE_NULL));
	}

	/**
	 * 
	 * @throws JOSEException
	 */
	@Test
	void given_refreshToken_when_itsGenerationIsNull_then_getFailure() throws JOSEException {
		/*
		 * Setup
		 */
		Instant now = Instant.now();

		String jwtId = UUID.randomUUID().toString();

		SignedJWT refreshToken = generateRefreshToken(
			JWSAlgorithm.RS256,
			kid,
			jwtId,
			subject,
			new Date(now.toEpochMilli()),
			new Date(now.plus(duration, ChronoUnit.MINUTES).toEpochMilli()),
			acquirerId,
			channel,
			merchantId,
			clientId,
			terminalId,
			null,
			Scope.OFFLINE_ACCESS,
			false);

		when(tokenSigner.verify(any(SignedJWT.class)))
			.thenReturn(UniGenerator.item(null));

		when(clientVerifier.verify(clientId, channel, null))
			.thenReturn(UniGenerator.item(new ClientEntity()));

		/*
		 * Test
		 */
		GetAccessTokenRequest request = new GetAccessTokenRequest()
			.setAcquirerId(acquirerId)
			.setChannel(channel)
			.setClientId(clientId)
			.setRefreshToken(refreshToken)
			.setGrantType(GrantType.REFRESH_TOKEN)
			.setMerchantId(merchantId)
			.setTerminalId(terminalId);

		refreshTokensService.process(request)
			.subscribe()
			.with(
				i -> fail("Failure expected"),
				f -> assertAuthException(f, AuthErrorCode.REFRESH_TOKEN_GENERATION_ID_MUST_NOT_BE_NULL));
	}

	/**
	 * 
	 * @throws JOSEException
	 */
	@Test
	void given_refreshToken_when_itsLocationClaimIsInvalid_then_getFailure() throws JOSEException {
		/*
		 * Current refresh token
		 */
		Instant now = Instant.now();

		String jwtId = UUID.randomUUID().toString();

		SignedJWT currentRefreshToken = generateRefreshToken(
			JWSAlgorithm.RS256,
			kid,
			jwtId,
			subject,
			new Date(now.toEpochMilli()),
			new Date(now.plus(duration, ChronoUnit.MINUTES).toEpochMilli()),
			acquirerId,
			channel,
			merchantId,
			clientId,
			terminalId,
			generationId,
			Scope.OFFLINE_ACCESS,
			"invalid location");

		/*
		 * Mocks
		 */
		when(tokenSigner.verify(any(SignedJWT.class)))
			.thenReturn(UniGenerator.item(null));

		when(clientVerifier.verify(clientId, channel, null))
			.thenReturn(UniGenerator.item(new ClientEntity()));

		when(roleFinder.findRoles(acquirerId, channel, clientId, merchantId, terminalId))
			.thenReturn(UniGenerator.item(new SetOfRolesEntity()
				.setRoles(roles)));

		when(tokenSigner.sign(any(JWTClaimsSet.class)))
			.thenAnswer(i -> {
				JWSHeader newHeader = new JWSHeader(
					JWSAlgorithm.RS256,
					null,
					null,
					null,
					null,
					null,
					null,
					null,
					null,
					null,
					kid,
					true,
					null,
					null);

				JWTClaimsSet newPayload = i.getArgument(0);

				SignedJWT newRefreshToken = new SignedJWT(newHeader, newPayload);
				newRefreshToken.sign(signer);

				return UniGenerator.item(newRefreshToken);
			});

		when(revokedRefreshTokensGenerationRepository.findByGenerationId(generationId))
			.thenReturn(Uni.createFrom()
				.item(Optional.empty()));

		when(revokedRefreshTokenRepository.findByJwtId(jwtId))
			.thenReturn(Uni.createFrom()
				.item(Optional.empty()));

		RevokedRefreshTokenEntity revokedRefreshTokenEntity = new RevokedRefreshTokenEntity(null, jwtId);
		when(revokedRefreshTokenRepository.persist(revokedRefreshTokenEntity))
			.thenReturn(Uni.createFrom().item(revokedRefreshTokenEntity));

		/*
		 * Test
		 */
		GetAccessTokenRequest request = new GetAccessTokenRequest()
			.setAcquirerId(acquirerId)
			.setChannel(channel)
			.setMerchantId(merchantId)
			.setTerminalId(terminalId)
			.setClientId(clientId)
			.setGrantType(GrantType.REFRESH_TOKEN)
			.setRefreshToken(currentRefreshToken);

		refreshTokensService.process(request)
			.subscribe()
			.with(
				i -> fail("Failure expected"),
				f -> assertAuthError(f, AuthErrorCode.ERROR_PARSING_TOKEN));
	}
	
	/**
	 * 
	 * @throws JOSEException
	 */
	@Test
	void given_refreshToken_when_revocationFail_then_getFailure() throws JOSEException {
		/*
		 * Current refresh token
		 */
		Instant now = Instant.now();

		String jwtId = UUID.randomUUID().toString();

		SignedJWT currentRefreshToken = generateRefreshToken(
			JWSAlgorithm.RS256,
			kid,
			jwtId,
			subject,
			new Date(now.toEpochMilli()),
			new Date(now.plus(duration, ChronoUnit.MINUTES).toEpochMilli()),
			acquirerId,
			channel,
			merchantId,
			clientId,
			terminalId,
			generationId,
			Scope.OFFLINE_ACCESS,
			false);

		/*
		 * Mocks
		 */
		when(tokenSigner.verify(any(SignedJWT.class)))
			.thenReturn(UniGenerator.item(null));

		when(clientVerifier.verify(clientId, channel, null))
			.thenReturn(UniGenerator.item(new ClientEntity()));

		when(roleFinder.findRoles(acquirerId, channel, clientId, merchantId, terminalId))
			.thenReturn(UniGenerator.item(new SetOfRolesEntity()
				.setRoles(roles)));

		when(tokenSigner.sign(any(JWTClaimsSet.class)))
			.thenAnswer(i -> {
				JWSHeader newHeader = new JWSHeader(
					JWSAlgorithm.RS256,
					null,
					null,
					null,
					null,
					null,
					null,
					null,
					null,
					null,
					kid,
					true,
					null,
					null);

				JWTClaimsSet newPayload = i.getArgument(0);

				SignedJWT newRefreshToken = new SignedJWT(newHeader, newPayload);
				newRefreshToken.sign(signer);

				return UniGenerator.item(newRefreshToken);
			});

		when(revokedRefreshTokensGenerationRepository.findByGenerationId(generationId))
			.thenReturn(Uni.createFrom()
				.item(Optional.empty()));

		when(revokedRefreshTokenRepository.findByJwtId(jwtId))
			.thenReturn(Uni.createFrom()
				.item(Optional.empty()));

		RevokedRefreshTokenEntity revokedRefreshTokenEntity = new RevokedRefreshTokenEntity(null, jwtId);
		when(revokedRefreshTokenRepository.persist(revokedRefreshTokenEntity))
			.thenReturn(Uni.createFrom().failure(new Exception("synthetic exception")));

		/*
		 * Test
		 */
		GetAccessTokenRequest request = new GetAccessTokenRequest()
			.setAcquirerId(acquirerId)
			.setChannel(channel)
			.setMerchantId(merchantId)
			.setTerminalId(terminalId)
			.setClientId(clientId)
			.setGrantType(GrantType.REFRESH_TOKEN)
			.setRefreshToken(currentRefreshToken);

		refreshTokensService.process(request)
			.subscribe()
			.with(
				i -> fail("Failure expected"),
				f -> assertAuthError(f, AuthErrorCode.ERROR_REVOKING_REFRESH_TOKEN));
	}
}