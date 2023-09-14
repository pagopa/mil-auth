/**
 * 
 */
package it.pagopa.swclient.mil.auth.service;

import static org.mockito.Mockito.when;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.mutiny.helpers.test.UniAssertSubscriber;
import it.pagopa.swclient.mil.auth.bean.GetAccessTokenRequest;
import it.pagopa.swclient.mil.auth.bean.GrantType;
import it.pagopa.swclient.mil.auth.bean.Scope;
import it.pagopa.swclient.mil.auth.bean.User;
import it.pagopa.swclient.mil.auth.client.AuthDataRepository;
import it.pagopa.swclient.mil.auth.qualifier.Password;
import it.pagopa.swclient.mil.auth.util.AuthError;
import it.pagopa.swclient.mil.auth.util.PasswordVerifier;
import it.pagopa.swclient.mil.auth.util.UniGenerator;
import it.pagopa.swclient.mil.bean.Channel;
import jakarta.enterprise.inject.Any;
import jakarta.enterprise.inject.Instance;
import jakarta.enterprise.util.AnnotationLiteral;
import jakarta.inject.Inject;

/**
 *
 * @author Antonio Tarricone
 */
@QuarkusTest
@TestInstance(Lifecycle.PER_CLASS)
public class TokenByPasswordServiceTest {
	/*
	 *
	 */
	private static final String ACQUIRER_ID = "4585625";
	private static final String MERCHANT_ID = "28405fHfk73x88D";
	private static final String TERMINAL_ID = "12345678";
	private static final String CLIENT_ID = "3965df56-ca9a-49e5-97e8-061433d4a25b";
	private static final String USERNAME = "user";
	private static final String PASSWORD = "password";
	private static final String SALT = "zfN59oSr9RfFiiSASUO1YIcv8bARsj1OAV8tEydQiKC3su5Mlz1TsjbFwvWrGCjXdkDUsbeXGnYZDavJuTKw6Q==";

	/*
	 * 
	 */
	@Inject
	@Any
	Instance<TokenService> tokenService;

	/*
	 *
	 */
	@InjectMock
	@RestClient
	AuthDataRepository repository;

	/*
	 * 
	 */
	@Test
	@SuppressWarnings("serial")
	void testNoSuchAlgorithmFindingCredentials() {
		try (MockedStatic<MessageDigest> digest = Mockito.mockStatic(MessageDigest.class)) {
			digest.when(() -> MessageDigest.getInstance("SHA256"))
				.thenThrow(NoSuchAlgorithmException.class);

			/*
			 * Test.
			 */
			tokenService.select(new AnnotationLiteral<Password>() {
			})
				.get().process(new GetAccessTokenRequest("00000000-0000-0000-0000-500000000000", null, ACQUIRER_ID, Channel.POS, MERCHANT_ID, TERMINAL_ID, GrantType.PASSWORD, USERNAME, PASSWORD, null, null, null, CLIENT_ID, Scope.OFFLINE_ACCESS, null))
				.subscribe()
				.withSubscriber(UniAssertSubscriber.create())
				.assertFailedWith(AuthError.class);
		}
	}

	/*
	 * 
	 */
	@Test
	@SuppressWarnings("serial")
	void testNoSuchAlgorithmVerifingPassword() throws NoSuchAlgorithmException {
		/*
		 * User respository setup.
		 */
		String userHash = Base64.getUrlEncoder().encodeToString(
			MessageDigest.getInstance("SHA256").digest(
				USERNAME.getBytes(StandardCharsets.UTF_8)));

		String passwordHash = Base64.getEncoder().encodeToString(PasswordVerifier.hashBytes(PASSWORD, SALT));

		when(repository.getUser(userHash))
			.thenReturn(UniGenerator.item(new User(USERNAME, SALT, passwordHash, ACQUIRER_ID, Channel.POS, MERCHANT_ID)));

		try (MockedStatic<PasswordVerifier> passwordVerifier = Mockito.mockStatic(PasswordVerifier.class)) {
			passwordVerifier.when(() -> PasswordVerifier.verify(PASSWORD, SALT, passwordHash))
				.thenThrow(NoSuchAlgorithmException.class);

			/*
			 * Test.
			 */
			tokenService.select(new AnnotationLiteral<Password>() {
			})
				.get().process(new GetAccessTokenRequest("00000000-0000-0000-0000-500000000001", null, ACQUIRER_ID, Channel.POS, MERCHANT_ID, TERMINAL_ID, GrantType.PASSWORD, USERNAME, PASSWORD, null, null, null, CLIENT_ID, Scope.OFFLINE_ACCESS, null))
				.subscribe()
				.withSubscriber(UniAssertSubscriber.create())
				.assertFailedWith(AuthError.class);
		}
	}
}