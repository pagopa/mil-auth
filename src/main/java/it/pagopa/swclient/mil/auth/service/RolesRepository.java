/*
 * AuthRolesRepository.java
 *
 * 29 mar 2024
 */
package it.pagopa.swclient.mil.auth.service;

import io.smallrye.mutiny.Uni;
import it.pagopa.swclient.mil.auth.bean.Roles;

/**
 * 
 * @author Antonio Tarricone
 */
public interface RolesRepository {
	/**
	 * 
	 * @param acquirerId
	 * @param channel
	 * @param merchantId
	 * @param clientId
	 * @param terminalId
	 * @return
	 */
	public Uni<Roles> getRoles(String acquirerId, String channel, String clientId, String merchantId, String terminalId);
}