/*
 * TokenByPasswordService.java
 *
 * 17 mag 2023
 */
package it.pagopa.swclient.mil.auth.service;

import static it.pagopa.swclient.mil.auth.ErrorCode.ERROR_SEARCHING_FOR_CREDENTIALS;
import static it.pagopa.swclient.mil.auth.ErrorCode.ERROR_VERIFING_CREDENTIALS;
import static it.pagopa.swclient.mil.auth.ErrorCode.INCONSISTENT_CREDENTIALS;
import static it.pagopa.swclient.mil.auth.ErrorCode.WRONG_CREDENTIALS;
import static it.pagopa.swclient.mil.auth.util.UniGenerator.error;
import static it.pagopa.swclient.mil.auth.util.UniGenerator.exception;
import static it.pagopa.swclient.mil.auth.util.UniGenerator.item;
import static it.pagopa.swclient.mil.auth.util.UniGenerator.voidItem;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Objects;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import com.nimbusds.jose.util.StandardCharset;

import io.quarkus.cache.CacheResult;
import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
import it.pagopa.swclient.mil.auth.bean.GetAccessTokenResponse;
import it.pagopa.swclient.mil.auth.bean.GetAccessTokenRequest;
import it.pagopa.swclient.mil.auth.bean.User;
import it.pagopa.swclient.mil.auth.client.AuthDataRepository;
import it.pagopa.swclient.mil.auth.qualifier.Password;
import it.pagopa.swclient.mil.auth.util.AuthError;
import it.pagopa.swclient.mil.auth.util.AuthException;
import it.pagopa.swclient.mil.auth.util.PasswordVerifier;
import it.pagopa.swclient.mil.auth.util.UniGenerator;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

/**
 * 
 * @author Antonio Tarricone
 */
@ApplicationScoped
@Password
public class TokenByPasswordService extends TokenService {
	/*
	 * 
	 */
	@RestClient
	AuthDataRepository repository;

	/**
	 * 
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
					getAccessToken.getUsername().getBytes(StandardCharset.UTF_8)));
		} catch (NoSuchAlgorithmException e) {
			String message = String.format("[%s] Error searching for the credentials.", ERROR_SEARCHING_FOR_CREDENTIALS);
			Log.errorf(e, message);
			return UniGenerator.error(ERROR_SEARCHING_FOR_CREDENTIALS, message);
		}

		return getUser(userHash)
			.onFailure().transform(t -> {
				if (t instanceof WebApplicationException e) {
					Response r = e.getResponse();
					// r cannot be null
					if (r.getStatus() == 404) {
						Log.warnf("[%s] Credentials not found.", WRONG_CREDENTIALS);
						return new AuthException(WRONG_CREDENTIALS, String.format("[%s] Wrong credentials.", WRONG_CREDENTIALS)); // It's better not to give details...
					} else {
						String message = String.format("[%s] Error searching for the credentials.", ERROR_SEARCHING_FOR_CREDENTIALS);
						Log.errorf(t, message);
						return new AuthError(ERROR_SEARCHING_FOR_CREDENTIALS, message);
					}
				} else {
					String message = String.format("[%s] Error searching for the credentials.", ERROR_SEARCHING_FOR_CREDENTIALS);
					Log.errorf(t, message);
					return new AuthError(ERROR_SEARCHING_FOR_CREDENTIALS, message);
				}
			});
	}

	/**
	 * This method verifies acquirer/channel/merchant/username consistency.
	 * 
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
			return item(credentialsEntity);
		} else {
			Log.warnf("[%s] Acquirer/channel/merchant isn't consistent. Expected [%s/%s/%s], found [%s/%s/%s].", INCONSISTENT_CREDENTIALS, expectedAcquirerId, expectedChannel, expectedMerchantId, foundAcquirerId, foundChannel, foundMerchantId);
			return exception(INCONSISTENT_CREDENTIALS, String.format("[%s] Inconsistent credentials.", INCONSISTENT_CREDENTIALS)); // It's better not to give details...
		}
	}

	/**
	 * This method verifies the password.
	 * 
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
				return voidItem();
			} else {
				String message = String.format("[%s] Wrong credentials.", WRONG_CREDENTIALS);
				Log.warn(message);
				return exception(WRONG_CREDENTIALS, message);
			}
		} catch (NoSuchAlgorithmException e) {
			String message = String.format("[%s] Error verifing credentials.", ERROR_VERIFING_CREDENTIALS);
			Log.errorf(e, message);
			return error(ERROR_VERIFING_CREDENTIALS, message);
		}
	}

	/**
	 * This method verifies credentials.
	 * 
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
	 * 
	 * @param getAccessToken
	 * @return
	 */
	@Override
	public Uni<GetAccessTokenResponse> process(GetAccessTokenRequest getAccessToken) {
		Log.debugf("Generation of the token/s by password.");
		return verifyCredentials(getAccessToken)
			.chain(() -> super.process(getAccessToken));
	}

	public static void main(String[] args) throws NoSuchAlgorithmException {
		System.out.println(Base64.getEncoder().encodeToString(
			MessageDigest.getInstance("SHA256").digest(
				"carlodeche2".getBytes(StandardCharset.UTF_8)))
			.replace("+", "-"));
	}
}