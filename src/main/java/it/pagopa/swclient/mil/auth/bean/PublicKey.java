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
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author Antonio Tarricone
 */
@RegisterForReflection
@Data
@Accessors(chain = true)
@JsonInclude(value = Include.NON_NULL)
public class PublicKey {
	/*
	 * Public exponent
	 */
	@JsonProperty(JsonPropertyName.EXPONENT)
	@JsonSerialize(using = ByteArraySerializer.class)
	@JsonDeserialize(using = ByteArrayDeserializer.class)
	private byte[] e;

	/*
	 * Public key use
	 */
	@JsonProperty(JsonPropertyName.USE)
	private String use;

	/*
	 * Key ID
	 */
	@JsonProperty(JsonPropertyName.KID)
	private String kid;

	/*
	 * Modulus
	 */
	@JsonProperty(JsonPropertyName.MODULUS)
	@JsonSerialize(using = ByteArraySerializer.class)
	@JsonDeserialize(using = ByteArrayDeserializer.class)
	private byte[] n;

	/*
	 * Key type
	 */
	@JsonProperty(JsonPropertyName.TYPE)
	private String kty;

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