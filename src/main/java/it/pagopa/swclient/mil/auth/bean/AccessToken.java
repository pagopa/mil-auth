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
import lombok.Getter;

/**
 * 
 * @author Antonio Tarricone
 */
@RegisterForReflection
@JsonInclude(Include.NON_NULL)
@Getter
public class AccessToken {
	/*
	 * access_token
	 */
	@JsonProperty("access_token")
	private String accessTokenProper;

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
	 * @param accessTokenProper
	 * @param refreshToken
	 * @param expiresIn
	 */
	public AccessToken(String accessTokenProper, String refreshToken, long expiresIn) {
		this.accessTokenProper = accessTokenProper;
		this.refreshToken = refreshToken;
		this.expiresIn = expiresIn;
	}
}