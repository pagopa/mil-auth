/*
 * Scope.java
 *
 * 10 apr 2024
 */
package it.pagopa.swclient.mil.auth.azure.identity.bean;

/**
 * Scopes of access tokens.
 * 
 * @author Antonio Tarricone
 */
public enum Scope {
	/*
	 * Scope to get an access token to access to Key Vault APIs.
	 */
	VAULT("https://vault.azure.net"),
	
	/*
	 * Scope to get an access token to access to Storage Account APIs.
	 */
	STORAGE("https://storage.azure.com");

	/*
	 * 
	 */
	private final String value;

	/**
	 * 
	 * @param value
	 */
	private Scope(String value) {
		this.value = value;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return value;
	}
}