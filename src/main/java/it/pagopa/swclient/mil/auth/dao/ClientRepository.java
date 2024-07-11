/*
 * ClientRepository.java
 *
 * 9 lug 2024
 */
package it.pagopa.swclient.mil.auth.dao;

import io.quarkus.mongodb.panache.reactive.ReactivePanacheMongoRepository;
import io.smallrye.mutiny.Uni;
import it.pagopa.swclient.mil.observability.TraceReactivePanacheMongoRepository;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * 
 * @author Antonio Tarricone
 */
@TraceReactivePanacheMongoRepository
@ApplicationScoped
public class ClientRepository implements ReactivePanacheMongoRepository<ClientEntity> {
	/**
	 * 
	 * @param clientId
	 * @return
	 */
	public Uni<ClientEntity> findByClientId(String clientId) {
		return find(ClientEntity.CLIENT_ID_PRP, clientId).firstResult();
	}
}
