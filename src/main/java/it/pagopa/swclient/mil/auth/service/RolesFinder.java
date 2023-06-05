/*
 * RolesFinder.java
 *
 * 30 apr 2023
 */
package it.pagopa.swclient.mil.auth.service;

import static it.pagopa.swclient.mil.auth.ErrorCode.ERROR_SEARCHING_FOR_ROLES;
import static it.pagopa.swclient.mil.auth.ErrorCode.ROLES_NOT_FOUND;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
import it.pagopa.swclient.mil.auth.bean.Role;
import it.pagopa.swclient.mil.auth.client.AuthDataRepository;
import it.pagopa.swclient.mil.auth.util.AuthError;
import it.pagopa.swclient.mil.auth.util.AuthException;
import it.pagopa.swclient.mil.auth.util.UniGenerator;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

/**
 * 
 * @author Antonio Tarricone
 */
@ApplicationScoped
public class RolesFinder {
	/*
	 * Role repository.
	 */
	@RestClient
	AuthDataRepository repository;

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
	 * @param clientId
	 * @param merchantId
	 * @param terminalId
	 * @return
	 */
	private Uni<Role> find(String acquirerId, String channel, String clientId, String merchantId, String terminalId) {
		Log.debugf("Search for the roles with acquirerId=%s, channel=%s, clientId=%s, merchantId=%s, terminalId=%s.", acquirerId, channel, clientId, merchantId, terminalId);
		return repository.getRoles(
			replaceNullWithNa(acquirerId),
			replaceNullWithNa(channel),
			clientId,
			replaceNullWithNa(merchantId),
			replaceNullWithNa(terminalId))
			.onFailure().transform(t -> {
				if (t instanceof WebApplicationException) {
					WebApplicationException e = (WebApplicationException) t;
					Response r = e.getResponse();
					// r is always not null
					// if (r != null) {
					if (r.getStatus() == 404) {
						String message = String.format("[%s] Roles not found.", ROLES_NOT_FOUND);
						Log.warn(message);
						return new AuthException(ROLES_NOT_FOUND, message);
					} else {
						String message = String.format("[%s] Error searching for the roles.", ERROR_SEARCHING_FOR_ROLES);
						Log.errorf(t, message);
						return new AuthError(ERROR_SEARCHING_FOR_ROLES, message);
					}
					// } else {
					// String message = String.format("[%s] Error searching for the roles.", ERROR_SEARCHING_FOR_ROLES);
					// Log.errorf(t, message);
					// return new AuthError(ERROR_SEARCHING_FOR_ROLES, message);
					// }
				} else {
					String message = String.format("[%s] Error searching for the roles.", ERROR_SEARCHING_FOR_ROLES);
					Log.errorf(t, message);
					return new AuthError(ERROR_SEARCHING_FOR_ROLES, message);
				}
			})
			.chain(r -> {
				return UniGenerator.item(r);
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
		return find(acquirerId, channel, clientId, merchantId, terminalId)
			.onFailure(AuthException.class)
			.recoverWithUni(t -> {
				if (terminalId != null) {
					/*
					 * If there are no roles for acquirer/channel/client/merchant/terminal, search for
					 * acquirer/channel/client/merchant (without terminal).
					 */
					return find(acquirerId, channel, clientId, merchantId, "NA");
				} else {
					return Uni.createFrom().failure(t);
				}
			});
	}
}