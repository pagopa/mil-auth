/*
 * ExpKeysCleanerResourceTest.java
 *
 * 22 lug 2024
 */
package it.pagopa.swclient.mil.auth.admin.resource;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import io.quarkus.test.InjectMock;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.smallrye.mutiny.Multi;
import it.pagopa.swclient.mil.auth.admin.AuthAdminErrorCode;
import it.pagopa.swclient.mil.auth.admin.bean.DeletedKeys;
import it.pagopa.swclient.mil.auth.util.KeyUtils;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.DeletedKeyBundle;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.JsonWebKey;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.service.AzureKeyVaultKeysExtReactiveService;
import it.pagopa.swclient.mil.bean.Errors;
import jakarta.ws.rs.core.MediaType;

/**
 * 
 * @author Antonio Tarricone
 */
@QuarkusTest
@TestHTTPEndpoint(ExpKeysCleanerResource.class)
@TestSecurity(user = "test_user", roles = {
	"mil-auth-admin"
})
class ExpKeysCleanerResourceTest {
	/*
	 *
	 */
	@InjectMock
	AzureKeyVaultKeysExtReactiveService keyExtService;

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
	void given_setOfExpiredKeys_when_theEndPointIsInvoked_then_getListOfDeletedKeys() {
		when(keyExtService.deleteExpiredKeys(KeyUtils.DOMAIN_VALUE))
			.thenReturn(Multi.createFrom().items(
				new DeletedKeyBundle()
					.setKey(new JsonWebKey()
						.setKid("https://keyvault/keys/key_name_1/key_version_1")),
				new DeletedKeyBundle()
					.setKey(new JsonWebKey()
						.setKid("https://keyvault/keys/key_name_2/key_version_2"))));

		DeletedKeys actual = given()
			.when()
			.post()
			.then()
			.log()
			.everything()
			.statusCode(200)
			.contentType(MediaType.APPLICATION_JSON)
			.extract()
			.response()
			.as(DeletedKeys.class);

		assertThat(actual.getKids())
			.containsExactlyInAnyOrder(
				"key_name_1/key_version_1",
				"key_name_2/key_version_2");
	}

	/**
	 * 
	 */
	@Test
	void given_setOfExpiredKeys_when_anErrorOccurs_then_getFailure() {
		when(keyExtService.deleteExpiredKeys(KeyUtils.DOMAIN_VALUE))
			.thenReturn(Multi.createFrom().failure(new Exception("synthetic_exception")));

		Errors actual = given()
			.when()
			.post()
			.then()
			.log()
			.everything()
			.statusCode(500)
			.contentType(MediaType.APPLICATION_JSON)
			.extract()
			.response()
			.as(Errors.class);

		assertThat(actual.getErrors())
			.containsExactlyInAnyOrder(AuthAdminErrorCode.ERROR_DELETING_EXP_KEYS);
	}
}