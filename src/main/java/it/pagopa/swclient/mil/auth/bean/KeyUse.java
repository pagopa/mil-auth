/*
 * KeyUse.java
 *
 * 21 mar 2023
 */
package it.pagopa.swclient.mil.auth.bean;

import com.fasterxml.jackson.annotation.JsonCreator;
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

	/**
	 * 
	 * @param string
	 * @return
	 */
	@JsonCreator
	public static KeyUse fromString(String string) {
		if (string == null) {
			throw new IllegalArgumentException("value is null");
		}

		int i = 0;
		while (i < values().length && !values()[i].toString().equals(string)) {
			i++;
		}
		if (values()[i].toString().equals(string)) {
			return values()[i];
		}

		throw new IllegalArgumentException(String.format("%s doesn't exist", string));
	}
}
