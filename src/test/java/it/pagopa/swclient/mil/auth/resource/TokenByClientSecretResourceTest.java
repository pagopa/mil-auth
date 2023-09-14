/*
 * TokenByClientSecretResourceTest.java
 *
 * 8 ago 2023
 */
package it.pagopa.swclient.mil.auth.resource;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.List;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import io.quarkus.test.InjectMock;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.mutiny.Uni;
import it.pagopa.swclient.mil.auth.AuthErrorCode;
import it.pagopa.swclient.mil.auth.azurekeyvault.bean.CreateKeyRequest;
import it.pagopa.swclient.mil.auth.azurekeyvault.bean.CreateKeyResponse;
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
import it.pagopa.swclient.mil.auth.azurekeyvault.client.AzureAuthClient;
import it.pagopa.swclient.mil.auth.azurekeyvault.client.AzureKeyVaultClient;
import it.pagopa.swclient.mil.auth.bean.Client;
import it.pagopa.swclient.mil.auth.bean.FormParamName;
import it.pagopa.swclient.mil.auth.bean.GrantType;
import it.pagopa.swclient.mil.auth.bean.HeaderParamName;
import it.pagopa.swclient.mil.auth.bean.JsonPropertyName;
import it.pagopa.swclient.mil.auth.bean.Role;
import it.pagopa.swclient.mil.auth.bean.TokenType;
import it.pagopa.swclient.mil.auth.client.AuthDataRepository;
import it.pagopa.swclient.mil.auth.util.UniGenerator;
import it.pagopa.swclient.mil.bean.Channel;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * Steps to test: clientVerifier.verify roleFinder.findRoles generateToken tokenSigner.sign
 *
 * @author Antonio Tarricone
 */
@QuarkusTest
@TestHTTPEndpoint(TokenResource.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TokenByClientSecretResourceTest {
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
	private static final String AUTHORIZATION_HDR_VALUE = TokenType.BEARER + " " + AZURE_TOKEN;

	/*
	 *
	 */
	private static final String KEY_URL = "https://mil-d-appl-kv.vault.azure.net/keys/";
	private static final String KEY_NAME = "0709643f49394529b92c19a68c8e184a";
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
    void testOk() {
        /*
         * Client repository setup.
         */
        when(repository.getClient(CLIENT_ID))
                .thenReturn(UniGenerator.item(new Client(CLIENT_ID, Channel.POS, SALT, HASH, DESCRIPTION)));

        /*
         * Roles repository setup.
         */
        when(repository.getRoles(ACQUIRER_ID, Channel.POS, CLIENT_ID, MERCHANT_ID, TERMINAL_ID))
                .thenReturn(UniGenerator.item(new Role(ACQUIRER_ID, Channel.POS, CLIENT_ID, MERCHANT_ID, TERMINAL_ID, ROLES)));

        /*
         * Azure auth. client setup.
         */
        when(authClient.getAccessToken(anyString(), anyString(), anyString(), anyString(), anyString()))
                .thenReturn(UniGenerator.item(new GetAccessTokenResponse(JsonPropertyName.TOKEN_TYPE, AZURE_TOKEN_DURATION, AZURE_TOKEN_DURATION, AZURE_TOKEN)));

        /*
         * Azure key vault setup.
         */
        long now = Instant.now().getEpochSecond();
        KeyAttributes keyAttributes = new KeyAttributes(now - 300, now + 600, now - 300, now - 300, Boolean.TRUE, KEY_RECOVERY_LEVEL, 0, Boolean.FALSE);

        when(keyVaultClient.getKeys(AUTHORIZATION_HDR_VALUE))
                .thenReturn(UniGenerator.item(new GetKeysResponse(new Key[]{
                        new Key(KEY_URL + KEY_NAME, keyAttributes)
                })));

        when(keyVaultClient.getKeyVersions(AUTHORIZATION_HDR_VALUE, KEY_NAME))
                .thenReturn(UniGenerator.item(new GetKeyVersionsResponse(new KeyVersion[]{
                        new KeyVersion(KEY_URL + KEY_NAME + "/" + KEY_VERSION, keyAttributes)
                })));

        when(keyVaultClient.getKey(AUTHORIZATION_HDR_VALUE, KEY_NAME, KEY_VERSION))
                .thenReturn(UniGenerator.item(new GetKeyResponse(new KeyDetails(KEY_URL + KEY_NAME + "/" + KEY_VERSION, KEY_TYPE, KEY_OPS, MODULUS, PUBLIC_EXPONENT, keyAttributes))));

        when(keyVaultClient.sign(eq(AUTHORIZATION_HDR_VALUE), eq(KEY_NAME), eq(KEY_VERSION), any(SignRequest.class)))
                .thenReturn(UniGenerator.item(new SignResponse(KID, EXPECTED_SIGNATURE)));

        /*
         * Test.
         */
        given()
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .header(HeaderParamName.REQUEST_ID, "00000000-0000-0000-0000-000000000000")
                .header(HeaderParamName.ACQUIRER_ID, ACQUIRER_ID)
                .header(HeaderParamName.CHANNEL, Channel.POS)
                .header(HeaderParamName.MERCHANT_ID, MERCHANT_ID)
                .header(HeaderParamName.TERMINAL_ID, TERMINAL_ID)
                .formParam(FormParamName.CLIENT_ID, CLIENT_ID)
                .formParam(FormParamName.GRANT_TYPE, GrantType.CLIENT_CREDENTIALS)
                .formParam(FormParamName.CLIENT_SECRET, SECRET)
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

	@Test
    void testOkForAtm() {
        /*
         * Client repository setup.
         */
        when(repository.getClient(CLIENT_ID))
                .thenReturn(UniGenerator.item(new Client(CLIENT_ID, Channel.ATM, SALT, HASH, DESCRIPTION)));

        /*
         * Roles repository setup.
         */
        when(repository.getRoles(ACQUIRER_ID, Channel.ATM, CLIENT_ID, "NA", TERMINAL_ID))
                .thenReturn(UniGenerator.item(new Role(ACQUIRER_ID, Channel.ATM, CLIENT_ID, "NA", TERMINAL_ID, ROLES)));

        /*
         * Azure auth. client setup.
         */
        when(authClient.getAccessToken(anyString(), anyString(), anyString(), anyString(), anyString()))
                .thenReturn(UniGenerator.item(new GetAccessTokenResponse(JsonPropertyName.TOKEN_TYPE, AZURE_TOKEN_DURATION, AZURE_TOKEN_DURATION, AZURE_TOKEN)));

        /*
         * Azure key vault setup.
         */
        long now = Instant.now().getEpochSecond();
        KeyAttributes keyAttributes = new KeyAttributes(now - 300, now + 600, now - 300, now - 300, Boolean.TRUE, KEY_RECOVERY_LEVEL, 0, Boolean.FALSE);

        when(keyVaultClient.getKeys(AUTHORIZATION_HDR_VALUE))
                .thenReturn(UniGenerator.item(new GetKeysResponse(new Key[]{
                        new Key(KEY_URL + KEY_NAME, keyAttributes)
                })));

        when(keyVaultClient.getKeyVersions(AUTHORIZATION_HDR_VALUE, KEY_NAME))
                .thenReturn(UniGenerator.item(new GetKeyVersionsResponse(new KeyVersion[]{
                        new KeyVersion(KEY_URL + KEY_NAME + "/" + KEY_VERSION, keyAttributes)
                })));

        when(keyVaultClient.getKey(AUTHORIZATION_HDR_VALUE, KEY_NAME, KEY_VERSION))
                .thenReturn(UniGenerator.item(new GetKeyResponse(new KeyDetails(KEY_URL + KEY_NAME + "/" + KEY_VERSION, KEY_TYPE, KEY_OPS, MODULUS, PUBLIC_EXPONENT, keyAttributes))));

        when(keyVaultClient.sign(eq(AUTHORIZATION_HDR_VALUE), eq(KEY_NAME), eq(KEY_VERSION), any(SignRequest.class)))
                .thenReturn(UniGenerator.item(new SignResponse(KID, EXPECTED_SIGNATURE)));

        /*
         * Test.
         */
        given()
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .header(HeaderParamName.REQUEST_ID, "00000000-0000-0000-0000-00000000000C")
                .header(HeaderParamName.ACQUIRER_ID, ACQUIRER_ID)
                .header(HeaderParamName.CHANNEL, Channel.ATM)
                .header(HeaderParamName.TERMINAL_ID, TERMINAL_ID)
                .formParam(FormParamName.CLIENT_ID, CLIENT_ID)
                .formParam(FormParamName.GRANT_TYPE, GrantType.CLIENT_CREDENTIALS)
                .formParam(FormParamName.CLIENT_SECRET, SECRET)
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

	@Test
    void testOkForPortal() {
        /*
         * Client repository setup.
         */
        when(repository.getClient(CLIENT_ID))
                .thenReturn(UniGenerator.item(new Client(CLIENT_ID, null, SALT, HASH, DESCRIPTION)));

        /*
         * Roles repository setup.
         */
        when(repository.getRoles("NA", "NA", CLIENT_ID, "NA", "NA"))
                .thenReturn(UniGenerator.item(new Role("NA", "NA", CLIENT_ID, "NA", "NA", ROLES)));

        /*
         * Azure auth. client setup.
         */
        when(authClient.getAccessToken(anyString(), anyString(), anyString(), anyString(), anyString()))
                .thenReturn(UniGenerator.item(new GetAccessTokenResponse(JsonPropertyName.TOKEN_TYPE, AZURE_TOKEN_DURATION, AZURE_TOKEN_DURATION, AZURE_TOKEN)));

        /*
         * Azure key vault setup.
         */
        long now = Instant.now().getEpochSecond();
        KeyAttributes keyAttributes = new KeyAttributes(now - 300, now + 600, now - 300, now - 300, Boolean.TRUE, KEY_RECOVERY_LEVEL, 0, Boolean.FALSE);

        when(keyVaultClient.getKeys(AUTHORIZATION_HDR_VALUE))
                .thenReturn(UniGenerator.item(new GetKeysResponse(new Key[]{
                        new Key(KEY_URL + KEY_NAME, keyAttributes)
                })));

        when(keyVaultClient.getKeyVersions(AUTHORIZATION_HDR_VALUE, KEY_NAME))
                .thenReturn(UniGenerator.item(new GetKeyVersionsResponse(new KeyVersion[]{
                        new KeyVersion(KEY_URL + KEY_NAME + "/" + KEY_VERSION, keyAttributes)
                })));

        when(keyVaultClient.getKey(AUTHORIZATION_HDR_VALUE, KEY_NAME, KEY_VERSION))
                .thenReturn(UniGenerator.item(new GetKeyResponse(new KeyDetails(KEY_URL + KEY_NAME + "/" + KEY_VERSION, KEY_TYPE, KEY_OPS, MODULUS, PUBLIC_EXPONENT, keyAttributes))));

        when(keyVaultClient.sign(eq(AUTHORIZATION_HDR_VALUE), eq(KEY_NAME), eq(KEY_VERSION), any(SignRequest.class)))
                .thenReturn(UniGenerator.item(new SignResponse(KID, EXPECTED_SIGNATURE)));

        /*
         * Test.
         */
        given()
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .header(HeaderParamName.REQUEST_ID, "00000000-0000-0000-0000-00000000000D")
                .formParam(FormParamName.CLIENT_ID, CLIENT_ID)
                .formParam(FormParamName.GRANT_TYPE, GrantType.CLIENT_CREDENTIALS)
                .formParam(FormParamName.CLIENT_SECRET, SECRET)
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

	@Test
    void testClientNotFound() {
        when(repository.getClient(anyString()))
                .thenReturn(Uni.createFrom().failure(new WebApplicationException(Response.status(Response.Status.NOT_FOUND).build())));

        given()
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .header(HeaderParamName.REQUEST_ID, "00000000-0000-0000-0000-000000000001")
                .header(HeaderParamName.ACQUIRER_ID, ACQUIRER_ID)
                .header(HeaderParamName.CHANNEL, Channel.POS)
                .header(HeaderParamName.MERCHANT_ID, MERCHANT_ID)
                .header(HeaderParamName.TERMINAL_ID, TERMINAL_ID)
                .formParam(FormParamName.CLIENT_ID, CLIENT_ID)
                .formParam(FormParamName.GRANT_TYPE, GrantType.CLIENT_CREDENTIALS)
                .formParam(FormParamName.CLIENT_SECRET, SECRET)
                .when()
                .post()
                .then()
                .log()
                .everything()
                .statusCode(401)
                .contentType(MediaType.APPLICATION_JSON)
                .body(JsonPropertyName.ERRORS, hasItem(AuthErrorCode.CLIENT_NOT_FOUND));
    }

	@Test
    void testWebApplicationExceptionSerchingClient() {
        when(repository.getClient(anyString()))
                .thenReturn(Uni.createFrom().failure(new WebApplicationException(Response.status(Response.Status.INTERNAL_SERVER_ERROR).build())));

        given()
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .header(HeaderParamName.REQUEST_ID, "00000000-0000-0000-0000-000000000002")
                .header(HeaderParamName.ACQUIRER_ID, ACQUIRER_ID)
                .header(HeaderParamName.CHANNEL, Channel.POS)
                .header(HeaderParamName.MERCHANT_ID, MERCHANT_ID)
                .header(HeaderParamName.TERMINAL_ID, TERMINAL_ID)
                .formParam(FormParamName.CLIENT_ID, CLIENT_ID)
                .formParam(FormParamName.GRANT_TYPE, GrantType.CLIENT_CREDENTIALS)
                .formParam(FormParamName.CLIENT_SECRET, SECRET)
                .when()
                .post()
                .then()
                .log()
                .everything()
                .statusCode(500)
                .contentType(MediaType.APPLICATION_JSON)
                .body(JsonPropertyName.ERRORS, hasItem(AuthErrorCode.ERROR_SEARCHING_FOR_CLIENT));
    }

	@Test
    void testExceptionSearchingClient() {
        when(repository.getClient(anyString()))
                .thenReturn(Uni.createFrom().failure(new Exception()));

        given()
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .header(HeaderParamName.REQUEST_ID, "00000000-0000-0000-0000-000000000003")
                .header(HeaderParamName.ACQUIRER_ID, ACQUIRER_ID)
                .header(HeaderParamName.CHANNEL, Channel.POS)
                .header(HeaderParamName.MERCHANT_ID, MERCHANT_ID)
                .header(HeaderParamName.TERMINAL_ID, TERMINAL_ID)
                .formParam(FormParamName.CLIENT_ID, CLIENT_ID)
                .formParam(FormParamName.GRANT_TYPE, GrantType.CLIENT_CREDENTIALS)
                .formParam(FormParamName.CLIENT_SECRET, SECRET)
                .when()
                .post()
                .then()
                .log()
                .everything()
                .statusCode(500)
                .contentType(MediaType.APPLICATION_JSON)
                .body(JsonPropertyName.ERRORS, hasItem(AuthErrorCode.ERROR_SEARCHING_FOR_CLIENT));
    }

	@Test
    void testClientHasWrongChannel() {
        when(repository.getClient(CLIENT_ID))
                .thenReturn(UniGenerator.item(new Client(CLIENT_ID, Channel.ATM, SALT, HASH, DESCRIPTION)));

        given()
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .header(HeaderParamName.REQUEST_ID, "00000000-0000-0000-0000-000000000004")
                .header(HeaderParamName.ACQUIRER_ID, ACQUIRER_ID)
                .header(HeaderParamName.CHANNEL, Channel.POS)
                .header(HeaderParamName.MERCHANT_ID, MERCHANT_ID)
                .header(HeaderParamName.TERMINAL_ID, TERMINAL_ID)
                .formParam(FormParamName.CLIENT_ID, CLIENT_ID)
                .formParam(FormParamName.GRANT_TYPE, GrantType.CLIENT_CREDENTIALS)
                .formParam(FormParamName.CLIENT_SECRET, SECRET)
                .when()
                .post()
                .then()
                .log()
                .everything()
                .statusCode(401)
                .contentType(MediaType.APPLICATION_JSON)
                .body(JsonPropertyName.ERRORS, hasItem(AuthErrorCode.WRONG_CHANNEL));
    }

	@Test
    void testWrongSecret() {
        when(repository.getClient(CLIENT_ID))
                .thenReturn(UniGenerator.item(new Client(CLIENT_ID, Channel.POS, SALT, HASH, DESCRIPTION)));

        given()
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .header(HeaderParamName.REQUEST_ID, "00000000-0000-0000-0000-000000000005")
                .header(HeaderParamName.ACQUIRER_ID, ACQUIRER_ID)
                .header(HeaderParamName.CHANNEL, Channel.POS)
                .header(HeaderParamName.MERCHANT_ID, MERCHANT_ID)
                .header(HeaderParamName.TERMINAL_ID, TERMINAL_ID)
                .formParam(FormParamName.CLIENT_ID, CLIENT_ID)
                .formParam(FormParamName.GRANT_TYPE, GrantType.CLIENT_CREDENTIALS)
                .formParam(FormParamName.CLIENT_SECRET, WRONG_SECRET)
                .when()
                .post()
                .then()
                .log()
                .everything()
                .statusCode(401)
                .contentType(MediaType.APPLICATION_JSON)
                .body(JsonPropertyName.ERRORS, hasItem(AuthErrorCode.WRONG_SECRET));
    }

	@Test
    void testWrongSecretWithNullExpected() {
        when(repository.getClient(CLIENT_ID))
                .thenReturn(UniGenerator.item(new Client(CLIENT_ID, Channel.POS, null, null, DESCRIPTION)));

        given()
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .header(HeaderParamName.REQUEST_ID, "00000000-0000-0000-0000-000000000006")
                .header(HeaderParamName.ACQUIRER_ID, ACQUIRER_ID)
                .header(HeaderParamName.CHANNEL, Channel.POS)
                .header(HeaderParamName.MERCHANT_ID, MERCHANT_ID)
                .header(HeaderParamName.TERMINAL_ID, TERMINAL_ID)
                .formParam(FormParamName.CLIENT_ID, CLIENT_ID)
                .formParam(FormParamName.GRANT_TYPE, GrantType.CLIENT_CREDENTIALS)
                .formParam(FormParamName.CLIENT_SECRET, SECRET)
                .when()
                .post()
                .then()
                .log()
                .everything()
                .statusCode(401)
                .contentType(MediaType.APPLICATION_JSON)
                .body(JsonPropertyName.ERRORS, hasItem(AuthErrorCode.WRONG_SECRET));
    }

	@Test
    void testRolesNotFound() {
        when(repository.getClient(CLIENT_ID))
                .thenReturn(UniGenerator.item(new Client(CLIENT_ID, Channel.POS, SALT, HASH, DESCRIPTION)));

        when(repository.getRoles(ACQUIRER_ID, Channel.POS, CLIENT_ID, MERCHANT_ID, TERMINAL_ID))
                .thenReturn(Uni.createFrom().failure(new WebApplicationException(Response.status(Response.Status.NOT_FOUND).build())));

        when(repository.getRoles(ACQUIRER_ID, Channel.POS, CLIENT_ID, MERCHANT_ID, "NA"))
                .thenReturn(Uni.createFrom().failure(new WebApplicationException(Response.status(Response.Status.NOT_FOUND).build())));

        when(repository.getRoles(ACQUIRER_ID, Channel.POS, CLIENT_ID, "NA", "NA"))
                .thenReturn(Uni.createFrom().failure(new WebApplicationException(Response.status(Response.Status.NOT_FOUND).build())));

        given()
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .header(HeaderParamName.REQUEST_ID, "00000000-0000-0000-0000-000000000007")
                .header(HeaderParamName.ACQUIRER_ID, ACQUIRER_ID)
                .header(HeaderParamName.CHANNEL, Channel.POS)
                .header(HeaderParamName.MERCHANT_ID, MERCHANT_ID)
                .header(HeaderParamName.TERMINAL_ID, TERMINAL_ID)
                .formParam(FormParamName.CLIENT_ID, CLIENT_ID)
                .formParam(FormParamName.GRANT_TYPE, GrantType.CLIENT_CREDENTIALS)
                .formParam(FormParamName.CLIENT_SECRET, SECRET)
                .when()
                .post()
                .then()
                .log()
                .everything()
                .statusCode(401)
                .contentType(MediaType.APPLICATION_JSON)
                .body(JsonPropertyName.ERRORS, hasItem(AuthErrorCode.ROLES_NOT_FOUND));
    }

	@Test
    void testWebApplicationExceptionSearchingRoles() {
        when(repository.getClient(CLIENT_ID))
                .thenReturn(UniGenerator.item(new Client(CLIENT_ID, Channel.POS, SALT, HASH, DESCRIPTION)));

        when(repository.getRoles(ACQUIRER_ID, Channel.POS, CLIENT_ID, MERCHANT_ID, TERMINAL_ID))
                .thenReturn(Uni.createFrom().failure(new WebApplicationException(Response.status(Response.Status.INTERNAL_SERVER_ERROR).build())));

        given()
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .header(HeaderParamName.REQUEST_ID, "00000000-0000-0000-0000-000000000008")
                .header(HeaderParamName.ACQUIRER_ID, ACQUIRER_ID)
                .header(HeaderParamName.CHANNEL, Channel.POS)
                .header(HeaderParamName.MERCHANT_ID, MERCHANT_ID)
                .header(HeaderParamName.TERMINAL_ID, TERMINAL_ID)
                .formParam(FormParamName.CLIENT_ID, CLIENT_ID)
                .formParam(FormParamName.GRANT_TYPE, GrantType.CLIENT_CREDENTIALS)
                .formParam(FormParamName.CLIENT_SECRET, SECRET)
                .when()
                .post()
                .then()
                .log()
                .everything()
                .statusCode(500)
                .contentType(MediaType.APPLICATION_JSON)
                .body(JsonPropertyName.ERRORS, hasItem(AuthErrorCode.ERROR_SEARCHING_FOR_ROLES));
    }

	@Test
    void testExceptionSearchingRoles() {
        when(repository.getClient(CLIENT_ID))
                .thenReturn(UniGenerator.item(new Client(CLIENT_ID, Channel.POS, SALT, HASH, DESCRIPTION)));

        when(repository.getRoles(ACQUIRER_ID, Channel.POS, CLIENT_ID, MERCHANT_ID, TERMINAL_ID))
                .thenReturn(Uni.createFrom().failure(new Exception()));

        given()
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .header(HeaderParamName.REQUEST_ID, "00000000-0000-0000-0000-000000000009")
                .header(HeaderParamName.ACQUIRER_ID, ACQUIRER_ID)
                .header(HeaderParamName.CHANNEL, Channel.POS)
                .header(HeaderParamName.MERCHANT_ID, MERCHANT_ID)
                .header(HeaderParamName.TERMINAL_ID, TERMINAL_ID)
                .formParam(FormParamName.CLIENT_ID, CLIENT_ID)
                .formParam(FormParamName.GRANT_TYPE, GrantType.CLIENT_CREDENTIALS)
                .formParam(FormParamName.CLIENT_SECRET, SECRET)
                .when()
                .post()
                .then()
                .log()
                .everything()
                .statusCode(500)
                .contentType(MediaType.APPLICATION_JSON)
                .body(JsonPropertyName.ERRORS, hasItem(AuthErrorCode.ERROR_SEARCHING_FOR_ROLES));
    }

	@Test
    void test401OnGetAccessToken() {
        /*
         * Client repository setup.
         */
        when(repository.getClient(CLIENT_ID))
                .thenReturn(UniGenerator.item(new Client(CLIENT_ID, Channel.POS, SALT, HASH, DESCRIPTION)));

        /*
         * Roles repository setup.
         */
        when(repository.getRoles(ACQUIRER_ID, Channel.POS, CLIENT_ID, MERCHANT_ID, TERMINAL_ID))
                .thenReturn(UniGenerator.item(new Role(ACQUIRER_ID, Channel.POS, CLIENT_ID, MERCHANT_ID, TERMINAL_ID, ROLES)));

        /*
         * Azure auth. client setup.
         */
        when(authClient.getAccessToken(anyString(), anyString(), anyString(), anyString(), anyString()))
                .thenReturn(Uni.createFrom().failure(new WebApplicationException(Response.status(Response.Status.UNAUTHORIZED).build())));

        /*
         * Test.
         */
        given()
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .header(HeaderParamName.REQUEST_ID, "00000000-0000-0000-0000-00000000000A")
                .header(HeaderParamName.ACQUIRER_ID, ACQUIRER_ID)
                .header(HeaderParamName.CHANNEL, Channel.POS)
                .header(HeaderParamName.MERCHANT_ID, MERCHANT_ID)
                .header(HeaderParamName.TERMINAL_ID, TERMINAL_ID)
                .formParam(FormParamName.CLIENT_ID, CLIENT_ID)
                .formParam(FormParamName.GRANT_TYPE, GrantType.CLIENT_CREDENTIALS)
                .formParam(FormParamName.CLIENT_SECRET, SECRET)
                .when()
                .post()
                .then()
                .log()
                .everything()
                .statusCode(500)
                .contentType(MediaType.APPLICATION_JSON)
                .body(JsonPropertyName.ERRORS, hasItem(AuthErrorCode.ERROR_FROM_AZURE));
    }

	@Test
    void test401OnGetKeys() {
        /*
         * Client repository setup.
         */
        when(repository.getClient(CLIENT_ID))
                .thenReturn(UniGenerator.item(new Client(CLIENT_ID, Channel.POS, SALT, HASH, DESCRIPTION)));

        /*
         * Roles repository setup.
         */
        when(repository.getRoles(ACQUIRER_ID, Channel.POS, CLIENT_ID, MERCHANT_ID, TERMINAL_ID))
                .thenReturn(UniGenerator.item(new Role(ACQUIRER_ID, Channel.POS, CLIENT_ID, MERCHANT_ID, TERMINAL_ID, ROLES)));

        /*
         * Azure auth. client setup.
         */
        when(authClient.getAccessToken(anyString(), anyString(), anyString(), anyString(), anyString()))
                .thenReturn(UniGenerator.item(new GetAccessTokenResponse(JsonPropertyName.TOKEN_TYPE, AZURE_TOKEN_DURATION, AZURE_TOKEN_DURATION, AZURE_TOKEN)));

        /*
         * Azure key vault setup.
         */
        when(keyVaultClient.getKeys(AUTHORIZATION_HDR_VALUE))
                .thenReturn(Uni.createFrom().failure(new WebApplicationException(Response.status(Response.Status.UNAUTHORIZED).build())));

        /*
         * Test.
         */
        given()
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .header(HeaderParamName.REQUEST_ID, "00000000-0000-0000-0000-00000000000A")
                .header(HeaderParamName.ACQUIRER_ID, ACQUIRER_ID)
                .header(HeaderParamName.CHANNEL, Channel.POS)
                .header(HeaderParamName.MERCHANT_ID, MERCHANT_ID)
                .header(HeaderParamName.TERMINAL_ID, TERMINAL_ID)
                .formParam(FormParamName.CLIENT_ID, CLIENT_ID)
                .formParam(FormParamName.GRANT_TYPE, GrantType.CLIENT_CREDENTIALS)
                .formParam(FormParamName.CLIENT_SECRET, SECRET)
                .when()
                .post()
                .then()
                .log()
                .everything()
                .statusCode(500)
                .contentType(MediaType.APPLICATION_JSON)
                .body(JsonPropertyName.ERRORS, hasItem(AuthErrorCode.ERROR_FROM_AZURE));
    }

	@Test
    void test401WithNullAccessToken() {
        /*
         * Client repository setup.
         */
        when(repository.getClient(CLIENT_ID))
                .thenReturn(UniGenerator.item(new Client(CLIENT_ID, Channel.POS, SALT, HASH, DESCRIPTION)));

        /*
         * Roles repository setup.
         */
        when(repository.getRoles(ACQUIRER_ID, Channel.POS, CLIENT_ID, MERCHANT_ID, TERMINAL_ID))
                .thenReturn(UniGenerator.item(new Role(ACQUIRER_ID, Channel.POS, CLIENT_ID, MERCHANT_ID, TERMINAL_ID, ROLES)));

        /*
         * Azure auth. client setup.
         */
        when(authClient.getAccessToken(anyString(), anyString(), anyString(), anyString(), anyString()))
                .thenReturn(UniGenerator.item(new GetAccessTokenResponse(JsonPropertyName.TOKEN_TYPE, AZURE_TOKEN_DURATION, AZURE_TOKEN_DURATION, null)));

        /*
         * Test.
         */
        given()
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .header(HeaderParamName.REQUEST_ID, "00000000-0000-0000-0000-00000000000A")
                .header(HeaderParamName.ACQUIRER_ID, ACQUIRER_ID)
                .header(HeaderParamName.CHANNEL, Channel.POS)
                .header(HeaderParamName.MERCHANT_ID, MERCHANT_ID)
                .header(HeaderParamName.TERMINAL_ID, TERMINAL_ID)
                .formParam(FormParamName.CLIENT_ID, CLIENT_ID)
                .formParam(FormParamName.GRANT_TYPE, GrantType.CLIENT_CREDENTIALS)
                .formParam(FormParamName.CLIENT_SECRET, SECRET)
                .when()
                .post()
                .then()
                .log()
                .everything()
                .statusCode(500)
                .contentType(MediaType.APPLICATION_JSON)
                .body(JsonPropertyName.ERRORS, hasItem(AuthErrorCode.AZURE_ACCESS_TOKEN_IS_NULL));
    }

	@Test
    void testExpiredKeyOnKeyCreation() {
        /*
         * Client repository setup.
         */
        when(repository.getClient(CLIENT_ID))
                .thenReturn(UniGenerator.item(new Client(CLIENT_ID, Channel.POS, SALT, HASH, DESCRIPTION)));

        /*
         * Roles repository setup.
         */
        when(repository.getRoles(ACQUIRER_ID, Channel.POS, CLIENT_ID, MERCHANT_ID, TERMINAL_ID))
                .thenReturn(UniGenerator.item(new Role(ACQUIRER_ID, Channel.POS, CLIENT_ID, MERCHANT_ID, TERMINAL_ID, ROLES)));

        /*
         * Azure auth. client setup.
         */
        when(authClient.getAccessToken(anyString(), anyString(), anyString(), anyString(), anyString()))
                .thenReturn(UniGenerator.item(new GetAccessTokenResponse(JsonPropertyName.TOKEN_TYPE, AZURE_TOKEN_DURATION, AZURE_TOKEN_DURATION, AZURE_TOKEN)));

        /*
         * Azure key vault setup.
         */
        when(keyVaultClient.getKeys(AUTHORIZATION_HDR_VALUE))
                .thenReturn(Uni.createFrom().item(new GetKeysResponse(new Key[]{})));

        long now = Instant.now().getEpochSecond();
        when(keyVaultClient.createKey(eq(AUTHORIZATION_HDR_VALUE), anyString(), any(CreateKeyRequest.class)))
                .thenReturn(Uni.createFrom().item(new CreateKeyResponse(new KeyDetails(KEY_URL + KEY_NAME + "/" + KEY_VERSION, KEY_TYPE, KEY_OPS, MODULUS, PUBLIC_EXPONENT, new KeyAttributes(now - 300, now - 100, now - 300, now - 300, Boolean.TRUE, KEY_RECOVERY_LEVEL, 0, Boolean.FALSE)))));

        /*
         * Test.
         */
        given()
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .header(HeaderParamName.REQUEST_ID, "00000000-0000-0000-0000-00000000000A")
                .header(HeaderParamName.ACQUIRER_ID, ACQUIRER_ID)
                .header(HeaderParamName.CHANNEL, Channel.POS)
                .header(HeaderParamName.MERCHANT_ID, MERCHANT_ID)
                .header(HeaderParamName.TERMINAL_ID, TERMINAL_ID)
                .formParam(FormParamName.CLIENT_ID, CLIENT_ID)
                .formParam(FormParamName.GRANT_TYPE, GrantType.CLIENT_CREDENTIALS)
                .formParam(FormParamName.CLIENT_SECRET, SECRET)
                .when()
                .post()
                .then()
                .log()
                .everything()
                .statusCode(500)
                .contentType(MediaType.APPLICATION_JSON)
                .body(JsonPropertyName.ERRORS, hasItem(AuthErrorCode.ERROR_GENERATING_KEY_PAIR));
    }

	@Test
    void testErrorOnKeyCreation() {
        /*
         * Client repository setup.
         */
        when(repository.getClient(CLIENT_ID))
                .thenReturn(UniGenerator.item(new Client(CLIENT_ID, Channel.POS, SALT, HASH, DESCRIPTION)));

        /*
         * Roles repository setup.
         */
        when(repository.getRoles(ACQUIRER_ID, Channel.POS, CLIENT_ID, MERCHANT_ID, TERMINAL_ID))
                .thenReturn(UniGenerator.item(new Role(ACQUIRER_ID, Channel.POS, CLIENT_ID, MERCHANT_ID, TERMINAL_ID, ROLES)));

        /*
         * Azure auth. client setup.
         */
        when(authClient.getAccessToken(anyString(), anyString(), anyString(), anyString(), anyString()))
                .thenReturn(UniGenerator.item(new GetAccessTokenResponse(JsonPropertyName.TOKEN_TYPE, AZURE_TOKEN_DURATION, AZURE_TOKEN_DURATION, AZURE_TOKEN)));

        /*
         * Azure key vault setup.
         */
        when(keyVaultClient.getKeys(AUTHORIZATION_HDR_VALUE))
                .thenReturn(Uni.createFrom().item(new GetKeysResponse(new Key[]{})));

        when(keyVaultClient.createKey(eq(AUTHORIZATION_HDR_VALUE), anyString(), any(CreateKeyRequest.class)))
                .thenReturn(Uni.createFrom().failure(new WebApplicationException(Response.status(Response.Status.UNAUTHORIZED).build())));

        /*
         * Test.
         */
        given()
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .header(HeaderParamName.REQUEST_ID, "00000000-0000-0000-0000-00000000000B")
                .header(HeaderParamName.ACQUIRER_ID, ACQUIRER_ID)
                .header(HeaderParamName.CHANNEL, Channel.POS)
                .header(HeaderParamName.MERCHANT_ID, MERCHANT_ID)
                .header(HeaderParamName.TERMINAL_ID, TERMINAL_ID)
                .formParam(FormParamName.CLIENT_ID, CLIENT_ID)
                .formParam(FormParamName.GRANT_TYPE, GrantType.CLIENT_CREDENTIALS)
                .formParam(FormParamName.CLIENT_SECRET, SECRET)
                .when()
                .post()
                .then()
                .log()
                .everything()
                .statusCode(500)
                .contentType(MediaType.APPLICATION_JSON)
                .body(JsonPropertyName.ERRORS, hasItem(AuthErrorCode.ERROR_GENERATING_KEY_PAIR));
    }
}
