/*
 * AzureAuthClient.java
 *
 * 23 lug 2023
 */
package it.pagopa.swclient.mil.auth.azure.auth.client;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import io.quarkus.rest.client.reactive.ClientQueryParam;
import io.smallrye.mutiny.Uni;
import it.pagopa.swclient.mil.auth.azure.auth.bean.GetAccessTokenResponse;
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
	@WithSpan(kind = SpanKind.CLIENT)
	Uni<GetAccessTokenResponse> getAccessToken(
		@HeaderParam("x-identity-header") String identity,
		@QueryParam("resource") String scope);
}
