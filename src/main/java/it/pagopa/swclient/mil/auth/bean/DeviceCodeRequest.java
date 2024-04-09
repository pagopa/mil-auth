/*
 * DeviceCodeRequest.java
 * 
 * 29 mar 2024
 */
package it.pagopa.swclient.mil.auth.bean;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.ws.rs.HeaderParam;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request to get device code and user code.
 * 
 * @author Antonio Tarricone
 */
@RegisterForReflection
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeviceCodeRequest {
	/*
	 * 
	 */
	public static final String REQUEST_ID = "RequestId";
	public static final String CLIENT_ID = "client_id";
	public static final String TERMINAL_HANDLER_ID = "terminal_handler_id";
	public static final String TERMINAL_ID = "terminal_id";

	/*
	 * Request ID.
	 */
	@HeaderParam(REQUEST_ID)
	@Pattern(regexp = "^[a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12}$", message = AuthErrorCode.REQUEST_ID_MUST_MATCH_REGEXP_MSG)
	private String requestId;

	/*
	 * Client ID.
	 */
	@JsonProperty(CLIENT_ID)
	@NotNull(message = AuthErrorCode.CLIENT_ID_MUST_NOT_BE_NULL_MSG)
	@Pattern(regexp = "^[a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12}$", message = AuthErrorCode.CLIENT_ID_MUST_MATCH_REGEXP_MSG)
	private String clientId;

	/*
	 * Terminal handler identifier.
	 */
	@JsonProperty(TERMINAL_HANDLER_ID)
	@NotNull(message = AuthErrorCode.TERMINAL_HANDLER_ID_MUST_NOT_BE_NULL_MSG)
	@Pattern(regexp = "^\\d{5}$", message = AuthErrorCode.TERMINAL_HANDLER_ID_MUST_MATCH_REGEXP_MSG)
	private String terminalHandlerId;

	/*
	 * ID of the POS. It must be unique per terminal handler.
	 */
	@JsonProperty(TERMINAL_ID)
	@NotNull(message = AuthErrorCode.TERMINAL_ID_MUST_NOT_BE_NULL_MSG)
	@Pattern(regexp = "^\\d{8}$", message = AuthErrorCode.TERMINAL_ID_MUST_MATCH_REGEXP_MSG)
	private String terminalId;
}