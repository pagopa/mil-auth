/*
 * ByteArrayDeserializer.java
 *
 * 11 apr 2024
 */
package it.pagopa.swclient.mil.auth.azure.keyvaultkeys.util;

import java.io.IOException;
import java.util.Base64;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

/**
 * 
 * @author Antonio Tarricone
 */
public class ByteArrayDeserializer extends JsonDeserializer<byte[]> {
	/**
	 * @see com.fasterxml.jackson.databind.JsonDeserializer#deserialize(JsonParser,
	 *      DeserializationContext)
	 */
	@Override
	public byte[] deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
		String value = p.getText();
		if (value != null) {
			return Base64.getUrlDecoder().decode(value);
		} else {
			return null;
		}
	}

}
