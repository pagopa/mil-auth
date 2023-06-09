/*
 * KeyPairGenerator.java
 *
 * 22 mar 2023
 */
package it.pagopa.swclient.mil.auth.service;

import java.util.Date;
import java.util.UUID;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator;

import it.pagopa.swclient.mil.auth.bean.KeyPair;
import it.pagopa.swclient.mil.auth.bean.KeyType;
import it.pagopa.swclient.mil.auth.bean.KeyUse;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * 
 * @author Antonio Tarricone
 */
@ApplicationScoped
public class KeyPairGenerator {
	/*
	 * Cryptoperiod of RSA keys in millis.
	 */
	@ConfigProperty(name = "cryptoperiod", defaultValue = "86400000")
	long cryptoperiod;

	/*
	 * Key size (modulus) of RSA keys in bits.
	 */
	@ConfigProperty(name = "keysize", defaultValue = "4096")
	int keysize;

	/**
	 * 
	 * @return
	 * @throws JOSEException
	 */
	public KeyPair generate() throws JOSEException {
		Date issueTime = new Date();
		Date expirationTime = new Date(issueTime.getTime() + cryptoperiod);
		String kid = UUID.randomUUID().toString();

		RSAKey rsaJwk = new RSAKeyGenerator(keysize)
			.keyUse(com.nimbusds.jose.jwk.KeyUse.SIGNATURE)
			.keyID(kid)
			.issueTime(issueTime)
			.expirationTime(expirationTime)
			.generate();

		/*
		 * Private exponent
		 */
		String d = rsaJwk.getPrivateExponent().toJSONString().replace("\"", "");

		/*
		 * Public exponent
		 */
		String e = rsaJwk.getPublicExponent().toJSONString().replace("\"", "");

		/*
		 * Public key use
		 */
		KeyUse use = KeyUse.sig;

		/*
		 * Modulus
		 */
		String n = rsaJwk.getModulus().toJSONString().replace("\"", "");

		/*
		 * Key type
		 */
		KeyType kty = KeyType.RSA;

		/*
		 * Expiration time
		 */
		long exp = expirationTime.getTime();

		/*
		 * Issued at
		 */
		long iat = issueTime.getTime();

		return new KeyPair(d, e, use, kid, n, kty, exp, iat);
	}
}