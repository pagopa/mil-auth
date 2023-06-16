/*
 * ResourceOwnerCredentialsEntity.java
 *
 * 20 mar 2023
 */
package it.pagopa.swclient.mil.auth.dao;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 
 * FOR DEMO ONLY. THIS WILL BE REPLACED BY DB.
 * 
 * @author Antonio Tarricone
 */
@AllArgsConstructor
@Getter
public class ResourceOwnerCredentialsEntity {
	/*
	 * 
	 */
	private String username;

	/*
	 * 
	 */
	private String salt;

	/*
	 * 
	 */
	private String passwordHash;

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
	private String merchantId;
}