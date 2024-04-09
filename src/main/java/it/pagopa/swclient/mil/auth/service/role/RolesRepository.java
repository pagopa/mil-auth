/*
 * RolesRepository.java
 *
 * 29 mar 2024
 */
package it.pagopa.swclient.mil.auth.service.role;

import io.smallrye.mutiny.Uni;
import it.pagopa.swclient.mil.auth.bean.Roles;

/**
 * 
 * @author Antonio Tarricone
 */
public interface RolesRepository {
	/**
	 * 
	 * @param microservice
	 * @return
	 */
	public Uni<Roles> getMilRoles(String microservice);
	
	/**
	 * 
	 * @param subject
	 * @return
	 */
	public Uni<Roles> getServerRoles(String subject);
	
	/**
	 * 
	 * @param payeeCode
	 * @return
	 */
	public Uni<Roles> getPublicAdministrationRoles(String payeeCode);
	
	/**
	 * 
	 * @param subject
	 * @return
	 */
	public Uni<Roles> getPosServiceProviderRoles(String subject);
	
	/**
	 * 
	 * @param bankId
	 * @param terminalId
	 * @return
	 */
	public Uni<Roles> getAtmRoles(String bankId, String terminalId);
	
	/**
	 * 
	 * @param bankId
	 * @return
	 */
	public Uni<Roles> getBankAtmRoles(String bankId);
	
	/**
	 * 
	 * @return
	 */
	public Uni<Roles> getDefaultAtmRoles();
}