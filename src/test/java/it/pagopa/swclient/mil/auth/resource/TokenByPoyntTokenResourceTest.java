/*
 * TokenByPoyntTokenResourceTest.java
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

import java.time.Instant;
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
import it.pagopa.swclient.mil.auth.azure.keyvault.bean.KeyAttributes;
import it.pagopa.swclient.mil.auth.azure.keyvault.bean.KeyDetails;
import it.pagopa.swclient.mil.auth.azure.keyvault.bean.SignRequest;
import it.pagopa.swclient.mil.auth.azure.keyvault.bean.SignResponse;
import it.pagopa.swclient.mil.auth.azure.keyvault.client.AzureKeyVaultClient;
import it.pagopa.swclient.mil.auth.bean.Client;
import it.pagopa.swclient.mil.auth.bean.FormParamName;
import it.pagopa.swclient.mil.auth.bean.GrantType;
import it.pagopa.swclient.mil.auth.bean.HeaderParamName;
import it.pagopa.swclient.mil.auth.bean.JsonPropertyName;
import it.pagopa.swclient.mil.auth.bean.Role;
import it.pagopa.swclient.mil.auth.bean.Scope;
import it.pagopa.swclient.mil.auth.bean.TokenType;
import it.pagopa.swclient.mil.auth.client.PoyntClient;
import it.pagopa.swclient.mil.auth.service.AuthDataRepository;
import it.pagopa.swclient.mil.auth.util.UniGenerator;
import it.pagopa.swclient.mil.bean.Channel;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

/**
 * @author Antonio Tarricone
 */
@QuarkusTest
@TestHTTPEndpoint(TokenResource.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TokenByPoyntTokenResourceTest {
	/*
	 *
	 */
	private static final String ACQUIRER_ID = "4585625";
	private static final String MERCHANT_ID = "28405fHfk73x88D";
	private static final String TERMINAL_ID = "12345678";
	private static final String CLIENT_ID = "3965df56-ca9a-49e5-97e8-061433d4a25b";
	private static final String DESCRIPTION = "VAS Layer";
	private static final List<String> ROLES = List.of("NoticePayer", "SlavePos");
	private static final String EXT_TOKEN = "eyJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJOZXhpIiwicG95bnQuZGlkIjoidXJuOnRpZDo1NTYyYjhlZC1lODljLTMzMmEtYThkYy1jYTA4MTcxMzUxMTAiLCJwb3ludC5kc3QiOiJEIiwicG95bnQub3JnIjoiMGU2Zjc4ODYtMDk1Ni00NDA1LWJjNDgtYzE5ODY4ZDdlZTIyIiwicG95bnQuc2N0IjoiVSIsImlzcyI6Imh0dHBzOlwvXC9zZXJ2aWNlcy1ldS5wb3ludC5uZXQiLCJwb3ludC51cmUiOiJPIiwicG95bnQua2lkIjozOTMyNDI1MjY4MDY5NDA5MjM0LCJwb3ludC5zY3YiOiJOZXhpIiwicG95bnQuc3RyIjoiZDNmZDNmZDMtMTg5ZC00N2M4LThjMzYtYjY4NWRkNjBkOTY0IiwiYXVkIjoidXJuOnRpZDo1NTYyYjhlZC1lODljLTMzMmEtYThkYy1jYTA4MTcxMzUxMTAiLCJwb3ludC51aWQiOjM3MzY1NzQsInBveW50LmJpeiI6IjRiN2ViOTRiLTEwYzktNGYxMS1hMTBlLTcyOTJiMjlhYjExNSIsImV4cCI6MTY4NDU3NTMzNiwiaWF0IjoxNjg0NDg4OTM2LCJqdGkiOiJmNzc5MjQ1OS00ODU1LTQ5YjMtYTZiYS05N2QzNzQ5NDQ2ZGIifQ.niR8AS3OHlmWg1-n3FD4DKoAWlY0nJyEJGBZSBFWHYCl01vjIIFYCmTCyBshZVEtDBKpTG1bWTmVctOCX2ybF5gQ0vBH1H3LFD13Tf73Ps439Ht5_u3Q-jHPf_arXDf2enOs_vKwp8TsdJNPRcxMhYZ91yyiAhbHERVypP2YPszwv5h6mMq_HWNzK9qjrLh8zQCGBEMkFfnSG1xOjzTZLJ4ROPazaDHJ9DSZReC4dY_jRqAlivbXVeLOnN3D4y_GatcHQO1_p_jYE-eXHjLP-wINeAqW57P57HmSe2n67q6UkQf5v5zKVHrJpTFAtHWpDVLxmhPKGurTX45yOvaDZw";
	private static final String ADD_DATA = "4b7eb94b-10c9-4f11-a10e-7292b29ab115";

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
	@InjectMock
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

	/*
	 *
	 */
	@InjectMock
	@RestClient
	PoyntClient poyntClient;

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
	void testOk() {
		/*
		 * Poynt client setup.
		 */
		when(poyntClient.getBusinessObject(anyString(), anyString()))
			.thenReturn(UniGenerator.item(Response.ok().build()));

		/*
		 * Client repository setup.
		 */
		when(repository.getClient(CLIENT_ID))
			.thenReturn(UniGenerator.item(new Client(CLIENT_ID, Channel.POS, null, null, DESCRIPTION)));

		/*
		 * Roles repository setup.
		 */
		when(repository.getRoles(ACQUIRER_ID, Channel.POS, CLIENT_ID, MERCHANT_ID, TERMINAL_ID))
			.thenReturn(UniGenerator.item(new Role(ACQUIRER_ID, Channel.POS, CLIENT_ID, MERCHANT_ID, TERMINAL_ID, ROLES)));

		/*
		 * Azure auth. client setup.
		 */
		when(authClient.getAccessToken(anyString(), anyString()))
			.thenReturn(UniGenerator.item(new GetAccessTokenResponse(JsonPropertyName.TOKEN_TYPE, Instant.now().getEpochSecond() + AZURE_TOKEN_DURATION, "", "", AZURE_TOKEN)));

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
			.header(HeaderParamName.REQUEST_ID, "00000000-0000-0000-0000-200000000000")
			.header(HeaderParamName.ACQUIRER_ID, ACQUIRER_ID)
			.header(HeaderParamName.CHANNEL, Channel.POS)
			.header(HeaderParamName.MERCHANT_ID, MERCHANT_ID)
			.header(HeaderParamName.TERMINAL_ID, TERMINAL_ID)
			.formParam(FormParamName.CLIENT_ID, CLIENT_ID)
			.formParam(FormParamName.GRANT_TYPE, GrantType.POYNT_TOKEN)
			.formParam(FormParamName.EXT_TOKEN, EXT_TOKEN)
			.formParam(FormParamName.ADD_DATA, ADD_DATA)
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
	void testWebApplicationExceptionGettingBusinessObject() {
		/*
		 * Poynt client setup.
		 */
		when(poyntClient.getBusinessObject(anyString(), anyString()))
			.thenReturn(Uni.createFrom().failure(new WebApplicationException(Response.status(Status.UNAUTHORIZED).build())));

		/*
		 * Test
		 */
		given()
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.header(HeaderParamName.REQUEST_ID, "00000000-0000-0000-0000-200000000001")
			.header(HeaderParamName.ACQUIRER_ID, ACQUIRER_ID)
			.header(HeaderParamName.CHANNEL, Channel.POS)
			.header(HeaderParamName.MERCHANT_ID, MERCHANT_ID)
			.header(HeaderParamName.TERMINAL_ID, TERMINAL_ID)
			.formParam(FormParamName.CLIENT_ID, CLIENT_ID)
			.formParam(FormParamName.GRANT_TYPE, GrantType.POYNT_TOKEN)
			.formParam(FormParamName.EXT_TOKEN, EXT_TOKEN)
			.formParam(FormParamName.ADD_DATA, ADD_DATA)
			.formParam(FormParamName.SCOPE, Scope.OFFLINE_ACCESS)
			.when()
			.post()
			.then()
			.statusCode(401)
			.contentType(MediaType.APPLICATION_JSON)
			.body(JsonPropertyName.ERRORS, hasItem(AuthErrorCode.EXT_TOKEN_NOT_VALID));
	}

	@Test
	void testExceptionGettingBusinessObject() {
		/*
		 * Poynt client setup.
		 */
		when(poyntClient.getBusinessObject(anyString(), anyString()))
			.thenReturn(Uni.createFrom().failure(new Exception("synthetic exception")));

		/*
		 * Test
		 */
		given()
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.header(HeaderParamName.REQUEST_ID, "00000000-0000-0000-0000-200000000002")
			.header(HeaderParamName.ACQUIRER_ID, ACQUIRER_ID)
			.header(HeaderParamName.CHANNEL, Channel.POS)
			.header(HeaderParamName.MERCHANT_ID, MERCHANT_ID)
			.header(HeaderParamName.TERMINAL_ID, TERMINAL_ID)
			.formParam(FormParamName.CLIENT_ID, CLIENT_ID)
			.formParam(FormParamName.GRANT_TYPE, GrantType.POYNT_TOKEN)
			.formParam(FormParamName.EXT_TOKEN, EXT_TOKEN)
			.formParam(FormParamName.ADD_DATA, ADD_DATA)
			.formParam(FormParamName.SCOPE, Scope.OFFLINE_ACCESS)
			.when()
			.post()
			.then()
			.statusCode(500)
			.contentType(MediaType.APPLICATION_JSON)
			.body(JsonPropertyName.ERRORS, hasItem(AuthErrorCode.ERROR_VALIDATING_EXT_TOKEN));
	}

	@Test
	void test401GettingBusinessObject() {
		/*
		 * Poynt client setup.
		 */
		when(poyntClient.getBusinessObject(anyString(), anyString()))
			.thenReturn(UniGenerator.item(Response.status(Status.UNAUTHORIZED).build()));

		/*
		 * Test
		 */
		given()
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.header(HeaderParamName.REQUEST_ID, "00000000-0000-0000-0000-200000000003")
			.header(HeaderParamName.ACQUIRER_ID, ACQUIRER_ID)
			.header(HeaderParamName.CHANNEL, Channel.POS)
			.header(HeaderParamName.MERCHANT_ID, MERCHANT_ID)
			.header(HeaderParamName.TERMINAL_ID, TERMINAL_ID)
			.formParam(FormParamName.CLIENT_ID, CLIENT_ID)
			.formParam(FormParamName.GRANT_TYPE, GrantType.POYNT_TOKEN)
			.formParam(FormParamName.EXT_TOKEN, EXT_TOKEN)
			.formParam(FormParamName.ADD_DATA, ADD_DATA)
			.formParam(FormParamName.SCOPE, Scope.OFFLINE_ACCESS)
			.when()
			.post()
			.then()
			.statusCode(401)
			.contentType(MediaType.APPLICATION_JSON)
			.body(JsonPropertyName.ERRORS, hasItem(AuthErrorCode.EXT_TOKEN_NOT_VALID));
	}

	// @Test
	// void testErrorFromSuper() {
	// }
}
