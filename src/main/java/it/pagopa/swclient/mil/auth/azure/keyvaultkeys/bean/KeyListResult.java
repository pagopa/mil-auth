/*
 * KeyListResult.java
 *
 * 11 apr 2024
 */
package it.pagopa.swclient.mil.auth.azure.keyvaultkeys.bean;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * The key list result.
 * 
 * @see <a href=
 *      "https://learn.microsoft.com/en-us/rest/api/keyvault/keys/get-keys/get-keys?view=rest-keyvault-keys-7.4&tabs=HTTP#keylistresult">Microsoft
 *      Azure Documentation</a>
 * @author Antonio Tarricone
 */
@RegisterForReflection
@Getter
@Setter
@Accessors(chain = true)
@JsonInclude(value = Include.NON_NULL)
public class KeyListResult {
	/*
	 * 
	 */
	public static final String NEXT_LINK = "nextLink";
	public static final String VALUE = "value";

	/*
	 * The URL to get the next set of keys.
	 */
	@JsonProperty(NEXT_LINK)
	private String nextLink;

	/*
	 * A response message containing a list of keys in the key vault along with a link to the next page
	 * of keys.
	 */
	@JsonProperty(VALUE)
	private List<KeyItem> value;
}