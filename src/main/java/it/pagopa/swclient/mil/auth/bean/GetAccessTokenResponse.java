/*
 * GetAccessTokenResponse.java
 *
 * 16 mar 2023
 */
package it.pagopa.swclient.mil.auth.bean;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.nimbusds.jwt.SignedJWT;

import io.quarkus.runtime.annotations.RegisterForReflection;
import it.pagopa.swclient.mil.auth.util.SignedJWTSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @author Antonio Tarricone
 */
@RegisterForReflection
@JsonInclude(Include.NON_NULL)
@NoArgsConstructor
@AllArgsConstructor
@Data
@Accessors(chain = true)
public class GetAccessTokenResponse {
	/*
	 * access_token
	 */
	@JsonProperty(AuthJsonPropertyName.ACCESS_TOKEN)
	@ToString.Exclude
	@JsonSerialize(using = SignedJWTSerializer.class)
	private SignedJWT accessToken;

	/*
	 * refresh_token
	 */
	@JsonProperty(AuthJsonPropertyName.REFRESH_TOKEN)
	@ToString.Exclude
	@JsonSerialize(using = SignedJWTSerializer.class)
	private SignedJWT refreshToken;

	/*
	 * token_type
	 */
	@JsonProperty(AuthJsonPropertyName.TOKEN_TYPE)
	private String tokenType = TokenType.BEARER;

	/*
	 * expires_in
	 */
	@JsonProperty(AuthJsonPropertyName.EXPIRES_IN)
	private long expiresIn;

	/**
	 * @param accessToken
	 * @param refreshToken
	 * @param expiresIn
	 */
	public GetAccessTokenResponse(SignedJWT accessToken, SignedJWT refreshToken, long expiresIn) {
		this.accessToken = accessToken;
		this.refreshToken = refreshToken;
		this.expiresIn = expiresIn;
	}
}