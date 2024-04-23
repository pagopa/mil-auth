/*
 * JsonWebKeyType.java
 *
 * 10 apr 2024
 */
package it.pagopa.swclient.mil.auth.azure.keyvaultkeys.bean;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * JsonWebKey Key Type (kty), as defined in
 * https://tools.ietf.org/html/draft-ietf-jose-json-web-algorithms-40.
 * 
 * @see <a href=
 *      "https://learn.microsoft.com/en-us/rest/api/keyvault/keys/create-key/create-key?view=rest-keyvault-keys-7.4&tabs=HTTP#jsonwebkeytype">Microsoft
 *      Azure Documentation</a>
 * @author Antonio Tarricone
 */
public enum JsonWebKeyType {
	/*
	 * Elliptic Curve.
	 */
	@JsonProperty("EC")
	EC("EC"),

	/*
	 * Elliptic Curve with a private key which is stored in the HSM.
	 */
	@JsonProperty("EC-HSM")
	EC_HSM("EC-HSM"),

	/*
	 * RSA (https://tools.ietf.org/html/rfc3447).
	 */
	@JsonProperty("RSA")
	RSA("RSA"),

	/*
	 * RSA with a private key which is stored in the HSM.
	 */
	@JsonProperty("RSA-HSM")
	RSA_HSM("RSA-HSM"),

	/*
	 * Octet sequence (used to represent symmetric keys).
	 */
	@JsonProperty("oct")
	OCT("oct"),

	/*
	 * Octet sequence (used to represent symmetric keys) which is stored the HSM.
	 */
	@JsonProperty("oct-HSM")
	OCT_HSM("oct-HSM");

	/*
	 * 
	 */
	private final String value;

	/**
	 * 
	 * @param value
	 */
	private JsonWebKeyType(String value) {
		this.value = value;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return value;
	}
}
