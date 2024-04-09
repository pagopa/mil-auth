/*
 * Verifier.java
 *
 * 19 mag 2023
 */
package it.pagopa.swclient.mil.auth.validation.constraints;

import java.util.function.Predicate;

import io.quarkus.logging.Log;
import it.pagopa.swclient.mil.auth.bean.AccessTokenRequest;

/**
 * 
 * @author Antonio Tarricone
 */
public abstract class Verifier implements Predicate<AccessTokenRequest> {
	/**
	 * 
	 * @param accessTokenRequest
	 * @return
	 */
	protected boolean bankIdMustBeNull(AccessTokenRequest accessTokenRequest) {
		boolean check = accessTokenRequest.getBankId() == null;
		if (!check) {
			Log.warn(AccessTokenRequest.BANK_ID + " must be null");
		}
		return check;
	}

	/**
	 * 
	 * @param accessTokenRequest
	 * @return
	 */
	protected boolean bankIdMustNotBeNull(AccessTokenRequest accessTokenRequest) {
		boolean check = accessTokenRequest.getBankId() != null;
		if (!check) {
			Log.warn(AccessTokenRequest.BANK_ID + " must not be null");
		}
		return check;
	}

	/**
	 * 
	 * @param accessTokenRequest
	 * @return
	 */
	protected boolean clientSecretMustBeNull(AccessTokenRequest accessTokenRequest) {
		boolean check = accessTokenRequest.getClientSecret() == null;
		if (!check) {
			Log.warn(AccessTokenRequest.CLIENT_SECRET + " must be null");
		}
		return check;
	}

	/**
	 * 
	 * @param accessTokenRequest
	 * @return
	 */
	protected boolean clientSecretMustNotBeNull(AccessTokenRequest accessTokenRequest) {
		boolean check = accessTokenRequest.getClientSecret() != null;
		if (!check) {
			Log.warn(AccessTokenRequest.CLIENT_SECRET + " must not be null");
		}
		return check;
	}

	/**
	 * 
	 * @param accessTokenRequest
	 * @return
	 */
	protected boolean deviceCodeMustBeNull(AccessTokenRequest accessTokenRequest) {
		boolean check = accessTokenRequest.getDeviceCode() == null;
		if (!check) {
			Log.warn(AccessTokenRequest.DEVICE_CODE + " must be null");
		}
		return check;
	}

	/**
	 * 
	 * @param accessTokenRequest
	 * @return
	 */
	protected boolean deviceCodeMustNotBeNull(AccessTokenRequest accessTokenRequest) {
		boolean check = accessTokenRequest.getDeviceCode() != null;
		if (!check) {
			Log.warn(AccessTokenRequest.DEVICE_CODE + " must not be null");
		}
		return check;
	}

	/**
	 * 
	 * @param accessTokenRequest
	 * @return
	 */
	protected boolean refreshTokenMustBeNull(AccessTokenRequest accessTokenRequest) {
		boolean check = accessTokenRequest.getRefreshToken() == null;
		if (!check) {
			Log.warn(AccessTokenRequest.REFRESH_TOKEN + " must be null");
		}
		return check;
	}

	/**
	 * 
	 * @param accessTokenRequest
	 * @return
	 */
	protected boolean refreshTokenMustNotBeNull(AccessTokenRequest accessTokenRequest) {
		boolean check = accessTokenRequest.getRefreshToken() != null;
		if (!check) {
			Log.warn(AccessTokenRequest.REFRESH_TOKEN + " must not be null");
		}
		return check;
	}
	
	/**
	 * 
	 * @param accessTokenRequest
	 * @return
	 */
	protected boolean terminalIdMustBeNull(AccessTokenRequest accessTokenRequest) {
		boolean check = accessTokenRequest.getTerminalId() == null;
		if (!check) {
			Log.warn(AccessTokenRequest.TERMINAL_ID + " must be null");
		}
		return check;
	}

	/**
	 * 
	 * @param accessTokenRequest
	 * @return
	 */
	protected boolean terminalIdMustNotBeNull(AccessTokenRequest accessTokenRequest) {
		boolean check = accessTokenRequest.getTerminalId() != null;
		if (!check) {
			Log.warn(AccessTokenRequest.TERMINAL_ID + " must not be null");
		}
		return check;
	}
}