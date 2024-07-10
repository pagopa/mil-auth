/*
 * RolesFinder.java
 *
 * 30 apr 2023
 */
package it.pagopa.swclient.mil.auth.service;

import io.quarkus.cache.CacheResult;
import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
import it.pagopa.swclient.mil.auth.AuthErrorCode;
import it.pagopa.swclient.mil.auth.dao.RolesEntity;
import it.pagopa.swclient.mil.auth.dao.RolesRepository;
import it.pagopa.swclient.mil.auth.util.AuthError;
import it.pagopa.swclient.mil.auth.util.AuthException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 * 
 * @author Antonio Tarricone
 */
@ApplicationScoped
public class RolesFinder {
	/*
	 *
	 */
	private static final String NA = "NA";

	/*
	 * Roles repository.
	 */
	private RolesRepository repository;

	/**
	 * 
	 * @param repository
	 */
	@Inject
	RolesFinder(RolesRepository repository) {
		this.repository = repository;
	}

	/**
	 * @param s
	 * @return
	 */
	private String replaceNullWithNa(String s) {
		return s != null ? s : NA;
	}

	/**
	 * @param acquirerId
	 * @param channel
	 * @param clientId
	 * @param merchantId
	 * @param terminalId
	 * @return
	 */
	private Uni<RolesEntity> find(String acquirerId, String channel, String clientId, String merchantId, String terminalId) {
		Log.tracef("Search (sub) for the roles for acquirerId=%s, channel=%s, clientId=%s, merchantId=%s, terminalId=%s", acquirerId, channel, clientId, merchantId, terminalId);
		return repository.findByFullKey(replaceNullWithNa(acquirerId), replaceNullWithNa(channel), clientId, replaceNullWithNa(merchantId), replaceNullWithNa(terminalId))
			.onFailure().transform(t -> {
				String message = String.format("[%s] Error searching for the roles for ", AuthErrorCode.ERROR_SEARCHING_FOR_ROLES);
				Log.errorf(t, message);
				return new AuthError(AuthErrorCode.ERROR_SEARCHING_FOR_ROLES, message);
			})
			.invoke(rolesEntity -> {
				if (rolesEntity == null) {
					String message = String.format("[%s] roles for acquirerId=%s, channel=%s, clientId=%s, merchantId=%s, terminalId=%s not found", AuthErrorCode.ROLES_NOT_FOUND, acquirerId, channel, clientId, merchantId, terminalId);
					Log.warn(message);
					throw new AuthException(AuthErrorCode.ROLES_NOT_FOUND, message);
				} else {
					Log.debugf("Roles for acquirerId=%s, channel=%s, clientId=%s, merchantId=%s, terminalId=%s found: %s", acquirerId, channel, clientId, merchantId, terminalId, rolesEntity);
				}
			});
	}

	/**
	 * Finds roles.
	 *
	 * @param acquirerId
	 * @param channel
	 * @param clientId
	 * @param merchantId
	 * @param terminalId
	 * @return
	 */
	@CacheResult(cacheName = "client-role")
	public Uni<RolesEntity> findRoles(String acquirerId, String channel, String clientId, String merchantId, String terminalId) {
		Log.tracef("Search (main) for the roles for acquirerId=%s, channel=%s, clientId=%s, merchantId=%s, terminalId=%s", acquirerId, channel, clientId, merchantId, terminalId);
		return find(acquirerId, channel, clientId, merchantId, terminalId)
			.onFailure(AuthException.class)
			.recoverWithUni(t -> {
				if (terminalId != null) {
					/*
					 * If there are no roles for acquirer/channel/client/merchant/terminal, search for
					 * acquirer/channel/client/merchant (without terminal).
					 */
					return find(acquirerId, channel, clientId, merchantId, NA).onFailure(AuthException.class)
						.recoverWithUni(tt -> {
							if (merchantId != null) {
								/*
								 * If there are no roles for acquirer/channel/client/merchant (without terminal), search for
								 * acquirer/channel/client (without terminal and merchant).
								 */
								return find(acquirerId, channel, clientId, NA, NA);
							} else {
								return Uni.createFrom().failure(tt);
							}
						});
				} else {
					if (merchantId != null) {
						/*
						 * If there are no roles for acquirer/channel/client/merchant (without terminal), search for
						 * acquirer/channel/client (without terminal and merchant).
						 */
						return find(acquirerId, channel, clientId, NA, NA);
					} else {
						return Uni.createFrom().failure(t);
					}
				}
			});
	}
}