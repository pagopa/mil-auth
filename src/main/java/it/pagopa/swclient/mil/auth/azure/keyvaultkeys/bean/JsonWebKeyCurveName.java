/*
 * JsonWebKeyCurveName.java
 *
 * 11 apr 2024
 */
package it.pagopa.swclient.mil.auth.azure.keyvaultkeys.bean;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Elliptic curve name. For valid values, see JsonWebKeyCurveName.
 * 
 * @see <a href=
 *      "https://learn.microsoft.com/en-us/rest/api/keyvault/keys/create-key/create-key?view=rest-keyvault-keys-7.4&tabs=HTTP#jsonwebkeycurvename">Microsoft
 *      Azure Documentation</a>
 * @author Antonio Tarricone
 */
public enum JsonWebKeyCurveName {
	/*
	 * The NIST P-256 elliptic curve, AKA SECG curve SECP256R1.
	 */
	@JsonProperty("P-256")
	P256("P-256"),

	/*
	 * The SECG SECP256K1 elliptic curve.
	 */
	@JsonProperty("P-256K")
	P256K("P-256K"),

	/*
	 * The NIST P-384 elliptic curve, AKA SECG curve SECP384R1.
	 */
	@JsonProperty("P-384")
	P384("P-384"),

	/*
	 * The NIST P-521 elliptic curve, AKA SECG curve SECP521R1.
	 */
	@JsonProperty("P-521")
	P521("P-521");

	/*
	 * 
	 */
	private final String value;

	/**
	 * 
	 * @param value
	 */
	private JsonWebKeyCurveName(String value) {
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
