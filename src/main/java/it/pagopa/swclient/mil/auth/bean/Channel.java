/*
 * Channel.java
 *
 * 2 apr 2024
 */
package it.pagopa.swclient.mil.auth.bean;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 
 * @author Antonio Tarricone
 */
public enum Channel {
	@JsonProperty("POS_SERVICE_PROVIDER")
	POS_SERVICE_PROVIDER,

	@JsonProperty("PUBLIC_ADMINISTRATION")
	PUBLIC_ADMINISTRATION,

	@JsonProperty("MIL")
	MIL,

	@JsonProperty("SERVER")
	SERVER,

	@JsonProperty("ATM")
	ATM,

	@JsonProperty("POS")
	POS
}