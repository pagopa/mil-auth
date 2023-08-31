/*
 * ClientVerifierTest.java
 *
 * 7 ago 2023
 */
package it.pagopa.swclient.mil.auth.service;

import static jakarta.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;
import static jakarta.ws.rs.core.Response.Status.NOT_FOUND;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.security.NoSuchAlgorithmException;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.helpers.test.UniAssertSubscriber;
import it.pagopa.swclient.mil.auth.bean.Client;
import it.pagopa.swclient.mil.auth.client.AuthDataRepository;
import it.pagopa.swclient.mil.auth.util.AuthError;
import it.pagopa.swclient.mil.auth.util.AuthException;
import it.pagopa.swclient.mil.auth.util.PasswordVerifier;
import it.pagopa.swclient.mil.auth.util.UniGenerator;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

/**
 * 
 * @author Antonio Tarricone
 */
@QuarkusTest
class ClientVerifierTest {
	/*
	 * 
	 */
	private static final String ID = "3965df56-ca9a-49e5-97e8-061433d4a25b";
	private static final String CHANNEL = "POS";
	private static final String SALT = "aGw/h/8Fm9S2aNvlvIaxJyhKP67ZU4FEm6mDVhL3aEVrahXFif9x2BkQ4OY87Z9tWVyWbSB/JeztYVmTshrFWQ==";
	private static final String HASH = "G3oYMwnLVR9+m7WB4/pvoVeHxzsTdeyhndpVoruHzog=";
	private static final String SECRET = "5ceef788-4115-43a7-a704-b1bcc9a47c86";
	private static final String DESCRIPTION = "VAS Layer";
	private static final String WRONG_CHANNEL = "ATM";
	private static final String WRONG_SECRET = "3674f0e7-d717-44cc-a3bc-5f8f41771fea";

	/*
	 * 
	 */
	@InjectMock
	@RestClient
	AuthDataRepository repository;

	/*
	 * 
	 */
	@Inject
	ClientVerifier verifier;

	/**
	 * Test method for {@link it.pagopa.swclient.mil.auth.service.ClientVerifier#findClient(java.lang.String)}.
	 */
	@Test
	void testFindClientWithNotFound() {
		when(repository.getClient(anyString()))
			.thenReturn(Uni.createFrom().failure(new WebApplicationException(Response.status(NOT_FOUND).build())));
			
		verifier.findClient(ID)
			.subscribe()
			.withSubscriber(UniAssertSubscriber.create())
			.assertFailedWith(AuthException.class);
		
	}

	/**
	 * Test method for {@link it.pagopa.swclient.mil.auth.service.ClientVerifier#findClient(java.lang.String)}.
	 */
	@Test
	void testFindClientWithError1() {
		when(repository.getClient(anyString()))
			.thenReturn(Uni.createFrom().failure(new WebApplicationException(Response.status(INTERNAL_SERVER_ERROR).build())));
		
		verifier.findClient(ID)
			.subscribe()
			.withSubscriber(UniAssertSubscriber.create())
			.assertFailedWith(AuthError.class);
	}

	/**
	 * Test method for
	 * {@link it.pagopa.swclient.mil.auth.service.ClientVerifier#findClient(java.lang.String)}.
	 */
	@Test
	void testFindClientWithError2() {
		when(repository.getClient(anyString()))
			.thenReturn(Uni.createFrom().failure(new Exception()));
	
		verifier.findClient(ID)
			.subscribe()
			.withSubscriber(UniAssertSubscriber.create())
			.assertFailedWith(AuthError.class);
	}

	/**
	 * Test method for
	 * {@link it.pagopa.swclient.mil.auth.service.ClientVerifier#findClient(java.lang.String)}.
	 */
	@Test
	void testFindClientOk() {
		Client client = new Client(ID, CHANNEL, SALT, HASH, DESCRIPTION);

		when(repository.getClient(ID))
			.thenReturn(UniGenerator.item(client));

		verifier.findClient(ID)
			.subscribe()
			.withSubscriber(UniAssertSubscriber.create())
			.assertCompleted()
			.assertItem(client);
	}

	/**
	 * Test method for
	 * {@link it.pagopa.swclient.mil.auth.service.ClientVerifier#verify(java.lang.String, java.lang.String, java.lang.String)}.
	 */
	@Test
	void testVerifyWithWrongChannel() {
		Client client = new Client(ID, CHANNEL, SALT, HASH, DESCRIPTION);

		when(repository.getClient(ID))
			.thenReturn(UniGenerator.item(client));

		verifier.verify(ID, WRONG_CHANNEL, SECRET)
			.subscribe()
			.withSubscriber(UniAssertSubscriber.create())
			.assertFailedWith(AuthException.class);
	}

	/**
	 * Test method for
	 * {@link it.pagopa.swclient.mil.auth.service.ClientVerifier#verify(java.lang.String, java.lang.String, java.lang.String)}.
	 */
	@Test
	void testVerifyWithWrongSecret() {
		Client client = new Client(ID, CHANNEL, SALT, HASH, DESCRIPTION);

		when(repository.getClient(ID))
			.thenReturn(UniGenerator.item(client));

		verifier.verify(ID, CHANNEL, WRONG_SECRET)
			.subscribe()
			.withSubscriber(UniAssertSubscriber.create())
			.assertFailedWith(AuthException.class);
	}

	/**
	 * Test method for
	 * {@link it.pagopa.swclient.mil.auth.service.ClientVerifier#verify(java.lang.String, java.lang.String, java.lang.String)}.
	 */
	@Test
	void testVerifyWithNullSecretExpectedButNonNullValueFound() {
		Client client = new Client(ID, CHANNEL, SALT, HASH, DESCRIPTION);

		when(repository.getClient(ID))
			.thenReturn(UniGenerator.item(client));

		verifier.verify(ID, CHANNEL, null)
			.subscribe()
			.withSubscriber(UniAssertSubscriber.create())
			.assertFailedWith(AuthException.class);
	}

	/**
	 * Test method for
	 * {@link it.pagopa.swclient.mil.auth.service.ClientVerifier#verify(java.lang.String, java.lang.String, java.lang.String)}.
	 */
	@Test
	void testVerifyWithNonNullSecretExpectedButNullValueFound() {
		Client client = new Client(ID, CHANNEL, null, null, DESCRIPTION);

		when(repository.getClient(ID))
			.thenReturn(UniGenerator.item(client));

		verifier.verify(ID, CHANNEL, SECRET)
			.subscribe()
			.withSubscriber(UniAssertSubscriber.create())
			.assertFailedWith(AuthException.class);
	}

	/**
	 * Test method for
	 * {@link it.pagopa.swclient.mil.auth.service.ClientVerifier#verify(java.lang.String, java.lang.String, java.lang.String)}.
	 */
	@Test
	void testVerifyWithErrorVerifingSecret() {
		Client client = new Client(ID, CHANNEL, SALT, HASH, DESCRIPTION);

		when(repository.getClient(ID))
			.thenReturn(UniGenerator.item(client));

		try (MockedStatic<PasswordVerifier> digest = Mockito.mockStatic(PasswordVerifier.class)) {
			digest.when(() -> PasswordVerifier.verify(anyString(), anyString(), anyString()))
				.thenThrow(NoSuchAlgorithmException.class);
			verifier.verify(ID, CHANNEL, SECRET)
				.subscribe()
				.withSubscriber(UniAssertSubscriber.create())
				.assertFailedWith(AuthError.class);
		}
	}

	/**
	 * Test method for
	 * {@link it.pagopa.swclient.mil.auth.service.ClientVerifier#verify(java.lang.String, java.lang.String, java.lang.String)}.
	 */
	@Test
	void testVerifyOk() {
		Client client = new Client(ID, CHANNEL, SALT, HASH, DESCRIPTION);

		when(repository.getClient(ID))
			.thenReturn(UniGenerator.item(client));

		verifier.verify(ID, CHANNEL, SECRET)
			.subscribe()
			.withSubscriber(UniAssertSubscriber.create())
			.assertCompleted()
			.assertItem(client);
	}

	/**
	 * Test method for
	 * {@link it.pagopa.swclient.mil.auth.service.ClientVerifier#verify(java.lang.String, java.lang.String, java.lang.String)}.
	 */
	@Test
	void testVerifyWithoutSecret() {
		Client client = new Client(ID, CHANNEL, null, null, DESCRIPTION);

		when(repository.getClient(ID))
			.thenReturn(UniGenerator.item(client));

		verifier.verify(ID, CHANNEL, null)
			.subscribe()
			.withSubscriber(UniAssertSubscriber.create())
			.assertCompleted()
			.assertItem(client);
	}
}
