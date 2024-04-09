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
import lombok.NoArgsConstructor;

/**
 * 
 * @author Antonio Tarricone
 */
@RegisterForReflection
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PublicKey {
	/*
	 * 
	 */
	public static final String EXPONENT = "e";
	public static final String USE = "use";
	public static final String KID = "kid";
	public static final String MODULUS = "n";
	public static final String TYPE = "kty";
	public static final String EXPIRATION = "exp";
	public static final String ISSUED_AT = "iat";
	
	/*
	 * Public exponent
	 */
	@JsonProperty(EXPONENT)
	private byte[] e;

	/*
	 * Public key use
	 */
	@JsonProperty(USE)
	private KeyUse use;

	/*
	 * Key ID
	 */
	@JsonProperty(KID)
	private String kid;

	/*
	 * Modulus
	 */
	@JsonProperty(MODULUS)
	private byte[] n;

	/*
	 * Key type
	 */
	@JsonProperty(TYPE)
	private KeyType kty;

	/*
	 * Expiration time
	 */
	@JsonProperty(EXPIRATION)
	private long exp;

	/*
	 * Issued at
	 */
	@JsonProperty(ISSUED_AT)
	private long iat;
}