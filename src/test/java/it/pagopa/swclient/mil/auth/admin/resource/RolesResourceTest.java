/*
 * RolesResourceTest.java
 *
 * 27 ago 2024
 */
package it.pagopa.swclient.mil.auth.admin.resource;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.util.List;
import java.util.Optional;

import org.assertj.core.util.Files;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.mockito.Mockito;

import com.atlassian.oai.validator.restassured.OpenApiValidationFilter;
import com.mongodb.MongoWriteException;
import com.nimbusds.jose.util.StandardCharset;

import io.quarkus.test.InjectMock;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.tuples.Tuple2;
import it.pagopa.swclient.mil.auth.admin.bean.AdminPathParamName;
import it.pagopa.swclient.mil.auth.admin.bean.AdminQueryParamName;
import it.pagopa.swclient.mil.auth.admin.bean.CreateOrUpdateSetOfRolesRequest;
import it.pagopa.swclient.mil.auth.admin.bean.PageMetadata;
import it.pagopa.swclient.mil.auth.admin.bean.PageOfSetOfRoles;
import it.pagopa.swclient.mil.auth.admin.bean.SetOfRoles;
import it.pagopa.swclient.mil.auth.dao.RolesRepository;
import it.pagopa.swclient.mil.auth.dao.SetOfRolesEntity;
import it.pagopa.swclient.mil.auth.util.UniGenerator;
import jakarta.ws.rs.core.MediaType;

/**
 * 
 * @author Antonio Tarricone
 */
@QuarkusTest
@TestHTTPEndpoint(RolesResource.class)
@TestSecurity(user = "test_user", roles = {
	"mil-auth-admin"
})
class RolesResourceTest {
	/*
	 *
	 */
	@InjectMock
	RolesRepository repository;

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
		Mockito.reset(repository);
	}

	/**
	 * 
	 */
	@Test
	void given_requestToCreateNewRoles_when_allGoesOk_then_getResourceUrl() {
		when(repository.persist(any(SetOfRolesEntity.class)))
			.thenReturn(UniGenerator.item(new SetOfRolesEntity()));

		given()
			.log().all()
			.filter(validationFilter)
			.contentType(MediaType.APPLICATION_JSON)
			.body(new CreateOrUpdateSetOfRolesRequest(
				"83c0b10f-b398-4cc8-b356-a3e0f0291679",
				"NA",
				"NA",
				"NA",
				"NA",
				List.of("mil-auth-admin")))
			.when()
			.post()
			.then()
			.log().all()
			.statusCode(201)
			.header("Location", notNullValue());
	}

	/**
	 * 
	 */
	@Test
	void given_requestToCreateNewRoles_when_duplicateKeyOccurs_then_getFailure() {
		MongoWriteException exc = mock(MongoWriteException.class, CALLS_REAL_METHODS);
		when(exc.getCode()).thenReturn(11000);

		when(repository.persist(any(SetOfRolesEntity.class)))
			.thenReturn(Uni.createFrom().failure(exc));

		given()
			.log().all()
			.filter(validationFilter)
			.contentType(MediaType.APPLICATION_JSON)
			.body(new CreateOrUpdateSetOfRolesRequest(
				"83c0b10f-b398-4cc8-b356-a3e0f0291679",
				"NA",
				"NA",
				"NA",
				"NA",
				List.of("mil-auth-admin")))
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
	void given_requestToCreateNewRoles_when_mongoErrorOccursOnPersist_then_getFailure() {
		MongoWriteException exc = mock(MongoWriteException.class, CALLS_REAL_METHODS);
		when(exc.getCode()).thenReturn(12000);

		when(repository.persist(any(SetOfRolesEntity.class)))
			.thenReturn(Uni.createFrom().failure(exc));

		given()
			.log().all()
			.filter(validationFilter)
			.contentType(MediaType.APPLICATION_JSON)
			.body(new CreateOrUpdateSetOfRolesRequest(
				"83c0b10f-b398-4cc8-b356-a3e0f0291679",
				"NA",
				"NA",
				"NA",
				"NA",
				List.of("mil-auth-admin")))
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
	void given_requestToCreateNewRoles_when_otherErrorOccursOnPersist_then_getFailure() {
		when(repository.persist(any(SetOfRolesEntity.class)))
			.thenReturn(Uni.createFrom().failure(new Exception("synthetic exception")));

		given()
			.log().all()
			.filter(validationFilter)
			.contentType(MediaType.APPLICATION_JSON)
			.body(new CreateOrUpdateSetOfRolesRequest(
				"83c0b10f-b398-4cc8-b356-a3e0f0291679",
				"NA",
				"NA",
				"NA",
				"NA",
				List.of("mil-auth-admin")))
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
	void given_requestToReadRolesByParameters_when_allGoesOk_then_getTheListOfFoundRoles() {
		/*
		 * Mocking
		 */
		SetOfRolesEntity entity = new SetOfRolesEntity()
			.setSetOfRolesId("7f49f59a-2033-4def-b462-9f64b25b20ea")
			.setAcquirerId("NA")
			.setChannel("NA")
			.setClientId("83c0b10f-b398-4cc8-b356-a3e0f0291679")
			.setMerchantId("NA")
			.setTerminalId("NA")
			.setRoles(List.of("mil-auth-admin"));

		when(repository.findByParameters(0, 2, null, null, "83c0b10f-b398-4cc8-b356-a3e0f0291679", null, null))
			.thenReturn(UniGenerator.item(Tuple2.of(Long.valueOf(1), List.of(entity))));

		/*
		 * Expected values
		 */
		SetOfRoles dto = new SetOfRoles()
			.setSetOfRolesId("7f49f59a-2033-4def-b462-9f64b25b20ea")
			.setAcquirerId("NA")
			.setChannel("NA")
			.setClientId("83c0b10f-b398-4cc8-b356-a3e0f0291679")
			.setMerchantId("NA")
			.setTerminalId("NA")
			.setRoles(List.of("mil-auth-admin"));

		PageMetadata metadata = new PageMetadata(1, 1, 0, 2);

		/*
		 * Tests
		 */
		PageOfSetOfRoles roles = given()
			.log().all()
			.filter(validationFilter)
			.queryParam(AdminQueryParamName.PAGE, 0)
			.queryParam(AdminQueryParamName.SIZE, 2)
			.queryParam(AdminQueryParamName.CLIENT_ID, "83c0b10f-b398-4cc8-b356-a3e0f0291679")
			.when()
			.get()
			.then()
			.log().all()
			.statusCode(200)
			.contentType(MediaType.APPLICATION_JSON)
			.extract().response().as(PageOfSetOfRoles.class);

		assertThat(roles.getSetsOfRoles())
			.containsExactlyInAnyOrder(dto);

		assertEquals(metadata, roles.getPage());
	}

	/**
	 * 
	 */
	@Test
	void given_requestToReadRolesByParameters_when_noRolesFound_then_getEmptyList() {
		/*
		 * Mocking
		 */
		when(repository.findByParameters(0, 2, null, null, "83c0b10f-b398-4cc8-b356-a3e0f0291679", null, null))
			.thenReturn(UniGenerator.item(Tuple2.of(Long.valueOf(0), List.of())));

		/*
		 * Expected values
		 */
		PageMetadata metadata = new PageMetadata(0, 0, 0, 2);

		/*
		 * Tests
		 */
		PageOfSetOfRoles roles = given()
			.log().all()
			.filter(validationFilter)
			.queryParam(AdminQueryParamName.PAGE, 0)
			.queryParam(AdminQueryParamName.SIZE, 2)
			.queryParam(AdminQueryParamName.CLIENT_ID, "83c0b10f-b398-4cc8-b356-a3e0f0291679")
			.when()
			.get()
			.then()
			.log().all()
			.statusCode(200)
			.contentType(MediaType.APPLICATION_JSON)
			.extract().response().as(PageOfSetOfRoles.class);

		assertThat(roles.getSetsOfRoles())
			.isEmpty();

		assertEquals(metadata, roles.getPage());
	}

	/**
	 * 
	 */
	@Test
	void given_requestToReadRolesByParameters_when_databaseErrorOccurs_then_getServerError() {
		/*
		 * Mocking
		 */
		when(repository.findByParameters(0, 2, null, null, "83c0b10f-b398-4cc8-b356-a3e0f0291679", null, null))
			.thenReturn(Uni.createFrom().failure(new Exception("synthetic exception")));

		/*
		 * Tests
		 */
		given()
			.log().all()
			.filter(validationFilter)
			.queryParam(AdminQueryParamName.PAGE, 0)
			.queryParam(AdminQueryParamName.SIZE, 2)
			.queryParam(AdminQueryParamName.CLIENT_ID, "83c0b10f-b398-4cc8-b356-a3e0f0291679")
			.when()
			.get()
			.then()
			.log().all()
			.statusCode(500)
			.contentType(MediaType.APPLICATION_JSON);
	}

	/**
	 * 
	 */
	@Test
	void given_requestToReadRolesById_when_allGoesOk_then_getThem() {
		/*
		 * Mocking
		 */
		SetOfRolesEntity entity = new SetOfRolesEntity()
			.setSetOfRolesId("7f49f59a-2033-4def-b462-9f64b25b20ea")
			.setAcquirerId("NA")
			.setChannel("NA")
			.setClientId("83c0b10f-b398-4cc8-b356-a3e0f0291679")
			.setMerchantId("NA")
			.setTerminalId("NA")
			.setRoles(List.of("mil-auth-admin"));

		when(repository.findBySetOfRolesId("7f49f59a-2033-4def-b462-9f64b25b20ea"))
			.thenReturn(UniGenerator.item(Optional.of(entity)));

		/*
		 * Expected values
		 */
		SetOfRoles dto = new SetOfRoles()
			.setSetOfRolesId("7f49f59a-2033-4def-b462-9f64b25b20ea")
			.setAcquirerId("NA")
			.setChannel("NA")
			.setClientId("83c0b10f-b398-4cc8-b356-a3e0f0291679")
			.setMerchantId("NA")
			.setTerminalId("NA")
			.setRoles(List.of("mil-auth-admin"));

		/*
		 * Tests
		 */
		SetOfRoles roles = given()
			.log().all()
			.filter(validationFilter)
			.pathParam(AdminPathParamName.SET_OF_ROLES_ID, "7f49f59a-2033-4def-b462-9f64b25b20ea")
			.when()
			.get("/{" + AdminPathParamName.SET_OF_ROLES_ID + "}")
			.then()
			.log().all()
			.statusCode(200)
			.contentType(MediaType.APPLICATION_JSON)
			.extract().response().as(SetOfRoles.class);

		assertEquals(dto, roles);
	}

	/**
	 * 
	 */
	@Test
	void given_requestToReadRolesById_when_rolesDontExist_then_getNotFound() {
		/*
		 * Mocking
		 */
		when(repository.findBySetOfRolesId("7f49f59a-2033-4def-b462-9f64b25b20ea"))
			.thenReturn(UniGenerator.item(Optional.empty()));

		/*
		 * Tests
		 */
		given()
			.log().all()
			.filter(validationFilter)
			.pathParam(AdminPathParamName.SET_OF_ROLES_ID, "7f49f59a-2033-4def-b462-9f64b25b20ea")
			.when()
			.get("/{" + AdminPathParamName.SET_OF_ROLES_ID + "}")
			.then()
			.log().all()
			.statusCode(404)
			.contentType(MediaType.APPLICATION_JSON);
	}

	/**
	 * 
	 */
	@Test
	void given_requestToReadRolesById_when_databaseErrorOccurs_then_getFailure() {
		/*
		 * Mocking
		 */
		when(repository.findBySetOfRolesId("7f49f59a-2033-4def-b462-9f64b25b20ea"))
			.thenReturn(Uni.createFrom().failure(new Exception("synthetic exception")));

		/*
		 * Tests
		 */
		given()
			.log().all()
			.filter(validationFilter)
			.pathParam(AdminPathParamName.SET_OF_ROLES_ID, "7f49f59a-2033-4def-b462-9f64b25b20ea")
			.when()
			.get("/{" + AdminPathParamName.SET_OF_ROLES_ID + "}")
			.then()
			.log().all()
			.statusCode(500)
			.contentType(MediaType.APPLICATION_JSON);
	}

	/**
	 * 
	 */
	@Test
	void given_requestToDeleteRolesById_when_allGoesOk_then_getNoContent() {
		/*
		 * Mocking
		 */
		when(repository.deleteBySetOfRolesId("7f49f59a-2033-4def-b462-9f64b25b20ea"))
			.thenReturn(UniGenerator.item(Long.valueOf(1)));

		/*
		 * Tests
		 */
		given()
			.log().all()
			.filter(validationFilter)
			.pathParam(AdminPathParamName.SET_OF_ROLES_ID, "7f49f59a-2033-4def-b462-9f64b25b20ea")
			.when()
			.delete("/{" + AdminPathParamName.SET_OF_ROLES_ID + "}")
			.then()
			.log().all()
			.statusCode(204);
	}

	/**
	 * 
	 */
	@Test
	void given_requestToDeleteRolesById_when_rolesDontExist_then_getNotFound() {
		/*
		 * Mocking
		 */
		when(repository.deleteBySetOfRolesId("7f49f59a-2033-4def-b462-9f64b25b20ea"))
			.thenReturn(UniGenerator.item(Long.valueOf(0)));

		/*
		 * Tests
		 */
		given()
			.log().all()
			.filter(validationFilter)
			.pathParam(AdminPathParamName.SET_OF_ROLES_ID, "7f49f59a-2033-4def-b462-9f64b25b20ea")
			.when()
			.delete("/{" + AdminPathParamName.SET_OF_ROLES_ID + "}")
			.then()
			.log().all()
			.statusCode(404)
			.contentType(MediaType.APPLICATION_JSON);
	}

	/**
	 * 
	 */
	@Test
	void given_requestToDeleteRolesById_when_databaseErrorOccurs_then_getFailure() {
		/*
		 * Mocking
		 */
		when(repository.deleteBySetOfRolesId("7f49f59a-2033-4def-b462-9f64b25b20ea"))
			.thenReturn(Uni.createFrom().failure(new Exception("synthetic exception")));

		/*
		 * Tests
		 */
		given()
			.log().all()
			.filter(validationFilter)
			.pathParam(AdminPathParamName.SET_OF_ROLES_ID, "7f49f59a-2033-4def-b462-9f64b25b20ea")
			.when()
			.delete("/{" + AdminPathParamName.SET_OF_ROLES_ID + "}")
			.then()
			.log().all()
			.statusCode(500)
			.contentType(MediaType.APPLICATION_JSON);
	}

	/**
	 * 
	 */
	@Test
	void given_requestToUpdateRolesById_when_allGoesOk_then_getNoContent() {
		/*
		 * Mocking
		 */
		when(repository.updateBySetOfRolesId(
			"7f49f59a-2033-4def-b462-9f64b25b20ea",
			"NA",
			"NA",
			"83c0b10f-b398-4cc8-b356-a3e0f0291679",
			"NA",
			"NA",
			List.of("mil-auth-admin")))
			.thenReturn(UniGenerator.item(Long.valueOf(1)));

		/*
		 * Tests
		 */
		given()
			.log().all()
			.filter(validationFilter)
			.contentType(MediaType.APPLICATION_JSON)
			.body(new CreateOrUpdateSetOfRolesRequest(
				"83c0b10f-b398-4cc8-b356-a3e0f0291679",
				"NA",
				"NA",
				"NA",
				"NA",
				List.of("mil-auth-admin")))
			.pathParam(AdminPathParamName.SET_OF_ROLES_ID, "7f49f59a-2033-4def-b462-9f64b25b20ea")
			.when()
			.put("/{" + AdminPathParamName.SET_OF_ROLES_ID + "}")
			.then()
			.log().all()
			.statusCode(204);
	}

	/**
	 * 
	 */
	@Test
	void given_requestToUpdateRolesById_when_rolesDontExist_then_getNotFound() {
		/*
		 * Mocking
		 */
		when(repository.updateBySetOfRolesId(
			"7f49f59a-2033-4def-b462-9f64b25b20ea",
			"NA",
			"NA",
			"83c0b10f-b398-4cc8-b356-a3e0f0291679",
			"NA",
			"NA",
			List.of("mil-auth-admin")))
			.thenReturn(UniGenerator.item(Long.valueOf(0)));

		/*
		 * Tests
		 */
		given()
			.log().all()
			.filter(validationFilter)
			.contentType(MediaType.APPLICATION_JSON)
			.body(new CreateOrUpdateSetOfRolesRequest(
				"83c0b10f-b398-4cc8-b356-a3e0f0291679",
				"NA",
				"NA",
				"NA",
				"NA",
				List.of("mil-auth-admin")))
			.pathParam(AdminPathParamName.SET_OF_ROLES_ID, "7f49f59a-2033-4def-b462-9f64b25b20ea")
			.when()
			.put("/{" + AdminPathParamName.SET_OF_ROLES_ID + "}")
			.then()
			.log().all()
			.statusCode(404)
			.contentType(MediaType.APPLICATION_JSON);
	}

	/**
	 * 
	 */
	@Test
	void given_requestToUpdateRolesById_when_databaseErrorOccurs_then_getFailure() {
		/*
		 * Mocking
		 */
		when(repository.updateBySetOfRolesId(
			"7f49f59a-2033-4def-b462-9f64b25b20ea",
			"NA",
			"NA",
			"83c0b10f-b398-4cc8-b356-a3e0f0291679",
			"NA",
			"NA",
			List.of("mil-auth-admin")))
			.thenReturn(Uni.createFrom().failure(new Exception("synthetic exception")));

		/*
		 * Tests
		 */
		given()
			.log().all()
			.filter(validationFilter)
			.contentType(MediaType.APPLICATION_JSON)
			.body(new CreateOrUpdateSetOfRolesRequest(
				"83c0b10f-b398-4cc8-b356-a3e0f0291679",
				"NA",
				"NA",
				"NA",
				"NA",
				List.of("mil-auth-admin")))
			.pathParam(AdminPathParamName.SET_OF_ROLES_ID, "7f49f59a-2033-4def-b462-9f64b25b20ea")
			.when()
			.put("/{" + AdminPathParamName.SET_OF_ROLES_ID + "}")
			.then()
			.log().all()
			.statusCode(500)
			.contentType(MediaType.APPLICATION_JSON);
	}
}