/*
 * TokenByClientSecretService.java
 *
 * 17 mag 2023
 */
package it.pagopa.swclient.mil.auth.service;

import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
import it.pagopa.swclient.mil.auth.bean.GetAccessTokenRequest;
import it.pagopa.swclient.mil.auth.bean.GetAccessTokenResponse;
import it.pagopa.swclient.mil.auth.qualifier.ClientCredentials;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * @author Antonio Tarricone
 */
@ApplicationScoped
@ClientCredentials
public class TokenByClientSecretService extends TokenService {
	/**
	 * @param getAccessToken
	 * @return
	 */
	@Override
	public Uni<GetAccessTokenResponse> process(GetAccessTokenRequest getAccessToken) {
		Log.debugf("Generation of the token by client secret.");
		return super.process(getAccessToken);
	}
}