/*
 * PersonalDataVaultClient.java
 *
 * 8 apr 2024
 */
package it.pagopa.swclient.mil.auth.client;

import org.eclipse.microprofile.rest.client.annotation.ClientHeaderParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import io.smallrye.mutiny.Uni;
import it.pagopa.swclient.mil.auth.bean.TokenizationRequest;
import it.pagopa.swclient.mil.auth.bean.TokenizationResponse;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;

/**
 * 
 * @author Antonio Tarricone
 */
@RegisterRestClient(configKey = "personal-data-vault")
@Path("/tokens")
public interface PersonalDataVaultClient {
	@PUT
	@ClientHeaderParam(name = "x-api-key", value = "${personal-data-vault.api-key}")
	Uni<TokenizationResponse> tokenize(TokenizationRequest tokenizationRequest);
}
