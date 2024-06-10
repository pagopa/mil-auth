/*
 * User.java
 *
 * 20 mar 2023
 */
package it.pagopa.swclient.mil.auth.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @author Antonio Tarricone
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Accessors(chain = true)
public class User {
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