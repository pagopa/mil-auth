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
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author Antonio Tarricone
 */
@RegisterForReflection
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
@EqualsAndHashCode
public class PublicKeys {
	/*
	 *
	 */
	@JsonProperty(JsonPropertyName.KEYS)
	private List<PublicKey> keys;
}