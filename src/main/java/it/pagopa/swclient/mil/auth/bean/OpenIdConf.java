/*
 * OpenIdConf.java
 *
 * 14 nov 2023
 */
package it.pagopa.swclient.mil.auth.bean;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @author Antonio Tarricone
 */
@RegisterForReflection
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OpenIdConf {
	/*
	 * 
	 */
	public static final String ISSUER = "issuer";
	public static final String TOKEN_ENDPOINT = "token_endpoint";
	public static final String JWKS_URI = "jwks_uri";
	
	/*
	 * issuer
	 */
	@JsonProperty(ISSUER)
	private String issuer;

	/*
	 * token_endpoint
	 */
	@JsonProperty(TOKEN_ENDPOINT)
	private String tokenEndpoint;

	/*
	 * jwks_uri
	 */
	@JsonProperty(JWKS_URI)
	private String jwksUri;
}