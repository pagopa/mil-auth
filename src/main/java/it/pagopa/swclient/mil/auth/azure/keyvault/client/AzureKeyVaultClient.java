/*
 * AzureKeyVaultClient.java
 *
 * 23 lug 2023
 */
package it.pagopa.swclient.mil.auth.azure.keyvault.client;

import org.eclipse.microprofile.rest.client.annotation.ClientHeaderParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import io.quarkus.rest.client.reactive.ClientQueryParam;
import io.quarkus.rest.client.reactive.NotBody;
import io.smallrye.mutiny.Uni;
import it.pagopa.swclient.mil.auth.azure.keyvault.bean.CreateKeyRequest;
import it.pagopa.swclient.mil.auth.azure.keyvault.bean.DetailedKey;
import it.pagopa.swclient.mil.auth.azure.keyvault.bean.GetKeysResponse;
import it.pagopa.swclient.mil.auth.azure.keyvault.bean.SignRequest;
import it.pagopa.swclient.mil.auth.azure.keyvault.bean.SignResponse;
import it.pagopa.swclient.mil.auth.azure.keyvault.bean.VerifySignatureRequest;
import it.pagopa.swclient.mil.auth.azure.keyvault.bean.VerifySignatureResponse;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

/**
 * @author Antonio Tarricone
 */
@RegisterRestClient(configKey = "azure-key-vault-api")
public interface AzureKeyVaultClient {
	/**
	 * 
	 * @param accessToken
	 * @param keyName
	 * @param createKeyRequest
	 * @return
	 */
	@WithSpan(kind = SpanKind.CLIENT)
	@Path("/keys/{keyName}/create")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@ClientQueryParam(name = "api-version", value = "${azure-key-vault-api.version}")
	@ClientHeaderParam(name = "Authorization", value = "Bearer {accessToken}")
	Uni<DetailedKey> createKey(
		@NotBody String accessToken,
		@PathParam("keyName") String keyName,
		CreateKeyRequest createKeyRequest);

	/**
	 * 
	 * @param accessToken
	 * @return
	 */
	@WithSpan(kind = SpanKind.CLIENT)
	@Path("/keys")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@ClientQueryParam(name = "api-version", value = "${azure-key-vault-api.version}")
	@ClientHeaderParam(name = "Authorization", value = "Bearer {accessToken}")
	Uni<GetKeysResponse> getKeys(
		@NotBody String accessToken);

	/**
	 * 
	 * @param accessToken
	 * @param keyName
	 * @param keyVersion
	 * @return
	 */
	@WithSpan(kind = SpanKind.CLIENT)
	@Path("/keys/{keyName}/{keyVersion}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@ClientQueryParam(name = "api-version", value = "${azure-key-vault-api.version}")
	@ClientHeaderParam(name = "Authorization", value = "Bearer {accessToken}")
	Uni<DetailedKey> getKey(
		@NotBody String accessToken,
		@PathParam("keyName") String keyName,
		@PathParam("keyVersion") String keyVersion);

	/**
	 * 
	 * @param accessToken
	 * @param keyName
	 * @return
	 */
	@WithSpan(kind = SpanKind.CLIENT)
	@Path("/keys/{keyName}/versions")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@ClientQueryParam(name = "api-version", value = "${azure-key-vault-api.version}")
	@ClientHeaderParam(name = "Authorization", value = "Bearer {accessToken}")
	Uni<GetKeysResponse> getKeyVersions(
		@NotBody String accessToken,
		@PathParam("keyName") String keyName);

	/**
	 * 
	 * @param accessToken
	 * @param keyName
	 * @param keyVersion
	 * @param signRequest
	 * @return
	 */
	@WithSpan(kind = SpanKind.CLIENT)
	@Path("/keys/{keyName}/{keyVersion}/sign")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@ClientQueryParam(name = "api-version", value = "${azure-key-vault-api.version}")
	@ClientHeaderParam(name = "Authorization", value = "Bearer {accessToken}")
	Uni<SignResponse> sign(
		@NotBody String accessToken,
		@PathParam("keyName") String keyName,
		@PathParam("keyVersion") String keyVersion,
		SignRequest signRequest);

	/**
	 * 
	 * @param accessToken
	 * @param keyName
	 * @param keyVersion
	 * @param verifySignatureRequest
	 * @return
	 */
	@WithSpan(kind = SpanKind.CLIENT)
	@Path("/keys/{keyName}/{keyVersion}/verify")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@ClientQueryParam(name = "api-version", value = "${azure-key-vault-api.version}")
	@ClientHeaderParam(name = "Authorization", value = "Bearer {accessToken}")
	Uni<VerifySignatureResponse> verifySignature(
		@NotBody String accessToken,
		@PathParam("keyName") String keyName,
		@PathParam("keyVersion") String keyVersion,
		VerifySignatureRequest verifySignatureRequest);
}
