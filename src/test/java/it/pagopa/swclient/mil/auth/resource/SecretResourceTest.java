/*
 * SecretResourceTest.java
 *
 * 29 jun 2023
 */
package it.pagopa.swclient.mil.auth.resource;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mock;
import org.mockito.MockedConstruction;

import com.azure.identity.DefaultAzureCredentialBuilder;
import com.azure.security.keyvault.secrets.SecretClient;
import com.azure.security.keyvault.secrets.SecretClientBuilder;
import com.azure.security.keyvault.secrets.models.KeyVaultSecret;
import com.nimbusds.jose.JOSEException;
import com.azure.security.keyvault.secrets.SecretClient;

import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import it.pagopa.swclient.mil.auth.service.RedisClient;
import it.pagopa.swclient.mil.auth.service.SecretClientBuilderService;

/**
 * 
 * @author Antonio Tarricone
 */
@QuarkusTest
@TestHTTPEndpoint(SecretResource.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SecretResourceTest {
	class SecretClientMock extends SecretClient
	/*
	 * 
	 */
	@InjectMock
	SecretClientBuilderService secretClientBuilderService;
	
	@BeforeAll
	void setup() {
		new SecretClient
	}
	
	/**
	 * 
	 */
	@Test()
	void generateSecret() {
		when(secretClient.setSecret(anyString(), anyString()))
			.thenReturn(null);
		
		given()
			.when()
			.post()
			.then()
			.statusCode(greaterThanOrEqualTo(0));
	}

	/**
	 * 
	 */
	@Test()
	void retrieveSecret() throws JOSEException {
		when(secretClient.getSecret(anyString()))
		.thenReturn(new KeyVaultSecret("quarkus-azure-test", "this is the secret"));
		
		given()
			.when()
			.get()
			.then()
			.statusCode(greaterThanOrEqualTo(0));
	}

	/**
	 * 
	 */
	@Test()
	void deleteSecret() {
		doNothing().
			when(secretClient.beginDeleteSecret(any()));
		
		given()
			.when()
			.delete()
			.then()
			.statusCode(greaterThanOrEqualTo(0));
	}
}