/*
 * AzureAuthDataRepositoryClient.java
 *
 * 30 mag 2023
 */
package it.pagopa.swclient.mil.auth.azure.storage.client;

import org.eclipse.microprofile.rest.client.annotation.ClientHeaderParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import io.smallrye.mutiny.Uni;
import it.pagopa.swclient.mil.auth.bean.Client;
import it.pagopa.swclient.mil.auth.bean.Role;
import it.pagopa.swclient.mil.auth.bean.User;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;

/**
 * @author Antonio Tarricone
 */
@RegisterRestClient(configKey = "auth-data-repository")
public interface AzureAuthDataRepositoryClient {
	/**
	 * @param authorization
	 * @param clientId
	 * @return
	 */
	@Path("clients/{clientId}.json")
	@GET
	@ClientHeaderParam(name = "x-ms-version", value = "${azure-storage-api.version}")
	Uni<Client> getClient(@HeaderParam("Authorization") String authorization, @PathParam("clientId") String clientId);

	/**
	 * @param authorization
	 * @param acquirerId
	 * @param channel
	 * @param merchantId
	 * @param clientId
	 * @param terminalId
	 * @return
	 */
	@Path("roles/{acquirerId}/{channel}/{clientId}/{merchantId}/{terminalId}/roles.json")
	@GET
	@ClientHeaderParam(name = "x-ms-version", value = "${azure-storage-api.version}")
	Uni<Role> getRoles(
		@HeaderParam("Authorization") String authorization,
		@PathParam("acquirerId") String acquirerId,
		@PathParam("channel") String channel,
		@PathParam("clientId") String clientId,
		@PathParam("merchantId") String merchantId,
		@PathParam("terminalId") String terminalId);

	/**
	 * @param authorization
	 * @param userHash
	 * @return
	 */
	@Path("users/{userHash}.json")
	@GET
	@ClientHeaderParam(name = "x-ms-version", value = "${azure-storage-api.version}")
	Uni<User> getUser(@HeaderParam("Authorization") String authorization, @PathParam("userHash") String userHash);
}