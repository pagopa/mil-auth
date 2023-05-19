/*
 * RefreshTokensService.java
 *
 * 17 mag 2023
 */
package it.pagopa.swclient.mil.auth.service;

import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
import it.pagopa.swclient.mil.auth.bean.AccessToken;
import it.pagopa.swclient.mil.auth.bean.GetAccessToken;
import it.pagopa.swclient.mil.auth.qualifier.RefreshToken;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 * 
 * @author Antonio Tarricone
 */
@ApplicationScoped
@RefreshToken
public class RefreshTokensService extends TokenService {
	/*
	 * 
	 */
	@Inject
	RefreshTokenVerifier refreshTokenVerifier;

	/**
	 * 
	 * @param getAccessToken
	 * @return
	 */
	public Uni<AccessToken> process(GetAccessToken getAccessToken) {
		Log.debug("Tokens refreshing.");
		return refreshTokenVerifier.verify(getAccessToken.getRefreshToken())
			.chain(() -> {
				return super.process(getAccessToken);
			});
	}
}