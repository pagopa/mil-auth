/*
 * AuthException.java
 *
 * 27 apr 2023
 */
package it.pagopa.swclient.mil.auth.util;

/**
 * To be used if a check fails.
 * 
 * @author Antonio Tarricone
 */
public class AuthException extends RuntimeException {
	/*
	 * 
	 */
	private static final long serialVersionUID = -523911093354154820L;

	/*
	 * 
	 */
	private String code;

	/**
	 * 
	 * @param code
	 * @param message;
	 */
	public AuthException(String code, String message) {
		super(message);
		this.code = code;
	}

	/**
	 * 
	 * @return
	 */
	public String getCode() {
		return code;
	}
}