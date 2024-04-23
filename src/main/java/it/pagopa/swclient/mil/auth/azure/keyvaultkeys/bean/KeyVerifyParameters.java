/*
 * KeyVerifyParameters.java
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
 * The key verify parameters.
 * 
 * @see <a href=
 *      "https://learn.microsoft.com/en-us/rest/api/keyvault/keys/verify/verify?view=rest-keyvault-keys-7.4&tabs=HTTP#keyverifyparameters">Microsoft
 *      Azure Documentation</a>
 * @author Antonio Tarricone
 */
@RegisterForReflection
@Getter
@Setter
@Accessors(chain = true)
@JsonInclude(value = Include.NON_NULL)
public class KeyVerifyParameters {
	/*
	 * 
	 */
	public static final String ALG = "alg";
	public static final String DIGEST = "digest";
	public static final String VALUE = "value";

	/*
	 * The signing/verification algorithm. For more information on possible algorithm types, see
	 * JsonWebKeySignatureAlgorithm.
	 */
	@JsonProperty(ALG)
	private JsonWebKeySignatureAlgorithm alg;

	/*
	 * The digest used for signing.
	 */
	@JsonProperty(DIGEST)
	@JsonSerialize(using = ByteArraySerializer.class)
	@JsonDeserialize(using = ByteArrayDeserializer.class)
	private byte[] digest;

	/*
	 * The signature to be verified.
	 */
	@JsonProperty(VALUE)
	@JsonSerialize(using = ByteArraySerializer.class)
	@JsonDeserialize(using = ByteArrayDeserializer.class)
	private byte[] value;
}
