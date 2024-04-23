/*
 * ByteArraySerializer.java
 *
 * 11 apr 2024
 */
package it.pagopa.swclient.mil.auth.azure.keyvaultkeys.util;

import java.io.IOException;
import java.util.Base64;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

/**
 * 
 * @author Antonio Tarricone
 */
public class ByteArraySerializer extends JsonSerializer<byte[]> {
	/**
	 * @see com.fasterxml.jackson.databind.JsonSerializer#serialize(Object, JsonGenerator,
	 *      SerializerProvider)
	 */
	@Override
	public void serialize(byte[] value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
		if (value != null) {
			gen.writeString(Base64.getUrlEncoder().withoutPadding().encodeToString(value));
		} else {
			gen.writeNull();
		}
	}
}