/*
 * Verifier.java
 *
 * 19 mag 2023
 */
package it.pagopa.swclient.mil.auth.validation.constraints;

import java.util.function.Predicate;

import io.quarkus.logging.Log;
import it.pagopa.swclient.mil.auth.bean.GetAccessTokenRequest;

/**
 * 
 * @author Antonio Tarricone
 */
public abstract class Verifier implements Predicate<GetAccessTokenRequest> {
	/**
	 * 
	 * @param getAccessToken
	 * @return
	 */
	protected boolean acquirerIdMustBeNull(GetAccessTokenRequest getAccessToken) {
		boolean check = getAccessToken.getAcquirerId() == null;
		if (!check) {
			Log.warn("AcquirerId must be null.");
		}
		return check;
	}

	/**
	 * 
	 * @param getAccessToken
	 * @return
	 */
	protected boolean acquirerIdMustNotBeNull(GetAccessTokenRequest getAccessToken) {
		boolean check = getAccessToken.getAcquirerId() != null;
		if (!check) {
			Log.warn("AcquirerId must not be null.");
		}
		return check;
	}

	/**
	 * 
	 * @param getAccessToken
	 * @return
	 */
	protected boolean merchantIdMustBeNull(GetAccessTokenRequest getAccessToken) {
		boolean check = getAccessToken.getMerchantId() == null;
		if (!check) {
			Log.warn("MerchantId must be null.");
		}
		return check;
	}

	/**
	 * 
	 * @param getAccessToken
	 * @return
	 */
	protected boolean merchantIdMustNotBeNull(GetAccessTokenRequest getAccessToken) {
		boolean check = getAccessToken.getMerchantId() != null;
		if (!check) {
			Log.warn("MerchantId must not be null.");
		}
		return check;
	}

	/**
	 * 
	 * @param getAccessToken
	 * @return
	 */
	protected boolean terminalIdMustBeNull(GetAccessTokenRequest getAccessToken) {
		boolean check = getAccessToken.getTerminalId() == null;
		if (!check) {
			Log.warn("TerminalId must be null.");
		}
		return check;
	}

	/**
	 * 
	 * @param getAccessToken
	 * @return
	 */
	protected boolean terminalIdMustNotBeNull(GetAccessTokenRequest getAccessToken) {
		boolean check = getAccessToken.getTerminalId() != null;
		if (!check) {
			Log.warn("TerminalId must not be null.");
		}
		return check;
	}

	/**
	 * 
	 * @param getAccessToken
	 * @return
	 */
	protected boolean clientSecretMustBeNull(GetAccessTokenRequest getAccessToken) {
		boolean check = getAccessToken.getClientSecret() == null;
		if (!check) {
			Log.warn("client_secret must be null.");
		}
		return check;
	}

	/**
	 * 
	 * @param getAccessToken
	 * @return
	 */
	protected boolean clientSecretMustNotBeNull(GetAccessTokenRequest getAccessToken) {
		boolean check = getAccessToken.getClientSecret() != null;
		if (!check) {
			Log.warn("client_secret must not be null.");
		}
		return check;
	}

	/**
	 * 
	 * @param getAccessToken
	 * @return
	 */
	protected boolean extTokenMustBeNull(GetAccessTokenRequest getAccessToken) {
		boolean check = getAccessToken.getExtToken() == null;
		if (!check) {
			Log.warn("ext_token must be null.");
		}
		return check;
	}

	/**
	 * 
	 * @param getAccessToken
	 * @return
	 */
	protected boolean extTokenMustNotBeNull(GetAccessTokenRequest getAccessToken) {
		boolean check = getAccessToken.getExtToken() != null;
		if (!check) {
			Log.warn("ext_token must not be null.");
		}
		return check;
	}

	/**
	 * 
	 * @param getAccessToken
	 * @return
	 */
	protected boolean addDataMustBeNull(GetAccessTokenRequest getAccessToken) {
		boolean check = getAccessToken.getAddData() == null;
		if (!check) {
			Log.warn("add_data must be null.");
		}
		return check;
	}

	/**
	 * 
	 * @param getAccessToken
	 * @return
	 */
	protected boolean addDataMustNotBeNull(GetAccessTokenRequest getAccessToken) {
		boolean check = getAccessToken.getAddData() != null;
		if (!check) {
			Log.warn("add_data must not be null.");
		}
		return check;
	}

	/**
	 * 
	 * @param getAccessToken
	 * @return
	 */
	protected boolean refreshTokenMustBeNull(GetAccessTokenRequest getAccessToken) {
		boolean check = getAccessToken.getRefreshToken() == null;
		if (!check) {
			Log.warn("refresh_token must be null.");
		}
		return check;
	}

	/**
	 * 
	 * @param getAccessToken
	 * @return
	 */
	protected boolean refreshTokenMustNotBeNull(GetAccessTokenRequest getAccessToken) {
		boolean check = getAccessToken.getRefreshToken() != null;
		if (!check) {
			Log.warn("refresh_token must not be null.");
		}
		return check;
	}

	/**
	 * 
	 * @param getAccessToken
	 * @return
	 */
	protected boolean usernameMustBeNull(GetAccessTokenRequest getAccessToken) {
		boolean check = getAccessToken.getUsername() == null;
		if (!check) {
			Log.warn("username must be null.");
		}
		return check;
	}

	/**
	 * 
	 * @param getAccessToken
	 * @return
	 */
	protected boolean usernameMustNotBeNull(GetAccessTokenRequest getAccessToken) {
		boolean check = getAccessToken.getUsername() != null;
		if (!check) {
			Log.warn("username must not be null.");
		}
		return check;
	}

	/**
	 * 
	 * @param getAccessToken
	 * @return
	 */
	protected boolean passwordMustBeNull(GetAccessTokenRequest getAccessToken) {
		boolean check = getAccessToken.getPassword() == null;
		if (!check) {
			Log.warn("password must be null.");
		}
		return check;
	}

	/**
	 * 
	 * @param getAccessToken
	 * @return
	 */
	protected boolean passwordMustNotBeNull(GetAccessTokenRequest getAccessToken) {
		boolean check = getAccessToken.getPassword() != null;
		if (!check) {
			Log.warn("password must not be null.");
		}
		return check;
	}

	/**
	 * 
	 * @param getAccessToken
	 * @return
	 */
	protected boolean scopedMustBeNull(GetAccessTokenRequest getAccessToken) {
		boolean check = getAccessToken.getScope() == null;
		if (!check) {
			Log.warn("scope must be null.");
		}
		return check;
	}
}