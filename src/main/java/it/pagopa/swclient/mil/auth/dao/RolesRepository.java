/*
 * RolesRepository.java
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
public class RolesRepository implements ReactivePanacheMongoRepository<RolesEntity> {
	/*
	 * 
	 */
	static final String FIND_BY_ALL = String.format(
		"%s = ?1 and %s = ?2 and %s = ?3 and %s = ?4 and %s = ?5",
		RolesEntity.ACQUIRER_ID_PRP,
		RolesEntity.CHANNEL_PRP,
		RolesEntity.CLIENT_ID_PRP,
		RolesEntity.MERCHANT_ID_PRP,
		RolesEntity.TERMINAL_ID_PRP);

	/**
	 * 
	 * @param acquirerId
	 * @param channel
	 * @param clientId
	 * @param merchantId
	 * @param terminalId
	 * @return
	 */
	public Uni<RolesEntity> findByFullKey(String acquirerId, String channel, String clientId, String merchantId, String terminalId) {
		return find(FIND_BY_ALL, acquirerId, channel, clientId, merchantId, terminalId).firstResult();
	}
}
