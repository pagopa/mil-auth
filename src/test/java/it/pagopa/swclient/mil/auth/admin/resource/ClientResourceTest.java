/*
 * ClientResourceTest.java
 *
 * 30 lug 2024
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
import org.bson.Document;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import com.atlassian.oai.validator.restassured.OpenApiValidationFilter;
import com.mongodb.MongoWriteException;
import com.nimbusds.jose.util.StandardCharset;

import io.quarkus.mongodb.panache.common.reactive.ReactivePanacheUpdate;
import io.quarkus.mongodb.panache.reactive.ReactivePanacheQuery;
import io.quarkus.test.InjectMock;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.smallrye.mutiny.Uni;
import it.pagopa.swclient.mil.auth.admin.bean.AdminJsonPropertyName;
import it.pagopa.swclient.mil.auth.admin.bean.AdminPathParamName;
import it.pagopa.swclient.mil.auth.admin.bean.AdminQueryParamName;
import it.pagopa.swclient.mil.auth.admin.bean.Client;
import it.pagopa.swclient.mil.auth.admin.bean.CreateOrUpdateClientRequest;
import it.pagopa.swclient.mil.auth.admin.bean.PageMetadata;
import it.pagopa.swclient.mil.auth.admin.bean.PageOfClients;
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
@SuppressWarnings("unchecked")
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
			.body(new CreateOrUpdateClientRequest(null, "Test description", "test-subject"))
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
			.body(new CreateOrUpdateClientRequest(null, "Test description", "test-subject"))
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
			.body(new CreateOrUpdateClientRequest(null, "Test description", "test-subject"))
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
			.body(new CreateOrUpdateClientRequest(null, "Test description", "test-subject"))
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
	void given_requestToReadAllClients_when_allGoesOk_then_getTheListOfFoundClients() {
		/*
		 * Mocking
		 */
		ClientEntity entity1 = new ClientEntity()
			.setChannel("ATM")
			.setClientId("83c0b10f-b398-4cc8-b356-a3e0f0291679")
			.setDescription("description #1")
			.setSalt("zfN59oSr9RfFiiSASUO1YIcv8bARsj1OAV8tEydQiKC3su5Mlz1TsjbFwvWrGCjXdkDUsbeXGnYZDavJuTKw6Q==")
			.setSecretHash("zrH0O1skqerDIQ5uuzSkva0ZBZ3mrzV0OxdX69sBWBs=")
			.setSubject("subject #1");

		ClientEntity entity2 = new ClientEntity()
			.setChannel("POS")
			.setClientId("3965df56-ca9a-49e5-97e8-061433d4a25b")
			.setDescription("description #2")
			.setSalt("aGw/h/8Fm9S2aNvlvIaxJyhKP67ZU4FEm6mDVhL3aEVrahXFif9x2BkQ4OY87Z9tWVyWbSB/JeztYVmTshrFWQ==")
			.setSecretHash("6y//vlAdvWKBgtxZ8AYUuISqwzPJbTB+6Ed4TRYRPfU=")
			.setSubject("subject #2");

		ReactivePanacheQuery<ClientEntity> query = mock(ReactivePanacheQuery.class);
		when(query.list())
			.thenReturn(UniGenerator.item(List.of(entity1, entity2)));
		when(query.page(1, 2))
			.thenReturn(query);

		when(repository.findAll(any()))
			.thenReturn(query);
		when(repository.count())
			.thenReturn(UniGenerator.item(Long.valueOf(5)));

		/*
		 * Expected values
		 */
		Client dto1 = new Client()
			.setChannel("ATM")
			.setClientId("83c0b10f-b398-4cc8-b356-a3e0f0291679")
			.setDescription("description #1")
			.setSalt("zfN59oSr9RfFiiSASUO1YIcv8bARsj1OAV8tEydQiKC3su5Mlz1TsjbFwvWrGCjXdkDUsbeXGnYZDavJuTKw6Q==")
			.setSecretHash("zrH0O1skqerDIQ5uuzSkva0ZBZ3mrzV0OxdX69sBWBs=")
			.setSubject("subject #1");

		Client dto2 = new Client()
			.setChannel("POS")
			.setClientId("3965df56-ca9a-49e5-97e8-061433d4a25b")
			.setDescription("description #2")
			.setSalt("aGw/h/8Fm9S2aNvlvIaxJyhKP67ZU4FEm6mDVhL3aEVrahXFif9x2BkQ4OY87Z9tWVyWbSB/JeztYVmTshrFWQ==")
			.setSecretHash("6y//vlAdvWKBgtxZ8AYUuISqwzPJbTB+6Ed4TRYRPfU=")
			.setSubject("subject #2");

		PageMetadata metadata = new PageMetadata(5, 3, 1, 2);

		/*
		 * Tests
		 */
		PageOfClients clients = given()
			.filter(validationFilter)
			.queryParam(AdminQueryParamName.PAGE, 1)
			.queryParam(AdminQueryParamName.SIZE, 2)
			.when()
			.get()
			.then()
			.log()
			.everything()
			.statusCode(200)
			.contentType(MediaType.APPLICATION_JSON)
			.extract()
			.response()
			.as(PageOfClients.class);

		assertThat(clients.getClients())
			.containsExactlyInAnyOrder(
				dto1,
				dto2);

		assertEquals(metadata, clients.getPage());
	}
	
	/**
	 * 
	 */
	@Test
	void given_requestToReadAllClients_when_noClientFound_then_getEmptyList() {
		/*
		 * Mocking
		 */
		ReactivePanacheQuery<ClientEntity> query = mock(ReactivePanacheQuery.class);
		when(query.list())
			.thenReturn(UniGenerator.item(List.of()));
		when(query.page(1, 2))
			.thenReturn(query);

		when(repository.findAll(any()))
			.thenReturn(query);
		when(repository.count())
			.thenReturn(UniGenerator.item(Long.valueOf(0)));

		/*
		 * Expected values
		 */
		PageMetadata metadata = new PageMetadata(0, 0, 1, 2);

		/*
		 * Tests
		 */
		PageOfClients clients = given()
			.filter(validationFilter)
			.queryParam(AdminQueryParamName.PAGE, 1)
			.queryParam(AdminQueryParamName.SIZE, 2)
			.when()
			.get()
			.then()
			.log()
			.everything()
			.statusCode(200)
			.contentType(MediaType.APPLICATION_JSON)
			.extract()
			.response()
			.as(PageOfClients.class);

		assertThat(clients.getClients())
			.isEmpty();

		assertEquals(metadata, clients.getPage());
	}
	
	/**
	 * 
	 */
	@Test
	void given_requestToReadAllClients_when_databaseErrorOccurs_then_getServerError() {
		/*
		 * Mocking
		 */
		ReactivePanacheQuery<ClientEntity> query = mock(ReactivePanacheQuery.class);
		when(query.list())
			.thenReturn(UniGenerator.item(List.of()));
		when(query.page(1, 2))
			.thenReturn(query);

		when(repository.findAll(any()))
			.thenReturn(query);
		when(repository.count())
			.thenReturn(Uni.createFrom().failure(new Exception("synthetic exception")));

		/*
		 * Tests
		 */
		given()
			.filter(validationFilter)
			.queryParam(AdminQueryParamName.PAGE, 1)
			.queryParam(AdminQueryParamName.SIZE, 2)
			.when()
			.get()
			.then()
			.log()
			.everything()
			.statusCode(500)
			.contentType(MediaType.APPLICATION_JSON);
	}

	/**
	 * 
	 */
	@Test
	void given_requestToReadClient_when_allGoesOk_then_getIt() {
		/*
		 * Mocking
		 */
		ClientEntity entity = new ClientEntity()
			.setChannel("ATM")
			.setClientId("83c0b10f-b398-4cc8-b356-a3e0f0291679")
			.setDescription("description #1")
			.setSalt("zfN59oSr9RfFiiSASUO1YIcv8bARsj1OAV8tEydQiKC3su5Mlz1TsjbFwvWrGCjXdkDUsbeXGnYZDavJuTKw6Q==")
			.setSecretHash("zrH0O1skqerDIQ5uuzSkva0ZBZ3mrzV0OxdX69sBWBs=")
			.setSubject("subject #1");

		ReactivePanacheQuery<ClientEntity> query = mock(ReactivePanacheQuery.class);
		when(query.firstResultOptional())
			.thenReturn(UniGenerator.item(Optional.of(entity)));

		when(repository.find(ClientEntity.CLIENT_ID_PRP, "83c0b10f-b398-4cc8-b356-a3e0f0291679"))
			.thenReturn(query);

		/*
		 * Expected values
		 */
		Client dto = new Client()
			.setChannel("ATM")
			.setClientId("83c0b10f-b398-4cc8-b356-a3e0f0291679")
			.setDescription("description #1")
			.setSalt("zfN59oSr9RfFiiSASUO1YIcv8bARsj1OAV8tEydQiKC3su5Mlz1TsjbFwvWrGCjXdkDUsbeXGnYZDavJuTKw6Q==")
			.setSecretHash("zrH0O1skqerDIQ5uuzSkva0ZBZ3mrzV0OxdX69sBWBs=")
			.setSubject("subject #1");

		/*
		 * Tests
		 */
		Client client = given()
			// .filter(validationFilter)
			.pathParam(AdminPathParamName.CLIENT_ID, "83c0b10f-b398-4cc8-b356-a3e0f0291679")
			.when()
			.get("/{" + AdminPathParamName.CLIENT_ID + "}")
			.then()
			.log()
			.everything()
			.statusCode(200)
			.contentType(MediaType.APPLICATION_JSON)
			.extract()
			.response()
			.as(Client.class);

		assertEquals(dto, client);
	}

	/**
	 * 
	 */
	@Test
	void given_requestToReadClient_when_clientDoesntExist_then_getNotFound() {
		/*
		 * Mocking
		 */
		ReactivePanacheQuery<ClientEntity> query = mock(ReactivePanacheQuery.class);
		when(query.firstResultOptional())
			.thenReturn(UniGenerator.item(Optional.empty()));

		when(repository.find(ClientEntity.CLIENT_ID_PRP, "83c0b10f-b398-4cc8-b356-a3e0f0291679"))
			.thenReturn(query);

		/*
		 * Tests
		 */
		given()
			// .filter(validationFilter)
			.pathParam(AdminPathParamName.CLIENT_ID, "83c0b10f-b398-4cc8-b356-a3e0f0291679")
			.when()
			.get("/{" + AdminPathParamName.CLIENT_ID + "}")
			.then()
			.log()
			.everything()
			.statusCode(404)
			.contentType(MediaType.APPLICATION_JSON);
	}

	/**
	 * 
	 */
	@Test
	void given_requestToReadClient_when_databaseErrorOccurs_then_getServerError() {
		/*
		 * Mocking
		 */
		ReactivePanacheQuery<ClientEntity> query = mock(ReactivePanacheQuery.class);
		when(query.firstResultOptional())
			.thenReturn(Uni.createFrom().failure(new Exception("synthetic exception")));

		when(repository.find(ClientEntity.CLIENT_ID_PRP, "83c0b10f-b398-4cc8-b356-a3e0f0291679"))
			.thenReturn(query);

		/*
		 * Tests
		 */
		given()
			// .filter(validationFilter)
			.pathParam(AdminPathParamName.CLIENT_ID, "83c0b10f-b398-4cc8-b356-a3e0f0291679")
			.when()
			.get("/{" + AdminPathParamName.CLIENT_ID + "}")
			.then()
			.log()
			.everything()
			.statusCode(500)
			.contentType(MediaType.APPLICATION_JSON);
	}

	/**
	 * 
	 */
	@Test
	void given_requestToUpdateClient_when_allGoesOk_then_getNoContent() {
		/*
		 * Mocking
		 */
		ReactivePanacheUpdate panacheUpdate = mock(ReactivePanacheUpdate.class);
		when(panacheUpdate.where(ClientEntity.CLIENT_ID_PRP, "83c0b10f-b398-4cc8-b356-a3e0f0291679"))
			.thenReturn(UniGenerator.item(Long.valueOf(1)));

		Document update = new Document("$set", new Document()
			.append(ClientEntity.CHANNEL_PRP, "ATM")
			.append(ClientEntity.DESCRIPTION_PRP, "Test description")
			.append(ClientEntity.SUBJECT_PRP, "test-subject"));

		when(repository.update(update))
			.thenReturn(panacheUpdate);

		/*
		 * Tests
		 */
		given()
			// .filter(validationFilter)
			.pathParam(AdminPathParamName.CLIENT_ID, "83c0b10f-b398-4cc8-b356-a3e0f0291679")
			.contentType(MediaType.APPLICATION_JSON)
			.body(new CreateOrUpdateClientRequest("ATM", "Test description", "test-subject"))
			.when()
			.patch("/{" + AdminPathParamName.CLIENT_ID + "}")
			.then()
			.log()
			.everything()
			.statusCode(204);
	}

	/**
	 * 
	 */
	@Test
	void given_requestToUpdateClient_when_clientDoesntExist_then_getNotFound() {
		/*
		 * Mocking
		 */
		ReactivePanacheUpdate panacheUpdate = mock(ReactivePanacheUpdate.class);
		when(panacheUpdate.where(ClientEntity.CLIENT_ID_PRP, "83c0b10f-b398-4cc8-b356-a3e0f0291679"))
			.thenReturn(UniGenerator.item(Long.valueOf(0)));

		Document update = new Document("$set", new Document()
			.append(ClientEntity.CHANNEL_PRP, "ATM")
			.append(ClientEntity.DESCRIPTION_PRP, "Test description")
			.append(ClientEntity.SUBJECT_PRP, "test-subject"));

		when(repository.update(update))
			.thenReturn(panacheUpdate);

		/*
		 * Tests
		 */
		given()
			// .filter(validationFilter)
			.pathParam(AdminPathParamName.CLIENT_ID, "83c0b10f-b398-4cc8-b356-a3e0f0291679")
			.contentType(MediaType.APPLICATION_JSON)
			.body(new CreateOrUpdateClientRequest("ATM", "Test description", "test-subject"))
			.when()
			.patch("/{" + AdminPathParamName.CLIENT_ID + "}")
			.then()
			.log()
			.everything()
			.statusCode(404)
			.contentType(MediaType.APPLICATION_JSON);
	}

	/**
	 * 
	 */
	@Test
	void given_requestToUpdateClient_when_databaseExceptionOccurs_then_getServerError() {
		/*
		 * Mocking
		 */
		ReactivePanacheUpdate panacheUpdate = mock(ReactivePanacheUpdate.class);
		when(panacheUpdate.where(ClientEntity.CLIENT_ID_PRP, "83c0b10f-b398-4cc8-b356-a3e0f0291679"))
			.thenReturn(Uni.createFrom().failure(new Exception("synthetic exception")));

		Document update = new Document("$set", new Document()
			.append(ClientEntity.CHANNEL_PRP, "ATM")
			.append(ClientEntity.DESCRIPTION_PRP, "Test description")
			.append(ClientEntity.SUBJECT_PRP, "test-subject"));

		when(repository.update(update))
			.thenReturn(panacheUpdate);

		/*
		 * Tests
		 */
		given()
			// .filter(validationFilter)
			.pathParam(AdminPathParamName.CLIENT_ID, "83c0b10f-b398-4cc8-b356-a3e0f0291679")
			.contentType(MediaType.APPLICATION_JSON)
			.body(new CreateOrUpdateClientRequest("ATM", "Test description", "test-subject"))
			.when()
			.patch("/{" + AdminPathParamName.CLIENT_ID + "}")
			.then()
			.log()
			.everything()
			.statusCode(500)
			.contentType(MediaType.APPLICATION_JSON);
	}

	/**
	 * 
	 */
	@Test
	void given_requestToDeleteClient_when_allGoesOk_then_getNoContent() {
		/*
		 * Mocking
		 */
		when(repository.delete(ClientEntity.CLIENT_ID_PRP, "83c0b10f-b398-4cc8-b356-a3e0f0291679"))
			.thenReturn(UniGenerator.item((Long.valueOf(1))));

		/*
		 * Tests
		 */
		given()
			// .filter(validationFilter)
			.pathParam(AdminPathParamName.CLIENT_ID, "83c0b10f-b398-4cc8-b356-a3e0f0291679")
			.when()
			.delete("/{" + AdminPathParamName.CLIENT_ID + "}")
			.then()
			.log()
			.everything()
			.statusCode(204);
	}

	/**
	 * 
	 */
	@Test
	void given_requestToDeleteClient_when_clientDoesntExist_then_getNotFound() {
		/*
		 * Mocking
		 */
		when(repository.delete(ClientEntity.CLIENT_ID_PRP, "83c0b10f-b398-4cc8-b356-a3e0f0291679"))
			.thenReturn(UniGenerator.item((Long.valueOf(0))));

		/*
		 * Tests
		 */
		given()
			// .filter(validationFilter)
			.pathParam(AdminPathParamName.CLIENT_ID, "83c0b10f-b398-4cc8-b356-a3e0f0291679")
			.when()
			.delete("/{" + AdminPathParamName.CLIENT_ID + "}")
			.then()
			.log()
			.everything()
			.statusCode(404)
			.contentType(MediaType.APPLICATION_JSON);
	}

	/**
	 * 
	 */
	@Test
	void given_requestToDeleteClient_when_databaseErrorOccurs_then_getServerError() {
		/*
		 * Mocking
		 */
		when(repository.delete(ClientEntity.CLIENT_ID_PRP, "83c0b10f-b398-4cc8-b356-a3e0f0291679"))
			.thenReturn(Uni.createFrom().failure(new Exception("synthetic exception")));

		/*
		 * Tests
		 */
		given()
			// .filter(validationFilter)
			.pathParam(AdminPathParamName.CLIENT_ID, "83c0b10f-b398-4cc8-b356-a3e0f0291679")
			.when()
			.delete("/{" + AdminPathParamName.CLIENT_ID + "}")
			.then()
			.log()
			.everything()
			.statusCode(500)
			.contentType(MediaType.APPLICATION_JSON);
	}
}