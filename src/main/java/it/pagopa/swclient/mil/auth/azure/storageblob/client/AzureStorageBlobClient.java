/*
 * AzureStorageBlobClient.java
 *
 * 10 apr 2024
 */
package it.pagopa.swclient.mil.auth.azure.storageblob.client;

import org.eclipse.microprofile.rest.client.annotation.ClientHeaderParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import io.quarkus.rest.client.reactive.NotBody;
import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;

/**
 * 
 * @author Antonio Tarricone
 */
@RegisterRestClient(configKey = "azure-storage-blob")
public interface AzureStorageBlobClient {
	/**
	 * 
	 * @param accessToken
	 * @param path
	 * @return
	 */
	@Path("{path}")
	@GET
	@ClientHeaderParam(name = "Authorization", value = "Bearer {accessToken}")
	@ClientHeaderParam(name = "x-ms-version", value = "${azure-storage-blob.x-ms-version}")
	Uni<Response> getBlob(@NotBody String accessToken, @PathParam("path") String path);
}
