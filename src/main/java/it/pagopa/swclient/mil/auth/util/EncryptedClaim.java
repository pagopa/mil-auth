/*
 * EncryptedClaim.java
 *
 * 24 mag 2024
 */
package it.pagopa.swclient.mil.auth.util;

import java.util.Base64;

import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.JsonWebKeyEncryptionAlgorithm;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * 
 * @author Antonio Tarricone
 */
@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class EncryptedClaim {
	/**
	 * Kid of the key used to encrypt.
	 */
	private String kid;

	/**
	 * Algorithm used to encrypt.
	 */
	private JsonWebKeyEncryptionAlgorithm alg;

	/**
	 * Base64 URL-safe string of encrypted value.
	 */
	private String value;

	/**
	 * 
	 * @param bytes
	 * @return
	 */
	public EncryptedClaim setValue(byte[] bytes) {
		value = Base64.getUrlEncoder().encodeToString(bytes);
		return this;
	}
}