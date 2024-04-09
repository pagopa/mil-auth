/*
 * GetAccessTokenRequest.java
 *
 * 16 mar 2023
 */
package it.pagopa.swclient.mil.auth.bean;

import io.quarkus.runtime.annotations.RegisterForReflection;
import it.pagopa.swclient.mil.auth.validation.constraints.ValidationTarget;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.HeaderParam;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Request to get or refresh the access token.
 * 
 * @author Antonio Tarricone
 */
@RegisterForReflection
@ValidationTarget(message = AuthErrorCode.INCONSISTENT_ACCESS_TOKEN_REQUEST_MSG)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccessTokenRequest {
	/*
	 * 
	 */
	public static final String REQUEST_ID = "RequestId";
	public static final String GRANT_TYPE= "grant_type";
	public static final String CLIENT_ID= "client_id";
	public static final String REFRESH_TOKEN= "refresh_token";
	public static final String DEVICE_CODE= "device_code";
	public static final String CLIENT_SECRET= "client_secret";
	public static final String BANK_ID= "bank_id";
	public static final String TERMINAL_ID= "terminal_id";
	public static final String USER_TAX_CODE = "user_code";
	
	/*
	 * Request ID.
	 */
	@HeaderParam(REQUEST_ID)
	@Pattern(regexp = "^[a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12}$", message = AuthErrorCode.REQUEST_ID_MUST_MATCH_REGEXP_MSG)
	private String requestId;

	/*
	 * Grant type to get an access token.
	 */
	@FormParam(GRANT_TYPE)
	@NotNull(message = AuthErrorCode.GRANT_TYPE_MUST_NOT_BE_NULL_MSG)
	@Pattern(regexp = "^(refresh_token|client_credentials|device_code)$", message = AuthErrorCode.GRANT_TYPE_MUST_MATCH_REGEXP_MSG)
	private GrantType grantType;

	/*
	 * Client ID.
	 */
	@FormParam(CLIENT_ID)
	@NotNull(message = AuthErrorCode.CLIENT_ID_MUST_NOT_BE_NULL_MSG)
	@Pattern(regexp = "^[a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12}$", message = AuthErrorCode.CLIENT_ID_MUST_MATCH_REGEXP_MSG)
	private String clientId;

	/*
	 * Refresh token.
	 */
	@FormParam(REFRESH_TOKEN)
	@Pattern(regexp = "^[a-zA-Z0-9_-]{1,1024}\\.[a-zA-Z0-9_-]{1,1024}\\.[a-zA-Z0-9_-]{1,1024}$", message = AuthErrorCode.REFRESH_TOKEN_MUST_MATCH_REGEXP_MSG)
	private String refreshToken;

	/*
	 * Device code.
	 */
	@FormParam(DEVICE_CODE)
	@Pattern(regexp = "^[0-9a-f]{24}$", message = AuthErrorCode.DEVICE_CODE_MUST_MATCH_REGEXP_MSG)
	private String deviceCode;

	/*
	 * Client secret.
	 */
	@FormParam(CLIENT_SECRET)
	@Pattern(regexp = "^[a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12}$", message = AuthErrorCode.CLIENT_SECRET_MUST_MATCH_REGEXP_MSG)
	private String clientSecret;

	/*
	 * ID of the Bank assigned by the ABI.
	 */
	@FormParam(BANK_ID)
	@Pattern(regexp = "^\\d{5}$", message = AuthErrorCode.BANK_ID_MUST_MATCH_REGEXP_MSG)
	private String bankId;

	/*
	 * ID of the ATM. It must be unique per Bank ID.
	 */
	@FormParam(TERMINAL_ID)
	@Pattern(regexp = "^\\d{4,9}$", message = "[" + AuthErrorCode.TERMINAL_ID_MUST_MATCH_REGEXP_MSG)
	private String terminalId;

	/*
	 * User tax code (a.k.a. fiscal code)
	 */
	@FormParam(USER_TAX_CODE)
	@Pattern(regexp = "^(([A-Z]{6}\\d{2}[A-Z]\\d{2}[A-Z]\\d{3}[A-Z])|(\\d{11}))$", message = AuthErrorCode.USER_TAX_CODE_MUST_MATCH_REGEXP_MSG)
	private String userCode;
	
	
	/**
	 * 
	 * @return
	 */
	@ToString.Include(name="clientSecret")
	private String maskClientSecret() {
	  return clientSecret == null ? null : "*****";
	}
	
	/**
	 * 
	 * @return
	 */
	@ToString.Include(name="userCode")
	private String maskUserCode() {
	  return userCode == null ? null : "*****";
	}
}