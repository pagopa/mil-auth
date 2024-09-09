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
import jakarta.inject.Inject;

/**
 * @author Antonio Tarricone
 */
@ApplicationScoped
@ClientCredentials
public class TokenByClientSecretService extends TokenService {
	/**
	 * 
	 */
	TokenByClientSecretService() {
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
	TokenByClientSecretService(ClientVerifier clientVerifier, RolesFinder roleFinder, TokenSigner tokenSigner, ClaimEncryptor claimEncryptor) {
		super(clientVerifier, roleFinder, tokenSigner, claimEncryptor);
	}

	/**
	 * @param getAccessToken
	 * @return
	 */
	@Override
	public Uni<GetAccessTokenResponse> process(GetAccessTokenRequest getAccessToken) {
		Log.tracef("Generation of the token by client secret");
		return super.process(getAccessToken);
	}
}