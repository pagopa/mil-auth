/*
 * TokenByClientSecretService.java
 *
 * 17 mag 2023
 */
package it.pagopa.swclient.mil.auth.service;

import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
import it.pagopa.swclient.mil.auth.bean.AccessTokenRequest;
import it.pagopa.swclient.mil.auth.bean.AccessTokenResponse;
import it.pagopa.swclient.mil.auth.qualifier.grant.ClientCredentials;
import it.pagopa.swclient.mil.auth.service.crypto.TokenSigner;
import it.pagopa.swclient.mil.auth.service.role.RolesFinder;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 * @author Antonio Tarricone
 */
@ApplicationScoped
@ClientCredentials
public class TokenByClientSecretService extends TokenService {
	/**
	 * Non-private no-args constructor.
	 */
	TokenByClientSecretService() {
		super();
	}

	/**
	 * Constructor.
	 * 
	 * @param clientVerifier
	 * @param roleFinder
	 * @param tokenSigner
	 */
	@Inject
	TokenByClientSecretService(ClientVerifier clientVerifier, RolesFinder roleFinder, TokenSigner tokenSigner) {
		super(clientVerifier, roleFinder, tokenSigner);
	}

	/**
	 * @param getAccessToken
	 * @return
	 */
	@Override
	public Uni<AccessTokenResponse> process(AccessTokenRequest getAccessToken) {
		Log.debugf("Generation of the token by client secret.");
		return super.process(getAccessToken);
	}
}