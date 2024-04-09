/*
 * GrantType.java
 *
 * 6 apr 2023
 */
package it.pagopa.swclient.mil.auth.bean;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 
 * @author Antonio Tarricone
 */
public enum GrantType {
	@JsonProperty("refresh_token")
	REFRESH_TOKEN,

	@JsonProperty("client_credentials")
	CLIENT_CREDENTIALS,

	@JsonProperty("device_code")
	DEVICE_CODE
}