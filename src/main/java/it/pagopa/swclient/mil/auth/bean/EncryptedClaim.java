/*
 * EncryptedClaim.java
 *
 * 24 mag 2024
 */
package it.pagopa.swclient.mil.auth.bean;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.JsonWebKeyEncryptionAlgorithm;
import lombok.EqualsAndHashCode;
import lombok.Getter;
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
@ToString
@EqualsAndHashCode
public class EncryptedClaim {
	/**
	 * Kid of the key used to encrypt.
	 */
	private String kid;

	/**
	 * Algorithm used to encrypt. See {@link JsonWebKeyEncryptionAlgorithm}
	 */
	private String alg;

	/**
	 * Encrypted value.
	 */
	private byte[] value;

	/**
	 * 
	 * @param map
	 * @return
	 */
	public EncryptedClaim fromMap(Map<String, Object> map) {
		kid = Objects.toString(map.get("kid"));
		alg = Objects.toString(map.get("alg"));
		value = null;

		Object valueObj = map.get("value");
		if (valueObj != null) {
			try {
				value = Base64.getUrlDecoder().decode(valueObj.toString());
			} catch (IllegalArgumentException e) {
			}
		}

		return this;
	}

	/**
	 * 
	 * @return
	 */
	public Map<String, String> toMap() {
		HashMap<String, String> map = new HashMap<>();
		map.put("kid", kid);
		map.put("alg", alg);
		map.put("value", value != null ? Base64.getUrlEncoder().encodeToString(value) : null);
		return map;
	}
}