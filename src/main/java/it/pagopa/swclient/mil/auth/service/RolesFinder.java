/*
 * RolesFinder.java
 *
 * 30 apr 2023
 */
package it.pagopa.swclient.mil.auth.service;

import java.util.Map;
import java.util.Optional;

import org.bson.Document;

import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
import it.pagopa.swclient.mil.auth.dao.RoleEntity;
import it.pagopa.swclient.mil.auth.dao.RoleRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 * 
 * @author Antonio Tarricone
 */
@ApplicationScoped
public class RolesFinder {
	/*
	 * Role repository.
	 */
	@Inject
	RoleRepository roleRepository;

	/**
	 * 
	 * @param s
	 * @return
	 */
	private String replaceNullWithNa(String s) {
		return s != null ? s : "NA";
	}

	/**
	 *
	 * @param acquirerId
	 * @param channel
	 * @param merchantId
	 * @param clientId
	 * @param terminalId
	 * @return
	 */
	private Uni<Optional<RoleEntity>> find(String acquirerId, String channel, String merchantId, String clientId, String terminalId) {
		Document criterion = new Document(Map.of(
			"acquirerId", replaceNullWithNa(acquirerId),
			"channel", replaceNullWithNa(channel),
			"merchantId", replaceNullWithNa(merchantId),
			"clientId", clientId,
			"terminalId", replaceNullWithNa(terminalId)));

		Log.debugf("Search for the roles with %s.", criterion.toString());

		return roleRepository.findSingleResultOptional(criterion);
	}

	/**
	 * Finds roles.
	 * 
	 * @param acquirerId
	 * @param channel
	 * @param merchantId
	 * @param clientId
	 * @param terminalId
	 * @return
	 */
	public Uni<Optional<RoleEntity>> findRoles(String acquirerId, String channel, String merchantId, String clientId, String terminalId) {
		return find(acquirerId, channel, merchantId, clientId, terminalId).chain(o -> {
			if (o.isPresent() || terminalId.equals("NA")) {
				return Uni.createFrom().item(o);
			} else {
				/*
				 * If there are no roles for acquirer/channel/merchant/client/terminal, search with
				 * acquirer/channel/merchant/client.
				 */
				return find(acquirerId, channel, merchantId, clientId, "NA");
			}
		});
	}
}