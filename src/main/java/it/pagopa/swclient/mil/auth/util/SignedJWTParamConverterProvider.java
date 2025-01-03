/*
 * SignedJWTParamConverterProvider.java
 *
 * 3 jan 2025
 */
package it.pagopa.swclient.mil.auth.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import com.nimbusds.jwt.SignedJWT;

import io.quarkus.logging.Log;
import jakarta.ws.rs.ext.ParamConverter;
import jakarta.ws.rs.ext.ParamConverterProvider;
import jakarta.ws.rs.ext.Provider;

/**
 * 
 * @author Antonio Tarricone
 */
@Provider
public class SignedJWTParamConverterProvider implements ParamConverterProvider {
	/**
	 * @see jakarta.ws.rs.ext.ParamConverterProvider#getConverter(Class, Type, Annotation[])
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <T> ParamConverter<T> getConverter(Class<T> rawType, Type genericType, Annotation[] annotations) {
		Log.trace("getConverter");
		if (rawType.isAssignableFrom(SignedJWT.class)) {
			return (ParamConverter<T>) new SignedJWTParamConverter();
		}
		return null;
	}
}
