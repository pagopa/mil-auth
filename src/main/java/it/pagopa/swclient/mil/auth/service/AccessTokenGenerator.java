/*
 * AccessTokenGenerator.java
 *
 * 9 apr 2024
 */
package it.pagopa.swclient.mil.auth.service;

import java.util.Date;

import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
import it.pagopa.swclient.mil.auth.bean.AccessTokenRequest;
import it.pagopa.swclient.mil.auth.bean.ClaimName;
import it.pagopa.swclient.mil.auth.bean.Client;
import it.pagopa.swclient.mil.auth.bean.Roles;
import it.pagopa.swclient.mil.auth.service.crypto.TokenSigner;

/**
 * Generates access tokens.
 * 
 * @author Antonio Tarricone
 */
public abstract class AccessTokenGenerator {
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
	
	/*
	 * 
	 */
	private TokenSigner tokenSigner;
	
	/**
	 * Constructor.
	 * 
	 * @param duration
	 * @param baseUrl
	 * @param audience
	 * @param tokenSigner
	 */
	AccessTokenGenerator(
		long duration,
		String baseUrl,
		String audience,
		TokenSigner tokenSigner) {
		this.duration = duration;
		this.baseUrl = baseUrl;
		this.audience = audience;
		this.tokenSigner = tokenSigner;
	}
	
	/**
	 * Generates access token.
	 * 
	 * @param client
	 * @param roles
	 * @param accessTokenRequest
	 * @return
	 */
	public abstract Uni<String> generate(Client client, Roles roles, AccessTokenRequest accessTokenRequest);
	
	/**
	 * 
	 * @param client
	 * @param roles
	 * @return
	 */
	protected JWTClaimsSet.Builder prepareJwtClaimSetBuilder(Client client, Roles roles) {
		Date now = new Date();
		return new JWTClaimsSet.Builder()
			.issueTime(now)
			.issuer(baseUrl)
			.audience(audience)
			.expirationTime(new Date(now.getTime() + duration * 1000))
			.claim(ClaimName.CHANNEL, client.getChannel())
			.claim(ClaimName.GROUPS, roles.getListOfRoles());
	}
	
	/**
	 * 
	 * @param payload
	 * @return
	 */
	protected Uni<String> signPayload(JWTClaimsSet payload) {
		Log.debug("Token signing.");
		return tokenSigner.sign(payload)
			.map(SignedJWT::serialize);
	}
}