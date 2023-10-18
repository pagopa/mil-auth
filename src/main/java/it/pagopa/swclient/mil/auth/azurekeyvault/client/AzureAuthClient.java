/*
 * AzureAuthClient.java
 *
 * 23 lug 2023
 */
package it.pagopa.swclient.mil.auth.azurekeyvault.client;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import io.smallrye.mutiny.Uni;
import it.pagopa.swclient.mil.auth.azurekeyvault.bean.GetAccessTokenResponse;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

/**
 * @author Antonio Tarricone
 */
@RegisterRestClient(configKey = "azure-auth-api")
public interface AzureAuthClient {
	/**
	 * @param tenantId
	 * @param grantType
	 * @param clientId
	 * @param clientSecret
	 * @param scope
	 * @return
	 */
	@Path("?resource={scope}&api-version=2019-08-01")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	Uni<GetAccessTokenResponse> getAccessToken(
		@HeaderParam("x-identity-header") String identity,
		@PathParam("scope") String scope);
}
