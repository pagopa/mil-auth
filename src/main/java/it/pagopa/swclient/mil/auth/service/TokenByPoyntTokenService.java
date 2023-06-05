/*
 * TokenByPoyntTokenService.java
 *
 * 17 mag 2023
 */
package it.pagopa.swclient.mil.auth.service;

import static it.pagopa.swclient.mil.auth.ErrorCode.ERROR_VALIDATING_EXT_TOKEN;
import static it.pagopa.swclient.mil.auth.ErrorCode.EXT_TOKEN_NOT_VALID;
import static it.pagopa.swclient.mil.auth.util.UniGenerator.exception;
import static it.pagopa.swclient.mil.auth.util.UniGenerator.voidItem;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
import it.pagopa.swclient.mil.auth.bean.AccessToken;
import it.pagopa.swclient.mil.auth.bean.GetAccessToken;
import it.pagopa.swclient.mil.auth.client.PoyntClient;
import it.pagopa.swclient.mil.auth.qualifier.PoyntToken;
import it.pagopa.swclient.mil.auth.util.AuthError;
import it.pagopa.swclient.mil.auth.util.AuthException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

/**
 * 
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
	 * This method verifies Poynt token.
	 *
	 * @param getAccessToken
	 * @return
	 */
	public Uni<Void> verifyPoyntToken(GetAccessToken getAccessToken) {
		Log.debug("Poynt token verification.");
		return poyntClient.getBusinessObject("Bearer " + getAccessToken.getExtToken(), getAccessToken.getAddData())
			.onFailure().transform(t -> {
				if (t instanceof WebApplicationException) {
					WebApplicationException e = (WebApplicationException) t;
					Response r = e.getResponse();
					// r cannot be null
					String message = null;
					// if (r != null) {
					message = String.format("[%s] Poynt Token not valid. Status: %s", EXT_TOKEN_NOT_VALID, r.getStatus());
					// } else {
					// message = String.format("[%s] Poynt Token not valid.", EXT_TOKEN_NOT_VALID);
					// }
					Log.warnf(e, message);
					return new AuthException(EXT_TOKEN_NOT_VALID, message);
				} else {
					String message = String.format("[%s] Error validating Poynt token.", ERROR_VALIDATING_EXT_TOKEN);
					Log.errorf(t, message);
					return new AuthError(ERROR_VALIDATING_EXT_TOKEN, message);
				}
			})
			.chain(r -> {
				if (r.getStatus() != 200) {
					String message = String.format("[%s] Poynt Token not valid. Status: %s", EXT_TOKEN_NOT_VALID, r.getStatus());
					Log.warn(message);
					return exception(EXT_TOKEN_NOT_VALID, message);
				} else {
					Log.debug("Poynt token has been successfully verified.");
					return voidItem();
				}
			});
	}

	/**
	 * 
	 * @param getAccessToken
	 * @return
	 */
	public Uni<AccessToken> process(GetAccessToken getAccessToken) {
		Log.debugf("Generation of the token/s by Poynt token.");
		return verifyPoyntToken(getAccessToken)
			.chain(() -> {
				return super.process(getAccessToken);
			});
	}
}