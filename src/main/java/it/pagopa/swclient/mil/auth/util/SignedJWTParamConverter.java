/*
 * SignedJWTParamConverter.java
 *
 * 3 jan 2025
 */
package it.pagopa.swclient.mil.auth.util;

import java.text.ParseException;

import com.nimbusds.jwt.SignedJWT;

import io.quarkus.logging.Log;
import it.pagopa.swclient.mil.auth.AuthErrorCode;
import it.pagopa.swclient.mil.bean.Errors;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import jakarta.ws.rs.ext.ParamConverter;

/**
 * 
 * @author Antonio Tarricone
 */
public class SignedJWTParamConverter implements ParamConverter<SignedJWT> {
	/**
	 * @see jakarta.ws.rs.ext.ParamConverter#fromString(String)
	 */
	@Override
	public SignedJWT fromString(String value) {
		Log.trace("fromString");
		try {
			return SignedJWT.parse(value);
		} catch (ParseException e) {
			String message = String.format("[%s] Error parsing token", AuthErrorCode.ERROR_PARSING_TOKEN);
			Log.errorf(e, message);
			Response error = Response.status(Status.BAD_REQUEST)
				.entity(new Errors(AuthErrorCode.ERROR_PARSING_TOKEN, message))
				.build();
			throw new BadRequestException(error);
		}
	}

	/**
	 * @see jakarta.ws.rs.ext.ParamConverter#toString(Object)
	 */
	@Override
	public String toString(SignedJWT value) {
		Log.trace("toString");
		return value.serialize();
	}
}