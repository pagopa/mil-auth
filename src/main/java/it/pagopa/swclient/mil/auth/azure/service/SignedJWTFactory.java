/*
 * SignedJWTFactory.java
 *
 * 4 ago 2023
 */
package it.pagopa.swclient.mil.auth.azure.service;

import java.text.ParseException;

import com.nimbusds.jose.util.Base64URL;
import com.nimbusds.jwt.SignedJWT;

/**
 * To make easier the mocking of the constructor of SignedJWT(Base64URL firstPart, Base64URL
 * secondPart, Base64URL thirdPart) for unit testing.
 *
 * @author Antonio Tarricone
 */
public class SignedJWTFactory {
	/**
	 *
	 */
	private SignedJWTFactory() {
	}

	/**
	 * @param header
	 * @param payload
	 * @param signature
	 * @return
	 * @throws ParseException
	 */
	public static SignedJWT createInstance(Base64URL header, Base64URL payload, Base64URL signature) throws ParseException {
		return new SignedJWT(header, payload, signature);
	}
}