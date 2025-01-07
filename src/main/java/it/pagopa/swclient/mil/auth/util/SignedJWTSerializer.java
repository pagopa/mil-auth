/*
 * SignedJWTSerializer.java
 *
 * 3 jan 2025
 */
package it.pagopa.swclient.mil.auth.util;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.nimbusds.jwt.SignedJWT;

import io.quarkus.logging.Log;

/**
 * <p>
 * Serialize signed JWT in a string.
 * </p>
 * 
 * @author Antonio Tarricone
 */
public class SignedJWTSerializer extends JsonSerializer<SignedJWT> {
	/**
	 * <p>
	 * Default constructor.
	 * </p>
	 */
	public SignedJWTSerializer() {
		super();
	}

	/**
	 * @see com.fasterxml.jackson.databind.JsonSerializer#serialize(Object, JsonGenerator,
	 *      SerializerProvider) JsonSerializer#serialize(Object, JsonGenerator, SerializerProvider)
	 */
	@Override
	public void serialize(SignedJWT value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
		Log.trace("serialize");
		gen.writeString(value.serialize());
	}
}