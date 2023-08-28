/*
 * AzureTokenSignerTest.java
 *
 * 2 ago 2023
 */
package it.pagopa.swclient.mil.auth.azurekeyvault.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.text.ParseException;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.List;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.util.Base64URL;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.mutiny.Context;
import io.smallrye.mutiny.ItemWithContext;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.helpers.test.UniAssertSubscriber;
import it.pagopa.swclient.mil.auth.azurekeyvault.bean.GetAccessTokenResponse;
import it.pagopa.swclient.mil.auth.azurekeyvault.bean.SignRequest;
import it.pagopa.swclient.mil.auth.azurekeyvault.bean.SignResponse;
import it.pagopa.swclient.mil.auth.azurekeyvault.bean.VerifySignatureRequest;
import it.pagopa.swclient.mil.auth.azurekeyvault.bean.VerifySignatureResponse;
import it.pagopa.swclient.mil.auth.azurekeyvault.client.AzureAuthClient;
import it.pagopa.swclient.mil.auth.azurekeyvault.client.AzureKeyVaultClient;
import it.pagopa.swclient.mil.auth.azurekeyvault.util.SignedJWTFactory;
import it.pagopa.swclient.mil.auth.bean.KeyType;
import it.pagopa.swclient.mil.auth.bean.KeyUse;
import it.pagopa.swclient.mil.auth.service.TokenSigner;
import it.pagopa.swclient.mil.auth.util.AuthError;
import it.pagopa.swclient.mil.auth.util.AuthException;
import jakarta.inject.Inject;

/**
 * 
 */
@QuarkusTest
@TestInstance(Lifecycle.PER_CLASS)
class AzureTokenSignerTest {
	/*
	 * 
	 */
	@Inject
	TokenSigner tokenSigner;

	/*
	 * 
	 */
	@InjectMock
	AzureKeyFinder keyFinder;

	/*
	 * 
	 */
	@InjectMock
	@RestClient
	AzureKeyVaultClient keyVaultClient;

	/*
	 * 
	 */
	@InjectMock
	@RestClient
	AzureAuthClient authClient;

	/*
	 * 
	 */
	private static final String KEY_NAME = "KEY_NAME";
	private static final String KEY_VERSION = "KEY_VERSION";
	private static final String KID = KEY_NAME + "/" + KEY_VERSION;
	private static final String MODULUS = "AKnFsF5Y16TB9qkmoOyDXG3ulenUWYoW78U7mcGBoYKRpMlswxhc_ZiKcC65vIrCP6hbS5Cx88IbQG2DWH-nE329OLzUbzcdraDLR-7V2BX0nNwmwXxhkd4ofzzjKyhWjV8AkxFpqJPtFG09YCyCpaC8YluVPbHUpWJ1wrOsavdc_YM1W1XuaGvJv4SkilM8vBa81zOLEVhbEE5msHxPNLwVyC_0PIE6OFL9RY4YP1U1q7gjTMmKDc9qgEYkdziMnlxWp_EkKTZOERbEatP0fditFt-zWKlXw0qO4FKFlmj9n5tbB55vaopB71Kv6LcsAY1Q-fgOuoM41HldLppzfDOPwLGyCQF9ODJt1xaKkup6i_BxZum7-QckibwPaj3ODZbYsPuNZ_npQiR6NJZ_q_31YMlyuGdqltawluYLJidw3EzkpTN__bHdio892WbY29PRwbrG486IJ_88qP3lWs1TfzohVa1czUOZwQHqp0ixVBi_SK3jICk-V65DbwzgS5zwBFaqfWO3XVOf6tmWFMZ6ly7wtOnYWoMR15rudsD5xXWwqE-s7IP1lVZuIOdMfLH7-1Pgn-YJuPsBLbZri9_M4KtflYbqnuDckSyFNBynTwoSvSSuBhpkmNgiSQ-WBXHHss5Wy-pr-YjNK7JYppPOHvfHSY96XnJl9SPWcnwx";
	private static final String PRIVATE_EXPONENT = "IlITaUNTFtzaUVA8lIuqxhOHLW3vCv4_ixMVLnwXC0cHteudliGIZ8vGyX9laPTDezS3lkEPSuSI9gqpO6cqRs9Xtr7IW-9NQDYQLO2AoVGh21SfZVZxL2Tm8gdnnGBA9J1wXcMLIBp7uGjBtkXUF2Y2CRcm0XowU_MEASAgQLEFE_8Xn4vSgsXWiIld6F1dFcinxaT9xOul5H-Yeozll4dcwKsCh0pehBJs-wCWXxK6S_-g4JZe29lHJMbu7hjpU7f1_AcIKNEH3d8nzID-5ux49RCz4goasgonua8FXOS23Sh-Jg6WjmwtZj0nEc6c4rVlzzqlBG2a8I0ApJsnlo2RK1E-XftVNip52Bsb9jRKGNjNZP3VOgAdLg-py8HVU3sxn95yJRN6AF7S8a0Jnb6uAzxagmfZqLe1ykswBPJWPP2dyQivb59CMcmHQoOK-up_Tt1P6oIltTCHEg0z79GVatWvikmfrN0tLrMJl8iR_67IDvehkp0r4DoFQNkhKNm5moFGFJWqkWZSpi3OUhPYZNmWPJTf1CxM3li6hNqRuGLCe-M9-gyZ01U9j9sUbV3xaK6kXhDPje2JB-0FkZuU7ewmpmQ5ETuRYrXyQa6b6VyxNwYokvgAGxdQ8leT2jxq_UVoMw-C0JU8tOC1fkXxClfOsSfCKx5WQXIKFrU=";
	private static final String PUBLIC_EXPONENT = "AQAB";
	private static final long KEY_DURATION = 5 * 60;

	/*
	 * 
	 */
	private static final long TOKEN_DURATION = 5 * 60;
	private static final String ACQUIRER_ID = "ACQUIRER_ID";
	private static final String CHANNEL = "CHANNEL";
	private static final String MERCHANT_ID = "MERCHANT_ID";
	private static final String CLIENT_ID = "ACQUIRER_ID";
	private static final String TERMINAL_ID = "TERMINAL_ID";
	private static final String SCOPES = "SCOPES";
	private static final List<String> GROUPS = List.of("GROUP_1", "GROUP_2");

	/*
	 * 
	 */
	private PrivateKey privateKey;
	private PublicKey publicKey;

	/**
	 * 
	 */
	@BeforeAll
	void init() throws NoSuchAlgorithmException, InvalidKeySpecException {
		BigInteger modulus = new BigInteger(1, Base64.getUrlDecoder().decode(MODULUS));
		BigInteger privateExponent = new BigInteger(1, Base64.getUrlDecoder().decode(PRIVATE_EXPONENT));
		BigInteger publicExponent = new BigInteger(1, Base64.getUrlDecoder().decode(PUBLIC_EXPONENT));

		KeyFactory factory = KeyFactory.getInstance("RSA");
		privateKey = factory.generatePrivate(new RSAPrivateKeySpec(modulus, privateExponent));
		publicKey = factory.generatePublic(new RSAPublicKeySpec(modulus, publicExponent));
	}

	/**
	 * Test method for
	 * {@link it.pagopa.swclient.mil.auth.azurekeyvault.service.AzureTokenSigner#sign(com.nimbusds.jwt.JWTClaimsSet)}.
	 * 
	 * @throws JOSEException
	 */
	@Test
	void testSign() throws JOSEException {
		/*
		 * Expected result.
		 */
		Instant now = Instant.now();
		JWSHeader header = new JWSHeader(JWSAlgorithm.RS256, null, null, null, null, null, null, null, null, null, KID, true, null, null);
		JWTClaimsSet payload = new JWTClaimsSet.Builder()
			.subject(CLIENT_ID)
			.issueTime(new Date(now.toEpochMilli()))
			.expirationTime(new Date(now.toEpochMilli() + TOKEN_DURATION * 1000))
			.claim("acquirerId", ACQUIRER_ID)
			.claim("channel", CHANNEL)
			.claim("merchantId", MERCHANT_ID)
			.claim("clientId", CLIENT_ID)
			.claim("terminalId", TERMINAL_ID)
			.claim("scope", SCOPES)
			.claim("groups", GROUPS)
			.build();
		SignedJWT expectedToken = new SignedJWT(header, payload);
		JWSSigner signer = new RSASSASigner(privateKey);
		expectedToken.sign(signer);
		String[] components = expectedToken.serialize().split("\\.");
		String expectedSignatureBase64 = components[2];

		/*
		 * Setup.
		 */
		when(keyFinder.findValidPublicKeyWithGreatestExpiration())
			.thenReturn(
				Uni.createFrom().item(
					new ItemWithContext<>(
						Context.of().put(AzureKeyFinder.TOKEN, "this_is_the_token"),
						new it.pagopa.swclient.mil.auth.bean.PublicKey(
							PUBLIC_EXPONENT,
							KeyUse.sig,
							KID,
							MODULUS,
							KeyType.RSA,
							now.getEpochSecond() + KEY_DURATION,
							now.getEpochSecond()))));

		when(keyVaultClient.sign(anyString(), eq(KEY_NAME), eq(KEY_VERSION), any(SignRequest.class)))
			.thenReturn(Uni.createFrom().item(new SignResponse(KID, expectedSignatureBase64)));

		/*
		 * Test.
		 */
		SignedJWT actualToken = tokenSigner.sign(payload)
			.subscribe()
			.withSubscriber(UniAssertSubscriber.create())
			.assertCompleted()
			.getItem();

		components = actualToken.serialize().split("\\.");
		String actualSignatureBase64 = components[2];

		assertEquals(expectedSignatureBase64, actualSignatureBase64);
	}

	/**
	 * Test method for
	 * {@link it.pagopa.swclient.mil.auth.azurekeyvault.service.AzureTokenSigner#sign(com.nimbusds.jwt.JWTClaimsSet)}.
	 * 
	 * @throws JOSEException
	 */
	@Test
	void testSignWithNoSuchAlgorithmException() throws JOSEException {
		/*
		 * Expected result.
		 */
		Instant now = Instant.now();
		JWSHeader header = new JWSHeader(JWSAlgorithm.RS256, null, null, null, null, null, null, null, null, null, KID, true, null, null);
		JWTClaimsSet payload = new JWTClaimsSet.Builder()
			.subject(CLIENT_ID)
			.issueTime(new Date(now.toEpochMilli()))
			.expirationTime(new Date(now.toEpochMilli() + TOKEN_DURATION * 1000))
			.claim("acquirerId", ACQUIRER_ID)
			.claim("channel", CHANNEL)
			.claim("merchantId", MERCHANT_ID)
			.claim("clientId", CLIENT_ID)
			.claim("terminalId", TERMINAL_ID)
			.claim("scope", SCOPES)
			.claim("groups", GROUPS)
			.build();
		SignedJWT expectedToken = new SignedJWT(header, payload);
		JWSSigner signer = new RSASSASigner(privateKey);
		expectedToken.sign(signer);
		String[] components = expectedToken.serialize().split("\\.");
		String expectedSignatureBase64 = components[2];

		/*
		 * Setup.
		 */
		when(keyFinder.findValidPublicKeyWithGreatestExpiration())
			.thenReturn(
				Uni.createFrom().item(
					new ItemWithContext<>(
						Context.of().put(AzureKeyFinder.TOKEN, "this_is_the_token"),
						new it.pagopa.swclient.mil.auth.bean.PublicKey(
							PUBLIC_EXPONENT,
							KeyUse.sig,
							KID,
							MODULUS,
							KeyType.RSA,
							now.getEpochSecond() + KEY_DURATION,
							now.getEpochSecond()))));

		when(keyVaultClient.sign(anyString(), eq(KEY_NAME), eq(KEY_VERSION), any(SignRequest.class)))
			.thenReturn(Uni.createFrom().item(new SignResponse(KID, expectedSignatureBase64)));

		try (MockedStatic<MessageDigest> digest = Mockito.mockStatic(MessageDigest.class)) {
			digest.when(() -> MessageDigest.getInstance("SHA256"))
				.thenThrow(NoSuchAlgorithmException.class);

			/*
			 * Test.
			 */
			tokenSigner.sign(payload)
				.subscribe()
				.withSubscriber(UniAssertSubscriber.create())
				.assertFailedWith(AuthError.class);
		}
	}

	/**
	 * Test method for
	 * {@link it.pagopa.swclient.mil.auth.azurekeyvault.service.AzureTokenSigner#sign(com.nimbusds.jwt.JWTClaimsSet)}.
	 * 
	 * @throws JOSEException
	 */
	@Test
	void testSignWithParseException() throws JOSEException {
		System.out.println("Test AzureTokenSigner.sign(...) with ParseException");

		/*
		 * Expected result.
		 */
		Instant now = Instant.now();
		JWSHeader header = new JWSHeader(JWSAlgorithm.RS256, null, null, null, null, null, null, null, null, null, KID, true, null, null);
		JWTClaimsSet payload = new JWTClaimsSet.Builder()
			.subject(CLIENT_ID)
			.issueTime(new Date(now.toEpochMilli()))
			.expirationTime(new Date(now.toEpochMilli() + TOKEN_DURATION * 1000))
			.claim("acquirerId", ACQUIRER_ID)
			.claim("channel", CHANNEL)
			.claim("merchantId", MERCHANT_ID)
			.claim("clientId", CLIENT_ID)
			.claim("terminalId", TERMINAL_ID)
			.claim("scope", SCOPES)
			.claim("groups", GROUPS)
			.build();
		SignedJWT expectedToken = new SignedJWT(header, payload);
		JWSSigner signer = new RSASSASigner(privateKey);
		expectedToken.sign(signer);
		String[] components = expectedToken.serialize().split("\\.");
		String expectedSignatureBase64 = components[2];

		/*
		 * Setup.
		 */
		when(keyFinder.findValidPublicKeyWithGreatestExpiration())
			.thenReturn(
				Uni.createFrom().item(
					new ItemWithContext<>(
						Context.of().put(AzureKeyFinder.TOKEN, "this_is_the_token"),
						new it.pagopa.swclient.mil.auth.bean.PublicKey(
							PUBLIC_EXPONENT,
							KeyUse.sig,
							KID,
							MODULUS,
							KeyType.RSA,
							now.getEpochSecond() + KEY_DURATION,
							now.getEpochSecond()))));

		when(keyVaultClient.sign(anyString(), eq(KEY_NAME), eq(KEY_VERSION), any(SignRequest.class)))
			.thenReturn(Uni.createFrom().item(new SignResponse(KID, expectedSignatureBase64)));

		try (MockedStatic<SignedJWTFactory> factory = Mockito.mockStatic(SignedJWTFactory.class)) {
			factory.when(() -> SignedJWTFactory.createInstance(any(Base64URL.class), any(Base64URL.class), any(Base64URL.class)))
				.thenThrow(new ParseException("synthetic exception", 0));

			/*
			 * Test.
			 */
			tokenSigner.sign(payload)
				.subscribe()
				.withSubscriber(UniAssertSubscriber.create())
				.assertFailedWith(AuthError.class);
		}
	}

	/**
	 * Test method for
	 * {@link it.pagopa.swclient.mil.auth.azurekeyvault.service.AzureTokenSigner#verify(com.nimbusds.jwt.SignedJWT)}.
	 * 
	 * @throws JOSEException
	 */
	@Test
	void testVerify() throws JOSEException {
		/*
		 * Expected result.
		 */
		Instant now = Instant.now();
		JWSHeader header = new JWSHeader(JWSAlgorithm.RS256, null, null, null, null, null, null, null, null, null, KID, true, null, null);
		JWTClaimsSet payload = new JWTClaimsSet.Builder()
			.subject(CLIENT_ID)
			.issueTime(new Date(now.toEpochMilli()))
			.expirationTime(new Date(now.toEpochMilli() + TOKEN_DURATION * 1000))
			.claim("acquirerId", ACQUIRER_ID)
			.claim("channel", CHANNEL)
			.claim("merchantId", MERCHANT_ID)
			.claim("clientId", CLIENT_ID)
			.claim("terminalId", TERMINAL_ID)
			.claim("scope", SCOPES)
			.claim("groups", GROUPS)
			.build();
		SignedJWT token = new SignedJWT(header, payload);
		JWSSigner signer = new RSASSASigner(privateKey);
		token.sign(signer);

		/*
		 * Setup.
		 */
		when(authClient.getAccessToken(anyString(), anyString(), anyString(), anyString(), anyString()))
			.thenReturn(Uni.createFrom().item(new GetAccessTokenResponse("Bearer", 3599, 3599, "this_is_the_token")));

		when(keyVaultClient.verifySignature(anyString(), eq(KEY_NAME), eq(KEY_VERSION), any(VerifySignatureRequest.class)))
			.thenReturn(Uni.createFrom().item(new VerifySignatureResponse(Boolean.TRUE)));

		/*
		 * Test.
		 */
		tokenSigner.verify(token)
			.subscribe()
			.withSubscriber(UniAssertSubscriber.create())
			.assertCompleted()
			.assertItem(null);
	}

	/**
	 * Test method for
	 * {@link it.pagopa.swclient.mil.auth.azurekeyvault.service.AzureTokenSigner#verify(com.nimbusds.jwt.SignedJWT)}.
	 * 
	 * @throws JOSEException
	 */
	@Test
	void testVerifyWithFailedVerification() throws JOSEException {
		/*
		 * Expected result.
		 */
		Instant now = Instant.now();
		JWSHeader header = new JWSHeader(JWSAlgorithm.RS256, null, null, null, null, null, null, null, null, null, KID, true, null, null);
		JWTClaimsSet payload = new JWTClaimsSet.Builder()
			.subject(CLIENT_ID)
			.issueTime(new Date(now.toEpochMilli()))
			.expirationTime(new Date(now.toEpochMilli() + TOKEN_DURATION * 1000))
			.claim("acquirerId", ACQUIRER_ID)
			.claim("channel", CHANNEL)
			.claim("merchantId", MERCHANT_ID)
			.claim("clientId", CLIENT_ID)
			.claim("terminalId", TERMINAL_ID)
			.claim("scope", SCOPES)
			.claim("groups", GROUPS)
			.build();
		SignedJWT token = new SignedJWT(header, payload);
		JWSSigner signer = new RSASSASigner(privateKey);
		token.sign(signer);

		/*
		 * Setup.
		 */
		when(authClient.getAccessToken(anyString(), anyString(), anyString(), anyString(), anyString()))
			.thenReturn(Uni.createFrom().item(new GetAccessTokenResponse("Bearer", 3599, 3599, "this_is_the_token")));

		when(keyVaultClient.verifySignature(anyString(), eq(KEY_NAME), eq(KEY_VERSION), any(VerifySignatureRequest.class)))
			.thenReturn(Uni.createFrom().item(new VerifySignatureResponse(Boolean.FALSE)));

		/*
		 * Test.
		 */
		tokenSigner.verify(token)
			.subscribe()
			.withSubscriber(UniAssertSubscriber.create())
			.assertFailedWith(AuthException.class);
	}

	/**
	 * Test method for
	 * {@link it.pagopa.swclient.mil.auth.azurekeyvault.service.AzureTokenSigner#verify(com.nimbusds.jwt.SignedJWT)}.
	 * 
	 * @throws JOSEException
	 */
	@Test
	void testVerifyWithNullAccessToken() throws JOSEException {
		/*
		 * Expected result.
		 */
		Instant now = Instant.now();
		JWSHeader header = new JWSHeader(JWSAlgorithm.RS256, null, null, null, null, null, null, null, null, null, KID, true, null, null);
		JWTClaimsSet payload = new JWTClaimsSet.Builder()
			.subject(CLIENT_ID)
			.issueTime(new Date(now.toEpochMilli()))
			.expirationTime(new Date(now.toEpochMilli() + TOKEN_DURATION * 1000))
			.claim("acquirerId", ACQUIRER_ID)
			.claim("channel", CHANNEL)
			.claim("merchantId", MERCHANT_ID)
			.claim("clientId", CLIENT_ID)
			.claim("terminalId", TERMINAL_ID)
			.claim("scope", SCOPES)
			.claim("groups", GROUPS)
			.build();
		SignedJWT token = new SignedJWT(header, payload);
		JWSSigner signer = new RSASSASigner(privateKey);
		token.sign(signer);

		/*
		 * Setup.
		 */
		when(authClient.getAccessToken(anyString(), anyString(), anyString(), anyString(), anyString()))
			.thenReturn(Uni.createFrom().item(new GetAccessTokenResponse("Bearer", 3599, 3599, null)));

		when(keyVaultClient.verifySignature(anyString(), eq(KEY_NAME), eq(KEY_VERSION), any(VerifySignatureRequest.class)))
			.thenReturn(Uni.createFrom().item(new VerifySignatureResponse(Boolean.TRUE)));

		/*
		 * Test.
		 */
		tokenSigner.verify(token)
			.subscribe()
			.withSubscriber(UniAssertSubscriber.create())
			.assertFailedWith(AuthError.class);
	}

	/**
	 * Test method for
	 * {@link it.pagopa.swclient.mil.auth.azurekeyvault.service.AzureTokenSigner#verify(com.nimbusds.jwt.SignedJWT)}.
	 * 
	 * @throws JOSEException
	 */
	@Test
	void testVerifyWithNoSuchAlgorithmException() throws JOSEException {
		/*
		 * Expected result.
		 */
		Instant now = Instant.now();
		JWSHeader header = new JWSHeader(JWSAlgorithm.RS256, null, null, null, null, null, null, null, null, null, KID, true, null, null);
		JWTClaimsSet payload = new JWTClaimsSet.Builder()
			.subject(CLIENT_ID)
			.issueTime(new Date(now.toEpochMilli()))
			.expirationTime(new Date(now.toEpochMilli() + TOKEN_DURATION * 1000))
			.claim("acquirerId", ACQUIRER_ID)
			.claim("channel", CHANNEL)
			.claim("merchantId", MERCHANT_ID)
			.claim("clientId", CLIENT_ID)
			.claim("terminalId", TERMINAL_ID)
			.claim("scope", SCOPES)
			.claim("groups", GROUPS)
			.build();
		SignedJWT token = new SignedJWT(header, payload);
		JWSSigner signer = new RSASSASigner(privateKey);
		token.sign(signer);

		/*
		 * Setup.
		 */
		when(authClient.getAccessToken(anyString(), anyString(), anyString(), anyString(), anyString()))
			.thenReturn(Uni.createFrom().item(new GetAccessTokenResponse("Bearer", 3599, 3599, "this_is_the_token")));

		when(keyVaultClient.verifySignature(anyString(), eq(KEY_NAME), eq(KEY_VERSION), any(VerifySignatureRequest.class)))
			.thenReturn(Uni.createFrom().item(new VerifySignatureResponse(Boolean.TRUE)));

		try (MockedStatic<MessageDigest> digest = Mockito.mockStatic(MessageDigest.class)) {
			digest.when(() -> MessageDigest.getInstance("SHA256"))
				.thenThrow(NoSuchAlgorithmException.class);

			/*
			 * Test.
			 */
			tokenSigner.verify(token)
				.subscribe()
				.withSubscriber(UniAssertSubscriber.create())
				.assertFailedWith(AuthError.class);
		}
	}

	public static void main(String[] args) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException, InvalidKeySpecException {
		String s = "http://ciccio//keys/";
		String x = s.replaceAll("//keys", "/keys");
		System.out.println(x);
	}
}
