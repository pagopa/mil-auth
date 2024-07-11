/*
 * ClientVerifier.java
 *
 * 16 mag 2023
 */
package it.pagopa.swclient.mil.auth.service;

import java.util.Objects;

import io.quarkus.cache.CacheResult;
import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
import it.pagopa.swclient.mil.auth.AuthErrorCode;
import it.pagopa.swclient.mil.auth.dao.ClientEntity;
import it.pagopa.swclient.mil.auth.dao.ClientRepository;
import it.pagopa.swclient.mil.auth.util.AuthError;
import it.pagopa.swclient.mil.auth.util.AuthException;
import it.pagopa.swclient.mil.auth.util.SecretVerifier;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 * @author Antonio Tarricone
 */
@ApplicationScoped
public class ClientVerifier {
	/*
	 * 
	 */
	private ClientRepository repository;

	/**
	 * 
	 * @param repository
	 */
	@Inject
	ClientVerifier(ClientRepository repository) {
		this.repository = repository;
	}

	/**
	 * 
	 * @param clientId
	 * @return
	 */
	@CacheResult(cacheName = "client-role")
	public Uni<ClientEntity> findClient(String clientId) {
		Log.tracef("Search for the client %s", clientId);
		return repository.findByClientId(clientId)
			.onFailure().transform(t -> {
				String message = String.format("[%s] Error searching for client %s", AuthErrorCode.ERROR_SEARCHING_FOR_CLIENT, clientId);
				Log.errorf(t, message);
				return new AuthError(AuthErrorCode.ERROR_SEARCHING_FOR_CLIENT, message);
			})
			.invoke(clientEntity -> {
				if (clientEntity == null) {
					String message = String.format("[%s] Client %s not found", AuthErrorCode.CLIENT_NOT_FOUND, clientId);
					Log.warn(message);
					throw new AuthException(AuthErrorCode.CLIENT_NOT_FOUND, message);
				} else {
					Log.debugf("Client found: %s", clientEntity);
				}
			});
	}

	/**
	 * 
	 * @param clientEntity
	 * @param expectedChannel
	 */
	private ClientEntity verifyChannel(ClientEntity clientEntity, String expectedChannel) {
		Log.trace("Channel verification");
		String foundChannel = clientEntity.getChannel();
		if (Objects.equals(foundChannel, expectedChannel)) {
			Log.debug("Channel has been successfully verified");
			return clientEntity;
		} else {
			String message = String.format("[%s] Wrong channel: expected %s, found %s", AuthErrorCode.WRONG_CHANNEL, expectedChannel, foundChannel);
			Log.warn(message);
			throw new AuthException(AuthErrorCode.WRONG_CHANNEL, message);
		}
	}

	/**
	 * 
	 * @param clientEntity
	 * @param expectedSecret
	 */
	private ClientEntity verifySecret(ClientEntity clientEntity, String expectedSecret) {
		Log.trace("Secret verification");
		String foundSecret = clientEntity.getSecretHash();
		if (foundSecret == null && expectedSecret == null) {
			Log.debug("Secret is not used");
			return clientEntity;
		} else if (foundSecret != null && expectedSecret != null && SecretVerifier.verify(expectedSecret, clientEntity.getSalt(), foundSecret)) {
			Log.debug("Secret is ok");
			return clientEntity;
		} else {
			String message = String.format("[%s] Wrong secret", AuthErrorCode.WRONG_SECRET);
			Log.warn(message);
			throw new AuthException(AuthErrorCode.WRONG_SECRET, message);
		}
	}

	/**
	 * 
	 * @param clientId
	 * @param channel
	 * @param secret
	 * @return
	 */
	public Uni<ClientEntity> verify(String clientId, String channel, String secret) {
		return findClient(clientId)
			.map(e -> verifyChannel(e, channel))
			.map(e -> verifySecret(e, secret));
	}
}