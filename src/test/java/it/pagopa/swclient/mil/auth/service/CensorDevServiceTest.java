/*
 * CensorDevServiceTest.java
 *
 * 13 mar 2025
 */
package it.pagopa.swclient.mil.auth.service;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.mutiny.Uni;
import it.pagopa.swclient.mil.auth.dao.ClientEntity;
import it.pagopa.swclient.mil.auth.dao.ClientRepository;
import it.pagopa.swclient.mil.auth.dao.RolesRepository;
import it.pagopa.swclient.mil.auth.dao.SetOfRolesEntity;
import it.pagopa.swclient.mil.auth.dao.UserEntity;
import it.pagopa.swclient.mil.auth.dao.UserRepository;

/**
 * 
 * @author antonio.tarricone
 */
@QuarkusTest
class CensorDevServiceTest {
	/*
	 * 
	 */
	@InjectMock
	ClientRepository clientRepository;

	/*
	 * 
	 */
	@InjectMock
	RolesRepository rolesRepository;

	/*
	 * 
	 */
	@InjectMock
	UserRepository userRepository;

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
	void onStartOk() {
		when(clientRepository.persist(any(ClientEntity.class))).thenAnswer(i -> {
			return Uni.createFrom().item(i.getArgument(0, ClientEntity.class));
		});

		when(rolesRepository.persist(any(SetOfRolesEntity.class))).thenAnswer(i -> {
			return Uni.createFrom().item(i.getArgument(0, SetOfRolesEntity.class));
		});

		when(userRepository.persist(any(UserEntity.class))).thenAnswer(i -> {
			return Uni.createFrom().item(i.getArgument(0, UserEntity.class));
		});

		CensorDevService service = new CensorDevService(clientRepository, rolesRepository, userRepository);
		assertThatNoException().isThrownBy(() -> service.onStart(null));
	}

	/**
	 * 
	 */
	@Test
	void onStartKo() {
		when(clientRepository.persist(any(ClientEntity.class))).thenAnswer(i -> {
			return Uni.createFrom().failure(new Exception());
		});

		when(rolesRepository.persist(any(SetOfRolesEntity.class))).thenAnswer(i -> {
			return Uni.createFrom().failure(new Exception());
		});

		when(userRepository.persist(any(UserEntity.class))).thenAnswer(i -> {
			return Uni.createFrom().failure(new Exception());
		});

		CensorDevService service = new CensorDevService(clientRepository, rolesRepository, userRepository);
		assertThatNoException().isThrownBy(() -> service.onStart(null));
	}
}