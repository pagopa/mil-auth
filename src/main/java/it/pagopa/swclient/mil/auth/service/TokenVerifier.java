/*
 * TokenVerifier.java
 *
 * 27 apr 2023
 */
package it.pagopa.swclient.mil.auth.service;

import static it.pagopa.swclient.mil.auth.ErrorCode.ERROR_SEARCHING_FOR_KEYS;
import static it.pagopa.swclient.mil.auth.ErrorCode.ERROR_VERIFING_SIGNATURE;
import static it.pagopa.swclient.mil.auth.ErrorCode.EXPIRATION_TIME_MUST_NOT_BE_NULL;
import static it.pagopa.swclient.mil.auth.ErrorCode.ISSUE_TIME_MUST_NOT_BE_NULL;
import static it.pagopa.swclient.mil.auth.ErrorCode.KEY_NOT_FOUND;
import static it.pagopa.swclient.mil.auth.ErrorCode.TOKEN_EXPIRED;
import static it.pagopa.swclient.mil.auth.ErrorCode.WRONG_ALGORITHM;
import static it.pagopa.swclient.mil.auth.ErrorCode.WRONG_ISSUE_TIME;
import static it.pagopa.swclient.mil.auth.ErrorCode.WRONG_SCOPE;
import static it.pagopa.swclient.mil.auth.ErrorCode.WRONG_SIGNATURE;
import static it.pagopa.swclient.mil.auth.util.KeyPairUtil.getPublicKey;
import static it.pagopa.swclient.mil.auth.util.UniGenerator.error;
import static it.pagopa.swclient.mil.auth.util.UniGenerator.exception;
import static it.pagopa.swclient.mil.auth.util.UniGenerator.voidItem;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Date;
import java.util.Objects;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
import it.pagopa.swclient.mil.auth.bean.PublicKey;
import it.pagopa.swclient.mil.auth.util.AuthError;
import it.pagopa.swclient.mil.auth.util.AuthException;
import jakarta.inject.Inject;

/**
 * 
 * @author Antonio Tarricone
 */
public abstract class TokenVerifier {
	/*
	 * 
	 */
	@Inject
	KeyFinder keyFinder;

	/**
	 * This method verifies the token algorithm.
	 * 
	 * If the verification succeeds, the method returns void, otherwise it returns a failure with
	 * specific error code.
	 *
	 * @param token
	 * @return
	 */
	protected Uni<Void> verifyAlgorithm(SignedJWT token) {
		Log.debug("Algorithm verification.");
		JWSAlgorithm algorithm = token.getHeader().getAlgorithm();
		if (Objects.equals(algorithm, JWSAlgorithm.RS256)) {
			Log.debug("Algorithm has been successfully verified.");
			return voidItem();
		} else {
			String message = String.format("[%s] Wrong algorithm. Expected %s, found %s.", WRONG_ALGORITHM, JWSAlgorithm.RS256, algorithm);
			Log.warn(message);
			return exception(WRONG_ALGORITHM, message);
		}
	}

	/**
	 * This method verifies the token issue time.
	 * 
	 * If the verification succeeds, the method returns void, otherwise it returns a failure with
	 * specific error code.
	 */
	protected Uni<Void> verifyIssueTime(JWTClaimsSet claimsSet) {
		Log.debug("Issue time verification.");
		Date issueTime = claimsSet.getIssueTime();
		if (issueTime == null) {
			String message = String.format("[%s] Issue time must not be null.", ISSUE_TIME_MUST_NOT_BE_NULL);
			Log.warn(message);
			return exception(ISSUE_TIME_MUST_NOT_BE_NULL, message);
		} else {
			long issueEpoch = issueTime.getTime();
			long now = new Date().getTime();
			if (issueEpoch > now) {
				String message = String.format("[%s] Wrong issue time. Found %d but now is %d.", WRONG_ISSUE_TIME, issueEpoch, now);
				Log.warn(message);
				return exception(WRONG_ISSUE_TIME, message);
			} else {
				Log.debug("Issue time has been successfully verified.");
				return voidItem();
			}
		}
	}

	/**
	 * This method verifies the token expiration time.
	 * 
	 * If the verification succeeds, the method returns void, otherwise it returns a failure with
	 * specific error code.
	 * 
	 * @param claimsSet
	 * @return
	 */
	protected Uni<Void> verifyExpirationTime(JWTClaimsSet claimsSet) {
		Log.debug("Expiration time verification.");
		Date expirationTime = claimsSet.getExpirationTime();
		if (expirationTime == null) {
			String message = String.format("[%s] Expiration time must not be null.", EXPIRATION_TIME_MUST_NOT_BE_NULL);
			Log.warn(message);
			return exception(EXPIRATION_TIME_MUST_NOT_BE_NULL, message);
		} else if (expirationTime.before(new Date())) {
			String message = String.format("[%s] Token expired.", TOKEN_EXPIRED);
			Log.warn(message);
			return exception(TOKEN_EXPIRED, message);
		} else {
			Log.debug("Expiration time has been successfully verified.");
			return voidItem();
		}
	}

	/**
	 * This method verifies the token scope.
	 * 
	 * If the verification succeeds, the method returns void, otherwise it returns a failure with
	 * specific error code.
	 * 
	 * @param claimsSet
	 * @param expectedScope
	 * @return
	 */
	protected Uni<Void> verifyScope(JWTClaimsSet claimsSet, String expectedScope) {
		Log.debug("Scope verification.");
		Object foundScope = claimsSet.getClaim("scope");
		if (Objects.equals(foundScope, expectedScope)) {
			Log.debug("Scope has been successfully verified.");
			return voidItem();
		} else {
			String message = String.format("[%s] Wrong scope. Expected %s, found %s.", WRONG_SCOPE, expectedScope, foundScope);
			Log.warn(message);
			return exception(WRONG_SCOPE, message);
		}
	}

	/**
	 * This class verifies the token signature.
	 * 
	 * If the verification succeeds, the method returns void, otherwise it returns a failure with
	 * specific error code.
	 * 
	 * @param token
	 * @param key
	 * @return
	 */
	private Uni<Void> verifySignature(SignedJWT token, PublicKey key) {
		Log.debug("Signature verification.");
		try {
			JWSVerifier verifier = new RSASSAVerifier(getPublicKey(key));
			if (token.verify(verifier)) {
				Log.debug("Signature has been successfully verified.");
				return voidItem();
			} else {
				String message = String.format("[%s] Wrong signature.", WRONG_SIGNATURE);
				Log.warn(message);
				return exception(WRONG_SIGNATURE, message);
			}
		} catch (JOSEException | NoSuchAlgorithmException | InvalidKeySpecException e) {
			String message = String.format("[%s] Error verifing signature.", ERROR_VERIFING_SIGNATURE);
			Log.errorf(e, message);
			return error(ERROR_VERIFING_SIGNATURE, message);
		}
	}

	/**
	 * This method retrieves a public key.
	 * 
	 * @param kid
	 * @return
	 */
	private Uni<PublicKey> findPublicKey(String kid) {
		Log.debugf("Search for the public key %s.", kid);
		return keyFinder.findPublicKey(kid)
			.onFailure().transform(t -> {
				String message = String.format("[%s] Error searching for the public key.", ERROR_SEARCHING_FOR_KEYS);
				Log.errorf(t, message);
				return new AuthError(ERROR_SEARCHING_FOR_KEYS, message);
			})
			.onItem().transform(op -> op.orElseThrow(() -> {
				String message = String.format("[%s] Key %s not found.", KEY_NOT_FOUND, kid);
				Log.warn(message);
				return new AuthException(KEY_NOT_FOUND, message);
			}));
	}

	/**
	 * This method verifies the token signature retrieving the suitable public key.
	 * 
	 * If the verification succeeds, the method returns void, otherwise it returns a failure with
	 * specific error code.
	 * 
	 * @param token
	 * @return
	 */
	protected Uni<Void> verifySignature(SignedJWT token) {
		String kid = token.getHeader().getKeyID();
		return findPublicKey(kid)
			.chain(k -> {
				return verifySignature(token, k);
			});
	}
}