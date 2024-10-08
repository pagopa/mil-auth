/*
 * ClientVerifierTest.java
 *
 * 7 ago 2023
 */
package it.pagopa.swclient.mil.auth.service;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.helpers.test.UniAssertSubscriber;
import it.pagopa.swclient.mil.auth.dao.ClientEntity;
import it.pagopa.swclient.mil.auth.dao.ClientRepository;
import it.pagopa.swclient.mil.auth.util.AuthError;
import it.pagopa.swclient.mil.auth.util.AuthException;
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
	private static final String HASH = "EOPjxZXy7YbxLubGSs7EhavNqbjVF0ywYQPFE0WYbSw=";
	private static final String SECRET = "5ceef788-4115-43a7-a704-b1bcc9a47c86";
	private static final String DESCRIPTION = "VAS Layer";
	private static final String WRONG_CHANNEL = "ATM";
	private static final String WRONG_SECRET = "3674f0e7-d717-44cc-a3bc-5f8f41771fea";

	/*
	 *
	 */
	@InjectMock
	ClientRepository repository;

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
		when(repository.findByClientId(anyString()))
			.thenReturn(Uni.createFrom()
				.item(Optional.empty()));

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
		when(repository.findByClientId(anyString()))
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
		when(repository.findByClientId(anyString()))
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
	void given_handledErrorFindingClient_when_invokeFindClient_then_getFailure() {
		when(repository.findByClientId(anyString()))
			.thenReturn(UniGenerator.error("code", "string"));

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
		ClientEntity client = new ClientEntity(ID, CHANNEL, SALT, HASH, DESCRIPTION, null);

		when(repository.findByClientId(ID))
			.thenReturn(UniGenerator.item(Optional.of(client)));

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
		ClientEntity client = new ClientEntity(ID, CHANNEL, SALT, HASH, DESCRIPTION, null);

		when(repository.findByClientId(ID))
			.thenReturn(UniGenerator.item(Optional.of(client)));

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
		ClientEntity client = new ClientEntity(ID, CHANNEL, SALT, HASH, DESCRIPTION, null);

		when(repository.findByClientId(ID))
			.thenReturn(UniGenerator.item(Optional.of(client)));

		verifier.verify(ID, CHANNEL, WRONG_SECRET)
			.subscribe()
			.withSubscriber(UniAssertSubscriber.create())
			.assertFailedWith(AuthException.class);
	}

	/**
	 * 
	 */
	@Test
	void given_clientDataWithUnexpectedSecret_when_invokeVerify_then_getFailure() {
		ClientEntity client = new ClientEntity(ID, CHANNEL, SALT, HASH, DESCRIPTION, null);

		when(repository.findByClientId(ID))
			.thenReturn(UniGenerator.item(Optional.of(client)));

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
		ClientEntity client = new ClientEntity(ID, CHANNEL, null, null, DESCRIPTION, null);

		when(repository.findByClientId(ID))
			.thenReturn(UniGenerator.item(Optional.of(client)));

		verifier.verify(ID, CHANNEL, SECRET)
			.subscribe()
			.withSubscriber(UniAssertSubscriber.create())
			.assertFailedWith(AuthException.class);
	}

	/**
	 * 
	 */
	@Test
	void given_okScenario_when_invokeVerify_then_getOk() {
		ClientEntity client = new ClientEntity(ID, CHANNEL, SALT, HASH, DESCRIPTION, null);

		when(repository.findByClientId(ID))
			.thenReturn(UniGenerator.item(Optional.of(client)));

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
		ClientEntity client = new ClientEntity(ID, CHANNEL, null, null, DESCRIPTION, null);

		when(repository.findByClientId(ID))
			.thenReturn(UniGenerator.item(Optional.of(client)));

		verifier.verify(ID, CHANNEL, null)
			.subscribe()
			.withSubscriber(UniAssertSubscriber.create())
			.assertCompleted()
			.assertItem(client);
	}
}