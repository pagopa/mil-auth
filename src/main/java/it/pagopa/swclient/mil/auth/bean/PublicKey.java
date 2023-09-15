/*
 * PublicKey.java
 *
 * 21 mar 2023
 */
package it.pagopa.swclient.mil.auth.bean;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author Antonio Tarricone
 */
@RegisterForReflection
@Data
@AllArgsConstructor
public class PublicKey {
	/*
	 * Public exponent
	 */
	@JsonProperty(JsonPropertyName.EXPONENT)
	private String e;

	/*
	 * Public key use
	 */
	@JsonProperty(JsonPropertyName.USE)
	private KeyUse use;

	/*
	 * Key ID
	 */
	@JsonProperty(JsonPropertyName.KID)
	private String kid;

	/*
	 * Modulus
	 */
	@JsonProperty(JsonPropertyName.MODULUS)
	private String n;

	/*
	 * Key type
	 */
	@JsonProperty(JsonPropertyName.TYPE)
	private KeyType kty;

	/*
	 * Expiration time
	 */
	@JsonProperty(JsonPropertyName.EXPIRATION)
	private long exp;

	/*
	 * Issued at
	 */
	@JsonProperty(JsonPropertyName.ISSUED_AT)
	private long iat;
}