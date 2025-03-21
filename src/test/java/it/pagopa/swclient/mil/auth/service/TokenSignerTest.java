/*
 * TokenSignerTest.java
 *
 * 5 giu 2024
 */
package it.pagopa.swclient.mil.auth.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionException;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import com.nimbusds.jose.util.Base64URL;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.MockitoConfig;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.TimeoutException;
import io.smallrye.mutiny.helpers.test.UniAssertSubscriber;
import io.vertx.mutiny.core.eventbus.EventBus;
import it.pagopa.swclient.mil.auth.bean.ClaimName;
import it.pagopa.swclient.mil.auth.util.AuthError;
import it.pagopa.swclient.mil.auth.util.AuthException;
import it.pagopa.swclient.mil.auth.util.KeyUtils;
import it.pagopa.swclient.mil.auth.util.SignedJWTFactory;
import it.pagopa.swclient.mil.auth.util.UniGenerator;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.DeletedKeyBundle;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.JsonWebKey;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.JsonWebKeyOperation;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.JsonWebKeyType;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyAttributes;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyBundle;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyCreateParameters;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyOperationResult;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeySignParameters;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyVerifyParameters;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyVerifyResult;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.service.AzureKeyVaultKeysExtReactiveService;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.service.AzureKeyVaultKeysReactiveService;
import jakarta.inject.Inject;

/**
 * 
 * @author Antonio Tarricone
 */
@QuarkusTest
class TokenSignerTest {
	/*
	 * 
	 */
	@Inject
	TokenSigner tokenSigner;

	/*
	 * 
	 */
	@InjectMock
	AzureKeyVaultKeysExtReactiveService keysExtService;

	/*
	 * 
	 */
	@InjectMock
	AzureKeyVaultKeysReactiveService keysService;

	/*
	 * 
	 */
	@InjectMock
	@MockitoConfig(convertScopes = true)
	EventBus eventBus;

	/*
	 *
	 */
	@ConfigProperty(name = "quarkus.rest-client.azure-key-vault-keys.url")
	String vaultBaseUrl;

	/*
	 * 
	 */
	private String keyBaseUrl;

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
		keyBaseUrl = vaultBaseUrl + (vaultBaseUrl.endsWith("/") ? "keys/" : "/keys/");
		tokenSigner.cleanCache();
		Mockito.reset(keysExtService, keysService, eventBus);
	}

	/**
	 * 
	 */
	@Test
	void given_claimsSetToSign_when_suitableKeyExists_then_getSignedJwt() {
		/*
		 * Setup
		 */
		when(keysExtService.getKeyWithLongestExp(
			KeyUtils.DOMAIN_VALUE,
			List.of(JsonWebKeyOperation.SIGN, JsonWebKeyOperation.VERIFY),
			List.of(JsonWebKeyType.RSA)))
			.thenReturn(UniGenerator.item(
				Optional.of(new KeyBundle()
					.setAttributes(new KeyAttributes()
						.setExp(Instant.now().plus(15, ChronoUnit.MINUTES).getEpochSecond()))
					.setKey(new JsonWebKey()
						.setKid(keyBaseUrl + "key_name/key_version")))));

		when(keysService.sign(
			eq("key_name"),
			eq("key_version"),
			any(KeySignParameters.class)))
			.thenReturn(UniGenerator.item(
				new KeyOperationResult()
					.setKid(keyBaseUrl + "key_name/key_version")
					.setValue(new byte[1])));

		/*
		 * Test
		 */
		JWTClaimsSet payload = new JWTClaimsSet.Builder()
			.subject("client_id")
			.issueTime(new Date(1717592477))
			.expirationTime(new Date(1717592477 + 60000))
			.claim(ClaimName.ACQUIRER_ID, "acquirer_id")
			.claim(ClaimName.CHANNEL, "channel")
			.claim(ClaimName.MERCHANT_ID, "merchant_id")
			.claim(ClaimName.CLIENT_ID, "client_id")
			.claim(ClaimName.TERMINAL_ID, "terminal_id")
			.claim(ClaimName.SCOPE, "scope")
			.claim(ClaimName.GROUPS, "role")
			.claim(ClaimName.FISCAL_CODE, "enc_fiscal_code")
			.issuer("https://mil-auth")
			.audience("https://mil")
			.build();

		String expected = "eyJraWQiOiJrZXlfbmFtZS9rZXlfdmVyc2lvbiIsImFsZyI6IlJTMjU2In0.eyJzdWIiOiJjbGllbnRfaWQiLCJjbGllbnRJZCI6ImNsaWVudF9pZCIsImNoYW5uZWwiOiJjaGFubmVsIiwiaXNzIjoiaHR0cHM6Ly9taWwtYXV0aCIsImdyb3VwcyI6InJvbGUiLCJ0ZXJtaW5hbElkIjoidGVybWluYWxfaWQiLCJhdWQiOiJodHRwczovL21pbCIsIm1lcmNoYW50SWQiOiJtZXJjaGFudF9pZCIsInNjb3BlIjoic2NvcGUiLCJmaXNjYWxDb2RlIjoiZW5jX2Zpc2NhbF9jb2RlIiwiZXhwIjoxNzE3NjUyLCJhY3F1aXJlcklkIjoiYWNxdWlyZXJfaWQiLCJpYXQiOjE3MTc1OTJ9.AA";

		tokenSigner.sign(payload)
			.subscribe()
			.with(
				actual -> assertEquals(expected, actual.serialize()),
				f -> fail(f));
	}

	/**
	 * 
	 */
	@Test
	void given_claimsSetToSign_when_suitableKeyIsCached_then_getSignedJwt() {
		/*
		 * Setup
		 */
		when(keysExtService.getKeyWithLongestExp(
			KeyUtils.DOMAIN_VALUE,
			List.of(JsonWebKeyOperation.SIGN, JsonWebKeyOperation.VERIFY),
			List.of(JsonWebKeyType.RSA)))
			.thenReturn(UniGenerator.item(
				Optional.of(new KeyBundle()
					.setAttributes(new KeyAttributes()
						.setExp(Instant.now().plus(15, ChronoUnit.MINUTES).getEpochSecond()))
					.setKey(new JsonWebKey()
						.setKid(keyBaseUrl + "key_name/key_version")))));

		when(keysService.sign(
			eq("key_name"),
			eq("key_version"),
			any(KeySignParameters.class)))
			.thenReturn(UniGenerator.item(
				new KeyOperationResult()
					.setKid(keyBaseUrl + "key_name/key_version")
					.setValue(new byte[1])));

		/*
		 * Test
		 */
		JWTClaimsSet payload = new JWTClaimsSet.Builder()
			.subject("client_id")
			.issueTime(new Date(1717592477))
			.expirationTime(new Date(1717592477 + 60000))
			.claim(ClaimName.ACQUIRER_ID, "acquirer_id")
			.claim(ClaimName.CHANNEL, "channel")
			.claim(ClaimName.MERCHANT_ID, "merchant_id")
			.claim(ClaimName.CLIENT_ID, "client_id")
			.claim(ClaimName.TERMINAL_ID, "terminal_id")
			.claim(ClaimName.SCOPE, "scope")
			.claim(ClaimName.GROUPS, "role")
			.claim(ClaimName.FISCAL_CODE, "enc_fiscal_code")
			.issuer("https://mil-auth")
			.audience("https://mil")
			.build();

		/*
		 * Invocation to cache kid.
		 */
		tokenSigner.sign(payload).subscribe().with(System.out::println, System.err::println);

		String expected = "eyJraWQiOiJrZXlfbmFtZS9rZXlfdmVyc2lvbiIsImFsZyI6IlJTMjU2In0.eyJzdWIiOiJjbGllbnRfaWQiLCJjbGllbnRJZCI6ImNsaWVudF9pZCIsImNoYW5uZWwiOiJjaGFubmVsIiwiaXNzIjoiaHR0cHM6Ly9taWwtYXV0aCIsImdyb3VwcyI6InJvbGUiLCJ0ZXJtaW5hbElkIjoidGVybWluYWxfaWQiLCJhdWQiOiJodHRwczovL21pbCIsIm1lcmNoYW50SWQiOiJtZXJjaGFudF9pZCIsInNjb3BlIjoic2NvcGUiLCJmaXNjYWxDb2RlIjoiZW5jX2Zpc2NhbF9jb2RlIiwiZXhwIjoxNzE3NjUyLCJhY3F1aXJlcklkIjoiYWNxdWlyZXJfaWQiLCJpYXQiOjE3MTc1OTJ9.AA";

		tokenSigner.sign(payload)
			.subscribe()
			.with(
				actual -> assertEquals(expected, actual.serialize()),
				f -> fail(f));
	}

	/**
	 * 
	 * @param andException
	 */
	private void given_claimsSetToSign_when_suitableKeyDoesntExist_then_createNewKeyAnd(Throwable andException) {
		/*
		 * Setup
		 */
		when(keysExtService.getKeyWithLongestExp(
			KeyUtils.DOMAIN_VALUE,
			List.of(JsonWebKeyOperation.SIGN, JsonWebKeyOperation.VERIFY),
			List.of(JsonWebKeyType.RSA)))
			.thenReturn(UniGenerator.item(
				Optional.empty()));

		if (andException == null) {
			when(keysExtService.deleteExpiredKeys(KeyUtils.DOMAIN_VALUE))
				.thenReturn(Multi.createFrom().items(
					new DeletedKeyBundle(),
					new DeletedKeyBundle()));
		} else {
			when(keysExtService.deleteExpiredKeys(KeyUtils.DOMAIN_VALUE))
				.thenThrow(andException);
		}

		when(keysService.createKey(anyString(), any(KeyCreateParameters.class)))
			.thenReturn(UniGenerator.item(
				new KeyBundle()
					.setAttributes(new KeyAttributes()
						.setExp(Instant.now().plus(15, ChronoUnit.MINUTES).getEpochSecond()))
					.setKey(new JsonWebKey()
						.setKid(keyBaseUrl + "key_name/key_version"))));

		when(keysService.sign(
			eq("key_name"),
			eq("key_version"),
			any(KeySignParameters.class)))
			.thenReturn(UniGenerator.item(
				new KeyOperationResult()
					.setKid(keyBaseUrl + "key_name/key_version")
					.setValue(new byte[1])));

		TokenSigner localTokenSigner = spy(new TokenSigner(keysExtService, keysService, eventBus));

		when(eventBus.send(KeyManCapabilities.DELETE_EXPIRED_KEYS_EVENT, null))
			.thenAnswer(i -> {
				localTokenSigner.deleteExpiredKeys(null);
				return null;
			});

		/*
		 * Test
		 */
		JWTClaimsSet payload = new JWTClaimsSet.Builder()
			.subject("client_id")
			.issueTime(new Date(1717592477))
			.expirationTime(new Date(1717592477 + 60000))
			.claim(ClaimName.ACQUIRER_ID, "acquirer_id")
			.claim(ClaimName.CHANNEL, "channel")
			.claim(ClaimName.MERCHANT_ID, "merchant_id")
			.claim(ClaimName.CLIENT_ID, "client_id")
			.claim(ClaimName.TERMINAL_ID, "terminal_id")
			.claim(ClaimName.SCOPE, "scope")
			.claim(ClaimName.GROUPS, "role")
			.claim(ClaimName.FISCAL_CODE, "enc_fiscal_code")
			.issuer("https://mil-auth")
			.audience("https://mil")
			.build();

		String expected = "eyJraWQiOiJrZXlfbmFtZS9rZXlfdmVyc2lvbiIsImFsZyI6IlJTMjU2In0.eyJzdWIiOiJjbGllbnRfaWQiLCJjbGllbnRJZCI6ImNsaWVudF9pZCIsImNoYW5uZWwiOiJjaGFubmVsIiwiaXNzIjoiaHR0cHM6Ly9taWwtYXV0aCIsImdyb3VwcyI6InJvbGUiLCJ0ZXJtaW5hbElkIjoidGVybWluYWxfaWQiLCJhdWQiOiJodHRwczovL21pbCIsIm1lcmNoYW50SWQiOiJtZXJjaGFudF9pZCIsInNjb3BlIjoic2NvcGUiLCJmaXNjYWxDb2RlIjoiZW5jX2Zpc2NhbF9jb2RlIiwiZXhwIjoxNzE3NjUyLCJhY3F1aXJlcklkIjoiYWNxdWlyZXJfaWQiLCJpYXQiOjE3MTc1OTJ9.AA";

		localTokenSigner.sign(payload)
			.subscribe()
			.with(
				actual -> {
					System.out.println(">>>> " + actual.serialize());
					assertEquals(expected, actual.serialize());
				},
				f -> fail(f));

		verify(localTokenSigner).deleteExpiredKeys(null);
	}

	/**
	 * 
	 */
	@Test
	void given_claimsSetToSign_when_suitableKeyDoesntExist_then_createNewKeyAndGetSignedJwt() {
		given_claimsSetToSign_when_suitableKeyDoesntExist_then_createNewKeyAnd(null);
	}

	/**
	 * 
	 */
	@Test
	void given_claimsSetToSign_when_suitableKeyDoesntExist_then_createNewKeyAndGetSignedJwtAndHandleErrorDeletingExpKeys() {
		given_claimsSetToSign_when_suitableKeyDoesntExist_then_createNewKeyAnd(new CompletionException(new Exception("synthetic exception")));
	}

	/**
	 * 
	 */
	@Test
	void given_claimsSetToSign_when_suitableKeyDoesntExist_then_createNewKeyAndGetSignedJwtAndHandleTimeoutDeletingExpKeys() {
		given_claimsSetToSign_when_suitableKeyDoesntExist_then_createNewKeyAnd(new TimeoutException());
	}

	/**
	 * 
	 */
	@Test
	void given_claimsSetToSign_when_jwtCreateInstanceGoesWrong_then_getFailure() {
		/*
		 * Setup
		 */
		when(keysExtService.getKeyWithLongestExp(
			KeyUtils.DOMAIN_VALUE,
			List.of(JsonWebKeyOperation.SIGN, JsonWebKeyOperation.VERIFY),
			List.of(JsonWebKeyType.RSA)))
			.thenReturn(UniGenerator.item(
				Optional.of(new KeyBundle()
					.setAttributes(new KeyAttributes()
						.setExp(Instant.now().plus(15, ChronoUnit.MINUTES).getEpochSecond()))
					.setKey(new JsonWebKey()
						.setKid(keyBaseUrl + "key_name/key_version")))));

		when(keysService.sign(
			eq("key_name"),
			eq("key_version"),
			any(KeySignParameters.class)))
			.thenReturn(UniGenerator.item(
				new KeyOperationResult()
					.setKid(keyBaseUrl + "key_name/key_version")
					.setValue(new byte[1])));

		try (MockedStatic<SignedJWTFactory> signedJWTFactory = mockStatic(SignedJWTFactory.class)) {
			signedJWTFactory.when(() -> SignedJWTFactory.createInstance(
				any(Base64URL.class),
				any(Base64URL.class),
				any(Base64URL.class)))
				.thenThrow(new ParseException("synthetic_exception", 0));

			/*
			 * Test
			 */
			JWTClaimsSet payload = new JWTClaimsSet.Builder()
				.subject("client_id")
				.issueTime(new Date(1717592477))
				.expirationTime(new Date(1717592477 + 60000))
				.claim(ClaimName.ACQUIRER_ID, "acquirer_id")
				.claim(ClaimName.CHANNEL, "channel")
				.claim(ClaimName.MERCHANT_ID, "merchant_id")
				.claim(ClaimName.CLIENT_ID, "client_id")
				.claim(ClaimName.TERMINAL_ID, "terminal_id")
				.claim(ClaimName.SCOPE, "scope")
				.claim(ClaimName.GROUPS, "role")
				.claim(ClaimName.FISCAL_CODE, "enc_fiscal_code")
				.issuer("https://mil-auth")
				.audience("https://mil")
				.build();

			tokenSigner.sign(payload)
				.subscribe()
				.withSubscriber(UniAssertSubscriber.create())
				.assertFailedWith(AuthError.class);
		}
	}

	/**
	 * 
	 */
	@Test
	void given_claimsSetToSign_when_messageDigestGetInstanceGoesWrong_then_getFailure() {
		/*
		 * Setup
		 */
		when(keysExtService.getKeyWithLongestExp(
			KeyUtils.DOMAIN_VALUE,
			List.of(JsonWebKeyOperation.SIGN, JsonWebKeyOperation.VERIFY),
			List.of(JsonWebKeyType.RSA)))
			.thenReturn(UniGenerator.item(
				Optional.of(new KeyBundle()
					.setAttributes(new KeyAttributes()
						.setExp(Instant.now().plus(15, ChronoUnit.MINUTES).getEpochSecond()))
					.setKey(new JsonWebKey()
						.setKid(keyBaseUrl + "key_name/key_version")))));

		when(keysService.sign(
			eq("key_name"),
			eq("key_version"),
			any(KeySignParameters.class)))
			.thenReturn(UniGenerator.item(
				new KeyOperationResult()
					.setKid(keyBaseUrl + "key_name/key_version")
					.setValue(new byte[1])));

		try (MockedStatic<MessageDigest> digest = mockStatic(MessageDigest.class)) {
			digest.when(() -> MessageDigest.getInstance("SHA256"))
				.thenThrow(NoSuchAlgorithmException.class);

			/*
			 * Test
			 */
			JWTClaimsSet payload = new JWTClaimsSet.Builder()
				.subject("client_id")
				.issueTime(new Date(1717592477))
				.expirationTime(new Date(1717592477 + 60000))
				.claim(ClaimName.ACQUIRER_ID, "acquirer_id")
				.claim(ClaimName.CHANNEL, "channel")
				.claim(ClaimName.MERCHANT_ID, "merchant_id")
				.claim(ClaimName.CLIENT_ID, "client_id")
				.claim(ClaimName.TERMINAL_ID, "terminal_id")
				.claim(ClaimName.SCOPE, "scope")
				.claim(ClaimName.GROUPS, "role")
				.claim(ClaimName.FISCAL_CODE, "enc_fiscal_code")
				.issuer("https://mil-auth")
				.audience("https://mil")
				.build();

			tokenSigner.sign(payload)
				.subscribe()
				.withSubscriber(UniAssertSubscriber.create())
				.assertFailedWith(AuthError.class);
		}
	}

	/**
	 * 
	 */
	@Test
	void given_claimsSetToSign_when_unexpectedErrorOccurs_then_getFailure() {
		/*
		 * Setup
		 */
		when(keysExtService.getKeyWithLongestExp(
			KeyUtils.DOMAIN_VALUE,
			List.of(JsonWebKeyOperation.SIGN, JsonWebKeyOperation.VERIFY),
			List.of(JsonWebKeyType.RSA)))
			.thenReturn(UniGenerator.exception("code_string", "message_string"));

		/*
		 * Test
		 */
		JWTClaimsSet payload = new JWTClaimsSet.Builder()
			.subject("client_id")
			.issueTime(new Date(1717592477))
			.expirationTime(new Date(1717592477 + 60000))
			.claim(ClaimName.ACQUIRER_ID, "acquirer_id")
			.claim(ClaimName.CHANNEL, "channel")
			.claim(ClaimName.MERCHANT_ID, "merchant_id")
			.claim(ClaimName.CLIENT_ID, "client_id")
			.claim(ClaimName.TERMINAL_ID, "terminal_id")
			.claim(ClaimName.SCOPE, "scope")
			.claim(ClaimName.GROUPS, "role")
			.claim(ClaimName.FISCAL_CODE, "enc_fiscal_code")
			.issuer("https://mil-auth")
			.audience("https://mil")
			.build();

		tokenSigner.sign(payload)
			.subscribe()
			.withSubscriber(UniAssertSubscriber.create())
			.assertFailedWith(AuthError.class);
	}

	/**
	 * 
	 * @throws ParseException
	 */
	@Test
	void given_signedJwt_when_allGoesOk_get_okOnSignatureValidation() throws ParseException {
		/*
		 * Setup
		 */
		when(keysService.verify(
			eq("key_name"),
			eq("key_version"),
			any(KeyVerifyParameters.class)))
			.thenReturn(UniGenerator.item(new KeyVerifyResult().setValue(Boolean.TRUE)));

		/*
		 * Test
		 */
		SignedJWT token = SignedJWT.parse("eyJraWQiOiJrZXlfbmFtZS9rZXlfdmVyc2lvbiIsImFsZyI6IlJTMjU2In0.eyJzdWIiOiJjbGllbnRfaWQiLCJjbGllbnRJZCI6ImNsaWVudF9pZCIsImNoYW5uZWwiOiJjaGFubmVsIiwiaXNzIjoiaHR0cHM6Ly9taWwtYXV0aCIsImdyb3VwcyI6InJvbGUiLCJ0ZXJtaW5hbElkIjoidGVybWluYWxfaWQiLCJhdWQiOiJodHRwczovL21pbCIsIm1lcmNoYW50SWQiOiJtZXJjaGFudF9pZCIsInNjb3BlIjoic2NvcGUiLCJmaXNjYWxDb2RlIjoiZW5jX2Zpc2NhbF9jb2RlIiwiZXhwIjoxNzE3NjUyLCJhY3F1aXJlcklkIjoiYWNxdWlyZXJfaWQiLCJpYXQiOjE3MTc1OTJ9.AA");
		tokenSigner.verify(token)
			.subscribe()
			.withSubscriber(UniAssertSubscriber.create())
			.assertItem(null);
	}

	/**
	 * 
	 * @throws ParseException
	 */
	@Test
	void given_signedJwt_when_allGoesOkButSignatureIsWrong_get_exceptionOnSignatureValidation() throws ParseException {
		/*
		 * Setup
		 */
		when(keysService.verify(
			eq("key_name"),
			eq("key_version"),
			any(KeyVerifyParameters.class)))
			.thenReturn(UniGenerator.item(new KeyVerifyResult().setValue(Boolean.FALSE)));

		/*
		 * Test
		 */
		SignedJWT token = SignedJWT.parse("eyJraWQiOiJrZXlfbmFtZS9rZXlfdmVyc2lvbiIsImFsZyI6IlJTMjU2In0.eyJzdWIiOiJjbGllbnRfaWQiLCJjbGllbnRJZCI6ImNsaWVudF9pZCIsImNoYW5uZWwiOiJjaGFubmVsIiwiaXNzIjoiaHR0cHM6Ly9taWwtYXV0aCIsImdyb3VwcyI6InJvbGUiLCJ0ZXJtaW5hbElkIjoidGVybWluYWxfaWQiLCJhdWQiOiJodHRwczovL21pbCIsIm1lcmNoYW50SWQiOiJtZXJjaGFudF9pZCIsInNjb3BlIjoic2NvcGUiLCJmaXNjYWxDb2RlIjoiZW5jX2Zpc2NhbF9jb2RlIiwiZXhwIjoxNzE3NjUyLCJhY3F1aXJlcklkIjoiYWNxdWlyZXJfaWQiLCJpYXQiOjE3MTc1OTJ9.AA");
		tokenSigner.verify(token)
			.subscribe()
			.withSubscriber(UniAssertSubscriber.create())
			.assertFailedWith(AuthException.class);
	}

	/**
	 * 
	 * @throws ParseException
	 */
	@Test
	void given_signedJwt_when_tokenParsingExceptionOccurs_get_signatureValidationError() throws ParseException {
		/*
		 * Test
		 */
		SignedJWT token = SignedJWT.parse("eyJraWQiOiJrZXlfbmFtZS9rZXlfdmVyc2lvbiIsImFsZyI6IlJTMjU2In0.@.AA");
		tokenSigner.verify(token)
			.subscribe()
			.withSubscriber(UniAssertSubscriber.create())
			.assertFailedWith(AuthError.class);
	}
}
