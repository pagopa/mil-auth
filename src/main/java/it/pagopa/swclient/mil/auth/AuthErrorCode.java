/*
 * AuthErrorCode.java
 *
 * 15 mar 2023
 */
package it.pagopa.swclient.mil.auth;

import it.pagopa.swclient.mil.auth.bean.FormParamName;

/**
 * Error codes returned by not-admin operations.
 *
 * @author Antonio Tarricone
 */
public final class AuthErrorCode {
	public static final String MODULE_ID = "009";

	// @formatter:off
	public static final String CLIENT_ID_MUST_NOT_BE_NULL       = MODULE_ID + "000001";
	public static final String CLIENT_ID_MUST_MATCH_REGEXP      = MODULE_ID + "000002";
	public static final String GRANT_TYPE_MUST_NOT_BE_NULL      = MODULE_ID + "000003";
	public static final String GRANT_TYPE_MUST_MATCH_REGEXP     = MODULE_ID + "000004";
	public static final String USERNAME_MUST_MATCH_REGEXP       = MODULE_ID + "000005";
	public static final String PASSWORD_MUST_MATCH_REGEXP       = MODULE_ID + "000006"; // NOSONAR
	public static final String SCOPE_MUST_MATCH_REGEXP          = MODULE_ID + "000007";
	public static final String REFRESH_TOKEN_MUST_MATCH_REGEXP  = MODULE_ID + "000008";
	public static final String ERROR_SEARCHING_FOR_CLIENT       = MODULE_ID + "000009";
	public static final String CLIENT_NOT_FOUND                 = MODULE_ID + "00000A";
	public static final String ERROR_SEARCHING_FOR_CREDENTIALS  = MODULE_ID + "00000B";
	public static final String WRONG_CREDENTIALS                = MODULE_ID + "00000C";
	public static final String ERROR_VERIFING_CREDENTIALS       = MODULE_ID + "00000D";
	public static final String INCONSISTENT_CREDENTIALS         = MODULE_ID + "00000E";
	public static final String ERROR_SEARCHING_FOR_KEYS         = MODULE_ID + "00000F";
	public static final String ERROR_SEARCHING_FOR_ROLES        = MODULE_ID + "000010";
	public static final String ERROR_PARSING_TOKEN              = MODULE_ID + "000011";
	public static final String INCONSISTENT_REQUEST             = MODULE_ID + "000012";
	public static final String ROLES_NOT_FOUND                  = MODULE_ID + "000013";
	public static final String WRONG_ALGORITHM                  = MODULE_ID + "000014";
	public static final String ISSUE_TIME_MUST_NOT_BE_NULL      = MODULE_ID + "000015";
	public static final String WRONG_ISSUE_TIME                 = MODULE_ID + "000016";
	public static final String EXPIRATION_TIME_MUST_NOT_BE_NULL = MODULE_ID + "000017";
	public static final String TOKEN_EXPIRED                    = MODULE_ID + "000018";
	public static final String WRONG_SCOPE                      = MODULE_ID + "000019";
	public static final String WRONG_SIGNATURE                  = MODULE_ID + "00001A";
	public static final String ERROR_VERIFING_SIGNATURE         = MODULE_ID + "00001B";
	public static final String EXT_TOKEN_MUST_MATCH_REGEXP      = MODULE_ID + "00001C";
	public static final String ADD_DATA_MUST_MATCH_REGEXP       = MODULE_ID + "00001D";
	public static final String ERROR_VALIDATING_EXT_TOKEN       = MODULE_ID + "00001E";
	public static final String EXT_TOKEN_NOT_VALID              = MODULE_ID + "00001F";
	public static final String CLIENT_SECRET_MUST_MATCH_REGEXP  = MODULE_ID + "000020";
	public static final String WRONG_SECRET                     = MODULE_ID + "000021";
	public static final String WRONG_CHANNEL                    = MODULE_ID + "000022";
	public static final String UNEXPECTED_ERROR                 = MODULE_ID + "000023";
	public static final String FISCAL_CODE_MUST_MATCH_REGEXP    = MODULE_ID + "000024";
	public static final String TOKEN_MUST_NOT_BE_NULL           = MODULE_ID + "000025";
	public static final String TOKEN_MUST_MATCH_REGEXP          = MODULE_ID + "000026";
	public static final String ERROR_ENCRYPTING_CLAIM           = MODULE_ID + "000027";
	public static final String ERROR_DECRYPTING_CLAIM           = MODULE_ID + "000028";
	public static final String ERROR_SIGNING_TOKEN              = MODULE_ID + "000029";
	public static final String ERROR_SEARCHING_FOR_USER         = MODULE_ID + "00002A";
	// @formatter:on

	public static final String MUST_NOT_BE_NULL_MSG = " must not be null";
	public static final String MUST_MATCH_REGEXP_MSG = " must match \"{regexp}\"";

	// @formatter:off
	public static final String GRANT_TYPE_MUST_NOT_BE_NULL_MSG     = "[" + GRANT_TYPE_MUST_NOT_BE_NULL     + "] " + FormParamName.GRANT_TYPE    + MUST_NOT_BE_NULL_MSG;
	public static final String GRANT_TYPE_MUST_MATCH_REGEXP_MSG    = "[" + GRANT_TYPE_MUST_MATCH_REGEXP    + "] " + FormParamName.GRANT_TYPE    + MUST_MATCH_REGEXP_MSG;
	public static final String USERNAME_MUST_MATCH_REGEXP_MSG      = "[" + USERNAME_MUST_MATCH_REGEXP      + "] " + FormParamName.USERNAME      + MUST_MATCH_REGEXP_MSG;
	public static final String PASSWORD_MUST_MATCH_REGEXP_MSG      = "[" + PASSWORD_MUST_MATCH_REGEXP      + "] " + FormParamName.PASSWORD      + MUST_MATCH_REGEXP_MSG;
	public static final String REFRESH_TOKEN_MUST_MATCH_REGEXP_MSG = "[" + REFRESH_TOKEN_MUST_MATCH_REGEXP + "] " + FormParamName.REFRESH_TOKEN + MUST_MATCH_REGEXP_MSG;
	public static final String EXT_TOKEN_MUST_MATCH_REGEXP_MSG     = "[" + EXT_TOKEN_MUST_MATCH_REGEXP     + "] " + FormParamName.EXT_TOKEN     + MUST_MATCH_REGEXP_MSG;
	public static final String ADD_DATA_MUST_MATCH_REGEXP_MSG      = "[" + ADD_DATA_MUST_MATCH_REGEXP      + "] " + FormParamName.ADD_DATA      + MUST_MATCH_REGEXP_MSG;
	public static final String CLIENT_ID_MUST_NOT_BE_NULL_MSG      = "[" + CLIENT_ID_MUST_NOT_BE_NULL      + "] " + FormParamName.CLIENT_ID     + MUST_NOT_BE_NULL_MSG;
	public static final String CLIENT_ID_MUST_MATCH_REGEXP_MSG     = "[" + CLIENT_ID_MUST_MATCH_REGEXP     + "] " + FormParamName.CLIENT_ID     + MUST_MATCH_REGEXP_MSG;
	public static final String SCOPE_MUST_MATCH_REGEXP_MSG         = "[" + SCOPE_MUST_MATCH_REGEXP         + "] " + FormParamName.SCOPE         + MUST_MATCH_REGEXP_MSG;
	public static final String CLIENT_SECRET_MUST_MATCH_REGEXP_MSG = "[" + CLIENT_SECRET_MUST_MATCH_REGEXP + "] " + FormParamName.CLIENT_SECRET + MUST_MATCH_REGEXP_MSG;
	public static final String FISCAL_CODE_MUST_MATCH_REGEXP_MSG   = "[" + FISCAL_CODE_MUST_MATCH_REGEXP   + "] " + FormParamName.FISCAL_CODE   + MUST_MATCH_REGEXP_MSG;
	public static final String INCONSISTENT_REQUEST_MSG            = "[" + INCONSISTENT_REQUEST            + "] Inconsistent request.";
	public static final String TOKEN_MUST_NOT_BE_NULL_MSG          = "[" + TOKEN_MUST_NOT_BE_NULL          + "] " + FormParamName.TOKEN         + MUST_NOT_BE_NULL_MSG;
	public static final String TOKEN_MUST_MATCH_REGEXP_MSG         = "[" + TOKEN_MUST_MATCH_REGEXP         + "] " + FormParamName.TOKEN         + MUST_MATCH_REGEXP_MSG;
	// @formatter:on

	/**
	 *
	 */
	private AuthErrorCode() {
	}
}