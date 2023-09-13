/*
 * PoyntClient.java
 *
 * 6 apr 2023
 */
package it.pagopa.swclient.mil.auth.client;

import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.annotation.ClientHeaderParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import java.util.UUID;

/**
 * @author Antonio Tarricone
 */
@RegisterRestClient(configKey = "poynt-api")
public interface PoyntClient {
    /**
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
