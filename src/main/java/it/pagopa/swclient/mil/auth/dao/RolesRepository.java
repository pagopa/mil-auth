/*
 * RolesRepository.java
 *
 * 9 lug 2024
 */
package it.pagopa.swclient.mil.auth.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.bson.Document;

import io.quarkus.logging.Log;
import io.quarkus.mongodb.panache.reactive.ReactivePanacheMongoRepository;
import io.quarkus.panache.common.Sort;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.tuples.Tuple2;
import it.pagopa.swclient.mil.observability.TraceReactivePanacheMongoRepository;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * 
 * @author Antonio Tarricone
 */
@TraceReactivePanacheMongoRepository
@ApplicationScoped
public class RolesRepository implements ReactivePanacheMongoRepository<SetOfRolesEntity> {
	/*
	 * 
	 */
	static final String FIND_BY_FULL_KEY = String.format(
		"%s = ?1 and %s = ?2 and %s = ?3 and %s = ?4 and %s = ?5",
		SetOfRolesEntity.ACQUIRER_ID_PRP,
		SetOfRolesEntity.CHANNEL_PRP,
		SetOfRolesEntity.CLIENT_ID_PRP,
		SetOfRolesEntity.MERCHANT_ID_PRP,
		SetOfRolesEntity.TERMINAL_ID_PRP);

	/**
	 * 
	 * @param page
	 * @param size
	 * @return
	 */
	public Uni<List<SetOfRolesEntity>> findAll(int page, int size) {
		return findAll(
			Sort.ascending(
				SetOfRolesEntity.CLIENT_ID_PRP,
				SetOfRolesEntity.ACQUIRER_ID_PRP,
				SetOfRolesEntity.CHANNEL_PRP,
				SetOfRolesEntity.MERCHANT_ID_PRP,
				SetOfRolesEntity.TERMINAL_ID_PRP))
			.page(page, size)
			.list();
	}

	/**
	 * 
	 * @param acquirerId
	 * @param channel
	 * @param clientId
	 * @param merchantId
	 * @param terminalId
	 * @return
	 */
	public Uni<Optional<SetOfRolesEntity>> findByFullKey(String acquirerId, String channel, String clientId, String merchantId, String terminalId) {
		return find(
			FIND_BY_FULL_KEY,
			acquirerId,
			channel,
			clientId,
			merchantId,
			terminalId)
			.firstResultOptional();
	}

	/**
	 * 
	 * @param id
	 * @return
	 */
	public Uni<Optional<SetOfRolesEntity>> findBySetOfRolesId(String id) {
		return find(SetOfRolesEntity.ID_PRP, id).firstResultOptional();
	}

	/**
	 * 
	 * @param page
	 * @param size
	 * @param acquirerId
	 * @param channel
	 * @param clientId
	 * @param merchantId
	 * @param terminalId
	 * @return
	 */
	public Uni<Tuple2<Long, List<SetOfRolesEntity>>> findByParameters(int page, int size, String acquirerId, String channel, String clientId, String merchantId, String terminalId) {
		Map<String, Object> parameters = new HashMap<>();
		parameters.put(SetOfRolesEntity.ACQUIRER_ID_PRP, acquirerId);
		parameters.put(SetOfRolesEntity.CHANNEL_PRP, channel);
		parameters.put(SetOfRolesEntity.CLIENT_ID_PRP, clientId);
		parameters.put(SetOfRolesEntity.MERCHANT_ID_PRP, merchantId);
		parameters.put(SetOfRolesEntity.TERMINAL_ID_PRP, terminalId);

		Log.debugf("Parameters: ", parameters);

		String query = parameters
			.entrySet()
			.stream()
			.filter(e -> e.getValue() != null)
			.map(e -> String.format("%s = :%s", e.getKey(), e.getKey()))
			.collect(Collectors.joining(" and "));

		Log.debugf("Query: ", query);

		if (query.isEmpty()) {
			return Uni.combine()
				.all()
				.unis(
					count(),
					findAll(
						Sort.ascending(
							SetOfRolesEntity.CLIENT_ID_PRP,
							SetOfRolesEntity.ACQUIRER_ID_PRP,
							SetOfRolesEntity.CHANNEL_PRP,
							SetOfRolesEntity.MERCHANT_ID_PRP,
							SetOfRolesEntity.TERMINAL_ID_PRP))
						.page(page, size)
						.list())
				.asTuple();
		} else {
			return Uni.combine()
				.all()
				.unis(
					count(query, parameters),
					find(
						query,
						Sort.ascending(
							SetOfRolesEntity.CLIENT_ID_PRP,
							SetOfRolesEntity.ACQUIRER_ID_PRP,
							SetOfRolesEntity.CHANNEL_PRP,
							SetOfRolesEntity.MERCHANT_ID_PRP,
							SetOfRolesEntity.TERMINAL_ID_PRP),
						parameters)
						.page(page, size)
						.list())
				.asTuple();
		}
	}

	/**
	 * 
	 * @param id
	 * @param acquirerId
	 * @param channel
	 * @param clientId
	 * @param merchantId
	 * @param terminalId
	 * @param roles
	 * @return
	 */
	public Uni<Long> updateBySetOfRolesId(String id, String acquirerId, String channel, String clientId, String merchantId, String terminalId, List<String> roles) {
		return update(
			new Document("$set", new Document()
				.append(SetOfRolesEntity.ACQUIRER_ID_PRP, acquirerId)
				.append(SetOfRolesEntity.CHANNEL_PRP, channel)
				.append(SetOfRolesEntity.CLIENT_ID_PRP, clientId)
				.append(SetOfRolesEntity.MERCHANT_ID_PRP, merchantId)
				.append(SetOfRolesEntity.TERMINAL_ID_PRP, terminalId)
				.append(SetOfRolesEntity.ROLES_PRP, roles)))
			.where(SetOfRolesEntity.ID_PRP, id);
	}

	/**
	 * 
	 * @param id
	 * @return
	 */
	public Uni<Long> deleteBySetOfRolesId(String id) {
		return delete(SetOfRolesEntity.ID_PRP, id);
	}
}
