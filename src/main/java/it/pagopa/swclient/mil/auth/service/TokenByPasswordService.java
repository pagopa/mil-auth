/*
 * TokenByPasswordService.java
 *
 * 17 mag 2023
 */
package it.pagopa.swclient.mil.auth.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Objects;

import io.quarkus.cache.CacheResult;
import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
import it.pagopa.swclient.mil.auth.AuthErrorCode;
import it.pagopa.swclient.mil.auth.bean.GetAccessTokenRequest;
import it.pagopa.swclient.mil.auth.bean.GetAccessTokenResponse;
import it.pagopa.swclient.mil.auth.bean.User;
import it.pagopa.swclient.mil.auth.qualifier.Password;
import it.pagopa.swclient.mil.auth.util.AuthError;
import it.pagopa.swclient.mil.auth.util.AuthException;
import it.pagopa.swclient.mil.auth.util.PasswordVerifier;
import it.pagopa.swclient.mil.auth.util.UniGenerator;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

/**
 * @author Antonio Tarricone
 */
@ApplicationScoped
@Password
public class TokenByPasswordService extends TokenService {
	/*
	 * 
	 */
	private static final String ERROR_SEARCHING_FOR_CREDENTIALS_MSG = "[%s] Error searching for the credentials.";

	/*
	 *
	 */
	private AuthDataRepository repository;

	/**
	 * 
	 */
	TokenByPasswordService() {
		super();
	}

	/**
	 * 
	 * @param clientVerifier
	 * @param roleFinder
	 * @param tokenSigner
	 * @param claimEncryptor
	 * @param repository
	 */
	@Inject
	TokenByPasswordService(ClientVerifier clientVerifier, RolesFinder roleFinder, TokenSigner tokenSigner, ClaimEncryptor claimEncryptor, AuthDataRepository repository) {
		super(clientVerifier, roleFinder, tokenSigner, claimEncryptor);
		this.repository = repository;
	}

	/**
	 * @param userHash
	 * @return
	 */
	@CacheResult(cacheName = "client-role")
	public Uni<User> getUser(String userHash) {
		return repository.getUser(userHash);
	}

	/**
	 * This method finds for resource owner credentials.
	 *
	 * @param getAccessToken
	 * @return
	 */
	private Uni<User> findCredentials(GetAccessTokenRequest getAccessToken) {
		Log.debug("Search for the credentials.");

		String userHash;
		try {
			userHash = Base64.getUrlEncoder().encodeToString(
				MessageDigest.getInstance("SHA256").digest(
					getAccessToken.getUsername().getBytes(StandardCharsets.UTF_8)));
		} catch (NoSuchAlgorithmException e) {
			String message = String.format(ERROR_SEARCHING_FOR_CREDENTIALS_MSG, AuthErrorCode.ERROR_SEARCHING_FOR_CREDENTIALS);
			Log.errorf(e, message);
			return UniGenerator.error(AuthErrorCode.ERROR_SEARCHING_FOR_CREDENTIALS, message);
		}

		return getUser(userHash)
			.onFailure().transform(t -> {
				if (t instanceof WebApplicationException e) {
					Response r = e.getResponse();
					// r cannot be null
					if (r.getStatus() == 404) {
						Log.warnf("[%s] Credentials not found.", AuthErrorCode.WRONG_CREDENTIALS);
						return new AuthException(AuthErrorCode.WRONG_CREDENTIALS, String.format("[%s] Wrong credentials.", AuthErrorCode.WRONG_CREDENTIALS)); // It's better not to give details...
					} else {
						String message = String.format(ERROR_SEARCHING_FOR_CREDENTIALS_MSG, AuthErrorCode.ERROR_SEARCHING_FOR_CREDENTIALS);
						Log.errorf(t, message);
						return new AuthError(AuthErrorCode.ERROR_SEARCHING_FOR_CREDENTIALS, message);
					}
				} else {
					String message = String.format(ERROR_SEARCHING_FOR_CREDENTIALS_MSG, AuthErrorCode.ERROR_SEARCHING_FOR_CREDENTIALS);
					Log.errorf(t, message);
					return new AuthError(AuthErrorCode.ERROR_SEARCHING_FOR_CREDENTIALS, message);
				}
			});
	}

	/**
	 * This method verifies acquirer/channel/merchant/username consistency.
	 * <p>
	 * If the verification succeeds, the method returns ResourceOwnerCredentialsEntity, otherwise it
	 * returns a failure with specific error code.
	 *
	 * @param credentialsEntity
	 * @param getAccessToken
	 * @return
	 */
	private Uni<User> verifyConsistency(User credentialsEntity, GetAccessTokenRequest getAccessToken) {
		Log.debug("Acquirer/channel/merchant consistency verification.");

		String foundAcquirerId = credentialsEntity.getAcquirerId();
		String foundChannel = credentialsEntity.getChannel();
		String foundMerchantId = credentialsEntity.getMerchantId();

		String expectedAcquirerId = getAccessToken.getAcquirerId();
		String expectedChannel = getAccessToken.getChannel();
		String expectedMerchantId = getAccessToken.getMerchantId();

		boolean consistency = foundAcquirerId.equals(expectedAcquirerId)
			&& foundChannel.equals(expectedChannel)
			&& Objects.equals(foundMerchantId, expectedMerchantId);

		if (consistency) {
			Log.debug("Acquirer/channel/merchant consistency has been successufully verified.");
			return UniGenerator.item(credentialsEntity);
		} else {
			Log.warnf("[%s] Acquirer/channel/merchant isn't consistent. Expected [%s/%s/%s], found [%s/%s/%s].", AuthErrorCode.INCONSISTENT_CREDENTIALS, expectedAcquirerId, expectedChannel, expectedMerchantId, foundAcquirerId, foundChannel, foundMerchantId);
			return UniGenerator.exception(AuthErrorCode.INCONSISTENT_CREDENTIALS, String.format("[%s] Inconsistent credentials.", AuthErrorCode.INCONSISTENT_CREDENTIALS)); // It's better not to give details...
		}
	}

	/**
	 * This method verifies the password.
	 * <p>
	 * If the verification succeeds, the method returns void, otherwise it returns a failure with
	 * specific error code.
	 *
	 * @param credentialsEntity
	 * @param getAccessToken
	 * @return
	 */
	private Uni<Void> verifyPassword(User credentialsEntity, GetAccessTokenRequest getAccessToken) {
		Log.debug("Password verification.");
		try {
			if (PasswordVerifier.verify(getAccessToken.getPassword(), credentialsEntity.getSalt(), credentialsEntity.getPasswordHash())) {
				Log.debug("Password has been successfully verified.");
				return UniGenerator.voidItem();
			} else {
				String message = String.format("[%s] Wrong credentials.", AuthErrorCode.WRONG_CREDENTIALS);
				Log.warn(message);
				return UniGenerator.exception(AuthErrorCode.WRONG_CREDENTIALS, message);
			}
		} catch (NoSuchAlgorithmException e) {
			String message = String.format("[%s] Error verifing credentials.", AuthErrorCode.ERROR_VERIFING_CREDENTIALS);
			Log.errorf(e, message);
			return UniGenerator.error(AuthErrorCode.ERROR_VERIFING_CREDENTIALS, message);
		}
	}

	/**
	 * This method verifies credentials.
	 * <p>
	 * ResourceOwnerCredentialsEntity
	 *
	 * @param getAccessToken
	 */
	private Uni<Void> verifyCredentials(GetAccessTokenRequest getAccessToken) {
		return findCredentials(getAccessToken)
			.chain(c -> verifyConsistency(c, getAccessToken))
			.chain(c -> verifyPassword(c, getAccessToken));
	}

	/**
	 * @param getAccessToken
	 * @return
	 */
	@Override
	public Uni<GetAccessTokenResponse> process(GetAccessTokenRequest getAccessToken) {
		Log.debugf("Generation of the token/s by password.");
		return verifyCredentials(getAccessToken)
			.chain(() -> super.process(getAccessToken));
	}
}