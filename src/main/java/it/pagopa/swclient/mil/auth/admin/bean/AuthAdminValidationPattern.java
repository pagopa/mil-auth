/*
 * AuthAdminValidationPattern.java
 *
 * 26 lug 2024
 */
package it.pagopa.swclient.mil.auth.admin.bean;

/**
 * 
 * @author Antonio Tarricone
 */
public class AuthAdminValidationPattern {
	public static final String DESCRIPTION = "^[ -~]{1,256}$";
	public static final String SUBJECT = "^[ -~]{1,256}$";

	/**
	 * 
	 */
	private AuthAdminValidationPattern() {
	}
}
