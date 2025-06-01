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

	/*
	 * id_token_signing_alg_values_supported
	 */
	@JsonProperty(AuthJsonPropertyName.ID_TOKEN_SIGN_ALG_VALUES_SUPPORTED)
	private String[] idTokenSignAlgValuesSupported;

	/*
	 * token_endpoint_auth_signing_alg_values_supported
	 */
	@JsonProperty(AuthJsonPropertyName.TOKEN_ENDPOINT_AUTH_SIGN_ALG_VALUES_SUPPORTED)
	private String[] tokenEndpointAuthSignAlgValuesSupported;

	/*
	 * request_object_signing_alg_values_supported
	 */
	@JsonProperty(AuthJsonPropertyName.REQ_OBJ_SIGN_ALG_VALUES_SUPPORTED)
	private String[] reqObjSignAlgValuesSupported;

}