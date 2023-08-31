/*
 * ErrorCode.java
 *
 * 15 mar 2023
 */
package it.pagopa.swclient.mil.auth;

/**
 * Error codes returned by this service.
 * 
 * @author Antonio Tarricone
 */
public final class AuthErrorCode {
	public static final String MODULE_ID = "009";

	public static final String CLIENT_ID_MUST_NOT_BE_NULL = MODULE_ID + "000001";
	public static final String CLIENT_ID_MUST_MATCH_REGEXP = MODULE_ID + "000002";
	public static final String GRANT_TYPE_MUST_NOT_BE_NULL = MODULE_ID + "000003";
	public static final String GRANT_TYPE_MUST_MATCH_REGEXP = MODULE_ID + "000004";
	public static final String USERNAME_MUST_MATCH_REGEXP = MODULE_ID + "000005";
	public static final String PASSWORD_MUST_MATCH_REGEXP = MODULE_ID + "000006";
	public static final String SCOPE_MUST_MATCH_REGEXP = MODULE_ID + "000007";
	public static final String REFRESH_TOKEN_MUST_MATCH_REGEXP = MODULE_ID + "000008";
	public static final String ERROR_SEARCHING_FOR_CLIENT = MODULE_ID + "000009";
	public static final String CLIENT_NOT_FOUND = MODULE_ID + "00000A";
	public static final String ERROR_SEARCHING_FOR_CREDENTIALS = MODULE_ID + "00000B";
	public static final String WRONG_CREDENTIALS = MODULE_ID + "00000C";
	public static final String ERROR_VERIFING_CREDENTIALS = MODULE_ID + "00000D";
	public static final String INCONSISTENT_CREDENTIALS = MODULE_ID + "00000E";
	public static final String ERROR_SEARCHING_FOR_KEYS = MODULE_ID + "0000F";
	public static final String ERROR_SEARCHING_FOR_ROLES = MODULE_ID + "000010";
	public static final String ERROR_GENERATING_TOKEN = MODULE_ID + "000011";
	public static final String ERROR_PARSING_TOKEN = MODULE_ID + "000012";
	public static final String INCONSISTENT_REQUEST = MODULE_ID + "000013";
	public static final String ROLES_NOT_FOUND = MODULE_ID + "000014";
	public static final String WRONG_ALGORITHM = MODULE_ID + "000015";
	public static final String ISSUE_TIME_MUST_NOT_BE_NULL = MODULE_ID + "000016";
	public static final String WRONG_ISSUE_TIME = MODULE_ID + "000017";
	public static final String EXPIRATION_TIME_MUST_NOT_BE_NULL = MODULE_ID + "000018";
	public static final String TOKEN_EXPIRED = MODULE_ID + "000019";
	public static final String WRONG_SCOPE = MODULE_ID + "00001A";
	public static final String KEY_NOT_FOUND = MODULE_ID + "00001B";
	public static final String WRONG_SIGNATURE = MODULE_ID + "00001C";
	public static final String ERROR_VERIFING_SIGNATURE = MODULE_ID + "00001D";
	public static final String EXT_TOKEN_MUST_MATCH_REGEXP = MODULE_ID + "00001E";
	public static final String ADD_DATA_MUST_MATCH_REGEXP = MODULE_ID + "00001F";
	public static final String ERROR_VALIDATING_EXT_TOKEN = MODULE_ID + "000020";
	public static final String EXT_TOKEN_NOT_VALID = MODULE_ID + "000021";
	public static final String CLIENT_SECRET_MUST_MATCH_REGEXP = MODULE_ID + "000022";
	public static final String WRONG_SECRET = MODULE_ID + "000023";
	public static final String WRONG_CHANNEL = MODULE_ID + "000024";
	public static final String ERROR_GENERATING_KEY_PAIR = MODULE_ID + "000025";
	public static final String UNEXPECTED_ERROR = MODULE_ID + "000026";
	public static final String ERROR_VERIFING_SECRET = MODULE_ID + "000027";
	public static final String ERROR_STORING_KEY_PAIR = MODULE_ID + "000028";
	public static final String AZURE_ACCESS_TOKEN_IS_NULL = MODULE_ID + "000029";
	public static final String ERROR_FROM_AZURE = MODULE_ID + "00002A";

	/**
	 * 
	 */
	private AuthErrorCode() {
	}
}