/*
 * JwksResourceTest.java
 *
 * 14 set 2023
 */
package it.pagopa.swclient.mil.auth.resource;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import io.quarkus.test.InjectMock;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.mutiny.Multi;
import it.pagopa.swclient.mil.auth.bean.JsonPropertyName;
import it.pagopa.swclient.mil.auth.bean.PublicKeys;
import it.pagopa.swclient.mil.auth.util.KeyUtils;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.JsonWebKey;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.JsonWebKeyOperation;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.JsonWebKeyType;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyAttributes;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyBundle;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.service.AzureKeyVaultKeysExtReactiveService;
import jakarta.ws.rs.core.MediaType;

/**
 *
 * @author Antonio Tarricone
 */
@QuarkusTest
@TestHTTPEndpoint(JwksResource.class)
class JwksResourceTest {
	/*
	 * 
	 */
	@InjectMock
	private AzureKeyVaultKeysExtReactiveService keyExtService;

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
	void given_setOfKeys_when_theEndPointIsInvoked_then_getListOfPublicKeys() {
		/*
		 * Setup
		 */
		long iat1 = Instant.now().minus(1, ChronoUnit.MINUTES).getEpochSecond();
		long exp1 = Instant.now().plus(9, ChronoUnit.MINUTES).getEpochSecond();

		KeyBundle keyBundle1 = new KeyBundle()
			.setAttributes(new KeyAttributes()
				.setCreated(iat1)
				.setEnabled(true)
				.setExp(exp1)
				.setNbf(iat1))
			.setKey(new JsonWebKey()
				.setKty(JsonWebKeyType.RSA)
				.setE(new byte[0])
				.setN(new byte[0])
				.setKeyOps(List.of(
					JsonWebKeyOperation.SIGN,
					JsonWebKeyOperation.VERIFY))
				.setKid("https://keyvault/keys/key_name_1/key_version_1"))
			.setTags(Map.of(
				it.pagopa.swclient.mil.azureservices.keyvault.keys.util.KeyUtils.DOMAIN_KEY,
				KeyUtils.DOMAIN_VALUE));

		long iat2 = Instant.now().getEpochSecond();
		long exp2 = Instant.now().plus(10, ChronoUnit.MINUTES).getEpochSecond();

		KeyBundle keyBundle2 = new KeyBundle()
			.setAttributes(new KeyAttributes()
				.setCreated(iat2)
				.setEnabled(true)
				.setExp(exp2)
				.setNbf(iat2))
			.setKey(new JsonWebKey()
				.setKty(JsonWebKeyType.RSA)
				.setE(new byte[0])
				.setN(new byte[0])
				.setKeyOps(List.of(
					JsonWebKeyOperation.SIGN,
					JsonWebKeyOperation.VERIFY))
				.setKid("https://keyvault/keys/key_name_2/key_version_2"))
			.setTags(Map.of(
				it.pagopa.swclient.mil.azureservices.keyvault.keys.util.KeyUtils.DOMAIN_KEY,
				KeyUtils.DOMAIN_VALUE));

		when(keyExtService.getKeys(
			KeyUtils.DOMAIN_VALUE,
			List.of(JsonWebKeyOperation.SIGN, JsonWebKeyOperation.VERIFY),
			List.of(JsonWebKeyType.RSA)))
			.thenReturn(Multi.createFrom().items(keyBundle1, keyBundle2));

		/*
		 * Test
		 */
		PublicKeys actual = given()
			.when()
			.get()
			.then()
			.log()
			.everything()
			.statusCode(200)
			.contentType(MediaType.APPLICATION_JSON)
			.header("Cache-Control", containsString("max-age"))
			.extract()
			.response()
			.as(PublicKeys.class);

		assertThat(actual.getKeys())
			.containsExactlyInAnyOrder(
				KeyUtils.keyBundle2PublicKey(keyBundle1),
				KeyUtils.keyBundle2PublicKey(keyBundle2));
	}

	/**
	 * 
	 */
	@Test
	void given_noKey_when_theEndPointIsInvoked_then_getEmptyListOfPublicKeys() {
		/*
		 * Setup
		 */
		when(keyExtService.getKeys(
			KeyUtils.DOMAIN_VALUE,
			List.of(JsonWebKeyOperation.SIGN, JsonWebKeyOperation.VERIFY),
			List.of(JsonWebKeyType.RSA)))
			.thenReturn(Multi.createFrom().items());

		/*
		 * Test
		 */
		PublicKeys actual = given()
			.when()
			.get()
			.then()
			.log()
			.everything()
			.statusCode(200)
			.contentType(MediaType.APPLICATION_JSON)
			.header("Cache-Control", containsString("no-cache"))
			.extract()
			.response()
			.as(PublicKeys.class);

		assertThat(actual.getKeys())
			.isEmpty();
	}

	/**
	 * 
	 */
	@Test
	void given_errorSearchingKeys_when_theEndPointIsInvoked_then_getFailure() {
		/*
		 * Setup
		 */
		when(keyExtService.getKeys(
			KeyUtils.DOMAIN_VALUE,
			List.of(JsonWebKeyOperation.SIGN, JsonWebKeyOperation.VERIFY),
			List.of(JsonWebKeyType.RSA)))
			.thenReturn(Multi.createFrom().failure(new Exception("synthetic_exception")));

		/*
		 * Test
		 */
		given()
			.when()
			.get()
			.then()
			.log()
			.everything()
			.statusCode(500)
			.contentType(MediaType.APPLICATION_JSON)
			.body(JsonPropertyName.ERRORS, notNullValue());
	}
}