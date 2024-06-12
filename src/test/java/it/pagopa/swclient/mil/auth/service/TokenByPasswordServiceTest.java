/**
 * 
 */
package it.pagopa.swclient.mil.auth.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.Base64;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.helpers.test.UniAssertSubscriber;
import it.pagopa.swclient.mil.auth.bean.Client;
import it.pagopa.swclient.mil.auth.bean.GetAccessTokenRequest;
import it.pagopa.swclient.mil.auth.bean.GrantType;
import it.pagopa.swclient.mil.auth.bean.Role;
import it.pagopa.swclient.mil.auth.bean.User;
import it.pagopa.swclient.mil.auth.qualifier.Password;
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
@TestInstance(Lifecycle.PER_CLASS)
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
	AuthDataRepository repository;

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
	 * @throws NoSuchAlgorithmException
	 */
	@Test
	void given_userCredentials_when_allGoesOk_then_getAccessToken() throws ParseException, NoSuchAlgorithmException {
		/*
		 * Setup
		 */
		final String username = "username";
		final String password = "password";
		final String salt = "zfN59oSr9RfFiiSASUO1YIcv8bARsj1OAV8tEydQiKC3su5Mlz1TsjbFwvWrGCjXdkDUsbeXGnYZDavJuTKw6Q==";

		String userHash = Base64.getUrlEncoder()
			.encodeToString(MessageDigest.getInstance("SHA256")
				.digest(username.getBytes(StandardCharsets.UTF_8)));

		byte[] passwordBytes = password.getBytes(StandardCharsets.UTF_8);
		byte[] saltBytes = Base64.getDecoder().decode(salt);

		byte[] data = new byte[passwordBytes.length + saltBytes.length];
		System.arraycopy(passwordBytes, 0, data, 0, passwordBytes.length);
		System.arraycopy(saltBytes, 0, data, passwordBytes.length, saltBytes.length);

		byte[] passwordHashBytes = MessageDigest.getInstance("SHA256").digest(data);

		String passwordHash = Base64.getEncoder().encodeToString(passwordHashBytes);

		when(repository.getUser(userHash))
			.thenReturn(UniGenerator.item(new User()
				.setAcquirerId("acquirer_id")
				.setChannel("channel")
				.setMerchantId("merchant_id")
				.setPasswordHash(passwordHash)
				.setSalt(salt)
				.setUsername(username)));

		when(clientVerifier.verify("client_id", "channel", null))
			.thenReturn(UniGenerator.item(new Client()));

		when(roleFinder.findRoles("acquirer_id", "channel", "client_id", "merchant_id", "terminal_id"))
			.thenReturn(UniGenerator.item(new Role()
				.setRoles(List.of("role"))));

		SignedJWT signedJwt = SignedJWT.parse("eyJraWQiOiJrZXlfbmFtZS9rZXlfdmVyc2lvbiIsImFsZyI6IlJTMjU2In0.eyJzdWIiOiJjbGllbnRfaWQiLCJjbGllbnRJZCI6ImNsaWVudF9pZCIsImNoYW5uZWwiOiJjaGFubmVsIiwiaXNzIjoiaHR0cHM6Ly9taWwtYXV0aCIsImdyb3VwcyI6InJvbGUiLCJ0ZXJtaW5hbElkIjoidGVybWluYWxfaWQiLCJhdWQiOiJodHRwczovL21pbCIsIm1lcmNoYW50SWQiOiJtZXJjaGFudF9pZCIsInNjb3BlIjoic2NvcGUiLCJmaXNjYWxDb2RlIjoiZW5jX2Zpc2NhbF9jb2RlIiwiZXhwIjoxNzE3NjUyLCJhY3F1aXJlcklkIjoiYWNxdWlyZXJfaWQiLCJpYXQiOjE3MTc1OTJ9.AA");

		when(tokenSigner.sign(any(JWTClaimsSet.class)))
			.thenReturn(UniGenerator.item(signedJwt));

		/*
		 * Test
		 */
		GetAccessTokenRequest request = new GetAccessTokenRequest()
			.setAcquirerId("acquirer_id")
			.setChannel("channel")
			.setClientId("client_id")
			.setGrantType(GrantType.PASSWORD)
			.setMerchantId("merchant_id")
			.setRequestId("request_id")
			.setTerminalId("terminal_id")
			.setUsername(username)
			.setPassword(password);

		tokenByPasswordService.process(request)
			.subscribe()
			.with(
				response -> {
					assertEquals(
						"eyJraWQiOiJrZXlfbmFtZS9rZXlfdmVyc2lvbiIsImFsZyI6IlJTMjU2In0.eyJzdWIiOiJjbGllbnRfaWQiLCJjbGllbnRJZCI6ImNsaWVudF9pZCIsImNoYW5uZWwiOiJjaGFubmVsIiwiaXNzIjoiaHR0cHM6Ly9taWwtYXV0aCIsImdyb3VwcyI6InJvbGUiLCJ0ZXJtaW5hbElkIjoidGVybWluYWxfaWQiLCJhdWQiOiJodHRwczovL21pbCIsIm1lcmNoYW50SWQiOiJtZXJjaGFudF9pZCIsInNjb3BlIjoic2NvcGUiLCJmaXNjYWxDb2RlIjoiZW5jX2Zpc2NhbF9jb2RlIiwiZXhwIjoxNzE3NjUyLCJhY3F1aXJlcklkIjoiYWNxdWlyZXJfaWQiLCJpYXQiOjE3MTc1OTJ9.AA",
						response.getAccessToken());
				},
				f -> fail(f));
	}

	/**
	 * 
	 */
	@Test
	void given_userCredentials_when_messageDigestThrowsException_then_getFailure() {
		try (MockedStatic<MessageDigest> digest = Mockito.mockStatic(MessageDigest.class)) {
			digest.when(() -> MessageDigest.getInstance("SHA256"))
				.thenThrow(NoSuchAlgorithmException.class);

			GetAccessTokenRequest request = new GetAccessTokenRequest()
				.setAcquirerId("acquirer_id")
				.setChannel("channel")
				.setClientId("client_id")
				.setGrantType(GrantType.PASSWORD)
				.setMerchantId("merchant_id")
				.setRequestId("request_id")
				.setTerminalId("terminal_id")
				.setUsername("username")
				.setPassword("password");

			tokenByPasswordService.process(request)
				.subscribe()
				.withSubscriber(UniAssertSubscriber.create())
				.assertFailedWith(AuthError.class);
		}
	}

	/**
	 * 
	 * @throws NoSuchAlgorithmException
	 */
	@Test
	void given_userCredentials_when_userNotFound_then_getFailure() throws NoSuchAlgorithmException {
		/*
		 * Setup
		 */
		final String username = "username";
		final String password = "password";

		String userHash = Base64.getUrlEncoder()
			.encodeToString(MessageDigest.getInstance("SHA256")
				.digest(username.getBytes(StandardCharsets.UTF_8)));

		when(repository.getUser(userHash))
			.thenReturn(Uni.createFrom().failure(new WebApplicationException(Response.status(Response.Status.NOT_FOUND).build())));

		/*
		 * Test
		 */
		GetAccessTokenRequest request = new GetAccessTokenRequest()
			.setAcquirerId("acquirer_id")
			.setChannel("channel")
			.setClientId("client_id")
			.setGrantType(GrantType.PASSWORD)
			.setMerchantId("merchant_id")
			.setRequestId("request_id")
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
	 * @throws NoSuchAlgorithmException
	 */
	@Test
	void given_userCredentials_when_getUserReturns500_then_getFailure() throws NoSuchAlgorithmException {
		/*
		 * Setup
		 */
		final String username = "username";
		final String password = "password";

		String userHash = Base64.getUrlEncoder()
			.encodeToString(MessageDigest.getInstance("SHA256")
				.digest(username.getBytes(StandardCharsets.UTF_8)));

		when(repository.getUser(userHash))
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
			.setRequestId("request_id")
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
	 * @throws NoSuchAlgorithmException
	 */
	@Test
	void given_userCredentials_when_getUserThrowsAnotherException_then_getFailure() throws NoSuchAlgorithmException {
		/*
		 * Setup
		 */
		final String username = "username";
		final String password = "password";

		String userHash = Base64.getUrlEncoder()
			.encodeToString(MessageDigest.getInstance("SHA256")
				.digest(username.getBytes(StandardCharsets.UTF_8)));

		when(repository.getUser(userHash))
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
			.setRequestId("request_id")
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
	 * @throws NoSuchAlgorithmException
	 */
	@Test
	void given_userCredentials_when_consistencyVerificationFailsDueToAcquirerId_then_getFailure() throws NoSuchAlgorithmException {
		/*
		 * Setup
		 */
		final String username = "username";
		final String password = "password";
		final String salt = "zfN59oSr9RfFiiSASUO1YIcv8bARsj1OAV8tEydQiKC3su5Mlz1TsjbFwvWrGCjXdkDUsbeXGnYZDavJuTKw6Q==";

		String userHash = Base64.getUrlEncoder()
			.encodeToString(MessageDigest.getInstance("SHA256")
				.digest(username.getBytes(StandardCharsets.UTF_8)));

		byte[] passwordBytes = password.getBytes(StandardCharsets.UTF_8);
		byte[] saltBytes = Base64.getDecoder().decode(salt);

		byte[] data = new byte[passwordBytes.length + saltBytes.length];
		System.arraycopy(passwordBytes, 0, data, 0, passwordBytes.length);
		System.arraycopy(saltBytes, 0, data, passwordBytes.length, saltBytes.length);

		byte[] passwordHashBytes = MessageDigest.getInstance("SHA256").digest(data);

		String passwordHash = Base64.getEncoder().encodeToString(passwordHashBytes);

		when(repository.getUser(userHash))
			.thenReturn(UniGenerator.item(new User()
				.setAcquirerId("acquirer_id_2")
				.setChannel("channel")
				.setMerchantId("merchant_id")
				.setPasswordHash(passwordHash)
				.setSalt(salt)
				.setUsername(username)));

		when(clientVerifier.verify("client_id", "channel", null))
			.thenReturn(UniGenerator.item(new Client()));

		when(roleFinder.findRoles("acquirer_id", "channel", "client_id", "merchant_id", "terminal_id"))
			.thenReturn(UniGenerator.item(new Role()
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
			.setRequestId("request_id")
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
	 * @throws NoSuchAlgorithmException
	 */
	@Test
	void given_userCredentials_when_consistencyVerificationFailsDueToChannel_then_getFailure() throws NoSuchAlgorithmException {
		/*
		 * Setup
		 */
		final String username = "username";
		final String password = "password";
		final String salt = "zfN59oSr9RfFiiSASUO1YIcv8bARsj1OAV8tEydQiKC3su5Mlz1TsjbFwvWrGCjXdkDUsbeXGnYZDavJuTKw6Q==";

		String userHash = Base64.getUrlEncoder()
			.encodeToString(MessageDigest.getInstance("SHA256")
				.digest(username.getBytes(StandardCharsets.UTF_8)));

		byte[] passwordBytes = password.getBytes(StandardCharsets.UTF_8);
		byte[] saltBytes = Base64.getDecoder().decode(salt);

		byte[] data = new byte[passwordBytes.length + saltBytes.length];
		System.arraycopy(passwordBytes, 0, data, 0, passwordBytes.length);
		System.arraycopy(saltBytes, 0, data, passwordBytes.length, saltBytes.length);

		byte[] passwordHashBytes = MessageDigest.getInstance("SHA256").digest(data);

		String passwordHash = Base64.getEncoder().encodeToString(passwordHashBytes);

		when(repository.getUser(userHash))
			.thenReturn(UniGenerator.item(new User()
				.setAcquirerId("acquirer_id")
				.setChannel("channel_2")
				.setMerchantId("merchant_id")
				.setPasswordHash(passwordHash)
				.setSalt(salt)
				.setUsername(username)));

		when(clientVerifier.verify("client_id", "channel", null))
			.thenReturn(UniGenerator.item(new Client()));

		when(roleFinder.findRoles("acquirer_id", "channel", "client_id", "merchant_id", "terminal_id"))
			.thenReturn(UniGenerator.item(new Role()
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
			.setRequestId("request_id")
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
	 * @throws NoSuchAlgorithmException
	 */
	@Test
	void given_userCredentials_when_consistencyVerificationFailsDueToMerchantId_then_getFailure() throws NoSuchAlgorithmException {
		/*
		 * Setup
		 */
		final String username = "username";
		final String password = "password";
		final String salt = "zfN59oSr9RfFiiSASUO1YIcv8bARsj1OAV8tEydQiKC3su5Mlz1TsjbFwvWrGCjXdkDUsbeXGnYZDavJuTKw6Q==";

		String userHash = Base64.getUrlEncoder()
			.encodeToString(MessageDigest.getInstance("SHA256")
				.digest(username.getBytes(StandardCharsets.UTF_8)));

		byte[] passwordBytes = password.getBytes(StandardCharsets.UTF_8);
		byte[] saltBytes = Base64.getDecoder().decode(salt);

		byte[] data = new byte[passwordBytes.length + saltBytes.length];
		System.arraycopy(passwordBytes, 0, data, 0, passwordBytes.length);
		System.arraycopy(saltBytes, 0, data, passwordBytes.length, saltBytes.length);

		byte[] passwordHashBytes = MessageDigest.getInstance("SHA256").digest(data);

		String passwordHash = Base64.getEncoder().encodeToString(passwordHashBytes);

		when(repository.getUser(userHash))
			.thenReturn(UniGenerator.item(new User()
				.setAcquirerId("acquirer_id")
				.setChannel("channel")
				.setMerchantId("merchant_id_2")
				.setPasswordHash(passwordHash)
				.setSalt(salt)
				.setUsername(username)));

		when(clientVerifier.verify("client_id", "channel", null))
			.thenReturn(UniGenerator.item(new Client()));

		when(roleFinder.findRoles("acquirer_id", "channel", "client_id", "merchant_id", "terminal_id"))
			.thenReturn(UniGenerator.item(new Role()
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
			.setRequestId("request_id")
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
	 * @throws NoSuchAlgorithmException
	 */
	@Test
	void given_userCredentials_when_passwordIsWrong_then_getFailure() throws NoSuchAlgorithmException {
		/*
		 * Setup
		 */
		final String username = "username";
		final String password = "password";
		final String salt = "zfN59oSr9RfFiiSASUO1YIcv8bARsj1OAV8tEydQiKC3su5Mlz1TsjbFwvWrGCjXdkDUsbeXGnYZDavJuTKw6Q==";

		String userHash = Base64.getUrlEncoder()
			.encodeToString(MessageDigest.getInstance("SHA256")
				.digest(username.getBytes(StandardCharsets.UTF_8)));

		byte[] passwordBytes = password.getBytes(StandardCharsets.UTF_8);
		byte[] saltBytes = Base64.getDecoder().decode(salt);

		byte[] data = new byte[passwordBytes.length + saltBytes.length];
		System.arraycopy(passwordBytes, 0, data, 0, passwordBytes.length);
		System.arraycopy(saltBytes, 0, data, passwordBytes.length, saltBytes.length);

		byte[] passwordHashBytes = MessageDigest.getInstance("SHA256").digest(data);

		String passwordHash = Base64.getEncoder().encodeToString(passwordHashBytes);

		when(repository.getUser(userHash))
			.thenReturn(UniGenerator.item(new User()
				.setAcquirerId("acquirer_id")
				.setChannel("channel")
				.setMerchantId("merchant_id")
				.setPasswordHash(passwordHash)
				.setSalt(salt)
				.setUsername(username)));

		when(clientVerifier.verify("client_id", "channel", null))
			.thenReturn(UniGenerator.item(new Client()));

		when(roleFinder.findRoles("acquirer_id", "channel", "client_id", "merchant_id", "terminal_id"))
			.thenReturn(UniGenerator.item(new Role()
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
			.setRequestId("request_id")
			.setTerminalId("terminal_id")
			.setUsername(username)
			.setPassword("password_2");

		tokenByPasswordService.process(request)
			.subscribe()
			.withSubscriber(UniAssertSubscriber.create())
			.assertFailedWith(AuthException.class);
	}

	/**
	 * 
	 * @throws NoSuchAlgorithmException
	 */
	@Test
	void given_userCredentials_when_passwordVerificationThrowsException_then_getFailure() throws NoSuchAlgorithmException {
		/*
		 * Setup
		 */
		final String username = "username";
		final String password = "password";
		final String salt = "zfN59oSr9RfFiiSASUO1YIcv8bARsj1OAV8tEydQiKC3su5Mlz1TsjbFwvWrGCjXdkDUsbeXGnYZDavJuTKw6Q==";

		String userHash = Base64.getUrlEncoder()
			.encodeToString(MessageDigest.getInstance("SHA256")
				.digest(username.getBytes(StandardCharsets.UTF_8)));

		byte[] passwordBytes = password.getBytes(StandardCharsets.UTF_8);
		byte[] saltBytes = Base64.getDecoder().decode(salt);

		byte[] data = new byte[passwordBytes.length + saltBytes.length];
		System.arraycopy(passwordBytes, 0, data, 0, passwordBytes.length);
		System.arraycopy(saltBytes, 0, data, passwordBytes.length, saltBytes.length);

		byte[] passwordHashBytes = MessageDigest.getInstance("SHA256").digest(data);

		String passwordHash = Base64.getEncoder().encodeToString(passwordHashBytes);

		when(repository.getUser(userHash))
			.thenReturn(UniGenerator.item(new User()
				.setAcquirerId("acquirer_id")
				.setChannel("channel")
				.setMerchantId("merchant_id")
				.setPasswordHash(passwordHash)
				.setSalt(salt)
				.setUsername(username)));

		when(clientVerifier.verify("client_id", "channel", null))
			.thenReturn(UniGenerator.item(new Client()));

		when(roleFinder.findRoles("acquirer_id", "channel", "client_id", "merchant_id", "terminal_id"))
			.thenReturn(UniGenerator.item(new Role()
				.setRoles(List.of("role"))));

		/*
		 * Test
		 */
		try (MockedStatic<PasswordVerifier> passwordVerifier = Mockito.mockStatic(PasswordVerifier.class)) {
			passwordVerifier.when(() -> PasswordVerifier.verify(password, salt, passwordHash))
				.thenThrow(NoSuchAlgorithmException.class);

			GetAccessTokenRequest request = new GetAccessTokenRequest()
				.setAcquirerId("acquirer_id")
				.setChannel("channel")
				.setClientId("client_id")
				.setGrantType(GrantType.PASSWORD)
				.setMerchantId("merchant_id")
				.setRequestId("request_id")
				.setTerminalId("terminal_id")
				.setUsername(username)
				.setPassword(password);

			tokenByPasswordService.process(request)
				.subscribe()
				.withSubscriber(UniAssertSubscriber.create())
				.assertFailedWith(AuthError.class);
		}
	}
}