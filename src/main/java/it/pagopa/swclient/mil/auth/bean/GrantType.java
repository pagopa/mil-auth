/*
 * GrantType.java
 *
 * 6 apr 2023
 */
package it.pagopa.swclient.mil.auth.bean;

/**
 * @author Antonio Tarricone
 */
public class GrantType {
	public static final String REFRESH_TOKEN = "refresh_token";
	public static final String CLIENT_CREDENTIALS = "client_credentials";

	private GrantType() {
	}
}