/*
 * RefreshTokensService.java
 *
 * 17 mag 2023
 */
package it.pagopa.swclient.mil.auth.service;

import static it.pagopa.swclient.mil.auth.ErrorCode.ERROR_PARSING_TOKEN;
import static it.pagopa.swclient.mil.auth.ErrorCode.EXPIRATION_TIME_MUST_NOT_BE_NULL;
import static it.pagopa.swclient.mil.auth.ErrorCode.ISSUE_TIME_MUST_NOT_BE_NULL;
import static it.pagopa.swclient.mil.auth.ErrorCode.TOKEN_EXPIRED;
import static it.pagopa.swclient.mil.auth.ErrorCode.WRONG_ALGORITHM;
import static it.pagopa.swclient.mil.auth.ErrorCode.WRONG_ISSUE_TIME;
import static it.pagopa.swclient.mil.auth.ErrorCode.WRONG_SCOPE;
import static it.pagopa.swclient.mil.auth.util.UniGenerator.error;
import static it.pagopa.swclient.mil.auth.util.UniGenerator.exception;
import static it.pagopa.swclient.mil.auth.util.UniGenerator.voidItem;

import java.text.ParseException;
import java.util.Date;
import java.util.Objects;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
import it.pagopa.swclient.mil.auth.bean.GetAccessTokenResponse;
import it.pagopa.swclient.mil.auth.bean.GetAccessTokenRequest;
import it.pagopa.swclient.mil.auth.qualifier.RefreshToken;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * 
 * @author Antonio Tarricone
 */
@ApplicationScoped
@RefreshToken
public class RefreshTokensService extends TokenService {
	/**
	 * This method verifies the token algorithm.
	 * 
	 * If the verification succeeds, the method returns void, otherwise it returns a failure with
	 * specific error code.
	 *
	 * @param token
	 * @return
	 */
	private Uni<Void> verifyAlgorithm(SignedJWT token) {
		Log.debug("Algorithm verification.");
		JWSAlgorithm algorithm = token.getHeader().getAlgorithm();
		if (Objects.equals(algorithm, JWSAlgorithm.RS256)) {
			Log.debug("Algorithm has been successfully verified.");
			return voidItem();
		} else {
			String message = String.format("[%s] Wrong algorithm. Expected [%s], found [%s].", WRONG_ALGORITHM, JWSAlgorithm.RS256, algorithm);
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
	private Uni<Void> verifyIssueTime(JWTClaimsSet claimsSet) {
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
				String message = String.format("[%s] Wrong issue time. Found [%d] but now is [%d].", WRONG_ISSUE_TIME, issueEpoch, now);
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
	private Uni<Void> verifyExpirationTime(JWTClaimsSet claimsSet) {
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
	private Uni<Void> verifyScope(JWTClaimsSet claimsSet, String expectedScope) {
		Log.debug("Scope verification.");
		Object foundScope = claimsSet.getClaim("scope");
		if (Objects.equals(foundScope, expectedScope)) {
			Log.debug("Scope has been successfully verified.");
			return voidItem();
		} else {
			String message = String.format("[%s] Wrong scope. Expected [%s], found [%s].", WRONG_SCOPE, expectedScope, foundScope);
			Log.warn(message);
			return exception(WRONG_SCOPE, message);
		}
	}

	/**
	 * 
	 * @param refreshTokenStr
	 * @return
	 */
	private Uni<Void> verify(String tokenStr) {
		try {
			SignedJWT token = SignedJWT.parse(tokenStr);
			JWTClaimsSet claimsSet = token.getJWTClaimsSet();
			return verifyAlgorithm(token)
				.chain(() -> verifyIssueTime(claimsSet))
				.chain(() -> verifyExpirationTime(claimsSet))
				.chain(() -> verifyScope(claimsSet, "offline_access"))
				.chain(() -> tokenSigner.verify(token));
		} catch (ParseException e) {
			String message = String.format("[%s] Error parsing token.", ERROR_PARSING_TOKEN);
			Log.errorf(e, message);
			return error(ERROR_PARSING_TOKEN, message);
		}
	}

	/**
	 * 
	 * @param getAccessToken
	 * @return
	 */
	@Override
	public Uni<GetAccessTokenResponse> process(GetAccessTokenRequest getAccessToken) {
		Log.debug("Tokens refreshing.");
		return verify(getAccessToken.getRefreshToken())
			.chain(() -> super.process(getAccessToken));
	}
}