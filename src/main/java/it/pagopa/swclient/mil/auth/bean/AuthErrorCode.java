/*
 * AuthErrorCode.java
 *
 * 15 mar 2023
 */
package it.pagopa.swclient.mil.auth.bean;

/**
 * Error codes returned by this service.
 *
 * @author Antonio Tarricone
 */
public final class AuthErrorCode {
	private static final String MODULE_ID = "009";

	/*
	 * Input validation
	 */
	public static final String REQUEST_ID_MUST_MATCH_REGEXP = MODULE_ID + "000039";
	public static final String REQUEST_ID_MUST_MATCH_REGEXP_MSG = "["+REQUEST_ID_MUST_MATCH_REGEXP+"] " + AccessTokenRequest.REQUEST_ID + " must match \"{regexp}\"";
	
	public static final String CLIENT_ID_MUST_NOT_BE_NULL = MODULE_ID + "000001";
	public static final String CLIENT_ID_MUST_NOT_BE_NULL_MSG = "[" + CLIENT_ID_MUST_NOT_BE_NULL + "] " + AccessTokenRequest.CLIENT_ID + " must not be null";

	public static final String CLIENT_ID_MUST_MATCH_REGEXP = MODULE_ID + "000002";
	public static final String CLIENT_ID_MUST_MATCH_REGEXP_MSG = "[" + CLIENT_ID_MUST_MATCH_REGEXP + "] " + AccessTokenRequest.CLIENT_ID + " must match \"{regexp}\"";

	public static final String TERMINAL_HANDLER_ID_MUST_NOT_BE_NULL = MODULE_ID + "000035";
	public static final String TERMINAL_HANDLER_ID_MUST_NOT_BE_NULL_MSG = "[" + TERMINAL_HANDLER_ID_MUST_NOT_BE_NULL + "] " + DeviceCodeRequest.TERMINAL_HANDLER_ID + " must not be null";

	public static final String TERMINAL_HANDLER_ID_MUST_MATCH_REGEXP = MODULE_ID + "000036";
	public static final String TERMINAL_HANDLER_ID_MUST_MATCH_REGEXP_MSG = "[" + TERMINAL_HANDLER_ID_MUST_MATCH_REGEXP + "] " + DeviceCodeRequest.TERMINAL_HANDLER_ID + " must match \"{regexp}\"";

	public static final String TERMINAL_ID_MUST_NOT_BE_NULL = MODULE_ID + "000037";
	public static final String TERMINAL_ID_MUST_NOT_BE_NULL_MSG = "[" + TERMINAL_ID_MUST_NOT_BE_NULL + "] " + DeviceCodeRequest.TERMINAL_ID + " must not be null";

	public static final String TERMINAL_ID_MUST_MATCH_REGEXP = MODULE_ID + "000038";
	public static final String TERMINAL_ID_MUST_MATCH_REGEXP_MSG = "[" + TERMINAL_ID_MUST_MATCH_REGEXP + "] " + DeviceCodeRequest.TERMINAL_ID + " must match \"{regexp}\"";

	public static final String GRANT_TYPE_MUST_NOT_BE_NULL = MODULE_ID + "000003";
	public static final String GRANT_TYPE_MUST_NOT_BE_NULL_MSG = "[" + GRANT_TYPE_MUST_NOT_BE_NULL + "] " + AccessTokenRequest.GRANT_TYPE + " must not be null";
	
	public static final String GRANT_TYPE_MUST_MATCH_REGEXP = MODULE_ID + "000004";
	public static final String GRANT_TYPE_MUST_MATCH_REGEXP_MSG = "[" + GRANT_TYPE_MUST_MATCH_REGEXP + "] " + AccessTokenRequest.GRANT_TYPE + " must match \"{regexp}\"";
	
	public static final String REFRESH_TOKEN_MUST_MATCH_REGEXP = MODULE_ID + "000008";
	public static final String REFRESH_TOKEN_MUST_MATCH_REGEXP_MSG = "[" + REFRESH_TOKEN_MUST_MATCH_REGEXP + "] " + AccessTokenRequest.REFRESH_TOKEN + " must match \"{regexp}\"";

	public static final String DEVICE_CODE_MUST_MATCH_REGEXP = MODULE_ID + "00003A";
	public static final String DEVICE_CODE_MUST_MATCH_REGEXP_MSG = "[" + DEVICE_CODE_MUST_MATCH_REGEXP + "] " + AccessTokenRequest.DEVICE_CODE + " must match \"{regexp}\"";

	public static final String CLIENT_SECRET_MUST_MATCH_REGEXP = MODULE_ID + "000022";
	public static final String CLIENT_SECRET_MUST_MATCH_REGEXP_MSG = "[" + CLIENT_SECRET_MUST_MATCH_REGEXP + "] " + AccessTokenRequest.CLIENT_SECRET + " must match \"{regexp}\"";
	
	public static final String BANK_ID_MUST_MATCH_REGEXP = MODULE_ID + "00003B";
	public static final String BANK_ID_MUST_MATCH_REGEXP_MSG = "[" + BANK_ID_MUST_MATCH_REGEXP + "] " + AccessTokenRequest.BANK_ID + " must match \"{regexp}\"";
	
	public static final String USER_TAX_CODE_MUST_MATCH_REGEXP = MODULE_ID + "000029";
	public static final String USER_TAX_CODE_MUST_MATCH_REGEXP_MSG = "[" + USER_TAX_CODE_MUST_MATCH_REGEXP + "] " + AccessTokenRequest.USER_TAX_CODE + " must match \"{regexp}\"";

	public static final String INCONSISTENT_ACCESS_TOKEN_REQUEST = MODULE_ID + "00003C";
	public static final String INCONSISTENT_ACCESS_TOKEN_REQUEST_MSG = "[" + INCONSISTENT_ACCESS_TOKEN_REQUEST + "] inconsistent access token request";
	
	public static final String USER_CODE_MUST_NOT_BE_NULL = MODULE_ID + "00003D";
	public static final String USER_CODE_MUST_NOT_BE_NULL_MSG = "[" + USER_CODE_MUST_NOT_BE_NULL + "] " + ActivateRequest.USER_CODE + " must not be null";
	
	public static final String USER_CODE_MUST_MATCH_REGEXP = MODULE_ID + "00003D";
	public static final String USER_CODE_MUST_MATCH_REGEXP_MSG = "[" + USER_CODE_MUST_MATCH_REGEXP + "] " + ActivateRequest.USER_CODE + " must match \"{regexp}\"";

	/*
	 * Processing
	 */
	public static final String CLIENT_EXPIRED = MODULE_ID + "00003E";
	public static final String CLIENT_EXPIRED_MSG = "[" + CLIENT_EXPIRED + "] Client expired";
	
	public static final String WRONG_GRANT_TYPE = MODULE_ID + "00003F";
	public static final String WRONG_GRANT_TYPE_MSG = "[" + WRONG_GRANT_TYPE + "] Wrong grant type";
	
	public static final String WRONG_CREDENTIALS = MODULE_ID + "00000C";
	public static final String WRONG_CREDENTIALS_MSG = "[" + WRONG_CREDENTIALS + "] Wrong credentials";
	
	public static final String ERROR_VERIFING_SECRET = MODULE_ID + "000027";
	public static final String ERROR_VERIFING_SECRET_MSG = "[" + ERROR_VERIFING_SECRET + "] Error verifing secret";
	
	public static final String ROLES_NOT_FOUND = MODULE_ID + "000014";
	public static final String ROLES_NOT_FOUND_MSG = "[" + ROLES_NOT_FOUND + "] Roles not found";
	
	public static final String REQUEST_INCONSISTENT_WITH_CHANNEL = MODULE_ID + "000014";
	public static final String REQUEST_INCONSISTENT_WITH_CHANNEL_MSG = "[" + REQUEST_INCONSISTENT_WITH_CHANNEL + "] Request inconsistent with channel";
	
	
	public static final String ERROR_SEARCHING_FOR_CLIENT = MODULE_ID + "000009";
	public static final String CLIENT_NOT_FOUND = MODULE_ID + "00000A";
	public static final String ERROR_SEARCHING_FOR_CREDENTIALS = MODULE_ID + "00000B";
	public static final String ERROR_VERIFING_CREDENTIALS = MODULE_ID + "00000D";
	public static final String INCONSISTENT_CREDENTIALS = MODULE_ID + "00000E";
	public static final String ERROR_SEARCHING_FOR_KEYS = MODULE_ID + "0000F";
	public static final String ERROR_SEARCHING_FOR_ROLES = MODULE_ID + "000010";

	public static final String ERROR_GENERATING_TOKEN = MODULE_ID + "000011";
	public static final String ERROR_GENERATING_TOKEN_MSG = "Error generating token";

	public static final String ERROR_PARSING_TOKEN = MODULE_ID + "000012";
	
	
	public static final String WRONG_ALGORITHM = MODULE_ID + "000015";
	public static final String ISSUE_TIME_MUST_NOT_BE_NULL = MODULE_ID + "000016";
	public static final String WRONG_ISSUE_TIME = MODULE_ID + "000017";
	public static final String EXPIRATION_TIME_MUST_NOT_BE_NULL = MODULE_ID + "000018";
	public static final String TOKEN_EXPIRED = MODULE_ID + "000019";
	public static final String WRONG_SCOPE = MODULE_ID + "00001A";
	public static final String KEY_NOT_FOUND = MODULE_ID + "00001B";

	public static final String WRONG_SIGNATURE = MODULE_ID + "00001C";
	public static final String WRONG_SIGNATURE_MSG = "Wrong signature";

	public static final String ERROR_VERIFING_SIGNATURE = MODULE_ID + "00001D";
	public static final String ERROR_VERIFING_SIGNATURE_MSG = "Error verifing signature";

	public static final String WRONG_SECRET = MODULE_ID + "000023";
	public static final String WRONG_CHANNEL = MODULE_ID + "000024";
	public static final String ERROR_GENERATING_KEY_PAIR = MODULE_ID + "000025";
	public static final String UNEXPECTED_ERROR = MODULE_ID + "000026";
	
	public static final String ERROR_STORING_KEY_PAIR = MODULE_ID + "000028";

	public static final String INVALID_KID = MODULE_ID + "00002A";
	public static final String INVALID_KID_MSG = "Invalid kid";

	/*
	 * Storage Container
	 */
	public static final String ERROR_CREATING_BLOB_ASYNC_CLIENT = MODULE_ID + "00002B";
	public static final String ERROR_CREATING_BLOB_ASYNC_CLIENT_MSG = "Error creating BlobAsyncClient";

	public static final String JSON_DESERIALIZATION_ERROR = MODULE_ID + "00002C";
	public static final String JSON_DESERIALIZATION_ERROR_MSG = "JSON deserialization error";

	/*
	 * Key Vault
	 */
	public static final String ERROR_RETRIEVING_KEYS_DATA = MODULE_ID + "00002D";
	public static final String ERROR_RETRIEVING_KEYS_DATA_MSG = "Error retrieving keys data";

	public static final String EXCEPTION_RETRIEVING_KEYS_DATA = MODULE_ID + "00002E";
	public static final String EXCEPTION_RETRIEVING_KEYS_DATA_MSG = "Exception retrieving keys data";

	public static final String ERROR_CREATING_RSA_KEY = MODULE_ID + "00002F";
	public static final String ERROR_CREATING_RSA_KEY_MSG = "Error creating RSA key";

	public static final String EXCEPTION_CREATING_RSA_KEY = MODULE_ID + "000030";
	public static final String EXCEPTION_CREATING_RSA_KEY_MSG = "Exception creating RSA key";

	public static final String ERROR_RETRIEVING_KEY = MODULE_ID + "000031";
	public static final String ERROR_RETRIEVING_KEY_MSG = "Error retrieving key";

	public static final String EXCEPTION_RETRIEVING_KEY = MODULE_ID + "000032";
	public static final String EXCEPTION_RETRIEVING_KEY_MSG = "Exception retrieving key";

	public static final String ERROR_CREATING_CRYPTOGRAPHY_ASYNC_CLIENT = MODULE_ID + "000033";
	public static final String ERROR_CREATING_CRYPTOGRAPHY_ASYNC_CLIENT_MSG = "Error creating CryptographyAsyncClient";

	public static final String EXCEPTION_CREATING_CRYPTOGRAPHY_ASYNC_CLIENT = MODULE_ID + "000034";
	public static final String EXCEPTION_CREATING_CRYPTOGRAPHY_ASYNC_CLIENT_MSG = "Exception creating CryptographyAsyncClient";

	/**
	 *
	 */
	private AuthErrorCode() {
	}
}