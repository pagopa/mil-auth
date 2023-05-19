/*
 * GrantsRetriever.java
 *
 * 30 apr 2023
 */
package it.pagopa.swclient.mil.auth.service;

import java.util.Map;
import java.util.Optional;

import org.bson.Document;

import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
import it.pagopa.swclient.mil.auth.dao.GrantEntity;
import it.pagopa.swclient.mil.auth.dao.GrantRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 * 
 * @author Antonio Tarricone
 */
@ApplicationScoped
public class GrantsFinder {
	/*
	 * Grant repository.
	 */
	@Inject
	GrantRepository grantRepository;

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
	private Uni<Optional<GrantEntity>> _findGrants(String acquirerId, String channel, String merchantId, String clientId, String terminalId) {
		Document criterion = new Document(Map.of(
			"acquirerId", replaceNullWithNa(acquirerId),
			"channel", replaceNullWithNa(channel),
			"merchantId", replaceNullWithNa(merchantId),
			"clientId", clientId,
			"terminalId", replaceNullWithNa(terminalId)));

		Log.debugf("Search for the grants with %s.", criterion.toString());

		return grantRepository.findSingleResultOptional(criterion);
	}

	/**
	 * Finds grants.
	 * 
	 * @param acquirerId
	 * @param channel
	 * @param merchantId
	 * @param clientId
	 * @param terminalId
	 * @return
	 */
	public Uni<Optional<GrantEntity>> findGrants(String acquirerId, String channel, String merchantId, String clientId, String terminalId) {
		return _findGrants(acquirerId, channel, merchantId, clientId, terminalId).chain(o -> {
			if (o.isPresent() || terminalId.equals("NA")) {
				return Uni.createFrom().item(o);
			} else {
				/*
				 * If there are no grants for acquirer/channel/merchant/client/terminal, search with
				 * acquirer/channel/merchant/client.
				 */
				return _findGrants(acquirerId, channel, merchantId, clientId, "NA");
			}
		});
	}
}