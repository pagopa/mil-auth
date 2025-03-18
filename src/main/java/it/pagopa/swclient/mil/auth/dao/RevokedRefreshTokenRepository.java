/*
 * RevokedRefreshTokenRepository.java
 *
 * 13 gen 2025
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
public class RevokedRefreshTokenRepository implements ReactivePanacheMongoRepository<RevokedRefreshTokenEntity> {
	/**
	 * 
	 * @param jwtId
	 * @return
	 */
	public Uni<Optional<RevokedRefreshTokenEntity>> findByJwtId(String jwtId) {
		return find(RevokedRefreshTokenEntity.JWT_ID, jwtId).firstResultOptional();
	}
}
