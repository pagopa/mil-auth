/*
 * User.java
 *
 * 20 mar 2023
 */
package it.pagopa.swclient.mil.auth.dao;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 
 * @author Antonio Tarricone
 */
@RegisterForReflection
@NoArgsConstructor
@AllArgsConstructor
@Data
@Accessors(chain = true)
public class UserEntity {
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