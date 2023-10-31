/*
 * BasicKey.java
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
 * @author Antonio Tarricone
 */
@RegisterForReflection
@NoArgsConstructor
@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BasicKey extends Key {
	/*
	 *
	 */
	@JsonProperty("kid")
	private String kid;

	/**
	 * @param kid
	 * @param attributes
	 */
	public BasicKey(String kid, KeyAttributes attributes) {
		super(attributes);
		this.kid = kid;
	}
}