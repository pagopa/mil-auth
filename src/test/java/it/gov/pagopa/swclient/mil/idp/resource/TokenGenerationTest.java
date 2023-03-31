package it.gov.pagopa.swclient.mil.idp.resource;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.ws.rs.core.MediaType;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;

import com.nimbusds.jose.JOSEException;

import io.quarkus.logging.Log;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.smallrye.common.constraint.Assert;
import io.smallrye.mutiny.Uni;
import it.gov.pagopa.swclient.mil.idp.ErrorCode;
import it.gov.pagopa.swclient.mil.bean.CommonHeader;
import it.gov.pagopa.swclient.mil.idp.bean.AccessToken;
import it.gov.pagopa.swclient.mil.idp.bean.GetAccessToken;
import it.gov.pagopa.swclient.mil.idp.dao.ClientRepository;
import it.gov.pagopa.swclient.mil.idp.dao.GrantRepository;
import it.gov.pagopa.swclient.mil.idp.dao.ResourceOwnerCredentialsRepository;

@QuarkusTest
@TestHTTPEndpoint(TokenResource.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TokenGenerationTest {
	/*
	 * @Test() public void testCreateToken() {
	 * 
	 * // Prepare request GetAccessToken getAccessToken = new GetAccessToken();
	 * CommonHeader commonHeader = new CommonHeader(); String clientId =
	 * "5254f087-1214-45cd-94ae-fda53c835197"; String channel = "POS"; String
	 * username = "antonio.tarricone"; String password = "antonio"; String
	 * merchantId = "28405fHfk73x88D"; String acquirerId = "01234568909"; String
	 * terminalId = "testTerm"; getAccessToken.setClientId(clientId);
	 * commonHeader.setChannel(channel); getAccessToken.setUsername(username);
	 * getAccessToken.setPassword(username);
	 * getAccessToken.setScope("offline_access");
	 * commonHeader.setMerchantId(merchantId);
	 * commonHeader.setAcquirerId(acquirerId);
	 * commonHeader.setTerminalId(terminalId); RequestSpecification request =
	 * given() .contentType(MediaType.APPLICATION_FORM_URLENCODED)
	 * .formParam("client_id", clientId) .formParam("username", username)
	 * .formParam("password", password) .formParam("grant_type", "password")
	 * .header("AcquirerId", acquirerId) .header("TerminalId", terminalId)
	 * .header("MerchantId", merchantId) .header("RequestId",
	 * "aB1aB1aB-aB1a-aB1a-aB1a-aB1aB1aB1abb") .header("Channel", channel);
	 * 
	 * Response resp = request.when() .log() .all() .post(); //I was thinking of
	 * elaborating the test a bit more, checking if the type is correct
	 * Uni<AccessToken> Log.info(resp.body());
	 * 
	 * resp .then() .statusCode(200);
	 * 
	 * }
	 * 
	 * @Test() public void testCreateTokenWithWrongClientId() {
	 * 
	 * 
	 * // Prepare request GetAccessToken getAccessToken = new GetAccessToken();
	 * CommonHeader commonHeader = new CommonHeader(); String clientId =
	 * "5254f087-1214-45cd-94ae-fda53c835198"; String channel = "POS"; String
	 * username = "antonio.tarricone"; String password = "antonio"; String
	 * merchantId = "28405fHfk73x88D"; String acquirerId = "01234568909"; String
	 * terminalId = "testTerm"; getAccessToken.setClientId(clientId);
	 * commonHeader.setChannel(channel); getAccessToken.setUsername(username);
	 * getAccessToken.setPassword(username);
	 * getAccessToken.setScope("offline_access");
	 * commonHeader.setMerchantId(merchantId);
	 * commonHeader.setAcquirerId(acquirerId);
	 * commonHeader.setTerminalId(terminalId); RequestSpecification
	 * wrongClientIdRequest = given()
	 * .contentType(MediaType.APPLICATION_FORM_URLENCODED) .formParam("client_id",
	 * clientId) .formParam("username", username) .formParam("password", password)
	 * .formParam("grant_type", "password") .header("AcquirerId", acquirerId)
	 * .header("TerminalId", terminalId) .header("MerchantId", merchantId)
	 * .header("RequestId", "aB1aB1aB-aB1a-aB1a-aB1a-aB1aB1aB1abb")
	 * .header("Channel", channel);
	 * 
	 * Response wrongClientIdResponse = wrongClientIdRequest.when() .log() .all()
	 * .post();
	 * 
	 * wrongClientIdResponse.then() .statusCode(401);
	 * 
	 * ArrayList<String> wrongClientIdResponseMessage
	 * =wrongClientIdResponse.body().path("errors");
	 * System.out.println(wrongClientIdResponseMessage.get(0));
	 * Assert.assertTrue(wrongClientIdResponseMessage.get(0).equals(ErrorCode.
	 * CLIENT_ID_NOT_FOUND)); }
	 * 
	 * @Test() public void testCreateTokenWithWrongChannel() {
	 * 
	 * 
	 * // Prepare request GetAccessToken getAccessToken = new GetAccessToken();
	 * CommonHeader commonHeader = new CommonHeader(); String clientId =
	 * "5254f087-1214-45cd-94ae-fda53c835197"; String channel = "TOTEM"; String
	 * username = "antonio.tarricone"; String password = "antonio"; String
	 * merchantId = "28405fHfk73x88D"; String acquirerId = "01234568909"; String
	 * terminalId = "testTerm"; getAccessToken.setClientId(clientId);
	 * commonHeader.setChannel(channel); getAccessToken.setUsername(username);
	 * getAccessToken.setPassword(username);
	 * getAccessToken.setScope("offline_access");
	 * commonHeader.setMerchantId(merchantId);
	 * commonHeader.setAcquirerId(acquirerId);
	 * commonHeader.setTerminalId(terminalId); RequestSpecification request =
	 * given() .contentType(MediaType.APPLICATION_FORM_URLENCODED)
	 * .formParam("client_id", clientId) .formParam("username", username)
	 * .formParam("password", password) .formParam("grant_type", "password")
	 * .header("AcquirerId", acquirerId) .header("TerminalId", terminalId)
	 * .header("MerchantId", merchantId) .header("RequestId",
	 * "aB1aB1aB-aB1a-aB1a-aB1a-aB1aB1aB1abb") .header("Channel", channel);
	 * 
	 * Response resp = request.when() .log() .all() .post();
	 * 
	 * resp.then() .statusCode(401);
	 * 
	 * ArrayList<String> responseMessage =resp.body().path("errors");
	 * System.out.println(resp.body().asPrettyString()); Log.debug(responseMessage);
	 * Assert.assertTrue(responseMessage.get(0).equals(ErrorCode.
	 * ERROR_WHILE_FINDING_GRANTS)); }
	 * 
	 * @Test public void testCreateTokenWithWrongUser() {
	 * 
	 * 
	 * // Prepare request GetAccessToken getAccessToken = new GetAccessToken();
	 * CommonHeader commonHeader = new CommonHeader(); String clientId =
	 * "5254f087-1214-45cd-94ae-fda53c835197"; String channel = "POS"; String
	 * username = "antonio.t"; String password = "antonio"; String merchantId =
	 * "28405fHfk73x88D"; String acquirerId = "01234568909"; String terminalId =
	 * "testTerm"; getAccessToken.setClientId(clientId);
	 * commonHeader.setChannel(channel); getAccessToken.setUsername(username);
	 * getAccessToken.setPassword(username);
	 * getAccessToken.setScope("offline_access");
	 * commonHeader.setMerchantId(merchantId);
	 * commonHeader.setAcquirerId(acquirerId);
	 * commonHeader.setTerminalId(terminalId); RequestSpecification request =
	 * given() .contentType(MediaType.APPLICATION_FORM_URLENCODED)
	 * .formParam("client_id", clientId) .formParam("username", username)
	 * .formParam("password", password) .formParam("grant_type", "password")
	 * .header("AcquirerId", acquirerId) .header("TerminalId", terminalId)
	 * .header("MerchantId", merchantId) .header("RequestId",
	 * "aB1aB1aB-aB1a-aB1a-aB1a-aB1aB1aB1abb") .header("Channel", channel);
	 * 
	 * Response resp = request.when() .log() .all() .post();
	 * 
	 * resp.then() .statusCode(401);
	 * 
	 * ArrayList<String> responseMessage =resp.body().path("errors");
	 * System.out.println(resp.body().asPrettyString()); Log.debug(responseMessage);
	 * Assert.assertTrue(responseMessage.get(0).equals(ErrorCode.WRONG_CREDENTIALS))
	 * ; }
	 * 
	 * 
	 * @Test public void testCreateTokenWithWrongMerchant() {
	 * 
	 * 
	 * // Prepare request GetAccessToken getAccessToken = new GetAccessToken();
	 * CommonHeader commonHeader = new CommonHeader(); String clientId =
	 * "5254f087-1214-45cd-94ae-fda53c835197"; String channel = "POS"; String
	 * username = "antonio.tarricone"; String password = "antonio"; String
	 * merchantId = "28405fHfk73x88E"; String acquirerId = "01234568909"; String
	 * terminalId = "testTerm"; getAccessToken.setClientId(clientId);
	 * commonHeader.setChannel(channel); getAccessToken.setUsername(username);
	 * getAccessToken.setPassword(username);
	 * getAccessToken.setScope("offline_access");
	 * commonHeader.setMerchantId(merchantId);
	 * commonHeader.setAcquirerId(acquirerId);
	 * commonHeader.setTerminalId(terminalId); RequestSpecification request =
	 * given() .contentType(MediaType.APPLICATION_FORM_URLENCODED)
	 * .formParam("client_id", clientId) .formParam("username", username)
	 * .formParam("password", password) .formParam("grant_type", "password")
	 * .header("AcquirerId", acquirerId) .header("TerminalId", terminalId)
	 * .header("MerchantId", merchantId) .header("RequestId",
	 * "aB1aB1aB-aB1a-aB1a-aB1a-aB1aB1aB1abb") .header("Channel", channel);
	 * 
	 * Response resp = request.when() .log() .all() .post();
	 * 
	 * resp.then() .statusCode(401);
	 * 
	 * ArrayList<String> responseMessage =resp.body().path("errors");
	 * System.out.println(resp.body().asPrettyString()); Log.debug(responseMessage);
	 * Assert.assertTrue(responseMessage.get(0).equals(ErrorCode.WRONG_CREDENTIALS))
	 * ; }
	 */

	@Test
	public void testCreateTokenFromRefresh() {

		// Prepare request
		GetAccessToken getAccessToken = new GetAccessToken();
		CommonHeader commonHeader = new CommonHeader();
		String clientId = "5254f087-1214-45cd-94ae-fda53c835197";
		String channel = "POS";
		String username = "antonio.tarricone";
		String password = "antonio";
		String merchantId = "28405fHfk73x88D";
		String acquirerId = "01234568909";
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

		Assert.assertTrue(responseMap.containsKey("refresh_token"));
		Assert.assertTrue(responseMap.containsKey("access_token"));
		Assert.assertTrue(responseMap.containsKey("token_type"));
		Assert.assertTrue(responseMap.containsKey("expires_in"));

		String refreshToken = responsePath.getString("refresh_token");
		String accessToekn = responsePath.getString("access_token");
		String tokenType = responsePath.getString("token_type");
		String expiresIn = responsePath.getString("expires_in");
		resp.then().statusCode(200);
		System.out.println(refreshToken);
		
		RequestSpecification refreshRequest = given().contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.formParam("client_id", clientId)
				.formParam("scope", "offline_access").formParam("refresh_token", refreshToken)
				.formParam("grant_type", "refresh_token")
				.header("AcquirerId", acquirerId).header("TerminalId", terminalId).header("MerchantId", merchantId)
				.header("RequestId", "aB1aB1aB-aB1a-aB1a-aB1a-aB1aB1aB1abb").header("Channel", channel);
		
		Response refreshResp = refreshRequest.when().log().all().post();
		refreshResp.then().statusCode(200);



	}

}
