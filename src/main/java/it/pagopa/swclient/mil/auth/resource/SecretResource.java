/*
 * SecretResource.java
 *
 * 29 jun 2023
 */
package it.pagopa.swclient.mil.auth.resource;

import java.util.UUID;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import com.azure.identity.DefaultAzureCredentialBuilder;
import com.azure.security.keyvault.secrets.SecretClient;
import com.azure.security.keyvault.secrets.SecretClientBuilder;
import com.azure.security.keyvault.secrets.models.KeyVaultSecret;

import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
import it.pagopa.swclient.mil.auth.service.AzureKeyVault;
import jakarta.inject.Inject;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

/**
 * 
 * @author Antonio Tarricone
 */
@Path("/secrets")
public class SecretResource {
	/*
	 * 
	 */
	@ConfigProperty(name = "keyvault.uri")
	String keyVaultUri;

	/*
	 * 
	 */
	@ConfigProperty(name = "keyvault.secret.name", defaultValue = "quarkus-azure-test")
	String secretName;
	
	@Inject
	AzureKeyVault secretClientBuilderService;
	
	/**
	 * 
	 * @return
	 */
	@POST
	public Uni<Response> generateSecret() {
		Log.debug("Generate secret invoked.");

		SecretClient secretClient = secretClientBuilderService.build();

		Log.debug("Generating secret.");
		String secretValue = UUID.randomUUID().toString();
		Log.debugf("The secret is: [%s]", secretValue);

		Log.debug("Storing secret.");
		secretClient.setSecret(secretName, secretValue);

		Log.debug("Secret stored.");
		return Uni.createFrom().item(Response.created(null).build());
	}

	/**
	 * 
	 * @return
	 */
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public Uni<Response> retrieveSecret() {
		Log.debug("Retrieve secret invoked.");

		SecretClient secretClient = secretClientBuilderService.build();
		
		Log.debug("Retrieving secret.");
		KeyVaultSecret secret = secretClient.getSecret(secretName);
		if (secret != null) {
			String secretValue = secret.getValue();
			Log.debugf("The secret is: [%s]", secretValue);
			return Uni.createFrom().item(Response.ok(secretValue).build());
		} else {
			Log.warn("Secret not found.");
			return Uni.createFrom().item(Response.status(Status.NOT_FOUND).build());
		}
	}
	
	/**
	 * 
	 * @return
	 */
	@DELETE
	public Uni<Response> deleteSecret() {
		Log.debug("Delete secret invoked.");
		
		SecretClient secretClient = secretClientBuilderService.build();
		
		Log.debug("Begin deleting secret.");
		secretClient.beginDeleteSecret(secretName);
		Log.debug("Secret deletion started.");
		
		return Uni.createFrom().item(Response.accepted().build());
	}
}