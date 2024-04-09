/*
 * TerminalRegistryClient.java
 * 
 * 02 apr 2024
 */
package it.pagopa.swclient.mil.auth.client;

import org.eclipse.microprofile.rest.client.annotation.ClientHeaderParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import io.quarkus.cache.CacheResult;
import io.quarkus.rest.client.reactive.NotBody;
import io.smallrye.mutiny.Uni;
import it.pagopa.swclient.mil.auth.bean.Terminal;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;

/**
 * 
 * @author Antonio Tarricone
 */
@RegisterRestClient(configKey = "terminal-registry")
@Path("/terminals")
public interface TerminalRegistryClient {
	/**
	 * 
	 * @param accessToken
	 * @param terminalHandlerId
	 * @param terminalId
	 * @return
	 */
	@Path("/findByThIds")
	@GET
	@ClientHeaderParam(name = "Authorization", value = "Bearer {accessToken}")
	@CacheResult(cacheName = "role-cache")
	Uni<Terminal> find(
		@NotBody String accessToken,
		@QueryParam("terminalHandlerId") String terminalHandlerId,
		@QueryParam("terminalId") String terminalId);

	/**
	 * 
	 * @param accessToken
	 * @param terminalUuid
	 * @return
	 */
	@Path("/{terminalUuid}")
	@GET
	@ClientHeaderParam(name = "Authorization", value = "Bearer {accessToken}")
	Uni<Terminal> find(
		@NotBody String accessToken,
		@PathParam("terminalUuid") String terminalUuid);
}