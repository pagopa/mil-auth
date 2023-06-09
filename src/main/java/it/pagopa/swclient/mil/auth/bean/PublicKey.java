/*
 * PublicKey.java
 *
 * 21 mar 2023
 */
package it.pagopa.swclient.mil.auth.bean;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 
 * @author Antonio Tarricone
 */
@RegisterForReflection
@Data
@AllArgsConstructor
public class PublicKey {
	/*
	 * Public exponent
	 */
	private String e;

	/*
	 * Public key use
	 */
	private KeyUse use;

	/*
	 * Key ID
	 */
	private String kid;

	/*
	 * Modulus
	 */
	private String n;

	/*
	 * Key type
	 */
	private KeyType kty;

	/*
	 * Expiration time
	 */
	private long exp;

	/*
	 * Issued at
	 */
	private long iat;

	/**
	 * 
	 * @param publicKey
	 */
	public PublicKey(PublicKey publicKey) {
		this.e = publicKey.e;
		this.use = publicKey.use;
		this.kid = publicKey.kid;
		this.n = publicKey.n;
		this.kty = publicKey.kty;
		this.exp = publicKey.exp;
		this.iat = publicKey.iat;
	}
}