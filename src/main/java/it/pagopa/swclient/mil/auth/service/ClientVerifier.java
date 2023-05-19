/*
 * ClientVerifier.java
 *
 * 16 mag 2023
 */
package it.pagopa.swclient.mil.auth.service;

import static it.pagopa.swclient.mil.auth.ErrorCode.CLIENT_NOT_FOUND;
import static it.pagopa.swclient.mil.auth.ErrorCode.ERROR_SEARCHING_FOR_CLIENT;
import static it.pagopa.swclient.mil.auth.ErrorCode.WRONG_CHANNEL;
import static it.pagopa.swclient.mil.auth.ErrorCode.WRONG_SECRET;

import java.util.Objects;

import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
import it.pagopa.swclient.mil.auth.dao.ClientEntity;
import it.pagopa.swclient.mil.auth.dao.ClientRepository;
import it.pagopa.swclient.mil.auth.util.AuthError;
import it.pagopa.swclient.mil.auth.util.AuthException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 * 
 * @author Antonio Tarricone
 */
@ApplicationScoped
public class ClientVerifier {
	/*
	 * 
	 */
	@Inject
	ClientRepository clientRepository;

	/**
	 * 
	 * @param clientRepository
	 * @param clientId
	 * @return
	 */
	private Uni<ClientEntity> findClient(String clientId) {
		Log.debugf("Search for the client %s.", clientId);
		return clientRepository.findByIdOptional(clientId)
			.onFailure().transform(t -> {
				String message = String.format("[%s] Error searching for the client %s.", ERROR_SEARCHING_FOR_CLIENT, clientId);
				Log.errorf(t, message);
				return new AuthError(ERROR_SEARCHING_FOR_CLIENT, message);
			})
			.map(o -> o.orElseThrow(() -> {
				String message = String.format("[%s] Client %s not found.", CLIENT_NOT_FOUND, clientId);
				Log.warn(message);
				return new AuthException(CLIENT_NOT_FOUND, message);
			}));
	}

	/**
	 * 
	 * @param clientEntity
	 * @param expectedChannel
	 */
	private ClientEntity verifyChannel(ClientEntity clientEntity, String expectedChannel) {
		Log.debug("Channel verification.");
		String foundChannel = clientEntity.getChannel();
		if (Objects.equals(foundChannel, expectedChannel)) {
			Log.debug("Channel has been successfully verified.");
			return clientEntity;
		} else {
			String message = String.format("[%s] Wrong channel. Expected %s, found %s.", WRONG_CHANNEL, expectedChannel, expectedChannel);
			Log.warn(message);
			throw new AuthException(WRONG_CHANNEL, message);
		}
	}

	/**
	 * 
	 * @param clientEntity
	 * @param expectedSecret
	 */
	private ClientEntity verifySecret(ClientEntity clientEntity, String expectedSecret) {
		Log.debug("Secret verification.");
		String foundSecret = clientEntity.getSecret();
		if (Objects.equals(foundSecret, expectedSecret)) {
			Log.debug("Secret is ok.");
			return clientEntity;
		} else {
			String message = String.format("[%s] Wrong secret.", WRONG_SECRET);
			Log.warn(message);
			throw new AuthException(WRONG_SECRET, message);
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