/*
 * AzureAuthService.java
 *
 * 1 ago 2023
 */
package it.pagopa.swclient.mil.auth.azurekeyvault.service;

import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
import it.pagopa.swclient.mil.auth.azurekeyvault.bean.GetAccessTokenResponse;
import it.pagopa.swclient.mil.auth.azurekeyvault.client.AzureAuthClient;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;

/**
 * @author Antonio Tarricone
 */
@ApplicationScoped
public class AzureAuthService {
    /*
     * Grant types.
     */
    private static final String CLIENT_CREDENTIALS = "client_credentials";
    /*
     * Scope for authentication.
     */
    private static final String VAULT = "https://vault.azure.net/.default";
    /*
     *
     */
    @RestClient
    AzureAuthClient client;
    /*
     *
     */
    @ConfigProperty(name = "azure-auth-api.tenant-id")
    String tenantId;
    /*
     *
     */
    @ConfigProperty(name = "azure-auth-api.client-id")
    String clientId;
    /*
     *
     */
    @ConfigProperty(name = "azure-auth-api.client-secret")
    String clientSecret;

    /**
     * @return
     */
    public Uni<GetAccessTokenResponse> getAccessToken() {
        Log.debug("Authenticating to Azure AD.");
        return client.getAccessToken(tenantId, CLIENT_CREDENTIALS, clientId, clientSecret, VAULT);
    }
}
