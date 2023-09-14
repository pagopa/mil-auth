/*
 * GetAccessTokenRequest.java
 *
 * 16 mar 2023
 */
package it.pagopa.swclient.mil.auth.bean;

import io.quarkus.runtime.annotations.RegisterForReflection;
import it.pagopa.swclient.mil.ErrorCode;
import it.pagopa.swclient.mil.auth.AuthErrorCode;
import it.pagopa.swclient.mil.auth.validation.constraints.ValidationTarget;
import it.pagopa.swclient.mil.bean.Channel;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.HeaderParam;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @author Antonio Tarricone
 */
@RegisterForReflection
@ValidationTarget(message = "[" + AuthErrorCode.INCONSISTENT_REQUEST + "] Inconsistent request.")
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class GetAccessTokenRequest {
	/*
	 * Request ID
	 */
	@HeaderParam(HeaderParamName.REQUEST_ID)
	@Pattern(regexp = "^[a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12}$", message = ErrorCode.REQUEST_ID_MUST_MATCH_REGEXP_MSG)
	private String requestId;

	/*
	 * Version of the required API
	 */
	@HeaderParam(HeaderParamName.VERSION)
	@Size(max = 64, message = ErrorCode.VERSION_SIZE_MUST_BE_AT_MOST_MAX_MSG)
	@Pattern(regexp = "^(0|[1-9]\\d*)\\.(0|[1-9]\\d*)\\.(0|[1-9]\\d*)(?:-((?:0|[1-9]\\d*|\\d*[a-zA-Z-][0-9a-zA-Z-]*)(?:\\.(?:0|[1-9]\\d*|\\d*[a-zA-Z-][0-9a-zA-Z-]*))*))?(?:\\+([0-9a-zA-Z-]+(?:\\.[0-9a-zA-Z-]+)*))?$", message = ErrorCode.VERSION_MUST_MATCH_REGEXP_MSG)
	private String version;

	/*
	 * Acquirer ID assigned by PagoPA
	 */
	@HeaderParam(HeaderParamName.ACQUIRER_ID)
	@Pattern(regexp = "^\\d{1,11}$", message = ErrorCode.ACQUIRER_ID_MUST_MATCH_REGEXP_MSG)
	private String acquirerId;

	/*
	 * Channel originating the request
	 */
	@HeaderParam(HeaderParamName.CHANNEL)
	@Pattern(regexp = "^(" + Channel.ATM + "|" + Channel.POS + ")$", message = ErrorCode.CHANNEL_MUST_MATCH_REGEXP_MSG)
	private String channel;

	/*
	 * Merchant ID originating the transaction. If Channel equals to POS, MerchantId must not be null.
	 */
	@HeaderParam(HeaderParamName.MERCHANT_ID)
	@Pattern(regexp = "^[0-9a-zA-Z]{1,15}$", message = ErrorCode.MERCHANT_ID_MUST_MATCH_REGEXP_MSG)
	private String merchantId;

	/*
	 * ID of the terminal originating the transaction. It must be unique per acquirer, channel and
	 * merchant if present.
	 */
	@HeaderParam(HeaderParamName.TERMINAL_ID)
	@Pattern(regexp = "^[0-9a-zA-Z]{1,8}$", message = ErrorCode.TERMINAL_ID_MUST_MATCH_REGEXP_MSG)
	private String terminalId;

	/*
	 * grant_type
	 */
	@FormParam(FormParamName.GRANT_TYPE)
	@NotNull(message = "[" + AuthErrorCode.GRANT_TYPE_MUST_NOT_BE_NULL + "] grant_type must not be null")
	@Pattern(regexp = "^" + GrantType.PASSWORD + "|" + GrantType.REFRESH_TOKEN + "|" + GrantType.POYNT_TOKEN + "|" + GrantType.CLIENT_CREDENTIALS + "$", message = "[" + AuthErrorCode.GRANT_TYPE_MUST_MATCH_REGEXP + "] grant_type must match \"{regexp}\"")
	private String grantType;

	/*
	 * username
	 */
	@FormParam(FormParamName.USERNAME)
	@Pattern(regexp = "^[ -~]{1,64}$", message = "[" + AuthErrorCode.USERNAME_MUST_MATCH_REGEXP + "] username must match \"{regexp}\"")
	private String username;

	/*
	 * password
	 */
	@FormParam(FormParamName.PASSWORD)
	@Pattern(regexp = "^[ -~]{1,64}$", message = "[" + AuthErrorCode.PASSWORD_MUST_MATCH_REGEXP + "] password must match \"{regexp}\"")
	private String password;

	/*
	 * refresh_token
	 */
	@FormParam(FormParamName.REFRESH_TOKEN)
	@Pattern(regexp = "^[a-zA-Z0-9_-]{1,1024}\\.[a-zA-Z0-9_-]{1,1024}\\.[a-zA-Z0-9_-]{1,1024}$", message = "[" + AuthErrorCode.REFRESH_TOKEN_MUST_MATCH_REGEXP + "] refresh_token must match \"{regexp}\"")
	private String refreshToken;

	/*
	 * poynt_token
	 */
	@FormParam(FormParamName.EXT_TOKEN)
	@Pattern(regexp = "^[ -~]{1,4096}$", message = "[" + AuthErrorCode.EXT_TOKEN_MUST_MATCH_REGEXP + "] ext_token must match \"{regexp}\"")
	private String extToken;

	/*
	 * add_data
	 */
	@FormParam(FormParamName.ADD_DATA)
	@Pattern(regexp = "^[ -~]{1,4096}$", message = "[" + AuthErrorCode.ADD_DATA_MUST_MATCH_REGEXP + "] add_data must match \"{regexp}\"")
	private String addData;

	/*
	 * client_id
	 */
	@FormParam(FormParamName.CLIENT_ID)
	@NotNull(message = "[" + AuthErrorCode.CLIENT_ID_MUST_NOT_BE_NULL + "] client_id must not be null")
	@Pattern(regexp = "^[a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12}$", message = "[" + AuthErrorCode.CLIENT_ID_MUST_MATCH_REGEXP + "] client_id must match \"{regexp}\"")
	private String clientId;

	/*
	 * scope
	 */
	@FormParam(FormParamName.SCOPE)
	@Pattern(regexp = "^" + Scope.OFFLINE_ACCESS + "$", message = "[" + AuthErrorCode.SCOPE_MUST_MATCH_REGEXP + "] scope must match \"{regexp}\"")
	private String scope;

	/*
	 * client_secret
	 */
	@FormParam(FormParamName.CLIENT_SECRET)
	@Pattern(regexp = "^[a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12}$", message = "[" + AuthErrorCode.CLIENT_SECRET_MUST_MATCH_REGEXP + "] client_secret must match \"{regexp}\"")
	private String clientSecret;
}