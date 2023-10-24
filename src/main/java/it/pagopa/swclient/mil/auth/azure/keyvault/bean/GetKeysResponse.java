/*
 * GetKeysResponse.java
 *
 * 24 lug 2023
 */
package it.pagopa.swclient.mil.auth.azure.keyvault.bean;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * GetKeysResponse GetKeyVersionsResponse
 * 
 * @author Antonio Tarricone
 */
@RegisterForReflection
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GetKeysResponse {
	/*
	 *
	 */
	@JsonProperty("value")
	private BasicKey[] keys;
}
