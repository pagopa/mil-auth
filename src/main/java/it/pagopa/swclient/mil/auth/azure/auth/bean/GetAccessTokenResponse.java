/*
 * GetAccessTokenResponse.java
 *
 * 21 lug 2023
 */
package it.pagopa.swclient.mil.auth.azure.auth.bean;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Antonio Tarricone
 */
@RegisterForReflection
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GetAccessTokenResponse {
	/*
	 *
	 */
	@JsonProperty("token_type")
	private String type;

	/*
	 *
	 */
	@JsonProperty("expires_on")
	private long expiresOn;

	/*
	 *
	 */
	@JsonProperty("client_id")
	private String clientId;
	
	/*
	 *
	 */
	@JsonProperty("resource")
	private String resource;

	/*
	 *
	 */
	@JsonProperty("access_token")
	private String token;
}