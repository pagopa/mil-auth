/*
 * KeyBundle.java
 *
 * 11 apr 2024
 */
package it.pagopa.swclient.mil.auth.azure.keyvaultkeys.bean;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * A KeyBundle consisting of a WebKey plus its attributes.
 * 
 * @see <a href=
 *      "https://learn.microsoft.com/en-us/rest/api/keyvault/keys/create-key/create-key?view=rest-keyvault-keys-7.4&tabs=HTTP#keybundle">Microsoft
 *      Azure Documentation</a>
 * @author Antonio Tarricone
 */
@RegisterForReflection
@Getter
@Setter
@Accessors(chain = true)
@JsonInclude(value = Include.NON_NULL)
public class KeyBundle {
	/*
	 * 
	 */
	public static final String ATTRIBUTES = "attributes";
	public static final String KEY = "key";
	public static final String MANAGED = "managed";
	public static final String RELEASE_POLICY = "release_policy";
	public static final String TAGS = "tags";

	/*
	 * The key management attributes.
	 */
	@JsonProperty(ATTRIBUTES)
	private KeyAttributes attributes;

	/*
	 * The Json web key.
	 */
	@JsonProperty(KEY)
	private JsonWebKey key;

	/*
	 * True if the key's lifetime is managed by key vault. If this is a key backing a certificate, then
	 * managed will be true.
	 */
	@JsonProperty(MANAGED)
	private Boolean managed;

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
