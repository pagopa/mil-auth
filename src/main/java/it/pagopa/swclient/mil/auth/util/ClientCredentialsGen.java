/*
 * 
 */
package it.pagopa.swclient.mil.auth.util;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.UUID;

/**
 * This class generates client id, client secret, salt and salted hash for a new client which uses
 * client credentials grant flow. Once this date has been generated, client id and client secret
 * must be sent in a secure way to the client, client id, salt and salted hash must be uploaded in
 * the client credential vault.
 * 
 * @author Antonio Tarricone
 */
public class ClientCredentialsGen {
	/**
	 * @param args
	 * @throws NoSuchAlgorithmException
	 */
	public static void main(String[] args) throws NoSuchAlgorithmException {
		String clientId = UUID.randomUUID().toString();
		String clientSecret = UUID.randomUUID().toString();
		String salt = Base64.getEncoder().encodeToString(SecureRandom.getInstanceStrong().generateSeed(64));
		String saltedHash = Base64.getEncoder().encodeToString(PasswordVerifier.hashBytes(clientSecret, salt));

		System.out.printf("Client ID....: %s%n", clientId);
		System.out.printf("Client Secret: %s%n", clientSecret);
		System.out.printf("Salt.........: %s%n", salt);
		System.out.printf("Salted Hash..: %s%n", saltedHash);
	}
}