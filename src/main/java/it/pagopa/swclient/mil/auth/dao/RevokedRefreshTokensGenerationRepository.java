/*
 * RevokedRefreshTokensGenerationRepository.java
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
public class RevokedRefreshTokensGenerationRepository implements ReactivePanacheMongoRepository<RevokedRefreshTokensGenerationEntity> {
	/**
	 * 
	 * @param generationId
	 * @return
	 */
	public Uni<Optional<RevokedRefreshTokensGenerationEntity>> findByGenerationId(String generationId) {
		return find(RevokedRefreshTokensGenerationEntity.GENERATION_ID, generationId).firstResultOptional();
	}
}
