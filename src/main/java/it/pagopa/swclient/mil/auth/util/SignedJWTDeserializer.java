/*
 * SignedJWTDeserializer.java
 *
 * 8 jan 2025
 */
package it.pagopa.swclient.mil.auth.util;

import java.io.IOException;
import java.text.ParseException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.nimbusds.jwt.SignedJWT;

import io.quarkus.logging.Log;
import it.pagopa.swclient.mil.auth.AuthErrorCode;
import it.pagopa.swclient.mil.bean.Errors;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

/**
 * <p>
 * Deserializes strings in signed JWT.
 * </p>
 * 
 * @author Antonio Tarricone
 */
public class SignedJWTDeserializer extends JsonDeserializer<SignedJWT> {
	/**
	 * <p>
	 * Default constructor.
	 * </p>
	 */
	public SignedJWTDeserializer() {
		super();
	}

	/**
	 * 
	 * @see com.fasterxml.jackson.databind.JsonDeserializer#deserialize(JsonParser,
	 *      DeserializationContext) JsonDeserializer#deserialize(JsonParser, DeserializationContext)
	 */
	@Override
	public SignedJWT deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
		try {
			return SignedJWT.parse(p.getText());
		} catch (ParseException e) {
			String message = String.format("[%s] Error parsing token", AuthErrorCode.ERROR_PARSING_TOKEN);
			Log.errorf(e, message);
			Response error = Response.status(Status.BAD_REQUEST)
				.entity(new Errors(AuthErrorCode.ERROR_PARSING_TOKEN, message))
				.build();
			throw new BadRequestException(error);
		}
	}
}
