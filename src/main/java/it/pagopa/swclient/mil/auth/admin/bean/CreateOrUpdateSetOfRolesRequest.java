/*
 * CreateOrUpdateSetOfRolesRequest.java
 *
 * 18 ago 2024
 */
package it.pagopa.swclient.mil.auth.admin.bean;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.quarkus.runtime.annotations.RegisterForReflection;
import it.pagopa.swclient.mil.ErrorCode;
import it.pagopa.swclient.mil.auth.AuthErrorCode;
import it.pagopa.swclient.mil.auth.admin.AdminErrorCode;
import it.pagopa.swclient.mil.auth.bean.AuthValidationPattern;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * <p>
 * DTO of the request to create or update a set of roles.
 * </p>
 * 
 * @author Antonio Tarricone
 */
@RegisterForReflection
@JsonInclude(Include.NON_NULL)
@NoArgsConstructor
@AllArgsConstructor
@Data
@Accessors(chain = true)
public class CreateOrUpdateSetOfRolesRequest {
	/**
	 * <p>
	 * Client ID.
	 * </p>
	 */
	@JsonProperty(value = AdminPathParamName.CLIENT_ID)
	@Pattern(regexp = AuthValidationPattern.CLIENT_ID, message = AuthErrorCode.CLIENT_ID_MUST_MATCH_REGEXP_MSG)
	@NotNull(message = AuthErrorCode.CLIENT_ID_MUST_NOT_BE_NULL_MSG)
	private String clientId;

	/**
	 * <p>
	 * Acquirer ID.
	 * </p>
	 */
	@JsonProperty(value = AdminJsonPropertyName.ACQUIRER_ID)
	@Pattern(regexp = AdminValidationPattern.NA_ACQUIRER_ID, message = ErrorCode.ACQUIRER_ID_MUST_MATCH_REGEXP_MSG)
	@NotNull(message = ErrorCode.ACQUIRER_ID_MUST_NOT_BE_NULL_MSG)
	private String acquirerId;

	/**
	 * <p>
	 * Channel.
	 * </p>
	 */
	@JsonProperty(value = AdminJsonPropertyName.CHANNEL)
	@Pattern(regexp = AdminValidationPattern.NA_CHANNEL, message = ErrorCode.CHANNEL_MUST_MATCH_REGEXP_MSG)
	@NotNull(message = ErrorCode.CHANNEL_MUST_NOT_BE_NULL_MSG)
	private String channel;

	/**
	 * <p>
	 * Merchant ID.
	 * </p>
	 */
	@JsonProperty(value = AdminJsonPropertyName.MERCHANT_ID)
	@Pattern(regexp = AdminValidationPattern.NA_MERCHANT_ID, message = ErrorCode.MERCHANT_ID_MUST_MATCH_REGEXP_MSG)
	@NotNull(message = AdminErrorCode.MERCHANT_ID_MUST_NOT_BE_NULL_MSG)
	private String merchantId;

	/**
	 * <p>
	 * Terminal ID.
	 * </p>
	 */
	@JsonProperty(value = AdminJsonPropertyName.TERMINAL_ID)
	@Pattern(regexp = AdminValidationPattern.NA_TERMINAL_ID, message = ErrorCode.TERMINAL_ID_MUST_MATCH_REGEXP_MSG)
	@NotNull(message = ErrorCode.TERMINAL_ID_MUST_NOT_BE_NULL_MSG)
	private String terminalId;

	/**
	 * <p>
	 * List of roles.
	 * </p>
	 */
	@JsonProperty(value = AdminJsonPropertyName.ROLES)
	@Size(min = 0, max = 32, message = AdminErrorCode.ROLES_SIZE_MUST_BE_BETWEEN_MIN_AND_MAX_MSG)
	@NotNull(message = AdminErrorCode.ROLES_MUST_NOT_BE_NULL_MSG)
	private List<@Pattern(regexp = AdminValidationPattern.ROLE, message = AdminErrorCode.ROLE_MUST_MATCH_REGEXP_MSG) @NotNull(message = AdminErrorCode.ROLE_MUST_NOT_BE_NULL_MSG) String> roles;
}
