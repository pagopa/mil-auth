/*
 * TokenInfoResponse.java
 *
 * 24 mag 2024
 */
package it.pagopa.swclient.mil.auth.bean;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * 
 * @author Antonio Tarricone
 */
@RegisterForReflection
@JsonInclude(Include.NON_NULL)
@Getter
@Setter
@Accessors(chain = true)
@EqualsAndHashCode
public class TokenInfoResponse {
	/*
	 * Fiscal code
	 */
	@JsonProperty(JsonPropertyName.FISCAL_CODE)
	private String fiscalCode;
}
