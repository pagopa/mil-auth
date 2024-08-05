/*
 * TokenInfoRequest.java
 *
 * 24 mag 2024
 */
package it.pagopa.swclient.mil.auth.bean;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.quarkus.runtime.annotations.RegisterForReflection;
import it.pagopa.swclient.mil.auth.AuthErrorCode;
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
@NoArgsConstructor
@AllArgsConstructor
@Data
@Accessors(chain = true)
public class TokenInfoRequest {
	/*
	 * Token
	 */
	@JsonProperty(JsonPropertyName.TOKEN)
	@NotNull(message = AuthErrorCode.TOKEN_MUST_NOT_BE_NULL_MSG)
	@Pattern(regexp = AuthValidationPattern.TOKEN, message = AuthErrorCode.TOKEN_MUST_MATCH_REGEXP_MSG)
	@ToString.Exclude
	private String token;
}
