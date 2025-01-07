/*
 * 
 */
package it.pagopa.swclient.mil.auth.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.text.ParseException;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.helpers.test.UniAssertSubscriber;
import it.pagopa.swclient.mil.auth.bean.GetAccessTokenRequest;
import it.pagopa.swclient.mil.auth.bean.GrantType;
import it.pagopa.swclient.mil.auth.dao.ClientEntity;
import it.pagopa.swclient.mil.auth.dao.SetOfRolesEntity;
import it.pagopa.swclient.mil.auth.dao.UserEntity;
import it.pagopa.swclient.mil.auth.dao.UserRepository;
import it.pagopa.swclient.mil.auth.qualifier.Password;
import it.pagopa.swclient.mil.auth.util.AuthError;
import it.pagopa.swclient.mil.auth.util.AuthException;
import it.pagopa.swclient.mil.auth.util.UniGenerator;
import it.pagopa.swclient.mil.bean.Channel;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

/**
 *
 * @author Antonio Tarricone
 */
@QuarkusTest
class TokenByPasswordServiceTest {
	/*
	 * 
	 */
	@Inject
	@Password
	TokenByPasswordService tokenByPasswordService;

	/*
	 *
	 */
	@InjectMock
	UserRepository repository;

	/*
	 * 
	 */
	@InjectMock
	ClientVerifier clientVerifier;

	/*
	 * 
	 */
	@InjectMock
	RolesFinder roleFinder;

	/*
	 * 
	 */
	@InjectMock
	TokenSigner tokenSigner;

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
	 * @throws ParseException
	 */
	@Test
	void given_userCredentials_when_allGoesOk_then_getAccessToken() throws ParseException {
		/*
		 * Setup
		 */
		final String username = "username";
		final String password = "3ebcd984-48b1-4df2-99d8-f5d550dbad02";
		final String salt = "TSO2VIJixd6taCapX1Aq9bTIbTAEuDtXzLleB9A3W6NUgppiJkNbAnBX8CVYvpsPMpzJHGhK2ouHDONevrcVUg==";
		final String passwordHash = "gKWXj0IXDkeO5xvrozbm47tO+SXHNGN8pE5ql3W4Hgo=";

		when(repository.findByUsernameAndClientId(username, "client_id"))
			.thenReturn(UniGenerator.item(Optional.of(new UserEntity()
				.setAcquirerId("acquirer_id")
				.setChannel(Channel.POS)
				.setMerchantId("merchant_id")
				.setPasswordHash(passwordHash)
				.setSalt(salt)
				.setUsername(username))));

		when(clientVerifier.verify("client_id", Channel.POS, null))
			.thenReturn(UniGenerator.item(new ClientEntity()));

		when(roleFinder.findRoles("acquirer_id", Channel.POS, "client_id", "merchant_id", "terminal_id"))
			.thenReturn(UniGenerator.item(new SetOfRolesEntity()
				.setRoles(List.of("role"))));

		SignedJWT signedJwt = SignedJWT.parse("eyJraWQiOiJrZXlfbmFtZS9rZXlfdmVyc2lvbiIsImFsZyI6IlJTMjU2In0.eyJzdWIiOiJjbGllbnRfaWQiLCJjbGllbnRJZCI6ImNsaWVudF9pZCIsImNoYW5uZWwiOiJjaGFubmVsIiwiaXNzIjoiaHR0cHM6Ly9taWwtYXV0aCIsImdyb3VwcyI6InJvbGUiLCJ0ZXJtaW5hbElkIjoidGVybWluYWxfaWQiLCJhdWQiOiJodHRwczovL21pbCIsIm1lcmNoYW50SWQiOiJtZXJjaGFudF9pZCIsInNjb3BlIjoic2NvcGUiLCJmaXNjYWxDb2RlIjoiZW5jX2Zpc2NhbF9jb2RlIiwiZXhwIjoxNzE3NjUyLCJhY3F1aXJlcklkIjoiYWNxdWlyZXJfaWQiLCJpYXQiOjE3MTc1OTJ9.AA");

		when(tokenSigner.sign(any(JWTClaimsSet.class)))
			.thenReturn(UniGenerator.item(signedJwt));

		/*
		 * Test
		 */
		GetAccessTokenRequest request = new GetAccessTokenRequest()
			.setAcquirerId("acquirer_id")
			.setChannel(Channel.POS)
			.setClientId("client_id")
			.setGrantType(GrantType.PASSWORD)
			.setMerchantId("merchant_id")
			.setTerminalId("terminal_id")
			.setUsername(username)
			.setPassword(password);

		tokenByPasswordService.process(request)
			.subscribe()
			.with(
				response -> {
					assertEquals(
						signedJwt,
						response.getAccessToken());
				},
				f -> fail(f));
	}

	/**
	 * 
	 */
	@Test
	void given_userCredentials_when_userNotFound_then_getFailure() {
		/*
		 * Setup
		 */
		final String username = "username";
		final String password = "password";

		when(repository.findByUsernameAndClientId(username, "client_id"))
			.thenReturn(Uni.createFrom()
				.item(Optional.empty()));

		/*
		 * Test
		 */
		GetAccessTokenRequest request = new GetAccessTokenRequest()
			.setAcquirerId("acquirer_id")
			.setChannel("channel")
			.setClientId("client_id")
			.setGrantType(GrantType.PASSWORD)
			.setMerchantId("merchant_id")
			.setTerminalId("terminal_id")
			.setUsername(username)
			.setPassword(password);

		tokenByPasswordService.process(request)
			.subscribe()
			.withSubscriber(UniAssertSubscriber.create())
			.assertFailedWith(AuthException.class);
	}

	/**
	 * 
	 */
	@Test
	void given_userCredentials_when_getUserReturns500_then_getFailure() {
		/*
		 * Setup
		 */
		final String username = "username";
		final String password = "password";

		when(repository.findByUsernameAndClientId(username, "client_id"))
			.thenReturn(Uni.createFrom().failure(new WebApplicationException(Response.status(Response.Status.INTERNAL_SERVER_ERROR).build())));

		/*
		 * Test
		 */
		GetAccessTokenRequest request = new GetAccessTokenRequest()
			.setAcquirerId("acquirer_id")
			.setChannel("channel")
			.setClientId("client_id")
			.setGrantType(GrantType.PASSWORD)
			.setMerchantId("merchant_id")
			.setTerminalId("terminal_id")
			.setUsername(username)
			.setPassword(password);

		tokenByPasswordService.process(request)
			.subscribe()
			.withSubscriber(UniAssertSubscriber.create())
			.assertFailedWith(AuthError.class);
	}

	/**
	 * 
	 */
	@Test
	void given_userCredentials_when_getUserThrowsAnotherException_then_getFailure() {
		/*
		 * Setup
		 */
		final String username = "username";
		final String password = "password";

		when(repository.findByUsernameAndClientId(username, "client_id"))
			.thenReturn(Uni.createFrom().failure(new Exception("synthetic_exception")));

		/*
		 * Test
		 */
		GetAccessTokenRequest request = new GetAccessTokenRequest()
			.setAcquirerId("acquirer_id")
			.setChannel("channel")
			.setClientId("client_id")
			.setGrantType(GrantType.PASSWORD)
			.setMerchantId("merchant_id")
			.setTerminalId("terminal_id")
			.setUsername(username)
			.setPassword(password);

		tokenByPasswordService.process(request)
			.subscribe()
			.withSubscriber(UniAssertSubscriber.create())
			.assertFailedWith(AuthError.class);
	}

	/**
	 * 
	 */
	@Test
	void given_userCredentials_when_consistencyVerificationFailsDueToAcquirerId_then_getFailure() {
		/*
		 * Setup
		 */
		final String username = "username";
		final String password = "3ebcd984-48b1-4df2-99d8-f5d550dbad02";
		final String salt = "TSO2VIJixd6taCapX1Aq9bTIbTAEuDtXzLleB9A3W6NUgppiJkNbAnBX8CVYvpsPMpzJHGhK2ouHDONevrcVUg==";
		final String passwordHash = "gKWXj0IXDkeO5xvrozbm47tO+SXHNGN8pE5ql3W4Hgo=";

		when(repository.findByUsernameAndClientId(username, "client_id"))
			.thenReturn(UniGenerator.item(Optional.of(new UserEntity()
				.setAcquirerId("acquirer_id_2")
				.setChannel("channel")
				.setMerchantId("merchant_id")
				.setPasswordHash(passwordHash)
				.setSalt(salt)
				.setUsername(username))));

		when(clientVerifier.verify("client_id", "channel", null))
			.thenReturn(UniGenerator.item(new ClientEntity()));

		when(roleFinder.findRoles("acquirer_id", "channel", "client_id", "merchant_id", "terminal_id"))
			.thenReturn(UniGenerator.item(new SetOfRolesEntity()
				.setRoles(List.of("role"))));

		/*
		 * Test
		 */
		GetAccessTokenRequest request = new GetAccessTokenRequest()
			.setAcquirerId("acquirer_id")
			.setChannel("channel")
			.setClientId("client_id")
			.setGrantType(GrantType.PASSWORD)
			.setMerchantId("merchant_id")
			.setTerminalId("terminal_id")
			.setUsername(username)
			.setPassword(password);

		tokenByPasswordService.process(request)
			.subscribe()
			.withSubscriber(UniAssertSubscriber.create())
			.assertFailedWith(AuthException.class);
	}

	/**
	 * 
	 */
	@Test
	void given_userCredentials_when_consistencyVerificationFailsDueToChannel_then_getFailure() {
		/*
		 * Setup
		 */
		final String username = "username";
		final String password = "3ebcd984-48b1-4df2-99d8-f5d550dbad02";
		final String salt = "TSO2VIJixd6taCapX1Aq9bTIbTAEuDtXzLleB9A3W6NUgppiJkNbAnBX8CVYvpsPMpzJHGhK2ouHDONevrcVUg==";
		final String passwordHash = "gKWXj0IXDkeO5xvrozbm47tO+SXHNGN8pE5ql3W4Hgo=";

		when(repository.findByUsernameAndClientId(username, "client_id"))
			.thenReturn(UniGenerator.item(Optional.of(new UserEntity()
				.setAcquirerId("acquirer_id")
				.setChannel("channel_2")
				.setMerchantId("merchant_id")
				.setPasswordHash(passwordHash)
				.setSalt(salt)
				.setUsername(username))));

		when(clientVerifier.verify("client_id", "channel", null))
			.thenReturn(UniGenerator.item(new ClientEntity()));

		when(roleFinder.findRoles("acquirer_id", "channel", "client_id", "merchant_id", "terminal_id"))
			.thenReturn(UniGenerator.item(new SetOfRolesEntity()
				.setRoles(List.of("role"))));

		/*
		 * Test
		 */
		GetAccessTokenRequest request = new GetAccessTokenRequest()
			.setAcquirerId("acquirer_id")
			.setChannel("channel")
			.setClientId("client_id")
			.setGrantType(GrantType.PASSWORD)
			.setMerchantId("merchant_id")
			.setTerminalId("terminal_id")
			.setUsername(username)
			.setPassword(password);

		tokenByPasswordService.process(request)
			.subscribe()
			.withSubscriber(UniAssertSubscriber.create())
			.assertFailedWith(AuthException.class);
	}

	/**
	 * 
	 */
	@Test
	void given_userCredentials_when_consistencyVerificationFailsDueToMerchantId_then_getFailure() {
		/*
		 * Setup
		 */
		final String username = "username";
		final String password = "3ebcd984-48b1-4df2-99d8-f5d550dbad02";
		final String salt = "TSO2VIJixd6taCapX1Aq9bTIbTAEuDtXzLleB9A3W6NUgppiJkNbAnBX8CVYvpsPMpzJHGhK2ouHDONevrcVUg==";
		final String passwordHash = "gKWXj0IXDkeO5xvrozbm47tO+SXHNGN8pE5ql3W4Hgo=";

		when(repository.findByUsernameAndClientId(username, "client_id"))
			.thenReturn(UniGenerator.item(Optional.of(new UserEntity()
				.setAcquirerId("acquirer_id")
				.setChannel("channel")
				.setMerchantId("merchant_id_2")
				.setPasswordHash(passwordHash)
				.setSalt(salt)
				.setUsername(username))));

		when(clientVerifier.verify("client_id", "channel", null))
			.thenReturn(UniGenerator.item(new ClientEntity()));

		when(roleFinder.findRoles("acquirer_id", "channel", "client_id", "merchant_id", "terminal_id"))
			.thenReturn(UniGenerator.item(new SetOfRolesEntity()
				.setRoles(List.of("role"))));

		/*
		 * Test
		 */
		GetAccessTokenRequest request = new GetAccessTokenRequest()
			.setAcquirerId("acquirer_id")
			.setChannel("channel")
			.setClientId("client_id")
			.setGrantType(GrantType.PASSWORD)
			.setMerchantId("merchant_id")
			.setTerminalId("terminal_id")
			.setUsername(username)
			.setPassword(password);

		tokenByPasswordService.process(request)
			.subscribe()
			.withSubscriber(UniAssertSubscriber.create())
			.assertFailedWith(AuthException.class);
	}

	/**
	 * 
	 */
	@Test
	void given_userCredentials_when_passwordIsWrong_then_getFailure() {
		/*
		 * Setup
		 */
		final String username = "username";
		final String salt = "TSO2VIJixd6taCapX1Aq9bTIbTAEuDtXzLleB9A3W6NUgppiJkNbAnBX8CVYvpsPMpzJHGhK2ouHDONevrcVUg==";
		final String passwordHash = "gKWXj0IXDkeO5xvrozbm47tO+SXHNGN8pE5ql3W4Hgo=";

		when(repository.findByUsernameAndClientId(username, "client_id"))
			.thenReturn(UniGenerator.item(Optional.of(new UserEntity()
				.setAcquirerId("acquirer_id")
				.setChannel("channel")
				.setMerchantId("merchant_id")
				.setPasswordHash(passwordHash)
				.setSalt(salt)
				.setUsername(username))));

		when(clientVerifier.verify("client_id", "channel", null))
			.thenReturn(UniGenerator.item(new ClientEntity()));

		when(roleFinder.findRoles("acquirer_id", "channel", "client_id", "merchant_id", "terminal_id"))
			.thenReturn(UniGenerator.item(new SetOfRolesEntity()
				.setRoles(List.of("role"))));

		/*
		 * Test
		 */
		GetAccessTokenRequest request = new GetAccessTokenRequest()
			.setAcquirerId("acquirer_id")
			.setChannel("channel")
			.setClientId("client_id")
			.setGrantType(GrantType.PASSWORD)
			.setMerchantId("merchant_id")
			.setTerminalId("terminal_id")
			.setUsername(username)
			.setPassword("password_2");

		tokenByPasswordService.process(request)
			.subscribe()
			.withSubscriber(UniAssertSubscriber.create())
			.assertFailedWith(AuthException.class);
	}
}