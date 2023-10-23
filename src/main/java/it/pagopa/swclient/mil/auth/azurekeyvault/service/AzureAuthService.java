/*
 * AzureAuthService.java
 *
 * 1 ago 2023
 */
package it.pagopa.swclient.mil.auth.azurekeyvault.service;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
import it.pagopa.swclient.mil.auth.azurekeyvault.bean.GetAccessTokenResponse;
import it.pagopa.swclient.mil.auth.azurekeyvault.client.AzureAuthClient;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * @author Antonio Tarricone
 */
@ApplicationScoped
public class AzureAuthService {
	/*
	 * Scope for authentication.
	 */
	//private static final String VAULT = "https://vault.azure.net/.default";
	private static final String VAULT = "https://vault.azure.net";
	
	/*
	 *
	 */
	@RestClient
	AzureAuthClient client;
	
	/*
	 *
	 */
	@ConfigProperty(name = "azure-auth-api.identity")
	String identity;
	
	/**
	 * @return
	 */
	public Uni<GetAccessTokenResponse> getAccessToken() {
		Log.debug("Authenticating to Azure AD.");
		return client.getAccessToken(identity, VAULT);
	}
}
