/*
 * JwksResource.java
 *
 * 21 mar 2023
 */
package it.pagopa.swclient.mil.auth.resource;

import static it.pagopa.swclient.mil.auth.ErrorCode.ERROR_SEARCHING_FOR_KEYS;

import java.util.List;

import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
import it.pagopa.swclient.mil.auth.bean.PublicKeys;
import it.pagopa.swclient.mil.auth.service.KeyFinder;
import it.pagopa.swclient.mil.bean.Errors;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.InternalServerErrorException;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

/**
 * 
 * @author Antonio Tarricone
 */
@Path("/.well-known/jwks.json")
public class JwksResource {
	/*
	 * 
	 */
	@Inject
	KeyFinder keyRetriever;

	/**
	 * 
	 * @param t
	 * @return
	 */
	private InternalServerErrorException errorOnRetrievingKeys(Throwable t) {
		String message = String.format("[%s] Error searching for keys.", ERROR_SEARCHING_FOR_KEYS);
		Log.errorf(t, message);
		return new InternalServerErrorException(Response
			.status(Status.INTERNAL_SERVER_ERROR)
			.entity(new Errors(List.of(ERROR_SEARCHING_FOR_KEYS), List.of(message)))
			.build());
	}

	/**
	 * 
	 * @return
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Uni<PublicKeys> get() {
		Log.debug("get - Input parameters: n/a");
		return keyRetriever.findPublicKeys() // Retrieve keys.
			.invoke(t -> Log.debugf("get - Output parameters: %s", t.toString()))
			.onFailure().transform(this::errorOnRetrievingKeys); // Error while retrieving keys.
	}
}