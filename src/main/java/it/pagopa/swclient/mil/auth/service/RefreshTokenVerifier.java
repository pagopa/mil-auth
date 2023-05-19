/*
 * RefreshTokenVerifier.java
 *
 * 28 apr 2023
 */
package it.pagopa.swclient.mil.auth.service;

import static it.pagopa.swclient.mil.auth.ErrorCode.ERROR_PARSING_TOKEN;
import static it.pagopa.swclient.mil.auth.util.UniGenerator.error;

import java.text.ParseException;

import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * This class verifies the refresh token.
 * 
 * @author Antonio Tarricone
 */
@ApplicationScoped
public class RefreshTokenVerifier extends TokenVerifier {
	/**
	 * 
	 * @param refreshTokenStr
	 * @return
	 */
	public Uni<Void> verify(String tokenStr) {
		try {
			SignedJWT token = SignedJWT.parse(tokenStr);
			JWTClaimsSet claimsSet = token.getJWTClaimsSet();
			return verifyAlgorithm(token)
				.chain(() -> {
					return verifyIssueTime(claimsSet);
				})
				.chain(() -> {
					return verifyExpirationTime(claimsSet);
				})
				.chain(() -> {
					return verifyScope(claimsSet, "offline_access");
				})
				.chain(o -> {
					return verifySignature(token);
				});
		} catch (ParseException e) {
			String message = String.format("[%s] Error parsing token.", ERROR_PARSING_TOKEN);
			Log.errorf(e, message);
			return error(ERROR_PARSING_TOKEN, message);
		}
	}
}