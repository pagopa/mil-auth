/*
 * TokenizationRequest.java
 *
 * 8 apr 2024
 */
package it.pagopa.swclient.mil.auth.bean;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 
 * @author Antonio Tarricone
 */
@RegisterForReflection
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TokenizationRequest {
	/*
	 * 
	 */
	public static final String PII = "pii";
	
	/*
	 * 
	 */
	@JsonProperty(PII)
	private String pii;
	
	/**
	 * 
	 * @return
	 */
	@ToString.Include(name="pii")
	private String maskPii() {
	  return pii == null ? null : "*****";
	}
}