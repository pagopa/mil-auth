/*
 * TokenResourceTest.java
 *
 * 19 mag 2023
 */
package it.pagopa.swclient.mil.auth.resource;

import static io.restassured.RestAssured.given;
import static it.pagopa.swclient.mil.auth.util.KeyPairUtil.getPrivateKey;
import static it.pagopa.swclient.mil.auth.util.UniGenerator.item;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.anyString;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.bson.Document;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import io.quarkus.logging.Log;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import io.smallrye.mutiny.Uni;
import it.pagopa.swclient.mil.auth.bean.GrantType;
import it.pagopa.swclient.mil.auth.bean.KeyPair;
import it.pagopa.swclient.mil.auth.bean.Role;
import it.pagopa.swclient.mil.auth.client.PoyntClient;
import it.pagopa.swclient.mil.auth.dao.ClientEntity;
import it.pagopa.swclient.mil.auth.dao.ClientRepository;
import it.pagopa.swclient.mil.auth.dao.ResourceOwnerCredentialsEntity;
import it.pagopa.swclient.mil.auth.dao.ResourceOwnerCredentialsRepository;
import it.pagopa.swclient.mil.auth.dao.RoleEntity;
import it.pagopa.swclient.mil.auth.dao.RoleRepository;
import it.pagopa.swclient.mil.auth.service.KeyFinder;
import it.pagopa.swclient.mil.auth.service.KeyPairGenerator;
import it.pagopa.swclient.mil.auth.service.RedisClient;
import it.pagopa.swclient.mil.auth.util.PasswordVerifier;
import it.pagopa.swclient.mil.auth.util.TokenGenerator;
import it.pagopa.swclient.mil.auth.util.TokenSigner;
import it.pagopa.swclient.mil.bean.Channel;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

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
	RoleRepository grantRepository;

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
	@Inject
	KeyFinder keyFinder;

	/*
	 * 
	 */
	@Inject
	KeyPairGenerator keyPairGenerator;

	/*
	 * 
	 */
	final String clientId = "5254f087-1214-45cd-94ae-fda53c835197";
	final String clientId2 = "1e7921f7-677f-4ae0-bd51-2f580fd111e3";
	final String acquirerId = "4585625";
	final String acquirerId2 = "4585626";
	final String merchantId = "28405fHfk73x88D";
	final String terminalId = "01234567";
	final String extToken = "eyJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJOZXhpIiwicG95bnQuZGlkIjoidXJuOnRpZDo1NTYyYjhlZC1lODljLTMzMmEtYThkYy1jYTA4MTcxMzUxMTAiLCJwb3ludC5kc3QiOiJEIiwicG95bnQub3JnIjoiMGU2Zjc4ODYtMDk1Ni00NDA1LWJjNDgtYzE5ODY4ZDdlZTIyIiwicG95bnQuc2N0IjoiVSIsImlzcyI6Imh0dHBzOlwvXC9zZXJ2aWNlcy1ldS5wb3ludC5uZXQiLCJwb3ludC51cmUiOiJPIiwicG95bnQua2lkIjozOTMyNDI1MjY4MDY5NDA5MjM0LCJwb3ludC5zY3YiOiJOZXhpIiwicG95bnQuc3RyIjoiZDNmZDNmZDMtMTg5ZC00N2M4LThjMzYtYjY4NWRkNjBkOTY0IiwiYXVkIjoidXJuOnRpZDo1NTYyYjhlZC1lODljLTMzMmEtYThkYy1jYTA4MTcxMzUxMTAiLCJwb3ludC51aWQiOjM3MzY1NzQsInBveW50LmJpeiI6IjRiN2ViOTRiLTEwYzktNGYxMS1hMTBlLTcyOTJiMjlhYjExNSIsImV4cCI6MTY4NDU3NTMzNiwiaWF0IjoxNjg0NDg4OTM2LCJqdGkiOiJmNzc5MjQ1OS00ODU1LTQ5YjMtYTZiYS05N2QzNzQ5NDQ2ZGIifQ.niR8AS3OHlmWg1-n3FD4DKoAWlY0nJyEJGBZSBFWHYCl01vjIIFYCmTCyBshZVEtDBKpTG1bWTmVctOCX2ybF5gQ0vBH1H3LFD13Tf73Ps439Ht5_u3Q-jHPf_arXDf2enOs_vKwp8TsdJNPRcxMhYZ91yyiAhbHERVypP2YPszwv5h6mMq_HWNzK9qjrLh8zQCGBEMkFfnSG1xOjzTZLJ4ROPazaDHJ9DSZReC4dY_jRqAlivbXVeLOnN3D4y_GatcHQO1_p_jYE-eXHjLP-wINeAqW57P57HmSe2n67q6UkQf5v5zKVHrJpTFAtHWpDVLxmhPKGurTX45yOvaDZw";
	final String addData = "4b7eb94b-10c9-4f11-a10e-7292b29ab115";
	final String username = "mariorossi";
	final String password = "dF@dkj$S73j#fjd7X!";
	final String salt = "BhPEAxmNsm6JIidDZXl/jwIfuFUFwn/hjfoLnDuYyQEfUMQOrtlOCFljm8IYmN5OmMIh3RddWfNSJEVlRxZjig==";
	final String clientSecret = "3674f0e7-d717-44cc-a3bc-5f8f41771fea";
	final String clientSecret2 = "fe3490ca-e1dc-4f67-8348-d30f2d7d7169";

	/**
	 * 
	 */
	private void setupForCreateTokenByPoyntToken() {
		Mockito
			.when(poyntClient.getBusinessObject(anyString(), anyString()))
			.thenReturn(item(Response.ok().build()));

		Mockito
			.when(clientRepository.findByIdOptional(clientId))
			.thenReturn(item(Optional.ofNullable(new ClientEntity(clientId, Channel.POS, null, null, "SmartPOS"))));

		Mockito
			.when(grantRepository.findSingleResultOptional(new Document(Map.of(
				"acquirerId", acquirerId,
				"channel", Channel.POS,
				"clientId", clientId,
				"merchantId", merchantId,
				"terminalId", "NA"))))
			.thenReturn(item(Optional.ofNullable(new RoleEntity(acquirerId, Channel.POS, clientId, merchantId, "NA", List.of(Role.NoticePayer.name(), Role.SlavePos.name())))));
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
	void createTokenByPoyntTokenWithErrorVerifingToken1() {
		/*
		 * Setup
		 */
		Mockito
			.when(poyntClient.getBusinessObject(anyString(), anyString()))
			.thenReturn(Uni.createFrom().failure(new WebApplicationException(Response.status(Status.UNAUTHORIZED).build())));

		Mockito
			.when(clientRepository.findByIdOptional(clientId))
			.thenReturn(item(Optional.ofNullable(new ClientEntity(clientId, Channel.POS, null, null, "SmartPOS"))));

		Mockito
			.when(grantRepository.findSingleResultOptional(new Document(Map.of(
				"acquirerId", acquirerId,
				"channel", Channel.POS,
				"clientId", clientId,
				"merchantId", merchantId,
				"terminalId", "NA"))))
			.thenReturn(item(Optional.ofNullable(new RoleEntity(acquirerId, Channel.POS, clientId, merchantId, "NA", List.of(Role.NoticePayer.name(), Role.SlavePos.name())))));

		/*
		 * Test
		 */
		given()
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.header("RequestId", "00000000-0000-0000-0000-000000000012")
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
			.statusCode(401)
			.contentType(MediaType.APPLICATION_JSON)
			.body("errors", notNullValue());
	}

	/**
	 * 
	 */
	@Test
	void createTokenByPoyntTokenWithErrorVerifingToken2() {
		/*
		 * Setup
		 */
		Mockito
			.when(poyntClient.getBusinessObject(anyString(), anyString()))
			.thenReturn(Uni.createFrom().failure(new WebApplicationException()));

		Mockito
			.when(clientRepository.findByIdOptional(clientId))
			.thenReturn(item(Optional.ofNullable(new ClientEntity(clientId, Channel.POS, null, null, "SmartPOS"))));

		Mockito
			.when(grantRepository.findSingleResultOptional(new Document(Map.of(
				"acquirerId", acquirerId,
				"channel", Channel.POS,
				"clientId", clientId,
				"merchantId", merchantId,
				"terminalId", "NA"))))
			.thenReturn(item(Optional.ofNullable(new RoleEntity(acquirerId, Channel.POS, clientId, merchantId, "NA", List.of(Role.NoticePayer.name(), Role.SlavePos.name())))));

		/*
		 * Test
		 */
		given()
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.header("RequestId", "00000000-0000-0000-0000-000000000013")
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
			.statusCode(401)
			.contentType(MediaType.APPLICATION_JSON)
			.body("errors", notNullValue());
	}

	/**
	 * 
	 */
	@Test
	void createTokenByPoyntTokenWithErrorVerifingToken3() {
		/*
		 * Setup
		 */
		Mockito
			.when(poyntClient.getBusinessObject(anyString(), anyString()))
			.thenReturn(Uni.createFrom().failure(new Exception("synthetic exception")));

		Mockito
			.when(clientRepository.findByIdOptional(clientId))
			.thenReturn(item(Optional.ofNullable(new ClientEntity(clientId, Channel.POS, null, null, "SmartPOS"))));

		Mockito
			.when(grantRepository.findSingleResultOptional(new Document(Map.of(
				"acquirerId", acquirerId,
				"channel", Channel.POS,
				"clientId", clientId,
				"merchantId", merchantId,
				"terminalId", "NA"))))
			.thenReturn(item(Optional.ofNullable(new RoleEntity(acquirerId, Channel.POS, clientId, merchantId, "NA", List.of(Role.NoticePayer.name(), Role.SlavePos.name())))));

		/*
		 * Test
		 */
		given()
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.header("RequestId", "00000000-0000-0000-0000-000000000014")
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
			.statusCode(500)
			.contentType(MediaType.APPLICATION_JSON)
			.body("errors", notNullValue());
	}

	/**
	 * 
	 */
	@Test
	void createTokenByPoyntTokenWithErrorVerifingToken4() {
		/*
		 * Setup
		 */
		Mockito
			.when(poyntClient.getBusinessObject(anyString(), anyString()))
			.thenReturn(Uni.createFrom().item(Response.serverError().build()));

		Mockito
			.when(clientRepository.findByIdOptional(clientId))
			.thenReturn(item(Optional.ofNullable(new ClientEntity(clientId, Channel.POS, null, null, "SmartPOS"))));

		Mockito
			.when(grantRepository.findSingleResultOptional(new Document(Map.of(
				"acquirerId", acquirerId,
				"channel", Channel.POS,
				"clientId", clientId,
				"merchantId", merchantId,
				"terminalId", "NA"))))
			.thenReturn(item(Optional.ofNullable(new RoleEntity(acquirerId, Channel.POS, clientId, merchantId, "NA", List.of(Role.NoticePayer.name(), Role.SlavePos.name())))));

		/*
		 * Test
		 */
		given()
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.header("RequestId", "00000000-0000-0000-0000-000000000015")
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
			.statusCode(401)
			.contentType(MediaType.APPLICATION_JSON)
			.body("errors", notNullValue());
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
	 * @throws NoSuchAlgorithmException
	 */
	private void setupForCreateTokenByPassword() throws NoSuchAlgorithmException {
		Mockito
			.when(clientRepository.findByIdOptional(clientId))
			.thenReturn(item(Optional.ofNullable(new ClientEntity(clientId, Channel.POS, null, null, "SmartPOS"))));

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
			.thenReturn(item(Optional.ofNullable(new RoleEntity(acquirerId, Channel.POS, clientId, merchantId, "NA", List.of(Role.NoticePayer.name(), Role.SlavePos.name())))));
	}

	/**
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
	 */
	@Test
	void createTokenByPasswordWithoutConsistency() throws NoSuchAlgorithmException {
		/*
		 * Setup
		 */
		setupForCreateTokenByPassword();

		/*
		 * Test
		 */
		given()
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.header("RequestId", "00000000-0000-0000-0000-000000000017")
			.header("AcquirerId", acquirerId2)
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
			.statusCode(401)
			.contentType(MediaType.APPLICATION_JSON)
			.body("errors", notNullValue());
	}

	/**
	 * @throws NoSuchAlgorithmException
	 */
	@Test
	void createTokenByPasswordWithWrongPassword() throws NoSuchAlgorithmException {
		/*
		 * Setup
		 */
		setupForCreateTokenByPassword();

		/*
		 * Test
		 */
		given()
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.header("RequestId", "00000000-0000-0000-0000-000000000016")
			.header("AcquirerId", acquirerId)
			.header("Channel", Channel.POS)
			.header("MerchantId", merchantId)
			.header("TerminalId", terminalId)
			.formParam("client_id", clientId)
			.formParam("grant_type", GrantType.PASSWORD)
			.formParam("username", username)
			.formParam("password", password + password)
			.formParam("scope", "offline_access")
			.when()
			.post()
			.then()
			.statusCode(401)
			.contentType(MediaType.APPLICATION_JSON)
			.body("errors", notNullValue());
	}

	/**
	 * @throws NoSuchAlgorithmException
	 */
	@Test
	void createTokenByPasswordWithWrongUsername() throws NoSuchAlgorithmException {
		/*
		 * Setup
		 */
		setupForCreateTokenByPassword();

		/*
		 * Test
		 */
		given()
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.header("RequestId", "00000000-0000-0000-0000-000000000018")
			.header("AcquirerId", acquirerId)
			.header("Channel", Channel.POS)
			.header("MerchantId", merchantId)
			.header("TerminalId", terminalId)
			.formParam("client_id", clientId)
			.formParam("grant_type", GrantType.PASSWORD)
			.formParam("username", username + username)
			.formParam("password", password)
			.formParam("scope", "offline_access")
			.when()
			.post()
			.then()
			.statusCode(401)
			.contentType(MediaType.APPLICATION_JSON)
			.body("errors", notNullValue());
	}

	/**
	 * @throws NoSuchAlgorithmException
	 */
	@Test
	void createTokenByPasswordWithErrorSearchingCredentials() throws NoSuchAlgorithmException {
		/*
		 * Setup
		 */
		Mockito
			.when(clientRepository.findByIdOptional(clientId))
			.thenReturn(item(Optional.ofNullable(new ClientEntity(clientId, Channel.POS, null, null, "SmartPOS"))));

		Mockito
			.when(resourceOwnerCredentialsRespository.findByIdOptional(username))
			.thenReturn(Uni.createFrom().failure(new Exception("synthetic")));

		Mockito
			.when(grantRepository.findSingleResultOptional(new Document(Map.of(
				"acquirerId", acquirerId,
				"channel", Channel.POS,
				"clientId", clientId,
				"merchantId", merchantId,
				"terminalId", "NA"))))
			.thenReturn(item(Optional.ofNullable(new RoleEntity(acquirerId, Channel.POS, clientId, merchantId, "NA", List.of(Role.NoticePayer.name(), Role.SlavePos.name())))));

		/*
		 * Test
		 */
		given()
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.header("RequestId", "00000000-0000-0000-0000-000000000019")
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
			.statusCode(500)
			.contentType(MediaType.APPLICATION_JSON)
			.body("errors", notNullValue());
	}

	/**
	 * @throws NoSuchAlgorithmException
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
			.header("RequestId", "00000000-0000-0000-0000-000000000004")
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
	 * @throws NoSuchAlgorithmException
	 */
	private void setupForCreateTokenByClientSecret() throws NoSuchAlgorithmException {
		Mockito
			.when(clientRepository.findByIdOptional(clientId))
			.thenReturn(item(Optional.ofNullable(new ClientEntity(clientId, Channel.POS, salt, PasswordVerifier.hash(clientSecret, salt), "VAS Layer"))));

		Mockito
			.when(grantRepository.findSingleResultOptional(new Document(Map.of(
				"acquirerId", acquirerId,
				"channel", Channel.POS,
				"clientId", clientId,
				"merchantId", merchantId,
				"terminalId", "NA"))))
			.thenReturn(item(Optional.ofNullable(new RoleEntity(acquirerId, Channel.POS, clientId, merchantId, "NA", List.of(Role.NoticePayer.name(), Role.SlavePos.name())))));
	}

	/**
	 * @throws NoSuchAlgorithmException
	 */
	@Test
	void createTokenByClientSecret() throws NoSuchAlgorithmException {
		/*
		 * Setup
		 */
		setupForCreateTokenByClientSecret();

		/*
		 * Test
		 */
		given()
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.header("RequestId", "00000000-0000-0000-0000-000000000005")
			.header("AcquirerId", acquirerId)
			.header("Channel", Channel.POS)
			.header("MerchantId", merchantId)
			.header("TerminalId", terminalId)
			.formParam("client_id", clientId)
			.formParam("grant_type", GrantType.CLIENT_CREDENTIALS)
			.formParam("client_secret", clientSecret)
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
	 * @throws NoSuchAlgorithmException
	 */
	@Test
	void createTokenByClientSecretWithWrongSecret() throws NoSuchAlgorithmException {
		/*
		 * Setup
		 */
		setupForCreateTokenByClientSecret();

		/*
		 * Test
		 */
		given()
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.header("RequestId", "00000000-0000-0000-0000-00000000001a")
			.header("AcquirerId", acquirerId)
			.header("Channel", Channel.POS)
			.header("MerchantId", merchantId)
			.header("TerminalId", terminalId)
			.formParam("client_id", clientId)
			.formParam("grant_type", GrantType.CLIENT_CREDENTIALS)
			.formParam("client_secret", clientSecret2)
			.when()
			.post()
			.then()
			.statusCode(401)
			.contentType(MediaType.APPLICATION_JSON)
			.body("errors", notNullValue());
	}

	/**
	 * @throws NoSuchAlgorithmException
	 */
	@Test
	void createTokenByClientSecretWithWrongChannel() throws NoSuchAlgorithmException {
		/*
		 * Setup
		 */
		setupForCreateTokenByClientSecret();

		/*
		 * Test
		 */
		given()
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.header("RequestId", "00000000-0000-0000-0000-00000000001b")
			.header("AcquirerId", acquirerId)
			.header("Channel", Channel.ATM)
			.header("TerminalId", terminalId)
			.formParam("client_id", clientId)
			.formParam("grant_type", GrantType.CLIENT_CREDENTIALS)
			.formParam("client_secret", clientSecret)
			.when()
			.post()
			.then()
			.statusCode(401)
			.contentType(MediaType.APPLICATION_JSON)
			.body("errors", notNullValue());
	}

	/**
	 * @throws NoSuchAlgorithmException
	 */
	@Test
	void createTokenByClientSecretWithErrorSearchingClient() throws NoSuchAlgorithmException {
		/*
		 * Setup
		 */
		Mockito
			.when(clientRepository.findByIdOptional(clientId))
			.thenReturn(Uni.createFrom().failure(new Exception("synthetic")));

		Mockito
			.when(grantRepository.findSingleResultOptional(new Document(Map.of(
				"acquirerId", acquirerId,
				"channel", Channel.POS,
				"clientId", clientId,
				"merchantId", merchantId,
				"terminalId", "NA"))))
			.thenReturn(item(Optional.ofNullable(new RoleEntity(acquirerId, Channel.POS, clientId, merchantId, "NA", List.of(Role.NoticePayer.name(), Role.SlavePos.name())))));

		/*
		 * Test
		 */
		given()
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.header("RequestId", "00000000-0000-0000-0000-00000000001c")
			.header("AcquirerId", acquirerId)
			.header("Channel", Channel.ATM)
			.header("TerminalId", terminalId)
			.formParam("client_id", clientId)
			.formParam("grant_type", GrantType.CLIENT_CREDENTIALS)
			.formParam("client_secret", clientSecret)
			.when()
			.post()
			.then()
			.statusCode(500)
			.contentType(MediaType.APPLICATION_JSON)
			.body("errors", notNullValue());
	}

	/**
	 * @throws NoSuchAlgorithmException
	 */
	@Test
	void createTokenByClientSecretWithClientIdNotFound() throws NoSuchAlgorithmException {
		/*
		 * Setup
		 */
		setupForCreateTokenByClientSecret();

		/*
		 * Test
		 */
		given()
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.header("RequestId", "00000000-0000-0000-0000-00000000001f")
			.header("AcquirerId", acquirerId)
			.header("Channel", Channel.POS)
			.header("MerchantId", merchantId)
			.header("TerminalId", terminalId)
			.formParam("client_id", clientId2)
			.formParam("grant_type", GrantType.CLIENT_CREDENTIALS)
			.formParam("client_secret", clientSecret)
			.when()
			.post()
			.then()
			.statusCode(401)
			.contentType(MediaType.APPLICATION_JSON)
			.body("errors", notNullValue());
	}

	/**
	 * @throws NoSuchAlgorithmException
	 */
	private void setupForCreateTokenByClientSecretForNodo() throws NoSuchAlgorithmException {
		Mockito
			.when(clientRepository.findByIdOptional(clientId))
			.thenReturn(item(Optional.ofNullable(new ClientEntity(clientId, null, salt, PasswordVerifier.hash(clientSecret, salt), "Nodo"))));

		Mockito
			.when(grantRepository.findSingleResultOptional(new Document(Map.of(
				"acquirerId", "NA",
				"channel", "NA",
				"clientId", clientId,
				"merchantId", "NA",
				"terminalId", "NA"))))
			.thenReturn(item(Optional.ofNullable(new RoleEntity("NA", "NA", clientId, "NA", "NA", List.of(Role.Nodo.name())))));
	}

	/**
	 * @throws NoSuchAlgorithmException
	 */
	@Test
	void createTokenByClientSecretForNodo() throws NoSuchAlgorithmException {
		/*
		 * Setup
		 */
		setupForCreateTokenByClientSecretForNodo();

		/*
		 * Test
		 */
		given()
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.header("RequestId", "00000000-0000-0000-0000-000000000006")
			.formParam("client_id", clientId)
			.formParam("grant_type", GrantType.CLIENT_CREDENTIALS)
			.formParam("client_secret", clientSecret)
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
	 * @throws JOSEException
	 * @throws InvalidKeySpecException
	 * @throws NoSuchAlgorithmException
	 */
	@Test
	void refreshToken() throws JOSEException, NoSuchAlgorithmException, InvalidKeySpecException {
		/*
		 * Setup
		 */
		KeyPair keyPair = keyPairGenerator.generate();

		Mockito
			.when(redisClient.keys("*"))
			.thenReturn(Uni.createFrom().item(
				List.of(
					keyPair.getKid())));

		Mockito
			.when(redisClient.get(keyPair.getKid()))
			.thenReturn(Uni.createFrom().item(keyPair));

		Mockito
			.when(clientRepository.findByIdOptional(clientId))
			.thenReturn(item(Optional.ofNullable(new ClientEntity(clientId, Channel.POS, null, null, "SmartPOS"))));

		Mockito
			.when(grantRepository.findSingleResultOptional(new Document(Map.of(
				"acquirerId", acquirerId,
				"channel", Channel.POS,
				"clientId", clientId,
				"merchantId", merchantId,
				"terminalId", "NA"))))
			.thenReturn(item(Optional.ofNullable(new RoleEntity(acquirerId, Channel.POS, clientId, merchantId, "NA", List.of(Role.NoticePayer.name(), Role.SlavePos.name())))));

		String token = TokenGenerator.generate(acquirerId, Channel.POS, merchantId, clientId, terminalId, 24 * 60 * 60 * 1000, null, List.of("offline_access"), keyPair);

		/*
		 * Test
		 */
		given()
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.header("RequestId", "00000000-0000-0000-0000-000000000007")
			.header("AcquirerId", acquirerId)
			.header("Channel", Channel.POS)
			.header("MerchantId", merchantId)
			.header("TerminalId", terminalId)
			.formParam("client_id", clientId)
			.formParam("grant_type", GrantType.REFRESH_TOKEN)
			.formParam("refresh_token", token)
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
	 * @throws JOSEException
	 * 
	 */
	@Test
	void refreshTokenWithBadToken() throws JOSEException {
		/*
		 * Test
		 */
		given()
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.header("RequestId", "00000000-0000-0000-0000-000000000008")
			.header("AcquirerId", acquirerId)
			.header("Channel", Channel.POS)
			.header("MerchantId", merchantId)
			.header("TerminalId", terminalId)
			.formParam("client_id", clientId)
			.formParam("grant_type", GrantType.REFRESH_TOKEN)
			.formParam("refresh_token", "a.a.a")
			.when()
			.post()
			.then()
			.statusCode(500)
			.contentType(MediaType.APPLICATION_JSON)
			.body("errors", notNullValue());
	}

	/**
	 * @throws JOSEException
	 * @throws InvalidKeySpecException
	 * @throws NoSuchAlgorithmException
	 */
	@Test
	void refreshTokenWithMissingKey() throws JOSEException, NoSuchAlgorithmException, InvalidKeySpecException {
		/*
		 * Setup
		 */
		setupForCreateTokenByPoyntToken();

		KeyPair keyPair = keyPairGenerator.generate();

		Mockito
			.when(redisClient.keys("*"))
			.thenReturn(Uni.createFrom().item(List.of()));

		String token = TokenGenerator.generate(acquirerId, Channel.POS, merchantId, clientId, terminalId, 24 * 60 * 60 * 1000, null, List.of("offline_access"), keyPair);

		given()
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.header("RequestId", "00000000-0000-0000-0000-000000000009")
			.header("AcquirerId", acquirerId)
			.header("Channel", Channel.POS)
			.header("MerchantId", merchantId)
			.header("TerminalId", terminalId)
			.formParam("client_id", clientId)
			.formParam("grant_type", GrantType.REFRESH_TOKEN)
			.formParam("refresh_token", token)
			.when()
			.post()
			.then()
			.statusCode(401)
			.contentType(MediaType.APPLICATION_JSON)
			.body("errors", notNullValue());
	}

	/**
	 * @throws JOSEException
	 * @throws InvalidKeySpecException
	 * @throws NoSuchAlgorithmException
	 */
	@Test
	void refreshTokenWithExpiredKey() throws JOSEException, NoSuchAlgorithmException, InvalidKeySpecException {
		/*
		 * Setup
		 */
		setupForCreateTokenByPoyntToken();

		KeyPair keyPair = keyPairGenerator.generate();
		keyPair.setExp(keyPair.getExp() - 24 * 60 * 60 * 1000);

		Mockito
			.when(redisClient.keys("*"))
			.thenReturn(Uni.createFrom().item(
				List.of(
					keyPair.getKid())));

		Mockito
			.when(redisClient.get(keyPair.getKid()))
			.thenReturn(Uni.createFrom().item(keyPair));

		String token = TokenGenerator.generate(acquirerId, Channel.POS, merchantId, clientId, terminalId, 24 * 60 * 60 * 1000, null, List.of("offline_access"), keyPair);

		/*
		 * Test
		 */
		given()
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.header("RequestId", "00000000-0000-0000-0000-00000000000a")
			.header("AcquirerId", acquirerId)
			.header("Channel", Channel.POS)
			.header("MerchantId", merchantId)
			.header("TerminalId", terminalId)
			.formParam("client_id", clientId)
			.formParam("grant_type", GrantType.REFRESH_TOKEN)
			.formParam("refresh_token", token)
			.when()
			.post()
			.then()
			.statusCode(401)
			.contentType(MediaType.APPLICATION_JSON)
			.body("errors", notNullValue());
	}

	/**
	 * @throws JOSEException
	 * @throws ParseException
	 * @throws InvalidKeySpecException
	 * @throws NoSuchAlgorithmException
	 */
	@Test
	void refreshTokenWithExpiredToken() throws JOSEException, ParseException, NoSuchAlgorithmException, InvalidKeySpecException {
		/*
		 * Setup
		 */
		KeyPair keyPair = keyPairGenerator.generate();

		Mockito
			.when(redisClient.keys("*"))
			.thenReturn(Uni.createFrom().item(
				List.of(
					keyPair.getKid())));

		Mockito
			.when(redisClient.get(keyPair.getKid()))
			.thenReturn(Uni.createFrom().item(keyPair));

		String token = TokenGenerator.generate(acquirerId, Channel.POS, merchantId, clientId, terminalId, -24 * 60 * 60 * 1000, null, List.of("offline_access"), keyPair);

		/*
		 * Test
		 */
		given()
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.header("RequestId", "00000000-0000-0000-0000-00000000000b")
			.header("AcquirerId", acquirerId)
			.header("Channel", Channel.POS)
			.header("MerchantId", merchantId)
			.header("TerminalId", terminalId)
			.formParam("client_id", clientId)
			.formParam("grant_type", GrantType.REFRESH_TOKEN)
			.formParam("refresh_token", token)
			.when()
			.post()
			.then()
			.statusCode(401)
			.contentType(MediaType.APPLICATION_JSON)
			.body("errors", notNullValue());
	}

	/**
	 * @throws JOSEException
	 * @throws ParseException
	 * @throws InvalidKeySpecException
	 * @throws NoSuchAlgorithmException
	 */
	@Test
	void refreshTokenWithWrongSignature() throws JOSEException, ParseException, NoSuchAlgorithmException, InvalidKeySpecException {
		/*
		 * Setup
		 */
		KeyPair keyPair = keyPairGenerator.generate();

		Mockito
			.when(redisClient.keys("*"))
			.thenReturn(Uni.createFrom().item(
				List.of(
					keyPair.getKid())));

		Mockito
			.when(redisClient.get(keyPair.getKid()))
			.thenReturn(Uni.createFrom().item(keyPair));

		KeyPair anotherKey = keyPairGenerator.generate();
		anotherKey.setKid(keyPair.getKid());

		String token = TokenGenerator.generate(acquirerId, Channel.POS, merchantId, clientId, terminalId, 24 * 60 * 60 * 1000, null, List.of("offline_access"), anotherKey);

		/*
		 * Test
		 */
		given()
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.header("RequestId", "00000000-0000-0000-0000-00000000000c")
			.header("AcquirerId", acquirerId)
			.header("Channel", Channel.POS)
			.header("MerchantId", merchantId)
			.header("TerminalId", terminalId)
			.formParam("client_id", clientId)
			.formParam("grant_type", GrantType.REFRESH_TOKEN)
			.formParam("refresh_token", token)
			.when()
			.post()
			.then()
			.statusCode(401)
			.contentType(MediaType.APPLICATION_JSON)
			.body("errors", notNullValue());
	}

	/**
	 * @throws JOSEException
	 * @throws ParseException
	 * @throws InvalidKeySpecException
	 * @throws NoSuchAlgorithmException
	 */
	@Test
	void refreshTokenWithWrongAlgorithm() throws JOSEException, ParseException, NoSuchAlgorithmException, InvalidKeySpecException {
		/*
		 * Setup
		 */
		KeyPair keyPair = keyPairGenerator.generate();

		Mockito
			.when(redisClient.keys("*"))
			.thenReturn(Uni.createFrom().item(
				List.of(
					keyPair.getKid())));

		Mockito
			.when(redisClient.get(keyPair.getKid()))
			.thenReturn(Uni.createFrom().item(keyPair));

		Date now = new Date();
		JWTClaimsSet payload = new JWTClaimsSet.Builder()
			.subject(clientId)
			.issueTime(now)
			.expirationTime(new Date(now.getTime() + 24 * 60 * 60 * 1000))
			.claim("acquirerId", acquirerId)
			.claim("channel", Channel.POS)
			.claim("merchantId", merchantId)
			.claim("clientId", clientId)
			.claim("terminalId", terminalId)
			.claim("scope", "offline_access")
			.build();

		JWSHeader header = new JWSHeader(JWSAlgorithm.RS512, null, null, null, null, null, null, null, null, null, keyPair.getKid(), true, null, null);
		SignedJWT token = new SignedJWT(header, payload);
		JWSSigner signer = new RSASSASigner(getPrivateKey(keyPair));
		token.sign(signer);

		/*
		 * Test
		 */
		given()
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.header("RequestId", "00000000-0000-0000-0000-00000000001d")
			.header("AcquirerId", acquirerId)
			.header("Channel", Channel.POS)
			.header("MerchantId", merchantId)
			.header("TerminalId", terminalId)
			.formParam("client_id", clientId)
			.formParam("grant_type", GrantType.REFRESH_TOKEN)
			.formParam("refresh_token", token.serialize())
			.when()
			.post()
			.then()
			.statusCode(401)
			.contentType(MediaType.APPLICATION_JSON)
			.body("errors", notNullValue());
	}

	/**
	 * @throws JOSEException
	 * @throws ParseException
	 * @throws InvalidKeySpecException
	 * @throws NoSuchAlgorithmException
	 */
	@Test
	void refreshTokenWithoutScope() throws JOSEException, ParseException, NoSuchAlgorithmException, InvalidKeySpecException {
		/*
		 * Setup
		 */
		setupForCreateTokenByPoyntToken();

		KeyPair keyPair = keyPairGenerator.generate();

		Mockito
			.when(redisClient.keys("*"))
			.thenReturn(Uni.createFrom().item(
				List.of(
					keyPair.getKid())));

		Mockito
			.when(redisClient.get(keyPair.getKid()))
			.thenReturn(Uni.createFrom().item(keyPair));

		String token = TokenGenerator.generate(acquirerId, Channel.POS, merchantId, clientId, terminalId, 24 * 60 * 60 * 1000, null, null, keyPair);

		/*
		 * Test
		 */
		given()
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.header("RequestId", "00000000-0000-0000-0000-00000000000d")
			.header("AcquirerId", acquirerId)
			.header("Channel", Channel.POS)
			.header("MerchantId", merchantId)
			.header("TerminalId", terminalId)
			.formParam("client_id", clientId)
			.formParam("grant_type", GrantType.REFRESH_TOKEN)
			.formParam("refresh_token", token)
			.when()
			.post()
			.then()
			.statusCode(401)
			.contentType(MediaType.APPLICATION_JSON)
			.body("errors", notNullValue());
	}

	/**
	 * @throws JOSEException
	 * @throws ParseException
	 * @throws InvalidKeySpecException
	 * @throws NoSuchAlgorithmException
	 */
	@Test
	void refreshTokenWithWrongIssueTime() throws JOSEException, ParseException, NoSuchAlgorithmException, InvalidKeySpecException {
		/*
		 * Setup
		 */
		setupForCreateTokenByPoyntToken();

		KeyPair keyPair = keyPairGenerator.generate();

		Mockito
			.when(redisClient.keys("*"))
			.thenReturn(Uni.createFrom().item(
				List.of(
					keyPair.getKid())));

		Mockito
			.when(redisClient.get(keyPair.getKid()))
			.thenReturn(Uni.createFrom().item(keyPair));

		Date now = new Date();
		JWTClaimsSet payload = new JWTClaimsSet.Builder()
			.subject(clientId)
			.issueTime(new Date(now.getTime() + 5 * 60 * 1000))
			.expirationTime(new Date(now.getTime() + 10 * 60 * 1000))
			.claim("acquirerId", acquirerId)
			.claim("channel", Channel.POS)
			.claim("merchantId", merchantId)
			.claim("clientId", clientId)
			.claim("terminalId", terminalId)
			.claim("scope", "offline_access")
			.build();
		Log.debug("Token signing.");
		SignedJWT token = TokenSigner.sign(keyPair, payload);

		/*
		 * Test
		 */
		given()
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.header("RequestId", "00000000-0000-0000-0000-00000000000e")
			.header("AcquirerId", acquirerId)
			.header("Channel", Channel.POS)
			.header("MerchantId", merchantId)
			.header("TerminalId", terminalId)
			.formParam("client_id", clientId)
			.formParam("grant_type", GrantType.REFRESH_TOKEN)
			.formParam("refresh_token", token.serialize())
			.when()
			.post()
			.then()
			.statusCode(401)
			.contentType(MediaType.APPLICATION_JSON)
			.body("errors", notNullValue());
	}

	/**
	 * @throws JOSEException
	 * @throws ParseException
	 * @throws InvalidKeySpecException
	 * @throws NoSuchAlgorithmException
	 */
	@Test
	void refreshTokenWithoutIssueTime() throws JOSEException, ParseException, NoSuchAlgorithmException, InvalidKeySpecException {
		/*
		 * Setup
		 */
		setupForCreateTokenByPoyntToken();

		KeyPair keyPair = keyPairGenerator.generate();

		Mockito
			.when(redisClient.keys("*"))
			.thenReturn(Uni.createFrom().item(
				List.of(
					keyPair.getKid())));

		Mockito
			.when(redisClient.get(keyPair.getKid()))
			.thenReturn(Uni.createFrom().item(keyPair));

		Date now = new Date();
		JWTClaimsSet payload = new JWTClaimsSet.Builder()
			.subject(clientId)
			.expirationTime(new Date(now.getTime() + 10 * 60 * 1000))
			.claim("acquirerId", acquirerId)
			.claim("channel", Channel.POS)
			.claim("merchantId", merchantId)
			.claim("clientId", clientId)
			.claim("terminalId", terminalId)
			.claim("scope", "offline_access")
			.build();
		Log.debug("Token signing.");
		SignedJWT token = TokenSigner.sign(keyPair, payload);

		/*
		 * Test
		 */
		given()
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.header("RequestId", "00000000-0000-0000-0000-00000000000f")
			.header("AcquirerId", acquirerId)
			.header("Channel", Channel.POS)
			.header("MerchantId", merchantId)
			.header("TerminalId", terminalId)
			.formParam("client_id", clientId)
			.formParam("grant_type", GrantType.REFRESH_TOKEN)
			.formParam("refresh_token", token.serialize())
			.when()
			.post()
			.then()
			.statusCode(401)
			.contentType(MediaType.APPLICATION_JSON)
			.body("errors", notNullValue());
	}

	/**
	 * @throws JOSEException
	 * @throws ParseException
	 * @throws InvalidKeySpecException
	 * @throws NoSuchAlgorithmException
	 */
	@Test
	void refreshTokenWithoutExpiration() throws JOSEException, ParseException, NoSuchAlgorithmException, InvalidKeySpecException {
		/*
		 * Setup
		 */
		setupForCreateTokenByPoyntToken();

		KeyPair keyPair = keyPairGenerator.generate();

		Mockito
			.when(redisClient.keys("*"))
			.thenReturn(Uni.createFrom().item(
				List.of(
					keyPair.getKid())));

		Mockito
			.when(redisClient.get(keyPair.getKid()))
			.thenReturn(Uni.createFrom().item(keyPair));

		Date now = new Date();
		JWTClaimsSet payload = new JWTClaimsSet.Builder()
			.subject(clientId)
			.issueTime(now)
			.claim("acquirerId", acquirerId)
			.claim("channel", Channel.POS)
			.claim("merchantId", merchantId)
			.claim("clientId", clientId)
			.claim("terminalId", terminalId)
			.claim("scope", "offline_access")
			.build();
		Log.debug("Token signing.");
		SignedJWT token = TokenSigner.sign(keyPair, payload);

		/*
		 * Test
		 */
		given()
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.header("RequestId", "00000000-0000-0000-0000-000000000010")
			.header("AcquirerId", acquirerId)
			.header("Channel", Channel.POS)
			.header("MerchantId", merchantId)
			.header("TerminalId", terminalId)
			.formParam("client_id", clientId)
			.formParam("grant_type", GrantType.REFRESH_TOKEN)
			.formParam("refresh_token", token.serialize())
			.when()
			.post()
			.then()
			.statusCode(401)
			.contentType(MediaType.APPLICATION_JSON)
			.body("errors", notNullValue());
	}

	/**
	 * @throws JOSEException
	 * @throws ParseException
	 * @throws InvalidKeySpecException
	 * @throws NoSuchAlgorithmException
	 */
	@Test
	void refreshTokenWithErrorSearchingPublicKey() throws JOSEException, ParseException, NoSuchAlgorithmException, InvalidKeySpecException {
		/*
		 * Setup
		 */
		setupForCreateTokenByPoyntToken();

		KeyPair keyPair = keyPairGenerator.generate();

		Mockito
			.when(redisClient.keys("*"))
			.thenReturn(Uni.createFrom().item(
				List.of(
					keyPair.getKid())));

		Mockito
			.when(redisClient.get(keyPair.getKid()))
			.thenReturn(Uni.createFrom().item(keyPair));

		String token = TokenGenerator.generate(acquirerId, Channel.POS, merchantId, clientId, terminalId, 24 * 60 * 60 * 1000, null, List.of("offline_access"), keyPair);

		Mockito
			.when(keyFinder.findPublicKey(keyPair.getKid()))
			.thenReturn(Uni.createFrom().failure(new Exception("synthetic exception")));

		/*
		 * Test
		 */
		given()
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.header("RequestId", "00000000-0000-0000-0000-000000000011")
			.header("AcquirerId", acquirerId)
			.header("Channel", Channel.POS)
			.header("MerchantId", merchantId)
			.header("TerminalId", terminalId)
			.formParam("client_id", clientId)
			.formParam("grant_type", GrantType.REFRESH_TOKEN)
			.formParam("refresh_token", token)
			.when()
			.post()
			.then()
			.statusCode(500)
			.contentType(MediaType.APPLICATION_JSON)
			.body("errors", notNullValue());
	}
}