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
import lombok.experimental.Accessors;

/**
 * @author Antonio Tarricone
 */
@RegisterForReflection
@NoArgsConstructor
@AllArgsConstructor
@Data
@Accessors(chain = true)
public class OpenIdConf {
	/*
	 * issuer
	 */
	@JsonProperty(AuthJsonPropertyName.ISSUER)
	private String issuer;

	/*
	 * token_endpoint
	 */
	@JsonProperty(AuthJsonPropertyName.TOKEN_ENDPOINT)
	private String tokenEndpoint;

	/*
	 * jwks_uri
	 */
	@JsonProperty(AuthJsonPropertyName.JWKS_URI)
	private String jwksUri;
}