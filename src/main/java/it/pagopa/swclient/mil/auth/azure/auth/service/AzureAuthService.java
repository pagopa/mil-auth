/*
 * AzureAuthService.java
 *
 * 1 ago 2023
 */
package it.pagopa.swclient.mil.auth.azure.auth.service;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
import it.pagopa.swclient.mil.auth.azure.auth.bean.GetAccessTokenResponse;
import it.pagopa.swclient.mil.auth.azure.auth.client.AzureAuthClient;
import it.pagopa.swclient.mil.auth.util.UniGenerator;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * 
 * @author Antonio Tarricone
 */
@ApplicationScoped
public class AzureAuthService {
	/*
	 * Scopes for authentication.
	 */
	public static final String VAULT = "https://vault.azure.net";
	public static final String STORAGE = "https://storage.azure.com";

	/*
	 *
	 */
	private AzureAuthClient client;

	/*
	 *
	 */
	private String identity;

	/*
	 * 
	 */
	private boolean cacheAccessTokens;

	/*
	 * 
	 */
	private Map<String, GetAccessTokenResponse> accessTokens;

	/**
	 * 
	 * @param identity
	 * @param cacheAccessTokens
	 * @param client
	 */
	AzureAuthService(
		@ConfigProperty(name = "azure-auth-api.identity") String identity,
		@ConfigProperty(name = "azure-auth-api.cache-access-tokens") boolean cacheAccessTokens,
		@RestClient AzureAuthClient client) {
		this.identity = identity;
		this.cacheAccessTokens = cacheAccessTokens;
		this.client = client;
		accessTokens = new HashMap<>();
	}

	/**
	 * 
	 * @return
	 */
	public Uni<GetAccessTokenResponse> getAccessToken() {
		GetAccessTokenResponse accessToken = accessTokens.get(VAULT);
		if (cacheAccessTokens && accessToken != null && accessToken.getExpiresOn() > Instant.now().getEpochSecond()) {
			Log.debug("Access token for Key Vault already got.");
			return UniGenerator.item(accessToken);
		} else {
			Log.debug("Authenticating to Azure AD for Key Vault.");
			return client.getAccessToken(identity, VAULT)
				.invoke(i -> accessTokens.put(VAULT, i));
		}
	}

	/**
	 * 
	 * @return
	 */
	public Uni<GetAccessTokenResponse> getAccessTokenForStorage() {
		GetAccessTokenResponse accessToken = accessTokens.get(STORAGE);
		if (cacheAccessTokens && accessToken != null && accessToken.getExpiresOn() > Instant.now().getEpochSecond()) {
			Log.debug("Access token for Storage Account already got.");
			return UniGenerator.item(accessToken);
		} else {
			Log.debug("Authenticating to Azure AD for Storage Account.");
			return client.getAccessToken(identity, STORAGE)
				.invoke(i -> accessTokens.put(STORAGE, i));
		}
	}
}
