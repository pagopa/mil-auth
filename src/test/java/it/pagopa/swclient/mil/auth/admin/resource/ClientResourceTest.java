/*
 * ClientResourceTest.java
 *
 * 30 lug 2024
 */
package it.pagopa.swclient.mil.auth.admin.resource;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;

import org.assertj.core.util.Files;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import com.atlassian.oai.validator.restassured.OpenApiValidationFilter;
import com.mongodb.MongoWriteException;
import com.nimbusds.jose.util.StandardCharset;

import io.quarkus.test.InjectMock;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.smallrye.mutiny.Uni;
import it.pagopa.swclient.mil.auth.admin.bean.AdminJsonPropertyName;
import it.pagopa.swclient.mil.auth.admin.bean.CreateClientRequest;
import it.pagopa.swclient.mil.auth.dao.ClientEntity;
import it.pagopa.swclient.mil.auth.dao.ClientRepository;
import it.pagopa.swclient.mil.auth.util.UniGenerator;
import jakarta.ws.rs.core.MediaType;

/**
 * 
 * @author Antonio Tarricone
 */
@QuarkusTest
@TestHTTPEndpoint(ClientResource.class)
@TestSecurity(user = "test_user", roles = {
	"mil-auth-admin"
})
class ClientResourceTest {
	/*
	 *
	 */
	@InjectMock
	ClientRepository repository;
	
	/*
	 * 
	 */
	private static OpenApiValidationFilter validationFilter;

	/**
	 * 
	 */
	@BeforeAll
	static void loadOpenApiDescriptor() {
		validationFilter = new OpenApiValidationFilter(
			Files.contentOf(
				new File("src/main/resources/META-INF/openapi.yaml"),
				StandardCharset.UTF_8));
	}

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
	void given_requestToCreateNewClient_when_allGoesOk_then_getClientIdAndSecret() {
		when(repository.persist(any(ClientEntity.class)))
			.thenReturn(UniGenerator.item(new ClientEntity()));

		given()
			.filter(validationFilter)
			.contentType(MediaType.APPLICATION_JSON)
			.body(new CreateClientRequest(null, "Test description", "test-subject"))
			.when()
			.post()
			.then()
			.log()
			.everything()
			.statusCode(201)
			.contentType(MediaType.APPLICATION_JSON)
			.body(AdminJsonPropertyName.CLIENT_ID, notNullValue())
			.body(AdminJsonPropertyName.CLIENT_SECRET, notNullValue());
	}

	/**
	 * 
	 */
	@Test
	void given_requestToCreateNewClient_when_duplicateKeyOccurs_then_getFailure() {
		MongoWriteException exc = mock(MongoWriteException.class, CALLS_REAL_METHODS);
		when(exc.getCode()).thenReturn(11000);

		when(repository.persist(any(ClientEntity.class)))
			.thenReturn(Uni.createFrom().failure(exc));

		given()
			.filter(validationFilter)
			.contentType(MediaType.APPLICATION_JSON)
			.body(new CreateClientRequest(null, "Test description", "test-subject"))
			.when()
			.post()
			.then()
			.log()
			.everything()
			.statusCode(409);
	}

	/**
	 * 
	 */
	@Test
	void given_requestToCreateNewClient_when_mongoErrorOccursOnPersist_then_getFailure() {
		MongoWriteException exc = mock(MongoWriteException.class, CALLS_REAL_METHODS);
		when(exc.getCode()).thenReturn(12000);

		when(repository.persist(any(ClientEntity.class)))
			.thenReturn(Uni.createFrom().failure(exc));

		given()
			.filter(validationFilter)
			.contentType(MediaType.APPLICATION_JSON)
			.body(new CreateClientRequest(null, "Test description", "test-subject"))
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
	void given_requestToCreateNewClient_when_otherErrorOccursOnPersist_then_getFailure() {
		when(repository.persist(any(ClientEntity.class)))
			.thenReturn(Uni.createFrom().failure(new Exception("synthetic exception")));

		given()
			.filter(validationFilter)
			.contentType(MediaType.APPLICATION_JSON)
			.body(new CreateClientRequest(null, "Test description", "test-subject"))
			.when()
			.post()
			.then()
			.log()
			.everything()
			.statusCode(500);
	}
}