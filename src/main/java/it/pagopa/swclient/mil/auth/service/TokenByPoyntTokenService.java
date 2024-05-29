/*
 * TokenByPoyntTokenService.java
 *
 * 17 mag 2023
 */
package it.pagopa.swclient.mil.auth.service;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
import it.pagopa.swclient.mil.auth.AuthErrorCode;
import it.pagopa.swclient.mil.auth.bean.GetAccessTokenRequest;
import it.pagopa.swclient.mil.auth.bean.GetAccessTokenResponse;
import it.pagopa.swclient.mil.auth.bean.TokenType;
import it.pagopa.swclient.mil.auth.client.PoyntClient;
import it.pagopa.swclient.mil.auth.qualifier.PoyntToken;
import it.pagopa.swclient.mil.auth.util.AuthError;
import it.pagopa.swclient.mil.auth.util.AuthException;
import it.pagopa.swclient.mil.auth.util.UniGenerator;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

/**
 * @author Antonio Tarricone
 */
@ApplicationScoped
@PoyntToken
public class TokenByPoyntTokenService extends TokenService {
	/*
	 *
	 */
	@RestClient
	PoyntClient poyntClient;

	/**
	 * 
	 */
	TokenByPoyntTokenService() {
		super();
	}

	/**
	 * 
	 * @param clientVerifier
	 * @param roleFinder
	 * @param tokenSigner
	 * @param claimEncryptor
	 */
	@Inject
	TokenByPoyntTokenService(ClientVerifier clientVerifier, RolesFinder roleFinder, TokenSigner tokenSigner, ClaimEncryptor claimEncryptor) {
		super(clientVerifier, roleFinder, tokenSigner, claimEncryptor);
	}

	/**
	 * This method verifies Poynt token.
	 *
	 * @param getAccessToken
	 * @return
	 */
	public Uni<Void> verifyPoyntToken(GetAccessTokenRequest getAccessToken) {
		Log.debug("Poynt token verification.");
		return poyntClient.getBusinessObject(TokenType.BEARER + " " + getAccessToken.getExtToken(), getAccessToken.getAddData())
			.onFailure().transform(t -> {
				if (t instanceof WebApplicationException e) {
					Response r = e.getResponse();
					// r cannot be null
					String message = String.format("[%s] Poynt Token not valid. Status: [%s]", AuthErrorCode.EXT_TOKEN_NOT_VALID, r.getStatus());
					Log.warnf(e, message);
					return new AuthException(AuthErrorCode.EXT_TOKEN_NOT_VALID, message);
				} else {
					String message = String.format("[%s] Error validating Poynt token.", AuthErrorCode.ERROR_VALIDATING_EXT_TOKEN);
					Log.errorf(t, message);
					return new AuthError(AuthErrorCode.ERROR_VALIDATING_EXT_TOKEN, message);
				}
			})
			.chain(r -> {
				if (r.getStatus() != 200) {
					String message = String.format("[%s] Poynt Token not valid. Status: %s", AuthErrorCode.EXT_TOKEN_NOT_VALID, r.getStatus());
					Log.warn(message);
					return UniGenerator.exception(AuthErrorCode.EXT_TOKEN_NOT_VALID, message);
				} else {
					Log.debug("Poynt token has been successfully verified.");
					return UniGenerator.voidItem();
				}
			});
	}

	/**
	 * @param getAccessToken
	 * @return
	 */
	@Override
	public Uni<GetAccessTokenResponse> process(GetAccessTokenRequest getAccessToken) {
		Log.debugf("Generation of the token/s by Poynt token.");
		return verifyPoyntToken(getAccessToken)
			.chain(() -> super.process(getAccessToken));
	}
}