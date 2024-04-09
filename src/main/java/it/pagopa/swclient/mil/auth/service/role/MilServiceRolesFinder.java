/*
 * MilServiceRolesFinder.java
 *
 * 3 apr 2024
 */
package it.pagopa.swclient.mil.auth.service.role;

import io.smallrye.mutiny.Uni;
import it.pagopa.swclient.mil.auth.bean.AccessTokenRequest;
import it.pagopa.swclient.mil.auth.bean.Client;
import it.pagopa.swclient.mil.auth.bean.Roles;
import it.pagopa.swclient.mil.auth.qualifier.channel.MilService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 * This has the responsibility to retrieve roles for clients that represent a MIL microservice.
 * 
 * @author Antonio Tarricone
 */
@MilService
@ApplicationScoped
public class MilServiceRolesFinder implements LazyRolesFinder {
	/*
	 * Rolel repository.
	 */
	private RolesRepository repository;
	
	/**
	 * Constructor.
	 * 
	 * @param repository
	 */
	@Inject
	MilServiceRolesFinder(RolesRepository repository) {
		this.repository = repository;
	}

	/**
	 * @see it.pagopa.swclient.mil.auth.service.role.LazyRolesFinder#getRoles(Client, AccessTokenRequest)
	 */
	@Override
	public Uni<Roles> getRoles(Client client, AccessTokenRequest accessTokenRequest) {
		return repository.getMilRoles(client.getSub());
	}
}