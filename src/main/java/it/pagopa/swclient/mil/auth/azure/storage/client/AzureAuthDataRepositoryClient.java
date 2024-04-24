/*
 * AzureAuthDataRepositoryClient.java
 *
 * 30 mag 2023
 */
package it.pagopa.swclient.mil.auth.azure.storage.client;

import org.eclipse.microprofile.rest.client.annotation.ClientHeaderParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import io.quarkus.rest.client.reactive.NotBody;
import io.smallrye.mutiny.Uni;
import it.pagopa.swclient.mil.auth.bean.Client;
import it.pagopa.swclient.mil.auth.bean.Role;
import it.pagopa.swclient.mil.auth.bean.User;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;

/**
 * @author Antonio Tarricone
 */
@RegisterRestClient(configKey = "auth-data-repository")
public interface AzureAuthDataRepositoryClient {
	/**
	 * @param accessToken
	 * @param clientId
	 * @return
	 */
	@WithSpan(kind = SpanKind.CLIENT)
	@Path("clients/{clientId}.json")
	@GET
	@ClientHeaderParam(name = "x-ms-version", value = "${azure-storage-api.version}")
	@ClientHeaderParam(name = "Authorization", value = "Bearer {accessToken}")
	Uni<Client> getClient(@NotBody String accessToken, @PathParam("clientId") String clientId);

	/**
	 * @param accessToken
	 * @param acquirerId
	 * @param channel
	 * @param merchantId
	 * @param clientId
	 * @param terminalId
	 * @return
	 */
	@WithSpan(kind = SpanKind.CLIENT)
	@Path("roles/{acquirerId}/{channel}/{clientId}/{merchantId}/{terminalId}/roles.json")
	@GET
	@ClientHeaderParam(name = "x-ms-version", value = "${azure-storage-api.version}")
	@ClientHeaderParam(name = "Authorization", value = "Bearer {accessToken}")
	Uni<Role> getRoles(
		@NotBody String accessToken,
		@PathParam("acquirerId") String acquirerId,
		@PathParam("channel") String channel,
		@PathParam("clientId") String clientId,
		@PathParam("merchantId") String merchantId,
		@PathParam("terminalId") String terminalId);

	/**
	 * @param accessToken
	 * @param userHash
	 * @return
	 */
	@WithSpan(kind = SpanKind.CLIENT)
	@Path("users/{userHash}.json")
	@GET
	@ClientHeaderParam(name = "x-ms-version", value = "${azure-storage-api.version}")
	@ClientHeaderParam(name = "Authorization", value = "Bearer {accessToken}")
	Uni<User> getUser(@NotBody String accessToken, @PathParam("userHash") String userHash);
}