/*
 * AuthDataRepositoryTest.java
 *
 * 6 giu 2024
 */
package it.pagopa.swclient.mil.auth.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.helpers.test.UniAssertSubscriber;
import it.pagopa.swclient.mil.auth.bean.Client;
import it.pagopa.swclient.mil.auth.bean.Role;
import it.pagopa.swclient.mil.auth.bean.User;
import it.pagopa.swclient.mil.auth.util.AuthError;
import it.pagopa.swclient.mil.auth.util.UniGenerator;
import it.pagopa.swclient.mil.azureservices.storageblob.service.AzureStorageBlobReactiveService;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

/**
 * 
 * @author Antonio Tarricone
 */
@QuarkusTest
class AuthDataRepositoryTest {
	/*
	 * 
	 */
	@Inject
	AuthDataRepository repository;

	/*
	 * 
	 */
	@InjectMock
	AzureStorageBlobReactiveService blobService;

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
	void given_clientData_when_invokeGetClient_then_getIt() {
		/*
		 * Setup
		 */
		when(blobService.getBlob("clients", "client_id.json"))
			.thenReturn(UniGenerator.item(Response.ok("""
				{
				  "id": "id",
				  "channel": "channel",
				  "salt": "salt",
				  "secretHash": "secretHash",
				  "description": "description"
				}
				""")
				.build()));

		/*
		 * Test
		 */
		repository.getClient("client_id")
			.subscribe()
			.with(
				actual -> {
					assertThat(actual)
						.usingRecursiveComparison()
						.isEqualTo(new Client("id", "channel", "salt", "secretHash", "description"));
				},
				f -> fail(f));
	}

	/**
	 * 
	 */
	@Test
	void given_inexistentClientId_when_invokeGetClient_then_getFailure() {
		/*
		 * Setup
		 */
		when(blobService.getBlob("clients", "client_id.json"))
			.thenReturn(UniGenerator.item(Response.status(Status.NOT_FOUND)
				.build()));

		/*
		 * Test
		 */
		repository.getClient("client_id")
			.subscribe()
			.withSubscriber(UniAssertSubscriber.create())
			.assertFailedWith(AuthError.class);
	}

	/**
	 * 
	 */
	@Test
	void given_badClientData_when_invokeGetClient_then_getFailure() {
		/*
		 * Setup
		 */
		when(blobService.getBlob("clients", "client_id.json"))
			.thenReturn(UniGenerator.item(Response.ok("""
				{
				  "imnotaclient": "imnotaclient"
				}
				""")
				.build()));

		/*
		 * Test
		 */
		repository.getClient("client_id")
			.subscribe()
			.withSubscriber(UniAssertSubscriber.create())
			.assertFailedWith(AuthError.class);
	}

	/**
	 * 
	 */
	@Test
	void given_errorFromBlobService_when_invokeGetClient_then_getFailure() {
		/*
		 * Setup
		 */
		when(blobService.getBlob("clients", "client_id.json"))
			.thenReturn(Uni.createFrom().failure(new Exception("synthetic_exception")));

		/*
		 * Test
		 */
		repository.getClient("client_id")
			.subscribe()
			.withSubscriber(UniAssertSubscriber.create())
			.assertFailedWith(Exception.class);
	}

	/**
	 * 
	 */
	@Test
	void given_rolesData_when_invokeGetRoles_then_getThem() {
		/*
		 * Setup
		 */
		when(blobService.getBlob("roles", "acquirerId", "channel", "clientId", "merchantId", "terminalId", "roles.json"))
			.thenReturn(UniGenerator.item(Response.ok("""
				{
				  "acquirerId": "acquirerId",
				  "channel": "channel",
				  "clientId": "clientId",
				  "merchantId": "mechantId",
				  "terminalId": "terminalId",
				  "roles": [
				    "role#1",
				    "role#2"
				  ]
				}
				""")
				.build()));

		/*
		 * Test
		 */
		repository.getRoles("acquirerId", "channel", "clientId", "merchantId", "terminalId")
			.subscribe()
			.with(
				actual -> {
					assertThat(actual)
						.usingRecursiveComparison()
						.isEqualTo(new Role("acquirerId", "channel", "clientId", "merchantId", "terminalId", List.of("role#1", "role#2")));
				},
				f -> fail(f));
	}

	/**
	 * 
	 */
	@Test
	void given_inexistentRolesKey_when_invokeGetRoles_then_getFailure() {
		/*
		 * Setup
		 */
		when(blobService.getBlob("roles", "acquirerId", "channel", "clientId", "merchantId", "terminalId", "roles.json"))
			.thenReturn(UniGenerator.item(Response.status(Status.NOT_FOUND)
				.build()));

		/*
		 * Test
		 */
		repository.getRoles("acquirerId", "channel", "clientId", "merchantId", "terminalId")
			.subscribe()
			.withSubscriber(UniAssertSubscriber.create())
			.assertFailedWith(AuthError.class);
	}

	/**
	 * 
	 */
	@Test
	void given_badRolesData_when_invokeGetRoles_then_getFailure() {
		/*
		 * Setup
		 */
		when(blobService.getBlob("roles", "acquirerId", "channel", "clientId", "merchantId", "terminalId", "roles.json"))
			.thenReturn(UniGenerator.item(Response.ok("""
				{
				  "imnotroles": "imnotroles"
				}
				""")
				.build()));

		/*
		 * Test
		 */
		repository.getRoles("acquirerId", "channel", "clientId", "merchantId", "terminalId")
			.subscribe()
			.withSubscriber(UniAssertSubscriber.create())
			.assertFailedWith(AuthError.class);
	}

	/**
	 * 
	 */
	@Test
	void given_errorFromBlobService_when_invokeGetRoles_then_getFailure() {
		/*
		 * Setup
		 */
		when(blobService.getBlob("roles", "acquirerId", "channel", "clientId", "merchantId", "terminalId", "roles.json"))
			.thenReturn(Uni.createFrom().failure(new Exception("synthetic_exception")));

		/*
		 * Test
		 */
		repository.getRoles("acquirerId", "channel", "clientId", "merchantId", "terminalId")
			.subscribe()
			.withSubscriber(UniAssertSubscriber.create())
			.assertFailedWith(Exception.class);
	}

	/**
	 * 
	 */
	@Test
	void given_userData_when_invokeGetUser_then_getIt() {
		/*
		 * Setup
		 */
		when(blobService.getBlob("users", "user_hash.json"))
			.thenReturn(UniGenerator.item(Response.ok("""
				{
				  "username": "username",
				  "salt": "salt",
				  "passwordHash": "passwordHash",
				  "acquirerId": "acquirerId",
				  "channel": "channel",
				  "merchantId": "merchantId"
				}
				""")
				.build()));

		/*
		 * Test
		 */
		repository.getUser("user_hash")
			.subscribe()
			.with(
				actual -> {
					assertThat(actual)
						.usingRecursiveComparison()
						.isEqualTo(new User("username", "salt", "passwordHash", "acquirerId", "channel", "merchantId"));
				},
				f -> fail(f));
	}

	/**
	 * 
	 */
	@Test
	void given_inexistentUserHash_when_invokeGetUser_then_getFailure() {
		/*
		 * Setup
		 */
		when(blobService.getBlob("users", "user_hash.json"))
			.thenReturn(UniGenerator.item(Response.status(Status.NOT_FOUND)
				.build()));

		/*
		 * Test
		 */
		repository.getUser("user_hash")
			.subscribe()
			.withSubscriber(UniAssertSubscriber.create())
			.assertFailedWith(AuthError.class);
	}

	/**
	 * 
	 */
	@Test
	void given_badUserData_when_invokeGetUser_then_getFailure() {
		/*
		 * Setup
		 */
		when(blobService.getBlob("users", "user_hash.json"))
			.thenReturn(UniGenerator.item(Response.ok("""
				{
				  "imnotauser": "imnotauser"
				}
				""")
				.build()));

		/*
		 * Test
		 */
		repository.getUser("user_hash")
			.subscribe()
			.withSubscriber(UniAssertSubscriber.create())
			.assertFailedWith(AuthError.class);
	}

	/**
	 * 
	 */
	@Test
	void given_errorFromBlobService_when_invokeGetUser_then_getFailure() {
		/*
		 * Setup
		 */
		when(blobService.getBlob("users", "user_hash.json"))
			.thenReturn(Uni.createFrom().failure(new Exception("synthetic_exception")));

		/*
		 * Test
		 */
		repository.getUser("user_hash")
			.subscribe()
			.withSubscriber(UniAssertSubscriber.create())
			.assertFailedWith(Exception.class);
	}
}
