/*
 * GrantType.java
 *
 * 6 apr 2023
 */
package it.gov.pagopa.swclient.mil.idp.bean;

/**
 * 
 * @author Antonio Tarricone
 */
public class GrantType {
	public static final String PASSWORD = "password";
	public static final String REFRESH_TOKEN = "refresh_token";
	public static final String POYNT_TOKEN = "poynt_token";

	private GrantType() {
	}
}