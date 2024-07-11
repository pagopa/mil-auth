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

import org.eclipse.microprofile.rest.client.inject.RestClient;
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
import it.pagopa.swclient.mil.auth.client.PoyntClient;
import it.pagopa.swclient.mil.auth.dao.ClientEntity;
import it.pagopa.swclient.mil.auth.dao.RolesEntity;
import it.pagopa.swclient.mil.auth.dao.UserRepository;
import it.pagopa.swclient.mil.auth.qualifier.PoyntToken;
import it.pagopa.swclient.mil.auth.util.AuthError;
import it.pagopa.swclient.mil.auth.util.AuthException;
import it.pagopa.swclient.mil.auth.util.UniGenerator;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

/**
 *
 * @author Antonio Tarricone
 */
@QuarkusTest
class TokenByPoyntTokenServiceTest {
	/*
	 * 
	 */
	@Inject
	@PoyntToken
	TokenByPoyntTokenService tokenByPoyntTokenService;

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

	/*
	 * 
	 */
	@InjectMock
	@RestClient
	PoyntClient poyntClient;

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
	void given_poyntToken_when_getBusinessObjectReturns401_then_getFailure() {
		/*
		 * Setup
		 */
		when(poyntClient.getBusinessObject("Bearer poynt_token", "business_id"))
			.thenReturn(UniGenerator.item(Response.status(Status.UNAUTHORIZED).build()));

		/*
		 * Test
		 */
		GetAccessTokenRequest request = new GetAccessTokenRequest()
			.setAcquirerId("acquirer_id")
			.setChannel("channel")
			.setClientId("client_id")
			.setGrantType(GrantType.POYNT_TOKEN)
			.setMerchantId("merchant_id")
			.setRequestId("request_id")
			.setTerminalId("terminal_id")
			.setExtToken("poynt_token")
			.setAddData("business_id");

		tokenByPoyntTokenService.process(request)
			.subscribe()
			.withSubscriber(UniAssertSubscriber.create())
			.assertFailedWith(AuthException.class);
	}

	/**
	 * 
	 */
	@Test
	void given_poyntToken_when_getBusinessObjectReturnsWebApplicationException_then_getFailure() {
		/*
		 * Setup
		 */
		when(poyntClient.getBusinessObject("Bearer poynt_token", "business_id"))
			.thenReturn(Uni.createFrom().failure(new WebApplicationException(401)));

		/*
		 * Test
		 */
		GetAccessTokenRequest request = new GetAccessTokenRequest()
			.setAcquirerId("acquirer_id")
			.setChannel("channel")
			.setClientId("client_id")
			.setGrantType(GrantType.POYNT_TOKEN)
			.setMerchantId("merchant_id")
			.setRequestId("request_id")
			.setTerminalId("terminal_id")
			.setExtToken("poynt_token")
			.setAddData("business_id");

		tokenByPoyntTokenService.process(request)
			.subscribe()
			.withSubscriber(UniAssertSubscriber.create())
			.assertFailedWith(AuthException.class);
	}

	/**
	 * 
	 */
	@Test
	void given_poyntToken_when_getBusinessObjectReturnsUnhandledException_then_getFailure() {
		/*
		 * Setup
		 */
		when(poyntClient.getBusinessObject("Bearer poynt_token", "business_id"))
			.thenReturn(Uni.createFrom().failure(new Exception("synthetic_exception")));

		/*
		 * Test
		 */
		GetAccessTokenRequest request = new GetAccessTokenRequest()
			.setAcquirerId("acquirer_id")
			.setChannel("channel")
			.setClientId("client_id")
			.setGrantType(GrantType.POYNT_TOKEN)
			.setMerchantId("merchant_id")
			.setRequestId("request_id")
			.setTerminalId("terminal_id")
			.setExtToken("poynt_token")
			.setAddData("business_id");

		tokenByPoyntTokenService.process(request)
			.subscribe()
			.withSubscriber(UniAssertSubscriber.create())
			.assertFailedWith(AuthError.class);
	}

	/**
	 * 
	 * @throws ParseException
	 */
	@Test
	void given_poyntToken_when_allGoesOk_then_getAccessToken() throws ParseException {
		/*
		 * Setup
		 */
		when(poyntClient.getBusinessObject("Bearer poynt_token", "business_id"))
			.thenReturn(UniGenerator.item(Response.ok().build()));

		when(clientVerifier.verify("client_id", "channel", null))
			.thenReturn(UniGenerator.item(new ClientEntity()));

		when(roleFinder.findRoles("acquirer_id", "channel", "client_id", "merchant_id", "terminal_id"))
			.thenReturn(UniGenerator.item(new RolesEntity()
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
			.setGrantType(GrantType.POYNT_TOKEN)
			.setMerchantId("merchant_id")
			.setRequestId("request_id")
			.setTerminalId("terminal_id")
			.setExtToken("poynt_token")
			.setAddData("business_id");

		tokenByPoyntTokenService.process(request)
			.subscribe()
			.with(
				response -> {
					assertEquals(
						"eyJraWQiOiJrZXlfbmFtZS9rZXlfdmVyc2lvbiIsImFsZyI6IlJTMjU2In0.eyJzdWIiOiJjbGllbnRfaWQiLCJjbGllbnRJZCI6ImNsaWVudF9pZCIsImNoYW5uZWwiOiJjaGFubmVsIiwiaXNzIjoiaHR0cHM6Ly9taWwtYXV0aCIsImdyb3VwcyI6InJvbGUiLCJ0ZXJtaW5hbElkIjoidGVybWluYWxfaWQiLCJhdWQiOiJodHRwczovL21pbCIsIm1lcmNoYW50SWQiOiJtZXJjaGFudF9pZCIsInNjb3BlIjoic2NvcGUiLCJmaXNjYWxDb2RlIjoiZW5jX2Zpc2NhbF9jb2RlIiwiZXhwIjoxNzE3NjUyLCJhY3F1aXJlcklkIjoiYWNxdWlyZXJfaWQiLCJpYXQiOjE3MTc1OTJ9.AA",
						response.getAccessToken());
				},
				f -> fail(f));
	}
}