/*
 * ClientRepositoryTest.java
 *
 * 9 lug 2024
 */
package it.pagopa.swclient.mil.auth.dao;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import io.quarkus.mongodb.panache.reactive.ReactivePanacheQuery;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.helpers.test.UniAssertSubscriber;

/**
 * 
 * @author Antonio Tarricone
 */
@QuarkusTest
class ClientRepositoryTest {
	/*
	 * 
	 */
	@InjectMock
	ClientRepository repository;

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
	 * Test method for
	 * {@link it.pagopa.swclient.mil.auth.dao.ClientRepository#findByClientId(java.lang.String)}.
	 */
	@Test
	void testFindByClientId() {
		ClientEntity entity = new ClientEntity("client_id", "channel", "salt", "secret_hash", "description", "subject");

		@SuppressWarnings("unchecked")
		ReactivePanacheQuery<ClientEntity> query = mock(ReactivePanacheQuery.class);
		when(query.firstResult())
			.thenReturn(Uni.createFrom().item(entity));

		when(repository.find(ClientEntity.CLIENT_ID_PRP, "client_id"))
			.thenReturn(query);

		when(repository.findByClientId("client_id"))
			.thenCallRealMethod();

		repository.findByClientId("client_id")
			.subscribe()
			.withSubscriber(UniAssertSubscriber.create())
			.assertCompleted()
			.assertItem(entity);
	}
}
