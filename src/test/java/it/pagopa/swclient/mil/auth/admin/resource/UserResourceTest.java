/*
 * UserResourceTest.java
 *
 * 22 nov 2024
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
import it.pagopa.swclient.mil.auth.admin.bean.AdminQueryParamName;
import it.pagopa.swclient.mil.auth.admin.bean.CreateUserRequest;
import it.pagopa.swclient.mil.auth.dao.UserEntity;
import it.pagopa.swclient.mil.auth.dao.UserRepository;
import it.pagopa.swclient.mil.auth.util.UniGenerator;
import jakarta.ws.rs.core.MediaType;

/**
 * 
 * @author Antonio Tarricone
 */
@QuarkusTest
@TestHTTPEndpoint(UserResource.class)
@TestSecurity(user = "test_user", roles = {
	"mil-auth-admin"
})
class UserResourceTest {
	/*
	 *
	 */
	@InjectMock
	UserRepository repository;

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
	void given_requestToCreateNewUser_when_allGoesOk_then_getPassword() {
		when(repository.persist(any(UserEntity.class)))
			.thenReturn(UniGenerator.item(new UserEntity()));

		given()
			.log().all()
			.filter(validationFilter)
			.contentType(MediaType.APPLICATION_JSON)
			.body(new CreateUserRequest().setUsername("username").setClientId("d0d654e6-97da-4848-b568-99fedccb642b"))
			.when()
			.post()
			.then()
			.log().all()
			.statusCode(201)
			.contentType(MediaType.APPLICATION_JSON)
			.body(AdminJsonPropertyName.PASSWORD, notNullValue());
	}

	/**
	 * 
	 */
	@Test
	void given_requestToCreateNewUser_when_duplicateKeyOccurs_then_getFailure() {
		MongoWriteException exc = mock(MongoWriteException.class, CALLS_REAL_METHODS);
		when(exc.getCode()).thenReturn(11000);

		when(repository.persist(any(UserEntity.class)))
			.thenReturn(Uni.createFrom().failure(exc));

		given()
			.log().all()
			.filter(validationFilter)
			.contentType(MediaType.APPLICATION_JSON)
			.body(new CreateUserRequest().setUsername("username").setClientId("d0d654e6-97da-4848-b568-99fedccb642b"))
			.when()
			.post()
			.then()
			.log().all()
			.statusCode(409);
	}

	/**
	 * 
	 */
	@Test
	void given_requestToCreateNewClient_when_mongoErrorOccursOnPersist_then_getFailure() {
		MongoWriteException exc = mock(MongoWriteException.class, CALLS_REAL_METHODS);
		when(exc.getCode()).thenReturn(12000);

		when(repository.persist(any(UserEntity.class)))
			.thenReturn(Uni.createFrom().failure(exc));

		given()
			.log().all()
			.filter(validationFilter)
			.contentType(MediaType.APPLICATION_JSON)
			.body(new CreateUserRequest().setUsername("username").setClientId("d0d654e6-97da-4848-b568-99fedccb642b"))
			.when()
			.post()
			.then()
			.log().all()
			.statusCode(500);
	}

	/**
	 * 
	 */
	@Test
	void given_requestToCreateNewClient_when_otherErrorOccursOnPersist_then_getFailure() {
		when(repository.persist(any(UserEntity.class)))
			.thenReturn(Uni.createFrom().failure(new Exception("synthetic exception")));

		given()
			.log().all()
			.filter(validationFilter)
			.contentType(MediaType.APPLICATION_JSON)
			.body(new CreateUserRequest().setUsername("username").setClientId("d0d654e6-97da-4848-b568-99fedccb642b"))
			.when()
			.post()
			.then()
			.log().all()
			.statusCode(500);
	}

	/**
	 * 
	 */
	@Test
	void given_requestToDeleteUser_when_allGoesOk_then_getNoContent() {
		/*
		 * Mocking
		 */
		when(repository.deleteByUsername("83c0b10f-b398-4cc8-b356-a3e0f0291679"))
			.thenReturn(UniGenerator.item((Long.valueOf(1))));

		/*
		 * Tests
		 */
		given()
			.log().all()
			.filter(validationFilter)
			.queryParam(AdminQueryParamName.USERNAME, "83c0b10f-b398-4cc8-b356-a3e0f0291679")
			.when()
			.delete()
			.then()
			.log().all()
			.statusCode(204);
	}

	/**
	 * 
	 */
	@Test
	void given_requestToDeleteUser_when_clientDoesntExist_then_getNotFound() {
		/*
		 * Mocking
		 */
		when(repository.deleteByUsername("83c0b10f-b398-4cc8-b356-a3e0f0291679"))
			.thenReturn(UniGenerator.item((Long.valueOf(0))));

		/*
		 * Tests
		 */
		given()
			.log().all()
			.filter(validationFilter)
			.queryParam(AdminQueryParamName.USERNAME, "83c0b10f-b398-4cc8-b356-a3e0f0291679")
			.when()
			.delete()
			.then()
			.log().all()
			.statusCode(404)
			.contentType(MediaType.APPLICATION_JSON);
	}

	/**
	 * 
	 */
	@Test
	void given_requestToDeleteUser_when_databaseErrorOccurs_then_getServerError() {
		/*
		 * Mocking
		 */
		when(repository.deleteByUsername("83c0b10f-b398-4cc8-b356-a3e0f0291679"))
			.thenReturn(Uni.createFrom().failure(new Exception("synthetic exception")));

		/*
		 * Tests
		 */
		given()
			.log().all()
			.filter(validationFilter)
			.queryParam(AdminQueryParamName.USERNAME, "83c0b10f-b398-4cc8-b356-a3e0f0291679")
			.when()
			.delete()
			.then()
			.log().all()
			.statusCode(500)
			.contentType(MediaType.APPLICATION_JSON);
	}
}