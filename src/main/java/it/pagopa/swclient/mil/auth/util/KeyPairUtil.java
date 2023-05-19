/*
 * KeyPairUtil.java
 *
 * 27 apr 2023
 */
package it.pagopa.swclient.mil.auth.util;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;

import com.nimbusds.jose.util.Base64URL;

import it.pagopa.swclient.mil.auth.bean.KeyPair;
import it.pagopa.swclient.mil.auth.bean.PublicKey;

/**
 * 
 * @author Antonio Tarricone
 */
public class KeyPairUtil {
	/**
	 * 
	 */
	private KeyPairUtil() {
	}

	/**
	 * 
	 * @param publicKey
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeySpecException
	 */
	public static RSAPublicKey getPublicKey(PublicKey publicKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
		BigInteger modulus = Base64URL.from(publicKey.getN()).decodeToBigInteger();
		BigInteger exponent = Base64URL.from(publicKey.getE()).decodeToBigInteger();
		RSAPublicKeySpec spec = new RSAPublicKeySpec(modulus, exponent);
		KeyFactory factory = KeyFactory.getInstance("RSA");
		return (RSAPublicKey) (factory.generatePublic(spec));
	}

	/**
	 * 
	 * @param keyPair
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeySpecException
	 */
	public static PrivateKey getPrivateKey(KeyPair keyPair) throws NoSuchAlgorithmException, InvalidKeySpecException {
		BigInteger modulus = Base64URL.from(keyPair.getN()).decodeToBigInteger();
		BigInteger privateExponent = Base64URL.from(keyPair.getD()).decodeToBigInteger();
		RSAPrivateKeySpec spec = new RSAPrivateKeySpec(modulus, privateExponent);
		KeyFactory factory = KeyFactory.getInstance("RSA");
		return factory.generatePrivate(spec);
	}
}