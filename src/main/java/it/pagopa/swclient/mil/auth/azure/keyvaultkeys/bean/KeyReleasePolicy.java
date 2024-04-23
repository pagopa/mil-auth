/*
 * KeyReleasePolicy.java
 *
 * 11 apr 2024
 */
package it.pagopa.swclient.mil.auth.azure.keyvaultkeys.bean;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * The policy rules under which the key can be exported.
 * 
 * @see <a href=
 *      "https://learn.microsoft.com/en-us/rest/api/keyvault/keys/create-key/create-key?view=rest-keyvault-keys-7.4&tabs=HTTP#keyreleasepolicy">Microsoft
 *      Azure Documentation</a>
 * @author Antonio Tarricone
 */
@RegisterForReflection
@Getter
@Setter
@Accessors(chain = true)
@JsonInclude(value = Include.NON_NULL)
public class KeyReleasePolicy {
	/*
	 * 
	 */
	public static final String CONTENT_TYPE = "contentType";
	public static final String DATA = "data";
	public static final String IMMUTABLE = "immutable";

	/*
	 * Content type and version of key release policy. Default value: application/json; charset=utf-8
	 */
	@JsonProperty(CONTENT_TYPE)
	private String contentType;

	/*
	 * Blob encoding the policy rules under which the key can be released. Blob must be base64 URL
	 * encoded.
	 */
	@JsonProperty(DATA)
	private String data;

	/*
	 * Defines the mutability state of the policy. Once marked immutable, this flag cannot be reset and
	 * the policy cannot be changed under any circumstances.
	 */
	@JsonProperty(IMMUTABLE)
	private Boolean immutable;
}