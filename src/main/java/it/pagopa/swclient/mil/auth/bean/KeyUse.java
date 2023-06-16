/*
 * KeyUse.java
 *
 * 21 mar 2023
 */
package it.pagopa.swclient.mil.auth.bean;

/**
 * 
 * @author Antonio Tarricone
 */
public enum KeyUse {
	SIG("sig");
	
	/*
	 * String value.
	 */
	private String string;
	
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
	@Override
	public String toString() {
		return string;
	}
}
