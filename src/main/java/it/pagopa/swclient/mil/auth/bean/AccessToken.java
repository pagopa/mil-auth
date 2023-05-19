/*
 * AccessToken.java
 *
 * 16 mar 2023
 */
package it.pagopa.swclient.mil.auth.bean;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 
 * @author Antonio Tarricone
 */
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
	 * @param accessToken
	 * @param expiresIn
	 */
	public AccessToken(String accessToken, long expiresIn) {
		this.accessToken = accessToken;
		this.expiresIn = expiresIn;
	}

	/**
	 * 
	 */
	public AccessToken() {
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
	 * @param accessToken the accessToken to set
	 */
	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
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
	 * @param refreshToken the refreshToken to set
	 */
	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
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
	 * @param tokenType the tokenType to set
	 */
	public void setTokenType(String tokenType) {
		this.tokenType = tokenType;
	}

	/**
	 * 
	 * @return the expiresIn
	 */
	public long getExpiresIn() {
		return expiresIn;
	}

	/**
	 * 
	 * @param expiresIn the expiresIn to set
	 */
	public void setExpiresIn(long expiresIn) {
		this.expiresIn = expiresIn;
	}

	/**
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return new StringBuilder("AccessToken [accessToken=")
			.append(accessToken)
			.append(", refreshToken=")
			.append(refreshToken)
			.append(", tokenType=")
			.append(tokenType)
			.append(", expiresIn=")
			.append(expiresIn)
			.append("]")
			.toString();
	}
}