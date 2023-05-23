/*
 * AccessToken.java
 *
 * 16 mar 2023
 */
package it.pagopa.swclient.mil.auth.bean;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.quarkus.runtime.annotations.RegisterForReflection;

/**
 * 
 * @author Antonio Tarricone
 */
@RegisterForReflection
@JsonInclude(Include.NON_NULL)
public class AccessToken {
	/*
	 * access_token
	 */
	@JsonProperty("access_token")
	private String accessToken;

	/*
	 * refresh_token
	 */
	@JsonProperty("refresh_token")
	private String refreshToken;

	/*
	 * token_type
	 */
	@JsonProperty("token_type")
	private String tokenType = "Bearer";

	/*
	 * expires_in
	 */
	@JsonProperty("expires_in")
	private long expiresIn;

	/**
	 * 
	 * @param accessToken
	 * @param refreshToken
	 * @param expiresIn
	 */
	public AccessToken(String accessToken, String refreshToken, long expiresIn) {
		this.accessToken = accessToken;
		this.refreshToken = refreshToken;
		this.expiresIn = expiresIn;
	}

	/**
	 * 
	 * @return the accessToken
	 */
	public String getAccessToken() {
		return accessToken;
	}

	/**
	 * 
	 * @return the refreshToken
	 */
	public String getRefreshToken() {
		return refreshToken;
	}

	/**
	 * 
	 * @return the tokenType
	 */
	public String getTokenType() {
		return tokenType;
	}

	/**
	 * 
	 * @return the expiresIn
	 */
	public long getExpiresIn() {
		return expiresIn;
	}
}