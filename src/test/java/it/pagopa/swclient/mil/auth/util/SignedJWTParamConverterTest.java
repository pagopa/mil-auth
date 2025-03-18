/*
 * SignedJWTParamConverterTest.java
 *
 * 8 gen 2025
 */
package it.pagopa.swclient.mil.auth.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.text.ParseException;

import org.junit.jupiter.api.Test;

import com.nimbusds.jwt.SignedJWT;

import io.quarkus.test.junit.QuarkusTest;

/**
 * 
 * @author Antonio Tarricone
 */
@QuarkusTest
class SignedJWTParamConverterTest {
	/**
	 * Test method for
	 * {@link it.pagopa.swclient.mil.auth.util.SignedJWTParamConverter#toString(com.nimbusds.jwt.SignedJWT)}.
	 * 
	 * @throws ParseException
	 */
	@Test
	void testToStringSignedJWT() throws ParseException {
		SignedJWT jwt = SignedJWT.parse("eyJraWQiOiJrZXlfbmFtZS9rZXlfdmVyc2lvbiIsImFsZyI6IlJTMjU2In0.eyJzdWIiOiJjbGllbnRfaWQiLCJjbGllbnRJZCI6ImNsaWVudF9pZCIsImNoYW5uZWwiOiJjaGFubmVsIiwiaXNzIjoiaHR0cHM6Ly9taWwtYXV0aCIsImdyb3VwcyI6InJvbGUiLCJ0ZXJtaW5hbElkIjoidGVybWluYWxfaWQiLCJhdWQiOiJodHRwczovL21pbCIsIm1lcmNoYW50SWQiOiJtZXJjaGFudF9pZCIsInNjb3BlIjoic2NvcGUiLCJmaXNjYWxDb2RlIjoiZW5jX2Zpc2NhbF9jb2RlIiwiZXhwIjoxNzE3NjUyLCJhY3F1aXJlcklkIjoiYWNxdWlyZXJfaWQiLCJpYXQiOjE3MTc1OTJ9.AA");
		assertEquals(jwt.serialize(), new SignedJWTParamConverter().toString(jwt));
	}
}
