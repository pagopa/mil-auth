/*
 * AzureAuthClient.java
 *
 * 23 lug 2023
 */
package it.pagopa.swclient.mil.auth.azurekeyvault.client;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import io.quarkus.rest.client.reactive.ClientQueryParam;
import io.smallrye.mutiny.Uni;
import it.pagopa.swclient.mil.auth.azurekeyvault.bean.GetAccessTokenResponse;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

/**
 * @author Antonio Tarricone
 */
@RegisterRestClient(configKey = "azure-auth-api")
public interface AzureAuthClient {
	/**
	 * @param identity
	 * @param scope
	 * @return
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@ClientQueryParam(name = "api-version", value = "${azure-auth-api.version}")
	Uni<GetAccessTokenResponse> getAccessToken(
		@HeaderParam("x-identity-header") String identity,
		@QueryParam("resource") String scope);
}
