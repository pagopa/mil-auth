/*
 * ClientConverter.java
 *
 * 8 ago 2024
 */
package it.pagopa.swclient.mil.auth.admin.util;

import it.pagopa.swclient.mil.auth.admin.bean.Client;
import it.pagopa.swclient.mil.auth.dao.ClientEntity;

/**
 * 
 * @author antonio.tarricone
 */
public class ClientConverter {
	/**
	 * 
	 */
	private ClientConverter() {
	}

	/**
	 * 
	 * @param entity
	 * @return
	 */
	public static Client convert(ClientEntity entity) {
		return new Client(
			entity.getClientId(),
			entity.getChannel(),
			entity.getSalt(),
			entity.getSecretHash(),
			entity.getDescription(),
			entity.getSubject());
	}
}
