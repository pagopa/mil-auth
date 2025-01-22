/*
 * AuthErrorCode.java
 *
 * 15 mar 2023
 */
package it.pagopa.swclient.mil.auth;

/**
 * Error codes returned by not-admin operations.
 *
 * @author Antonio Tarricone
 */
public final class AuthErrorCode {
	public static final String MODULE_ID = "009";

	// @formatter:off
	public static final String CLIENT_ID_MUST_NOT_BE_NULL                   = MODULE_ID + "000001";
	public static final String CLIENT_ID_MUST_MATCH_REGEXP                  = MODULE_ID + "000002";
	public static final String GRANT_TYPE_MUST_NOT_BE_NULL                  = MODULE_ID + "000003";
	public static final String GRANT_TYPE_MUST_MATCH_REGEXP                 = MODULE_ID + "000004";
	public static final String USERNAME_MUST_MATCH_REGEXP                   = MODULE_ID + "000005";
	public static final String PASSWORD_MUST_MATCH_REGEXP                   = MODULE_ID + "000006"; // NOSONAR
	public static final String SCOPE_MUST_MATCH_REGEXP                      = MODULE_ID + "000007";
	public static final String REFRESH_TOKEN_MUST_MATCH_REGEXP              = MODULE_ID + "000008";
	public static final String ERROR_SEARCHING_FOR_CLIENT                   = MODULE_ID + "000009";
	public static final String CLIENT_NOT_FOUND                             = MODULE_ID + "00000A";
	public static final String ERROR_SEARCHING_FOR_USER                     = MODULE_ID + "00000B";
	public static final String WRONG_CREDENTIALS                            = MODULE_ID + "00000C";
	public static final String ERROR_VERIFING_CREDENTIALS                   = MODULE_ID + "00000D";
	public static final String INCONSISTENT_CREDENTIALS                     = MODULE_ID + "00000E";
	public static final String ERROR_SEARCHING_FOR_KEYS                     = MODULE_ID + "00000F";
	public static final String ERROR_SEARCHING_FOR_ROLES                    = MODULE_ID + "000010";
	public static final String ERROR_PARSING_TOKEN                          = MODULE_ID + "000011";
	public static final String INCONSISTENT_REQUEST                         = MODULE_ID + "000012";
	public static final String ROLES_NOT_FOUND                              = MODULE_ID + "000013";
	public static final String WRONG_ALGORITHM                              = MODULE_ID + "000014";
	public static final String ISSUE_TIME_MUST_NOT_BE_NULL                  = MODULE_ID + "000015";
	public static final String WRONG_ISSUE_TIME                             = MODULE_ID + "000016";
	public static final String EXPIRATION_TIME_MUST_NOT_BE_NULL             = MODULE_ID + "000017";
	public static final String TOKEN_EXPIRED                                = MODULE_ID + "000018";
	public static final String WRONG_SCOPE                                  = MODULE_ID + "000019";
	public static final String WRONG_SIGNATURE                              = MODULE_ID + "00001A";
	public static final String ERROR_VERIFING_SIGNATURE                     = MODULE_ID + "00001B";
	public static final String EXT_TOKEN_MUST_MATCH_REGEXP                  = MODULE_ID + "00001C";
	public static final String ADD_DATA_MUST_MATCH_REGEXP                   = MODULE_ID + "00001D";
	public static final String ERROR_VALIDATING_EXT_TOKEN                   = MODULE_ID + "00001E";
	public static final String EXT_TOKEN_NOT_VALID                          = MODULE_ID + "00001F";
	public static final String CLIENT_SECRET_MUST_MATCH_REGEXP              = MODULE_ID + "000020";
	public static final String WRONG_SECRET                                 = MODULE_ID + "000021";
	public static final String WRONG_CHANNEL                                = MODULE_ID + "000022";
	public static final String UNEXPECTED_ERROR                             = MODULE_ID + "000023";
	public static final String FISCAL_CODE_MUST_MATCH_REGEXP                = MODULE_ID + "000024";
	public static final String TOKEN_MUST_NOT_BE_NULL                       = MODULE_ID + "000025";
	public static final String TOKEN_MUST_MATCH_REGEXP                      = MODULE_ID + "000026";
	public static final String ERROR_ENCRYPTING_CLAIM                       = MODULE_ID + "000027";
	public static final String ERROR_DECRYPTING_CLAIM                       = MODULE_ID + "000028";
	public static final String ERROR_SIGNING_TOKEN                          = MODULE_ID + "000029";
	public static final String USER_NOT_FOUND                               = MODULE_ID + "00002A";
	public static final String WRONG_CLIENT_ID                              = MODULE_ID + "00002B";
	public static final String REFRESH_TOKEN_GENERATION_REVOKED             = MODULE_ID + "00002C";
	public static final String REFRESH_TOKEN_REVOKED                        = MODULE_ID + "00002D";
	public static final String WRONG_REFRESH_TOKEN_LOCATION                 = MODULE_ID + "00002E";
	public static final String ERROR_REVOKING_REFRESH_TOKEN                 = MODULE_ID + "00002F";
	public static final String REFRESH_TOKEN_GENERATION_ID_MUST_NOT_BE_NULL = MODULE_ID + "000030";
	public static final String REFRESH_TOKEN_ID_MUST_NOT_BE_NULL            = MODULE_ID + "000031";
	// @formatter:on

	public static final String MUST_NOT_BE_NULL_MSG = " must not be null";
	public static final String MUST_MATCH_REGEXP_MSG = " must match \"{regexp}\"";

	// @formatter:off
	public static final String GRANT_TYPE_MUST_NOT_BE_NULL_MSG                  = "[" + GRANT_TYPE_MUST_NOT_BE_NULL      + "] Grant type"    + MUST_NOT_BE_NULL_MSG;
	public static final String GRANT_TYPE_MUST_MATCH_REGEXP_MSG                 = "[" + GRANT_TYPE_MUST_MATCH_REGEXP     + "] Grant type"    + MUST_MATCH_REGEXP_MSG;
	public static final String USERNAME_MUST_MATCH_REGEXP_MSG                   = "[" + USERNAME_MUST_MATCH_REGEXP       + "] Username"      + MUST_MATCH_REGEXP_MSG;
	public static final String PASSWORD_MUST_MATCH_REGEXP_MSG                   = "[" + PASSWORD_MUST_MATCH_REGEXP       + "] Password"      + MUST_MATCH_REGEXP_MSG;
	public static final String REFRESH_TOKEN_MUST_MATCH_REGEXP_MSG              = "[" + REFRESH_TOKEN_MUST_MATCH_REGEXP  + "] Refresh token" + MUST_MATCH_REGEXP_MSG;
	public static final String EXT_TOKEN_MUST_MATCH_REGEXP_MSG                  = "[" + EXT_TOKEN_MUST_MATCH_REGEXP      + "] Ext token"     + MUST_MATCH_REGEXP_MSG;
	public static final String ADD_DATA_MUST_MATCH_REGEXP_MSG                   = "[" + ADD_DATA_MUST_MATCH_REGEXP       + "] Add data"      + MUST_MATCH_REGEXP_MSG;
	public static final String CLIENT_ID_MUST_NOT_BE_NULL_MSG                   = "[" + CLIENT_ID_MUST_NOT_BE_NULL       + "] Client ID"     + MUST_NOT_BE_NULL_MSG;
	public static final String CLIENT_ID_MUST_MATCH_REGEXP_MSG                  = "[" + CLIENT_ID_MUST_MATCH_REGEXP      + "] Client ID"     + MUST_MATCH_REGEXP_MSG;
	public static final String SCOPE_MUST_MATCH_REGEXP_MSG                      = "[" + SCOPE_MUST_MATCH_REGEXP          + "] Scope"         + MUST_MATCH_REGEXP_MSG;
	public static final String CLIENT_SECRET_MUST_MATCH_REGEXP_MSG              = "[" + CLIENT_SECRET_MUST_MATCH_REGEXP  + "] Client secret" + MUST_MATCH_REGEXP_MSG;
	public static final String FISCAL_CODE_MUST_MATCH_REGEXP_MSG                = "[" + FISCAL_CODE_MUST_MATCH_REGEXP    + "] Fiscal code"   + MUST_MATCH_REGEXP_MSG;
	public static final String INCONSISTENT_REQUEST_MSG                         = "[" + INCONSISTENT_REQUEST             + "] Inconsistent request";
	public static final String TOKEN_MUST_NOT_BE_NULL_MSG                       = "[" + TOKEN_MUST_NOT_BE_NULL           + "] Token"         + MUST_NOT_BE_NULL_MSG;
	public static final String TOKEN_MUST_MATCH_REGEXP_MSG                      = "[" + TOKEN_MUST_MATCH_REGEXP          + "] Token"         + MUST_MATCH_REGEXP_MSG;
	public static final String USER_NOT_FOUND_MSG                               = "[" + USER_NOT_FOUND                   + "] User not found";
	public static final String WRONG_CLIENT_ID_MSG                              = "[" + WRONG_CLIENT_ID                  + "] Wrong client ID";
	public static final String REFRESH_TOKEN_GENERATION_REVOKED_MSG             = "[" + REFRESH_TOKEN_GENERATION_REVOKED + "] Refresh token generation revoked";
	public static final String REFRESH_TOKEN_REVOKED_MSG                        = "[" + REFRESH_TOKEN_REVOKED            + "] Refresh token revoked";
	public static final String ERROR_PARSING_TOKEN_MSG                          = "[" + ERROR_PARSING_TOKEN              + "] Error parsing token";
	public static final String ISSUE_TIME_MUST_NOT_BE_NULL_MSG                  = "[" + ISSUE_TIME_MUST_NOT_BE_NULL      + "] Issue time must not be null";
	public static final String EXPIRATION_TIME_MUST_NOT_BE_NULL_MSG             = "[" + EXPIRATION_TIME_MUST_NOT_BE_NULL + "] Expiration time must not be null";
	public static final String TOKEN_EXPIRED_MSG                                = "[" + TOKEN_EXPIRED                    + "] Token expired";
	public static final String WRONG_REFRESH_TOKEN_LOCATION_MSG                 = "[" + WRONG_REFRESH_TOKEN_LOCATION     + "] Wrong refresh token location";
	public static final String ERROR_REVOKING_REFRESH_TOKEN_MSG                 = "[" + ERROR_REVOKING_REFRESH_TOKEN     + "] Error revoking refresh token";
	public static final String REFRESH_TOKEN_GENERATION_ID_MUST_NOT_BE_NULL_MSG = "[" + REFRESH_TOKEN_GENERATION_ID_MUST_NOT_BE_NULL + "] Refresh token generation ID must not be null";
	public static final String REFRESH_TOKEN_ID_MUST_NOT_BE_NULL_MSG            = "[" + REFRESH_TOKEN_ID_MUST_NOT_BE_NULL + "] Refresh token generation ID must not be null";
	// @formatter:on

	/**
	 *
	 */
	private AuthErrorCode() {
	}
}