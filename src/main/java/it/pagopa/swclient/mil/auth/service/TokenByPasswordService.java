/*
 * TokenByPasswordService.java
 *
 * 17 mag 2023
 */
package it.pagopa.swclient.mil.auth.service;

import java.util.Objects;

import io.quarkus.cache.CacheResult;
import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
import it.pagopa.swclient.mil.auth.AuthErrorCode;
import it.pagopa.swclient.mil.auth.bean.GetAccessTokenRequest;
import it.pagopa.swclient.mil.auth.bean.GetAccessTokenResponse;
import it.pagopa.swclient.mil.auth.dao.UserEntity;
import it.pagopa.swclient.mil.auth.dao.UserRepository;
import it.pagopa.swclient.mil.auth.qualifier.Password;
import it.pagopa.swclient.mil.auth.util.AuthError;
import it.pagopa.swclient.mil.auth.util.AuthException;
import it.pagopa.swclient.mil.auth.util.SecretTriplet;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 * @author Antonio Tarricone
 */
@ApplicationScoped
@Password
public class TokenByPasswordService extends TokenService {
	/*
	 *
	 */
	private UserRepository repository;

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
	TokenByPasswordService(ClientVerifier clientVerifier, RolesFinder roleFinder, TokenSigner tokenSigner, ClaimEncryptor claimEncryptor, UserRepository repository) {
		super(clientVerifier, roleFinder, tokenSigner, claimEncryptor);
		this.repository = repository;
	}

	/**
	 * This method finds for resource owner credentials.
	 *
	 * @param getAccessToken
	 * @return
	 */
	@CacheResult(cacheName = "client-role")
	public Uni<UserEntity> findUser(GetAccessTokenRequest getAccessToken) {
		String username = getAccessToken.getUsername();
		Log.tracef("Search for the user %s", username);
		return repository.findByUsername(username)
			.onFailure().transform(t -> {
				String message = String.format("[%s] Error searching for user %s", AuthErrorCode.ERROR_SEARCHING_FOR_USER, username);
				Log.errorf(t, message);
				return new AuthError(AuthErrorCode.ERROR_SEARCHING_FOR_USER, message);
			})
			.map(opt -> opt.orElseThrow(() -> {
				String message = String.format("[%s] User %s not found", AuthErrorCode.USER_NOT_FOUND, username);
				Log.warn(message);
				throw new AuthException(AuthErrorCode.USER_NOT_FOUND, message);
			}))
			.invoke(entity -> Log.debugf("User found: %s", entity));
	}

	/**
	 * This method verifies acquirer/channel/merchant/username consistency.
	 * <p>
	 * If the verification succeeds, the method returns ResourceOwnerCredentialsEntity, otherwise it
	 * returns a failure with specific error code.
	 *
	 * @param userEntity
	 * @param getAccessToken
	 * @return
	 */
	private UserEntity verifyConsistency(UserEntity userEntity, GetAccessTokenRequest getAccessToken) {
		Log.trace("Acquirer/channel/merchant consistency verification");

		String foundAcquirerId = userEntity.getAcquirerId();
		String foundChannel = userEntity.getChannel();
		String foundMerchantId = userEntity.getMerchantId();

		String expectedAcquirerId = getAccessToken.getAcquirerId();
		String expectedChannel = getAccessToken.getChannel();
		String expectedMerchantId = getAccessToken.getMerchantId();

		boolean consistency = Objects.equals(foundAcquirerId, expectedAcquirerId)
			&& Objects.equals(foundChannel, expectedChannel)
			&& Objects.equals(foundMerchantId, expectedMerchantId);

		if (consistency) {
			Log.debug("Acquirer/channel/merchant consistency has been successufully verified");
			return userEntity;
		} else {
			Log.warnf("[%s] Acquirer/channel/merchant isn't consistent. Expected %s/%s/%s, found %s/%s/%s", AuthErrorCode.INCONSISTENT_CREDENTIALS, expectedAcquirerId, expectedChannel, expectedMerchantId, foundAcquirerId, foundChannel, foundMerchantId);
			throw new AuthException(AuthErrorCode.INCONSISTENT_CREDENTIALS, String.format("[%s] Inconsistent credentials", AuthErrorCode.INCONSISTENT_CREDENTIALS)); // It's better not to give details...
		}
	}

	/**
	 * This method verifies the password.
	 * <p>
	 * If the verification succeeds, the method returns void, otherwise it returns a failure with
	 * specific error code.
	 *
	 * @param userEntity
	 * @param getAccessToken
	 * @return
	 */
	private Void verifyPassword(UserEntity userEntity, GetAccessTokenRequest getAccessToken) {
		Log.trace("Password verification");
		if (new SecretTriplet(getAccessToken.getPassword(), userEntity.getSalt(), userEntity.getPasswordHash()).verify()) {
			Log.debug("Password has been successfully verified");
			return null;
		} else {
			String message = String.format("[%s] Wrong credentials", AuthErrorCode.WRONG_CREDENTIALS);
			Log.warn(message);
			throw new AuthException(AuthErrorCode.WRONG_CREDENTIALS, message);
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
		return findUser(getAccessToken)
			.map(c -> verifyConsistency(c, getAccessToken))
			.map(c -> verifyPassword(c, getAccessToken));
	}

	/**
	 * @param getAccessToken
	 * @return
	 */
	@Override
	public Uni<GetAccessTokenResponse> process(GetAccessTokenRequest getAccessToken) {
		Log.trace("Generation of the token/s by password");
		return verifyCredentials(getAccessToken)
			.chain(() -> super.process(getAccessToken));
	}
}