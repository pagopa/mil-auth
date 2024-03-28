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
import it.pagopa.swclient.mil.auth.bean.Role;
import it.pagopa.swclient.mil.auth.util.AuthError;
import it.pagopa.swclient.mil.auth.util.AuthException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

/**
 * @author Antonio Tarricone
 */
@ApplicationScoped
public class RolesFinder {
	/*
	 *
	 */
	private static final String NA = "NA";
	
	/*
	 * Role repository.
	 */
	private AuthDataRepository repository;
	
	/**
	 * Constructor.
	 * 
	 * @param repository
	 */
	@Inject
	RolesFinder(AuthDataRepository repository) {
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
	@CacheResult(cacheName = "role-cache")
	public Uni<Role> getRoles(String acquirerId, String channel, String clientId, String merchantId, String terminalId) {
		return repository.getRoles(acquirerId, channel, clientId, merchantId, terminalId);
	}

	/**
	 * @param acquirerId
	 * @param channel
	 * @param clientId
	 * @param merchantId
	 * @param terminalId
	 * @return
	 */
	private Uni<Role> find(String acquirerId, String channel, String clientId, String merchantId, String terminalId) {
		Log.debugf("Search (sub) for the roles with acquirerId=[%s], channel=[%s], clientId=[%s], merchantId=[%s], terminalId=[%s].", acquirerId, channel, clientId, merchantId, terminalId);
		return getRoles(replaceNullWithNa(acquirerId), replaceNullWithNa(channel), clientId, replaceNullWithNa(merchantId), replaceNullWithNa(terminalId))
			.invoke(role -> Log.debugf("Roles found: [%s]", role))
			.onFailure().transform(t -> {
				if (t instanceof WebApplicationException e) {
					Response r = e.getResponse();
					// r cannot be null
					if (r.getStatus() == 404) {
						String message = String.format("[%s] Roles not found.", AuthErrorCode.ROLES_NOT_FOUND);
						Log.warn(message);
						return new AuthException(AuthErrorCode.ROLES_NOT_FOUND, message);
					} else {
						String message = String.format("[%s] Error searching for the roles.", AuthErrorCode.ERROR_SEARCHING_FOR_ROLES);
						Log.errorf(t, message);
						return new AuthError(AuthErrorCode.ERROR_SEARCHING_FOR_ROLES, message);
					}
				} else {
					String message = String.format("[%s] Error searching for the roles.", AuthErrorCode.ERROR_SEARCHING_FOR_ROLES);
					Log.errorf(t, message);
					return new AuthError(AuthErrorCode.ERROR_SEARCHING_FOR_ROLES, message);
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
	public Uni<Role> findRoles(String acquirerId, String channel, String clientId, String merchantId, String terminalId) {
		Log.debugf("Search (main) for the roles for acquirerId=[%s], channel=[%s], clientId=[%s], merchantId=[%s], terminalId=[%s].", acquirerId, channel, clientId, merchantId, terminalId);
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