/*
 * AzureAuthServiceTest.java
 *
 * 24 apr 2024
 */
package it.pagopa.swclient.mil.auth.azure.auth.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.time.Instant;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.helpers.test.UniAssertSubscriber;
import it.pagopa.swclient.mil.auth.azure.auth.bean.GetAccessTokenResponse;
import it.pagopa.swclient.mil.auth.azure.auth.client.AzureAuthClient;

/**
 * 
 * @author Antonio Tarricone
 */
@QuarkusTest
@TestInstance(Lifecycle.PER_CLASS)
class AzureAuthServiceTest {
	/*
	 *
	 */
	@InjectMock
	@RestClient
	AzureAuthClient authClient;

	/**
	 * 
	 */
	@Test
	void givenAccessTokenForKeyVault_whenGetItAgain_thenReturnTheSameAccessToken() {
		long now = Instant.now().getEpochSecond();
		when(authClient.getAccessToken(anyString(), anyString()))
			.thenReturn(Uni.createFrom().item(new GetAccessTokenResponse("Bearer", now + 3599, "", "", "this_is_the_azure_access_token_for_keyvault")));

		AzureAuthService service = new AzureAuthService("identity", true, authClient);

		GetAccessTokenResponse accessToken = service.getAccessToken()
			.subscribe()
			.withSubscriber(UniAssertSubscriber.create())
			.assertCompleted()
			.getItem();

		GetAccessTokenResponse cachedAccessToken = service.getAccessToken()
			.subscribe()
			.withSubscriber(UniAssertSubscriber.create())
			.assertCompleted()
			.getItem();
		
		assertEquals(accessToken, cachedAccessToken);
	}
	
	/**
	 * 
	 */
	@Test
	void givenAccessTokenForStorage_whenGetItAgain_thenReturnTheSameAccessToken() {
		long now = Instant.now().getEpochSecond();
		when(authClient.getAccessToken(anyString(), anyString()))
			.thenReturn(Uni.createFrom().item(new GetAccessTokenResponse("Bearer", now + 3599, "", "", "this_is_the_azure_access_token_for_storage")));

		AzureAuthService service = new AzureAuthService("identity", true, authClient);

		GetAccessTokenResponse accessToken = service.getAccessTokenForStorage()
			.subscribe()
			.withSubscriber(UniAssertSubscriber.create())
			.assertCompleted()
			.getItem();

		GetAccessTokenResponse cachedAccessToken = service.getAccessTokenForStorage()
			.subscribe()
			.withSubscriber(UniAssertSubscriber.create())
			.assertCompleted()
			.getItem();
		
		assertEquals(accessToken, cachedAccessToken);
	}
}
