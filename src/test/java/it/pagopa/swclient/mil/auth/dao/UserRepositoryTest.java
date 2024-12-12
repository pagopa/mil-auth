/*
 * UserRepositoryTest.java
 *
 * 2 nov 2024
 */
package it.pagopa.swclient.mil.auth.dao;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.mockito.Mockito;

import io.quarkus.mongodb.panache.reactive.ReactivePanacheQuery;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.helpers.test.UniAssertSubscriber;
import it.pagopa.swclient.mil.auth.util.UniGenerator;

@QuarkusTest
class UserRepositoryTest {
	/*
	 * 
	 */
	@InjectMock
	UserRepository repository;

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
	void testFindByUsername() {
		UserEntity entity = new UserEntity("user_id", "username", "channel", "salt", "secret_hash", "acquirer_id", "merchant_id");

		@SuppressWarnings("unchecked")
		ReactivePanacheQuery<UserEntity> query = mock(ReactivePanacheQuery.class);
		when(query.firstResultOptional())
			.thenReturn(Uni.createFrom().item(Optional.of(entity)));

		when(repository.find(UserEntity.USERNAME_PRP, "username"))
			.thenReturn(query);

		when(repository.findByUsername("username"))
			.thenCallRealMethod();

		repository.findByUsername("username")
			.subscribe()
			.withSubscriber(UniAssertSubscriber.create())
			.assertCompleted()
			.assertItem(Optional.of(entity));
	}

	/**
	 * 
	 */
	@Test
	void testDeleteByUsername() {
		when(repository.delete(UserEntity.USERNAME_PRP, "83c0b10f-b398-4cc8-b356-a3e0f0291679"))
			.thenReturn(UniGenerator.item(Long.valueOf(1)));

		when(repository.deleteByUsername("83c0b10f-b398-4cc8-b356-a3e0f0291679"))
			.thenCallRealMethod();

		repository.deleteByUsername("83c0b10f-b398-4cc8-b356-a3e0f0291679")
			.subscribe()
			.withSubscriber(UniAssertSubscriber.create())
			.assertCompleted()
			.assertItem(Long.valueOf(1));
	}
}