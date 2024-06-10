/*
 * TokenByPasswordResourceTest.java
 *
 * 31 ago 2023
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

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Base64;
import java.util.List;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.TestInstance;

import io.quarkus.test.InjectMock;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.mutiny.Uni;
import it.pagopa.swclient.mil.auth.AuthErrorCode;
import it.pagopa.swclient.mil.auth.azure.auth.bean.GetAccessTokenResponse;
import it.pagopa.swclient.mil.auth.azure.auth.client.AzureAuthClient;
import it.pagopa.swclient.mil.auth.azure.keyvault.bean.BasicKey;
import it.pagopa.swclient.mil.auth.azure.keyvault.bean.DetailedKey;
import it.pagopa.swclient.mil.auth.azure.keyvault.bean.GetKeysResponse;
import it.pagopa.swclient.mil.auth.azure.keyvault.bean.KeyDetails;
import it.pagopa.swclient.mil.auth.azure.keyvault.bean.SignRequest;
import it.pagopa.swclient.mil.auth.azure.keyvault.bean.SignResponse;
import it.pagopa.swclient.mil.auth.azure.keyvault.client.AzureKeyVaultClient;
import it.pagopa.swclient.mil.auth.azure.storage.client.AzureAuthDataRepositoryClient;
import it.pagopa.swclient.mil.auth.bean.Client;
import it.pagopa.swclient.mil.auth.bean.FormParamName;
import it.pagopa.swclient.mil.auth.bean.GrantType;
import it.pagopa.swclient.mil.auth.bean.HeaderParamName;
import it.pagopa.swclient.mil.auth.bean.JsonPropertyName;
import it.pagopa.swclient.mil.auth.bean.Role;
import it.pagopa.swclient.mil.auth.bean.Scope;
import it.pagopa.swclient.mil.auth.bean.TokenType;
import it.pagopa.swclient.mil.auth.bean.User;
import it.pagopa.swclient.mil.auth.util.PasswordVerifier;
import it.pagopa.swclient.mil.auth.util.UniGenerator;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyAttributes;
import it.pagopa.swclient.mil.bean.Channel;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * Get user from repository -> Verify Consistency -> Verify Password -> Super
 * 
 * @author Antonio Tarricone
 */
@QuarkusTest
@TestHTTPEndpoint(TokenResource.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TokenByPasswordResourceTest {
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
	private static final String ACQUIRER_2_ID = "4585626";
	private static final String MERCHANT_2_ID = "28405fHfk73xkkk";

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
	private static final String PUBLIC_EXPONENT = "AQAB";
	private static final String EXPECTED_SIGNATURE = "expected_signature";

	/*
	 *
	 */
	private static final String USERNAME = "user";
	private static final String PASSWORD = "password";
	private static final String SALT = "zfN59oSr9RfFiiSASUO1YIcv8bARsj1OAV8tEydQiKC3su5Mlz1TsjbFwvWrGCjXdkDUsbeXGnYZDavJuTKw6Q==";

	/*
	 * 
	 */
	private static final String PASSWORD_2 = "password2";

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
	void testOk() throws NoSuchAlgorithmException {
		/*
		 * User respository setup.
		 */
		String userHash = Base64.getUrlEncoder().encodeToString(
			MessageDigest.getInstance("SHA256").digest(
				USERNAME.getBytes(StandardCharsets.UTF_8)));

		String passwordHash = Base64.getEncoder().encodeToString(PasswordVerifier.hashBytes(PASSWORD, SALT));

		when(repository.getUser(AZURE_TOKEN, userHash))
			.thenReturn(UniGenerator.item(new User(USERNAME, SALT, passwordHash, ACQUIRER_ID, Channel.POS, MERCHANT_ID)));

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

		/*
		 * Test
		 */
		given()
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.header(HeaderParamName.REQUEST_ID, "00000000-0000-0000-0000-400000000000")
			.header(HeaderParamName.ACQUIRER_ID, ACQUIRER_ID)
			.header(HeaderParamName.CHANNEL, Channel.POS)
			.header(HeaderParamName.MERCHANT_ID, MERCHANT_ID)
			.header(HeaderParamName.TERMINAL_ID, TERMINAL_ID)
			.formParam(FormParamName.CLIENT_ID, CLIENT_ID)
			.formParam(FormParamName.GRANT_TYPE, GrantType.PASSWORD)
			.formParam(FormParamName.USERNAME, USERNAME)
			.formParam(FormParamName.PASSWORD, PASSWORD)
			.formParam(FormParamName.SCOPE, Scope.OFFLINE_ACCESS)
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
	void testUserNotFound() throws NoSuchAlgorithmException {
		/*
		 * User respository setup.
		 */
		String userHash = Base64.getUrlEncoder().encodeToString(
			MessageDigest.getInstance("SHA256").digest(
				USERNAME.getBytes(StandardCharsets.UTF_8)));

		when(repository.getUser(AZURE_TOKEN, userHash))
			.thenReturn(Uni.createFrom().failure(new WebApplicationException(Response.status(Response.Status.NOT_FOUND).build())));

		/*
		 * Azure auth. client setup.
		 */
		when(authClient.getAccessToken(anyString(), anyString()))
			.thenReturn(UniGenerator.item(new GetAccessTokenResponse(TokenType.BEARER, Instant.now().getEpochSecond() + AZURE_TOKEN_DURATION, "", "", AZURE_TOKEN)));

		/*
		 * Test
		 */
		given()
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.header(HeaderParamName.REQUEST_ID, "00000000-0000-0000-0000-400000000001")
			.header(HeaderParamName.ACQUIRER_ID, ACQUIRER_ID)
			.header(HeaderParamName.CHANNEL, Channel.POS)
			.header(HeaderParamName.MERCHANT_ID, MERCHANT_ID)
			.header(HeaderParamName.TERMINAL_ID, TERMINAL_ID)
			.formParam(FormParamName.CLIENT_ID, CLIENT_ID)
			.formParam(FormParamName.GRANT_TYPE, GrantType.PASSWORD)
			.formParam(FormParamName.USERNAME, USERNAME)
			.formParam(FormParamName.PASSWORD, PASSWORD)
			.formParam(FormParamName.SCOPE, Scope.OFFLINE_ACCESS)
			.when()
			.post()
			.then()
			.statusCode(401)
			.contentType(MediaType.APPLICATION_JSON)
			.body(JsonPropertyName.ERRORS, hasItem(AuthErrorCode.WRONG_CREDENTIALS));
	}

	@Test
	void testWebApplicationExceptionGettingUser() throws NoSuchAlgorithmException {
		/*
		 * User respository setup.
		 */
		String userHash = Base64.getUrlEncoder().encodeToString(
			MessageDigest.getInstance("SHA256").digest(
				USERNAME.getBytes(StandardCharsets.UTF_8)));

		when(repository.getUser(AZURE_TOKEN, userHash))
			.thenReturn(Uni.createFrom().failure(new WebApplicationException(Response.status(Response.Status.INTERNAL_SERVER_ERROR).build())));

		/*
		 * Azure auth. client setup.
		 */
		when(authClient.getAccessToken(anyString(), anyString()))
			.thenReturn(UniGenerator.item(new GetAccessTokenResponse(TokenType.BEARER, Instant.now().getEpochSecond() + AZURE_TOKEN_DURATION, "", "", AZURE_TOKEN)));

		/*
		 * Test
		 */
		given()
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.header(HeaderParamName.REQUEST_ID, "00000000-0000-0000-0000-400000000002")
			.header(HeaderParamName.ACQUIRER_ID, ACQUIRER_ID)
			.header(HeaderParamName.CHANNEL, Channel.POS)
			.header(HeaderParamName.MERCHANT_ID, MERCHANT_ID)
			.header(HeaderParamName.TERMINAL_ID, TERMINAL_ID)
			.formParam(FormParamName.CLIENT_ID, CLIENT_ID)
			.formParam(FormParamName.GRANT_TYPE, GrantType.PASSWORD)
			.formParam(FormParamName.USERNAME, USERNAME)
			.formParam(FormParamName.PASSWORD, PASSWORD)
			.formParam(FormParamName.SCOPE, Scope.OFFLINE_ACCESS)
			.when()
			.post()
			.then()
			.statusCode(500)
			.contentType(MediaType.APPLICATION_JSON)
			.body(JsonPropertyName.ERRORS, hasItem(AuthErrorCode.ERROR_SEARCHING_FOR_CREDENTIALS));
	}

	@Test
	void testExceptionGettingUser() throws NoSuchAlgorithmException {
		/*
		 * User respository setup.
		 */
		String userHash = Base64.getUrlEncoder().encodeToString(
			MessageDigest.getInstance("SHA256").digest(
				USERNAME.getBytes(StandardCharsets.UTF_8)));

		when(repository.getUser(AZURE_TOKEN, userHash))
			.thenReturn(Uni.createFrom().failure(new Exception("synthetic exception")));

		/*
		 * Azure auth. client setup.
		 */
		when(authClient.getAccessToken(anyString(), anyString()))
			.thenReturn(UniGenerator.item(new GetAccessTokenResponse(TokenType.BEARER, Instant.now().getEpochSecond() + AZURE_TOKEN_DURATION, "", "", AZURE_TOKEN)));

		/*
		 * Test
		 */
		given()
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.header(HeaderParamName.REQUEST_ID, "00000000-0000-0000-0000-400000000003")
			.header(HeaderParamName.ACQUIRER_ID, ACQUIRER_ID)
			.header(HeaderParamName.CHANNEL, Channel.POS)
			.header(HeaderParamName.MERCHANT_ID, MERCHANT_ID)
			.header(HeaderParamName.TERMINAL_ID, TERMINAL_ID)
			.formParam(FormParamName.CLIENT_ID, CLIENT_ID)
			.formParam(FormParamName.GRANT_TYPE, GrantType.PASSWORD)
			.formParam(FormParamName.USERNAME, USERNAME)
			.formParam(FormParamName.PASSWORD, PASSWORD)
			.formParam(FormParamName.SCOPE, Scope.OFFLINE_ACCESS)
			.when()
			.post()
			.then()
			.statusCode(500)
			.contentType(MediaType.APPLICATION_JSON)
			.body(JsonPropertyName.ERRORS, hasItem(AuthErrorCode.ERROR_SEARCHING_FOR_CREDENTIALS));
	}

	void testNoSuchAlgorithmFindingCredentials() {
		// See TokenByPasswordServiceTest.testNoSuchAlgorithmFindingCredentials
	}

	@Test
	void testInconsistentAcquirerId() throws NoSuchAlgorithmException {
		/*
		 * User respository setup.
		 */
		String userHash = Base64.getUrlEncoder().encodeToString(
			MessageDigest.getInstance("SHA256").digest(
				USERNAME.getBytes(StandardCharsets.UTF_8)));

		String passwordHash = Base64.getEncoder().encodeToString(PasswordVerifier.hashBytes(PASSWORD, SALT));

		when(repository.getUser(AZURE_TOKEN, userHash))
			.thenReturn(UniGenerator.item(new User(USERNAME, SALT, passwordHash, ACQUIRER_2_ID, Channel.POS, MERCHANT_ID)));

		/*
		 * Azure auth. client setup.
		 */
		when(authClient.getAccessToken(anyString(), anyString()))
			.thenReturn(UniGenerator.item(new GetAccessTokenResponse(TokenType.BEARER, Instant.now().getEpochSecond() + AZURE_TOKEN_DURATION, "", "", AZURE_TOKEN)));

		/*
		 * Test
		 */
		given()
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.header(HeaderParamName.REQUEST_ID, "00000000-0000-0000-0000-400000000004")
			.header(HeaderParamName.ACQUIRER_ID, ACQUIRER_ID)
			.header(HeaderParamName.CHANNEL, Channel.POS)
			.header(HeaderParamName.MERCHANT_ID, MERCHANT_ID)
			.header(HeaderParamName.TERMINAL_ID, TERMINAL_ID)
			.formParam(FormParamName.CLIENT_ID, CLIENT_ID)
			.formParam(FormParamName.GRANT_TYPE, GrantType.PASSWORD)
			.formParam(FormParamName.USERNAME, USERNAME)
			.formParam(FormParamName.PASSWORD, PASSWORD)
			.formParam(FormParamName.SCOPE, Scope.OFFLINE_ACCESS)
			.when()
			.post()
			.then()
			.statusCode(401)
			.contentType(MediaType.APPLICATION_JSON)
			.body(JsonPropertyName.ERRORS, hasItem(AuthErrorCode.INCONSISTENT_CREDENTIALS));
	}

	@Test
	void testInconsistentChannel() throws NoSuchAlgorithmException {
		/*
		 * User respository setup.
		 */
		String userHash = Base64.getUrlEncoder().encodeToString(
			MessageDigest.getInstance("SHA256").digest(
				USERNAME.getBytes(StandardCharsets.UTF_8)));

		String passwordHash = Base64.getEncoder().encodeToString(PasswordVerifier.hashBytes(PASSWORD, SALT));

		when(repository.getUser(AZURE_TOKEN, userHash))
			.thenReturn(UniGenerator.item(new User(USERNAME, SALT, passwordHash, ACQUIRER_ID, Channel.ATM, MERCHANT_ID)));

		/*
		 * Azure auth. client setup.
		 */
		when(authClient.getAccessToken(anyString(), anyString()))
			.thenReturn(UniGenerator.item(new GetAccessTokenResponse(TokenType.BEARER, Instant.now().getEpochSecond() + AZURE_TOKEN_DURATION, "", "", AZURE_TOKEN)));

		/*
		 * Test
		 */
		given()
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.header(HeaderParamName.REQUEST_ID, "00000000-0000-0000-0000-400000000005")
			.header(HeaderParamName.ACQUIRER_ID, ACQUIRER_ID)
			.header(HeaderParamName.CHANNEL, Channel.POS)
			.header(HeaderParamName.MERCHANT_ID, MERCHANT_ID)
			.header(HeaderParamName.TERMINAL_ID, TERMINAL_ID)
			.formParam(FormParamName.CLIENT_ID, CLIENT_ID)
			.formParam(FormParamName.GRANT_TYPE, GrantType.PASSWORD)
			.formParam(FormParamName.USERNAME, USERNAME)
			.formParam(FormParamName.PASSWORD, PASSWORD)
			.formParam(FormParamName.SCOPE, Scope.OFFLINE_ACCESS)
			.when()
			.post()
			.then()
			.statusCode(401)
			.contentType(MediaType.APPLICATION_JSON)
			.body(JsonPropertyName.ERRORS, hasItem(AuthErrorCode.INCONSISTENT_CREDENTIALS));
	}

	@Test
	void testInconsistentMerchantId() throws NoSuchAlgorithmException {
		/*
		 * User respository setup.
		 */
		String userHash = Base64.getUrlEncoder().encodeToString(
			MessageDigest.getInstance("SHA256").digest(
				USERNAME.getBytes(StandardCharsets.UTF_8)));

		String passwordHash = Base64.getEncoder().encodeToString(PasswordVerifier.hashBytes(PASSWORD, SALT));

		when(repository.getUser(AZURE_TOKEN, userHash))
			.thenReturn(UniGenerator.item(new User(USERNAME, SALT, passwordHash, ACQUIRER_ID, Channel.POS, MERCHANT_2_ID)));

		/*
		 * Azure auth. client setup.
		 */
		when(authClient.getAccessToken(anyString(), anyString()))
			.thenReturn(UniGenerator.item(new GetAccessTokenResponse(TokenType.BEARER, Instant.now().getEpochSecond() + AZURE_TOKEN_DURATION, "", "", AZURE_TOKEN)));

		/*
		 * Test
		 */
		given()
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.header(HeaderParamName.REQUEST_ID, "00000000-0000-0000-0000-400000000006")
			.header(HeaderParamName.ACQUIRER_ID, ACQUIRER_ID)
			.header(HeaderParamName.CHANNEL, Channel.POS)
			.header(HeaderParamName.MERCHANT_ID, MERCHANT_ID)
			.header(HeaderParamName.TERMINAL_ID, TERMINAL_ID)
			.formParam(FormParamName.CLIENT_ID, CLIENT_ID)
			.formParam(FormParamName.GRANT_TYPE, GrantType.PASSWORD)
			.formParam(FormParamName.USERNAME, USERNAME)
			.formParam(FormParamName.PASSWORD, PASSWORD)
			.formParam(FormParamName.SCOPE, Scope.OFFLINE_ACCESS)
			.when()
			.post()
			.then()
			.statusCode(401)
			.contentType(MediaType.APPLICATION_JSON)
			.body(JsonPropertyName.ERRORS, hasItem(AuthErrorCode.INCONSISTENT_CREDENTIALS));
	}

	@Test
	void testWrongPassword() throws NoSuchAlgorithmException {
		/*
		 * User respository setup.
		 */
		String userHash = Base64.getUrlEncoder().encodeToString(
			MessageDigest.getInstance("SHA256").digest(
				USERNAME.getBytes(StandardCharsets.UTF_8)));

		String passwordHash = Base64.getEncoder().encodeToString(PasswordVerifier.hashBytes(PASSWORD_2, SALT));

		when(repository.getUser(AZURE_TOKEN, userHash))
			.thenReturn(UniGenerator.item(new User(USERNAME, SALT, passwordHash, ACQUIRER_ID, Channel.POS, MERCHANT_ID)));

		/*
		 * Azure auth. client setup.
		 */
		when(authClient.getAccessToken(anyString(), anyString()))
			.thenReturn(UniGenerator.item(new GetAccessTokenResponse(TokenType.BEARER, Instant.now().getEpochSecond() + AZURE_TOKEN_DURATION, "", "", AZURE_TOKEN)));

		/*
		 * Test
		 */
		given()
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.header(HeaderParamName.REQUEST_ID, "00000000-0000-0000-0000-400000000006")
			.header(HeaderParamName.ACQUIRER_ID, ACQUIRER_ID)
			.header(HeaderParamName.CHANNEL, Channel.POS)
			.header(HeaderParamName.MERCHANT_ID, MERCHANT_ID)
			.header(HeaderParamName.TERMINAL_ID, TERMINAL_ID)
			.formParam(FormParamName.CLIENT_ID, CLIENT_ID)
			.formParam(FormParamName.GRANT_TYPE, GrantType.PASSWORD)
			.formParam(FormParamName.USERNAME, USERNAME)
			.formParam(FormParamName.PASSWORD, PASSWORD)
			.formParam(FormParamName.SCOPE, Scope.OFFLINE_ACCESS)
			.when()
			.post()
			.then()
			.statusCode(401)
			.contentType(MediaType.APPLICATION_JSON)
			.body(JsonPropertyName.ERRORS, hasItem(AuthErrorCode.WRONG_CREDENTIALS));
	}

	// @Test
	// void testErrorFromSuper() {
	// }
}