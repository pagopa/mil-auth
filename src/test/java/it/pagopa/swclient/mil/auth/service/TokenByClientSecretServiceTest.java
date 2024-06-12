/*
 * TokenByClientSecretServiceTest.java
 *
 * 10 giu 2024
 */
package it.pagopa.swclient.mil.auth.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.text.ParseException;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import it.pagopa.swclient.mil.auth.bean.Client;
import it.pagopa.swclient.mil.auth.bean.EncryptedClaim;
import it.pagopa.swclient.mil.auth.bean.GetAccessTokenRequest;
import it.pagopa.swclient.mil.auth.bean.GrantType;
import it.pagopa.swclient.mil.auth.bean.Role;
import it.pagopa.swclient.mil.auth.bean.Scope;
import it.pagopa.swclient.mil.auth.qualifier.ClientCredentials;
import it.pagopa.swclient.mil.auth.util.UniGenerator;
import jakarta.inject.Inject;

/**
 * 
 * @author Antonio Tarricone
 */
@QuarkusTest
@TestInstance(Lifecycle.PER_CLASS)
class TokenByClientSecretServiceTest {
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

	/*
	 * 
	 */
	@InjectMock
	ClaimEncryptor claimEncryptor;

	/*
	 * 
	 */
	@Inject
	@ClientCredentials
	TokenByClientSecretService tokenByClientSecretService;

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
	void given_clientCredentials_when_allGoesOk_then_getAccessToken() throws ParseException {
		/*
		 * Setup
		 */
		when(clientVerifier.verify("client_id", "channel", "client_secret"))
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
			.setClientSecret("client_secret")
			.setGrantType(GrantType.CLIENT_CREDENTIALS)
			.setMerchantId("merchant_id")
			.setRequestId("request_id")
			.setTerminalId("terminal_id");

		tokenByClientSecretService.process(request)
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
	 * @throws ParseException
	 * 
	 */
	@Test
	void given_clientCredentialsWithFiscalCodeAndOfflineScope_when_allGoesOk_then_getTokens() throws ParseException {
		/*
		 * Setup
		 */
		when(clientVerifier.verify("client_id", "channel", "client_secret"))
			.thenReturn(UniGenerator.item(new Client()));

		when(roleFinder.findRoles("acquirer_id", "channel", "client_id", "merchant_id", "terminal_id"))
			.thenReturn(UniGenerator.item(new Role()
				.setRoles(List.of("role"))));

		SignedJWT signedJwt = SignedJWT.parse("eyJraWQiOiJrZXlfbmFtZS9rZXlfdmVyc2lvbiIsImFsZyI6IlJTMjU2In0.eyJzdWIiOiJjbGllbnRfaWQiLCJjbGllbnRJZCI6ImNsaWVudF9pZCIsImNoYW5uZWwiOiJjaGFubmVsIiwiaXNzIjoiaHR0cHM6Ly9taWwtYXV0aCIsImdyb3VwcyI6InJvbGUiLCJ0ZXJtaW5hbElkIjoidGVybWluYWxfaWQiLCJhdWQiOiJodHRwczovL21pbCIsIm1lcmNoYW50SWQiOiJtZXJjaGFudF9pZCIsInNjb3BlIjoic2NvcGUiLCJmaXNjYWxDb2RlIjoiZW5jX2Zpc2NhbF9jb2RlIiwiZXhwIjoxNzE3NjUyLCJhY3F1aXJlcklkIjoiYWNxdWlyZXJfaWQiLCJpYXQiOjE3MTc1OTJ9.AA");

		when(tokenSigner.sign(any(JWTClaimsSet.class)))
			.thenReturn(UniGenerator.item(signedJwt));

		when(claimEncryptor.encrypt("CGNNMO80A41A662W"))
			.thenReturn(UniGenerator.item(new EncryptedClaim()
				.setAlg("alg")
				.setKid("kid")
				.setValue(new byte[1])));

		/*
		 * Test
		 */
		GetAccessTokenRequest request = new GetAccessTokenRequest()
			.setAcquirerId("acquirer_id")
			.setChannel("channel")
			.setClientId("client_id")
			.setClientSecret("client_secret")
			.setGrantType(GrantType.CLIENT_CREDENTIALS)
			.setMerchantId("merchant_id")
			.setRequestId("request_id")
			.setTerminalId("terminal_id")
			.setScope(Scope.OFFLINE_ACCESS)
			.setFiscalCode("CGNNMO80A41A662W");

		tokenByClientSecretService.process(request)
			.subscribe()
			.with(
				response -> {
					assertNotNull(response.getAccessToken());
					assertNotNull(response.getRefreshToken());
				},
				f -> fail(f));
	}
}