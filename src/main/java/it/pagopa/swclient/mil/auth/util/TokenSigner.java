/*
 * TokenSigner.java
 *
 * 28 apr 2023
 */
package it.pagopa.swclient.mil.auth.util;

import static it.pagopa.swclient.mil.auth.util.KeyPairUtil.getPrivateKey;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import it.pagopa.swclient.mil.auth.bean.KeyPair;

/**
 * 
 * @author Antonio Tarricone
 */
public class TokenSigner {
	/**
	 * 
	 */
	private TokenSigner() {
	}

	/**
	 * 
	 * @param key
	 * @param payload
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeySpecException
	 * @throws JOSEException
	 */
	public static SignedJWT sign(KeyPair key, JWTClaimsSet payload) throws NoSuchAlgorithmException, InvalidKeySpecException, JOSEException {
		JWSHeader header = new JWSHeader(JWSAlgorithm.RS256, null, null, null, null, null, null, null, null, null, key.getKid(), true, null, null);
		SignedJWT token = new SignedJWT(header, payload);
		JWSSigner signer = new RSASSASigner(getPrivateKey(key));
		token.sign(signer);
		return token;
	}
}