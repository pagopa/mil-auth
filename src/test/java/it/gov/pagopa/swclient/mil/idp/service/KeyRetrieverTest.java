/*
 * KeyRetrieverTest.java
 *
 * 23 mar 2023
 */
package it.gov.pagopa.swclient.mil.idp.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.nimbusds.jose.JOSEException;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.helpers.test.UniAssertSubscriber;
import it.gov.pagopa.swclient.mil.idp.bean.KeyPair;
import it.gov.pagopa.swclient.mil.idp.bean.PublicKeys;

/**
 * 
 * @author Antonio Tarricone
 */
@QuarkusTest
public class KeyRetrieverTest {
	/*
	 * 
	 */
	@InjectMock
	RedisClient redisClient;

	/*
	 * 
	 */
	@Inject
	KeyRetriever keyRetriever;

	/*
	 * 
	 */
	@Inject
	KeyPairGenerator keyPairGenerator;

	// getKeyPair --------------------------------------------------------------

	/**
	 * Get key pair but there is no one, so it must be generated.
	 */
	@Test
	void getKeyPairWithKeyGeneration() {
		/*
		 * Setup
		 */
		Mockito
			.when(redisClient.keys("*"))
			.thenReturn(Uni.createFrom().item(new ArrayList<String>()));

		Mockito
			.when(redisClient.set(anyString(), any(KeyPair.class)))
			.thenReturn(Uni.createFrom().voidItem());

		Mockito
			.when(redisClient.expireat(anyString(), anyLong()))
			.thenReturn(Uni.createFrom().item(Boolean.TRUE));

		/*
		 * Test
		 */
		keyRetriever.getKeyPair()
			.subscribe()
			.with(
				item -> assertNotNull(item),
				fail -> assertNull(fail));
	}

	/**
	 * Get key pair but there is no one, so it must be generated but Redis fail on set method.
	 */
	@Test
	void getKeyPairWithKeyGenerationAndFailureOnSet() {
		/*
		 * Setup
		 */
		Mockito
			.when(redisClient.keys("*"))
			.thenReturn(Uni.createFrom().item(new ArrayList<String>()));

		Mockito
			.when(redisClient.set(anyString(), any(KeyPair.class)))
			.thenReturn(Uni.createFrom().failure(new Exception("synthetic exception")));

		/*
		 * Test
		 */
		keyRetriever.getKeyPair()
			.subscribe()
			.withSubscriber(UniAssertSubscriber.create())
			.assertFailed();
	}

	/**
	 * Get key pair but there is no one, so it must be generated but Redis fail on expireat method.
	 */
	@Test
	void getKeyPairWithKeyGenerationAndFailureOnExpireat() {
		/*
		 * Setup
		 */
		Mockito
			.when(redisClient.keys("*"))
			.thenReturn(Uni.createFrom().item(new ArrayList<String>()));

		Mockito
			.when(redisClient.set(anyString(), any(KeyPair.class)))
			.thenReturn(Uni.createFrom().voidItem());

		Mockito
			.when(redisClient.expireat(anyString(), anyLong()))
			.thenReturn(Uni.createFrom().failure(new Exception("synthetic exception")));

		/*
		 * Test
		 */
		/*
		 * Test
		 */
		keyRetriever.getKeyPair()
			.subscribe()
			.with(
				item -> assertNotNull(item),
				fail -> assertNull(fail));
	}

	/**
	 * 
	 * @throws JOSEException
	 */
	@Test
	void getKeyPairWithoutKeyGenerationAndTwoValidKeys() throws JOSEException {
		/*
		 * Setup
		 */
		KeyPair keyPairThatExpiresLater = keyPairGenerator.generateRsaKey();
		keyPairThatExpiresLater.setExp(keyPairThatExpiresLater.getExp() + 60000);

		KeyPair keyPair = keyPairGenerator.generateRsaKey();

		Mockito
			.when(redisClient.keys("*"))
			.thenReturn(Uni.createFrom().item(
				List.of(
					keyPairThatExpiresLater.getKid(),
					keyPair.getKid())));

		Mockito
			.when(redisClient.get(keyPairThatExpiresLater.getKid()))
			.thenReturn(Uni.createFrom().item(keyPairThatExpiresLater));

		Mockito
			.when(redisClient.get(keyPair.getKid()))
			.thenReturn(Uni.createFrom().item(keyPair));

		/*
		 * Test
		 */
		keyRetriever.getKeyPair()
			.subscribe()
			.withSubscriber(UniAssertSubscriber.create())
			.assertCompleted()
			.assertItem(keyPairThatExpiresLater);
	}

	/**
	 * 
	 */
	@Test
	void getKeyPairWithFailureOnKeys() {
		/*
		 * Setup
		 */
		Mockito
			.when(redisClient.keys("*"))
			.thenReturn(Uni.createFrom().failure(new Exception("synthetic exception")));

		/*
		 * Test
		 */
		keyRetriever.getKeyPair()
			.subscribe()
			.withSubscriber(UniAssertSubscriber.create())
			.assertFailed();
	}

	/**
	 * 
	 * @throws JOSEException
	 */
	@Test
	void getKeyPairWithFailureOnFirstGet() throws JOSEException {
		/*
		 * Setup
		 */
		KeyPair keyPair = keyPairGenerator.generateRsaKey();

		Mockito
			.when(redisClient.keys("*"))
			.thenReturn(Uni.createFrom().item(List.of(
				keyPair.getKid())));

		Mockito
			.when(redisClient.get(keyPair.getKid()))
			.thenReturn(Uni.createFrom().failure(new Exception("synthetic exception")));

		/*
		 * Test
		 */
		keyRetriever.getKeyPair()
			.subscribe()
			.withSubscriber(UniAssertSubscriber.create())
			.assertFailed();
	}

	/**
	 * 
	 * @throws JOSEException
	 */
	@Test
	void getKeyPairWithFailureOnSecondGet() throws JOSEException {
		/*
		 * Setup
		 */
		KeyPair keyPair1 = keyPairGenerator.generateRsaKey();
		KeyPair keyPair2 = keyPairGenerator.generateRsaKey();

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
		keyRetriever.getKeyPair()
			.subscribe()
			.withSubscriber(UniAssertSubscriber.create())
			.assertFailed();
	}

	// getPublicKeys -----------------------------------------------------------

	/**
	 * No key found.
	 */
	@Test()
	void getPublicKeysWithNoKeyFound() {
		/*
		 * Setup
		 */
		Mockito
			.when(redisClient.keys("*"))
			.thenReturn(Uni.createFrom().item(new ArrayList<String>()));

		/*
		 * Test
		 */
		keyRetriever.getPublicKeys()
			.subscribe()
			.withSubscriber(UniAssertSubscriber.create())
			.assertCompleted()
			.assertItem(new PublicKeys(List.of()));
	}

	/**
	 * 1 expired key + 2 valid keys
	 * 
	 * @throws JOSEException
	 */
	@Test()
	void getPublicKeysWithOneExpiredAndTwoValid() throws JOSEException {
		/*
		 * Setup
		 */
		KeyPair expiredKey = keyPairGenerator.generateRsaKey();
		expiredKey.setExp(Instant.now().getEpochSecond() - 1000);

		KeyPair validKey0 = keyPairGenerator.generateRsaKey();
		KeyPair validKey1 = keyPairGenerator.generateRsaKey();

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
		keyRetriever.getPublicKeys()
			.subscribe()
			.withSubscriber(UniAssertSubscriber.create())
			.assertCompleted()
			.assertItem(new PublicKeys(List.of(validKey0.publicKey(), validKey1.publicKey())));

	}

	/**
	 * Failure on ReactiveKeyCommands<String>.keys(String).
	 */
	@Test()
	void getPublicKeysWithFailureOnKeys() {
		/*
		 * Setup
		 */
		Mockito
			.when(redisClient.keys("*"))
			.thenReturn(Uni.createFrom().failure(new Exception("synthetic exception")));

		/*
		 * Test
		 */
		keyRetriever.getPublicKeys()
			.subscribe()
			.withSubscriber(UniAssertSubscriber.create())
			.assertFailed();

	}

	/**
	 * Failure on ReactiveValueCommands<String, KeyPair>.get(String).
	 * 
	 * @throws JOSEException
	 */
	@Test()
	void getPublicKeysWithFailureOnFirstGet() throws JOSEException {
		/*
		 * Setup
		 */
		KeyPair keyPair = keyPairGenerator.generateRsaKey();

		Mockito
			.when(redisClient.keys("*"))
			.thenReturn(Uni.createFrom().item(List.of(keyPair.getKid())));

		Mockito
			.when(redisClient.get(keyPair.getKid()))
			.thenReturn(Uni.createFrom().failure(new Exception("synthetic exception")));

		/*
		 * Test
		 */
		/*
		 * Test
		 */
		keyRetriever.getPublicKeys()
			.subscribe()
			.withSubscriber(UniAssertSubscriber.create())
			.assertFailed();
	}

	/**
	 * Failure on ReactiveValueCommands<String, KeyPair>.get(String).
	 * 
	 * @throws JOSEException
	 */
	@Test()
	void getPublicKeysWithFailureOnSecondGet() throws JOSEException {
		/*
		 * Setup
		 */
		KeyPair keyPair1 = keyPairGenerator.generateRsaKey();
		KeyPair keyPair2 = keyPairGenerator.generateRsaKey();

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
		/*
		 * Test
		 */
		keyRetriever.getPublicKeys()
			.subscribe()
			.withSubscriber(UniAssertSubscriber.create())
			.assertFailed();
	}
}