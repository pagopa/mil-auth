/*
 * AdminErrorCode.java
 *
 * 22 lug 2024
 */
package it.pagopa.swclient.mil.auth.admin;

import it.pagopa.swclient.mil.auth.AuthErrorCode;

/**
 * <p>
 * Error codes returned by admin operations.
 * </p>
 *
 * @author Antonio Tarricone
 */
public final class AdminErrorCode {
	// @formatter:off
	public static final String ERROR_DELETING_EXP_KEYS                = AuthErrorCode.MODULE_ID + "100001";
	public static final String DESCRIPTION_MUST_NOT_BE_NULL           = AuthErrorCode.MODULE_ID + "100002";
	public static final String DESCRIPTION_MUST_MATCH_REGEXP          = AuthErrorCode.MODULE_ID + "100003";
	public static final String SUBJECT_MUST_MATCH_REGEXP              = AuthErrorCode.MODULE_ID + "100004";
	public static final String ERROR_CREATING_CLIENT                  = AuthErrorCode.MODULE_ID + "100005";
	public static final String DUPLICATE_CLIENT_ID                    = AuthErrorCode.MODULE_ID + "100006";
	public static final String ERROR_STORING_CLIENT                   = AuthErrorCode.MODULE_ID + "100007";
	public static final String PAGE_MUST_BE_BETWEEN_MIN_AND_MAX       = AuthErrorCode.MODULE_ID + "100008";
	public static final String SIZE_MUST_BE_BETWEEN_MIN_AND_MAX       = AuthErrorCode.MODULE_ID + "100009";
	public static final String ERROR_READING_CLIENTS                  = AuthErrorCode.MODULE_ID + "10000A";
	public static final String CLIENT_NOT_FOUND                       = AuthErrorCode.MODULE_ID + "10000B";
	public static final String ERROR_DELETING_CLIENT                  = AuthErrorCode.MODULE_ID + "10000C";
	public static final String ERROR_UPDATING_CLIENT                  = AuthErrorCode.MODULE_ID + "10000D";
	public static final String ROLES_SIZE_MUST_BE_BETWEEN_MIN_AND_MAX = AuthErrorCode.MODULE_ID + "10000E";
	public static final String MERCHANT_ID_MUST_NOT_BE_NULL           = AuthErrorCode.MODULE_ID + "10000F";
	public static final String DUPLICATE_ROLES                        = AuthErrorCode.MODULE_ID + "100010"; 
	public static final String ERROR_STORING_ROLES                    = AuthErrorCode.MODULE_ID + "100011";
	public static final String ERROR_CREATING_ROLES                   = AuthErrorCode.MODULE_ID + "100012";
	public static final String ERROR_READING_ROLES                    = AuthErrorCode.MODULE_ID + "100013";
	public static final String ROLES_NOT_FOUND                        = AuthErrorCode.MODULE_ID + "100014";
	public static final String ERROR_UPDATING_ROLES                   = AuthErrorCode.MODULE_ID + "100015";
	public static final String ERROR_DELETING_ROLES                   = AuthErrorCode.MODULE_ID + "100016";
	public static final String ROLE_MUST_MATCH_REGEXP                 = AuthErrorCode.MODULE_ID + "100017";
	public static final String ROLE_MUST_NOT_BE_NULL                  = AuthErrorCode.MODULE_ID + "100018";
	public static final String ROLES_MUST_NOT_BE_NULL                 = AuthErrorCode.MODULE_ID + "100019";
	public static final String SET_OF_ROLES_ID_MUST_MATCH_REGEXP      = AuthErrorCode.MODULE_ID + "10001A";
	
	public static final String MUST_BE_BETWEEN_MIN_AND_MAX_MSG = " must be between {min} and {max}";

	public static final String ERROR_DELETING_EXP_KEYS_MSG                = "[" + ERROR_DELETING_EXP_KEYS                + "] Error deleting expired keys";
	public static final String DESCRIPTION_MUST_NOT_BE_NULL_MSG           = "[" + DESCRIPTION_MUST_NOT_BE_NULL           + "] Description" + AuthErrorCode.MUST_NOT_BE_NULL_MSG;
	public static final String DESCRIPTION_MUST_MATCH_REGEXP_MSG          = "[" + DESCRIPTION_MUST_MATCH_REGEXP          + "] Description" + AuthErrorCode.MUST_MATCH_REGEXP_MSG;
	public static final String SUBJECT_MUST_MATCH_REGEXP_MSG              = "[" + SUBJECT_MUST_MATCH_REGEXP              + "] Subject" + AuthErrorCode.MUST_MATCH_REGEXP_MSG;
	public static final String ERROR_CREATING_CLIENT_MSG                  = "[" + ERROR_CREATING_CLIENT                  + "] Error creating client";
	public static final String DUPLICATE_CLIENT_ID_MSG                    = "[" + DUPLICATE_CLIENT_ID                    + "] Duplicate client id";
	public static final String ERROR_STORING_CLIENT_MSG                   = "[" + ERROR_STORING_CLIENT                   + "] Error storing client";
	public static final String PAGE_MUST_BE_BETWEEN_MIN_AND_MAX_MSG       = "[" + PAGE_MUST_BE_BETWEEN_MIN_AND_MAX       + "] Page" + MUST_BE_BETWEEN_MIN_AND_MAX_MSG;
	public static final String SIZE_MUST_BE_BETWEEN_MIN_AND_MAX_MSG       = "[" + SIZE_MUST_BE_BETWEEN_MIN_AND_MAX       + "] Size" + MUST_BE_BETWEEN_MIN_AND_MAX_MSG;
	public static final String ERROR_READING_CLIENTS_MSG                  = "[" + ERROR_READING_CLIENTS                  + "] Error reading clients";
	public static final String CLIENT_NOT_FOUND_MSG                       = "[" + CLIENT_NOT_FOUND                       + "] Client not found";
	public static final String ERROR_DELETING_CLIENT_MSG                  = "[" + ERROR_DELETING_CLIENT                  + "] Error deleting client";
	public static final String ERROR_UPDATING_CLIENT_MSG                  = "[" + ERROR_UPDATING_CLIENT                  + "] Error updating client";
	public static final String ROLES_SIZE_MUST_BE_BETWEEN_MIN_AND_MAX_MSG = "[" + ROLES_SIZE_MUST_BE_BETWEEN_MIN_AND_MAX + "] Set of roles size" + MUST_BE_BETWEEN_MIN_AND_MAX_MSG;
	public static final String MERCHANT_ID_MUST_NOT_BE_NULL_MSG           = "[" + MERCHANT_ID_MUST_NOT_BE_NULL           + "] Merchant ID" + AuthErrorCode.MUST_NOT_BE_NULL_MSG;
	public static final String DUPLICATE_ROLES_MSG                        = "[" + DUPLICATE_ROLES                        + "] Duplicate roles";
	public static final String ERROR_STORING_ROLES_MSG                    = "[" + ERROR_STORING_ROLES                    + "] Error storing roles";
	public static final String ERROR_CREATING_ROLES_MSG                   = "[" + ERROR_CREATING_ROLES                   + "] Error creating roles";
	public static final String ERROR_READING_ROLES_MSG                    = "[" + ERROR_READING_ROLES                    + "] Error reading roles";
	public static final String ROLES_NOT_FOUND_MSG                        = "[" + ROLES_NOT_FOUND                        + "] Set of roles not found";
	public static final String ERROR_DELETING_ROLES_MSG                   = "[" + ERROR_DELETING_ROLES                   + "] Error deleting roles";
	public static final String ERROR_UPDATING_ROLES_MSG                   = "[" + ERROR_UPDATING_ROLES                   + "] Error updating roles";
	public static final String ROLE_MUST_NOT_BE_NULL_MSG                  = "[" + ROLE_MUST_NOT_BE_NULL                  + "] Role" + AuthErrorCode.MUST_NOT_BE_NULL_MSG;
	public static final String ROLE_MUST_MATCH_REGEXP_MSG                 = "[" + ROLE_MUST_MATCH_REGEXP                 + "] Role" + AuthErrorCode.MUST_MATCH_REGEXP_MSG;
	public static final String ROLES_MUST_NOT_BE_NULL_MSG                 = "[" + ROLES_MUST_NOT_BE_NULL                 + "] Roles" + AuthErrorCode.MUST_NOT_BE_NULL_MSG;
	public static final String SET_OF_ROLES_ID_MUST_MATCH_REGEXP_MSG      = "[" + SET_OF_ROLES_ID_MUST_MATCH_REGEXP      + "] Set of roles" + AuthErrorCode.MUST_MATCH_REGEXP_MSG;
	
	// @formatter:on

	/**
	 * <p>
	 * This class contains only constants.
	 * </p>
	 */
	private AdminErrorCode() {
	}
}