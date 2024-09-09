/*
 * RolesRepositoryTest.java
 *
 * 9 lug 2024
 */
package it.pagopa.swclient.mil.auth.dao;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.bson.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.mockito.Mockito;

import io.quarkus.mongodb.panache.common.reactive.ReactivePanacheUpdate;
import io.quarkus.mongodb.panache.reactive.ReactivePanacheQuery;
import io.quarkus.panache.common.Sort;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.helpers.test.UniAssertSubscriber;
import io.smallrye.mutiny.tuples.Tuple2;
import it.pagopa.swclient.mil.auth.util.UniGenerator;

/**
 * 
 * @author Antonio Tarricone
 */
@QuarkusTest
@SuppressWarnings("unchecked")
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
		Mockito.reset(repository);
	}

	/**
	 * 
	 */
	@Test
	void testFindByFullKey() {
		SetOfRolesEntity entity = new SetOfRolesEntity("id", "acquirer_id", "channel", "client_id", "merchant_id", "terminal_id", List.of("role"));

		ReactivePanacheQuery<SetOfRolesEntity> query = mock(ReactivePanacheQuery.class);
		when(query.firstResultOptional())
			.thenReturn(Uni.createFrom().item(Optional.of(entity)));

		when(repository.find(RolesRepository.FIND_BY_FULL_KEY, "acquirer_id", "channel", "client_id", "merchant_id", "terminal_id"))
			.thenReturn(query);

		when(repository.findByFullKey("acquirer_id", "channel", "client_id", "merchant_id", "terminal_id"))
			.thenCallRealMethod();

		repository.findByFullKey("acquirer_id", "channel", "client_id", "merchant_id", "terminal_id")
			.subscribe()
			.withSubscriber(UniAssertSubscriber.create())
			.assertCompleted()
			.assertItem(Optional.of(entity));
	}

	/**
	 * 
	 */
	@Test
	void testDeleteBySetOfRolesId() {
		when(repository.delete(SetOfRolesEntity.ID_PRP, "id"))
			.thenReturn(UniGenerator.item(Long.valueOf(1)));

		when(repository.deleteBySetOfRolesId("id"))
			.thenCallRealMethod();

		repository.deleteBySetOfRolesId("id")
			.subscribe()
			.withSubscriber(UniAssertSubscriber.create())
			.assertCompleted()
			.assertItem(Long.valueOf(1));
	}

	/**
	 * 
	 */
	@Test
	void testFindAll() {
		SetOfRolesEntity entity1 = new SetOfRolesEntity()
			.setAcquirerId("acquirer_id_1")
			.setChannel("ATM")
			.setClientId("client_id_1")
			.setMerchantId("merchant_id_1")
			.setTerminalId("terminal_id_1")
			.setRoles(List.of("role_1_1", "role_1_2"));

		SetOfRolesEntity entity2 = new SetOfRolesEntity()
			.setAcquirerId("acquirer_id_2")
			.setChannel("POS")
			.setClientId("client_id_2")
			.setMerchantId("merchant_id_2")
			.setTerminalId("terminal_id_2")
			.setRoles(List.of("role_2_1", "role_2_2"));

		ReactivePanacheQuery<SetOfRolesEntity> query = mock(ReactivePanacheQuery.class);
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
	void testFindByParameters() {
		SetOfRolesEntity entity1 = new SetOfRolesEntity()
			.setAcquirerId("acquirer_id")
			.setChannel("channel")
			.setClientId("client_id")
			.setMerchantId("merchant_id")
			.setTerminalId("terminal_id_1")
			.setRoles(List.of("role_1_1", "role_1_2"));

		SetOfRolesEntity entity2 = new SetOfRolesEntity()
			.setAcquirerId("acquirer_id")
			.setChannel("channel")
			.setClientId("client_id")
			.setMerchantId("merchant_id")
			.setTerminalId("terminal_id_2")
			.setRoles(List.of("role_2_1", "role_2_2"));

		ReactivePanacheQuery<SetOfRolesEntity> query = mock(ReactivePanacheQuery.class);
		when(query.list())
			.thenReturn(UniGenerator.item(List.of(entity1, entity2)));
		when(query.page(1, 2))
			.thenReturn(query);

		when(repository
			.find(
				any(String.class),
				any(Sort.class),
				any(Map.class)))
			.thenReturn(query);

		when(repository
			.count(
				any(String.class),
				any(Map.class)))
			.thenReturn(UniGenerator.item(Long.valueOf(2)));

		when(repository.findByParameters(1, 2, "acquirer_id", "channel", "client_id", "merchant_id", null))
			.thenCallRealMethod();

		repository.findByParameters(1, 2, "acquirer_id", "channel", "client_id", "merchant_id", null)
			.subscribe()
			.withSubscriber(UniAssertSubscriber.create())
			.assertCompleted()
			.assertItem(Tuple2.of(Long.valueOf(2), List.of(entity1, entity2)));
	}
	
	/**
	 * 
	 */
	@Test
	void testFindByNoParameters() {
		SetOfRolesEntity entity1 = new SetOfRolesEntity()
			.setAcquirerId("acquirer_id")
			.setChannel("channel")
			.setClientId("client_id")
			.setMerchantId("merchant_id")
			.setTerminalId("terminal_id_1")
			.setRoles(List.of("role_1_1", "role_1_2"));

		SetOfRolesEntity entity2 = new SetOfRolesEntity()
			.setAcquirerId("acquirer_id")
			.setChannel("channel")
			.setClientId("client_id")
			.setMerchantId("merchant_id")
			.setTerminalId("terminal_id_2")
			.setRoles(List.of("role_2_1", "role_2_2"));

		ReactivePanacheQuery<SetOfRolesEntity> query = mock(ReactivePanacheQuery.class);
		when(query.list())
			.thenReturn(UniGenerator.item(List.of(entity1, entity2)));
		when(query.page(0, 2))
			.thenReturn(query);

		when(repository
			.findAll(any(Sort.class)))
			.thenReturn(query);

		when(repository
			.count())
			.thenReturn(UniGenerator.item(Long.valueOf(2)));

		when(repository.findByParameters(0, 2, null, null, null, null, null))
			.thenCallRealMethod();

		repository.findByParameters(0, 2, null, null, null, null, null)
			.subscribe()
			.withSubscriber(UniAssertSubscriber.create())
			.assertCompleted()
			.assertItem(Tuple2.of(Long.valueOf(2), List.of(entity1, entity2)));
	}

	/**
	 * 
	 */
	@Test
	void testFindBySetOfRolesId() {
		SetOfRolesEntity entity = new SetOfRolesEntity()
			.setSetOfRolesId("7f49f59a-2033-4def-b462-9f64b25b20ea")
			.setAcquirerId("acquirer_id")
			.setChannel("channel")
			.setClientId("client_id")
			.setMerchantId("merchant_id")
			.setTerminalId("terminal_id_1")
			.setRoles(List.of("role_1_1", "role_1_2"));

		ReactivePanacheQuery<SetOfRolesEntity> query = mock(ReactivePanacheQuery.class);
		when(query.firstResultOptional())
			.thenReturn(UniGenerator.item(Optional.of(entity)));

		when(repository.find(SetOfRolesEntity.ID_PRP, "7f49f59a-2033-4def-b462-9f64b25b20ea"))
			.thenReturn(query);

		when(repository.findBySetOfRolesId("7f49f59a-2033-4def-b462-9f64b25b20ea"))
			.thenCallRealMethod();

		repository.findBySetOfRolesId("7f49f59a-2033-4def-b462-9f64b25b20ea")
			.subscribe()
			.withSubscriber(UniAssertSubscriber.create())
			.assertCompleted()
			.assertItem(Optional.of(entity));
	}

	/**
	 * 
	 */
	@Test
	void testUpdateBySetOfRolesId() {
		ReactivePanacheUpdate panacheUpdate = mock(ReactivePanacheUpdate.class);
		when(panacheUpdate.where(SetOfRolesEntity.ID_PRP, "id"))
			.thenReturn(UniGenerator.item(Long.valueOf(1)));

		Document update = new Document("$set", new Document()
			.append(SetOfRolesEntity.ACQUIRER_ID_PRP, "acquirer_id")
			.append(SetOfRolesEntity.CHANNEL_PRP, "channel")
			.append(SetOfRolesEntity.CLIENT_ID_PRP, "client_id")
			.append(SetOfRolesEntity.MERCHANT_ID_PRP, "merchant_id")
			.append(SetOfRolesEntity.TERMINAL_ID_PRP, "terminal_id")
			.append(SetOfRolesEntity.ROLES_PRP, List.of("role")));

		when(repository.update(update))
			.thenReturn(panacheUpdate);

		when(repository
			.updateBySetOfRolesId(
				"id",
				"acquirer_id",
				"channel",
				"client_id",
				"merchant_id",
				"terminal_id",
				List.of("role")))
			.thenCallRealMethod();

		repository
			.updateBySetOfRolesId(
				"id",
				"acquirer_id",
				"channel",
				"client_id",
				"merchant_id",
				"terminal_id",
				List.of("role"))
			.subscribe()
			.withSubscriber(UniAssertSubscriber.create())
			.assertCompleted()
			.assertItem(Long.valueOf(1));
	}
}
