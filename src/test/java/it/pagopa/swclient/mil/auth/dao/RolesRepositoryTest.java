/*
 * RolesRepositoryTest.java
 *
 * 9 lug 2024
 */
package it.pagopa.swclient.mil.auth.dao;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

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
class RolesRepositoryTest {
	/*
	 * 
	 */
	@InjectMock
	RolesRepository repository;

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
	 * {@link it.pagopa.swclient.mil.auth.dao.RolesRepository#findByFullKey(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)}.
	 */
	@Test
	void testFindByFullKey() {
		RolesEntity entity = new RolesEntity("acquirer_id", "channel", "client_id", "merchant_id", "terminal_id", List.of("role"));

		@SuppressWarnings("unchecked")
		ReactivePanacheQuery<RolesEntity> query = mock(ReactivePanacheQuery.class);
		when(query.firstResult())
			.thenReturn(Uni.createFrom().item(entity));

		when(repository.find(RolesRepository.FIND_BY_ALL, "acquirer_id", "channel", "client_id", "merchant_id", "terminal_id"))
			.thenReturn(query);

		when(repository.findByFullKey("acquirer_id", "channel", "client_id", "merchant_id", "terminal_id"))
			.thenCallRealMethod();

		repository.findByFullKey("acquirer_id", "channel", "client_id", "merchant_id", "terminal_id")
			.subscribe()
			.withSubscriber(UniAssertSubscriber.create())
			.assertCompleted()
			.assertItem(entity);
	}
}
