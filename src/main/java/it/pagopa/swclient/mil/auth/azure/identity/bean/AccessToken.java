/*
 * GetAccessTokenResponse.java
 *
 * 21 lug 2023
 */
package it.pagopa.swclient.mil.auth.azure.identity.bean;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * 
 * @author Antonio Tarricone
 */
@RegisterForReflection
@Getter
@Setter
@Accessors(chain = true)
@JsonInclude(value = Include.NON_NULL)
public class AccessToken {
	/*
	 * 
	 */
	public static final String TYPE = "token_type";
	public static final String EXPIRES_ON = "expires_on";
	public static final String CLIENT_ID = "client_id";
	public static final String RESOURCE = "resource";
	public static final String ACCESS_TOKEN = "access_token";

	/*
	 *
	 */
	@JsonProperty(TYPE)
	private String type;

	/*
	 *
	 */
	@JsonProperty(value = EXPIRES_ON, required = true)
	private long expiresOn;

	/*
	 *
	 */
	@JsonProperty(CLIENT_ID)
	private String clientId;

	/*
	 *
	 */
	@JsonProperty(RESOURCE)
	private String resource;

	/*
	 *
	 */
	@JsonProperty(value = ACCESS_TOKEN, required = true)
	private String accessToken;
}