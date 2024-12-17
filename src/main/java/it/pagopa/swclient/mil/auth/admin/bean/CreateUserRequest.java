/*
 * CreateUserRequest.java
 *
 * 20 nov 2024
 */
package it.pagopa.swclient.mil.auth.admin.bean;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.quarkus.runtime.annotations.RegisterForReflection;
import it.pagopa.swclient.mil.ErrorCode;
import it.pagopa.swclient.mil.auth.AuthErrorCode;
import it.pagopa.swclient.mil.auth.admin.AdminErrorCode;
import it.pagopa.swclient.mil.auth.bean.AuthValidationPattern;
import it.pagopa.swclient.mil.bean.ValidationPattern;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
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
public class CreateUserRequest {
	/**
	 * <p>
	 * User name.
	 * </p>
	 */
	@JsonProperty(value = AdminJsonPropertyName.USERNAME)
	@NotNull(message = AdminErrorCode.USERNAME_MUST_NOT_BE_NULL_MSG)
	@Pattern(regexp = AuthValidationPattern.USERNAME, message = AuthErrorCode.USERNAME_MUST_MATCH_REGEXP_MSG)
	@ToString.Exclude
	private String username;

	/**
	 * <p>
	 * Channel.
	 * </p>
	 */
	@JsonProperty(value = AdminJsonPropertyName.CHANNEL)
	@Pattern(regexp = ValidationPattern.CHANNEL, message = ErrorCode.CHANNEL_MUST_MATCH_REGEXP_MSG)
	private String channel;

	/**
	 * <p>
	 * Acquirer ID.
	 * </p>
	 */
	@JsonProperty(value = AdminJsonPropertyName.ACQUIRER_ID)
	@Pattern(regexp = ValidationPattern.ACQUIRER_ID, message = ErrorCode.ACQUIRER_ID_MUST_MATCH_REGEXP_MSG)
	private String acquirerId;

	/**
	 * <p>
	 * Merchant ID.
	 * </p>
	 */
	@JsonProperty(value = AdminJsonPropertyName.MERCHANT_ID)
	@Pattern(regexp = ValidationPattern.MERCHANT_ID, message = ErrorCode.MERCHANT_ID_MUST_MATCH_REGEXP_MSG)
	private String merchantId;
	
	/**
	 * <p>
	 * Client ID.
	 * </p>
	 */
	@JsonProperty(value = AdminJsonPropertyName.CLIENT_ID)
	@NotNull(message = AuthErrorCode.CLIENT_ID_MUST_NOT_BE_NULL_MSG)
	@Pattern(regexp = AuthValidationPattern.CLIENT_ID, message = AuthErrorCode.CLIENT_ID_MUST_MATCH_REGEXP_MSG)
	private String clientId;
}