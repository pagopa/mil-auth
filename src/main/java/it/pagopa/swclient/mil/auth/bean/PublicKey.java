/*
 * PublicKey.java
 *
 * 21 mar 2023
 */
package it.pagopa.swclient.mil.auth.bean;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import io.quarkus.runtime.annotations.RegisterForReflection;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.util.ByteArrayDeserializer;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.util.ByteArraySerializer;
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
@JsonInclude(value = Include.NON_NULL)
public class PublicKey {
	/*
	 * Public exponent
	 */
	@JsonProperty(AuthJsonPropertyName.EXPONENT)
	@JsonSerialize(using = ByteArraySerializer.class)
	@JsonDeserialize(using = ByteArrayDeserializer.class)
	private byte[] e;

	/*
	 * Public key use
	 */
	@JsonProperty(AuthJsonPropertyName.USE)
	private String use;

	/*
	 * Key ID
	 */
	@JsonProperty(AuthJsonPropertyName.KID)
	private String kid;

	/*
	 * Modulus
	 */
	@JsonProperty(AuthJsonPropertyName.MODULUS)
	@JsonSerialize(using = ByteArraySerializer.class)
	@JsonDeserialize(using = ByteArrayDeserializer.class)
	private byte[] n;

	/*
	 * Key type
	 */
	@JsonProperty(AuthJsonPropertyName.TYPE)
	private String kty;

	/*
	 * Expiration time
	 */
	@JsonProperty(AuthJsonPropertyName.EXPIRATION)
	private long exp;

	/*
	 * Issued at
	 */
	@JsonProperty(AuthJsonPropertyName.ISSUED_AT)
	private long iat;
}