/*
 * PublicKeys.java
 *
 * 21 mar 2023
 */
package it.pagopa.swclient.mil.auth.bean;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @author Antonio Tarricone
 */
@RegisterForReflection
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PublicKeys {
	public static final String KEYS = "keys";
	
	@JsonProperty(KEYS)
	private List<PublicKey> keys;
}