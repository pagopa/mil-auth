/*
 * JwksResourceTest.java
 *
 * 23 mar 2023
 */
package it.pagopa.swclient.mil.auth.resource;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;

import com.nimbusds.jose.JOSEException;

import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import io.smallrye.mutiny.Uni;
import it.pagopa.swclient.mil.auth.ErrorCode;
import it.pagopa.swclient.mil.auth.bean.KeyPair;
import it.pagopa.swclient.mil.auth.service.KeyPairGenerator;
import it.pagopa.swclient.mil.auth.service.RedisKeyVault;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.MediaType;

/**
 * 
 * @author Antonio Tarricone
 */
@QuarkusTest
@TestHTTPEndpoint(JwksResource.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class JwksResourceTest {
	/*
	 * 
	 */
	@InjectMock
	RedisKeyVault redisClient;

	/*
	 * 
	 */
	@Inject
	KeyPairGenerator keyPairGenerator;

	/**
	 * No key found.
	 */
	@Test()
	void noKeyFound() {
		/*
		 * Setup
		 */
		Mockito
			.when(redisClient.keys("*"))
			.thenReturn(Uni.createFrom().item(new ArrayList<String>()));

		/*
		 * Test
		 */
		given()
			.when()
			.get()
			.then()
			.statusCode(200)
			.contentType(MediaType.APPLICATION_JSON)
			.body("keys", empty());
	}

	/**
	 * 1 expired key + 2 valid keys
	 * 
	 * @throws JOSEException
	 */
	@Test()
	void oneExpiredAndTwoValid() throws JOSEException {
		/*
		 * Setup
		 */
		KeyPair expiredKey = keyPairGenerator.generate();
		expiredKey.setExp(Instant.now().toEpochMilli() - 1000);

		KeyPair validKey0 = keyPairGenerator.generate();
		KeyPair validKey1 = keyPairGenerator.generate();

		Mockito
			.when(redisClient.keys("*"))
			.thenReturn(Uni.createFrom().item(List.of(
				expiredKey.getKid(),
				validKey0.getKid(),
				validKey1.getKid())));

		Mockito
			.when(redisClient.get(expiredKey.getKid()))
			.thenReturn(Uni.createFrom().item(expiredKey));

		Mockito
			.when(redisClient.get(validKey0.getKid()))
			.thenReturn(Uni.createFrom().item(validKey0));

		Mockito
			.when(redisClient.get(validKey1.getKid()))
			.thenReturn(Uni.createFrom().item(validKey1));

		/*
		 * Test
		 */
		given()
			.when()
			.get()
			.then()
			.statusCode(200)
			.contentType(MediaType.APPLICATION_JSON)
			.body("keys[0].e", equalTo(validKey0.getE()))
			.body("keys[0].exp", equalTo(validKey0.getExp()))
			.body("keys[0].iat", equalTo(validKey0.getIat()))
			.body("keys[0].kid", equalTo(validKey0.getKid()))
			.body("keys[0].kty", equalTo(validKey0.getKty().toString()))
			.body("keys[0].n", equalTo(validKey0.getN()))
			.body("keys[0].use", equalTo(validKey0.getUse().toString()))
			.body("keys[1].e", equalTo(validKey1.getE()))
			.body("keys[1].exp", equalTo(validKey1.getExp()))
			.body("keys[1].iat", equalTo(validKey1.getIat()))
			.body("keys[1].kid", equalTo(validKey1.getKid()))
			.body("keys[1].kty", equalTo(validKey1.getKty().toString()))
			.body("keys[1].n", equalTo(validKey1.getN()))
			.body("keys[1].use", equalTo(validKey1.getUse().toString()));
	}

	/**
	 * Failure on ReactiveKeyCommands<String>.keys(String).
	 */
	@Test()
	void failureOnKeys() {
		/*
		 * Setup
		 */
		Mockito
			.when(redisClient.keys("*"))
			.thenReturn(Uni.createFrom().failure(new Exception("synthetic exception")));

		/*
		 * Test
		 */
		given()
			.when()
			.get()
			.then()
			.statusCode(500)
			.contentType(MediaType.APPLICATION_JSON)
			.body("errors", equalTo(List.of(ErrorCode.ERROR_SEARCHING_FOR_KEYS)));
	}

	/**
	 * Failure on ReactiveValueCommands<String, KeyPair>.get(String).
	 * 
	 * @throws JOSEException
	 */
	@Test()
	void failureOnFirstGet() throws JOSEException {
		/*
		 * Setup
		 */
		KeyPair keyPair = keyPairGenerator.generate();

		Mockito
			.when(redisClient.keys("*"))
			.thenReturn(Uni.createFrom().item(List.of(keyPair.getKid())));

		Mockito
			.when(redisClient.get(keyPair.getKid()))
			.thenReturn(Uni.createFrom().failure(new Exception("synthetic exception")));

		/*
		 * Test
		 */
		given()
			.when()
			.get()
			.then()
			.statusCode(500)
			.contentType(MediaType.APPLICATION_JSON)
			.body("errors", equalTo(List.of(ErrorCode.ERROR_SEARCHING_FOR_KEYS)));
	}

	/**
	 * Failure on ReactiveValueCommands<String, KeyPair>.get(String).
	 * 
	 * @throws JOSEException
	 */
	@Test()
	void failureOnSecondGet() throws JOSEException {
		/*
		 * Setup
		 */
		KeyPair keyPair1 = keyPairGenerator.generate();
		KeyPair keyPair2 = keyPairGenerator.generate();

		Mockito
			.when(redisClient.keys("*"))
			.thenReturn(Uni.createFrom().item(List.of(
				keyPair1.getKid(),
				keyPair2.getKid())));

		Mockito
			.when(redisClient.get(keyPair1.getKid()))
			.thenReturn(Uni.createFrom().item(keyPair1));

		Mockito
			.when(redisClient.get(keyPair2.getKid()))
			.thenReturn(Uni.createFrom().failure(new Exception("synthetic exception")));

		/*
		 * Test
		 */
		given()
			.when()
			.get()
			.then()
			.statusCode(500)
			.contentType(MediaType.APPLICATION_JSON)
			.body("errors", equalTo(List.of(ErrorCode.ERROR_SEARCHING_FOR_KEYS)));
	}
}