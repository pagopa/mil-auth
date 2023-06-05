/*
 * RoleEntity.java
 *
 * 20 mar 2023
 */
package it.pagopa.swclient.mil.auth.bean;

import java.util.List;

/**
 * 
 * @author Antonio Tarricone
 */
@SuppressWarnings("unused")
public class Role {
	/*
	 * 
	 */
	private String acquirerId;

	/*
	 * 
	 */
	private String channel;

	/*
	 * 
	 */
	private String clientId;

	/*
	 * 
	 */
	private String merchantId;

	/*
	 * 
	 */
	private String terminalId;

	/*
	 * 
	 */
	private List<String> roles;

	/**
	 * 
	 * @param acquirerId
	 * @param channel
	 * @param clientId
	 * @param merchantId
	 * @param terminalId
	 * @param roles
	 */
	public Role(String acquirerId, String channel, String clientId, String merchantId, String terminalId, List<String> roles) {
		this.acquirerId = acquirerId;
		this.channel = channel;
		this.clientId = clientId;
		this.merchantId = merchantId;
		this.terminalId = terminalId;
		this.roles = roles;
	}

	/**
	 * 
	 * @return the roles
	 */
	public List<String> getRoles() {
		return roles;
	}
}