/*
 * RefreshTokensService.java
 *
 * 17 mag 2023
 */
package it.pagopa.swclient.mil.auth.service;

import java.text.ParseException;
import java.util.Date;
import java.util.Objects;

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
import it.pagopa.swclient.mil.auth.dao.RevokedRefreshTokenEntity;
import it.pagopa.swclient.mil.auth.dao.RevokedRefreshTokenRepository;
import it.pagopa.swclient.mil.auth.dao.RevokedRefreshTokensGenerationEntity;
import it.pagopa.swclient.mil.auth.dao.RevokedRefreshTokensGenerationRepository;
import it.pagopa.swclient.mil.auth.qualifier.RefreshToken;
import it.pagopa.swclient.mil.auth.util.AuthError;
import it.pagopa.swclient.mil.auth.util.AuthException;
import it.pagopa.swclient.mil.auth.util.UniGenerator;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 * 
 * @author Antonio Tarricone
 */
@ApplicationScoped
@RefreshToken
public class RefreshTokensService extends TokenService {
	/*
	 * 
	 */
	private RevokedRefreshTokensGenerationRepository revokedRefreshTokensGenerationRepository;

	/*
	 * 
	 */
	private RevokedRefreshTokenRepository revokedRefreshTokenRepository;

	/**
	 * 
	 * @param clientVerifier
	 * @param roleFinder
	 * @param tokenSigner
	 * @param claimEncryptor
	 * @param revokedRefreshTokensGenerationRepository
	 * @param revokedRefreshTokenRepository
	 */
	@Inject
	RefreshTokensService(
		ClientVerifier clientVerifier,
		RolesFinder roleFinder,
		TokenSigner tokenSigner,
		ClaimEncryptor claimEncryptor,
		RevokedRefreshTokensGenerationRepository revokedRefreshTokensGenerationRepository,
		RevokedRefreshTokenRepository revokedRefreshTokenRepository) {
		super(clientVerifier, roleFinder, tokenSigner, claimEncryptor);
		this.revokedRefreshTokensGenerationRepository = revokedRefreshTokensGenerationRepository;
		this.revokedRefreshTokenRepository = revokedRefreshTokenRepository;
	}

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
		Log.trace("Algorithm verification");
		JWSAlgorithm algorithm = token.getHeader().getAlgorithm();
		if (Objects.equals(algorithm, JWSAlgorithm.RS256)) {
			Log.debug("Algorithm has been successfully verified");
			return UniGenerator.voidItem();
		} else {
			String message = String.format("[%s] Wrong algorithm: expected %s, found %s", AuthErrorCode.WRONG_ALGORITHM, JWSAlgorithm.RS256, algorithm);
			Log.warn(message);
			return UniGenerator.exception(AuthErrorCode.WRONG_ALGORITHM, message);
		}
	}

	/**
	 * This method verifies the token issue time.
	 * 
	 * If the verification succeeds, the method returns void, otherwise it throws an exception with
	 * specific error code.
	 * 
	 * @param issueTime
	 * @return
	 */
	private Void verifyIssueTime(Date issueTime) {
		Log.trace("Issue time verification");
		if (issueTime == null) {
			Log.warn(AuthErrorCode.ISSUE_TIME_MUST_NOT_BE_NULL_MSG);
			throw new AuthException(AuthErrorCode.ISSUE_TIME_MUST_NOT_BE_NULL, AuthErrorCode.ISSUE_TIME_MUST_NOT_BE_NULL_MSG);
		} else {
			long issueEpoch = issueTime.getTime();
			long now = new Date().getTime();
			if (issueEpoch > now) {
				String message = String.format("[%s] Wrong issue time: found %d but now is %d", AuthErrorCode.WRONG_ISSUE_TIME, issueEpoch, now);
				Log.warn(message);
				throw new AuthException(AuthErrorCode.WRONG_ISSUE_TIME, message);
			} else {
				Log.debug("Issue time has been successfully verified");
				return null;
			}
		}
	}

	/**
	 * This method verifies the token expiration time.
	 * 
	 * If the verification succeeds, the method returns void, otherwise it returns an exception with
	 * specific error code.
	 *
	 * @param expirationTime
	 * @return
	 */
	private Void verifyExpirationTime(Date expirationTime) {
		Log.trace("Expiration time verification");
		if (expirationTime == null) {
			Log.warn(AuthErrorCode.EXPIRATION_TIME_MUST_NOT_BE_NULL_MSG);
			throw new AuthException(AuthErrorCode.EXPIRATION_TIME_MUST_NOT_BE_NULL, AuthErrorCode.EXPIRATION_TIME_MUST_NOT_BE_NULL_MSG);
		} else if (expirationTime.before(new Date())) {
			Log.warn(AuthErrorCode.TOKEN_EXPIRED);
			throw new AuthException(AuthErrorCode.TOKEN_EXPIRED, AuthErrorCode.TOKEN_EXPIRED);
		} else {
			Log.debug("Expiration time has been successfully verified");
			return null;
		}
	}

	/**
	 * This method verifies the token scope.
	 * 
	 * If the verification succeeds, the method returns void, otherwise it throws returns an exception
	 * with specific error code.
	 *
	 * @param foundScope
	 * @param expectedScope
	 * @return
	 */
	private Void verifyScope(String foundScope, String expectedScope) {
		Log.trace("Scope verification");
		if (Objects.equals(foundScope, expectedScope)) {
			Log.debug("Scope has been successfully verified");
			return null;
		} else {
			String message = String.format("[%s] Wrong scope: expected %s, found %s", AuthErrorCode.WRONG_SCOPE, expectedScope, foundScope);
			Log.warn(message);
			throw new AuthException(AuthErrorCode.WRONG_SCOPE, message);
		}
	}

	/**
	 * This method verifies that the client ID of the request with the corresponding value reported in
	 * the claim of the refresh token.
	 * 
	 * If the verification succeeds, the method returns void, otherwise it throws an exception specific
	 * error code.
	 *
	 * @param foundClientId
	 * @param expectedClientId
	 * @return
	 */
	private Void verifyClientId(String foundClientId, String expectedClientId) {
		Log.trace("Client id verification");
		if (Objects.equals(foundClientId, expectedClientId)) {
			Log.debug("Client id has been successfully verified");
			return null;
		} else {
			final String message = String.format("[%s] Wrong client ID: expected %s, found %s", AuthErrorCode.WRONG_CLIENT_ID, expectedClientId, foundClientId);
			Log.warn(message);
			throw new AuthException(AuthErrorCode.WRONG_CLIENT_ID, AuthErrorCode.WRONG_CLIENT_ID_MSG);
		}
	}

	/**
	 * 
	 * @param generationId
	 * @param refreshTokenId
	 * @return
	 */
	private Uni<Void> verifyRefreshTokenRevocationList(String generationId, String refreshTokenId) {
		/*
		 * If the refresh token generation has not been revoked, check if the refresh token has been
		 * revoked.
		 */
		Log.debug("Check if the refresh token has been revoked");
		if (generationId != null) {
			if (refreshTokenId != null) {
				return revokedRefreshTokenRepository.findByJwtId(refreshTokenId)
					.chain(revokedRefreshTokenEntity -> {
						if (revokedRefreshTokenEntity.isPresent()) {
							/*
							 * If the refresh token has been revoked, revoke the entire refresh token generation.
							 */
							Log.warn("Refresh token has been revoked, the entire refresh token generation will be revoked");
							return revokedRefreshTokensGenerationRepository.persist(new RevokedRefreshTokensGenerationEntity()
								.setGenerationId(generationId))
								.onItem().invoke(() -> Log.debug("Refresh token generation revoked successfully"))
								.onFailure().invoke(failure -> Log.errorf(failure, "Error revoling refresh token"))
								.onItemOrFailure().transform((i, f) -> {
									throw new AuthException(AuthErrorCode.REFRESH_TOKEN_REVOKED, AuthErrorCode.REFRESH_TOKEN_REVOKED_MSG);
								});
						} else {
							Log.debug("Refresh token has not been revoked");
							return Uni.createFrom().voidItem();
						}
					});
			} else {
				Log.warn(AuthErrorCode.REFRESH_TOKEN_ID_MUST_NOT_BE_NULL_MSG);
				return UniGenerator.exception(AuthErrorCode.REFRESH_TOKEN_ID_MUST_NOT_BE_NULL, AuthErrorCode.REFRESH_TOKEN_ID_MUST_NOT_BE_NULL_MSG);
			}
		} else {
			Log.warn(AuthErrorCode.REFRESH_TOKEN_GENERATION_ID_MUST_NOT_BE_NULL_MSG);
			return UniGenerator.exception(AuthErrorCode.REFRESH_TOKEN_GENERATION_ID_MUST_NOT_BE_NULL, AuthErrorCode.REFRESH_TOKEN_GENERATION_ID_MUST_NOT_BE_NULL_MSG);
		}
	}

	/**
	 * 
	 * @param generationId
	 * @return
	 */
	private Uni<Void> verifyRefreshTokensGenerationRevocationList(String generationId) {
		/*
		 * Check if refresh token generation has been revoked.
		 */
		Log.trace("Check if refresh token generation has been revoked");
		return revokedRefreshTokensGenerationRepository.findByGenerationId(generationId)
			.chain(revokedRefreshTokensGenerationEntity -> {
				if (revokedRefreshTokensGenerationEntity.isPresent()) {
					/*
					 * Refresh token generation has been revoked.
					 */
					Log.warn(AuthErrorCode.REFRESH_TOKEN_GENERATION_REVOKED_MSG);
					return UniGenerator.exception(AuthErrorCode.REFRESH_TOKEN_GENERATION_REVOKED, AuthErrorCode.REFRESH_TOKEN_GENERATION_REVOKED_MSG);
				} else {
					/*
					 * Refresh token generation has not been revoked.
					 */
					Log.debug("Refresh token generation has not been revoked");
					return Uni.createFrom().voidItem();
				}
			});
	}

	/**
	 * Verifies that refresh token is received where expected (cookie or body).
	 * 
	 * @param getAccessToken
	 * @param returnedInTheCookie
	 * @param refreshTokenId
	 * @return
	 */
	private Uni<Void> verifyLocation(GetAccessTokenRequest getAccessToken, Boolean returnedInTheCookie, String refreshTokenId) {
		Log.trace("Refresh token location verification");

		boolean expected = returnedInTheCookie != null && returnedInTheCookie.booleanValue();
		Log.tracef("Refresh token expected in the cookie: %s", expected);

		boolean actual = getAccessToken.isTheRefreshTokenInTheCookie();
		Log.tracef("Refresh token is in the cookie: %s", actual);

		if (expected == actual) {
			/*
			 * Refresh token received where expected.
			 */
			Log.debug("Refresh token received where expected");
			return Uni.createFrom().voidItem();
		} else {
			/*
			 * Refresh token expected in the cookie but received in the body or vice versa.
			 * 
			 * In this case the refresh token will be revoked.
			 */
			Log.warn("Refresh token expected in the cookie but received in the body or vice versa, the refresh token will be revoked");
			return revokedRefreshTokenRepository.persist(new RevokedRefreshTokenEntity()
				.setJwtId(refreshTokenId))
				.onItem().invoke(item -> Log.debug("Refresh token revoked successfully"))
				.onFailure().invoke(failure -> Log.errorf(failure, AuthErrorCode.ERROR_REVOKING_REFRESH_TOKEN_MSG))
				.onItemOrFailure().transform((i, f) -> {
					throw new AuthException(AuthErrorCode.WRONG_REFRESH_TOKEN_LOCATION, AuthErrorCode.WRONG_REFRESH_TOKEN_LOCATION_MSG);
				});
		}
	}

	/**
	 * 
	 * @param getAccessToken
	 * @return
	 */
	@Override
	public Uni<GetAccessTokenResponse> process(GetAccessTokenRequest getAccessToken) {
		Log.trace("Tokens refreshing");
		SignedJWT token = getAccessToken.getTheRefreshToken();
		try {
			/*
			 * To avoid ParseException catching in each other methods!
			 */
			JWTClaimsSet claimsSet = token.getJWTClaimsSet();
			Date issueTime = claimsSet.getIssueTime();
			Date expirationTime = claimsSet.getExpirationTime();
			String scope = claimsSet.getStringClaim(ClaimName.SCOPE);
			String clientId = claimsSet.getStringClaim(ClaimName.CLIENT_ID);
			String generationId = claimsSet.getStringClaim(ClaimName.GENERATION_ID);
			String refreshTokenId = claimsSet.getJWTID();
			Boolean returnedInTheCookie = claimsSet.getBooleanClaim(ClaimName.RETURNED_IN_THE_COOKIE);

			return verifyAlgorithm(token)
				.map(x -> verifyIssueTime(issueTime))
				.map(x -> verifyExpirationTime(expirationTime))
				.map(x -> verifyScope(scope, Scope.OFFLINE_ACCESS))
				.map(x -> verifyClientId(clientId, getAccessToken.getClientId()))
				.chain(() -> tokenSigner.verify(token))
				.chain(() -> verifyRefreshTokenRevocationList(generationId, refreshTokenId))
				.chain(() -> verifyRefreshTokensGenerationRevocationList(generationId))
				.chain(() -> verifyLocation(getAccessToken, returnedInTheCookie, refreshTokenId))
				.chain(() -> {
					Log.debug("Current refresh token will be revoked");
					return revokedRefreshTokenRepository.persist(new RevokedRefreshTokenEntity()
						.setJwtId(refreshTokenId))
						.onItem().invoke(item -> Log.debug("Refresh token revoked successfully"))
						.onFailure().transform(failure -> {
							Log.errorf(failure, AuthErrorCode.ERROR_REVOKING_REFRESH_TOKEN_MSG);
							return new AuthError(AuthErrorCode.ERROR_REVOKING_REFRESH_TOKEN, AuthErrorCode.ERROR_REVOKING_REFRESH_TOKEN_MSG);
						});
				})
				.chain(() -> super.process(getAccessToken));
		} catch (ParseException e) {
			Log.errorf(e, AuthErrorCode.ERROR_PARSING_TOKEN_MSG);
			Log.errorf("Offending token: %s", token.serialize());
			return UniGenerator.error(AuthErrorCode.ERROR_PARSING_TOKEN, AuthErrorCode.ERROR_PARSING_TOKEN_MSG);
		}
	}
}