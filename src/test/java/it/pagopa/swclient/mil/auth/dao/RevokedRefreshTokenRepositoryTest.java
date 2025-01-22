/*
 * RevokedRefreshTokenRepositoryTest.java
 *
 * 20 gen 2025
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

/**
 * 
 * @author Antonio Tarricone
 */
@QuarkusTest
class RevokedRefreshTokenRepositoryTest {
	/*
	 * 
	 */
	@InjectMock
	RevokedRefreshTokenRepository repository;

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
	void testfindByRefreshTokenId() {
		RevokedRefreshTokenEntity entity = new RevokedRefreshTokenEntity().setJwtId("jwt_id");

		@SuppressWarnings("unchecked")
		ReactivePanacheQuery<RevokedRefreshTokenEntity> query = mock(ReactivePanacheQuery.class);
		when(query.firstResultOptional())
			.thenReturn(Uni.createFrom().item(Optional.of(entity)));

		when(repository.find(RevokedRefreshTokenEntity.JWT_ID, "jwt_id"))
			.thenReturn(query);

		when(repository.findByJwtId("jwt_id"))
			.thenCallRealMethod();

		repository.findByJwtId("jwt_id")
			.subscribe()
			.withSubscriber(UniAssertSubscriber.create())
			.assertCompleted()
			.assertItem(Optional.of(entity));
	}
}