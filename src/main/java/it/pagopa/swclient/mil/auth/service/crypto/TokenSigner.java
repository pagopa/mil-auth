/*
 * TokenSigner.java
 *
 * 7 ago 2023
 */
package it.pagopa.swclient.mil.auth.service.crypto;

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
	 * @param claimsSet
	 * @return
	 */
	public Uni<SignedJWT> sign(JWTClaimsSet claimsSet);

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