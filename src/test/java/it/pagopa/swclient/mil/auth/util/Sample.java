/*
 * Sample.java
 *
 * 8 jan 2025
 */
package it.pagopa.swclient.mil.auth.util;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.nimbusds.jwt.SignedJWT;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * 
 * @author Antonio Tarricone
 */
@Getter
@Setter
@Accessors(chain = true)
public class Sample {
	/**
	 * JSON keys.
	 */
	public static final String JWT_JK = "jwt";

	/**
	 * 
	 */
	@JsonProperty(JWT_JK)
	@JsonSerialize(using = SignedJWTSerializer.class)
	@JsonDeserialize(using = SignedJWTDeserializer.class)
	private SignedJWT jwt;
}