/*
 * DeviceCodeResource.java
 * 
 * 02 apr 2024
 */
package it.pagopa.swclient.mil.auth.resource;

import io.smallrye.mutiny.Uni;
import it.pagopa.swclient.mil.auth.bean.DeviceCodeRequest;
import it.pagopa.swclient.mil.auth.bean.DeviceCodeResponse;
import jakarta.validation.Valid;
import jakarta.ws.rs.BeanParam;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

/**
 * 
 * @author Antonio Tarricone
 */
@Path("/device/code")
public class DeviceCodeResource {
	/**
	 * Returns user code and device code for device authentication flow.
	 * 
	 * @param req
	 * @return
	 */
	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Uni<DeviceCodeResponse> getDeviceCode(@Valid @BeanParam DeviceCodeRequest req) {
		
	}
}