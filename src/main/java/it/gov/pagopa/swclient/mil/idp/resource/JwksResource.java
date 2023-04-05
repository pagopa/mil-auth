/*
 * JwksResource.java
 *
 * 21 mar 2023
 */
package it.gov.pagopa.swclient.mil.idp.resource;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
import it.gov.pagopa.swclient.mil.bean.Errors;
import it.gov.pagopa.swclient.mil.idp.ErrorCode;
import it.gov.pagopa.swclient.mil.idp.bean.PublicKeys;
import it.gov.pagopa.swclient.mil.idp.service.KeyRetriever;

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
	KeyRetriever keyRetriever;

	/**
	 * 
	 * @param t
	 * @return
	 */
	private InternalServerErrorException errorOnRetrievingKeys(Throwable t) {
		Log.errorf(t, "[%s] Error while retrieving keys.%n", ErrorCode.ERROR_WHILE_RETRIEVING_KEYS);
		return new InternalServerErrorException(Response
			.status(Status.INTERNAL_SERVER_ERROR)
			.entity(new Errors(List.of(ErrorCode.ERROR_WHILE_RETRIEVING_KEYS)))
			.build());
	}

	/**
	 * 
	 * @return
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Uni<PublicKeys> get() {
		Log.debugf("get - Input parameters: n/a");
		return keyRetriever.getPublicKeys() // Retrieve keys.
			.onFailure().transform(this::errorOnRetrievingKeys).log(); // Error while retrieving keys.
	}
}