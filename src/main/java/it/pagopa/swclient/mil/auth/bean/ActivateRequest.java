/*
 * ActivateRequest.java
 * 
 * 02 apr 2024
 */
package it.pagopa.swclient.mil.auth.bean;

import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.ws.rs.FormParam;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request to activate a device.
 * 
 * @author Antonio Tarricone
 */
@RegisterForReflection
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActivateRequest {
	/*
	 * 
	 */
	public static final String USER_CODE = "user_code";
	
	/*
	 * Base20 user code to authenticate the terminal.
	 */
	@FormParam(USER_CODE)
	@NotNull(message = AuthErrorCode.USER_CODE_MUST_NOT_BE_NULL_MSG)
	@Pattern(regexp = "^[BCDFGHJKLMNPQRSTVWXZ]{8}$", message = AuthErrorCode.USER_CODE_MUST_MATCH_REGEXP_MSG)
	private String userCode;
}