/*
 * SecretTriplet.java
 *
 * 29 lug 2024
 */
package it.pagopa.swclient.mil.auth.util;

import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

import org.bouncycastle.crypto.generators.Argon2BytesGenerator;
import org.bouncycastle.crypto.params.Argon2Parameters;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 
 * @author Antonio Tarricone
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Accessors(chain = true)
public class SecretTriplet {
	/*
	 * The following parameters are suggested by OWASP.
	 * https://cheatsheetseries.owasp.org/cheatsheets/Password_Storage_Cheat_Sheet.html#argon2id
	 */
	private static final int ITERATIONS = 2;
	private static final int MEM_LIMIT = 19 * 1024; // 19MB
	private static final int PARALLELISM = 1;

	/*
	 * Secret symbols.
	 */
	private static final String SYMBOLS = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ-";

	/*
	 * 
	 */
	private static final int SECRET_LEN = 36;

	/*
	 * 
	 */
	private static final int SALT_LEN = 64;

	/*
	 * 
	 */
	private static final int HASH_LEN = 32;

	/*
	 * 
	 */
	private String secret;
	private String salt;
	private String hash;

	/**
	 * 
	 * @return
	 */
	public boolean verify() {
		byte[] secretBytes = secret.getBytes(StandardCharsets.UTF_8);
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
		verifier.generateBytes(secretBytes, testHash, 0, hashBytes.length);

		return Arrays.equals(hashBytes, testHash);
	}

	/**
	 * 
	 * @return
	 */
	private static SecureRandom getSecureRandom() {
		try {
			return SecureRandom.getInstanceStrong();
		} catch (NoSuchAlgorithmException e) {
			return new SecureRandom();
		}
	}

	/**
	 * 
	 * @return
	 */
	public static SecretTriplet generate(int secretLen) {
		/*
		 * Generate random client secret.
		 */
		String secretStr = getSecureRandom().ints(secretLen, 0, SYMBOLS.length()) // 36 random integers included between [0, 63[
			.map(SYMBOLS::charAt)
			.collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
			.toString();

		/*
		 * Generate random salt.
		 */
		byte[] saltBytes = new byte[SALT_LEN];
		getSecureRandom().nextBytes(saltBytes);

		/*
		 * Client secret salted hash calculation.
		 */
		byte[] secretBytes = secretStr.getBytes(StandardCharsets.UTF_8);

		/*
		 * Generate hash.
		 */
		Argon2Parameters.Builder builder = new Argon2Parameters.Builder(Argon2Parameters.ARGON2_id)
			.withVersion(Argon2Parameters.ARGON2_VERSION_13)
			.withIterations(ITERATIONS)
			.withMemoryAsKB(MEM_LIMIT)
			.withParallelism(PARALLELISM)
			.withSalt(saltBytes);

		Argon2BytesGenerator generator = new Argon2BytesGenerator();
		generator.init(builder.build());

		byte[] hash = new byte[HASH_LEN];
		generator.generateBytes(secretBytes, hash, 0, HASH_LEN);

		/*
		 * Output the results.
		 */
		return new SecretTriplet(
			secretStr,
			Base64.getEncoder().encodeToString(saltBytes),
			Base64.getEncoder().encodeToString(hash));
	}

	/**
	 * 
	 * @return
	 */
	public static SecretTriplet generate() {
		return generate(SECRET_LEN);
	}
}