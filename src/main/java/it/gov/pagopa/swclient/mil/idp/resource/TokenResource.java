/*
 * TokenResource.java
 *
 * 16 mar 2023
 */
package it.gov.pagopa.swclient.mil.idp.resource;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

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
import org.eclipse.microprofile.config.inject.ConfigProperty;
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



    /*
     *
     */
    @Inject
    ClientRepository clientRepository;

    /*
     *
     */
    @Inject
    ResourceOwnerCredentialsRepository resourceOwnerCredentialsRepository;


    /*
     *
     */
    @Inject
    KeyRetriever keyRetriever;

    @RestClient
    PoyntClient poyntClient;


    @Inject
    TokenStringGenerator tokenStringGenerator;

    @Inject
    RefreshTokenStringGenerator refreshTokenStringGenerator;

    @Inject
    GrantsManager grantsManager;

    @Inject
    TokenManager tokenManager;


    private Uni<AccessToken> commonProcessing(CommonHeader commonHeader, GetAccessToken getAccessToken) {
        String clientId = getAccessToken.getClientId();
        String acquirerId = commonHeader.getAcquirerId();
        String channel = commonHeader.getChannel();
        String merchantId = commonHeader.getMerchantId();
        String terminalId = commonHeader.getTerminalId();

        Log.debug("Find client id.");
        return clientRepository.findByIdOptional(clientId).onFailure() // Error while finding client id.
                .transform(t -> {
                    Log.errorf(t, "[%s] Error while finding client id.", ErrorCode.ERROR_WHILE_FINDING_CLIENT_ID);
                    return new InternalServerErrorException(Response.status(Status.INTERNAL_SERVER_ERROR)
                            .entity(new Errors(List.of(ErrorCode.ERROR_WHILE_FINDING_CLIENT_ID))).build());
                }).onItem().transform(o -> o.orElseThrow(() -> {
                    /*
                     * If the 'optional' item is present return it, otherwise (this is done by this
                     * block) throw NotAuthorizedException.
                     */
                    Log.warnf("[%s] Client id not found: %s", ErrorCode.CLIENT_ID_NOT_FOUND, clientId);
                    return new NotAuthorizedException(Response.status(Status.UNAUTHORIZED)
                            .entity(new Errors(List.of(ErrorCode.CLIENT_ID_NOT_FOUND))).build());
                })).onItem().invoke(c -> {
                    /*
                     * Verify channel consistency.
                     */
                    grantsManager.verifyChannel(c, channel);
                }).chain(() -> {
                    /*
                     * Find grants.
                     */
                    return grantsManager.processGrants(acquirerId, channel, merchantId, clientId, terminalId);
                }).onItem().transform(o -> o.orElseThrow(() -> {
                    Log.warnf("[%s] Grants not found.", ErrorCode.GRANTS_NOT_FOUND);
                    return new NotAuthorizedException(Response.status(Status.UNAUTHORIZED)
                            .entity(new Errors(List.of(ErrorCode.GRANTS_NOT_FOUND))).build());
                })).onFailure(
                        /*
                         * If an error occurs during retriving grants.
                         */
                        t -> !(t instanceof NotAuthorizedException) && !(t instanceof InternalServerErrorException))
                .transform(t -> {
                    Log.errorf(t, "[%s] Error while finding grants.", ErrorCode.ERROR_WHILE_FINDING_GRANTS);
                    return new InternalServerErrorException(Response.status(Status.INTERNAL_SERVER_ERROR)
                            .entity(new Errors(List.of(ErrorCode.ERROR_WHILE_FINDING_GRANTS))).build());
                }).chain((grantEntity) -> {
                    /*
                     * Generate tokens.
                     */
                    return tokenManager.generateAccessToken(commonHeader, getAccessToken, grantEntity);
                }).onFailure(
                        /*
                         * Error during token signing.
                         */
                        t -> !(t instanceof NotAuthorizedException) && !(t instanceof InternalServerErrorException))
                .transform(t -> {
                    /*
                     * Err
                     */
                    return new InternalServerErrorException(Response.status(Status.INTERNAL_SERVER_ERROR)
                            .entity(new Errors(List.of(ErrorCode.ERROR_WHILE_SIGNING_TOKENS))).build());

                });
    }


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
            return createToken(commonHeader, getAccessToken);
        } else if (getAccessToken.isPoyntTokenGrantType()) {
            return createTokenByPoyntToken(commonHeader, getAccessToken);
        } else {
            return refreshToken(commonHeader, getAccessToken);
        }
    }

    /**
     * Create access and refresh tokens by means of username/password.
     *
     * @param commonHeader
     * @param getAccessToken
     * @return
     */
    private Uni<AccessToken> createToken(CommonHeader commonHeader, GetAccessToken getAccessToken) {
        Log.debugf("createToken - Input parameters: %s, %s", commonHeader, getAccessToken);

        String channel = commonHeader.getChannel();
        String username = getAccessToken.getUsername();
        String password = getAccessToken.getPassword();
        String merchantId = commonHeader.getMerchantId();
        String acquirerId = commonHeader.getAcquirerId();

        /*
         * Retrieve credentials.
         */
        Log.debug("Find credentials.");
        return resourceOwnerCredentialsRepository.findByIdOptional(username).onFailure(
                        /*
                         * It the failure is not for previous errors, then it must be for an error
                         * during credentials retrieving.
                         */
                        t -> !(t instanceof NotAuthorizedException) && !(t instanceof InternalServerErrorException))
                .transform(t -> {
                    Log.errorf(t, "[%s] Error while finding credentials.", ErrorCode.ERROR_WHILE_FINDING_CREDENTIALS);
                    return new InternalServerErrorException(Response.status(Status.INTERNAL_SERVER_ERROR)
                            .entity(new Errors(List.of(ErrorCode.ERROR_WHILE_FINDING_CREDENTIALS))).build());
                }).onItem().transform(credentials -> credentials.orElseThrow(() -> {
                    /*
                     * If the 'optional' item is present return it, otherwise (this is done by this
                     * block) throw NotAuthorizedException.
                     */
                    Log.warnf("[%s] Credentials not found.", ErrorCode.WRONG_CREDENTIALS);
                    return new NotAuthorizedException(Response.status(Status.UNAUTHORIZED)
                            .entity(new Errors(List.of(ErrorCode.WRONG_CREDENTIALS))).build());
                })).onItem().invoke(credentials -> {
                    /*
                     * Verify credentials.
                     */
                    grantsManager.verifyCredentials(credentials, acquirerId, channel, merchantId, password);
                }).chain(() -> {
                    return commonProcessing(commonHeader, getAccessToken);
                });
    }

    /**
     * Create access and refresh tokens by means of username/password.
     *
     * @param commonHeader
     * @param getAccessToken
     * @return
     */
    private Uni<AccessToken> createTokenByPoyntToken(CommonHeader commonHeader, GetAccessToken getAccessToken) {
        Log.debugf("createTokenByPoyntToken - Input parameters: %s, %s", commonHeader, getAccessToken);

        String channel = commonHeader.getChannel();
        String extToken = getAccessToken.getExtToken();
        String addData = getAccessToken.getAddData();
        String merchantId = commonHeader.getMerchantId();
        String acquirerId = commonHeader.getAcquirerId();

        /*
         * Verify Poynt token.
         */
        Log.debug("Verify Poynt token.");
        return poyntClient.getBusinessObject("Bearer " + extToken, addData)
                .onFailure(
                        t -> !(t instanceof NotAuthorizedException) && !(t instanceof InternalServerErrorException))
                .transform(t -> {
                    Log.errorf(t, "[%s] Error while vaidating Poynt Token.", ErrorCode.ERROR_WHILE_VALIDATING_EXT_TOKEN);
                    return new InternalServerErrorException(
                            Response.status(Status.INTERNAL_SERVER_ERROR)
                                    .entity(new Errors(List.of(
                                            ErrorCode.ERROR_WHILE_VALIDATING_EXT_TOKEN)))
                                    .build());
                })
                .onItem()
                .invoke(businessObject ->
                        {
                            if (businessObject.getStatus() != 200) {
                                throw new NotAuthorizedException(Response.status(Status.UNAUTHORIZED)
                                        .entity(new Errors(List.of(
                                                ErrorCode.EXT_TOKEN_NOT_VALID)))
                                        .build());
                            }

                        }
                )
                .onFailure(
                        t -> !(t instanceof NotAuthorizedException) && !(t instanceof InternalServerErrorException))
                .transform(t -> {
                            Log.errorf(t, "[%s] Error while vaidating Poynt Token.", ErrorCode.ERROR_WHILE_VALIDATING_EXT_TOKEN);
                            return new InternalServerErrorException(
                                    Response.status(Status.INTERNAL_SERVER_ERROR)
                                            .entity(new Errors(List.of(
                                                    ErrorCode.ERROR_WHILE_VALIDATING_EXT_TOKEN)))
                                            .build());
                        }
                )
                .chain(() -> {
                    return commonProcessing(commonHeader, getAccessToken);
                });
    }


    /**
     * @param commonHeader
     * @param refreshAccessToken
     * @return
     */
    private Uni<AccessToken> refreshToken(CommonHeader commonHeader, GetAccessToken refreshAccessToken) {
        Log.debugf("refreshToken - Input parameters: %s, %s", commonHeader, refreshAccessToken);

        String refreshTokenStr = refreshAccessToken.getRefreshToken();
        /*
         * Retrieve credentials.
         */
        Log.debug("Find credentials.");
        return tokenManager.verifyRefreshToken(refreshTokenStr).chain(() -> {
            return commonProcessing(commonHeader, refreshAccessToken);
        });
    }
}