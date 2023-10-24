/*
 * DetailedKey.java
 *
 * 19 set 2023
 */
package it.pagopa.swclient.mil.auth.azure.keyvault.bean;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * CreateKeyResponse GetKeyResponse
 * 
 * @author Antonio Tarricone
 */
@RegisterForReflection
@NoArgsConstructor
@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DetailedKey extends Key {
	/*
	 *
	 */
	@JsonProperty("key")
	private KeyDetails details;

	/**
	 * @param details
	 * @param attributes
	 */
	public DetailedKey(KeyDetails details, KeyAttributes attributes) {
		super(attributes);
		this.details = details;
	}
}