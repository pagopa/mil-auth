/*
 * TokenResource.java
 *
 * 16 mar 2023
 */
package it.gov.pagopa.swclient.mil.idp.resource;


import java.util.List;


import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.BeanParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import it.gov.pagopa.swclient.mil.idp.service.*;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
import it.gov.pagopa.swclient.mil.bean.CommonHeader;
import it.gov.pagopa.swclient.mil.bean.Errors;
import it.gov.pagopa.swclient.mil.idp.ErrorCode;
import it.gov.pagopa.swclient.mil.idp.bean.AccessToken;
import it.gov.pagopa.swclient.mil.idp.bean.GetAccessToken;
import it.gov.pagopa.swclient.mil.idp.client.PoyntClient;
import it.gov.pagopa.swclient.mil.idp.dao.ClientRepository;
import it.gov.pagopa.swclient.mil.idp.dao.ResourceOwnerCredentialsRepository;

/**
 * @author Antonio Tarricone & Anis Lucidi
 */
@Path("/pwd/token")
public class TokenResource {
    /*
     *
     */


    @Inject
    TokenManager tokenManager;




    /**
     * Dispatches the request to the right method.
     *
     * @param commonHeader
     * @param getAccessToken
     * @return
     */
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<AccessToken> createOrRefreshToken(@Valid @BeanParam CommonHeader commonHeader,
                                                 @Valid @BeanParam GetAccessToken getAccessToken) {
        if (getAccessToken.isPasswordGrantType()) {
            return tokenManager.createToken(commonHeader, getAccessToken);
        } else if (getAccessToken.isPoyntTokenGrantType()) {
            return tokenManager.createTokenByPoyntToken(commonHeader, getAccessToken);
        } else {
            return tokenManager.refreshToken(commonHeader, getAccessToken);
        }
    }


}