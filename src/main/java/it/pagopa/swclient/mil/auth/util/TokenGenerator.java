/*
 * TokenGenerator.java
 *
 * 28 apr 2023
 */
package it.pagopa.swclient.mil.auth.util;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Date;
import java.util.List;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import io.quarkus.logging.Log;
import it.pagopa.swclient.mil.auth.bean.KeyPair;

/**
 * 
 * @author Anis Lucidi & Antonio Tarricone
 */
public class TokenGenerator {
	/**
	 * 
	 */
	private TokenGenerator() {
	}

	/**
	 * 
	 * @param strings
	 * @return
	 */
	private static String concat(List<String> strings) {
		StringBuffer buffer = new StringBuffer();
		strings.forEach(x -> {
			buffer.append(x);
			buffer.append(" ");
		});
		return buffer.toString().trim();
	}

	/**
	 * 
	 * @param acquirerId
	 * @param channel
	 * @param merchantId
	 * @param clientId
	 * @param terminalId
	 * @param duration
	 * @param grants
	 * @param key
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeySpecException
	 * @throws JOSEException
	 */
	public static String generate(String acquirerId, String channel, String merchantId, String clientId, String terminalId, long duration, List<String> grants, KeyPair key) throws NoSuchAlgorithmException, InvalidKeySpecException, JOSEException {
		Date now = new Date();
		JWTClaimsSet payload = new JWTClaimsSet.Builder()
			.issueTime(now)
			.expirationTime(new Date(now.getTime() + duration * 1000))
			.claim("scope", concat(grants))
			.claim("acquirerId", acquirerId)
			.claim("channel", channel)
			.claim("merchantId", merchantId)
			.claim("clientId", clientId)
			.claim("terminalId", terminalId)
			.build();
		Log.debug("Token signing.");
		SignedJWT token = TokenSigner.sign(key, payload);
		return token.serialize();
	}
}