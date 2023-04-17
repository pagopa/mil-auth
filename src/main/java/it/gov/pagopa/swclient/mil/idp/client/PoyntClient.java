/*
 * PoyntClient.java
 *
 * 6 apr 2023
 */
package it.gov.pagopa.swclient.mil.idp.client;

import java.util.UUID;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.rest.client.annotation.ClientHeaderParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import io.smallrye.mutiny.Uni;

/**
 * 
 * @author Antonio Tarricone
 */
@RegisterRestClient(configKey = "poynt-api")
public interface PoyntClient {
	/**
	 * 
	 * @param poyntToken
	 * @param businessId
	 * @return
	 */
	@Path("/businesses/{businessId}")
	@GET
	@ClientHeaderParam(name = "Api-Version", value = "${poynt-api.version}")
	@ClientHeaderParam(name = "POYNT-REQUEST-ID", value = "{withParam}")
	Uni<Response> getBusinessObject(
		@HeaderParam("Authorization") String poyntToken,
		@PathParam("businessId") String businessId);

	/**
	 * 
	 * @param name
	 * @return
	 */
	default String withParam(String name) {
		if ("POYNT-REQUEST-ID".equals(name)) {
			return UUID.randomUUID().toString();
		}
		throw new IllegalArgumentException();
	}
}
