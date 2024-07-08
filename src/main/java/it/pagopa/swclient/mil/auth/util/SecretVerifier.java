/*
 * PasswordVerifier.java
 *
 * 20 mar 2023
 */
package it.pagopa.swclient.mil.auth.util;

import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;

import org.bouncycastle.crypto.generators.Argon2BytesGenerator;
import org.bouncycastle.crypto.params.Argon2Parameters;

/**
 * 
 * @author Antonio Tarricone
 */
public class SecretVerifier {
	/*
	 * The following parameters are suggested by OWASP.
	 * https://cheatsheetseries.owasp.org/cheatsheets/Password_Storage_Cheat_Sheet.html#argon2id
	 */
	private static final int ITERATIONS = 2;
	private static final int MEM_LIMIT = 19 * 1024; // 19MB
	private static final int PARALLELISM = 1;

	/**
	 *
	 */
	private SecretVerifier() {
	}

	/**
	 * @param password
	 * @param salt
	 * @param hash
	 * @return
	 * @throws NoSuchAlgorithmException
	 */
	public static boolean verify(String password, String salt, String hash) {
		byte[] passwordBytes = password.getBytes(StandardCharsets.UTF_8);
		byte[] saltBytes = Base64.getDecoder().decode(salt);
		byte[] hashBytes = Base64.getDecoder().decode(hash);

		Argon2Parameters.Builder builder = new Argon2Parameters.Builder(Argon2Parameters.ARGON2_id)
			.withVersion(Argon2Parameters.ARGON2_VERSION_13)
			.withIterations(ITERATIONS)
			.withMemoryAsKB(MEM_LIMIT)
			.withParallelism(PARALLELISM)
			.withSalt(saltBytes);

		Argon2BytesGenerator verifier = new Argon2BytesGenerator();
		verifier.init(builder.build());

		byte[] testHash = new byte[hashBytes.length];
		verifier.generateBytes(passwordBytes, testHash, 0, hashBytes.length);

		return Arrays.equals(hashBytes, testHash);
	}
}