/*
 * KeyPair.java
 *
 * 21 mar 2023
 */
package it.pagopa.swclient.mil.auth.bean;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 
 * @author Antonio Tarricone
 */
@RegisterForReflection
@Data
@NoArgsConstructor
@AllArgsConstructor
public class KeyPair {
	/*
	 * 
	 */
	public static final String PRIVATE_EXPONENT = "d";
	public static final String EXPONENT = "e";
	public static final String USE = "use";
	public static final String KID = "kid";
	public static final String MODULUS = "n";
	public static final String TYPE = "kty";
	public static final String EXPIRATION = "exp";
	public static final String ISSUED_AT = "iat";
	
	/*
	 * Private exponent
	 */
	@JsonProperty(PRIVATE_EXPONENT)
	private String d;

	/*
	 * Public exponent
	 */
	@JsonProperty(EXPONENT)
	private String e;

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
	private String n;

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
	
	/**
	 * 
	 * @return
	 */
	@ToString.Include(name="d")
	private String maskD() {
	  return d == null ? null : "*****";
	}
}