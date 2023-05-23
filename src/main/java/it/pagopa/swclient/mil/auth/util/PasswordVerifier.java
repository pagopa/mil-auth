/*
 * PasswordVerifier.java
 *
 * 20 mar 2023
 */
package it.pagopa.swclient.mil.auth.util;

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
	 */
	private PasswordVerifier() {
	}

	/**
	 * 
	 * @param password
	 * @param salt
	 * @param hash
	 * @return
	 * @throws NoSuchAlgorithmException
	 */
	public static boolean verify(String password, String salt, String hash) throws NoSuchAlgorithmException {
		byte[] hashBytes = Base64.getDecoder().decode(hash);
		byte[] calcHashBytes = hashBytes(password, salt);
		return Arrays.equals(calcHashBytes, hashBytes);
	}

	/**
	 * 
	 * @param password
	 * @param salt
	 * @return
	 * @throws NoSuchAlgorithmException
	 */
	private static byte[] hashBytes(String password, String salt) throws NoSuchAlgorithmException {
		byte[] passwordBytes = password.getBytes(StandardCharsets.UTF_8);
		byte[] saltBytes = Base64.getDecoder().decode(salt);

		byte[] data = new byte[passwordBytes.length + saltBytes.length];
		System.arraycopy(passwordBytes, 0, data, 0, passwordBytes.length);
		System.arraycopy(saltBytes, 0, data, passwordBytes.length, saltBytes.length);

		MessageDigest digest = MessageDigest.getInstance("SHA256");
		byte[] hashBytes = digest.digest(data);
		return hashBytes;
	}

	/**
	 * 
	 * @param password
	 * @param salt
	 * @return
	 * @throws NoSuchAlgorithmException
	 */
	public static String hash(String password, String salt) throws NoSuchAlgorithmException {
		byte[] hashBytes = hashBytes(password, salt);
		String hash = Base64.getEncoder().encodeToString(hashBytes);
		return hash;
	}

	// /**
	// *
	// * @throws NoSuchAlgorithmException
	// */
	// public static void generateSecrets() throws NoSuchAlgorithmException {
	// byte[] buf = new byte[64];
	// SecureRandom secure = SecureRandom.getInstanceStrong();
	// for (int i = 0; i < 4; i++) {
	// secure.nextBytes(buf);
	// String salt = Base64.getEncoder().encodeToString(buf);
	// String secret = UUID.randomUUID().toString();
	// String hash = hash(secret, salt);
	// System.out.printf("%d\t%s\t%s\t%s%n", i, salt, secret, hash);
	// }
	// }
	//
	// /**
	// *
	// * @param args
	// * @throws NoSuchAlgorithmException
	// */
	// public static void main(String[] args) throws NoSuchAlgorithmException {
	// generateSecrets();
	// }
}