/*
 * TokenResourceTest.java
 *
 * 19 mag 2023
 */
package it.pagopa.swclient.mil.auth.resource;

import static io.restassured.RestAssured.given;
import static it.pagopa.swclient.mil.auth.util.UniGenerator.item;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.anyString;

import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.bson.Document;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;

import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import it.pagopa.swclient.mil.auth.bean.GrantType;
import it.pagopa.swclient.mil.auth.client.PoyntClient;
import it.pagopa.swclient.mil.auth.dao.ClientEntity;
import it.pagopa.swclient.mil.auth.dao.ClientRepository;
import it.pagopa.swclient.mil.auth.dao.GrantEntity;
import it.pagopa.swclient.mil.auth.dao.GrantRepository;
import it.pagopa.swclient.mil.auth.dao.ResourceOwnerCredentialsEntity;
import it.pagopa.swclient.mil.auth.dao.ResourceOwnerCredentialsRepository;
import it.pagopa.swclient.mil.auth.service.RedisClient;
import it.pagopa.swclient.mil.auth.util.PasswordVerifier;
import it.pagopa.swclient.mil.bean.Channel;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * 
 * @author Antonio Tarricone
 */
@QuarkusTest
@TestHTTPEndpoint(TokenResource.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TokenResourceTest {
	/*
	 * 
	 */
	@InjectMock
	ClientRepository clientRepository;

	/*
	 * 
	 */
	@InjectMock
	GrantRepository grantRepository;

	/*
	 * 
	 */
	@InjectMock
	ResourceOwnerCredentialsRepository resourceOwnerCredentialsRespository;

	/*
	 *
	 */
	@InjectMock
	@RestClient
	PoyntClient poyntClient;

	/*
	 * 
	 */
	@InjectMock
	RedisClient redisClient;

	/*
	 * 
	 */
	final String clientId = "5254f087-1214-45cd-94ae-fda53c835197";
	final String acquirerId = "4585625";
	final String merchantId = "28405fHfk73x88D";
	final String terminalId = "01234567";
	final String extToken = "eyJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJOZXhpIiwicG95bnQuZGlkIjoidXJuOnRpZDo1NTYyYjhlZC1lODljLTMzMmEtYThkYy1jYTA4MTcxMzUxMTAiLCJwb3ludC5kc3QiOiJEIiwicG95bnQub3JnIjoiMGU2Zjc4ODYtMDk1Ni00NDA1LWJjNDgtYzE5ODY4ZDdlZTIyIiwicG95bnQuc2N0IjoiVSIsImlzcyI6Imh0dHBzOlwvXC9zZXJ2aWNlcy1ldS5wb3ludC5uZXQiLCJwb3ludC51cmUiOiJPIiwicG95bnQua2lkIjozOTMyNDI1MjY4MDY5NDA5MjM0LCJwb3ludC5zY3YiOiJOZXhpIiwicG95bnQuc3RyIjoiZDNmZDNmZDMtMTg5ZC00N2M4LThjMzYtYjY4NWRkNjBkOTY0IiwiYXVkIjoidXJuOnRpZDo1NTYyYjhlZC1lODljLTMzMmEtYThkYy1jYTA4MTcxMzUxMTAiLCJwb3ludC51aWQiOjM3MzY1NzQsInBveW50LmJpeiI6IjRiN2ViOTRiLTEwYzktNGYxMS1hMTBlLTcyOTJiMjlhYjExNSIsImV4cCI6MTY4NDU3NTMzNiwiaWF0IjoxNjg0NDg4OTM2LCJqdGkiOiJmNzc5MjQ1OS00ODU1LTQ5YjMtYTZiYS05N2QzNzQ5NDQ2ZGIifQ.niR8AS3OHlmWg1-n3FD4DKoAWlY0nJyEJGBZSBFWHYCl01vjIIFYCmTCyBshZVEtDBKpTG1bWTmVctOCX2ybF5gQ0vBH1H3LFD13Tf73Ps439Ht5_u3Q-jHPf_arXDf2enOs_vKwp8TsdJNPRcxMhYZ91yyiAhbHERVypP2YPszwv5h6mMq_HWNzK9qjrLh8zQCGBEMkFfnSG1xOjzTZLJ4ROPazaDHJ9DSZReC4dY_jRqAlivbXVeLOnN3D4y_GatcHQO1_p_jYE-eXHjLP-wINeAqW57P57HmSe2n67q6UkQf5v5zKVHrJpTFAtHWpDVLxmhPKGurTX45yOvaDZw";
	final String addData = "4b7eb94b-10c9-4f11-a10e-7292b29ab115";
	final String username = "mariorossi";
	final String password = "dF@dkj$S73j#fjd7X!";
	final String salt = "BhPEAxmNsm6JIidDZXl/jwIfuFUFwn/hjfoLnDuYyQEfUMQOrtlOCFljm8IYmN5OmMIh3RddWfNSJEVlRxZjig==";
	
	/**
	 * 
	 */
	private void setupForCreateTokenByPoyntToken() {
		Mockito
			.when(poyntClient.getBusinessObject(anyString(), anyString()))
			.thenReturn(item(Response.ok().build()));

		Mockito
			.when(clientRepository.findByIdOptional(clientId))
			.thenReturn(item(Optional.ofNullable(new ClientEntity(clientId, Channel.POS, null, "SmartPOS"))));

		Mockito
			.when(grantRepository.findSingleResultOptional(new Document(Map.of(
				"acquirerId", acquirerId,
				"channel", Channel.POS,
				"clientId", clientId,
				"merchantId", merchantId,
				"terminalId", "NA"))))
			.thenReturn(item(Optional.ofNullable(new GrantEntity(acquirerId, Channel.POS, clientId, merchantId, "NA", List.of(
				"verifyByQrCode",
				"activateByQrCode",
				"verifyByTaxCodeAndNoticeNumber",
				"activateByTaxCodeAndNoticeNumber",
				"close",
				"getPaymentStatus",
				"getFee")))));
	}

	/**
	 * 
	 */
	@Test
	void createTokenByPoyntTokenWithRefresh() {
		/*
		 * Setup
		 */
		setupForCreateTokenByPoyntToken();

		/*
		 * Test
		 */
		given()
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.header("RequestId", "00000000-0000-0000-0000-000000000001")
			.header("AcquirerId", acquirerId)
			.header("Channel", Channel.POS)
			.header("MerchantId", merchantId)
			.header("TerminalId", terminalId)
			.formParam("client_id", clientId)
			.formParam("grant_type", GrantType.POYNT_TOKEN)
			.formParam("ext_token", extToken)
			.formParam("add_data", addData)
			.formParam("scope", "offline_access")
			.when()
			.post()
			.then()
			.statusCode(200)
			.contentType(MediaType.APPLICATION_JSON)
			.body("access_token", notNullValue())
			.body("token_type", equalTo("Bearer"))
			.body("expires_in", notNullValue(Long.class))
			.body("refresh_token", notNullValue());
	}

	/**
	 * 
	 */
	@Test
	void createTokenByPoyntTokenWithoutRefresh() {
		/*
		 * Setup
		 */
		setupForCreateTokenByPoyntToken();

		/*
		 * Test
		 */
		given()
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.header("RequestId", "00000000-0000-0000-0000-000000000002")
			.header("AcquirerId", acquirerId)
			.header("Channel", Channel.POS)
			.header("MerchantId", merchantId)
			.header("TerminalId", terminalId)
			.formParam("client_id", clientId)
			.formParam("grant_type", GrantType.POYNT_TOKEN)
			.formParam("ext_token", extToken)
			.formParam("add_data", addData)
			.when()
			.post()
			.then()
			.statusCode(200)
			.contentType(MediaType.APPLICATION_JSON)
			.body("access_token", notNullValue())
			.body("token_type", equalTo("Bearer"))
			.body("expires_in", notNullValue(Long.class))
			.body("refresh_token", nullValue());
	}

	/**
	 * 
	 * @throws NoSuchAlgorithmException 
	 */
	private void setupForCreateTokenByPassword() throws NoSuchAlgorithmException {
		Mockito
			.when(clientRepository.findByIdOptional(clientId))
			.thenReturn(item(Optional.ofNullable(new ClientEntity(clientId, Channel.POS, null, "SmartPOS"))));

		Mockito
			.when(resourceOwnerCredentialsRespository.findByIdOptional(username))
			.thenReturn(item(Optional.ofNullable(new ResourceOwnerCredentialsEntity(username, salt, PasswordVerifier.hash(password, salt), acquirerId, Channel.POS, merchantId))));
		
		Mockito
			.when(grantRepository.findSingleResultOptional(new Document(Map.of(
				"acquirerId", acquirerId,
				"channel", Channel.POS,
				"clientId", clientId,
				"merchantId", merchantId,
				"terminalId", "NA"))))
			.thenReturn(item(Optional.ofNullable(new GrantEntity(acquirerId, Channel.POS, clientId, merchantId, "NA", List.of(
				"verifyByQrCode",
				"activateByQrCode",
				"verifyByTaxCodeAndNoticeNumber",
				"activateByTaxCodeAndNoticeNumber",
				"close",
				"getPaymentStatus",
				"getFee")))));
	}

	/**
	 * 
	 * @throws NoSuchAlgorithmException 
	 */
	@Test
	void createTokenByPasswordWithRefresh() throws NoSuchAlgorithmException {
		/*
		 * Setup
		 */
		setupForCreateTokenByPassword();

		/*
		 * Test
		 */
		given()
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.header("RequestId", "00000000-0000-0000-0000-000000000003")
			.header("AcquirerId", acquirerId)
			.header("Channel", Channel.POS)
			.header("MerchantId", merchantId)
			.header("TerminalId", terminalId)
			.formParam("client_id", clientId)
			.formParam("grant_type", GrantType.PASSWORD)
			.formParam("username", username)
			.formParam("password", password)
			.formParam("scope", "offline_access")
			.when()
			.post()
			.then()
			.statusCode(200)
			.contentType(MediaType.APPLICATION_JSON)
			.body("access_token", notNullValue())
			.body("token_type", equalTo("Bearer"))
			.body("expires_in", notNullValue(Long.class))
			.body("refresh_token", notNullValue());
	}

	/**
	 * @throws NoSuchAlgorithmException 
	 * 
	 */
	@Test
	void createTokenByPasswordWithoutRefresh() throws NoSuchAlgorithmException {
		/*
		 * Setup
		 */
		setupForCreateTokenByPassword();

		/*
		 * Test
		 */
		given()
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.header("RequestId", "00000000-0000-0000-0000-000000000003")
			.header("AcquirerId", acquirerId)
			.header("Channel", Channel.POS)
			.header("MerchantId", merchantId)
			.header("TerminalId", terminalId)
			.formParam("client_id", clientId)
			.formParam("grant_type", GrantType.PASSWORD)
			.formParam("username", username)
			.formParam("password", password)
			.when()
			.post()
			.then()
			.statusCode(200)
			.contentType(MediaType.APPLICATION_JSON)
			.body("access_token", notNullValue())
			.body("token_type", equalTo("Bearer"))
			.body("expires_in", notNullValue(Long.class))
			.body("refresh_token", nullValue());
	}

	/**
	 * 
	 */
	@Test
	void createTokenByClientSecret() {
	}

	/**
	 * 
	 */
	@Test
	void refreshToken() {
	}
}