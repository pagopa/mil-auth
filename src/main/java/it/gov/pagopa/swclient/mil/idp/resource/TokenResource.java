/*
 * TokenResource.java
 *
 * 16 mar 2023
 */
package it.gov.pagopa.swclient.mil.idp.resource;

import javax.validation.Valid;
import javax.ws.rs.BeanParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
import it.gov.pagopa.swclient.mil.bean.CommonHeader;
import it.gov.pagopa.swclient.mil.idp.bean.AccessToken;
import it.gov.pagopa.swclient.mil.idp.bean.GetAccessTokenByPassword;
import it.gov.pagopa.swclient.mil.idp.bean.RefreshAccessToken;

/**
 * 
 * @author Antonio Tarricone
 */
public class TokenResource {
	/**
	 * 
	 * @param commonHeader
	 * @param getAccessToken
	 * @return
	 */
	@Path("/pwd/token")
	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Uni<AccessToken> createToken(@Valid @BeanParam CommonHeader commonHeader, @Valid @BeanParam GetAccessTokenByPassword getAccessToken) {
		Log.debugf("createToken - Input parameters: %s, %s", commonHeader, getAccessToken);
		return null;
	}

	/**
	 * 
	 * @param commonHeader
	 * @param refreshAccessToken
	 * @return
	 */
	@Path("/pwd/token")
	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Uni<AccessToken> createToken(@Valid @BeanParam CommonHeader commonHeader, @Valid @BeanParam RefreshAccessToken refreshAccessToken) {
		Log.debugf("createToken - Input parameters: %s, %s", commonHeader, refreshAccessToken);
		return null;
	}
}