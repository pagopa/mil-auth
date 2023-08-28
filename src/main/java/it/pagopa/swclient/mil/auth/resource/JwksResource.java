/*
 * JwksResource.java
 *
 * 21 mar 2023
 */
package it.pagopa.swclient.mil.auth.resource;

import static it.pagopa.swclient.mil.auth.ErrorCode.ERROR_SEARCHING_FOR_KEYS;

import java.time.Instant;
import java.util.List;
import java.util.OptionalLong;

import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
import it.pagopa.swclient.mil.auth.service.KeyFinder;
import it.pagopa.swclient.mil.bean.Errors;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.InternalServerErrorException;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.CacheControl;
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
	private static final long SKEW = 5 * 60 * 1000L;

	/*
	 * 
	 */
	@Inject
	KeyFinder keyFinder;

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
	public Uni<Response> get() {
		Log.debug("get - Input parameters: n/a");
		return keyFinder.findPublicKeys() // Retrieve keys.
			.invoke(l -> Log.debugf("get - Output parameters: [%s]", l.toString()))
			.map(l -> {
				/*
				 * Search the key that expires first to set Cache-Control/max-age
				 */
				OptionalLong minExp = l.getKeys().stream()
					.map(k -> k.getExp())
					.mapToLong(e -> e)
					.min();

				long maxAge = 0;
				if (minExp.isPresent()) {
					/*
					 * To be sure that will not be cached keys that will expire in a while, subtract SKEW.
					 */
					maxAge = (minExp.getAsLong() - SKEW - Instant.now().toEpochMilli()) / 1000; // seconds
				}

				CacheControl cacheControl = new CacheControl();
				if (maxAge > 0) {
					cacheControl.setMaxAge((int) maxAge);
				} else {
					cacheControl.setNoCache(true);
				}

				return Response
					.status(Status.OK)
					.cacheControl(cacheControl)
					.entity(l)
					.build();
			})
			.onFailure().transform(this::errorOnRetrievingKeys); // Error while retrieving keys.
	}
}