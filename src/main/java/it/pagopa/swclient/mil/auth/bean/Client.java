/*
 * Client.java
 *
 * 16 mar 2023
 */
package it.pagopa.swclient.mil.auth.bean;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Antonio Tarricone
 */
@AllArgsConstructor
@Getter
public class Client {
	/*
	 *
	 */
	private String id;

	/*
	 *
	 */
	private String channel;

	/*
	 *
	 */
	private String salt;

	/*
	 *
	 */
	private String secretHash;

	/*
	 *
	 */
	private String description;
}