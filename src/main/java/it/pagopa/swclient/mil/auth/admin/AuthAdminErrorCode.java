/*
 * AuthAdminErrorCode.java
 *
 * 22 lug 2024
 */
package it.pagopa.swclient.mil.auth.admin;

import it.pagopa.swclient.mil.auth.AuthErrorCode;
import it.pagopa.swclient.mil.auth.admin.bean.AdminJsonPropertyName;
import it.pagopa.swclient.mil.auth.admin.bean.AdminQueryParamName;

/**
 * Error codes returned by admin operations.
 *
 * @author Antonio Tarricone
 */
public final class AuthAdminErrorCode {
	// @formatter:off
	public static final String ERROR_DELETING_EXP_KEYS                     = AuthErrorCode.MODULE_ID + "100001";
	public static final String DESCRIPTION_MUST_NOT_BE_NULL                = AuthErrorCode.MODULE_ID + "100002";
	public static final String DESCRIPTION_MUST_MATCH_REGEXP               = AuthErrorCode.MODULE_ID + "100003";
	public static final String SUBJECT_MUST_MATCH_REGEXP                   = AuthErrorCode.MODULE_ID + "100004";
	public static final String ERROR_CREATING_CLIENT                       = AuthErrorCode.MODULE_ID + "100005";
	public static final String DUPLICATE_CLIENT_ID                         = AuthErrorCode.MODULE_ID + "100006";
	public static final String ERROR_STORING_CLIENT                        = AuthErrorCode.MODULE_ID + "100007";
	public static final String PAGE_MUST_BE_LESS_THAN_OR_EQUAL_TO_VALUE    = AuthErrorCode.MODULE_ID + "100008";
	public static final String PAGE_MUST_BE_GREATER_THAN_OR_EQUAL_TO_VALUE = AuthErrorCode.MODULE_ID + "100009";
	public static final String SIZE_MUST_BE_LESS_THAN_OR_EQUAL_TO_VALUE    = AuthErrorCode.MODULE_ID + "10000A";
	public static final String SIZE_MUST_BE_GREATER_THAN_OR_EQUAL_TO_VALUE = AuthErrorCode.MODULE_ID + "10000B";
	public static final String ERROR_READING_CLIENTS                       = AuthErrorCode.MODULE_ID + "10000C";
	public static final String CLIENT_NOT_FOUND                            = AuthErrorCode.MODULE_ID + "10000D";
	public static final String ERROR_DELETING_CLIENT                       = AuthErrorCode.MODULE_ID + "10000E";
	public static final String ERROR_UPDATING_CLIENT                       = AuthErrorCode.MODULE_ID + "10000F";
	// @formatter:on

	// @formatter:off
	public static final String MUST_BE_LESS_THAN_OR_EQUAL_TO_VALUE    = " must be less than or equal to {value}";
	public static final String MUST_BE_GREATER_THAN_OR_EQUAL_TO_VALUE = " must be greater than or equal to {value}";
	// @formatter:on

	// @formatter:off
	public static final String ERROR_DELETING_EXP_KEYS_MSG                     = "[" + ERROR_DELETING_EXP_KEYS                     + "] Error deleting expired keys";
	public static final String DESCRIPTION_MUST_NOT_BE_NULL_MSG                = "[" + DESCRIPTION_MUST_NOT_BE_NULL                + "]" + AdminJsonPropertyName.DESCRIPTION + AuthErrorCode.MUST_NOT_BE_NULL_MSG;
	public static final String DESCRIPTION_MUST_MATCH_REGEXP_MSG               = "[" + DESCRIPTION_MUST_MATCH_REGEXP               + "]" + AdminJsonPropertyName.DESCRIPTION + AuthErrorCode.MUST_MATCH_REGEXP_MSG;
	public static final String SUBJECT_MUST_MATCH_REGEXP_MSG                   = "[" + SUBJECT_MUST_MATCH_REGEXP                   + "]" + AdminJsonPropertyName.SUBJECT     + AuthErrorCode.MUST_MATCH_REGEXP_MSG;
	public static final String ERROR_CREATING_CLIENT_MSG                       = "[" + ERROR_CREATING_CLIENT                       + "] Error creating client";
	public static final String DUPLICATE_CLIENT_ID_MSG                         = "[" + DUPLICATE_CLIENT_ID                         + "] Duplicate client id";
	public static final String ERROR_STORING_CLIENT_MSG                        = "[" + ERROR_STORING_CLIENT                        + "] Error storing client";
	public static final String PAGE_MUST_BE_LESS_THAN_OR_EQUAL_TO_VALUE_MSG    = "[" + PAGE_MUST_BE_LESS_THAN_OR_EQUAL_TO_VALUE    + "]" + AdminQueryParamName.PAGE          + MUST_BE_LESS_THAN_OR_EQUAL_TO_VALUE;
	public static final String PAGE_MUST_BE_GREATER_THAN_OR_EQUAL_TO_VALUE_MSG = "[" + PAGE_MUST_BE_GREATER_THAN_OR_EQUAL_TO_VALUE + "]" + AdminQueryParamName.PAGE          + MUST_BE_GREATER_THAN_OR_EQUAL_TO_VALUE;
	public static final String SIZE_MUST_BE_LESS_THAN_OR_EQUAL_TO_VALUE_MSG    = "[" + SIZE_MUST_BE_LESS_THAN_OR_EQUAL_TO_VALUE    + "]" + AdminQueryParamName.PAGE          + MUST_BE_LESS_THAN_OR_EQUAL_TO_VALUE;
	public static final String SIZE_MUST_BE_GREATER_THAN_OR_EQUAL_TO_VALUE_MSG = "[" + SIZE_MUST_BE_GREATER_THAN_OR_EQUAL_TO_VALUE + "]" + AdminQueryParamName.PAGE          + MUST_BE_GREATER_THAN_OR_EQUAL_TO_VALUE;
	public static final String ERROR_READING_CLIENTS_MSG                       = "[" + ERROR_READING_CLIENTS                       + "] Error reading clients";
	public static final String CLIENT_NOT_FOUND_MSG                            = "[" + CLIENT_NOT_FOUND                            + "] Client not found";
	public static final String ERROR_DELETING_CLIENT_MSG                       = "[" + ERROR_DELETING_CLIENT                       + "] Error deleting client";
	public static final String ERROR_UPDATING_CLIENT_MSG                       = "[" + ERROR_UPDATING_CLIENT                       + "] Error updating client";
	// @formatter:on

	/**
	 *
	 */
	private AuthAdminErrorCode() {
	}
}