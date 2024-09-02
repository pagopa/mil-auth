/*
 * RolesConverter.java
 *
 * 19 ago 2024
 */
package it.pagopa.swclient.mil.auth.admin.util;

import it.pagopa.swclient.mil.auth.admin.bean.SetOfRoles;
import it.pagopa.swclient.mil.auth.dao.SetOfRolesEntity;

/**
 * 
 * @author antonio.tarricone
 */
public class RolesConverter {
	/**
	 * 
	 */
	private RolesConverter() {
	}

	/**
	 * 
	 * @param entity
	 * @return
	 */
	public static SetOfRoles convert(SetOfRolesEntity entity) {
		return new SetOfRoles(
			entity.getSetOfRolesId(),
			entity.getAcquirerId(),
			entity.getChannel(),
			entity.getClientId(),
			entity.getMerchantId(),
			entity.getTerminalId(),
			entity.getRoles());
	}
}
