/*
 * Error.java
 * 
 * 02 apr 2024
 */
package it.pagopa.swclient.mil.auth.bean;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Error details.
 * 
 * @author Antonio Tarricone
 */
@RegisterForReflection
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Error {
	/*
	 * 
	 */
	public static final String CODE = "code";
	public static final String DESCRIPTION = "description";

	/*
	 * Error code.
	 */
	@JsonProperty(CODE)
	private String code;

	/*
	 * Error description.
	 */
	@JsonProperty(DESCRIPTION)
	private String description;
}