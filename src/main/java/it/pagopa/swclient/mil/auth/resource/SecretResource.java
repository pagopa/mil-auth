/*
 * SecretResource.java
 *
 * 29 jun 2023
 */
package it.pagopa.swclient.mil.auth.resource;

import java.net.URI;
import java.net.URISyntaxException;

import com.nimbusds.jose.JOSEException;

import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
import it.pagopa.swclient.mil.auth.bean.KeyPair;
import it.pagopa.swclient.mil.auth.service.AzureKeyVault;
import it.pagopa.swclient.mil.auth.service.KeyPairGenerator;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * 
 * @author Antonio Tarricone
 */
@Path("/keys")
public class SecretResource {
	/*
	 * 
	 */
	@Inject
	AzureKeyVault keyVault;

	@Inject
	KeyPairGenerator keyPairGenerator;

	/**
	 * 
	 * @return
	 */
	@POST
	public Uni<Response> generateKeyPair() {
		try {
			Log.debug("Generating key pair.");
			KeyPair keyPair = keyPairGenerator.generate();

			URI uri = new URI("/keys/" + keyPair.getKid());

			Log.debug("Storing key pair.");
			return keyVault.setex(keyPair.getKid(), 0, keyPair)
				.onItem()
				.transform(v -> {
					Log.debug("Key pair stored.");
					return Response.created(uri).build();
				});
		} catch (JOSEException | URISyntaxException e) {
			Log.errorf(e, "Error.");
			return Uni.createFrom().failure(e);
		}
	}

	/**
	 * 
	 * @return
	 */
	@GET
	@Path("/{kid}")
	@Produces(MediaType.APPLICATION_JSON)
	public Uni<KeyPair> retrieveKeyPair(String kid) {
		Log.debug("Retrieving secret.");
		return keyVault.get(kid);
	}
}