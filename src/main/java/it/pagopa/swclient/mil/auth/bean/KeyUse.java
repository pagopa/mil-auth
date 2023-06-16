/*
 * KeyUse.java
 *
 * 21 mar 2023
 */
package it.pagopa.swclient.mil.auth.bean;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 
 * @author Antonio Tarricone
 */
public enum KeyUse {
	SIG("sig");
	
	/*
	 * String value.
	 */
	private final String string;
	
	/**
	 * 
	 * @param string
	 */
	private KeyUse(String string) {
		this.string = string;
	}
	
	/**
	 * 
	 */
	@JsonValue
	@Override
	public String toString() {
		return string;
	}
}
