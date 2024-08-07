/*
 * TokenInfoResourceTest.java
 *
 * 27 mag 2024
 */
package it.pagopa.swclient.mil.auth.resource;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import io.quarkus.test.InjectMock;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.smallrye.mutiny.Uni;
import it.pagopa.swclient.mil.auth.bean.HeaderParamName;
import it.pagopa.swclient.mil.auth.bean.TokenInfoRequest;
import it.pagopa.swclient.mil.auth.bean.TokenInfoResponse;
import it.pagopa.swclient.mil.auth.service.ClaimEncryptor;
import it.pagopa.swclient.mil.auth.util.UniGenerator;
import jakarta.ws.rs.core.MediaType;

/**
 *
 * @author Antonio Tarricone
 */
@QuarkusTest
@TestHTTPEndpoint(TokenInfoResource.class)
class TokenInfoResourceTest {
	/*
	 * 
	 */
	@InjectMock
	ClaimEncryptor claimEncryptor;

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
	 * 
	 */
	@Test
	@TestSecurity(user = "test_user", roles = {
		"token_info"
	})
	void given_tokenWithEncFiscalCode_when_allIsOk_then_getDecryptedFiscalCode() {
		/*
		 * 
		 */
		when(claimEncryptor.decrypt(any()))
			.thenReturn(UniGenerator.item("my_fiscal_code"));

		/*
		 * 
		 */
		TokenInfoResponse actual = given()
			.contentType(MediaType.APPLICATION_JSON)
			.header(HeaderParamName.REQUEST_ID, "00000000-0000-0000-0000-700000000000")
			.body(new TokenInfoRequest("eyJraWQiOiJhdXRoMDcwOTY0M2Y0OTM5NDUyOWI5MmMxOWE2OGM4ZTE4NGEvNjU4MWM3MDRkZWRhNDk3OTk0M2MzYjM0NDY4ZGY3YzIiLCJhbGciOiJSUzI1NiJ9.eyJzdWIiOiIzOTY1ZGY1Ni1jYTlhLTQ5ZTUtOTdlOC0wNjE0MzNkNGEyNWIiLCJhdWQiOiJtaWwucGFnb3BhLml0IiwiY2xpZW50SWQiOiIzOTY1ZGY1Ni1jYTlhLTQ5ZTUtOTdlOC0wNjE0MzNkNGEyNWIiLCJtZXJjaGFudElkIjoiMjg0MDVmSGZrNzN4ODhEIiwiZmlzY2FsQ29kZSI6eyJraWQiOiJraWQiLCJhbGciOiJSU0EtT0FFUC0yNTYiLCJ2YWx1ZSI6IkFBRUNBdz09In0sImNoYW5uZWwiOiJQT1MiLCJpc3MiOiJodHRwOi8vZHVtbXkiLCJncm91cHMiOlsiTm90aWNlUGF5ZXIiLCJTbGF2ZVBvcyJdLCJ0ZXJtaW5hbElkIjoiMTIzNDU2NzgiLCJleHAiOjE3MTY4MDQ3MzksImFjcXVpcmVySWQiOiI0NTg1NjI1IiwiaWF0IjoxNzE2ODA0NDM5fQ.expected_signature"))
			.when()
			.post()
			.then()
			.log()
			.everything()
			.statusCode(200)
			.contentType(MediaType.APPLICATION_JSON)
			.extract()
			.response()
			.as(TokenInfoResponse.class);

		assertEquals(new TokenInfoResponse().setFiscalCode("my_fiscal_code"), actual);
	}

	/**
	 * 
	 */
	@Test
	@TestSecurity(user = "test_user", roles = {
		"token_info"
	})
	void given_tokenWOEncFiscalCode_when_allIsOk_then_getEmptyResponse() {
		/*
		 * 
		 */
		when(claimEncryptor.decrypt(any()))
			.thenReturn(UniGenerator.item("my_fiscal_code"));

		/*
		 * 
		 */
		given()
			.contentType(MediaType.APPLICATION_JSON)
			.header(HeaderParamName.REQUEST_ID, "00000000-0000-0000-0000-700000000001")
			.body(new TokenInfoRequest("eyJraWQiOiJhdXRoMDcwOTY0M2Y0OTM5NDUyOWI5MmMxOWE2OGM4ZTE4NGEvNjU4MWM3MDRkZWRhNDk3OTk0M2MzYjM0NDY4ZGY3YzIiLCJhbGciOiJSUzI1NiJ9.eyJzdWIiOiIzOTY1ZGY1Ni1jYTlhLTQ5ZTUtOTdlOC0wNjE0MzNkNGEyNWIiLCJhdWQiOiJtaWwucGFnb3BhLml0IiwiY2xpZW50SWQiOiIzOTY1ZGY1Ni1jYTlhLTQ5ZTUtOTdlOC0wNjE0MzNkNGEyNWIiLCJtZXJjaGFudElkIjoiMjg0MDVmSGZrNzN4ODhEIiwiY2hhbm5lbCI6IlBPUyIsImlzcyI6Imh0dHA6Ly9kdW1teSIsImdyb3VwcyI6WyJOb3RpY2VQYXllciIsIlNsYXZlUG9zIl0sInRlcm1pbmFsSWQiOiIxMjM0NTY3OCIsImV4cCI6MTcxNjg5MzcxOSwiYWNxdWlyZXJJZCI6IjQ1ODU2MjUiLCJpYXQiOjE3MTY4OTM0MTl9.expected_signature"))
			.when()
			.post()
			.then()
			.log()
			.everything()
			.statusCode(204);
	}

	/**
	 * 
	 */
	@Test
	@TestSecurity(user = "test_user", roles = {
		"token_info"
	})
	void given_badToken_when_tokeInfoIsInvoked_then_getBadRequest() {
		/*
		 * 
		 */
		when(claimEncryptor.decrypt(any()))
			.thenReturn(UniGenerator.item("my_fiscal_code"));

		/*
		 * 
		 */
		given()
			.contentType(MediaType.APPLICATION_JSON)
			.header(HeaderParamName.REQUEST_ID, "00000000-0000-0000-0000-700000000002")
			.body(new TokenInfoRequest("1.1.1"))
			.when()
			.post()
			.then()
			.log()
			.everything()
			.statusCode(400);
	}

	/**
	 * 
	 */
	@Test
	@TestSecurity(user = "test_user", roles = {
		"token_info"
	})
	void given_tokenWithEncFiscalCode_when_decryptWentWrongWithError_then_getServerError() {
		/*
		 * 
		 */
		when(claimEncryptor.decrypt(any()))
			.thenReturn(UniGenerator.error("code", "message"));

		/*
		 * 
		 */
		given()
			.contentType(MediaType.APPLICATION_JSON)
			.header(HeaderParamName.REQUEST_ID, "00000000-0000-0000-0000-700000000003")
			.body(new TokenInfoRequest("eyJraWQiOiJhdXRoMDcwOTY0M2Y0OTM5NDUyOWI5MmMxOWE2OGM4ZTE4NGEvNjU4MWM3MDRkZWRhNDk3OTk0M2MzYjM0NDY4ZGY3YzIiLCJhbGciOiJSUzI1NiJ9.eyJzdWIiOiIzOTY1ZGY1Ni1jYTlhLTQ5ZTUtOTdlOC0wNjE0MzNkNGEyNWIiLCJhdWQiOiJtaWwucGFnb3BhLml0IiwiY2xpZW50SWQiOiIzOTY1ZGY1Ni1jYTlhLTQ5ZTUtOTdlOC0wNjE0MzNkNGEyNWIiLCJtZXJjaGFudElkIjoiMjg0MDVmSGZrNzN4ODhEIiwiZmlzY2FsQ29kZSI6eyJraWQiOiJraWQiLCJhbGciOiJSU0EtT0FFUC0yNTYiLCJ2YWx1ZSI6IkFBRUNBdz09In0sImNoYW5uZWwiOiJQT1MiLCJpc3MiOiJodHRwOi8vZHVtbXkiLCJncm91cHMiOlsiTm90aWNlUGF5ZXIiLCJTbGF2ZVBvcyJdLCJ0ZXJtaW5hbElkIjoiMTIzNDU2NzgiLCJleHAiOjE3MTY4MDQ3MzksImFjcXVpcmVySWQiOiI0NTg1NjI1IiwiaWF0IjoxNzE2ODA0NDM5fQ.expected_signature"))
			.when()
			.post()
			.then()
			.log()
			.everything()
			.statusCode(500);
	}

	/**
	 * 
	 */
	@Test
	@TestSecurity(user = "test_user", roles = {
		"token_info"
	})
	void given_tokenWithEncFiscalCode_when_decryptWentWrongWithException_then_getServerError() {
		/*
		 * 
		 */
		when(claimEncryptor.decrypt(any()))
			.thenReturn(UniGenerator.exception("code", "message"));

		/*
		 * 
		 */
		given()
			.contentType(MediaType.APPLICATION_JSON)
			.header(HeaderParamName.REQUEST_ID, "00000000-0000-0000-0000-700000000004")
			.body(new TokenInfoRequest("eyJraWQiOiJhdXRoMDcwOTY0M2Y0OTM5NDUyOWI5MmMxOWE2OGM4ZTE4NGEvNjU4MWM3MDRkZWRhNDk3OTk0M2MzYjM0NDY4ZGY3YzIiLCJhbGciOiJSUzI1NiJ9.eyJzdWIiOiIzOTY1ZGY1Ni1jYTlhLTQ5ZTUtOTdlOC0wNjE0MzNkNGEyNWIiLCJhdWQiOiJtaWwucGFnb3BhLml0IiwiY2xpZW50SWQiOiIzOTY1ZGY1Ni1jYTlhLTQ5ZTUtOTdlOC0wNjE0MzNkNGEyNWIiLCJtZXJjaGFudElkIjoiMjg0MDVmSGZrNzN4ODhEIiwiZmlzY2FsQ29kZSI6eyJraWQiOiJraWQiLCJhbGciOiJSU0EtT0FFUC0yNTYiLCJ2YWx1ZSI6IkFBRUNBdz09In0sImNoYW5uZWwiOiJQT1MiLCJpc3MiOiJodHRwOi8vZHVtbXkiLCJncm91cHMiOlsiTm90aWNlUGF5ZXIiLCJTbGF2ZVBvcyJdLCJ0ZXJtaW5hbElkIjoiMTIzNDU2NzgiLCJleHAiOjE3MTY4MDQ3MzksImFjcXVpcmVySWQiOiI0NTg1NjI1IiwiaWF0IjoxNzE2ODA0NDM5fQ.expected_signature"))
			.when()
			.post()
			.then()
			.log()
			.everything()
			.statusCode(500);
	}

	/**
	 * 
	 */
	@Test
	@TestSecurity(user = "test_user", roles = {
		"token_info"
	})
	void given_tokenWithEncFiscalCode_when_decryptWentWrongWithOther_then_getServerError() {
		/*
		 * 
		 */
		when(claimEncryptor.decrypt(any()))
			.thenReturn(Uni.createFrom().failure(new Exception("synthetic_exception")));

		/*
		 * 
		 */
		given()
			.contentType(MediaType.APPLICATION_JSON)
			.header(HeaderParamName.REQUEST_ID, "00000000-0000-0000-0000-700000000005")
			.body(new TokenInfoRequest("eyJraWQiOiJhdXRoMDcwOTY0M2Y0OTM5NDUyOWI5MmMxOWE2OGM4ZTE4NGEvNjU4MWM3MDRkZWRhNDk3OTk0M2MzYjM0NDY4ZGY3YzIiLCJhbGciOiJSUzI1NiJ9.eyJzdWIiOiIzOTY1ZGY1Ni1jYTlhLTQ5ZTUtOTdlOC0wNjE0MzNkNGEyNWIiLCJhdWQiOiJtaWwucGFnb3BhLml0IiwiY2xpZW50SWQiOiIzOTY1ZGY1Ni1jYTlhLTQ5ZTUtOTdlOC0wNjE0MzNkNGEyNWIiLCJtZXJjaGFudElkIjoiMjg0MDVmSGZrNzN4ODhEIiwiZmlzY2FsQ29kZSI6eyJraWQiOiJraWQiLCJhbGciOiJSU0EtT0FFUC0yNTYiLCJ2YWx1ZSI6IkFBRUNBdz09In0sImNoYW5uZWwiOiJQT1MiLCJpc3MiOiJodHRwOi8vZHVtbXkiLCJncm91cHMiOlsiTm90aWNlUGF5ZXIiLCJTbGF2ZVBvcyJdLCJ0ZXJtaW5hbElkIjoiMTIzNDU2NzgiLCJleHAiOjE3MTY4MDQ3MzksImFjcXVpcmVySWQiOiI0NTg1NjI1IiwiaWF0IjoxNzE2ODA0NDM5fQ.expected_signature"))
			.when()
			.post()
			.then()
			.log()
			.everything()
			.statusCode(500);
	}
}
