/*
 * GetAccessTokenResponse.java
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
	private String accessToken;

	/*
	 * refresh_token
	 */
	@JsonProperty(AuthJsonPropertyName.REFRESH_TOKEN)
	@ToString.Exclude
	private String refreshToken;

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
	public GetAccessTokenResponse(String accessToken, String refreshToken, long expiresIn) {
		this.accessToken = accessToken;
		this.refreshToken = refreshToken;
		this.expiresIn = expiresIn;
	}
}