/*
 * KeyUtils.java
 *
 * 1 giu 2024
 */
package it.pagopa.swclient.mil.auth.util;

import java.net.URI;
import java.util.UUID;

import it.pagopa.swclient.mil.auth.bean.PublicKey;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.JsonWebKey;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.JsonWebKeyOperation;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyAttributes;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyBundle;

/**
 * 
 * @author Antonio Tarricone
 */
public class KeyUtils {
	/*
	 * 
	 */
	public static final String KEY_NAME_PREFIX = "auth";

	/**
	 * 
	 * @param azureKid
	 * @return
	 */
	public static String[] azureKid2KeyNameVersion(String azureKid) {
		String[] segments = URI.create(azureKid).getPath().split("/");
		return new String[] {
			segments[2], segments[3]
		};
	}

	/**
	 * 
	 * @param azureKid
	 * @return
	 */
	public static String azureKid2MyKid(String azureKid) {
		String[] keyNameVersion = azureKid2KeyNameVersion(azureKid);
		return keyNameVersion[0] + "/" + keyNameVersion[1];
	}

	/**
	 * 
	 * @param myKid
	 * @return
	 */
	public static String[] myKid2KeyNameVersion(String myKid) {
		String[] components = myKid.split("/");
		return new String[] {
			components[components.length - 2],
			components[components.length - 1]
		};
	}

	/**
	 * 
	 * @return
	 */
	public static String generateKeyName() {
		return KEY_NAME_PREFIX + UUID.randomUUID().toString().replace("-", "");
	}

	/**
	 * 
	 * @param keyBundle
	 * @return
	 */
	public static PublicKey keyBundle2PublicKey(KeyBundle keyBundle) {
		JsonWebKey jsonWebKey = keyBundle.getKey();
		KeyAttributes keyAttributes = keyBundle.getAttributes();

		return new PublicKey()
			.setE(jsonWebKey.getE())
			.setExp(keyAttributes.getExp())
			.setIat(keyAttributes.getCreated())
			.setKid(azureKid2MyKid(jsonWebKey.getKid()))
			.setKty(jsonWebKey.getKty())
			.setN(jsonWebKey.getN()).setExp(keyAttributes.getExp())
			.setUse(jsonWebKey.getKeyOps().contains(JsonWebKeyOperation.SIGN) && jsonWebKey.getKeyOps().contains(JsonWebKeyOperation.VERIFY) ? "sig" : null);
	}
}
