/*
 * TokenSigner.java
 *
 * 7 ago 2023
 */
package it.pagopa.swclient.mil.auth.service;

import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import io.smallrye.mutiny.Uni;

/**
 * 
 */
public interface TokenSigner {
	/**
	 * Signs the given token by means of the valid private key with the greatest expiration.
	 * 
	 * @param payload
	 * @return
	 */
	public Uni<SignedJWT> sign(JWTClaimsSet payload);

	/**
	 * This class verifies the token signature.
	 * 
	 * If the verification succeeds, the method returns void, otherwise it returns a failure with
	 * specific error code.
	 * 
	 * @param token
	 * @return
	 */
	public Uni<Void> verify(SignedJWT token);
}