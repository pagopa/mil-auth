/*
 * Client.java
 *
 * 16 mar 2023
 */
package it.pagopa.swclient.mil.auth.bean;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @author Antonio Tarricone
 */
@RegisterForReflection
@NoArgsConstructor
@AllArgsConstructor
@Data
@Accessors(chain = true)
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