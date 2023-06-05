/*
 * AuthDataRepository.java
 *
 * 30 mag 2023
 */
package it.pagopa.swclient.mil.auth.client;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import io.smallrye.mutiny.Uni;
import it.pagopa.swclient.mil.auth.bean.Client;
import it.pagopa.swclient.mil.auth.bean.Role;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;

/**
 * 
 * @author Antonio Tarricone
 */
@RegisterRestClient(configKey = "auth-data-repository")
public interface AuthDataRepository {
	/**
	 * 
	 * @param clientId
	 * @return
	 */
	@Path("clients/{clientId}.json")
	@GET
	Uni<Client> getClient(@PathParam("clientId") String clientId);

	/**
	 * 
	 * @param acquirerId
	 * @param channel
	 * @param merchantId
	 * @param clientId
	 * @param terminalId
	 * @return
	 */
	@Path("roles/{acquirerId}/{channel}/{clientId}/{merchantId}/{terminalId}/roles.json")
	@GET
	Uni<Role> getRoles(
		@PathParam("acquirerId") String acquirerId,
		@PathParam("channel") String channel,
		@PathParam("clientId") String clientId,
		@PathParam("merchantId") String merchantId,
		@PathParam("terminalId") String terminalId);
}