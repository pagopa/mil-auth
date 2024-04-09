/*
 * AtmRolesFinder.java
 *
 * 3 apr 2024
 */
package it.pagopa.swclient.mil.auth.service.role;

import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
import it.pagopa.swclient.mil.auth.bean.AccessTokenRequest;
import it.pagopa.swclient.mil.auth.bean.Client;
import it.pagopa.swclient.mil.auth.bean.Roles;
import it.pagopa.swclient.mil.auth.qualifier.channel.Atm;
import it.pagopa.swclient.mil.auth.util.AuthException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 * This has the responsibility to retrieve roles for ATMs.
 * 
 * @author Antonio Tarricone
 */
@Atm
@ApplicationScoped
public class AtmRolesFinder implements LazyRolesFinder {
	/*
	 * Roles repository.
	 */
	private RolesRepository repository;

	/**
	 * Constructor.
	 * 
	 * @param repository
	 */
	@Inject
	AtmRolesFinder(RolesRepository repository) {
		this.repository = repository;
	}

	/**
	 * @see it.pagopa.swclient.mil.auth.service.role.LazyRolesFinder#getRoles(Client,
	 *      AccessTokenRequest)
	 */
	@Override
	public Uni<Roles> getRoles(Client client, AccessTokenRequest accessTokenRequest) {
		String bankId = accessTokenRequest.getBankId();
		String terminalId = accessTokenRequest.getTerminalId();

		Log.debugf("Search roles for ATM [%s/%s]", bankId, terminalId);
		return repository.getAtmRoles(bankId, terminalId)
			.onFailure(AuthException.class).recoverWithUni(() -> {
				Log.debugf("Search roles for ATM of Bank [%s]", bankId);
				return repository.getBankAtmRoles(bankId);
			})
			.onFailure(AuthException.class).recoverWithUni(() -> {
				Log.debug("Search default roles for ATMs");
				return repository.getDefaultAtmRoles();
			})
			.onItem().invoke(roles -> Log.debugf("Roles found for ATM [%s/%s]: [%s]", bankId, terminalId, roles))
			.onFailure(AuthException.class).invoke(t -> Log.warnf(t, "Exception searching roles for ATM [%s/%s]", bankId, terminalId))
			.onFailure(t -> !(t instanceof AuthException)).invoke(t -> Log.warnf(t, "Error searching roles for ATM [%s/%s]", bankId, terminalId));
	}
}