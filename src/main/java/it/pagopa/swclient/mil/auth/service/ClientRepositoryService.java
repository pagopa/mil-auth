/*
 * ClientRepositoryService.java
 *
 * 2 apr 2024
 */
package it.pagopa.swclient.mil.auth.service;

import io.quarkus.cache.CacheResult;
import io.smallrye.mutiny.Uni;
import it.pagopa.swclient.mil.auth.bean.Client;
import it.pagopa.swclient.mil.auth.service.client.ClientsRepository;
import jakarta.inject.Inject;

/**
 * This service adds cache on top of ClientRepository.
 * 
 * @author Antonio Tarricone
 */
public class ClientRepositoryService {
	/*
	 * 
	 */
	private ClientsRepository repository;
	
	/**
	 * 
	 * @param repository
	 */
	@Inject
	public ClientRepositoryService(ClientsRepository repository) {
		this.repository = repository;
	}

	/**
	 * @see ClientsRepository#getClient(String)
	 */
	@CacheResult(cacheName = "client-repository-cache")
	public Uni<Client> getClient(String clientId) {
		return repository.getClient(clientId);
	}
}