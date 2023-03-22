/*
 * ErrorCode.java
 *
 * 15 mar 2023
 */
package it.gov.pagopa.swclient.mil.idp;

/**
 * Error codes returned by this service.
 * 
 * @author Antonio Tarricone
 */
public final class ErrorCode {
	public static final String MODULE_ID = "009";

	public static final String CLIENT_ID_MUST_NOT_BE_NULL = MODULE_ID + "000001";
	public static final String CLIENT_ID_MUST_MATCH_REGEXP = MODULE_ID + "000002";

	public static final String GRANT_TYPE_MUST_NOT_BE_NULL = MODULE_ID + "000003";
	public static final String GRANT_TYPE_MUST_MATCH_REGEXP = MODULE_ID + "000004";

	public static final String USERNAME_MUST_NOT_BE_NULL = MODULE_ID + "000005";
	public static final String USERNAME_MUST_MATCH_REGEXP = MODULE_ID + "000006";

	public static final String PASSWORD_MUST_NOT_BE_NULL = MODULE_ID + "000007";
	public static final String PASSWORD_MUST_MATCH_REGEXP = MODULE_ID + "000008";

	public static final String SCOPE_MUST_MATCH_REGEXP = MODULE_ID + "000009";

	public static final String REFRESH_TOKEN_MUST_NOT_BE_NULL = MODULE_ID + "00000A";
	public static final String REFRESH_TOKEN_MUST_MATCH_REGEXP = MODULE_ID + "00000B";

	public static final String ERROR_WHILE_FINDING_CLIENT_ID = MODULE_ID + "00000C";
	public static final String CLIENT_ID_NOT_FOUND = MODULE_ID + "00000D";
	public static final String INCONSISTENT_CHANNEL = MODULE_ID + "00000E";

	public static final String ERROR_WHILE_FINDING_CREDENTIALS = MODULE_ID + "00000F";
	public static final String WRONG_CREDENTIALS = MODULE_ID + "000010";
	public static final String ERROR_WHILE_CREDENTIALS_VERIFICATION = MODULE_ID + "000011";
	public static final String CREDENTIALS_INCONSISTENCY = MODULE_ID + "000012";

	public static final String ERROR_WHILE_RETRIEVING_KIDS = MODULE_ID + "000013";
	public static final String ERROR_WHILE_RETRIEVING_KEYS = MODULE_ID + "000014";
	public static final String ERROR_WHILE_GENERATING_KEY = MODULE_ID + "000015";

	private ErrorCode() {
	}
}