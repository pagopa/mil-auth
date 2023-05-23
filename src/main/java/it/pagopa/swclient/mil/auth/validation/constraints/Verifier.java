/*
 * Verifier.java
 *
 * 19 mag 2023
 */
package it.pagopa.swclient.mil.auth.validation.constraints;

import java.util.function.Predicate;

import io.quarkus.logging.Log;
import it.pagopa.swclient.mil.auth.bean.GetAccessToken;

/**
 * 
 * @author Antonio Tarricone
 */
public abstract class Verifier implements Predicate<GetAccessToken> {
	/**
	 * 
	 * @param getAccessToken
	 * @return
	 */
	protected boolean acquirerIdMustBeNull(GetAccessToken getAccessToken) {
		boolean check = getAccessToken.getAcquirerId() == null;
		if (check == false) {
			Log.warn("AcquirerId must be null.");
		}
		return check;
	}

	/**
	 * 
	 * @param getAccessToken
	 * @return
	 */
	protected boolean acquirerIdMustNotBeNull(GetAccessToken getAccessToken) {
		boolean check = getAccessToken.getAcquirerId() != null;
		if (check == false) {
			Log.warn("AcquirerId must not be null.");
		}
		return check;
	}

	/**
	 * 
	 * @param getAccessToken
	 * @return
	 */
	protected boolean merchantIdMustBeNull(GetAccessToken getAccessToken) {
		boolean check = getAccessToken.getMerchantId() == null;
		if (check == false) {
			Log.warn("MerchantId must be null.");
		}
		return check;
	}

	/**
	 * 
	 * @param getAccessToken
	 * @return
	 */
	protected boolean merchantIdMustNotBeNull(GetAccessToken getAccessToken) {
		boolean check = getAccessToken.getMerchantId() != null;
		if (check == false) {
			Log.warn("MerchantId must not be null.");
		}
		return check;
	}

	/**
	 * 
	 * @param getAccessToken
	 * @return
	 */
	protected boolean terminalIdMustBeNull(GetAccessToken getAccessToken) {
		boolean check = getAccessToken.getTerminalId() == null;
		if (check == false) {
			Log.warn("TerminalId must be null.");
		}
		return check;
	}

	/**
	 * 
	 * @param getAccessToken
	 * @return
	 */
	protected boolean terminalIdMustNotBeNull(GetAccessToken getAccessToken) {
		boolean check = getAccessToken.getTerminalId() != null;
		if (check == false) {
			Log.warn("TerminalId must not be null.");
		}
		return check;
	}

	/**
	 * 
	 * @param getAccessToken
	 * @return
	 */
	protected boolean clientSecretMustBeNull(GetAccessToken getAccessToken) {
		boolean check = getAccessToken.getClientSecret() == null;
		if (check == false) {
			Log.warn("client_secret must be null.");
		}
		return check;
	}

	/**
	 * 
	 * @param getAccessToken
	 * @return
	 */
	protected boolean clientSecretMustNotBeNull(GetAccessToken getAccessToken) {
		boolean check = getAccessToken.getClientSecret() != null;
		if (check == false) {
			Log.warn("client_secret must not be null.");
		}
		return check;
	}

	/**
	 * 
	 * @param getAccessToken
	 * @return
	 */
	protected boolean extTokenMustBeNull(GetAccessToken getAccessToken) {
		boolean check = getAccessToken.getExtToken() == null;
		if (check == false) {
			Log.warn("ext_token must be null.");
		}
		return check;
	}

	/**
	 * 
	 * @param getAccessToken
	 * @return
	 */
	protected boolean extTokenMustNotBeNull(GetAccessToken getAccessToken) {
		boolean check = getAccessToken.getExtToken() != null;
		if (check == false) {
			Log.warn("ext_token must not be null.");
		}
		return check;
	}

	/**
	 * 
	 * @param getAccessToken
	 * @return
	 */
	protected boolean addDataMustBeNull(GetAccessToken getAccessToken) {
		boolean check = getAccessToken.getAddData() == null;
		if (check == false) {
			Log.warn("add_data must be null.");
		}
		return check;
	}

	/**
	 * 
	 * @param getAccessToken
	 * @return
	 */
	protected boolean addDataMustNotBeNull(GetAccessToken getAccessToken) {
		boolean check = getAccessToken.getAddData() != null;
		if (check == false) {
			Log.warn("add_data must not be null.");
		}
		return check;
	}

	/**
	 * 
	 * @param getAccessToken
	 * @return
	 */
	protected boolean refreshTokenMustBeNull(GetAccessToken getAccessToken) {
		boolean check = getAccessToken.getRefreshToken() == null;
		if (check == false) {
			Log.warn("refresh_token must be null.");
		}
		return check;
	}

	/**
	 * 
	 * @param getAccessToken
	 * @return
	 */
	protected boolean refreshTokenMustNotBeNull(GetAccessToken getAccessToken) {
		boolean check = getAccessToken.getRefreshToken() != null;
		if (check == false) {
			Log.warn("refresh_token must not be null.");
		}
		return check;
	}

	/**
	 * 
	 * @param getAccessToken
	 * @return
	 */
	protected boolean usernameMustBeNull(GetAccessToken getAccessToken) {
		boolean check = getAccessToken.getUsername() == null;
		if (check == false) {
			Log.warn("username must be null.");
		}
		return check;
	}

	/**
	 * 
	 * @param getAccessToken
	 * @return
	 */
	protected boolean usernameMustNotBeNull(GetAccessToken getAccessToken) {
		boolean check = getAccessToken.getUsername() != null;
		if (check == false) {
			Log.warn("username must not be null.");
		}
		return check;
	}

	/**
	 * 
	 * @param getAccessToken
	 * @return
	 */
	protected boolean passwordMustBeNull(GetAccessToken getAccessToken) {
		boolean check = getAccessToken.getPassword() == null;
		if (check == false) {
			Log.warn("password must be null.");
		}
		return check;
	}

	/**
	 * 
	 * @param getAccessToken
	 * @return
	 */
	protected boolean passwordMustNotBeNull(GetAccessToken getAccessToken) {
		boolean check = getAccessToken.getPassword() != null;
		if (check == false) {
			Log.warn("password must not be null.");
		}
		return check;
	}

	/**
	 * 
	 * @param getAccessToken
	 * @return
	 */
	protected boolean scopedMustBeNull(GetAccessToken getAccessToken) {
		boolean check = getAccessToken.getScope() == null;
		if (check == false) {
			Log.warn("scope must be null.");
		}
		return check;
	}
}