/*
 * KeyOperationResult.java
 *
 * 11 apr 2024
 */
package it.pagopa.swclient.mil.auth.azure.keyvaultkeys.bean;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import io.quarkus.runtime.annotations.RegisterForReflection;
import it.pagopa.swclient.mil.auth.azure.keyvaultkeys.util.ByteArrayDeserializer;
import it.pagopa.swclient.mil.auth.azure.keyvaultkeys.util.ByteArraySerializer;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * The key operation result.
 * 
 * @see <a href=
 *      "https://learn.microsoft.com/en-us/rest/api/keyvault/keys/sign/sign?view=rest-keyvault-keys-7.4&tabs=HTTP#keyoperationresult">Microsoft
 *      Azure Documentation</a>
 * @author Antonio Tarricone
 */
@RegisterForReflection
@Getter
@Setter
@Accessors(chain = true)
@JsonInclude(value = Include.NON_NULL)
public class KeyOperationResult {
	/*
	 * 
	 */
	public static final String AAD = "aad";
	public static final String IV = "iv";
	public static final String KID = "kid";
	public static final String TAG = "tag";
	public static final String VALUE = "value";

	/*
	 * Additional authenticated data.
	 */
	@JsonProperty(AAD)
	@JsonSerialize(using = ByteArraySerializer.class)
	@JsonDeserialize(using = ByteArrayDeserializer.class)
	private byte[] aad;

	/*
	 * Initialization vector.
	 */
	@JsonProperty(IV)
	@JsonSerialize(using = ByteArraySerializer.class)
	@JsonDeserialize(using = ByteArrayDeserializer.class)
	private byte[] iv;

	/*
	 * Key identifier.
	 */
	@JsonProperty(KID)
	@JsonSerialize(using = ByteArraySerializer.class)
	@JsonDeserialize(using = ByteArrayDeserializer.class)
	private String kid;

	/*
	 * Authentication tag.
	 */
	@JsonProperty(TAG)
	@JsonSerialize(using = ByteArraySerializer.class)
	@JsonDeserialize(using = ByteArrayDeserializer.class)
	private byte[] tag;

	/*
	 * Result.
	 */
	@JsonProperty(VALUE)
	@JsonSerialize(using = ByteArraySerializer.class)
	@JsonDeserialize(using = ByteArrayDeserializer.class)
	private byte[] value;
}