/*
 * KeyItem.java
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
 * The key item containing key metadata.
 * 
 * @see <a href=
 *      "https://learn.microsoft.com/en-us/rest/api/keyvault/keys/get-keys/get-keys?view=rest-keyvault-keys-7.4&tabs=HTTP#keyitem">Microsoft
 *      Azure Documentation</a>
 * @author Antonio Tarricone
 */
@RegisterForReflection
@Getter
@Setter
@Accessors(chain = true)
@JsonInclude(value = Include.NON_NULL)
public class KeyItem {
	/*
	 * 
	 */
	private static final String ATTRIBUTES = "attributes";
	private static final String KID = "kid";
	private static final String MANAGED = "managed";
	private static final String TAGS = "tags";

	/*
	 * The key management attributes.
	 */
	@JsonProperty(ATTRIBUTES)
	private KeyAttributes attributes;

	/*
	 * Key identifier.
	 */
	@JsonProperty(KID)
	private String kid;

	/*
	 * True if the key's lifetime is managed by key vault. If this is a key backing a certificate, then
	 * managed will be true.
	 */
	@JsonProperty(MANAGED)
	private Boolean managed;

	/*
	 * Application specific metadata in the form of key-value pairs.
	 */
	@JsonProperty(TAGS)
	private Map<String, String> tags;
}