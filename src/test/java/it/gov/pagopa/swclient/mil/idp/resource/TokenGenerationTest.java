package it.gov.pagopa.swclient.mil.idp.resource;

import static io.restassured.RestAssured.given;
import static org.mockito.ArgumentMatchers.anyString;

import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;

import io.quarkus.logging.Log;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.smallrye.common.constraint.Assert;
import io.smallrye.mutiny.Uni;
import it.gov.pagopa.swclient.mil.bean.CommonHeader;
import it.gov.pagopa.swclient.mil.idp.ErrorCode;
import it.gov.pagopa.swclient.mil.idp.bean.GetAccessToken;
import it.gov.pagopa.swclient.mil.idp.bean.KeyPair;
import it.gov.pagopa.swclient.mil.idp.bean.KeyType;
import it.gov.pagopa.swclient.mil.idp.bean.KeyUse;
import it.gov.pagopa.swclient.mil.idp.bean.PublicKey;
import it.gov.pagopa.swclient.mil.idp.client.PoyntClient;
import it.gov.pagopa.swclient.mil.idp.service.KeyRetriever;
import it.gov.pagopa.swclient.mil.idp.service.RedisClient;

@QuarkusTest
@TestHTTPEndpoint(TokenResource.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TokenGenerationTest {

	@InjectMock
	RedisClient redisClient;
	
	@InjectMock
	KeyRetriever keyRetriever;

	@InjectMock
	@RestClient
	PoyntClient poyntClient;
	
	  private javax.ws.rs.core.Response buildMockResponse(Status status, String msgEntity) {
		  javax.ws.rs.core.Response mockResponse = Mockito.mock(javax.ws.rs.core.Response.class);
		    Mockito.when(mockResponse.readEntity(String.class)).thenReturn(msgEntity);
		    Mockito.when(mockResponse.getStatus()).thenReturn(status.getStatusCode());
		    Mockito.when(mockResponse.getStatusInfo()).thenReturn(status);

		    return mockResponse;
		  }
	@Test() //Method is currently not working because of injected keyRetriever, I mocked the needed methods here too. Don't know if it's the best solution. 
	public void testCreateToken() {
		 String e ="AQAB";
		 KeyUse use = KeyUse.sig;
		 String kid = "82f6353e-39b6-4a60-9fe5-cdce55a290bd";
		 String n = "i5x1lYLgZXwOdGZb0m8Bx1IqsedN4P9qLSsokxmqFxu6My5bFGhhVWA4OHdtTPACfYVNoRY85aH7RJOKe06PIuSct4nkAS0VBCOWamaJ0_Z9H083fiLrfPEwViNVW8GGnlzxmNkPZ4TAP8K7ceI660NSe-0OsK87seRLEpfgGAb2yFxR5y1MMsO9lHxVfW7BlfAYpF8zvOAafq-OwwZ8Av4Tz2NwPZDcl6vQ_XE5l6yXD8zRJxn7oeycEj7zavx5WLFzSl9Ox6oxHb_nfi3i2mWMmkBU8sUItDm729vyeUJ1njjpkDkY4KtLJvf_txJ3XKbTwQJ-fUCmawlcCaPS0D71t6YdnUI3GdhApq9-xL8J5S8sgDZdbFDhKOnpWpOd1hd_igb8KZG2eRguFY2R0hjGmWUIiyJ-Vc8XmD1QZwOvutKsaL8pwgpeEJ-C1x4ltJ5QyDeUSE5hsZWrg_c-KqC7u-FxvPE05X-O4aepksTuk0D0eo9TOlA7zw61x3V-A_zAYBPpevsANf2oPIrTdgWbODGVC3cIJuRJMN4QELGqYN7gcLho_1uiR6XcOmfynYfSFRXVou78tEyV_27U7cvUXmY6-Xl5FmqMy3thqMd1-Ak_UW7GXhkakradcP8PvT9fwozIG53seH14X4WDzjvXIKaS0NMOUi0dHKIOpWk";
		 KeyType kty = KeyType.RSA;
		 long exp = 1680695652675l;
		 long iat = 1680609252675l;
		 KeyPair k = new KeyPair("OCCNJzAIHdSrjQFPvgCR0vvGHWKz7RZSLdmRhN4HAyZLC-OBi2lRZDz8LTtaLlm5bpQmevIRRnKCLXYxcNRK-Ei3dpRRnWVt8w2JnboehCAu-mTjFutcCA2BFMg8u1hI0ibknEH7ju8QSTH1INpGPKoCob7SrpasZZ61zcJQPeii0zdGWC3eWfRWpbj8t6JM4oY1qfN6Pd9M14yl1OMo_Rz_gttj1uSDwtg4-4NHF6Kz0o7RsSohf147-pvjSao58L3ab1Z0uhHCkEbD_vbcole8GJFvdXBgSPCDBWdeCoD8_0C7KZfC2CQVYRPH8uZSJWYOcKSbjKhun06SeW-qwyTiQZpL91txpkh7XNbx3RrKBMbX7NfJF6eTdcv2zCljzwUjrzhlvKZQXypXKjE9by4n6bRH49ZlO2dlq5B8f5jiwSTEl3C4t1NgT8gL9_Hy2BkmlrudXEZtNfONOkCKK47DBiwh0SFxNVsCMWF_qHvwUvQIBjjPpZABzSJSBch7rFVxepk1ch706Y9MuzKJ4Lc9po2KCnW4Bm2XHbUQjFONZUA4aL34RB8TIRlFFrISf9cOkWEx74BUUm3gPuniNwASQeJG9LbQEHfv4it979htxN8kGuMDqUlV4-X_rNSjYjaAoyJjsEnxZb7h96BtQ80LiY9P-xmUDNwhJWD5Isc", 
					"AQAB", 
					KeyUse.sig, 
					"82f6353e-39b6-4a60-9fe5-cdce55a290bd", 
					"ndDlP0YJEVxZ57ZVKZ4Ft6f3siaX3tCJkrUBABbsUysSC0LKrnmuABZTJ3fhWe-T8UPz57X20H9b6o-EyRgvDHlN4SklxTI9Nd87kKKyqAVgvelTwpaNz96lxgeBtC_CKgd9ww2P9Q0_1lAMndS7k8-mKcBnXWa6hDENYX_BAy04L4GnpsR4_gfe4z7ov2sbNO53i6nm1NQ1MqfQ1KQEbsLIva3V_iOay6Kqb6tm_WjlXi2nOPxGOkxSwJ-jUCMaIEAQWqMjgSYsXRS62C-96MqcL6Br09BqoHJlTQn8AksMEs_I2OMDcOLug8ynocN_CorPgSRuqgO9Ddr1I7e21Q", 
					"q49l5Yf4ta8JVuLCqHr5FwYAZKH_0QPOdAaS3z0NxgVbnH8I5iiojPg6mahnDclZQHlX8P_niUikeg-PQs4EIa7eGbp6TWrx0-KZoEEB2d5jwoB9O-ueCquHi1o13EzM7vadTBP_6gUIJt0MWXFTKzMZeuHtZS7FyNEDSN3vTdLekZzW8bbHTQUhBM0ZO0WFOHKToHcK3h1PxyAgddrJizJ-VHvr1hugt7BjJmI4YO20gJCmKSLWm13y2-D8tnPW8WH4ko8BGtMaiW7slo5ABeOAMmfesAcF-WnsEEs3vNoydmdXzFKjtR0lppFX9qCRjEma4zOhI-7vP_Ai6qY3hQ", 
					"i5x1lYLgZXwOdGZb0m8Bx1IqsedN4P9qLSsokxmqFxu6My5bFGhhVWA4OHdtTPACfYVNoRY85aH7RJOKe06PIuSct4nkAS0VBCOWamaJ0_Z9H083fiLrfPEwViNVW8GGnlzxmNkPZ4TAP8K7ceI660NSe-0OsK87seRLEpfgGAb2yFxR5y1MMsO9lHxVfW7BlfAYpF8zvOAafq-OwwZ8Av4Tz2NwPZDcl6vQ_XE5l6yXD8zRJxn7oeycEj7zavx5WLFzSl9Ox6oxHb_nfi3i2mWMmkBU8sUItDm729vyeUJ1njjpkDkY4KtLJvf_txJ3XKbTwQJ-fUCmawlcCaPS0D71t6YdnUI3GdhApq9-xL8J5S8sgDZdbFDhKOnpWpOd1hd_igb8KZG2eRguFY2R0hjGmWUIiyJ-Vc8XmD1QZwOvutKsaL8pwgpeEJ-C1x4ltJ5QyDeUSE5hsZWrg_c-KqC7u-FxvPE05X-O4aepksTuk0D0eo9TOlA7zw61x3V-A_zAYBPpevsANf2oPIrTdgWbODGVC3cIJuRJMN4QELGqYN7gcLho_1uiR6XcOmfynYfSFRXVou78tEyV_27U7cvUXmY6-Xl5FmqMy3thqMd1-Ak_UW7GXhkakradcP8PvT9fwozIG53seH14X4WDzjvXIKaS0NMOUi0dHKIOpWk",
					"vKI7tSmL0Hn4C1sm36irGQr83O2RJXmg76SBX4R_h2Ne7mIYTaMu1ozjM2ajJHzbciMnTUfBWpQ2xEf5UR-SgZgBPn6PxzAcbN0qs6w8OUw_eeEZY7ZOhltLpmFbM83w3Lg32lPZpuJbLHnkhJFhPRg499KV3SkYkChFyPxvbyQzRM0h2iA2_lno_LZLpfZ4B71ZEOYsbI7-AXYhzjbIDPUZrBUe9If3CDI6rAOBO1LQBs_oIDat7AwgJUnMLF3WnHVTKsZZH24XmlVeAmvYL3oBQwCt2ukpUxDc-H-Ps_wSB-r87lKWHdFdC_oKK8WylEbUHuiIZs2sZTk1fa76Jw",
					KeyType.RSA, 
					"vXhbi-11pjdnjWdppWEHMhk-LP8Yk6g78kBghSVw6Pfe8hgy5I-qx7S6VmD2yUHjNS9t_ZnLqv-IdnZCq3XPY4lQHPU5TDu8l-fUvAgejiwly1YHNvjsxzV-hiwYFTOkxFpezF4HA71vovqEkhQWqMXF2yxf8GJWQrBbFbReO8Q-ltk8rJVwxm1yHO4WqJohwtOyHrWzqjRT_IOOp402C6FxuHvmFyNKuUgMChDsNmHhtVsdQ9z4Kq0e8GibnIE8Se2hgkn1FbwWLRiSj5BqzCKkP_w1WtV21a3v8b5XZsahOXqEjvVcoyQaR45PzvYEqax16cAwaw5T13IrNBvt7w",
					"hsnut4VDuwCg-IEGbzthY6oVjJwUuSMs6atdyyBMcnWKV-sqYUqIP8iAqQBjFYcEwsVCbb_AubAbHrlQm42T2XCweTmnvZXqI7PMK5_aYp9DTwqI30Sr-usgjgQJkyMUG7jQj-EvY99xkYX7j9ilrslnkuE01OY0PWkvR_Jo09seG68EUBq3TBSOdSP_Cuwi9JxripytPVOXfnsdn4zuTxxjE8ZSxq6Ql6nm5tQ-Ni6JdQWzE6JqCTuwUtPGYP6hcCeASTGKTusy7t3zMkC1hdT5o77dlTGkbZt60gA-alRQoy8f0UkuDXgiIwDr9FILTyZHiDqu1UavDA-PfJ_PSA",
	1680695652675l, 1680609252675l);
			Mockito.when(keyRetriever.getKeyPair()).thenReturn(Uni.createFrom().item(k)); 
			PublicKey pk = new PublicKey(e, use, kid, n, kty, exp, iat);
			Mockito.when(keyRetriever.getPublicKey(kid))
			.thenReturn(Uni.createFrom().item(Optional.of(pk)));
		// Prepare request
		GetAccessToken getAccessToken = new GetAccessToken();
		CommonHeader commonHeader = new CommonHeader();
		String clientId = "5254f087-1214-45cd-94ae-fda53c835197";
		String channel = "POS";
		String username = "antonio.tarricone";
		String password = "antonio";
		String merchantId = "28405fHfk73x88D";
		String acquirerId = "4585625";
		String terminalId = "testTerm";
		getAccessToken.setClientId(clientId);
		commonHeader.setChannel(channel);
		getAccessToken.setUsername(username);
		getAccessToken.setPassword(username);
		getAccessToken.setScope("offline_access");
		commonHeader.setMerchantId(merchantId);
		commonHeader.setAcquirerId(acquirerId);
		commonHeader.setTerminalId(terminalId);
		RequestSpecification request = given().contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.formParam("client_id", clientId).formParam("username", username).formParam("password", password)
				.formParam("grant_type", "password").header("AcquirerId", acquirerId).header("TerminalId", terminalId)
				.header("MerchantId", merchantId).header("RequestId", "aB1aB1aB-aB1a-aB1a-aB1a-aB1aB1aB1abb")
				.header("Channel", channel);

		Response resp = request.when().log().all().post(); // I was thinking of elaborating the test a
		// bit more, checking if the type is correct Uni<AccessToken>
		// Log.info(resp.body());
		resp.then().statusCode(200);
	}

	@Test()
	public void testCreateTokenWithWrongClientId() {
		// Prepare request
		GetAccessToken getAccessToken = new GetAccessToken();
		CommonHeader commonHeader = new CommonHeader();
		String clientId = "5254f087-1214-45cd-94ae-fda53c835198";
		String channel = "POS";
		String username = "antonio.tarricone";
		String password = "antonio";
		String merchantId = "28405fHfk73x88D";
		String acquirerId = "4585625";
		String terminalId = "testTerm";
		getAccessToken.setClientId(clientId);
		commonHeader.setChannel(channel);
		getAccessToken.setUsername(username);
		getAccessToken.setPassword(username);
		getAccessToken.setScope("offline_access");
		commonHeader.setMerchantId(merchantId);
		commonHeader.setAcquirerId(acquirerId);
		commonHeader.setTerminalId(terminalId);
		RequestSpecification wrongClientIdRequest = given().contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.formParam("client_id", clientId).formParam("username", username).formParam("password", password)
				.formParam("grant_type", "password").header("AcquirerId", acquirerId).header("TerminalId", terminalId)
				.header("MerchantId", merchantId).header("RequestId", "aB1aB1aB-aB1a-aB1a-aB1a-aB1aB1aB1abb")
				.header("Channel", channel);

		Response wrongClientIdResponse = wrongClientIdRequest.when().log().all().post();

		wrongClientIdResponse.then().statusCode(401);

		ArrayList<String> wrongClientIdResponseMessage = wrongClientIdResponse.body().path("errors");
		System.out.println(wrongClientIdResponseMessage.get(0));
		Assert.assertTrue(wrongClientIdResponseMessage.get(0).equals(ErrorCode.CLIENT_ID_NOT_FOUND));
	}

	@Test()
	public void testCreateTokenWithWrongChannel() {

		// Prepare request 
		GetAccessToken getAccessToken = new GetAccessToken();
		CommonHeader commonHeader = new CommonHeader();
		String clientId = "5254f087-1214-45cd-94ae-fda53c835197";
		String channel = "TOTEM";
		String username = "antonio.tarricone";
		String password = "antonio";
		String merchantId = "28405fHfk73x88D";
		String acquirerId = "4585625";
		String terminalId = "testTerm";
		getAccessToken.setClientId(clientId);
		commonHeader.setChannel(channel);
		getAccessToken.setUsername(username);
		getAccessToken.setPassword(username);
		getAccessToken.setScope("offline_access");
		commonHeader.setMerchantId(merchantId);
		commonHeader.setAcquirerId(acquirerId);
		commonHeader.setTerminalId(terminalId);
		RequestSpecification request = given().contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.formParam("client_id", clientId).formParam("username", username).formParam("password", password)
				.formParam("grant_type", "password").header("AcquirerId", acquirerId).header("TerminalId", terminalId)
				.header("MerchantId", merchantId).header("RequestId", "aB1aB1aB-aB1a-aB1a-aB1a-aB1aB1aB1abb")
				.header("Channel", channel);

		Response resp = request.when().log().all().post();

		resp.then().statusCode(401);

		ArrayList<String> responseMessage = resp.body().path("errors");
		System.out.println(resp.body().asPrettyString());
		Log.debug(responseMessage);
		Assert.assertTrue(responseMessage.get(0).equals(ErrorCode.CREDENTIALS_INCONSISTENCY));
	}

	@Test
	public void testCreateTokenWithWrongUser() {

		// Prepare request
		GetAccessToken getAccessToken = new GetAccessToken();
		CommonHeader commonHeader = new CommonHeader();
		String clientId = "5254f087-1214-45cd-94ae-fda53c835197";
		String channel = "POS";
		String username = "antonio.t";
		String password = "antonio";
		String merchantId = "28405fHfk73x88D";
		String acquirerId = "4585625";
		String terminalId = "testTerm";
		getAccessToken.setClientId(clientId);
		commonHeader.setChannel(channel);
		getAccessToken.setUsername(username);
		getAccessToken.setPassword(username);
		getAccessToken.setScope("offline_access");
		commonHeader.setMerchantId(merchantId);
		commonHeader.setAcquirerId(acquirerId);
		commonHeader.setTerminalId(terminalId);
		RequestSpecification request = given().contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.formParam("client_id", clientId).formParam("username", username).formParam("password", password)
				.formParam("grant_type", "password").header("AcquirerId", acquirerId).header("TerminalId", terminalId)
				.header("MerchantId", merchantId).header("RequestId", "aB1aB1aB-aB1a-aB1a-aB1a-aB1aB1aB1abb")
				.header("Channel", channel);

		Response resp = request.when().log().all().post();

		resp.then().statusCode(401);

		ArrayList<String> responseMessage = resp.body().path("errors");
		System.out.println(resp.body().asPrettyString());
		Log.debug(responseMessage);
		Assert.assertTrue(responseMessage.get(0).equals(ErrorCode.WRONG_CREDENTIALS));
	}

	@Test
	public void testCreateTokenWithWrongMerchant() {

		// Prepare request
		GetAccessToken getAccessToken = new GetAccessToken();
		CommonHeader commonHeader = new CommonHeader();
		String clientId = "5254f087-1214-45cd-94ae-fda53c835197";
		String channel = "POS";
		String username = "antonio.tarricone";
		String password = "antonio";
		String merchantId = "28405fHfk73x88E";
		String acquirerId = "4585625";
		String terminalId = "testTerm";
		getAccessToken.setClientId(clientId);
		commonHeader.setChannel(channel);
		getAccessToken.setUsername(username);
		getAccessToken.setPassword(username);
		getAccessToken.setScope("offline_access");
		commonHeader.setMerchantId(merchantId);
		commonHeader.setAcquirerId(acquirerId);
		commonHeader.setTerminalId(terminalId);
		RequestSpecification request = given().contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.formParam("client_id", clientId).formParam("username", username).formParam("password", password)
				.formParam("grant_type", "password").header("AcquirerId", acquirerId).header("TerminalId", terminalId)
				.header("MerchantId", merchantId).header("RequestId", "aB1aB1aB-aB1a-aB1a-aB1a-aB1aB1aB1abb")
				.header("Channel", channel);

		Response resp = request.when().log().all().post();

		resp.then().statusCode(401);

		ArrayList<String> responseMessage = resp.body().path("errors");
		System.out.println(resp.body().asPrettyString());
		Log.debug(responseMessage);
		Assert.assertTrue(responseMessage.get(0).equals(ErrorCode.CREDENTIALS_INCONSISTENCY));
	}

	 @Test
		public void testCreateTokenFromRefresh() {
		//TODO: Generate key ffrom key Generator.
		 String e ="AQAB";
		 KeyUse use = KeyUse.sig;
		 String kid = "82f6353e-39b6-4a60-9fe5-cdce55a290bd";
		 String n = "i5x1lYLgZXwOdGZb0m8Bx1IqsedN4P9qLSsokxmqFxu6My5bFGhhVWA4OHdtTPACfYVNoRY85aH7RJOKe06PIuSct4nkAS0VBCOWamaJ0_Z9H083fiLrfPEwViNVW8GGnlzxmNkPZ4TAP8K7ceI660NSe-0OsK87seRLEpfgGAb2yFxR5y1MMsO9lHxVfW7BlfAYpF8zvOAafq-OwwZ8Av4Tz2NwPZDcl6vQ_XE5l6yXD8zRJxn7oeycEj7zavx5WLFzSl9Ox6oxHb_nfi3i2mWMmkBU8sUItDm729vyeUJ1njjpkDkY4KtLJvf_txJ3XKbTwQJ-fUCmawlcCaPS0D71t6YdnUI3GdhApq9-xL8J5S8sgDZdbFDhKOnpWpOd1hd_igb8KZG2eRguFY2R0hjGmWUIiyJ-Vc8XmD1QZwOvutKsaL8pwgpeEJ-C1x4ltJ5QyDeUSE5hsZWrg_c-KqC7u-FxvPE05X-O4aepksTuk0D0eo9TOlA7zw61x3V-A_zAYBPpevsANf2oPIrTdgWbODGVC3cIJuRJMN4QELGqYN7gcLho_1uiR6XcOmfynYfSFRXVou78tEyV_27U7cvUXmY6-Xl5FmqMy3thqMd1-Ak_UW7GXhkakradcP8PvT9fwozIG53seH14X4WDzjvXIKaS0NMOUi0dHKIOpWk";
		 KeyType kty = KeyType.RSA;
		 long exp = 1680695652675l;
		 long iat = 1680609252675l;
		 KeyPair k = new KeyPair("OCCNJzAIHdSrjQFPvgCR0vvGHWKz7RZSLdmRhN4HAyZLC-OBi2lRZDz8LTtaLlm5bpQmevIRRnKCLXYxcNRK-Ei3dpRRnWVt8w2JnboehCAu-mTjFutcCA2BFMg8u1hI0ibknEH7ju8QSTH1INpGPKoCob7SrpasZZ61zcJQPeii0zdGWC3eWfRWpbj8t6JM4oY1qfN6Pd9M14yl1OMo_Rz_gttj1uSDwtg4-4NHF6Kz0o7RsSohf147-pvjSao58L3ab1Z0uhHCkEbD_vbcole8GJFvdXBgSPCDBWdeCoD8_0C7KZfC2CQVYRPH8uZSJWYOcKSbjKhun06SeW-qwyTiQZpL91txpkh7XNbx3RrKBMbX7NfJF6eTdcv2zCljzwUjrzhlvKZQXypXKjE9by4n6bRH49ZlO2dlq5B8f5jiwSTEl3C4t1NgT8gL9_Hy2BkmlrudXEZtNfONOkCKK47DBiwh0SFxNVsCMWF_qHvwUvQIBjjPpZABzSJSBch7rFVxepk1ch706Y9MuzKJ4Lc9po2KCnW4Bm2XHbUQjFONZUA4aL34RB8TIRlFFrISf9cOkWEx74BUUm3gPuniNwASQeJG9LbQEHfv4it979htxN8kGuMDqUlV4-X_rNSjYjaAoyJjsEnxZb7h96BtQ80LiY9P-xmUDNwhJWD5Isc", 
					"AQAB", 
					KeyUse.sig, 
					"82f6353e-39b6-4a60-9fe5-cdce55a290bd", 
					"ndDlP0YJEVxZ57ZVKZ4Ft6f3siaX3tCJkrUBABbsUysSC0LKrnmuABZTJ3fhWe-T8UPz57X20H9b6o-EyRgvDHlN4SklxTI9Nd87kKKyqAVgvelTwpaNz96lxgeBtC_CKgd9ww2P9Q0_1lAMndS7k8-mKcBnXWa6hDENYX_BAy04L4GnpsR4_gfe4z7ov2sbNO53i6nm1NQ1MqfQ1KQEbsLIva3V_iOay6Kqb6tm_WjlXi2nOPxGOkxSwJ-jUCMaIEAQWqMjgSYsXRS62C-96MqcL6Br09BqoHJlTQn8AksMEs_I2OMDcOLug8ynocN_CorPgSRuqgO9Ddr1I7e21Q", 
					"q49l5Yf4ta8JVuLCqHr5FwYAZKH_0QPOdAaS3z0NxgVbnH8I5iiojPg6mahnDclZQHlX8P_niUikeg-PQs4EIa7eGbp6TWrx0-KZoEEB2d5jwoB9O-ueCquHi1o13EzM7vadTBP_6gUIJt0MWXFTKzMZeuHtZS7FyNEDSN3vTdLekZzW8bbHTQUhBM0ZO0WFOHKToHcK3h1PxyAgddrJizJ-VHvr1hugt7BjJmI4YO20gJCmKSLWm13y2-D8tnPW8WH4ko8BGtMaiW7slo5ABeOAMmfesAcF-WnsEEs3vNoydmdXzFKjtR0lppFX9qCRjEma4zOhI-7vP_Ai6qY3hQ", 
					"i5x1lYLgZXwOdGZb0m8Bx1IqsedN4P9qLSsokxmqFxu6My5bFGhhVWA4OHdtTPACfYVNoRY85aH7RJOKe06PIuSct4nkAS0VBCOWamaJ0_Z9H083fiLrfPEwViNVW8GGnlzxmNkPZ4TAP8K7ceI660NSe-0OsK87seRLEpfgGAb2yFxR5y1MMsO9lHxVfW7BlfAYpF8zvOAafq-OwwZ8Av4Tz2NwPZDcl6vQ_XE5l6yXD8zRJxn7oeycEj7zavx5WLFzSl9Ox6oxHb_nfi3i2mWMmkBU8sUItDm729vyeUJ1njjpkDkY4KtLJvf_txJ3XKbTwQJ-fUCmawlcCaPS0D71t6YdnUI3GdhApq9-xL8J5S8sgDZdbFDhKOnpWpOd1hd_igb8KZG2eRguFY2R0hjGmWUIiyJ-Vc8XmD1QZwOvutKsaL8pwgpeEJ-C1x4ltJ5QyDeUSE5hsZWrg_c-KqC7u-FxvPE05X-O4aepksTuk0D0eo9TOlA7zw61x3V-A_zAYBPpevsANf2oPIrTdgWbODGVC3cIJuRJMN4QELGqYN7gcLho_1uiR6XcOmfynYfSFRXVou78tEyV_27U7cvUXmY6-Xl5FmqMy3thqMd1-Ak_UW7GXhkakradcP8PvT9fwozIG53seH14X4WDzjvXIKaS0NMOUi0dHKIOpWk",
					"vKI7tSmL0Hn4C1sm36irGQr83O2RJXmg76SBX4R_h2Ne7mIYTaMu1ozjM2ajJHzbciMnTUfBWpQ2xEf5UR-SgZgBPn6PxzAcbN0qs6w8OUw_eeEZY7ZOhltLpmFbM83w3Lg32lPZpuJbLHnkhJFhPRg499KV3SkYkChFyPxvbyQzRM0h2iA2_lno_LZLpfZ4B71ZEOYsbI7-AXYhzjbIDPUZrBUe9If3CDI6rAOBO1LQBs_oIDat7AwgJUnMLF3WnHVTKsZZH24XmlVeAmvYL3oBQwCt2ukpUxDc-H-Ps_wSB-r87lKWHdFdC_oKK8WylEbUHuiIZs2sZTk1fa76Jw",
					KeyType.RSA, 
					"vXhbi-11pjdnjWdppWEHMhk-LP8Yk6g78kBghSVw6Pfe8hgy5I-qx7S6VmD2yUHjNS9t_ZnLqv-IdnZCq3XPY4lQHPU5TDu8l-fUvAgejiwly1YHNvjsxzV-hiwYFTOkxFpezF4HA71vovqEkhQWqMXF2yxf8GJWQrBbFbReO8Q-ltk8rJVwxm1yHO4WqJohwtOyHrWzqjRT_IOOp402C6FxuHvmFyNKuUgMChDsNmHhtVsdQ9z4Kq0e8GibnIE8Se2hgkn1FbwWLRiSj5BqzCKkP_w1WtV21a3v8b5XZsahOXqEjvVcoyQaR45PzvYEqax16cAwaw5T13IrNBvt7w",
					"hsnut4VDuwCg-IEGbzthY6oVjJwUuSMs6atdyyBMcnWKV-sqYUqIP8iAqQBjFYcEwsVCbb_AubAbHrlQm42T2XCweTmnvZXqI7PMK5_aYp9DTwqI30Sr-usgjgQJkyMUG7jQj-EvY99xkYX7j9ilrslnkuE01OY0PWkvR_Jo09seG68EUBq3TBSOdSP_Cuwi9JxripytPVOXfnsdn4zuTxxjE8ZSxq6Ql6nm5tQ-Ni6JdQWzE6JqCTuwUtPGYP6hcCeASTGKTusy7t3zMkC1hdT5o77dlTGkbZt60gA-alRQoy8f0UkuDXgiIwDr9FILTyZHiDqu1UavDA-PfJ_PSA",
	1680695652675l, 1680609252675l);
			Mockito.when(keyRetriever.getKeyPair()).thenReturn(Uni.createFrom().item(k)); 
			PublicKey pk = new PublicKey(e, use, kid, n, kty, exp, iat);
			Mockito.when(keyRetriever.getPublicKey(kid))
			.thenReturn(Uni.createFrom().item(Optional.of(pk)));
			//Need to simulate a key pair object.
			// Prepare request
			GetAccessToken getAccessToken = new GetAccessToken();
			CommonHeader commonHeader = new CommonHeader();
			String clientId = "5254f087-1214-45cd-94ae-fda53c835197";
			String channel = "POS";
			String username = "antonio.tarricone";
			String password = "antonio";
			String merchantId = "28405fHfk73x88D";
			String acquirerId = "4585625";
			String terminalId = "testTerm";
			getAccessToken.setClientId(clientId);
			commonHeader.setChannel(channel);
			getAccessToken.setUsername(username);
			getAccessToken.setPassword(username);
			getAccessToken.setScope("offline_access");
			commonHeader.setMerchantId(merchantId);
			commonHeader.setAcquirerId(acquirerId);
			commonHeader.setTerminalId(terminalId);
			RequestSpecification request = given().contentType(MediaType.APPLICATION_FORM_URLENCODED)
					.formParam("client_id", clientId).formParam("username", username).formParam("password", password)
					.formParam("scope", "offline_access").formParam("grant_type", "password")
					.header("AcquirerId", acquirerId).header("TerminalId", terminalId).header("MerchantId", merchantId)
					.header("RequestId", "aB1aB1aB-aB1a-aB1a-aB1a-aB1aB1aB1abb").header("Channel", channel);

			Response resp = request.when().log().all().post();

			JsonPath responsePath = resp.body().jsonPath();
			Map<Object, Object> responseMap = responsePath.getMap("");
			//System.out.println(resp.body().asPrettyString());

			Assert.assertTrue(responseMap.containsKey("refresh_token"));
			Assert.assertTrue(responseMap.containsKey("access_token"));
			Assert.assertTrue(responseMap.containsKey("token_type"));
			Assert.assertTrue(responseMap.containsKey("expires_in"));

			String refreshToken = responsePath.getString("refresh_token");
		resp.then().statusCode(200);
			System.out.println(refreshToken);

			RequestSpecification refreshRequest = given().contentType(MediaType.APPLICATION_FORM_URLENCODED)
					.formParam("client_id", clientId).formParam("scope", "offline_access")
					.formParam("refresh_token", refreshToken).formParam("grant_type", "refresh_token")
					.header("AcquirerId", acquirerId).header("TerminalId", terminalId).header("MerchantId", merchantId)
					.header("RequestId", "aB1aB1aB-aB1a-aB1a-aB1a-aB1aB1aB1abb").header("Channel", channel);

			Response refreshResp = refreshRequest.when().log().all().post();
			refreshResp.then().statusCode(200);

		}

	 @Test
		public void testCreateTokenFromRefreshExpiredKey() {
	
		 KeyPair k = new KeyPair("OCCNJzAIHdSrjQFPvgCR0vvGHWKz7RZSLdmRhN4HAyZLC-OBi2lRZDz8LTtaLlm5bpQmevIRRnKCLXYxcNRK-Ei3dpRRnWVt8w2JnboehCAu-mTjFutcCA2BFMg8u1hI0ibknEH7ju8QSTH1INpGPKoCob7SrpasZZ61zcJQPeii0zdGWC3eWfRWpbj8t6JM4oY1qfN6Pd9M14yl1OMo_Rz_gttj1uSDwtg4-4NHF6Kz0o7RsSohf147-pvjSao58L3ab1Z0uhHCkEbD_vbcole8GJFvdXBgSPCDBWdeCoD8_0C7KZfC2CQVYRPH8uZSJWYOcKSbjKhun06SeW-qwyTiQZpL91txpkh7XNbx3RrKBMbX7NfJF6eTdcv2zCljzwUjrzhlvKZQXypXKjE9by4n6bRH49ZlO2dlq5B8f5jiwSTEl3C4t1NgT8gL9_Hy2BkmlrudXEZtNfONOkCKK47DBiwh0SFxNVsCMWF_qHvwUvQIBjjPpZABzSJSBch7rFVxepk1ch706Y9MuzKJ4Lc9po2KCnW4Bm2XHbUQjFONZUA4aL34RB8TIRlFFrISf9cOkWEx74BUUm3gPuniNwASQeJG9LbQEHfv4it979htxN8kGuMDqUlV4-X_rNSjYjaAoyJjsEnxZb7h96BtQ80LiY9P-xmUDNwhJWD5Isc", 
					"AQAB", 
					KeyUse.sig, 
					"82f6353e-39b6-4a60-9fe5-cdce55a290bd", 
					"ndDlP0YJEVxZ57ZVKZ4Ft6f3siaX3tCJkrUBABbsUysSC0LKrnmuABZTJ3fhWe-T8UPz57X20H9b6o-EyRgvDHlN4SklxTI9Nd87kKKyqAVgvelTwpaNz96lxgeBtC_CKgd9ww2P9Q0_1lAMndS7k8-mKcBnXWa6hDENYX_BAy04L4GnpsR4_gfe4z7ov2sbNO53i6nm1NQ1MqfQ1KQEbsLIva3V_iOay6Kqb6tm_WjlXi2nOPxGOkxSwJ-jUCMaIEAQWqMjgSYsXRS62C-96MqcL6Br09BqoHJlTQn8AksMEs_I2OMDcOLug8ynocN_CorPgSRuqgO9Ddr1I7e21Q", 
					"q49l5Yf4ta8JVuLCqHr5FwYAZKH_0QPOdAaS3z0NxgVbnH8I5iiojPg6mahnDclZQHlX8P_niUikeg-PQs4EIa7eGbp6TWrx0-KZoEEB2d5jwoB9O-ueCquHi1o13EzM7vadTBP_6gUIJt0MWXFTKzMZeuHtZS7FyNEDSN3vTdLekZzW8bbHTQUhBM0ZO0WFOHKToHcK3h1PxyAgddrJizJ-VHvr1hugt7BjJmI4YO20gJCmKSLWm13y2-D8tnPW8WH4ko8BGtMaiW7slo5ABeOAMmfesAcF-WnsEEs3vNoydmdXzFKjtR0lppFX9qCRjEma4zOhI-7vP_Ai6qY3hQ", 
					"i5x1lYLgZXwOdGZb0m8Bx1IqsedN4P9qLSsokxmqFxu6My5bFGhhVWA4OHdtTPACfYVNoRY85aH7RJOKe06PIuSct4nkAS0VBCOWamaJ0_Z9H083fiLrfPEwViNVW8GGnlzxmNkPZ4TAP8K7ceI660NSe-0OsK87seRLEpfgGAb2yFxR5y1MMsO9lHxVfW7BlfAYpF8zvOAafq-OwwZ8Av4Tz2NwPZDcl6vQ_XE5l6yXD8zRJxn7oeycEj7zavx5WLFzSl9Ox6oxHb_nfi3i2mWMmkBU8sUItDm729vyeUJ1njjpkDkY4KtLJvf_txJ3XKbTwQJ-fUCmawlcCaPS0D71t6YdnUI3GdhApq9-xL8J5S8sgDZdbFDhKOnpWpOd1hd_igb8KZG2eRguFY2R0hjGmWUIiyJ-Vc8XmD1QZwOvutKsaL8pwgpeEJ-C1x4ltJ5QyDeUSE5hsZWrg_c-KqC7u-FxvPE05X-O4aepksTuk0D0eo9TOlA7zw61x3V-A_zAYBPpevsANf2oPIrTdgWbODGVC3cIJuRJMN4QELGqYN7gcLho_1uiR6XcOmfynYfSFRXVou78tEyV_27U7cvUXmY6-Xl5FmqMy3thqMd1-Ak_UW7GXhkakradcP8PvT9fwozIG53seH14X4WDzjvXIKaS0NMOUi0dHKIOpWk",
					"vKI7tSmL0Hn4C1sm36irGQr83O2RJXmg76SBX4R_h2Ne7mIYTaMu1ozjM2ajJHzbciMnTUfBWpQ2xEf5UR-SgZgBPn6PxzAcbN0qs6w8OUw_eeEZY7ZOhltLpmFbM83w3Lg32lPZpuJbLHnkhJFhPRg499KV3SkYkChFyPxvbyQzRM0h2iA2_lno_LZLpfZ4B71ZEOYsbI7-AXYhzjbIDPUZrBUe9If3CDI6rAOBO1LQBs_oIDat7AwgJUnMLF3WnHVTKsZZH24XmlVeAmvYL3oBQwCt2ukpUxDc-H-Ps_wSB-r87lKWHdFdC_oKK8WylEbUHuiIZs2sZTk1fa76Jw",
					KeyType.RSA, 
					"vXhbi-11pjdnjWdppWEHMhk-LP8Yk6g78kBghSVw6Pfe8hgy5I-qx7S6VmD2yUHjNS9t_ZnLqv-IdnZCq3XPY4lQHPU5TDu8l-fUvAgejiwly1YHNvjsxzV-hiwYFTOkxFpezF4HA71vovqEkhQWqMXF2yxf8GJWQrBbFbReO8Q-ltk8rJVwxm1yHO4WqJohwtOyHrWzqjRT_IOOp402C6FxuHvmFyNKuUgMChDsNmHhtVsdQ9z4Kq0e8GibnIE8Se2hgkn1FbwWLRiSj5BqzCKkP_w1WtV21a3v8b5XZsahOXqEjvVcoyQaR45PzvYEqax16cAwaw5T13IrNBvt7w",
					"hsnut4VDuwCg-IEGbzthY6oVjJwUuSMs6atdyyBMcnWKV-sqYUqIP8iAqQBjFYcEwsVCbb_AubAbHrlQm42T2XCweTmnvZXqI7PMK5_aYp9DTwqI30Sr-usgjgQJkyMUG7jQj-EvY99xkYX7j9ilrslnkuE01OY0PWkvR_Jo09seG68EUBq3TBSOdSP_Cuwi9JxripytPVOXfnsdn4zuTxxjE8ZSxq6Ql6nm5tQ-Ni6JdQWzE6JqCTuwUtPGYP6hcCeASTGKTusy7t3zMkC1hdT5o77dlTGkbZt60gA-alRQoy8f0UkuDXgiIwDr9FILTyZHiDqu1UavDA-PfJ_PSA",
	1680695652675l, 1680609252675l);
			Mockito.when(keyRetriever.getKeyPair()).thenReturn(Uni.createFrom().item(k)); 
			Mockito.when(keyRetriever.getPublicKey(anyString()))
			.thenReturn(Uni.createFrom().item(Optional.empty()));
			//Need to simulate a key pair object.
			// Prepare request
			GetAccessToken getAccessToken = new GetAccessToken();
			CommonHeader commonHeader = new CommonHeader();
			String clientId = "5254f087-1214-45cd-94ae-fda53c835197";
			String channel = "POS";
			String username = "antonio.tarricone";
			String password = "antonio";
			String merchantId = "28405fHfk73x88D";
			String acquirerId = "4585625";
			String terminalId = "testTerm";
			getAccessToken.setClientId(clientId);
			commonHeader.setChannel(channel);
			getAccessToken.setUsername(username);
			getAccessToken.setPassword(username);
			getAccessToken.setScope("offline_access");
			commonHeader.setMerchantId(merchantId);
			commonHeader.setAcquirerId(acquirerId);
			commonHeader.setTerminalId(terminalId);
			RequestSpecification request = given().contentType(MediaType.APPLICATION_FORM_URLENCODED)
					.formParam("client_id", clientId).formParam("username", username).formParam("password", password)
					.formParam("scope", "offline_access").formParam("grant_type", "password")
					.header("AcquirerId", acquirerId).header("TerminalId", terminalId).header("MerchantId", merchantId)
					.header("RequestId", "aB1aB1aB-aB1a-aB1a-aB1a-aB1aB1aB1abb").header("Channel", channel);

			Response resp = request.when().log().all().post();

			JsonPath responsePath = resp.body().jsonPath();
			Map<Object, Object> responseMap = responsePath.getMap("");
			//System.out.println(resp.body().asPrettyString());

			Assert.assertTrue(responseMap.containsKey("refresh_token"));
			Assert.assertTrue(responseMap.containsKey("access_token"));
			Assert.assertTrue(responseMap.containsKey("token_type"));
			Assert.assertTrue(responseMap.containsKey("expires_in"));

			String refreshToken = responsePath.getString("refresh_token");
		resp.then().statusCode(200);
			System.out.println(refreshToken);

			RequestSpecification refreshRequest = given().contentType(MediaType.APPLICATION_FORM_URLENCODED)
					.formParam("client_id", clientId).formParam("scope", "offline_access")
					.formParam("refresh_token", refreshToken).formParam("grant_type", "refresh_token")
					.header("AcquirerId", acquirerId).header("TerminalId", terminalId).header("MerchantId", merchantId)
					.header("RequestId", "aB1aB1aB-aB1a-aB1a-aB1a-aB1aB1aB1abb").header("Channel", channel);
			Response refreshResp = refreshRequest.when().log().all().post();
			ArrayList<String> responseMessage = refreshResp.body().path("errors");
			Assert.assertTrue(responseMessage.get(0).equals(ErrorCode.KEY_NOT_FOUND));
			refreshResp.then().statusCode(401);

		}

	 @Test
		public void testCreateTokenWithNoRefresh() {

		 String kid = "82f6353e-39b6-4a60-9fe5-cdce55a290bd";
		 KeyPair k = new KeyPair("OCCNJzAIHdSrjQFPvgCR0vvGHWKz7RZSLdmRhN4HAyZLC-OBi2lRZDz8LTtaLlm5bpQmevIRRnKCLXYxcNRK-Ei3dpRRnWVt8w2JnboehCAu-mTjFutcCA2BFMg8u1hI0ibknEH7ju8QSTH1INpGPKoCob7SrpasZZ61zcJQPeii0zdGWC3eWfRWpbj8t6JM4oY1qfN6Pd9M14yl1OMo_Rz_gttj1uSDwtg4-4NHF6Kz0o7RsSohf147-pvjSao58L3ab1Z0uhHCkEbD_vbcole8GJFvdXBgSPCDBWdeCoD8_0C7KZfC2CQVYRPH8uZSJWYOcKSbjKhun06SeW-qwyTiQZpL91txpkh7XNbx3RrKBMbX7NfJF6eTdcv2zCljzwUjrzhlvKZQXypXKjE9by4n6bRH49ZlO2dlq5B8f5jiwSTEl3C4t1NgT8gL9_Hy2BkmlrudXEZtNfONOkCKK47DBiwh0SFxNVsCMWF_qHvwUvQIBjjPpZABzSJSBch7rFVxepk1ch706Y9MuzKJ4Lc9po2KCnW4Bm2XHbUQjFONZUA4aL34RB8TIRlFFrISf9cOkWEx74BUUm3gPuniNwASQeJG9LbQEHfv4it979htxN8kGuMDqUlV4-X_rNSjYjaAoyJjsEnxZb7h96BtQ80LiY9P-xmUDNwhJWD5Isc", 
					"AQAB", 
					KeyUse.sig, 
					"82f6353e-39b6-4a60-9fe5-cdce55a290bd", 
					"ndDlP0YJEVxZ57ZVKZ4Ft6f3siaX3tCJkrUBABbsUysSC0LKrnmuABZTJ3fhWe-T8UPz57X20H9b6o-EyRgvDHlN4SklxTI9Nd87kKKyqAVgvelTwpaNz96lxgeBtC_CKgd9ww2P9Q0_1lAMndS7k8-mKcBnXWa6hDENYX_BAy04L4GnpsR4_gfe4z7ov2sbNO53i6nm1NQ1MqfQ1KQEbsLIva3V_iOay6Kqb6tm_WjlXi2nOPxGOkxSwJ-jUCMaIEAQWqMjgSYsXRS62C-96MqcL6Br09BqoHJlTQn8AksMEs_I2OMDcOLug8ynocN_CorPgSRuqgO9Ddr1I7e21Q", 
					"q49l5Yf4ta8JVuLCqHr5FwYAZKH_0QPOdAaS3z0NxgVbnH8I5iiojPg6mahnDclZQHlX8P_niUikeg-PQs4EIa7eGbp6TWrx0-KZoEEB2d5jwoB9O-ueCquHi1o13EzM7vadTBP_6gUIJt0MWXFTKzMZeuHtZS7FyNEDSN3vTdLekZzW8bbHTQUhBM0ZO0WFOHKToHcK3h1PxyAgddrJizJ-VHvr1hugt7BjJmI4YO20gJCmKSLWm13y2-D8tnPW8WH4ko8BGtMaiW7slo5ABeOAMmfesAcF-WnsEEs3vNoydmdXzFKjtR0lppFX9qCRjEma4zOhI-7vP_Ai6qY3hQ", 
					"i5x1lYLgZXwOdGZb0m8Bx1IqsedN4P9qLSsokxmqFxu6My5bFGhhVWA4OHdtTPACfYVNoRY85aH7RJOKe06PIuSct4nkAS0VBCOWamaJ0_Z9H083fiLrfPEwViNVW8GGnlzxmNkPZ4TAP8K7ceI660NSe-0OsK87seRLEpfgGAb2yFxR5y1MMsO9lHxVfW7BlfAYpF8zvOAafq-OwwZ8Av4Tz2NwPZDcl6vQ_XE5l6yXD8zRJxn7oeycEj7zavx5WLFzSl9Ox6oxHb_nfi3i2mWMmkBU8sUItDm729vyeUJ1njjpkDkY4KtLJvf_txJ3XKbTwQJ-fUCmawlcCaPS0D71t6YdnUI3GdhApq9-xL8J5S8sgDZdbFDhKOnpWpOd1hd_igb8KZG2eRguFY2R0hjGmWUIiyJ-Vc8XmD1QZwOvutKsaL8pwgpeEJ-C1x4ltJ5QyDeUSE5hsZWrg_c-KqC7u-FxvPE05X-O4aepksTuk0D0eo9TOlA7zw61x3V-A_zAYBPpevsANf2oPIrTdgWbODGVC3cIJuRJMN4QELGqYN7gcLho_1uiR6XcOmfynYfSFRXVou78tEyV_27U7cvUXmY6-Xl5FmqMy3thqMd1-Ak_UW7GXhkakradcP8PvT9fwozIG53seH14X4WDzjvXIKaS0NMOUi0dHKIOpWk",
					"vKI7tSmL0Hn4C1sm36irGQr83O2RJXmg76SBX4R_h2Ne7mIYTaMu1ozjM2ajJHzbciMnTUfBWpQ2xEf5UR-SgZgBPn6PxzAcbN0qs6w8OUw_eeEZY7ZOhltLpmFbM83w3Lg32lPZpuJbLHnkhJFhPRg499KV3SkYkChFyPxvbyQzRM0h2iA2_lno_LZLpfZ4B71ZEOYsbI7-AXYhzjbIDPUZrBUe9If3CDI6rAOBO1LQBs_oIDat7AwgJUnMLF3WnHVTKsZZH24XmlVeAmvYL3oBQwCt2ukpUxDc-H-Ps_wSB-r87lKWHdFdC_oKK8WylEbUHuiIZs2sZTk1fa76Jw",
					KeyType.RSA, 
					"vXhbi-11pjdnjWdppWEHMhk-LP8Yk6g78kBghSVw6Pfe8hgy5I-qx7S6VmD2yUHjNS9t_ZnLqv-IdnZCq3XPY4lQHPU5TDu8l-fUvAgejiwly1YHNvjsxzV-hiwYFTOkxFpezF4HA71vovqEkhQWqMXF2yxf8GJWQrBbFbReO8Q-ltk8rJVwxm1yHO4WqJohwtOyHrWzqjRT_IOOp402C6FxuHvmFyNKuUgMChDsNmHhtVsdQ9z4Kq0e8GibnIE8Se2hgkn1FbwWLRiSj5BqzCKkP_w1WtV21a3v8b5XZsahOXqEjvVcoyQaR45PzvYEqax16cAwaw5T13IrNBvt7w",
					"hsnut4VDuwCg-IEGbzthY6oVjJwUuSMs6atdyyBMcnWKV-sqYUqIP8iAqQBjFYcEwsVCbb_AubAbHrlQm42T2XCweTmnvZXqI7PMK5_aYp9DTwqI30Sr-usgjgQJkyMUG7jQj-EvY99xkYX7j9ilrslnkuE01OY0PWkvR_Jo09seG68EUBq3TBSOdSP_Cuwi9JxripytPVOXfnsdn4zuTxxjE8ZSxq6Ql6nm5tQ-Ni6JdQWzE6JqCTuwUtPGYP6hcCeASTGKTusy7t3zMkC1hdT5o77dlTGkbZt60gA-alRQoy8f0UkuDXgiIwDr9FILTyZHiDqu1UavDA-PfJ_PSA",
	1680695652675l, 1680609252675l);
			Mockito.when(keyRetriever.getKeyPair()).thenReturn(Uni.createFrom().item(k)); 
			Mockito.when(keyRetriever.getPublicKey(kid))
			.thenReturn(Uni.createFrom().item(Optional.empty()));
			//Need to simulate a key pair object.
			// Prepare request
			GetAccessToken getAccessToken = new GetAccessToken();
			CommonHeader commonHeader = new CommonHeader();
			String clientId = "5254f087-1214-45cd-94ae-fda53c835197";
			String channel = "POS";
			String username = "antonio.tarricone";
			String password = "antonio";
			String merchantId = "28405fHfk73x88D";
			String acquirerId = "4585625";
			String terminalId = "testTerm";
			getAccessToken.setClientId(clientId);
			commonHeader.setChannel(channel);
			getAccessToken.setUsername(username);
			getAccessToken.setPassword(username);
			getAccessToken.setScope("offline_access");
			commonHeader.setMerchantId(merchantId);
			commonHeader.setAcquirerId(acquirerId);
			commonHeader.setTerminalId(terminalId);
			RequestSpecification request = given().contentType(MediaType.APPLICATION_FORM_URLENCODED)
					.formParam("client_id", clientId).formParam("username", username).formParam("password", password)
					.formParam("scope", "offline_access").formParam("grant_type", "password")
					.header("AcquirerId", acquirerId).header("TerminalId", terminalId).header("MerchantId", merchantId)
					.header("RequestId", "aB1aB1aB-aB1a-aB1a-aB1a-aB1aB1aB1abb").header("Channel", channel);

			Response resp = request.when().log().all().post();

			JsonPath responsePath = resp.body().jsonPath();
			Map<Object, Object> responseMap = responsePath.getMap("");
			//System.out.println(resp.body().asPrettyString());

			Assert.assertTrue(responseMap.containsKey("refresh_token"));
			Assert.assertTrue(responseMap.containsKey("access_token"));
			Assert.assertTrue(responseMap.containsKey("token_type"));
			Assert.assertTrue(responseMap.containsKey("expires_in"));

			String refreshToken = "";
		resp.then().statusCode(200);
			System.out.println(refreshToken);

			RequestSpecification refreshRequest = given().contentType(MediaType.APPLICATION_FORM_URLENCODED)
					.formParam("client_id", clientId).formParam("scope", "offline_access")
					.formParam("refresh_token", refreshToken).formParam("grant_type", "refresh_token")
					.header("AcquirerId", acquirerId).header("TerminalId", terminalId).header("MerchantId", merchantId)
					.header("RequestId", "aB1aB1aB-aB1a-aB1a-aB1a-aB1aB1aB1abb").header("Channel", channel);
			Response refreshResp = refreshRequest.when().log().all().post();
			ArrayList<String> responseMessage = refreshResp.body().path("errors");
			Assert.assertTrue(responseMessage.get(0).equals(ErrorCode.REFRESH_TOKEN_MUST_MATCH_REGEXP));
			refreshResp.then().statusCode(400);

		}


	 
 	@Test
 	public void testCreateTokenWithWrongAcquirer() {

		// Prepare request
		GetAccessToken getAccessToken = new GetAccessToken();
		CommonHeader commonHeader = new CommonHeader();
		String clientId = "5254f087-1214-45cd-94ae-fda53c835197";
		String channel = "POS";
		String username = "antonio.tarricone";
		String password = "antonio";
		String merchantId = "28405fHfk73x88D";
		String acquirerId = "4585627";
		String terminalId = "testTerm";
		getAccessToken.setClientId(clientId);
		commonHeader.setChannel(channel);
		getAccessToken.setUsername(username);
		getAccessToken.setPassword(username);
		getAccessToken.setScope("offline_access");
		commonHeader.setMerchantId(merchantId);
		commonHeader.setAcquirerId(acquirerId);
		commonHeader.setTerminalId(terminalId);
		RequestSpecification request = given().contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.formParam("client_id", clientId).formParam("username", username).formParam("password", password)
				.formParam("grant_type", "password").header("AcquirerId", acquirerId).header("TerminalId", terminalId)
				.header("MerchantId", merchantId).header("RequestId", "aB1aB1aB-aB1a-aB1a-aB1a-aB1aB1aB1abb")
				.header("Channel", channel);

		Response resp = request.when().log().all().post(); 
		resp.then().statusCode(401);
		ArrayList<String> responseMessage = resp.body().path("errors");
		
		Assert.assertTrue(responseMessage.get(0).equals(ErrorCode.CREDENTIALS_INCONSISTENCY));
	}
	
	@Test
	public void testCreatePoyntToken() {
		javax.ws.rs.core.Response mockResponse = buildMockResponse(Status.UNAUTHORIZED, ErrorCode.EXT_TOKEN_NOT_VALID);
		Mockito.when(poyntClient.getBusinessObject(anyString(), anyString()))
		.thenReturn(Uni.createFrom().item(mockResponse));
		 String e ="AQAB";
		 KeyUse use = KeyUse.sig;
		 String kid = "82f6353e-39b6-4a60-9fe5-cdce55a290bd";
		 String n = "i5x1lYLgZXwOdGZb0m8Bx1IqsedN4P9qLSsokxmqFxu6My5bFGhhVWA4OHdtTPACfYVNoRY85aH7RJOKe06PIuSct4nkAS0VBCOWamaJ0_Z9H083fiLrfPEwViNVW8GGnlzxmNkPZ4TAP8K7ceI660NSe-0OsK87seRLEpfgGAb2yFxR5y1MMsO9lHxVfW7BlfAYpF8zvOAafq-OwwZ8Av4Tz2NwPZDcl6vQ_XE5l6yXD8zRJxn7oeycEj7zavx5WLFzSl9Ox6oxHb_nfi3i2mWMmkBU8sUItDm729vyeUJ1njjpkDkY4KtLJvf_txJ3XKbTwQJ-fUCmawlcCaPS0D71t6YdnUI3GdhApq9-xL8J5S8sgDZdbFDhKOnpWpOd1hd_igb8KZG2eRguFY2R0hjGmWUIiyJ-Vc8XmD1QZwOvutKsaL8pwgpeEJ-C1x4ltJ5QyDeUSE5hsZWrg_c-KqC7u-FxvPE05X-O4aepksTuk0D0eo9TOlA7zw61x3V-A_zAYBPpevsANf2oPIrTdgWbODGVC3cIJuRJMN4QELGqYN7gcLho_1uiR6XcOmfynYfSFRXVou78tEyV_27U7cvUXmY6-Xl5FmqMy3thqMd1-Ak_UW7GXhkakradcP8PvT9fwozIG53seH14X4WDzjvXIKaS0NMOUi0dHKIOpWk";
		 KeyType kty = KeyType.RSA;
		 long exp = 1680695652675l;
		 long iat = 1680609252675l;
		 KeyPair k = new KeyPair("OCCNJzAIHdSrjQFPvgCR0vvGHWKz7RZSLdmRhN4HAyZLC-OBi2lRZDz8LTtaLlm5bpQmevIRRnKCLXYxcNRK-Ei3dpRRnWVt8w2JnboehCAu-mTjFutcCA2BFMg8u1hI0ibknEH7ju8QSTH1INpGPKoCob7SrpasZZ61zcJQPeii0zdGWC3eWfRWpbj8t6JM4oY1qfN6Pd9M14yl1OMo_Rz_gttj1uSDwtg4-4NHF6Kz0o7RsSohf147-pvjSao58L3ab1Z0uhHCkEbD_vbcole8GJFvdXBgSPCDBWdeCoD8_0C7KZfC2CQVYRPH8uZSJWYOcKSbjKhun06SeW-qwyTiQZpL91txpkh7XNbx3RrKBMbX7NfJF6eTdcv2zCljzwUjrzhlvKZQXypXKjE9by4n6bRH49ZlO2dlq5B8f5jiwSTEl3C4t1NgT8gL9_Hy2BkmlrudXEZtNfONOkCKK47DBiwh0SFxNVsCMWF_qHvwUvQIBjjPpZABzSJSBch7rFVxepk1ch706Y9MuzKJ4Lc9po2KCnW4Bm2XHbUQjFONZUA4aL34RB8TIRlFFrISf9cOkWEx74BUUm3gPuniNwASQeJG9LbQEHfv4it979htxN8kGuMDqUlV4-X_rNSjYjaAoyJjsEnxZb7h96BtQ80LiY9P-xmUDNwhJWD5Isc", 
					"AQAB", 
					KeyUse.sig, 
					"82f6353e-39b6-4a60-9fe5-cdce55a290bd", 
					"ndDlP0YJEVxZ57ZVKZ4Ft6f3siaX3tCJkrUBABbsUysSC0LKrnmuABZTJ3fhWe-T8UPz57X20H9b6o-EyRgvDHlN4SklxTI9Nd87kKKyqAVgvelTwpaNz96lxgeBtC_CKgd9ww2P9Q0_1lAMndS7k8-mKcBnXWa6hDENYX_BAy04L4GnpsR4_gfe4z7ov2sbNO53i6nm1NQ1MqfQ1KQEbsLIva3V_iOay6Kqb6tm_WjlXi2nOPxGOkxSwJ-jUCMaIEAQWqMjgSYsXRS62C-96MqcL6Br09BqoHJlTQn8AksMEs_I2OMDcOLug8ynocN_CorPgSRuqgO9Ddr1I7e21Q", 
					"q49l5Yf4ta8JVuLCqHr5FwYAZKH_0QPOdAaS3z0NxgVbnH8I5iiojPg6mahnDclZQHlX8P_niUikeg-PQs4EIa7eGbp6TWrx0-KZoEEB2d5jwoB9O-ueCquHi1o13EzM7vadTBP_6gUIJt0MWXFTKzMZeuHtZS7FyNEDSN3vTdLekZzW8bbHTQUhBM0ZO0WFOHKToHcK3h1PxyAgddrJizJ-VHvr1hugt7BjJmI4YO20gJCmKSLWm13y2-D8tnPW8WH4ko8BGtMaiW7slo5ABeOAMmfesAcF-WnsEEs3vNoydmdXzFKjtR0lppFX9qCRjEma4zOhI-7vP_Ai6qY3hQ", 
					"i5x1lYLgZXwOdGZb0m8Bx1IqsedN4P9qLSsokxmqFxu6My5bFGhhVWA4OHdtTPACfYVNoRY85aH7RJOKe06PIuSct4nkAS0VBCOWamaJ0_Z9H083fiLrfPEwViNVW8GGnlzxmNkPZ4TAP8K7ceI660NSe-0OsK87seRLEpfgGAb2yFxR5y1MMsO9lHxVfW7BlfAYpF8zvOAafq-OwwZ8Av4Tz2NwPZDcl6vQ_XE5l6yXD8zRJxn7oeycEj7zavx5WLFzSl9Ox6oxHb_nfi3i2mWMmkBU8sUItDm729vyeUJ1njjpkDkY4KtLJvf_txJ3XKbTwQJ-fUCmawlcCaPS0D71t6YdnUI3GdhApq9-xL8J5S8sgDZdbFDhKOnpWpOd1hd_igb8KZG2eRguFY2R0hjGmWUIiyJ-Vc8XmD1QZwOvutKsaL8pwgpeEJ-C1x4ltJ5QyDeUSE5hsZWrg_c-KqC7u-FxvPE05X-O4aepksTuk0D0eo9TOlA7zw61x3V-A_zAYBPpevsANf2oPIrTdgWbODGVC3cIJuRJMN4QELGqYN7gcLho_1uiR6XcOmfynYfSFRXVou78tEyV_27U7cvUXmY6-Xl5FmqMy3thqMd1-Ak_UW7GXhkakradcP8PvT9fwozIG53seH14X4WDzjvXIKaS0NMOUi0dHKIOpWk",
					"vKI7tSmL0Hn4C1sm36irGQr83O2RJXmg76SBX4R_h2Ne7mIYTaMu1ozjM2ajJHzbciMnTUfBWpQ2xEf5UR-SgZgBPn6PxzAcbN0qs6w8OUw_eeEZY7ZOhltLpmFbM83w3Lg32lPZpuJbLHnkhJFhPRg499KV3SkYkChFyPxvbyQzRM0h2iA2_lno_LZLpfZ4B71ZEOYsbI7-AXYhzjbIDPUZrBUe9If3CDI6rAOBO1LQBs_oIDat7AwgJUnMLF3WnHVTKsZZH24XmlVeAmvYL3oBQwCt2ukpUxDc-H-Ps_wSB-r87lKWHdFdC_oKK8WylEbUHuiIZs2sZTk1fa76Jw",
					KeyType.RSA, 
					"vXhbi-11pjdnjWdppWEHMhk-LP8Yk6g78kBghSVw6Pfe8hgy5I-qx7S6VmD2yUHjNS9t_ZnLqv-IdnZCq3XPY4lQHPU5TDu8l-fUvAgejiwly1YHNvjsxzV-hiwYFTOkxFpezF4HA71vovqEkhQWqMXF2yxf8GJWQrBbFbReO8Q-ltk8rJVwxm1yHO4WqJohwtOyHrWzqjRT_IOOp402C6FxuHvmFyNKuUgMChDsNmHhtVsdQ9z4Kq0e8GibnIE8Se2hgkn1FbwWLRiSj5BqzCKkP_w1WtV21a3v8b5XZsahOXqEjvVcoyQaR45PzvYEqax16cAwaw5T13IrNBvt7w",
					"hsnut4VDuwCg-IEGbzthY6oVjJwUuSMs6atdyyBMcnWKV-sqYUqIP8iAqQBjFYcEwsVCbb_AubAbHrlQm42T2XCweTmnvZXqI7PMK5_aYp9DTwqI30Sr-usgjgQJkyMUG7jQj-EvY99xkYX7j9ilrslnkuE01OY0PWkvR_Jo09seG68EUBq3TBSOdSP_Cuwi9JxripytPVOXfnsdn4zuTxxjE8ZSxq6Ql6nm5tQ-Ni6JdQWzE6JqCTuwUtPGYP6hcCeASTGKTusy7t3zMkC1hdT5o77dlTGkbZt60gA-alRQoy8f0UkuDXgiIwDr9FILTyZHiDqu1UavDA-PfJ_PSA",
	1680695652675l, 1680609252675l);
			Mockito.when(keyRetriever.getKeyPair()).thenReturn(Uni.createFrom().item(k)); 
			PublicKey pk = new PublicKey(e, use, kid, n, kty, exp, iat);
			Mockito.when(keyRetriever.getPublicKey(kid))
			.thenReturn(Uni.createFrom().item(Optional.of(pk)));
		// Prepare request
		GetAccessToken getAccessToken = new GetAccessToken();
		CommonHeader commonHeader = new CommonHeader();
		String clientId = "5254f087-1214-45cd-94ae-fda53c835197";
		String channel = "POS";
		String merchantId = "28405fHfk73x88D";
		String acquirerId = "4585625";
		String terminalId = "testTerm";
		getAccessToken.setClientId(clientId);
		commonHeader.setChannel(channel);
		commonHeader.setMerchantId(merchantId);
		commonHeader.setAcquirerId(acquirerId);
		commonHeader.setTerminalId(terminalId);
		RequestSpecification request = given().contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.formParam("client_id", clientId).formParam("ext_token", "ext_token").formParam("add_data", "addData")
				.formParam("grant_type", "poynt_token").header("AcquirerId", acquirerId).header("TerminalId", terminalId)
				.header("MerchantId", merchantId).header("RequestId", "aB1aB1aB-aB1a-aB1a-aB1a-aB1aB1aB1abb")
				.header("Channel", channel);

		Response resp = request.when().log().all().post(); // I was thinking of elaborating the test a
		// bit more, checking if the type is correct Uni<AccessToken>
		// Log.info(resp.body());
		Log.debug(resp.asPrettyString());
			resp.then().statusCode(401);
	}

	


}
