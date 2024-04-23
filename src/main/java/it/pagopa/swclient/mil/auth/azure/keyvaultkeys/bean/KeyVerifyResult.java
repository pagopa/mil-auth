/*
 * KeyVerifyResult.java
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
 * The key verify result.
 * 
 * @see <a href=
 *      "https://learn.microsoft.com/en-us/rest/api/keyvault/keys/verify/verify?view=rest-keyvault-keys-7.4&tabs=HTTP#keyverifyresult">Microsoft
 *      Azure Documentation</a>
 * @author Antonio Tarricone
 */
@RegisterForReflection
@Getter
@Setter
@Accessors(chain = true)
@JsonInclude(value = Include.NON_NULL)
public class KeyVerifyResult {
	/*
	 * 
	 */
	public static final String VALUE = "value";

	/*
	 * True if the signature is verified, otherwise false.
	 */
	@JsonProperty(VALUE)
	private Boolean value;
}
