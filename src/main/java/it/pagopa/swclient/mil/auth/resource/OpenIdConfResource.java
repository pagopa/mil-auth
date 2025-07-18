/*
 * OpenIdConfResource.java
 *
 * 14 nov 2023
 */
package it.pagopa.swclient.mil.auth.resource;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import io.smallrye.mutiny.Uni;
import it.pagopa.swclient.mil.auth.bean.OpenIdConf;
import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

/**
 * @author Antonio Tarricone
 */
@Path("/.well-known/openid-configuration")
@PermitAll
public class OpenIdConfResource {
	/*
	 * 
	 */
	static final String[] SIGN_SUPPORTED_ALGS = {
		"RS256"
	};

	/*
	 * mil-auth base URL.
	 */
	@ConfigProperty(name = "base-url", defaultValue = "")
	String baseUrl;

	/**
	 * 
	 * @return
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Uni<Response> get() {
		String issuer = baseUrl.replaceAll("\\/$", "") + "/";
		OpenIdConf conf = new OpenIdConf(issuer, issuer + "token", issuer + ".well-known/jwks.json", SIGN_SUPPORTED_ALGS, SIGN_SUPPORTED_ALGS, SIGN_SUPPORTED_ALGS);
		return Uni.createFrom().item(Response
			.status(Status.OK)
			.entity(conf)
			.build());
	}
}