/*
 * SecretResourceTest.java
 *
 * 29 jun 2023
 */
package it.pagopa.swclient.mil.auth.resource;

import static io.restassured.RestAssured.given;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import com.nimbusds.jose.JOSEException;

import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import it.pagopa.swclient.mil.auth.bean.KeyPair;
import it.pagopa.swclient.mil.auth.service.AzureKeyVault;
import it.pagopa.swclient.mil.auth.service.KeyPairGenerator;
import it.pagopa.swclient.mil.auth.util.UniGenerator;
import jakarta.inject.Inject;

/**
 * 
 * @author Antonio Tarricone
 */
@QuarkusTest
@TestHTTPEndpoint(SecretResource.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SecretResourceTest {
	/*
	 * 
	 */
	@InjectMock
	AzureKeyVault azureKeyVault;

	/*
	 * 
	 */
	@Inject
	KeyPairGenerator keyPairGenerator;

	/**
	 * 
	 */
	@Test()
	void generateKeyPair() {
		when(azureKeyVault.setex(anyString(), anyLong(), any(KeyPair.class)))
			.thenReturn(UniGenerator.voidItem());
	
		given()
			.when()
			.post()
			.then()
			.statusCode(201);
	}

	/**
	 * @throws JOSEException 
	 * 
	 */
	@Test()
	void retrieveKeyPair() throws JOSEException {
		KeyPair keyPair = keyPairGenerator.generate();
		
		when(azureKeyVault.get(anyString()))
			.thenReturn(UniGenerator.item(keyPair));
		
		given()
			.when()
			.pathParam("kid", keyPair.getKid())
			.get("/{kid}")
			.then()
			.statusCode(200)
			.log();
	}
}