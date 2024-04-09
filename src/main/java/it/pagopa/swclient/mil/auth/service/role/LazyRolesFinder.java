/*
 * LazyRolesFinder.java
 *
 * 3 apr 2024
 */
package it.pagopa.swclient.mil.auth.service.role;

import io.smallrye.mutiny.Uni;
import it.pagopa.swclient.mil.auth.bean.AccessTokenRequest;
import it.pagopa.swclient.mil.auth.bean.Client;
import it.pagopa.swclient.mil.auth.bean.Roles;

/**
 * 
 * @author Antonio Tarricone
 */
public interface LazyRolesFinder {
	/**
	 * 
	 * @param client
	 * @param accessTokenRequest
	 * @return
	 */
	public Uni<Roles> getRoles(Client client, AccessTokenRequest accessTokenRequest);
}