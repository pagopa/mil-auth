/*
 * ClientRepositoryTest.java
 *
 * 9 lug 2024
 */
package it.pagopa.swclient.mil.auth.dao;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.bson.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.mockito.Mockito;

import io.quarkus.mongodb.panache.common.reactive.ReactivePanacheUpdate;
import io.quarkus.mongodb.panache.reactive.ReactivePanacheQuery;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.helpers.test.UniAssertSubscriber;
import it.pagopa.swclient.mil.auth.util.UniGenerator;

/**
 * 
 * @author Antonio Tarricone
 */
@QuarkusTest
@SuppressWarnings("unchecked")
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
		Mockito.reset(repository);
	}

	/**
	 * 
	 */
	@Test
	void testFindByClientId() {
		ClientEntity entity = new ClientEntity("client_id", "channel", "salt", "secret_hash", "description", "subject");

		ReactivePanacheQuery<ClientEntity> query = mock(ReactivePanacheQuery.class);
		when(query.firstResultOptional())
			.thenReturn(Uni.createFrom().item(Optional.of(entity)));

		when(repository.find(ClientEntity.CLIENT_ID_PRP, "client_id"))
			.thenReturn(query);

		when(repository.findByClientId("client_id"))
			.thenCallRealMethod();

		repository.findByClientId("client_id")
			.subscribe()
			.withSubscriber(UniAssertSubscriber.create())
			.assertCompleted()
			.assertItem(Optional.of(entity));
	}

	/**
	 * 
	 */
	@Test
	void testFindAll() {
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

		when(repository.findAll(1, 2))
			.thenCallRealMethod();

		repository.findAll(1, 2)
			.subscribe()
			.withSubscriber(UniAssertSubscriber.create())
			.assertCompleted()
			.assertItem(List.of(entity1, entity2));
	}

	/**
	 * 
	 */
	@Test
	void testDeleteByClientId() {
		when(repository.delete(ClientEntity.CLIENT_ID_PRP, "83c0b10f-b398-4cc8-b356-a3e0f0291679"))
			.thenReturn(UniGenerator.item(Long.valueOf(1)));

		when(repository.deleteByClientId("83c0b10f-b398-4cc8-b356-a3e0f0291679"))
			.thenCallRealMethod();

		repository.deleteByClientId("83c0b10f-b398-4cc8-b356-a3e0f0291679")
			.subscribe()
			.withSubscriber(UniAssertSubscriber.create())
			.assertCompleted()
			.assertItem(Long.valueOf(1));
	}

	/**
	 * 
	 */
	@Test
	void testUpdateByClientId() {
		ReactivePanacheUpdate panacheUpdate = mock(ReactivePanacheUpdate.class);
		when(panacheUpdate.where(ClientEntity.CLIENT_ID_PRP, "83c0b10f-b398-4cc8-b356-a3e0f0291679"))
			.thenReturn(UniGenerator.item(Long.valueOf(1)));

		Document update = new Document("$set", new Document()
			.append(ClientEntity.CHANNEL_PRP, "ATM")
			.append(ClientEntity.DESCRIPTION_PRP, "Test description")
			.append(ClientEntity.SUBJECT_PRP, "test-subject"));

		when(repository.update(update))
			.thenReturn(panacheUpdate);

		when(repository
			.updateByClientId(
				"83c0b10f-b398-4cc8-b356-a3e0f0291679",
				"ATM",
				"Test description",
				"test-subject"))
			.thenCallRealMethod();

		repository
			.updateByClientId(
				"83c0b10f-b398-4cc8-b356-a3e0f0291679",
				"ATM",
				"Test description",
				"test-subject")
			.subscribe()
			.withSubscriber(UniAssertSubscriber.create())
			.assertCompleted()
			.assertItem(Long.valueOf(1));
	}
}
