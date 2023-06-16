/*
 * RoleEntity.java
 *
 * 20 mar 2023
 */
package it.pagopa.swclient.mil.auth.bean;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 
 * @author Antonio Tarricone
 */
@AllArgsConstructor
@Getter
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
}