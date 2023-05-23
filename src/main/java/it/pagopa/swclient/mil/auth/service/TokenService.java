/*
 * TokenService.java
 *
 * 17 mag 2023
 */
package it.pagopa.swclient.mil.auth.service;

import static it.pagopa.swclient.mil.auth.ErrorCode.ERROR_GENERATING_TOKEN;
import static it.pagopa.swclient.mil.auth.ErrorCode.ERROR_SEARCHING_FOR_ROLES;
import static it.pagopa.swclient.mil.auth.ErrorCode.ROLES_NOT_FOUND;
import static it.pagopa.swclient.mil.auth.util.UniGenerator.error;
import static it.pagopa.swclient.mil.auth.util.UniGenerator.item;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;
import java.util.Objects;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import com.nimbusds.jose.JOSEException;

import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
import it.pagopa.swclient.mil.auth.bean.AccessToken;
import it.pagopa.swclient.mil.auth.bean.GetAccessToken;
import it.pagopa.swclient.mil.auth.bean.GrantType;
import it.pagopa.swclient.mil.auth.dao.RoleEntity;
import it.pagopa.swclient.mil.auth.util.AuthError;
import it.pagopa.swclient.mil.auth.util.AuthException;
import it.pagopa.swclient.mil.auth.util.TokenGenerator;
import jakarta.inject.Inject;

/**
 * 
 * @author Antonio Tarricone
 */
public abstract class TokenService {
	/*
	 * Access token duration.
	 */
	@ConfigProperty(name = "access.duration")
	long accessDuration;

	/*
	 * Duration of refresh tokens in seconds.
	 */
	@ConfigProperty(name = "refresh.duration")
	long refreshDuration;

	/*
	 * 
	 */
	@Inject
	KeyFinder keyFinder;

	/*
	 * 
	 */
	@Inject
	ClientVerifier clientVerifier;

	/*
	 * 
	 */
	@Inject
	RolesFinder roleFinder;

	/**
	 * This method generates access token string and refresh token string if any, finding the key pair
	 * to sign them.
	 * 
	 * @param getAccessToken
	 * @param roles
	 * @return
	 */
	private Uni<AccessToken> generateToken(GetAccessToken getAccessToken, List<String> roles) {
		return keyFinder.findKeyPair()
			.chain(k -> {
				Log.debug("Access token generation.");
				try {
					String accessToken = TokenGenerator.generate(getAccessToken.getAcquirerId(), getAccessToken.getChannel(), getAccessToken.getMerchantId(), getAccessToken.getClientId(), getAccessToken.getTerminalId(), accessDuration, roles, null, k);
					String refreshToken = null;
					if (Objects.equals(getAccessToken.getScope(), "offline_access") || getAccessToken.getGrantType().equals(GrantType.REFRESH_TOKEN)) {
						Log.debug("Refresh token generation.");
						refreshToken = TokenGenerator.generate(getAccessToken.getAcquirerId(), getAccessToken.getChannel(), getAccessToken.getMerchantId(), getAccessToken.getClientId(), getAccessToken.getTerminalId(), refreshDuration, null, List.of("offline_access"), k);
					}
					Log.debug("Token/s has/ve been successfully generated.");
					return item(new AccessToken(accessToken, refreshToken, accessDuration));
				} catch (NoSuchAlgorithmException | InvalidKeySpecException | JOSEException e) {
					String message = String.format("[%s] Error generating token/s.", ERROR_GENERATING_TOKEN);
					Log.errorf(e, message);
					return error(ERROR_GENERATING_TOKEN, message);
				}
			});
	}

	/**
	 * This method finds roles for the client, handling errors.
	 * 
	 * @param getAccessToken
	 * @return
	 */
	private Uni<RoleEntity> findRoles(GetAccessToken getAccessToken) {
		return roleFinder.findRoles(getAccessToken.getAcquirerId(), getAccessToken.getChannel(), getAccessToken.getMerchantId(), getAccessToken.getClientId(), getAccessToken.getTerminalId())
			.onFailure().transform(t -> {
				String message = String.format("[%s] Error searching for the roles.", ERROR_SEARCHING_FOR_ROLES);
				Log.errorf(t, message);
				return new AuthError(ERROR_SEARCHING_FOR_ROLES, message);
			})
			.map(o -> o.orElseThrow(() -> {
				String message = String.format("[%s] Roles not found.", ROLES_NOT_FOUND);
				Log.warn(message);
				return new AuthException(ROLES_NOT_FOUND, message);
			}));
	}

	/**
	 * This method contains all common logic behind the access token generation.
	 * 
	 * @param getAccessToken
	 * @return
	 */
	public Uni<AccessToken> process(GetAccessToken getAccessToken) {
		return clientVerifier.verify(getAccessToken.getClientId(), getAccessToken.getChannel(), getAccessToken.getClientSecret())
			.chain(() -> {
				return findRoles(getAccessToken);
			})
			.chain(roleEntity -> {
				return generateToken(getAccessToken, roleEntity.getRoles());
			});
	}
}