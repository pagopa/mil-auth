/*
 * CreateClientRequest.java
 *
 * 25 lug 2024
 */
package it.pagopa.swclient.mil.auth.admin.bean;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.quarkus.runtime.annotations.RegisterForReflection;
import it.pagopa.swclient.mil.ErrorCode;
import it.pagopa.swclient.mil.auth.admin.AuthAdminErrorCode;
import it.pagopa.swclient.mil.bean.HeaderParamName;
import it.pagopa.swclient.mil.bean.ValidationPattern;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.ws.rs.HeaderParam;
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
public class CreateClientRequest {
	/*
	 * Channel
	 */
	@HeaderParam(HeaderParamName.CHANNEL)
	@Pattern(regexp = ValidationPattern.CHANNEL, message = ErrorCode.CHANNEL_MUST_MATCH_REGEXP_MSG)
	private String channel;

	/*
	 * Client description
	 */
	@JsonProperty(value = AdminJsonPropertyName.DESCRIPTION)
	@NotNull(message = AuthAdminErrorCode.DESCRIPTION_MUST_MATCH_REGEXP_MSG)
	@Pattern(regexp = AuthAdminValidationPattern.DESCRIPTION, message = AuthAdminErrorCode.DESCRIPTION_MUST_MATCH_REGEXP_MSG)
	private String description;

	/*
	 * Subject
	 */
	@JsonProperty(value = AdminJsonPropertyName.SUBJECT)
	@Pattern(regexp = AuthAdminValidationPattern.SUBJECT, message = AuthAdminErrorCode.SUBJECT_MUST_MATCH_REGEXP_MSG)
	private String subject;
}