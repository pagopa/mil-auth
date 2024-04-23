/*
 * JsonWebKeyOperation.java
 *
 * 11 apr 2024
 */
package it.pagopa.swclient.mil.auth.azure.keyvaultkeys.bean;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 
 * @see <a href=
 *      "https://learn.microsoft.com/en-us/rest/api/keyvault/keys/create-key/create-key?view=rest-keyvault-keys-7.4&tabs=HTTP#jsonwebkeyoperation">Microsoft
 *      Azure Documentation</a>
 * @author Antonio Tarricone
 */
public enum JsonWebKeyOperation {
	/*
	 * 
	 */
	@JsonProperty("decrypt")
	DECRYPT("decrypt"),

	@JsonProperty("encrypt")
	ENCRYPT("encrypt"),

	@JsonProperty("export")
	EXPORT("export"),

	@JsonProperty("import")
	IMPORT("import"),

	@JsonProperty("sign")
	SIGN("sign"),

	@JsonProperty("unwrapKey")
	UNWRAP_KEY("unwrapKey"),

	@JsonProperty("verify")
	VERIFY("verify"),

	@JsonProperty("wrapKey")
	WRAP_KEY("wrapKey");

	/*
	 * 
	 */
	private final String value;

	/**
	 * 
	 * @param value
	 */
	private JsonWebKeyOperation(String value) {
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
