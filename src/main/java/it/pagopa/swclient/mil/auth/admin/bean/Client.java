/*
 * Client.java
 *
 * 31 lug 2024
 */
package it.pagopa.swclient.mil.auth.admin.bean;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
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
public class Client {
	/**
	 *
	 */
	@JsonProperty(AdminJsonPropertyName.CLIENT_ID)
	private String clientId;

	/**
	 *
	 */
	@JsonProperty(AdminJsonPropertyName.CHANNEL)
	private String channel;

	/**
	 *
	 */
	@JsonProperty(AdminJsonPropertyName.SALT)
	private String salt;

	/**
	 *
	 */
	@JsonProperty(AdminJsonPropertyName.SECRET_HASH)
	private String secretHash;

	/**
	 *
	 */
	@JsonProperty(AdminJsonPropertyName.DESCRIPTION)
	private String description;

	/**
	 * 
	 */
	@JsonProperty(AdminJsonPropertyName.SUBJECT)
	private String subject;
}