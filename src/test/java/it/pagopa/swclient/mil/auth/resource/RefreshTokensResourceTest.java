/*
 * RefreshTokensResourceTest.java
 *
 * 28 ago 2023
 */
package it.pagopa.swclient.mil.auth.resource;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateKeySpec;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.List;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.TestInstance;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import io.quarkus.test.InjectMock;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import it.pagopa.swclient.mil.auth.AuthErrorCode;
import it.pagopa.swclient.mil.auth.azure.auth.bean.GetAccessTokenResponse;
import it.pagopa.swclient.mil.auth.azure.auth.client.AzureAuthClient;
import it.pagopa.swclient.mil.auth.azure.keyvault.bean.BasicKey;
import it.pagopa.swclient.mil.auth.azure.keyvault.bean.DetailedKey;
import it.pagopa.swclient.mil.auth.azure.keyvault.bean.GetKeysResponse;
import it.pagopa.swclient.mil.auth.azure.keyvault.bean.KeyDetails;
import it.pagopa.swclient.mil.auth.azure.keyvault.bean.SignRequest;
import it.pagopa.swclient.mil.auth.azure.keyvault.bean.SignResponse;
import it.pagopa.swclient.mil.auth.azure.keyvault.bean.VerifySignatureRequest;
import it.pagopa.swclient.mil.auth.azure.keyvault.bean.VerifySignatureResponse;
import it.pagopa.swclient.mil.auth.azure.keyvault.client.AzureKeyVaultClient;
import it.pagopa.swclient.mil.auth.azure.storage.client.AzureAuthDataRepositoryClient;
import it.pagopa.swclient.mil.auth.bean.ClaimName;
import it.pagopa.swclient.mil.auth.bean.Client;
import it.pagopa.swclient.mil.auth.bean.FormParamName;
import it.pagopa.swclient.mil.auth.bean.GrantType;
import it.pagopa.swclient.mil.auth.bean.HeaderParamName;
import it.pagopa.swclient.mil.auth.bean.JsonPropertyName;
import it.pagopa.swclient.mil.auth.bean.Role;
import it.pagopa.swclient.mil.auth.bean.Scope;
import it.pagopa.swclient.mil.auth.bean.TokenType;
import it.pagopa.swclient.mil.auth.util.UniGenerator;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyAttributes;
import it.pagopa.swclient.mil.bean.Channel;
import jakarta.ws.rs.core.MediaType;

/**
 * @author Antonio Tarricone
 */
@QuarkusTest
@TestHTTPEndpoint(TokenResource.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RefreshTokensResourceTest {
	/*
	 *
	 */
	private static final String ACQUIRER_ID = "4585625";
	private static final String MERCHANT_ID = "28405fHfk73x88D";
	private static final String TERMINAL_ID = "12345678";
	private static final String CLIENT_ID = "3965df56-ca9a-49e5-97e8-061433d4a25b";
	private static final String DESCRIPTION = "VAS Layer";
	private static final List<String> ROLES = List.of("NoticePayer", "SlavePos");

	/*
	 *
	 */
	private static final String WRONG_SCOPE = "other_scope";
	private static final String INVALID_REFRESH_TOKEN = "1.1.1";

	/*
	 *
	 */
	private static final long AZURE_TOKEN_DURATION = 3599;
	private static final String AZURE_TOKEN = "this_is_the_token";

	/*
	 *
	 */
	@ConfigProperty(name = "quarkus.rest-client.azure-key-vault-api.url")
	String vaultBaseUrl;

	/*
	 * 
	 */
	private String keyUrl;

	/*
	 * 
	 */
	private static final String KEY_NAME = "auth0709643f49394529b92c19a68c8e184a";
	private static final String KEY_VERSION = "6581c704deda4979943c3b34468df7c2";
	private static final String KID = KEY_NAME + "/" + KEY_VERSION;
	private static final String KEY_RECOVERY_LEVEL = "Purgeable";
	private static final String KEY_TYPE = "RSA";
	private static final String[] KEY_OPS = new String[] {
		"verify", "sign"
	};
	private static final String MODULUS = "AKnFsF5Y16TB9qkmoOyDXG3ulenUWYoW78U7mcGBoYKRpMlswxhc_ZiKcC65vIrCP6hbS5Cx88IbQG2DWH-nE329OLzUbzcdraDLR-7V2BX0nNwmwXxhkd4ofzzjKyhWjV8AkxFpqJPtFG09YCyCpaC8YluVPbHUpWJ1wrOsavdc_YM1W1XuaGvJv4SkilM8vBa81zOLEVhbEE5msHxPNLwVyC_0PIE6OFL9RY4YP1U1q7gjTMmKDc9qgEYkdziMnlxWp_EkKTZOERbEatP0fditFt-zWKlXw0qO4FKFlmj9n5tbB55vaopB71Kv6LcsAY1Q-fgOuoM41HldLppzfDOPwLGyCQF9ODJt1xaKkup6i_BxZum7-QckibwPaj3ODZbYsPuNZ_npQiR6NJZ_q_31YMlyuGdqltawluYLJidw3EzkpTN__bHdio892WbY29PRwbrG486IJ_88qP3lWs1TfzohVa1czUOZwQHqp0ixVBi_SK3jICk-V65DbwzgS5zwBFaqfWO3XVOf6tmWFMZ6ly7wtOnYWoMR15rudsD5xXWwqE-s7IP1lVZuIOdMfLH7-1Pgn-YJuPsBLbZri9_M4KtflYbqnuDckSyFNBynTwoSvSSuBhpkmNgiSQ-WBXHHss5Wy-pr-YjNK7JYppPOHvfHSY96XnJl9SPWcnwx";
	private static final String PRIVATE_EXPONENT = "IlITaUNTFtzaUVA8lIuqxhOHLW3vCv4_ixMVLnwXC0cHteudliGIZ8vGyX9laPTDezS3lkEPSuSI9gqpO6cqRs9Xtr7IW-9NQDYQLO2AoVGh21SfZVZxL2Tm8gdnnGBA9J1wXcMLIBp7uGjBtkXUF2Y2CRcm0XowU_MEASAgQLEFE_8Xn4vSgsXWiIld6F1dFcinxaT9xOul5H-Yeozll4dcwKsCh0pehBJs-wCWXxK6S_-g4JZe29lHJMbu7hjpU7f1_AcIKNEH3d8nzID-5ux49RCz4goasgonua8FXOS23Sh-Jg6WjmwtZj0nEc6c4rVlzzqlBG2a8I0ApJsnlo2RK1E-XftVNip52Bsb9jRKGNjNZP3VOgAdLg-py8HVU3sxn95yJRN6AF7S8a0Jnb6uAzxagmfZqLe1ykswBPJWPP2dyQivb59CMcmHQoOK-up_Tt1P6oIltTCHEg0z79GVatWvikmfrN0tLrMJl8iR_67IDvehkp0r4DoFQNkhKNm5moFGFJWqkWZSpi3OUhPYZNmWPJTf1CxM3li6hNqRuGLCe-M9-gyZ01U9j9sUbV3xaK6kXhDPje2JB-0FkZuU7ewmpmQ5ETuRYrXyQa6b6VyxNwYokvgAGxdQ8leT2jxq_UVoMw-C0JU8tOC1fkXxClfOsSfCKx5WQXIKFrU=";
	private static final String PUBLIC_EXPONENT = "AQAB";
	private static final String EXPECTED_SIGNATURE = "expected_signature";

	/*
	 *
	 */
	@InjectMock
	@RestClient
	AzureAuthDataRepositoryClient repository;

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

	/**
	 *
	 */
	@BeforeAll
	void setup() {
		keyUrl = vaultBaseUrl + (vaultBaseUrl.endsWith("/") ? "keys/" : "/keys/");
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

	@Test
	void testOk() throws InvalidKeySpecException, NoSuchAlgorithmException, JOSEException {
		/*
		 * Client repository setup.
		 */
		when(repository.getClient(AZURE_TOKEN, CLIENT_ID))
			.thenReturn(UniGenerator.item(new Client(CLIENT_ID, Channel.POS, null, null, DESCRIPTION)));

		/*
		 * Roles repository setup.
		 */
		when(repository.getRoles(AZURE_TOKEN, ACQUIRER_ID, Channel.POS, CLIENT_ID, MERCHANT_ID, TERMINAL_ID))
			.thenReturn(UniGenerator.item(new Role(ACQUIRER_ID, Channel.POS, CLIENT_ID, MERCHANT_ID, TERMINAL_ID, ROLES)));

		/*
		 * Azure auth. client setup.
		 */
		when(authClient.getAccessToken(anyString(), anyString()))
			.thenReturn(UniGenerator.item(new GetAccessTokenResponse(TokenType.BEARER, Instant.now().getEpochSecond() + AZURE_TOKEN_DURATION, "", "", AZURE_TOKEN)));

		/*
		 * Azure key vault setup.
		 */
		long now = Instant.now().getEpochSecond();
		KeyAttributes keyAttributes = new KeyAttributes(now - 300, now + 600, now - 300, now - 300, Boolean.TRUE, KEY_RECOVERY_LEVEL, 0, Boolean.FALSE);

		when(keyVaultClient.getKeys(AZURE_TOKEN))
			.thenReturn(UniGenerator.item(new GetKeysResponse(new BasicKey[] {
				new BasicKey(keyUrl + KEY_NAME, keyAttributes)
			})));

		when(keyVaultClient.getKeyVersions(AZURE_TOKEN, KEY_NAME))
			.thenReturn(UniGenerator.item(new GetKeysResponse(new BasicKey[] {
				new BasicKey(keyUrl + KEY_NAME + "/" + KEY_VERSION, keyAttributes)
			})));

		when(keyVaultClient.getKey(AZURE_TOKEN, KEY_NAME, KEY_VERSION))
			.thenReturn(UniGenerator.item(new DetailedKey(new KeyDetails(keyUrl + KEY_NAME + "/" + KEY_VERSION, KEY_TYPE, KEY_OPS, MODULUS, PUBLIC_EXPONENT), keyAttributes)));

		when(keyVaultClient.sign(eq(AZURE_TOKEN), eq(KEY_NAME), eq(KEY_VERSION), any(SignRequest.class)))
			.thenReturn(UniGenerator.item(new SignResponse(KID, EXPECTED_SIGNATURE)));

		when(keyVaultClient.verifySignature(eq(AZURE_TOKEN), eq(KEY_NAME), eq(KEY_VERSION), any(VerifySignatureRequest.class)))
			.thenReturn(UniGenerator.item(new VerifySignatureResponse(Boolean.TRUE)));

		/*
		 * Refresh token.
		 */
		JWSHeader header = new JWSHeader(JWSAlgorithm.RS256, null, null, null, null, null, null, null, null, null, KID, true, null, null);

		JWTClaimsSet payload = new JWTClaimsSet.Builder()
			.subject(CLIENT_ID)
			.issueTime(new Date(now * 1000))
			.expirationTime(new Date((now + 15 * 60) * 1000))
			.claim(ClaimName.ACQUIRER_ID, ACQUIRER_ID)
			.claim(ClaimName.CHANNEL, Channel.POS)
			.claim(ClaimName.MERCHANT_ID, MERCHANT_ID)
			.claim(ClaimName.CLIENT_ID, CLIENT_ID)
			.claim(ClaimName.TERMINAL_ID, TERMINAL_ID)
			.claim(ClaimName.SCOPE, Scope.OFFLINE_ACCESS)
			.claim(ClaimName.GROUPS, ROLES)
			.build();

		SignedJWT refreshToken = new SignedJWT(header, payload);

		PrivateKey privateKey = KeyFactory.getInstance("RSA")
			.generatePrivate(new RSAPrivateKeySpec(
				new BigInteger(1, Base64.getUrlDecoder().decode(MODULUS)),
				new BigInteger(1, Base64.getUrlDecoder().decode(PRIVATE_EXPONENT))));

		JWSSigner signer = new RSASSASigner(privateKey);
		refreshToken.sign(signer);
		String refreshTokenStr = refreshToken.serialize();

		/*
		 * Test
		 */
		given()
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.header(HeaderParamName.REQUEST_ID, "00000000-0000-0000-0000-300000000000")
			.header(HeaderParamName.ACQUIRER_ID, ACQUIRER_ID)
			.header(HeaderParamName.CHANNEL, Channel.POS)
			.header(HeaderParamName.MERCHANT_ID, MERCHANT_ID)
			.header(HeaderParamName.TERMINAL_ID, TERMINAL_ID)
			.formParam(FormParamName.CLIENT_ID, CLIENT_ID)
			.formParam(FormParamName.GRANT_TYPE, GrantType.REFRESH_TOKEN)
			.formParam(FormParamName.REFRESH_TOKEN, refreshTokenStr)
			.when()
			.post()
			.then()
			.statusCode(200)
			.contentType(MediaType.APPLICATION_JSON)
			.body(JsonPropertyName.ACCESS_TOKEN, notNullValue())
			.body(JsonPropertyName.TOKEN_TYPE, equalTo(TokenType.BEARER))
			.body(JsonPropertyName.EXPIRES_IN, notNullValue(Long.class))
			.body(JsonPropertyName.REFRESH_TOKEN, notNullValue());
	}

	@Test
	void testExceptionParsingRefreshToken() throws NoSuchAlgorithmException, InvalidKeySpecException, JOSEException {
		/*
		 * Test
		 */
		given()
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.header(HeaderParamName.REQUEST_ID, "00000000-0000-0000-0000-300000000001")
			.header(HeaderParamName.ACQUIRER_ID, ACQUIRER_ID)
			.header(HeaderParamName.CHANNEL, Channel.POS)
			.header(HeaderParamName.MERCHANT_ID, MERCHANT_ID)
			.header(HeaderParamName.TERMINAL_ID, TERMINAL_ID)
			.formParam(FormParamName.CLIENT_ID, CLIENT_ID)
			.formParam(FormParamName.GRANT_TYPE, GrantType.REFRESH_TOKEN)
			.formParam(FormParamName.REFRESH_TOKEN, INVALID_REFRESH_TOKEN)
			.when()
			.post()
			.then()
			.statusCode(500)
			.contentType(MediaType.APPLICATION_JSON)
			.body(JsonPropertyName.ERRORS, hasItem(AuthErrorCode.ERROR_PARSING_TOKEN));
	}

	@Test
	void testWrongAlgorithm() throws NoSuchAlgorithmException, InvalidKeySpecException, JOSEException {
		/*
		 * Refresh token.
		 */
		long now = Instant.now().getEpochSecond();

		JWSHeader header = new JWSHeader(JWSAlgorithm.RS384, null, null, null, null, null, null, null, null, null, KID, true, null, null);

		JWTClaimsSet payload = new JWTClaimsSet.Builder()
			.subject(CLIENT_ID)
			.issueTime(new Date(now * 1000))
			.expirationTime(new Date((now + 15 * 60) * 1000))
			.claim(ClaimName.ACQUIRER_ID, ACQUIRER_ID)
			.claim(ClaimName.CHANNEL, Channel.POS)
			.claim(ClaimName.MERCHANT_ID, MERCHANT_ID)
			.claim(ClaimName.CLIENT_ID, CLIENT_ID)
			.claim(ClaimName.TERMINAL_ID, TERMINAL_ID)
			.claim(ClaimName.SCOPE, Scope.OFFLINE_ACCESS)
			.claim(ClaimName.GROUPS, ROLES)
			.build();

		SignedJWT refreshToken = new SignedJWT(header, payload);

		PrivateKey privateKey = KeyFactory.getInstance("RSA")
			.generatePrivate(new RSAPrivateKeySpec(
				new BigInteger(1, Base64.getUrlDecoder().decode(MODULUS)),
				new BigInteger(1, Base64.getUrlDecoder().decode(PRIVATE_EXPONENT))));

		JWSSigner signer = new RSASSASigner(privateKey);
		refreshToken.sign(signer);
		String refreshTokenStr = refreshToken.serialize();

		/*
		 * Test
		 */
		given()
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.header(HeaderParamName.REQUEST_ID, "00000000-0000-0000-0000-300000000002")
			.header(HeaderParamName.ACQUIRER_ID, ACQUIRER_ID)
			.header(HeaderParamName.CHANNEL, Channel.POS)
			.header(HeaderParamName.MERCHANT_ID, MERCHANT_ID)
			.header(HeaderParamName.TERMINAL_ID, TERMINAL_ID)
			.formParam(FormParamName.CLIENT_ID, CLIENT_ID)
			.formParam(FormParamName.GRANT_TYPE, GrantType.REFRESH_TOKEN)
			.formParam(FormParamName.REFRESH_TOKEN, refreshTokenStr)
			.when()
			.post()
			.then()
			.statusCode(401)
			.contentType(MediaType.APPLICATION_JSON)
			.body(JsonPropertyName.ERRORS, hasItem(AuthErrorCode.WRONG_ALGORITHM));
	}

	@Test
	void testNullIssueTime() throws NoSuchAlgorithmException, JOSEException, InvalidKeySpecException {
		/*
		 * Refresh token.
		 */
		long now = Instant.now().getEpochSecond();

		JWSHeader header = new JWSHeader(JWSAlgorithm.RS256, null, null, null, null, null, null, null, null, null, KID, true, null, null);

		JWTClaimsSet payload = new JWTClaimsSet.Builder()
			.subject(CLIENT_ID)
			.expirationTime(new Date((now + 15 * 60) * 1000))
			.claim(ClaimName.ACQUIRER_ID, ACQUIRER_ID)
			.claim(ClaimName.CHANNEL, Channel.POS)
			.claim(ClaimName.MERCHANT_ID, MERCHANT_ID)
			.claim(ClaimName.CLIENT_ID, CLIENT_ID)
			.claim(ClaimName.TERMINAL_ID, TERMINAL_ID)
			.claim(ClaimName.SCOPE, Scope.OFFLINE_ACCESS)
			.claim(ClaimName.GROUPS, ROLES)
			.build();

		SignedJWT refreshToken = new SignedJWT(header, payload);

		PrivateKey privateKey = KeyFactory.getInstance("RSA")
			.generatePrivate(new RSAPrivateKeySpec(
				new BigInteger(1, Base64.getUrlDecoder().decode(MODULUS)),
				new BigInteger(1, Base64.getUrlDecoder().decode(PRIVATE_EXPONENT))));

		JWSSigner signer = new RSASSASigner(privateKey);
		refreshToken.sign(signer);
		String refreshTokenStr = refreshToken.serialize();

		/*
		 * Test
		 */
		given()
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.header(HeaderParamName.REQUEST_ID, "00000000-0000-0000-0000-300000000003")
			.header(HeaderParamName.ACQUIRER_ID, ACQUIRER_ID)
			.header(HeaderParamName.CHANNEL, Channel.POS)
			.header(HeaderParamName.MERCHANT_ID, MERCHANT_ID)
			.header(HeaderParamName.TERMINAL_ID, TERMINAL_ID)
			.formParam(FormParamName.CLIENT_ID, CLIENT_ID)
			.formParam(FormParamName.GRANT_TYPE, GrantType.REFRESH_TOKEN)
			.formParam(FormParamName.REFRESH_TOKEN, refreshTokenStr)
			.when()
			.post()
			.then()
			.statusCode(401)
			.contentType(MediaType.APPLICATION_JSON)
			.body(JsonPropertyName.ERRORS, hasItem(AuthErrorCode.ISSUE_TIME_MUST_NOT_BE_NULL));
	}

	@Test
	void testWrongIssueTime() throws NoSuchAlgorithmException, JOSEException, InvalidKeySpecException {
		/*
		 * Refresh token.
		 */
		long now = Instant.now().getEpochSecond();

		JWSHeader header = new JWSHeader(JWSAlgorithm.RS256, null, null, null, null, null, null, null, null, null, KID, true, null, null);

		JWTClaimsSet payload = new JWTClaimsSet.Builder()
			.subject(CLIENT_ID)
			.issueTime(new Date((now + 10 * 60) * 1000))
			.expirationTime(new Date((now + 15 * 60) * 1000))
			.claim(ClaimName.ACQUIRER_ID, ACQUIRER_ID)
			.claim(ClaimName.CHANNEL, Channel.POS)
			.claim(ClaimName.MERCHANT_ID, MERCHANT_ID)
			.claim(ClaimName.CLIENT_ID, CLIENT_ID)
			.claim(ClaimName.TERMINAL_ID, TERMINAL_ID)
			.claim(ClaimName.SCOPE, Scope.OFFLINE_ACCESS)
			.claim(ClaimName.GROUPS, ROLES)
			.build();

		SignedJWT refreshToken = new SignedJWT(header, payload);

		PrivateKey privateKey = KeyFactory.getInstance("RSA")
			.generatePrivate(new RSAPrivateKeySpec(
				new BigInteger(1, Base64.getUrlDecoder().decode(MODULUS)),
				new BigInteger(1, Base64.getUrlDecoder().decode(PRIVATE_EXPONENT))));

		JWSSigner signer = new RSASSASigner(privateKey);
		refreshToken.sign(signer);
		String refreshTokenStr = refreshToken.serialize();

		/*
		 * Test
		 */
		given()
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.header(HeaderParamName.REQUEST_ID, "00000000-0000-0000-0000-300000000004")
			.header(HeaderParamName.ACQUIRER_ID, ACQUIRER_ID)
			.header(HeaderParamName.CHANNEL, Channel.POS)
			.header(HeaderParamName.MERCHANT_ID, MERCHANT_ID)
			.header(HeaderParamName.TERMINAL_ID, TERMINAL_ID)
			.formParam(FormParamName.CLIENT_ID, CLIENT_ID)
			.formParam(FormParamName.GRANT_TYPE, GrantType.REFRESH_TOKEN)
			.formParam(FormParamName.REFRESH_TOKEN, refreshTokenStr)
			.when()
			.post()
			.then()
			.statusCode(401)
			.contentType(MediaType.APPLICATION_JSON)
			.body(JsonPropertyName.ERRORS, hasItem(AuthErrorCode.WRONG_ISSUE_TIME));
	}

	@Test
	void testNullExpirationTime() throws NoSuchAlgorithmException, JOSEException, InvalidKeySpecException {
		/*
		 * Refresh token.
		 */
		long now = Instant.now().getEpochSecond();

		JWSHeader header = new JWSHeader(JWSAlgorithm.RS256, null, null, null, null, null, null, null, null, null, KID, true, null, null);

		JWTClaimsSet payload = new JWTClaimsSet.Builder()
			.subject(CLIENT_ID)
			.issueTime(new Date(now * 1000))
			.claim(ClaimName.ACQUIRER_ID, ACQUIRER_ID)
			.claim(ClaimName.CHANNEL, Channel.POS)
			.claim(ClaimName.MERCHANT_ID, MERCHANT_ID)
			.claim(ClaimName.CLIENT_ID, CLIENT_ID)
			.claim(ClaimName.TERMINAL_ID, TERMINAL_ID)
			.claim(ClaimName.SCOPE, Scope.OFFLINE_ACCESS)
			.claim(ClaimName.GROUPS, ROLES)
			.build();

		SignedJWT refreshToken = new SignedJWT(header, payload);

		PrivateKey privateKey = KeyFactory.getInstance("RSA")
			.generatePrivate(new RSAPrivateKeySpec(
				new BigInteger(1, Base64.getUrlDecoder().decode(MODULUS)),
				new BigInteger(1, Base64.getUrlDecoder().decode(PRIVATE_EXPONENT))));

		JWSSigner signer = new RSASSASigner(privateKey);
		refreshToken.sign(signer);
		String refreshTokenStr = refreshToken.serialize();

		/*
		 * Test
		 */
		given()
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.header(HeaderParamName.REQUEST_ID, "00000000-0000-0000-0000-300000000005")
			.header(HeaderParamName.ACQUIRER_ID, ACQUIRER_ID)
			.header(HeaderParamName.CHANNEL, Channel.POS)
			.header(HeaderParamName.MERCHANT_ID, MERCHANT_ID)
			.header(HeaderParamName.TERMINAL_ID, TERMINAL_ID)
			.formParam(FormParamName.CLIENT_ID, CLIENT_ID)
			.formParam(FormParamName.GRANT_TYPE, GrantType.REFRESH_TOKEN)
			.formParam(FormParamName.REFRESH_TOKEN, refreshTokenStr)
			.when()
			.post()
			.then()
			.statusCode(401)
			.contentType(MediaType.APPLICATION_JSON)
			.body(JsonPropertyName.ERRORS, hasItem(AuthErrorCode.EXPIRATION_TIME_MUST_NOT_BE_NULL));
	}

	@Test
	void testWrongExpirationTime() throws NoSuchAlgorithmException, JOSEException, InvalidKeySpecException {
		/*
		 * Refresh token.
		 */
		long now = Instant.now().getEpochSecond();

		JWSHeader header = new JWSHeader(JWSAlgorithm.RS256, null, null, null, null, null, null, null, null, null, KID, true, null, null);

		JWTClaimsSet payload = new JWTClaimsSet.Builder()
			.subject(CLIENT_ID)
			.issueTime(new Date(now * 1000))
			.expirationTime(new Date((now - 15 * 60) * 1000))
			.claim(ClaimName.ACQUIRER_ID, ACQUIRER_ID)
			.claim(ClaimName.CHANNEL, Channel.POS)
			.claim(ClaimName.MERCHANT_ID, MERCHANT_ID)
			.claim(ClaimName.CLIENT_ID, CLIENT_ID)
			.claim(ClaimName.TERMINAL_ID, TERMINAL_ID)
			.claim(ClaimName.SCOPE, Scope.OFFLINE_ACCESS)
			.claim(ClaimName.GROUPS, ROLES)
			.build();

		SignedJWT refreshToken = new SignedJWT(header, payload);

		PrivateKey privateKey = KeyFactory.getInstance("RSA")
			.generatePrivate(new RSAPrivateKeySpec(
				new BigInteger(1, Base64.getUrlDecoder().decode(MODULUS)),
				new BigInteger(1, Base64.getUrlDecoder().decode(PRIVATE_EXPONENT))));

		JWSSigner signer = new RSASSASigner(privateKey);
		refreshToken.sign(signer);
		String refreshTokenStr = refreshToken.serialize();

		/*
		 * Test
		 */
		given()
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.header(HeaderParamName.REQUEST_ID, "00000000-0000-0000-0000-300000000006")
			.header(HeaderParamName.ACQUIRER_ID, ACQUIRER_ID)
			.header(HeaderParamName.CHANNEL, Channel.POS)
			.header(HeaderParamName.MERCHANT_ID, MERCHANT_ID)
			.header(HeaderParamName.TERMINAL_ID, TERMINAL_ID)
			.formParam(FormParamName.CLIENT_ID, CLIENT_ID)
			.formParam(FormParamName.GRANT_TYPE, GrantType.REFRESH_TOKEN)
			.formParam(FormParamName.REFRESH_TOKEN, refreshTokenStr)
			.when()
			.post()
			.then()
			.statusCode(401)
			.contentType(MediaType.APPLICATION_JSON)
			.body(JsonPropertyName.ERRORS, hasItem(AuthErrorCode.TOKEN_EXPIRED));
	}

	// @Test
	// void testNullAzureAccessToken() {
	// }

	@Test
	void testWrongScope() throws NoSuchAlgorithmException, InvalidKeySpecException, JOSEException {
		/*
		 * Refresh token.
		 */
		long now = Instant.now().getEpochSecond();

		JWSHeader header = new JWSHeader(JWSAlgorithm.RS256, null, null, null, null, null, null, null, null, null, KID, true, null, null);

		JWTClaimsSet payload = new JWTClaimsSet.Builder()
			.subject(CLIENT_ID)
			.issueTime(new Date(now * 1000))
			.expirationTime(new Date((now + 15 * 60) * 1000))
			.claim(ClaimName.ACQUIRER_ID, ACQUIRER_ID)
			.claim(ClaimName.CHANNEL, Channel.POS)
			.claim(ClaimName.MERCHANT_ID, MERCHANT_ID)
			.claim(ClaimName.CLIENT_ID, CLIENT_ID)
			.claim(ClaimName.TERMINAL_ID, TERMINAL_ID)
			.claim(ClaimName.SCOPE, WRONG_SCOPE)
			.claim(ClaimName.GROUPS, ROLES)
			.build();

		SignedJWT refreshToken = new SignedJWT(header, payload);

		PrivateKey privateKey = KeyFactory.getInstance("RSA")
			.generatePrivate(new RSAPrivateKeySpec(
				new BigInteger(1, Base64.getUrlDecoder().decode(MODULUS)),
				new BigInteger(1, Base64.getUrlDecoder().decode(PRIVATE_EXPONENT))));

		JWSSigner signer = new RSASSASigner(privateKey);
		refreshToken.sign(signer);
		String refreshTokenStr = refreshToken.serialize();

		/*
		 * Test
		 */
		given()
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.header(HeaderParamName.REQUEST_ID, "00000000-0000-0000-0000-300000000007")
			.header(HeaderParamName.ACQUIRER_ID, ACQUIRER_ID)
			.header(HeaderParamName.CHANNEL, Channel.POS)
			.header(HeaderParamName.MERCHANT_ID, MERCHANT_ID)
			.header(HeaderParamName.TERMINAL_ID, TERMINAL_ID)
			.formParam(FormParamName.CLIENT_ID, CLIENT_ID)
			.formParam(FormParamName.GRANT_TYPE, GrantType.REFRESH_TOKEN)
			.formParam(FormParamName.REFRESH_TOKEN, refreshTokenStr)
			.when()
			.post()
			.then()
			.statusCode(401)
			.contentType(MediaType.APPLICATION_JSON)
			.body(JsonPropertyName.ERRORS, hasItem(AuthErrorCode.WRONG_SCOPE));
	}

	@Test
	void testWithScope() throws InvalidKeySpecException, NoSuchAlgorithmException, JOSEException {
		/*
		 * Refresh token.
		 */
		long now = Instant.now().getEpochSecond();
		JWSHeader header = new JWSHeader(JWSAlgorithm.RS256, null, null, null, null, null, null, null, null, null, KID, true, null, null);

		JWTClaimsSet payload = new JWTClaimsSet.Builder()
			.subject(CLIENT_ID)
			.issueTime(new Date(now * 1000))
			.expirationTime(new Date((now + 15 * 60) * 1000))
			.claim(ClaimName.ACQUIRER_ID, ACQUIRER_ID)
			.claim(ClaimName.CHANNEL, Channel.POS)
			.claim(ClaimName.MERCHANT_ID, MERCHANT_ID)
			.claim(ClaimName.CLIENT_ID, CLIENT_ID)
			.claim(ClaimName.TERMINAL_ID, TERMINAL_ID)
			.claim(ClaimName.SCOPE, Scope.OFFLINE_ACCESS)
			.claim(ClaimName.GROUPS, ROLES)
			.build();

		SignedJWT refreshToken = new SignedJWT(header, payload);

		PrivateKey privateKey = KeyFactory.getInstance("RSA")
			.generatePrivate(new RSAPrivateKeySpec(
				new BigInteger(1, Base64.getUrlDecoder().decode(MODULUS)),
				new BigInteger(1, Base64.getUrlDecoder().decode(PRIVATE_EXPONENT))));

		JWSSigner signer = new RSASSASigner(privateKey);
		refreshToken.sign(signer);
		String refreshTokenStr = refreshToken.serialize();

		/*
		 * Test
		 */
		given()
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.header(HeaderParamName.REQUEST_ID, "00000000-0000-0000-0000-300000000008")
			.header(HeaderParamName.ACQUIRER_ID, ACQUIRER_ID)
			.header(HeaderParamName.CHANNEL, Channel.POS)
			.header(HeaderParamName.MERCHANT_ID, MERCHANT_ID)
			.header(HeaderParamName.TERMINAL_ID, TERMINAL_ID)
			.formParam(FormParamName.CLIENT_ID, CLIENT_ID)
			.formParam(FormParamName.GRANT_TYPE, GrantType.REFRESH_TOKEN)
			.formParam(FormParamName.REFRESH_TOKEN, refreshTokenStr)
			.formParam(FormParamName.SCOPE, Scope.OFFLINE_ACCESS)
			.when()
			.post()
			.then()
			.statusCode(400)
			.contentType(MediaType.APPLICATION_JSON)
			.body(JsonPropertyName.ERRORS, notNullValue());
	}

	// @Test
	// void testUnauthorizedGettingAzureAccessToken() {
	// }

	// @Test
	// void testNoSuchAlgorithmGettingDerDigestInfo() {
	// }

	// @Test
	// void testExceptionGettingDerDigestInfo() {
	// }

	// @Test
	// void testWrongSignature() {
	// }

	// @Test
	// void testErrorFromSuper() {
	// }
}