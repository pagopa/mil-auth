/*
 * AzureIdentityClient.java
 *
 * 23 lug 2023
 */
package it.pagopa.swclient.mil.auth.azure.identity.client;

import org.eclipse.microprofile.rest.client.annotation.ClientHeaderParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import io.quarkus.rest.client.reactive.ClientQueryParam;
import io.smallrye.mutiny.Uni;
import it.pagopa.swclient.mil.auth.azure.identity.bean.AccessToken;
import it.pagopa.swclient.mil.auth.azure.identity.bean.Scope;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

/**
 * 
 * @author Antonio Tarricone
 */
@RegisterRestClient(configKey = "azure-identity")
public interface AzureIdentityClient {
	/**
	 * 
	 * @param scope
	 * @return
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@ClientQueryParam(name = "api-version", value = "${azure-identity.api-version}")
	@ClientHeaderParam(name="x-identity-header", value = "${azure-identity.x-identity-header}")
	Uni<AccessToken> getAccessToken(@QueryParam("resource") Scope scope);
}
