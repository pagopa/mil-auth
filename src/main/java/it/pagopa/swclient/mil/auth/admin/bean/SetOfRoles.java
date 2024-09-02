/*
 * SetOfRoles.java
 *
 * 19 ago 2024
 */
package it.pagopa.swclient.mil.auth.admin.bean;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

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
@JsonInclude(Include.NON_NULL)
@NoArgsConstructor
@AllArgsConstructor
@Data
@Accessors(chain = true)
public class SetOfRoles {
	/**
	 * <p>
	 * ID.
	 * </p>
	 */
	@JsonProperty(AdminJsonPropertyName.SET_OF_ROLES_ID)
	private String setOfRolesId;

	/**
	 * <p>
	 * Acquirer ID.
	 * </p>
	 */
	@JsonProperty(AdminJsonPropertyName.ACQUIRER_ID)
	private String acquirerId;

	/**
	 * <p>
	 * Channel.
	 * </p>
	 */
	@JsonProperty(AdminJsonPropertyName.CHANNEL)
	private String channel;

	/**
	 * <p>
	 * Client ID.
	 * </p>
	 */
	@JsonProperty(AdminJsonPropertyName.CLIENT_ID)
	private String clientId;

	/**
	 * <p>
	 * Merchant ID.
	 * </p>
	 */
	@JsonProperty(AdminJsonPropertyName.MERCHANT_ID)
	private String merchantId;

	/**
	 * <p>
	 * Terminal ID.
	 * </p>
	 */
	@JsonProperty(AdminJsonPropertyName.TERMINAL_ID)
	private String terminalId;

	/**
	 * <p>
	 * List of roles.
	 * </p>
	 */
	@JsonProperty(AdminJsonPropertyName.ROLES)
	private List<String> roles;
}