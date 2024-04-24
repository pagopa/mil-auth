/*
 * TokenByClientSecretService.java
 *
 * 17 mag 2023
 */
package it.pagopa.swclient.mil.auth.service;

import org.eclipse.microprofile.config.inject.ConfigProperty;

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
	 * @param accessDuration
	 * @param refreshDuration
	 * @param baseUrl
	 * @param audience
	 * @param clientVerifier
	 * @param roleFinder
	 * @param tokenSigner
	 */
	@Inject
	TokenByClientSecretService(@ConfigProperty(name = "access.duration") long accessDuration,
		@ConfigProperty(name = "refresh.duration") long refreshDuration,
		@ConfigProperty(name = "base-url", defaultValue = "") String baseUrl,
		@ConfigProperty(name = "token-audience", defaultValue = "mil.pagopa.it") String audience,
		ClientVerifier clientVerifier,
		RolesFinder roleFinder,
		TokenSigner tokenSigner) {
		super(accessDuration, refreshDuration, baseUrl, audience, clientVerifier, roleFinder, tokenSigner);
	}

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