/*
 * GetAccessTokenRequest.java
 *
 * 16 mar 2023
 */
package it.pagopa.swclient.mil.auth.bean;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.nimbusds.jwt.SignedJWT;

import io.quarkus.runtime.annotations.RegisterForReflection;
import it.pagopa.swclient.mil.ErrorCode;
import it.pagopa.swclient.mil.auth.AuthErrorCode;
import it.pagopa.swclient.mil.auth.validation.constraints.ValidationTarget;
import it.pagopa.swclient.mil.bean.HeaderParamName;
import it.pagopa.swclient.mil.bean.ValidationPattern;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.ws.rs.CookieParam;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.HeaderParam;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @author Antonio Tarricone
 */
@RegisterForReflection
@ValidationTarget(message = AuthErrorCode.INCONSISTENT_REQUEST_MSG)
@NoArgsConstructor
@AllArgsConstructor
@Data
@Accessors(chain = true)
public class GetAccessTokenRequest {
	/*
	 * Acquirer ID assigned by PagoPA
	 */
	@HeaderParam(HeaderParamName.ACQUIRER_ID)
	@Pattern(regexp = ValidationPattern.ACQUIRER_ID, message = ErrorCode.ACQUIRER_ID_MUST_MATCH_REGEXP_MSG)
	private String acquirerId;

	/*
	 * Channel originating the request
	 */
	@HeaderParam(HeaderParamName.CHANNEL)
	@Pattern(regexp = ValidationPattern.CHANNEL, message = ErrorCode.CHANNEL_MUST_MATCH_REGEXP_MSG)
	private String channel;

	/*
	 * Merchant ID originating the transaction. If Channel equals to POS, MerchantId must not be null.
	 */
	@HeaderParam(HeaderParamName.MERCHANT_ID)
	@Pattern(regexp = ValidationPattern.MERCHANT_ID, message = ErrorCode.MERCHANT_ID_MUST_MATCH_REGEXP_MSG)
	private String merchantId;

	/*
	 * ID of the terminal originating the transaction. It must be unique per acquirer, channel and
	 * merchant if present.
	 */
	@HeaderParam(HeaderParamName.TERMINAL_ID)
	@Pattern(regexp = ValidationPattern.TERMINAL_ID, message = ErrorCode.TERMINAL_ID_MUST_MATCH_REGEXP_MSG)
	private String terminalId;

	/*
	 * grant_type
	 */
	@FormParam(AuthFormParamName.GRANT_TYPE)
	@NotNull(message = AuthErrorCode.GRANT_TYPE_MUST_NOT_BE_NULL_MSG)
	@Pattern(regexp = AuthValidationPattern.GRANT_TYPE, message = AuthErrorCode.GRANT_TYPE_MUST_MATCH_REGEXP_MSG)
	private String grantType;

	/*
	 * username
	 */
	@FormParam(AuthFormParamName.USERNAME)
	@Pattern(regexp = AuthValidationPattern.USERNAME, message = AuthErrorCode.USERNAME_MUST_MATCH_REGEXP_MSG)
	@ToString.Exclude
	private String username;

	/*
	 * password
	 */
	@FormParam(AuthFormParamName.PASSWORD)
	@Pattern(regexp = AuthValidationPattern.PASSWORD, message = AuthErrorCode.PASSWORD_MUST_MATCH_REGEXP_MSG)
	@ToString.Exclude
	private String password;

	/*
	 * refresh_token
	 */
	@FormParam(AuthFormParamName.REFRESH_TOKEN)
	@ToString.Exclude
	private SignedJWT refreshToken;

	/*
	 * client_id
	 */
	@FormParam(AuthFormParamName.CLIENT_ID)
	@NotNull(message = AuthErrorCode.CLIENT_ID_MUST_NOT_BE_NULL_MSG)
	@Pattern(regexp = AuthValidationPattern.CLIENT_ID, message = AuthErrorCode.CLIENT_ID_MUST_MATCH_REGEXP_MSG)
	private String clientId;

	/*
	 * scope
	 */
	@FormParam(AuthFormParamName.SCOPE)
	@Pattern(regexp = AuthValidationPattern.SCOPE, message = AuthErrorCode.SCOPE_MUST_MATCH_REGEXP_MSG)
	private String scope;

	/*
	 * client_secret
	 */
	@FormParam(AuthFormParamName.CLIENT_SECRET)
	@Pattern(regexp = AuthValidationPattern.CLIENT_SECRET, message = AuthErrorCode.CLIENT_SECRET_MUST_MATCH_REGEXP_MSG)
	@ToString.Exclude
	private String clientSecret;

	/*
	 * fiscal_code
	 */
	@FormParam(AuthFormParamName.FISCAL_CODE)
	@Pattern(regexp = AuthValidationPattern.FISCAL_CODE, message = AuthErrorCode.FISCAL_CODE_MUST_MATCH_REGEXP_MSG)
	@ToString.Exclude
	private String fiscalCode;

	/*
	 * cookie
	 */
	@FormParam(AuthFormParamName.RETURN_THE_REFRESH_TOKEN_IN_THE_COOKIE)
	private boolean returnTheRefreshTokenInTheCookie;

	/*
	 * refresh_cookie
	 */
	@CookieParam(AuthCookieParamName.REFRESH_COOKIE)
	private SignedJWT refreshCookie;

	/**
	 * 
	 * @return
	 */
	public SignedJWT getTheRefreshToken() {
		if (refreshCookie == null) {
			return refreshToken;
		} else {
			return refreshCookie;
		}
	}

	/**
	 * 
	 * @return
	 */
	public boolean isTheRefreshTokenInTheCookie() {
		return refreshCookie != null;
	}
	
	/**
	 * 
	 * @return
	 */
	public String allToJsonString() {
		try {
			return new ObjectMapper().writeValueAsString(this);
		} catch (JsonProcessingException e) {
			return e.getMessage();
		}
	}
}