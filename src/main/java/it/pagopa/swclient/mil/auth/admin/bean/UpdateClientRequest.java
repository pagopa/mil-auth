/*
 * UpdateClientRequest.java
 *
 * 13 dic 2024
 */
package it.pagopa.swclient.mil.auth.admin.bean;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.quarkus.runtime.annotations.RegisterForReflection;
import it.pagopa.swclient.mil.ErrorCode;
import it.pagopa.swclient.mil.auth.admin.AdminErrorCode;
import it.pagopa.swclient.mil.bean.ValidationPattern;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
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
public class UpdateClientRequest {
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
	 * Client description.
	 * </p>
	 */
	@JsonProperty(value = AdminJsonPropertyName.DESCRIPTION)
	@NotNull(message = AdminErrorCode.DESCRIPTION_MUST_NOT_BE_NULL_MSG)
	@Pattern(regexp = AdminValidationPattern.DESCRIPTION, message = AdminErrorCode.DESCRIPTION_MUST_MATCH_REGEXP_MSG)
	private String description;

	/**
	 * <p>
	 * Client subject.
	 * </p>
	 */
	@JsonProperty(value = AdminJsonPropertyName.SUBJECT)
	@Pattern(regexp = AdminValidationPattern.SUBJECT, message = AdminErrorCode.SUBJECT_MUST_MATCH_REGEXP_MSG)
	private String subject;
}