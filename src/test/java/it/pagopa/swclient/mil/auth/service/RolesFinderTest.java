/*
 * RolesFinderTest.java
 *
 * 7 ago 2023
 */
package it.pagopa.swclient.mil.auth.service;

import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.helpers.test.UniAssertSubscriber;
import it.pagopa.swclient.mil.auth.bean.Role;
import it.pagopa.swclient.mil.auth.util.AuthError;
import it.pagopa.swclient.mil.auth.util.AuthException;
import it.pagopa.swclient.mil.auth.util.UniGenerator;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

/**
 * @author Antonio Tarricone
 */
@QuarkusTest
class RolesFinderTest {
	/*
	 *
	 */
	private static final String ACQUIRER_ID = "4585625";
	private static final String CHANNEL = "POS";
	private static final String CLIENT_ID = "3965df56-ca9a-49e5-97e8-061433d4a25b";
	private static final String MERCHANT_ID = "28405fHfk73x88D";
	private static final String TERMINAL_ID = "12345678";
	private static final List<String> ROLES = List.of("NoticePayer", "SlavePos");

	/*
	 *
	 */
	@InjectMock
	AuthDataRepository repository;

	/*
	 *
	 */
	@Inject
	RolesFinder finder;

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
	 * Test method for
	 * {@link it.pagopa.swclient.mil.auth.service.RolesFinder#findRoles(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)}.
	 */
	@Test
	void testFindRolesOk() {
		Role role = new Role(ACQUIRER_ID, CHANNEL, CLIENT_ID, MERCHANT_ID, TERMINAL_ID, ROLES);

		when(repository.getRoles(ACQUIRER_ID, CHANNEL, CLIENT_ID, MERCHANT_ID, TERMINAL_ID))
			.thenReturn(UniGenerator.item(role));

		finder.findRoles(ACQUIRER_ID, CHANNEL, CLIENT_ID, MERCHANT_ID, TERMINAL_ID)
			.subscribe()
			.withSubscriber(UniAssertSubscriber.create())
			.assertCompleted()
			.assertItem(role);
	}

	/**
	 * Test method for
	 * {@link it.pagopa.swclient.mil.auth.service.RolesFinder#findRoles(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)}.
	 */
	@Test
	void testFindRolesNotFound() {
		when(repository.getRoles(ACQUIRER_ID, CHANNEL, CLIENT_ID, MERCHANT_ID, TERMINAL_ID))
			.thenReturn(Uni.createFrom().failure(new WebApplicationException(Response.status(Response.Status.NOT_FOUND).build())));

		when(repository.getRoles(ACQUIRER_ID, CHANNEL, CLIENT_ID, MERCHANT_ID, "NA"))
			.thenReturn(Uni.createFrom().failure(new WebApplicationException(Response.status(Response.Status.NOT_FOUND).build())));

		when(repository.getRoles(ACQUIRER_ID, CHANNEL, CLIENT_ID, "NA", "NA"))
			.thenReturn(Uni.createFrom().failure(new WebApplicationException(Response.status(Response.Status.NOT_FOUND).build())));

		finder.findRoles(ACQUIRER_ID, CHANNEL, CLIENT_ID, MERCHANT_ID, TERMINAL_ID)
			.subscribe()
			.withSubscriber(UniAssertSubscriber.create())
			.assertFailedWith(AuthException.class);
	}

	/**
	 * Test method for
	 * {@link it.pagopa.swclient.mil.auth.service.RolesFinder#findRoles(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)}.
	 */
	@Test
	void testFindRolesNotFoundWithUnknownTerminal() {
		when(repository.getRoles(ACQUIRER_ID, CHANNEL, CLIENT_ID, MERCHANT_ID, "NA"))
			.thenReturn(Uni.createFrom().failure(new WebApplicationException(Response.status(Response.Status.NOT_FOUND).build())));

		when(repository.getRoles(ACQUIRER_ID, CHANNEL, CLIENT_ID, "NA", "NA"))
			.thenReturn(Uni.createFrom().failure(new WebApplicationException(Response.status(Response.Status.NOT_FOUND).build())));

		finder.findRoles(ACQUIRER_ID, CHANNEL, CLIENT_ID, MERCHANT_ID, null)
			.subscribe()
			.withSubscriber(UniAssertSubscriber.create())
			.assertFailedWith(AuthException.class);
	}

	/**
	 * Test method for
	 * {@link it.pagopa.swclient.mil.auth.service.RolesFinder#findRoles(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)}.
	 */
	@Test
	void testFindRolesNotFoundWithUnknownMerchant() {
		when(repository.getRoles(ACQUIRER_ID, CHANNEL, CLIENT_ID, "NA", "NA"))
			.thenReturn(Uni.createFrom().failure(new WebApplicationException(Response.status(Response.Status.NOT_FOUND).build())));

		finder.findRoles(ACQUIRER_ID, CHANNEL, CLIENT_ID, null, null)
			.subscribe()
			.withSubscriber(UniAssertSubscriber.create())
			.assertFailedWith(AuthException.class);
	}

	/**
	 * Test method for
	 * {@link it.pagopa.swclient.mil.auth.service.RolesFinder#findRoles(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)}.
	 */
	@Test
	void testFindRolesNotFoundWithUnknownMerchantAndKnownTerminal() {
		when(repository.getRoles(ACQUIRER_ID, CHANNEL, CLIENT_ID, "NA", TERMINAL_ID))
			.thenReturn(Uni.createFrom().failure(new WebApplicationException(Response.status(Response.Status.NOT_FOUND).build())));

		when(repository.getRoles(ACQUIRER_ID, CHANNEL, CLIENT_ID, "NA", "NA"))
			.thenReturn(Uni.createFrom().failure(new WebApplicationException(Response.status(Response.Status.NOT_FOUND).build())));

		finder.findRoles(ACQUIRER_ID, CHANNEL, CLIENT_ID, null, TERMINAL_ID)
			.subscribe()
			.withSubscriber(UniAssertSubscriber.create())
			.assertFailedWith(AuthException.class);
	}

	/**
	 * Test method for
	 * {@link it.pagopa.swclient.mil.auth.service.RolesFinder#findRoles(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)}.
	 */
	@Test
	void testFindRolesWithoutSpecificRolesForTerminal() {
		Role role = new Role(ACQUIRER_ID, CHANNEL, CLIENT_ID, MERCHANT_ID, null, ROLES);

		when(repository.getRoles(ACQUIRER_ID, CHANNEL, CLIENT_ID, MERCHANT_ID, TERMINAL_ID))
			.thenReturn(Uni.createFrom().failure(new WebApplicationException(Response.status(Response.Status.NOT_FOUND).build())));

		when(repository.getRoles(ACQUIRER_ID, CHANNEL, CLIENT_ID, MERCHANT_ID, "NA"))
			.thenReturn(UniGenerator.item(role));

		finder.findRoles(ACQUIRER_ID, CHANNEL, CLIENT_ID, MERCHANT_ID, TERMINAL_ID)
			.subscribe()
			.withSubscriber(UniAssertSubscriber.create())
			.assertCompleted()
			.assertItem(role);
	}

	/**
	 * Test method for
	 * {@link it.pagopa.swclient.mil.auth.service.RolesFinder#findRoles(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)}.
	 */
	@Test
	void testFindRolesWithoutSpecificRolesForTerminalButUnkwnownTerminal() {
		Role role = new Role(ACQUIRER_ID, CHANNEL, CLIENT_ID, null, null, ROLES);

		when(repository.getRoles(ACQUIRER_ID, CHANNEL, CLIENT_ID, MERCHANT_ID, "NA"))
			.thenReturn(Uni.createFrom().failure(new WebApplicationException(Response.status(Response.Status.NOT_FOUND).build())));

		when(repository.getRoles(ACQUIRER_ID, CHANNEL, CLIENT_ID, "NA", "NA"))
			.thenReturn(UniGenerator.item(role));

		finder.findRoles(ACQUIRER_ID, CHANNEL, CLIENT_ID, MERCHANT_ID, null)
			.subscribe()
			.withSubscriber(UniAssertSubscriber.create())
			.assertCompleted()
			.assertItem(role);
	}

	/**
	 * Test method for
	 * {@link it.pagopa.swclient.mil.auth.service.RolesFinder#findRoles(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)}.
	 */
	@Test
	void testFindRolesWithoutSpecificRolesForMerchant() {
		Role role = new Role(ACQUIRER_ID, CHANNEL, CLIENT_ID, null, null, ROLES);

		when(repository.getRoles(ACQUIRER_ID, CHANNEL, CLIENT_ID, MERCHANT_ID, TERMINAL_ID))
			.thenReturn(Uni.createFrom().failure(new WebApplicationException(Response.status(Response.Status.NOT_FOUND).build())));

		when(repository.getRoles(ACQUIRER_ID, CHANNEL, CLIENT_ID, MERCHANT_ID, "NA"))
			.thenReturn(Uni.createFrom().failure(new WebApplicationException(Response.status(Response.Status.NOT_FOUND).build())));

		when(repository.getRoles(ACQUIRER_ID, CHANNEL, CLIENT_ID, "NA", "NA"))
			.thenReturn(UniGenerator.item(role));

		finder.findRoles(ACQUIRER_ID, CHANNEL, CLIENT_ID, MERCHANT_ID, TERMINAL_ID)
			.subscribe()
			.withSubscriber(UniAssertSubscriber.create())
			.assertCompleted()
			.assertItem(role);
	}

	/**
	 * Test method for
	 * {@link it.pagopa.swclient.mil.auth.service.RolesFinder#findRoles(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)}.
	 */
	@Test
	void testFindRolesWithError1() {
		when(repository.getRoles(ACQUIRER_ID, CHANNEL, CLIENT_ID, MERCHANT_ID, TERMINAL_ID))
			.thenReturn(Uni.createFrom().failure(new WebApplicationException(Response.status(Response.Status.INTERNAL_SERVER_ERROR).build())));

		finder.findRoles(ACQUIRER_ID, CHANNEL, CLIENT_ID, MERCHANT_ID, TERMINAL_ID)
			.subscribe()
			.withSubscriber(UniAssertSubscriber.create())
			.assertFailedWith(AuthError.class);
	}

	/**
	 * Test method for
	 * {@link it.pagopa.swclient.mil.auth.service.RolesFinder#findRoles(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)}.
	 */
	@Test
	void testFindRolesWithError2() {
		when(repository.getRoles(ACQUIRER_ID, CHANNEL, CLIENT_ID, MERCHANT_ID, TERMINAL_ID))
			.thenReturn(Uni.createFrom().failure(new Exception()));

		finder.findRoles(ACQUIRER_ID, CHANNEL, CLIENT_ID, MERCHANT_ID, TERMINAL_ID)
			.subscribe()
			.withSubscriber(UniAssertSubscriber.create())
			.assertFailedWith(AuthError.class);
	}
}