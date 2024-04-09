/*
 * ClientsRepository.java
 *
 * 29 mar 2023
 */
package it.pagopa.swclient.mil.auth.service.client;

import io.smallrye.mutiny.Uni;
import it.pagopa.swclient.mil.auth.bean.Client;

/**
 * 
 * @author Antonio Tarricone
 */
public interface ClientsRepository {
	/**
	 * 
	 * @param clientId
	 * @return
	 */
	public Uni<Client> getClient(String clientId);
}