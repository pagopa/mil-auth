/*
 * TokenizationResponse.java
 *
 * 8 apr 2024
 */
package it.pagopa.swclient.mil.auth.bean;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @author antonio.tarricone
 */
@RegisterForReflection
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TokenizationResponse {
	/*
	 * 
	 */
	public static final String TOKEN = "token";
	
	/*
	 * 
	 */
	@JsonProperty(TOKEN)
	private String token;
}
