/*
 * SignedJWTDeserializerTest.java
 *
 * 8 gen 2025
 */
package it.pagopa.swclient.mil.auth.util;

import static org.junit.jupiter.api.Assertions.*;

import java.text.ParseException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jwt.SignedJWT;

import io.quarkus.test.junit.QuarkusTest;

/**
 * 
 * @author Antonio Tarricone
 */
@QuarkusTest
class SignedJWTDeserializerTest {
	/**
	 * 
	 * @param testInfo
	 */
	@BeforeEach
	void init(TestInfo testInfo) {
		String frame = "*".repeat(testInfo.getDisplayName().length() + 11);
		System.out.println(frame);
		System.out.printf("* %s: START *%n", testInfo.getDisplayName());
		System.out.println(frame);
	}

	/**
	 * Test method for
	 * {@link it.pagopa.swclient.mil.auth.util.SignedJWTDeserializer#deserialize(com.fasterxml.jackson.core.JsonParser, com.fasterxml.jackson.databind.DeserializationContext)}.
	 * 
	 * @throws ParseException
	 * @throws JsonProcessingException
	 */
	@Test
	void testDeserializeJsonParserDeserializationContext() throws ParseException, JsonProcessingException {
		SignedJWT expected = SignedJWT.parse("eyJraWQiOiJrZXlfbmFtZS9rZXlfdmVyc2lvbiIsImFsZyI6IlJTMjU2In0.eyJzdWIiOiJjbGllbnRfaWQiLCJjbGllbnRJZCI6ImNsaWVudF9pZCIsImNoYW5uZWwiOiJjaGFubmVsIiwiaXNzIjoiaHR0cHM6Ly9taWwtYXV0aCIsImdyb3VwcyI6InJvbGUiLCJ0ZXJtaW5hbElkIjoidGVybWluYWxfaWQiLCJhdWQiOiJodHRwczovL21pbCIsIm1lcmNoYW50SWQiOiJtZXJjaGFudF9pZCIsInNjb3BlIjoic2NvcGUiLCJmaXNjYWxDb2RlIjoiZW5jX2Zpc2NhbF9jb2RlIiwiZXhwIjoxNzE3NjUyLCJhY3F1aXJlcklkIjoiYWNxdWlyZXJfaWQiLCJpYXQiOjE3MTc1OTJ9.AA");

		String json = "{\"jwt\":\"eyJraWQiOiJrZXlfbmFtZS9rZXlfdmVyc2lvbiIsImFsZyI6IlJTMjU2In0.eyJzdWIiOiJjbGllbnRfaWQiLCJjbGllbnRJZCI6ImNsaWVudF9pZCIsImNoYW5uZWwiOiJjaGFubmVsIiwiaXNzIjoiaHR0cHM6Ly9taWwtYXV0aCIsImdyb3VwcyI6InJvbGUiLCJ0ZXJtaW5hbElkIjoidGVybWluYWxfaWQiLCJhdWQiOiJodHRwczovL21pbCIsIm1lcmNoYW50SWQiOiJtZXJjaGFudF9pZCIsInNjb3BlIjoic2NvcGUiLCJmaXNjYWxDb2RlIjoiZW5jX2Zpc2NhbF9jb2RlIiwiZXhwIjoxNzE3NjUyLCJhY3F1aXJlcklkIjoiYWNxdWlyZXJfaWQiLCJpYXQiOjE3MTc1OTJ9.AA\"}";

		Sample sample = new ObjectMapper().readValue(json, Sample.class);

		SignedJWT actual = sample.getJwt();

		assertEquals(expected.serialize(), actual.serialize());
	}

	/**
	 * Test method for
	 * {@link it.pagopa.swclient.mil.auth.util.SignedJWTDeserializer#deserialize(com.fasterxml.jackson.core.JsonParser, com.fasterxml.jackson.databind.DeserializationContext)}.
	 */
	@Test
	void testDeserializeJsonParserDeserializationContextWithInvalidJwt() {
		String json = "{\"jwt\":\"@.@.@\"}";
		assertThrows(JsonProcessingException.class, () -> new ObjectMapper().readValue(json, Sample.class));
	}
}