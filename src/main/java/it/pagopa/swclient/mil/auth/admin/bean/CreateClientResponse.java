/*
 * CreateClientResponse.java
 *
 * 26 lug 2024
 */
package it.pagopa.swclient.mil.auth.admin.bean;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * 
 * @author Antonio Tarricone
 */
@RegisterForReflection
@JsonInclude(Include.NON_NULL)
@NoArgsConstructor
@AllArgsConstructor
@Data
@Accessors(chain = true)
public class CreateClientResponse {
	/**
	 * <p>
	 * Client ID.
	 * </p>
	 */
	@JsonProperty(AdminJsonPropertyName.CLIENT_ID)
	private String clientId;

	/**
	 * <p>
	 * Client secret.
	 * </p>
	 */
	@JsonProperty(AdminJsonPropertyName.CLIENT_SECRET)
	@ToString.Exclude
	private String clientSecret;
}
