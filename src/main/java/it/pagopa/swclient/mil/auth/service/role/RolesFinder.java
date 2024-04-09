/*
 * RolesFinder.java
 *
 * 30 apr 2023
 */
package it.pagopa.swclient.mil.auth.service.role;

import io.smallrye.mutiny.Uni;
import it.pagopa.swclient.mil.auth.bean.AccessTokenRequest;
import it.pagopa.swclient.mil.auth.bean.Client;
import it.pagopa.swclient.mil.auth.bean.Roles;
import it.pagopa.swclient.mil.auth.qualifier.channel.ChannelQualifier;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Any;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;

/**
 * 
 * @author Antonio Tarricone
 */
@ApplicationScoped
public class RolesFinder implements LazyRolesFinder {
	/*
	 *
	 */
	private Instance<LazyRolesFinder> lazyFinder;

	/**
	 * Constructor.
	 * 
	 * @param lazyFinder
	 */
	@Inject
	RolesFinder(@Any Instance<LazyRolesFinder> lazyFinder) {
		this.lazyFinder = lazyFinder;
	}

	/**
	 * @see LazyRolesFinder#getRoles(Client, AccessTokenRequest)
	 */
	@Override
	public Uni<Roles> getRoles(Client client, AccessTokenRequest accessTokenRequest) {
		return lazyFinder.select(ChannelQualifier.get(client.getChannel()))
			.get()
			.getRoles(client, accessTokenRequest);
	}
}