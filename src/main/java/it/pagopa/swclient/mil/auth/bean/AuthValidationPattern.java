/*
 * AuthValidationPattern.java
 *
 * 26 lug 2024
 */
package it.pagopa.swclient.mil.auth.bean;

/**
 * 
 * @author Antonio Tarricone
 */
public class AuthValidationPattern {
	public static final String GRANT_TYPE = "^" + GrantType.PASSWORD + "|" + GrantType.REFRESH_TOKEN + "|" + GrantType.POYNT_TOKEN + "|" + GrantType.CLIENT_CREDENTIALS + "$";
	public static final String USERNAME = "^[ -~]{1,64}$";
	public static final String PASSWORD = "^[ -~]{1,64}$";
	public static final String REFRESH_TOKEN = "^[a-zA-Z0-9_-]{1,1024}\\.[a-zA-Z0-9_-]{1,1024}\\.[a-zA-Z0-9_-]{1,1024}$";
	public static final String EXT_TOKEN = "^[ -~]{1,4096}$";
	public static final String ADD_DATA = "^[ -~]{1,4096}$";
	public static final String CLIENT_ID = "^[a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12}$";
	public static final String SCOPE = "^" + Scope.OFFLINE_ACCESS + "$";
	public static final String CLIENT_SECRET = "^[0-9a-zA-Z-]{36}$";
	public static final String FISCAL_CODE = "^(([A-Z]{6}\\d{2}[A-Z]\\d{2}[A-Z]\\d{3}[A-Z])|(\\d{11}))$";
	public static final String TOKEN = "^[a-zA-Z0-9_-]{1,1024}\\.[a-zA-Z0-9_-]{1,1024}\\.[a-zA-Z0-9_-]{1,1024}$";

	private AuthValidationPattern() {
	}
}
