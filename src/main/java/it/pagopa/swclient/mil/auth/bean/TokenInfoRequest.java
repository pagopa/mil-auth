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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * 
 * @author Antonio Tarricone
 */
@RegisterForReflection
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Accessors(chain = true)
public class TokenInfoRequest {
	/*
	 * Token
	 */
	@JsonProperty(JsonPropertyName.TOKEN)
	@NotNull(message = "[" + AuthErrorCode.TOKEN_MUST_NOT_BE_NULL + "] " + FormParamName.TOKEN + " must not be null")
	@Pattern(regexp = "^[a-zA-Z0-9_-]{1,1024}\\.[a-zA-Z0-9_-]{1,1024}\\.[a-zA-Z0-9_-]{1,1024}$", message = "[" + AuthErrorCode.TOKEN_MUST_MATCH_REGEXP + "] " + FormParamName.TOKEN + " must match \"{regexp}\"")
	private String token;
}
