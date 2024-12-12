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
	/**
	 * 
	 * @param username
	 * @return
	 */
	public Uni<Optional<UserEntity>> findByUsername(String username) {
		return find(UserEntity.USERNAME_PRP, username).firstResultOptional();
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