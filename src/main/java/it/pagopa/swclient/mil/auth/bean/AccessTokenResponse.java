/*
 * AccessTokenResponse.java
 *
 * 16 mar 2023
 */
package it.pagopa.swclient.mil.auth.bean;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Response to the request to get or refresh the access token.
 * 
 * @author Antonio Tarricone
 */
@RegisterForReflection
@JsonInclude(Include.NON_NULL)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccessTokenResponse {
	/*
	 * 
	 */
	public static final String ACCESS_TOKEN = "access_token";
	public static final String REFRESH_TOKEN = "refresh_token";
	public static final String TOKEN_TYPE = "token_type";
	public static final String EXPIRES_IN = "expires_in";
	
	/*
	 * access_token
	 */
	@JsonProperty(ACCESS_TOKEN)
	private String accessToken;

	/*
	 * refresh_token
	 */
	@JsonProperty(REFRESH_TOKEN)
	private String refreshToken;

	/*
	 * token_type
	 */
	@JsonProperty(TOKEN_TYPE)
	private String tokenType = TokenType.BEARER;

	/*
	 * expires_in
	 */
	@JsonProperty(EXPIRES_IN)
	private long expiresIn;

	/**
	 * 
	 * @param accessToken
	 * @param refreshToken
	 * @param expiresIn
	 */
	public AccessTokenResponse(String accessToken, String refreshToken, long expiresIn) {
		this.accessToken = accessToken;
		this.refreshToken = refreshToken;
		this.expiresIn = expiresIn;
	}
	
	/**
	 * 
	 * @return
	 */
	@ToString.Include(name="accessToken")
	private String maskAccessToken() {
	  return accessToken == null ? null : "*****";
	}
	
	/**
	 * 
	 * @return
	 */
	@ToString.Include(name="refreshToken")
	private String maskRefreshToken() {
	  return refreshToken == null ? null : "*****";
	}
}