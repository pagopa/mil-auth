/*
 * RoleEntity.java
 *
 * 20 mar 2023
 */
package it.pagopa.swclient.mil.auth.bean;

import java.util.List;

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