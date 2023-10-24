/*
 * AuthDataRepository.java
 *
 * 23 ott 2023
 */
package it.pagopa.swclient.mil.auth.service;

import io.smallrye.mutiny.Uni;
import it.pagopa.swclient.mil.auth.bean.Client;
import it.pagopa.swclient.mil.auth.bean.Role;
import it.pagopa.swclient.mil.auth.bean.User;

/**
 * @author Antonio Tarricone
 */
public interface AuthDataRepository {
	/**
	 * @param clientId
	 * @return
	 */
	public Uni<Client> getClient(String clientId);

	/**
	 * @param acquirerId
	 * @param channel
	 * @param merchantId
	 * @param clientId
	 * @param terminalId
	 * @return
	 */
	public Uni<Role> getRoles(String acquirerId, String channel, String clientId, String merchantId, String terminalId);

	/**
	 * @param userHash
	 * @return
	 */
	public Uni<User> getUser(String userHash);
}