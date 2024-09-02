/*
 * AuthJsonPropertyName.java
 *
 * 28 ago 2023
 */
package it.pagopa.swclient.mil.auth.bean;

/**
 * @author Antonio Tarricone
 */
public class AuthJsonPropertyName {
	public static final String ACCESS_TOKEN = "access_token";
	public static final String REFRESH_TOKEN = "refresh_token";
	public static final String TOKEN_TYPE = "token_type";
	public static final String EXPIRES_IN = "expires_in";

	public static final String ERRORS = "errors";

	public static final String KEYS = "keys";
	public static final String EXPONENT = "e";
	public static final String USE = "use";
	public static final String KID = "kid";
	public static final String MODULUS = "n";
	public static final String TYPE = "kty";
	public static final String EXPIRATION = "exp";
	public static final String ISSUED_AT = "iat";

	public static final String ISSUER = "issuer";
	public static final String TOKEN_ENDPOINT = "token_endpoint";
	public static final String JWKS_URI = "jwks_uri";

	public static final String FISCAL_CODE = "fiscalCode";

	public static final String TOKEN = "token";

	private AuthJsonPropertyName() {
	}
}