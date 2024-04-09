/*
 * DefaultAccessTokenGenerator.java
 *
 * 9 apr 2024
 */
package it.pagopa.swclient.mil.auth.service;

import java.util.Date;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
import it.pagopa.swclient.mil.auth.bean.AccessTokenRequest;
import it.pagopa.swclient.mil.auth.bean.ClaimName;
import it.pagopa.swclient.mil.auth.bean.Client;
import it.pagopa.swclient.mil.auth.bean.Roles;
import it.pagopa.swclient.mil.auth.qualifier.channel.MilService;
import it.pagopa.swclient.mil.auth.qualifier.channel.Server;
import it.pagopa.swclient.mil.auth.service.crypto.TokenSigner;
import jakarta.inject.Inject;

/**
 * @see it.pagopa.swclient.mil.auth.service.AccessTokenGenerator
 * @author Antonio Tarricone
 */
@MilService
@Server
public class DefaultAccessTokenGenerator extends AccessTokenGenerator {
	/*
	 * 
	 */
	private TokenSigner tokenSigner;

	/*
	 * Access token duration.
	 */
	private long duration;

	/*
	 * mil-auth base URL.
	 */
	private String baseUrl;

	/*
	 * Token audience.
	 */
	private String audience;

	/**
	 * Constructor.
	 * 
	 * @param duration
	 * @param baseUrl
	 * @param audience
	 * @param tokenSigner
	 */
	@Inject
	DefaultAccessTokenGenerator(
		@ConfigProperty(name = "client_credentials_grant.access_token.duration") long duration,
		@ConfigProperty(name = "base-url") String baseUrl,
		@ConfigProperty(name = "audience") String audience,
		TokenSigner tokenSigner) {
		super(duration, baseUrl, audience, tokenSigner);
	}

	/**
	 * @see it.pagopa.swclient.mil.auth.service.AccessTokenGenerator#generate(Client, Roles,
	 *      AccessTokenRequest)
	 */
	@Override
	public Uni<String> generate(Client client, Roles roles, AccessTokenRequest accessTokenRequest) {
		return signPayload(prepareJwtClaimSetBuilder(client, roles)
			.subject(client.getSub())
			.build());
	}
}