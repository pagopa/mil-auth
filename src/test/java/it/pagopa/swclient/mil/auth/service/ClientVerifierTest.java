/*
 * ClientVerifierTest.java
 *
 * 7 ago 2023
 */
package it.pagopa.swclient.mil.auth.service;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.security.NoSuchAlgorithmException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.helpers.test.UniAssertSubscriber;
import it.pagopa.swclient.mil.auth.bean.Client;
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
	AuthDataRepository repository;

	/*
	 *
	 */
	@Inject
	ClientVerifier verifier;

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

	/**
	 * 
	 */
	@Test
	void given_inexistentClientId_when_invokeFindClient_then_getFailure() {
		when(repository.getClient(anyString()))
			.thenReturn(Uni.createFrom()
				.failure(new WebApplicationException(Response.status(Response.Status.NOT_FOUND).build())));

		verifier.findClient(ID)
			.subscribe()
			.withSubscriber(UniAssertSubscriber.create())
			.assertFailedWith(AuthException.class);

	}

	/**
	 * 
	 */
	@Test
	void given_errorFindingClient_when_invokeFindClient_then_getFailure() {
		when(repository.getClient(anyString()))
			.thenReturn(Uni.createFrom()
				.failure(new WebApplicationException(Response.status(Response.Status.INTERNAL_SERVER_ERROR).build())));

		verifier.findClient(ID)
			.subscribe()
			.withSubscriber(UniAssertSubscriber.create())
			.assertFailedWith(AuthError.class);
	}

	/**
	 * 
	 */
	@Test
	void given_unhandledErrorFindingClient_when_invokeFindClient_then_getFailure() {
		when(repository.getClient(anyString()))
			.thenReturn(Uni.createFrom().failure(new Exception()));

		verifier.findClient(ID)
			.subscribe()
			.withSubscriber(UniAssertSubscriber.create())
			.assertFailedWith(AuthError.class);
	}

	/**
	 * 
	 */
	@Test
	void given_clientData_when_invokeFindClient_then_getIt() {
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
	 * 
	 */
	@Test
	void given_clientDataWithBadChannel_when_invokeVerify_then_getFailure() {
		Client client = new Client(ID, CHANNEL, SALT, HASH, DESCRIPTION);

		when(repository.getClient(ID))
			.thenReturn(UniGenerator.item(client));

		verifier.verify(ID, WRONG_CHANNEL, SECRET)
			.subscribe()
			.withSubscriber(UniAssertSubscriber.create())
			.assertFailedWith(AuthException.class);
	}

	/**
	 * 
	 */
	@Test
	void given_clientDataWithBadSecret_when_invokeVerify_then_getFailure() {
		Client client = new Client(ID, CHANNEL, SALT, HASH, DESCRIPTION);

		when(repository.getClient(ID))
			.thenReturn(UniGenerator.item(client));

		verifier.verify(ID, CHANNEL, WRONG_SECRET)
			.subscribe()
			.withSubscriber(UniAssertSubscriber.create())
			.assertFailedWith(AuthException.class);
	}

	/**
	 * 
	 */
	@Test
	void give_clientDataWithUnexpectedSecret_when_invokeVerify_then_getFailure() {
		Client client = new Client(ID, CHANNEL, SALT, HASH, DESCRIPTION);

		when(repository.getClient(ID))
			.thenReturn(UniGenerator.item(client));

		verifier.verify(ID, CHANNEL, null)
			.subscribe()
			.withSubscriber(UniAssertSubscriber.create())
			.assertFailedWith(AuthException.class);
	}

	/**
	 * 
	 */
	@Test
	void given_clientDataWithUnexpectedNullSecret_when_invokeVerify_then_getFailure() {
		Client client = new Client(ID, CHANNEL, null, null, DESCRIPTION);

		when(repository.getClient(ID))
			.thenReturn(UniGenerator.item(client));

		verifier.verify(ID, CHANNEL, SECRET)
			.subscribe()
			.withSubscriber(UniAssertSubscriber.create())
			.assertFailedWith(AuthException.class);
	}

	/**
	 * 
	 */
	@Test
	void given_errorVerifingSecret_when_invokeVerify_then_getFailure() {
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
	 * 
	 */
	@Test
	void given_okScenario_when_invokeVerify_then_getOk() {
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
	 * 
	 */
	@Test
	void given_okScenarioWithoutSecret_when_invokeVerify_then_getOk() {
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