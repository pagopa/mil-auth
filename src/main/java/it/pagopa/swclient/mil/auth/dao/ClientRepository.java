/*
 * ClientRepository.java
 *
 * 9 lug 2024
 */
package it.pagopa.swclient.mil.auth.dao;

import java.util.List;
import java.util.Optional;

import org.bson.Document;

import io.quarkus.mongodb.panache.reactive.ReactivePanacheMongoRepository;
import io.quarkus.panache.common.Sort;
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
	 * @param page
	 * @param size
	 * @return
	 */
	public Uni<List<ClientEntity>> findAll(int page, int size) {
		return findAll(Sort.ascending(ClientEntity.CLIENT_ID_PRP))
			.page(page, size)
			.list();
	}

	/**
	 * 
	 * @param clientId
	 * @return
	 */
	public Uni<Optional<ClientEntity>> findByClientId(String clientId) {
		return find(ClientEntity.CLIENT_ID_PRP, clientId).firstResultOptional();
	}

	/**
	 * 
	 * @param clientId
	 * @return
	 */
	public Uni<Long> deleteByClientId(String clientId) {
		return delete(ClientEntity.CLIENT_ID_PRP, clientId);
	}

	/**
	 * 
	 * @param clientId
	 * @param channel
	 * @param description
	 * @param subject
	 * @return
	 */
	public Uni<Long> updateByClientId(String clientId, String channel, String description, String subject) {
		return update(
			new Document("$set", new Document()
				.append(ClientEntity.CHANNEL_PRP, channel)
				.append(ClientEntity.DESCRIPTION_PRP, description)
				.append(ClientEntity.SUBJECT_PRP, subject)))
			.where(ClientEntity.CLIENT_ID_PRP, clientId);
	}
}