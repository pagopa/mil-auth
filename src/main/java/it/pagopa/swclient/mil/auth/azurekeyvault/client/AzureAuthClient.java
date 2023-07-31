/*
 * AzureAuthClient.java
 *
 * 23 lug 2023
 */
package it.pagopa.swclient.mil.auth.azurekeyvault.client;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import io.smallrye.mutiny.Uni;
import it.pagopa.swclient.mil.auth.azurekeyvault.bean.GetAccessTokenResponse;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

/**
 * 
 * @author Antonio Tarricone
 */
@RegisterRestClient(configKey = "azure-auth-api")
public interface AzureAuthClient {
	/**
	 * 
	 * @param tenantId
	 * @param grantType
	 * @param clientId
	 * @param clientSecret
	 * @param scope
	 * @return
	 */
	@Path("/{tenantId}/oauth2/v2.0/token")
	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	Uni<GetAccessTokenResponse> getAccessToken(
		@PathParam("tenantId") String tenantId,
		@FormParam("grant_type") String grantType,
		@FormParam("client_id") String clientId,
		@FormParam("client_secret") String clientSecret,
		@FormParam("scope") String scope);
}
