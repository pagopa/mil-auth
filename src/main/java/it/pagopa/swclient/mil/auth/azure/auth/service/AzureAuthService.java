/*
 * AzureAuthService.java
 *
 * 1 ago 2023
 */
package it.pagopa.swclient.mil.auth.azure.auth.service;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
import it.pagopa.swclient.mil.auth.azure.auth.bean.GetAccessTokenResponse;
import it.pagopa.swclient.mil.auth.azure.auth.client.AzureAuthClient;
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

	/**
	 * 
	 * @param identity
	 * @param client
	 */
	AzureAuthService(
		@ConfigProperty(name = "azure-auth-api.identity") String identity,
		@RestClient AzureAuthClient client) {
		this.identity = identity;
		this.client = client;
	}

	/**
	 * 
	 * @return
	 */
	public Uni<GetAccessTokenResponse> getAccessToken() {
		Log.debug("Authenticating to Azure AD for Key Vault.");
		return client.getAccessToken(identity, VAULT);
	}

	/**
	 * 
	 * @return
	 */
	public Uni<GetAccessTokenResponse> getAccessTokenForStorage() {
		Log.debug("Authenticating to Azure AD for Storage Account.");
		return client.getAccessToken(identity, STORAGE);
	}
}
