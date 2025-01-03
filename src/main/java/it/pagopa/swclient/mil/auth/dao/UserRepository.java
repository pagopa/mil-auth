/*
 * UserRepository.java
 *
 * 23 ott 2023
 */
package it.pagopa.swclient.mil.auth.dao;

import java.util.Optional;

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
public class UserRepository implements ReactivePanacheMongoRepository<UserEntity> {
	/*
	 * 
	 */
	static final String FIND_BY_USERNAME_AND_CLIENT_ID = String.format(
		"%s = ?1 and %s = ?2",
		UserEntity.USERNAME_PRP,
		UserEntity.CLIENT_ID_PRP);

	/**
	 * 
	 * @param username
	 * @param clientId
	 * @return
	 */
	public Uni<Optional<UserEntity>> findByUsernameAndClientId(String username, String clientId) {
		return find(FIND_BY_USERNAME_AND_CLIENT_ID, username, clientId).firstResultOptional();
	}

	/**
	 * 
	 * @param username
	 * @return
	 */
	public Uni<Long> deleteByUsername(String username) {
		return delete(UserEntity.USERNAME_PRP, username);
	}
}