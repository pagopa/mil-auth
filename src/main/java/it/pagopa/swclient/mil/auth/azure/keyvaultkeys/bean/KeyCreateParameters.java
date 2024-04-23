/*
 * KeyCreateParameters.java
 *
 * 10 apr 2024
 */
package it.pagopa.swclient.mil.auth.azure.keyvaultkeys.bean;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * The key create parameters.
 * 
 * @see <a href=
 *      "https://learn.microsoft.com/en-us/rest/api/keyvault/keys/create-key/create-key?view=rest-keyvault-keys-7.4&tabs=HTTP#keycreateparameters">Microsoft
 *      Azure Documentation</a>
 * 
 * @author Antonio Tarricone
 */
@RegisterForReflection
@Getter
@Setter
@Accessors(chain = true)
@JsonInclude(value = Include.NON_NULL)
public class KeyCreateParameters {
	/*
	 * 
	 */
	public static final String KTY = "kty";
	public static final String ATTRIBUTES = "attributes";
	public static final String CRV = "crv";
	public static final String KEY_OPS = "key_ops";
	public static final String KEY_SIZE = "key_size";
	public static final String PUBLIC_EXPONENT = "public_exponent";
	public static final String RELEASE_POLICY = "release_policy";
	public static final String TAGS = "tags";

	/*
	 * The type of key to create.
	 */
	@JsonProperty(KTY)
	private JsonWebKeyType kty;

	/*
	 * The attributes of a key managed by the key vault service.
	 */
	@JsonProperty(ATTRIBUTES)
	private KeyAttributes attributes;

	/*
	 * Elliptic curve name.
	 */
	@JsonProperty(CRV)
	private JsonWebKeyCurveName crv;

	/*
	 * JSON web key operations.
	 */
	@JsonProperty(KEY_OPS)
	private List<JsonWebKeyOperation> keyOps;

	/*
	 * The key size in bits.
	 */
	@JsonProperty(KEY_SIZE)
	private Integer keySize;

	/*
	 * The public exponent for a RSA key.
	 */
	@JsonProperty(PUBLIC_EXPONENT)
	private Integer publicExponent;

	/*
	 * The policy rules under which the key can be exported.
	 */
	@JsonProperty(RELEASE_POLICY)
	private KeyReleasePolicy releasePolicy;

	/*
	 * Application specific metadata in the form of key-value pairs.
	 */
	@JsonProperty(TAGS)
	private Map<String, String> tags;
}