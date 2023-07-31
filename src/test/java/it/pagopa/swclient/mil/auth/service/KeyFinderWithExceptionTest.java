/*
 * KeyFinderTestForException.java
 *
 * 1 giu 2023
 */
package it.pagopa.swclient.mil.auth.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.nimbusds.jose.JOSEException;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.helpers.test.UniAssertSubscriber;
import it.pagopa.swclient.mil.auth.bean.KeyPair;
import jakarta.inject.Inject;

/**
 * 
 * @author Antonio Tarricone
 */
@QuarkusTest
class KeyFinderWithExceptionTest {
	/*
	 * 
	 */
	@InjectMock
	RedisKeyVault redisClient;

	/*
	 * 
	 */
	@Inject
	KeyFinder keyFinder;

	/*
	 * 
	 */
	@InjectMock
	KeyPairGenerator keyPairGenerator;

	/**
	 * Get key pair but there is no one, so it must be generated but during generation an exception
	 * occurs.
	 * 
	 * @throws JOSEException
	 */
	@Test
	void getKeyPairWithKeyGenerationAndException() throws JOSEException {
		/*
		 * Setup
		 */
		Mockito
			.when(redisClient.keys("*"))
			.thenReturn(Uni.createFrom().item(new ArrayList<String>()));

		Mockito
			.when(redisClient.setex(anyString(), anyLong(), any(KeyPair.class)))
			.thenReturn(Uni.createFrom().voidItem());

		Mockito
			.when(keyPairGenerator.generate())
			.thenThrow(new JOSEException("synthetic"));

		/*
		 * Test
		 */
		keyFinder.findKeyPair()
			.subscribe()
			.withSubscriber(UniAssertSubscriber.create())
			.assertFailed();
	}
}