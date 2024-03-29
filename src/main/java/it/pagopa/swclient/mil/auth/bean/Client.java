/*
 * Client.java
 *
 * 16 mar 2023
 */
package it.pagopa.swclient.mil.auth.bean;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @author Antonio Tarricone
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@EqualsAndHashCode
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