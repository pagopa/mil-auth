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
 * @author Antonio Tarricone
 */
@RegisterForReflection
@NoArgsConstructor
@AllArgsConstructor
@Data
public class OpenIdConf {
	/*
	 * issuer
	 */
	@JsonProperty(JsonPropertyName.ISSUER)
	private String issuer;

	/*
	 * token_endpoint
	 */
	@JsonProperty(JsonPropertyName.TOKEN_ENDPOINT)
	private String tokenEndpoint;

	/*
	 * jwks_uri
	 */
	@JsonProperty(JsonPropertyName.JWKS_URI)
	private String jwksUri;
}