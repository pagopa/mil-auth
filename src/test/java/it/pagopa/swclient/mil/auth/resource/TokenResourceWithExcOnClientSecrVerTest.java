/*
 * TokenResourceWithExcOnClientSecrVerTest.java
 *
 * 19 mag 2023
 */
package it.pagopa.swclient.mil.auth.resource;

import static io.restassured.RestAssured.given;
import static it.pagopa.swclient.mil.auth.util.UniGenerator.item;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.anyString;

import java.security.NoSuchAlgorithmException;
import java.util.UUID;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.TestInstance;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import io.quarkus.test.InjectMock;
//import io.quarkus.test.common.http.TestHTTPEndpoint;
//import io.quarkus.test.junit.QuarkusTest;
import it.pagopa.swclient.mil.auth.bean.Client;
import it.pagopa.swclient.mil.auth.bean.GrantType;
import it.pagopa.swclient.mil.auth.client.AuthDataRepository;
import it.pagopa.swclient.mil.auth.util.PasswordVerifier;
import it.pagopa.swclient.mil.bean.Channel;
import jakarta.ws.rs.core.MediaType;

/**
 * 
 * @author Antonio Tarricone
 */
//@QuarkusTest
//@TestHTTPEndpoint(TokenResource.class)
//@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TokenResourceWithExcOnClientSecrVerTest {
	/*
	 * 
	 */
	@InjectMock
	@RestClient
	AuthDataRepository authDataRepository;

	/*
	 * 
	 */
	private String clientId;

	/*
	 * 
	 */
	private static final String ACQUIRER_ID = "4585625";
	private static final String MERCHANT_ID = "28405fHfk73x88D";
	private static final String TERMINAL_ID = "01234567";
	private static final String SALT = "BhPEAxmNsm6JIidDZXl/jwIfuFUFwn/hjfoLnDuYyQEfUMQOrtlOCFljm8IYmN5OmMIh3RddWfNSJEVlRxZjig==";
	private static final String CLIENT_SECRET = "3674f0e7-d717-44cc-a3bc-5f8f41771fea";

	/**
	 * 
	 */
	@BeforeEach
	void generateClientId() {
		clientId = UUID.randomUUID().toString();
	}

	/**
	 * TODO: This test doesn't work due to an issue with mocking static classes when Mockito is used with Quarkus.
	 * 
	 * @throws NoSuchAlgorithmException
	 */
	//@Test
	void createTokenByClientSecretWithExceptionOnSecretVerification() throws NoSuchAlgorithmException {
		/*
		 * Setup
		 */
		Mockito
			.when(authDataRepository.getClient(clientId))
			.thenReturn(item(new Client(clientId, Channel.POS, SALT, PasswordVerifier.hash(CLIENT_SECRET, SALT), "VAS Layer")));

		try (MockedStatic<PasswordVerifier> passwordVerifier = Mockito.mockStatic(PasswordVerifier.class)) {
			passwordVerifier.when(() -> PasswordVerifier.verify(anyString(), anyString(), anyString()))
			.thenThrow(new NoSuchAlgorithmException("synthetic"));

			/*
			 * Test
			 */
			given()
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.header("RequestId", "00000000-0000-0000-0000-200000000001")
				.header("AcquirerId", ACQUIRER_ID)
				.header("Channel", Channel.POS)
				.header("MerchantId", MERCHANT_ID)
				.header("TerminalId", TERMINAL_ID)
				.formParam("client_id", clientId)
				.formParam("grant_type", GrantType.CLIENT_CREDENTIALS)
				.formParam("client_secret", CLIENT_SECRET)
				.when()
				.post()
				.then()
				.statusCode(500)
				.contentType(MediaType.APPLICATION_JSON)
				.body("errors", notNullValue());
		}
	}
}