/*
 * AzureClientsRepository.java
 * 
 * 29 mar 2024
 */
package it.pagopa.swclient.mil.auth.azure.service;

import io.quarkus.cache.CacheKey;
import io.quarkus.cache.CacheResult;
import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
import it.pagopa.swclient.mil.auth.bean.Client;
import it.pagopa.swclient.mil.auth.service.client.ClientsRepository;
import it.pagopa.swclient.mil.auth.util.AuthException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 * 
 * @author Antonio Tarricone
 */
@ApplicationScoped
public class AzureClientsRepository implements ClientsRepository {
	/*
	 * 
	 */
	private AzureBlobRepository repository;

	/**
	 * Constructor.
	 * 
	 * @param repository
	 */
	@Inject
	AzureClientsRepository(AzureBlobRepository repository) {
		this.repository = repository;
	}

	/**
	 * @see it.pagopa.swclient.mil.auth.service.client.ClientsRepository#getClient(String)
	 */
	@Override
	@CacheResult(cacheName = "clients-cache")
	public Uni<Client> getClient(@CacheKey String clientId) {
		String fileName = String.format("clients/%s.json", clientId);
		return repository.getFile(fileName, Client.class)
			.onItem().invoke(client -> Log.infof("Client [%s] found: [%s]", clientId, client))
			.onFailure(AuthException.class).invoke(t -> Log.warnf(t, "Exception searching for client [%s]", clientId))
			.onFailure(t -> !(t instanceof AuthException)).invoke(t -> Log.errorf(t, "Error searching for client [%s]", clientId));
	}
}