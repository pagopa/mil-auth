/*
 * KeyUse.java
 *
 * 21 mar 2023
 */
package it.pagopa.swclient.mil.auth.bean;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 
 * @author Antonio Tarricone
 */
public enum KeyUse {
	@JsonProperty("sig")
	SIG
}