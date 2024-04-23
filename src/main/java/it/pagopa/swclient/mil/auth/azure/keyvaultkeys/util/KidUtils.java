/*
 * KidUtils.java
 *
 * 12 apr 2024
 */
package it.pagopa.swclient.mil.auth.azure.keyvaultkeys.util;

import java.net.URI;

import io.quarkus.logging.Log;
import it.pagopa.swclient.mil.auth.bean.AuthErrorCode;
import it.pagopa.swclient.mil.auth.util.AuthError;

/**
 * 
 * @author Antonio Tarricone
 */
public class KidUtils {
	/**
	 * 
	 */
	private KidUtils() {
	}

	/**
	 * 
	 * @param azureKid
	 * @return
	 */
	public static String toMilKid(String azureKid) {
		if (azureKid != null && !azureKid.isEmpty()) {
			String[] tokens = URI.create(azureKid).getPath().split("/");
			if (tokens.length >= 4) {
				return tokens[2] + "/" + tokens[3];
			}
		}
		Log.errorf(AuthErrorCode.INVALID_KID_MSG + ": [%s]", azureKid);
		throw new AuthError(AuthErrorCode.INVALID_KID, AuthErrorCode.INVALID_KID_MSG);
	}

	/**
	 * 
	 * @param milKid
	 * @return
	 */
	public static String[] toKeyNameAndVersion(String milKid) {
		if (milKid != null && !milKid.isEmpty()) {
			String[] tokens = milKid.split("/");
			if (tokens.length >= 2) {
				return new String[] {
					tokens[0],
					tokens[1]
				};
			}
		}
		Log.errorf(AuthErrorCode.INVALID_KID_MSG + ": [%s]", milKid);
		throw new AuthError(AuthErrorCode.INVALID_KID, AuthErrorCode.INVALID_KID_MSG);
	}
}
