/*
 * AzureKeyVaultKeysClient.java
 *
 * 10 apr 2024
 */
package it.pagopa.swclient.mil.auth.azure.keyvaultkeys.client;

import org.eclipse.microprofile.rest.client.annotation.ClientHeaderParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import io.quarkus.rest.client.reactive.ClientQueryParam;
import io.quarkus.rest.client.reactive.NotBody;
import io.smallrye.mutiny.Uni;
import it.pagopa.swclient.mil.auth.azure.keyvaultkeys.bean.KeyCreateParameters;
import it.pagopa.swclient.mil.auth.azure.keyvaultkeys.bean.KeyBundle;
import it.pagopa.swclient.mil.auth.azure.keyvaultkeys.bean.KeyListResult;
import it.pagopa.swclient.mil.auth.azure.keyvaultkeys.bean.KeyOperationResult;
import it.pagopa.swclient.mil.auth.azure.keyvaultkeys.bean.KeySignParameters;
import it.pagopa.swclient.mil.auth.azure.keyvaultkeys.bean.KeyVerifyParameters;
import it.pagopa.swclient.mil.auth.azure.keyvaultkeys.bean.KeyVerifyResult;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

/**
 * 
 * @author Antonio Tarricone
 */
@RegisterRestClient(configKey = "azure-key-vault-keys")
public interface AzureKeyVaultKeysClient {
	/**
	 * Creates a new key, stores it, then returns key parameters and attributes to the client. The
	 * create key operation can be used to create any key type in Azure Key Vault. If the named key
	 * already exists, Azure Key Vault creates a new version of the key. It requires the keys/create
	 * permission.
	 * 
	 * @see <a href=
	 *      "https://learn.microsoft.com/en-us/rest/api/keyvault/keys/create-key/create-key?view=rest-keyvault-keys-7.4&tabs=HTTP">Microsoft
	 *      Azure Documentation</a>
	 * @param accessToken
	 * @param keyName             The name for the new key. The system will generate the version name
	 *                            for the new key. The value you provide may be copied globally for the
	 *                            purpose of running the service. The value provided should not include
	 *                            personally identifiable or sensitive information. Regex pattern:
	 *                            ^[0-9a-zA-Z-]+$
	 * @param keyCreateParameters
	 * @return
	 */
	@Path("/keys/{keyName}/create")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@ClientHeaderParam(name = "Authorization", value = "Bearer {accessToken}")
	@ClientQueryParam(name = "api-version", value = "${azure-key-vault-keys.api-version}")
	Uni<KeyBundle> createKey(
		@NotBody String accessToken,
		@PathParam("keyName") String keyName,
		KeyCreateParameters keyCreateParameters);

	/**
	 * List keys in the specified vault. Retrieves a list of the keys in the Key Vault as JSON Web Key
	 * structures that contain the public part of a stored key. The LIST operation is applicable to all
	 * key types, however only the base key identifier, attributes, and tags are provided in the
	 * response. Individual versions of a key are not listed in the response. This operation requires
	 * the keys/list permission.
	 * 
	 * @see <a href=
	 *      "https://learn.microsoft.com/en-us/rest/api/keyvault/keys/get-keys/get-keys?view=rest-keyvault-keys-7.4&tabs=HTTP">Microsoft
	 *      Azure Documentation</a>
	 * @param accessToken
	 * @return
	 */
	@Path("/keys")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@ClientHeaderParam(name = "Authorization", value = "Bearer {accessToken}")
	@ClientQueryParam(name = "maxresults", value = "${azure-key-vault-keys.get-keys.maxresults}")
	@ClientQueryParam(name = "api-version", value = "${azure-key-vault-keys.api-version}")
	Uni<KeyListResult> getKeys(@NotBody String accessToken);

	/**
	 * Gets the public part of a stored key. The get key operation is applicable to all key types. If
	 * the requested key is symmetric, then no key material is released in the response. This operation
	 * requires the keys/get permission.
	 * 
	 * @see <a href=
	 *      "https://learn.microsoft.com/en-us/rest/api/keyvault/keys/get-key/get-key?view=rest-keyvault-keys-7.4&tabs=HTTP">Microsoft
	 *      Azure Documentation</a>
	 * @param accessToken
	 * @param keyName     The name of the key to get.
	 * @param keyVersion  Adding the version parameter retrieves a specific version of a key. This URI
	 *                    fragment is optional. If not specified, the latest version of the key is
	 *                    returned.
	 * @return
	 */
	@Path("/keys/{keyName}/{keyVersion}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@ClientHeaderParam(name = "Authorization", value = "Bearer {accessToken}")
	@ClientQueryParam(name = "api-version", value = "${azure-key-vault-keys.api-version}")
	Uni<KeyBundle> getKey(
		@NotBody String accessToken,
		@PathParam("keyName") String keyName,
		@PathParam("keyVersion") String keyVersion);

	/**
	 * Retrieves a list of individual key versions with the same key name. The full key identifier,
	 * attributes, and tags are provided in the response. This operation requires the keys/list
	 * permission.
	 * 
	 * @see <a href=
	 *      "https://learn.microsoft.com/en-us/rest/api/keyvault/keys/get-key/get-key?view=rest-keyvault-keys-7.4&tabs=HTTP">Microsoft
	 *      Azure Documentation</a>
	 * @param accessToken
	 * @param keyName     The name of the key.
	 * @return
	 */
	@Path("/keys/{keyName}/versions")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@ClientHeaderParam(name = "Authorization", value = "Bearer {accessToken}")
	@ClientQueryParam(name = "maxresults", value = "${azure-key-vault-keys.get-key-version.maxresults}")
	@ClientQueryParam(name = "api-version", value = "${azure-key-vault-keys.api-version}")
	Uni<KeyListResult> getKeyVersions(
		@NotBody String accessToken,
		@PathParam("keyName") String keyName);

	/**
	 * Creates a signature from a digest using the specified key. The SIGN operation is applicable to
	 * asymmetric and symmetric keys stored in Azure Key Vault since this operation uses the private
	 * portion of the key. This operation requires the keys/sign permission.
	 * 
	 * @see <a href=
	 *      "https://learn.microsoft.com/en-us/rest/api/keyvault/keys/sign/sign?view=rest-keyvault-keys-7.4&tabs=HTTP">Microsoft
	 *      Azure Documentation</a>
	 * @param accessToken
	 * @param keyName           The name of the key.
	 * @param keyVersion        The version of the key.
	 * @param keySignParameters
	 * @return
	 */
	@Path("/keys/{keyName}/{keyVersion}/sign")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@ClientHeaderParam(name = "Authorization", value = "Bearer {accessToken}")
	@ClientQueryParam(name = "api-version", value = "${azure-key-vault-keys.api-version}")
	Uni<KeyOperationResult> sign(
		@NotBody String accessToken,
		@PathParam("keyName") String keyName,
		@PathParam("keyVersion") String keyVersion,
		KeySignParameters keySignParameters);

	/**
	 * Verifies a signature using a specified key. The VERIFY operation is applicable to symmetric keys
	 * stored in Azure Key Vault. VERIFY is not strictly necessary for asymmetric keys stored in Azure
	 * Key Vault since signature verification can be performed using the public portion of the key but
	 * this operation is supported as a convenience for callers that only have a key-reference and not
	 * the public portion of the key. This operation requires the keys/verify permission.
	 * 
	 * @see <a href=
	 *      "https://learn.microsoft.com/en-us/rest/api/keyvault/keys/verify/verify?view=rest-keyvault-keys-7.4&tabs=HTTP">Microsoft
	 *      Azure Documentation</a>
	 * @param authorization
	 * @param keyName                The name of the key.
	 * @param keyVersion             The version of the key.
	 * @param verifySignatureRequest
	 * @return
	 */
	@Path("/keys/{keyName}/{keyVersion}/verify")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@ClientHeaderParam(name = "Authorization", value = "Bearer {accessToken}")
	@ClientQueryParam(name = "api-version", value = "${azure-key-vault-keys.api-version}")
	Uni<KeyVerifyResult> verifySignature(
		@NotBody String accessToken,
		@PathParam("keyName") String keyName,
		@PathParam("keyVersion") String keyVersion,
		KeyVerifyParameters verifySignatureRequest);
}
