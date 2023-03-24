/*
 * PasswordVerifier.java
 *
 * 20 mar 2023
 */
package it.gov.pagopa.swclient.mil.idp.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;

/**
 * 
 * @author Antonio Tarricone
 */
public class PasswordVerifier {
	/**
	 * 
	 * @param password
	 * @param salt
	 * @param hash
	 * @return
	 * @throws NoSuchAlgorithmException
	 */
	public static boolean verify(String password, String salt, String hash) throws NoSuchAlgorithmException {
		byte[] passwordBytes = password.getBytes(StandardCharsets.UTF_8);
		byte[] saltBytes = Base64.getDecoder().decode(salt);
		byte[] hashBytes = Base64.getDecoder().decode(hash);

		byte[] data = new byte[passwordBytes.length + saltBytes.length];
		System.arraycopy(passwordBytes, 0, data, 0, passwordBytes.length);
		System.arraycopy(saltBytes, 0, data, passwordBytes.length, saltBytes.length);

		MessageDigest digest = MessageDigest.getInstance("SHA256");
		byte[] calcHashBytes = digest.digest(data);
		return Arrays.equals(calcHashBytes, hashBytes);
	}
}