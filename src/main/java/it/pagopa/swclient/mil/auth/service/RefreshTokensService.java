/*
 * RefreshTokensService.java
 *
 * 17 mag 2023
 */
package it.pagopa.swclient.mil.auth.service;

import java.text.ParseException;
import java.util.Date;
import java.util.Objects;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
import it.pagopa.swclient.mil.auth.AuthErrorCode;
import it.pagopa.swclient.mil.auth.bean.ClaimName;
import it.pagopa.swclient.mil.auth.bean.GetAccessTokenRequest;
import it.pagopa.swclient.mil.auth.bean.GetAccessTokenResponse;
import it.pagopa.swclient.mil.auth.bean.Scope;
import it.pagopa.swclient.mil.auth.qualifier.RefreshToken;
import it.pagopa.swclient.mil.auth.util.UniGenerator;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 * @author Antonio Tarricone
 */
@ApplicationScoped
@RefreshToken
public class RefreshTokensService extends TokenService {
	/**
	 * 
	 */
	RefreshTokensService() {
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
	RefreshTokensService(@ConfigProperty(name = "access.duration") long accessDuration,
		@ConfigProperty(name = "refresh.duration") long refreshDuration,
		@ConfigProperty(name = "base-url", defaultValue = "") String baseUrl,
		@ConfigProperty(name = "token-audience", defaultValue = "mil.pagopa.it") String audience,
		ClientVerifier clientVerifier,
		RolesFinder roleFinder,
		TokenSigner tokenSigner) {
		super(accessDuration, refreshDuration, baseUrl, audience, clientVerifier, roleFinder, tokenSigner);
	}

	/**
	 * This method verifies the token algorithm.
	 * <p>
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
			return UniGenerator.voidItem();
		} else {
			String message = String.format("[%s] Wrong algorithm. Expected [%s], found [%s].", AuthErrorCode.WRONG_ALGORITHM, JWSAlgorithm.RS256, algorithm);
			Log.warn(message);
			return UniGenerator.exception(AuthErrorCode.WRONG_ALGORITHM, message);
		}
	}

	/**
	 * This method verifies the token issue time.
	 * <p>
	 * If the verification succeeds, the method returns void, otherwise it returns a failure with
	 * specific error code.
	 */
	private Uni<Void> verifyIssueTime(JWTClaimsSet claimsSet) {
		Log.debug("Issue time verification.");
		Date issueTime = claimsSet.getIssueTime();
		if (issueTime == null) {
			String message = String.format("[%s] Issue time must not be null.", AuthErrorCode.ISSUE_TIME_MUST_NOT_BE_NULL);
			Log.warn(message);
			return UniGenerator.exception(AuthErrorCode.ISSUE_TIME_MUST_NOT_BE_NULL, message);
		} else {
			long issueEpoch = issueTime.getTime();
			long now = new Date().getTime();
			if (issueEpoch > now) {
				String message = String.format("[%s] Wrong issue time. Found [%d] but now is [%d].", AuthErrorCode.WRONG_ISSUE_TIME, issueEpoch, now);
				Log.warn(message);
				return UniGenerator.exception(AuthErrorCode.WRONG_ISSUE_TIME, message);
			} else {
				Log.debug("Issue time has been successfully verified.");
				return UniGenerator.voidItem();
			}
		}
	}

	/**
	 * This method verifies the token expiration time.
	 * <p>
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
			String message = String.format("[%s] Expiration time must not be null.", AuthErrorCode.EXPIRATION_TIME_MUST_NOT_BE_NULL);
			Log.warn(message);
			return UniGenerator.exception(AuthErrorCode.EXPIRATION_TIME_MUST_NOT_BE_NULL, message);
		} else if (expirationTime.before(new Date())) {
			String message = String.format("[%s] Token expired.", AuthErrorCode.TOKEN_EXPIRED);
			Log.warn(message);
			return UniGenerator.exception(AuthErrorCode.TOKEN_EXPIRED, message);
		} else {
			Log.debug("Expiration time has been successfully verified.");
			return UniGenerator.voidItem();
		}
	}

	/**
	 * This method verifies the token scope.
	 * <p>
	 * If the verification succeeds, the method returns void, otherwise it returns a failure with
	 * specific error code.
	 *
	 * @param claimsSet
	 * @param expectedScope
	 * @return
	 */
	private Uni<Void> verifyScope(JWTClaimsSet claimsSet, String expectedScope) {
		Log.debug("Scope verification.");
		Object foundScope = claimsSet.getClaim(ClaimName.SCOPE);
		if (Objects.equals(foundScope, expectedScope)) {
			Log.debug("Scope has been successfully verified.");
			return UniGenerator.voidItem();
		} else {
			String message = String.format("[%s] Wrong scope. Expected [%s], found [%s].", AuthErrorCode.WRONG_SCOPE, expectedScope, foundScope);
			Log.warn(message);
			return UniGenerator.exception(AuthErrorCode.WRONG_SCOPE, message);
		}
	}

	/**
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
				.chain(() -> verifyScope(claimsSet, Scope.OFFLINE_ACCESS))
				.chain(() -> tokenSigner.verify(token));
		} catch (ParseException e) {
			String message = String.format("[%s] Error parsing token.", AuthErrorCode.ERROR_PARSING_TOKEN);
			Log.errorf(e, message);
			return UniGenerator.error(AuthErrorCode.ERROR_PARSING_TOKEN, message);
		}
	}

	/**
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