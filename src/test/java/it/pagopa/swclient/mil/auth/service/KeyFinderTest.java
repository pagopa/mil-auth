/*
 * KeyFinderTest.java
 *
 * 23 mar 2023
 */
package it.pagopa.swclient.mil.auth.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.nimbusds.jose.JOSEException;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.helpers.test.UniAssertSubscriber;
import it.pagopa.swclient.mil.auth.bean.KeyPair;
import it.pagopa.swclient.mil.auth.bean.PublicKeys;
import jakarta.inject.Inject;

/**
 * 
 * @author Antonio Tarricone
 */
@QuarkusTest
class KeyFinderTest {
	/*
	 * 
	 */
	@InjectMock
	KeyVault redisClient;

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
			.when(redisClient.setex(anyString(), anyLong(), any(KeyPair.class)))
			.thenReturn(Uni.createFrom().voidItem());

		/*
		 * Test
		 */
		keyFinder.findKeyPair()
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
			.when(redisClient.setex(anyString(), anyLong(), any(KeyPair.class)))
			.thenReturn(Uni.createFrom().failure(new Exception("synthetic exception")));

		/*
		 * Test
		 */
		keyFinder.findKeyPair()
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
			.when(redisClient.setex(anyString(), anyLong(), any(KeyPair.class)))
			.thenReturn(Uni.createFrom().voidItem());

		/*
		 * Test
		 */
		/*
		 * Test
		 */
		keyFinder.findKeyPair()
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
		KeyPair keyPairThatExpiresLater = keyPairGenerator.generate();
		keyPairThatExpiresLater.setExp(keyPairThatExpiresLater.getExp() + 60000);

		KeyPair keyPair = keyPairGenerator.generate();

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
		keyFinder.findKeyPair()
			.subscribe()
			.withSubscriber(UniAssertSubscriber.create())
			.assertCompleted()
			.assertItem(keyPairThatExpiresLater);
	}

	/**
	 * 
	 * @throws JOSEException
	 */
	@Test
	void getKeyPairWithoutKeyGenerationAndTwoValidKeysInvertedOrder() throws JOSEException {
		/*
		 * Setup
		 */
		KeyPair keyPairThatExpiresLater = keyPairGenerator.generate();
		keyPairThatExpiresLater.setExp(keyPairThatExpiresLater.getExp() + 60000);

		KeyPair keyPair = keyPairGenerator.generate();

		Mockito
			.when(redisClient.keys("*"))
			.thenReturn(Uni.createFrom().item(
				List.of(
					keyPair.getKid(),
					keyPairThatExpiresLater.getKid())));

		Mockito
			.when(redisClient.get(keyPairThatExpiresLater.getKid()))
			.thenReturn(Uni.createFrom().item(keyPairThatExpiresLater));

		Mockito
			.when(redisClient.get(keyPair.getKid()))
			.thenReturn(Uni.createFrom().item(keyPair));

		/*
		 * Test
		 */
		keyFinder.findKeyPair()
			.subscribe()
			.withSubscriber(UniAssertSubscriber.create())
			.assertCompleted()
			.assertItem(keyPairThatExpiresLater);
	}

	/**
	 * 
	 * @throws JOSEException
	 */
	@Test
	void getKeyPairWithoutKeyGenerationAndTwoValidKeysSameExp() throws JOSEException {
		/*
		 * Setup
		 */
		KeyPair keyPair1 = keyPairGenerator.generate();

		KeyPair keyPair2 = keyPairGenerator.generate();
		keyPair2.setExp(keyPair1.getExp());

		Mockito
			.when(redisClient.keys("*"))
			.thenReturn(Uni.createFrom().item(
				List.of(
					keyPair1.getKid(),
					keyPair2.getKid())));

		Mockito
			.when(redisClient.get(keyPair1.getKid()))
			.thenReturn(Uni.createFrom().item(keyPair1));

		Mockito
			.when(redisClient.get(keyPair2.getKid()))
			.thenReturn(Uni.createFrom().item(keyPair2));

		/*
		 * Test
		 */
		keyFinder.findKeyPair()
			.subscribe()
			.withSubscriber(UniAssertSubscriber.create())
			.assertCompleted()
			.assertItem(keyPair1);
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
		keyFinder.findKeyPair()
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
		KeyPair keyPair = keyPairGenerator.generate();

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
		keyFinder.findKeyPair()
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
		keyFinder.findKeyPair()
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
		keyFinder.findPublicKeys()
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
		keyFinder.findPublicKeys()
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
		keyFinder.findPublicKeys()
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
		keyFinder.findPublicKeys()
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
		keyFinder.findPublicKeys()
			.subscribe()
			.withSubscriber(UniAssertSubscriber.create())
			.assertFailed();
	}
}