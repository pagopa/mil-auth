/*
 * RefreshTokensResourceTest.java
 *
 * 28 ago 2023
 */
package it.pagopa.swclient.mil.auth.resource;

import it.pagopa.swclient.mil.auth.bean.ClaimName;
import static it.pagopa.swclient.mil.auth.bean.JsonPropertyName.TOKEN_TYPE;
import static it.pagopa.swclient.mil.auth.bean.TokenType.BEARER;
import static it.pagopa.swclient.mil.bean.Channel.POS;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static io.restassured.RestAssured.given;

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

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.Test;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import io.quarkus.test.InjectMock;
import it.pagopa.swclient.mil.auth.azurekeyvault.bean.GetAccessTokenResponse;
import it.pagopa.swclient.mil.auth.azurekeyvault.bean.GetKeyResponse;
import it.pagopa.swclient.mil.auth.azurekeyvault.bean.GetKeyVersionsResponse;
import it.pagopa.swclient.mil.auth.azurekeyvault.bean.GetKeysResponse;
import it.pagopa.swclient.mil.auth.azurekeyvault.bean.Key;
import it.pagopa.swclient.mil.auth.azurekeyvault.bean.KeyAttributes;
import it.pagopa.swclient.mil.auth.azurekeyvault.bean.KeyDetails;
import it.pagopa.swclient.mil.auth.azurekeyvault.bean.KeyVersion;
import it.pagopa.swclient.mil.auth.azurekeyvault.bean.SignRequest;
import it.pagopa.swclient.mil.auth.azurekeyvault.bean.SignResponse;
import it.pagopa.swclient.mil.auth.azurekeyvault.bean.VerifySignatureRequest;
import it.pagopa.swclient.mil.auth.azurekeyvault.bean.VerifySignatureResponse;
import it.pagopa.swclient.mil.auth.azurekeyvault.client.AzureAuthClient;
import it.pagopa.swclient.mil.auth.azurekeyvault.client.AzureKeyVaultClient;
import it.pagopa.swclient.mil.auth.bean.Client;
import it.pagopa.swclient.mil.auth.bean.GrantType;
import it.pagopa.swclient.mil.auth.bean.Role;
import it.pagopa.swclient.mil.auth.bean.Scope;
import it.pagopa.swclient.mil.auth.bean.TokenType;
import it.pagopa.swclient.mil.auth.client.AuthDataRepository;
import it.pagopa.swclient.mil.auth.util.UniGenerator;
import it.pagopa.swclient.mil.bean.Channel;
import jakarta.ws.rs.core.MediaType;

import static org.hamcrest.Matchers.*;

/**
 * 
 * @author Antonio Tarricone
 */
class RefreshTokensResourceTest {
	/*
	 * 
	 */
	private static final String ACQUIRER_ID = "4585625";
	private static final String MERCHANT_ID = "28405fHfk73x88D";
	private static final String TERMINAL_ID = "12345678";
	private static final String CLIENT_ID = "3965df56-ca9a-49e5-97e8-061433d4a25b";
	private static final String SALT = "aGw/h/8Fm9S2aNvlvIaxJyhKP67ZU4FEm6mDVhL3aEVrahXFif9x2BkQ4OY87Z9tWVyWbSB/JeztYVmTshrFWQ==";
	private static final String HASH = "G3oYMwnLVR9+m7WB4/pvoVeHxzsTdeyhndpVoruHzog=";
	private static final String SECRET = "5ceef788-4115-43a7-a704-b1bcc9a47c86";
	private static final String DESCRIPTION = "VAS Layer";
	private static final String WRONG_SECRET = "3674f0e7-d717-44cc-a3bc-5f8f41771fea";
	private static final List<String> ROLES = List.of("NoticePayer", "SlavePos");

	/*
	 * 
	 */
	private static final long AZURE_TOKEN_DURATION = 3599;
	private static final String AZURE_TOKEN = "this_is_the_token";
	private static final String AUTHORIZATION_HDR_VALUE = BEARER + " " + AZURE_TOKEN;

	/*
	 * 
	 */
	private static final String KEY_URL = "https://quarkus-azure-test-kv.vault.azure.net/keys/";
	private static final String KEY_NAME = "0709643f49394529b92c19a68c8e184a";
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
	AuthDataRepository repository;

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

	@Test
	void testOk() throws InvalidKeySpecException, NoSuchAlgorithmException, JOSEException {
		/*
		 * Client repository setup.
		 */
		when(repository.getClient(CLIENT_ID))
			.thenReturn(UniGenerator.item(new Client(CLIENT_ID, POS, null, null, DESCRIPTION)));
		
		/*
		 * Roles repository setup.
		 */
		when(repository.getRoles(ACQUIRER_ID, POS, CLIENT_ID, MERCHANT_ID, TERMINAL_ID))
			.thenReturn(UniGenerator.item(new Role(ACQUIRER_ID, POS, CLIENT_ID, MERCHANT_ID, TERMINAL_ID, ROLES)));
		
		/*
		 * Azure auth. client setup.
		 */
		when(authClient.getAccessToken(anyString(), anyString(), anyString(), anyString(), anyString()))
			.thenReturn(UniGenerator.item(new GetAccessTokenResponse(TOKEN_TYPE, AZURE_TOKEN_DURATION, AZURE_TOKEN_DURATION, AZURE_TOKEN)));
		
		/*
		 * Azure key vault setup.
		 */
		long now = Instant.now().getEpochSecond();
		KeyAttributes keyAttributes = new KeyAttributes(now - 300, now + 600, now - 300, now - 300, Boolean.TRUE, KEY_RECOVERY_LEVEL, 0, Boolean.FALSE);
		
		when(keyVaultClient.getKeys(AUTHORIZATION_HDR_VALUE))
			.thenReturn(UniGenerator.item(new GetKeysResponse(new Key[] {
				new Key(KEY_URL + KEY_NAME, keyAttributes)
			})));
		
		when(keyVaultClient.getKeyVersions(AUTHORIZATION_HDR_VALUE, KEY_NAME))
			.thenReturn(UniGenerator.item(new GetKeyVersionsResponse(new KeyVersion[] {
				new KeyVersion(KEY_URL + KEY_NAME + "/" + KEY_VERSION, keyAttributes)
			})));
		
		when(keyVaultClient.getKey(AUTHORIZATION_HDR_VALUE, KEY_NAME, KEY_VERSION))
			.thenReturn(UniGenerator.item(new GetKeyResponse(new KeyDetails(KEY_URL + KEY_NAME + "/" + KEY_VERSION, KEY_TYPE, KEY_OPS, MODULUS, PUBLIC_EXPONENT, keyAttributes))));
		
		when(keyVaultClient.sign(eq(AUTHORIZATION_HDR_VALUE), eq(KEY_NAME), eq(KEY_VERSION), any(SignRequest.class)))
			.thenReturn(UniGenerator.item(new SignResponse(KID, EXPECTED_SIGNATURE)));
		
		when(keyVaultClient.verifySignature(eq(AUTHORIZATION_HDR_VALUE), eq(KEY_NAME), eq(KEY_VERSION), any(VerifySignatureRequest.class)))
			.thenReturn(UniGenerator.item(new VerifySignatureResponse(Boolean.TRUE)));
		
		/*
		 * Refresh token.
		 */
		JWSHeader header = new JWSHeader(JWSAlgorithm.RS256, null, null, null, null, null, null, null, null, null, KID, true, null, null);
		
		JWTClaimsSet payload = new JWTClaimsSet.Builder()
			.subject(CLIENT_ID)
			.issueTime(new Date(now    *1000))
			.expirationTime(new Date((now + 15 * 60) * 1000))
			.claim(ClaimName.ACQUIRER_ID, ACQUIRER_ID)
			.claim(ClaimName.CHANNEL, POS)
			.claim(ClaimName.MERCHANT_ID, MERCHANT_ID)
			.claim(ClaimName.CLIENT_ID, CLIENT_ID)
			.claim(ClaimName.TERMINAL_ID, TERMINAL_ID)
			.claim(ClaimName.SCOPE, Scope.OFFLINE_ACCESS)
			.claim(ClaimName.GROUPS, ROLES)
			.build();
		
		SignedJWT token = new SignedJWT(header, payload);
		
		PrivateKey privateKey = KeyFactory.getInstance("RSA")
			.generatePrivate(new RSAPrivateKeySpec(
				new BigInteger(1, Base64.getUrlDecoder().decode(MODULUS)),
				new BigInteger(1, Base64.getUrlDecoder().decode(PRIVATE_EXPONENT))));

		JWSSigner signer = new RSASSASigner(privateKey);
		token.sign(signer);
		String refreshTokenStr = token.serialize();
		
		/*
		 * Test
		 */
		given()
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.header("RequestId", "00000000-0000-0000-0000-300000000000")
			.header("AcquirerId", ACQUIRER_ID)
			.header("Channel", Channel.POS)
			.header("MerchantId", MERCHANT_ID)
			.header("TerminalId", TERMINAL_ID)
			.formParam("client_id", CLIENT_ID)
			.formParam("grant_type", GrantType.REFRESH_TOKEN)
			.formParam("refresh_token", refreshTokenStr)
			.when()
			.post()
			.then()
			.statusCode(200)
			.contentType(MediaType.APPLICATION_JSON)
			.body("access_token", notNullValue())
			.body("token_type", equalTo(TokenType.BEARER))
			.body("expires_in", notNullValue(Long.class))
			.body("refresh_token", notNullValue());
		
	}

	@Test
	void testExceptionParsingRefreshToken() {
		fail("Not yet implemented");
	}

	@Test
	void testWrongAlgorithm() {
		fail("Not yet implemented");
	}

	@Test
	void testNullIssueTime() {
		fail("Not yet implemented");
	}

	@Test
	void testWrongIssueTime() {
		fail("Not yet implemented");
	}

	@Test
	void testNullExpirationTime() {
		fail("Not yet implemented");
	}

	@Test
	void testWrongExpirationTime() {
		fail("Not yet implemented");
	}

	@Test
	void testWrongScope() {
		fail("Not yet implemented");
	}

	@Test
	void testNullAzureAccessToken() {
		fail("Not yet implemented");
	}

	@Test
	void testUnauthorizedGettingAzureAccessToken() {
		fail("Not yet implemented");
	}

	@Test
	void testNoSuchAlgorithmGettingDerDigestInfo() {
		fail("Not yet implemented");
	}

	@Test
	void testExceptionGettingDerDigestInfo() {
		fail("Not yet implemented");
	}

	@Test
	void testWrongSignature() {
		fail("Not yet implemented");
	}

	@Test
	void testErrorFromSuper() {
		fail("Not yet implemented");
	}
}