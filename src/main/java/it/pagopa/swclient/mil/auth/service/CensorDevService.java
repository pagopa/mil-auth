/*
 * CensorDevService.java
 *
 * 27 gen 2025
 */
package it.pagopa.swclient.mil.auth.service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

import org.bouncycastle.crypto.generators.Argon2BytesGenerator;
import org.bouncycastle.crypto.params.Argon2Parameters;

import io.quarkus.arc.profile.IfBuildProfile;
import io.quarkus.logging.Log;
import io.quarkus.runtime.StartupEvent;
import it.pagopa.swclient.mil.auth.admin.resource.UserResource;
import it.pagopa.swclient.mil.auth.dao.ClientEntity;
import it.pagopa.swclient.mil.auth.dao.ClientRepository;
import it.pagopa.swclient.mil.auth.dao.RolesRepository;
import it.pagopa.swclient.mil.auth.dao.SetOfRolesEntity;
import it.pagopa.swclient.mil.auth.dao.UserEntity;
import it.pagopa.swclient.mil.auth.dao.UserRepository;
import it.pagopa.swclient.mil.auth.util.SecretTriplet;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;

/**
 * This service is executed on startup when the profile is dev and initializes the repositories with
 * test data.
 * 
 * @author Antonio Tarricone
 */
@IfBuildProfile("dev")
@ApplicationScoped
public class CensorDevService {
	/*
	 * 
	 */
	private ClientRepository clientRepository;

	/*
	 * 
	 */
	private RolesRepository rolesRepository;

	/*
	 * 
	 */
	private UserRepository userRepository;

	/*
	 * 
	 */
	private static final String CLIENT_ID = "00000000-0000-0000-0000-000000000000";
	private static final String SUBJECT = "00000000000";
	private static final String DESCRIPTION = "Client for Dev profile";
	private static final byte[] SECRET = "0".repeat(SecretTriplet.SECRET_LEN).getBytes(StandardCharsets.UTF_8);
	private static final byte[] SALT = new byte[SecretTriplet.SALT_LEN];

	private static final String SECRETLESS_CLIENT_ID = "99999999-9999-9999-9999-999999999999";
	private static final String SECRETLESS_SUBJECT = "99999999999";
	private static final String SECRETLESS_DESCRIPTION = "Secretless Client for Dev profile";

	private static final String SET_OF_ROLES_ID = "aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa";

	private static final String USER_ID = "ffffffff-ffff-ffff-ffff-ffffffffffff";
	private static final String USERNAME = "user.name@email.gov.it";
	private static final byte[] PASSWORD = "f".repeat(UserResource.PASSWORD_LEN).getBytes(StandardCharsets.UTF_8);

	private static final String NA = "NA";

	/**
	 * 
	 * @param clientRepository
	 * @param rolesRepository
	 * @param userRepository
	 */
	@Inject
	CensorDevService(ClientRepository clientRepository, RolesRepository rolesRepository, UserRepository userRepository) {
		this.clientRepository = clientRepository;
		this.rolesRepository = rolesRepository;
		this.userRepository = userRepository;
	}

	/**
	 * 
	 * @param ev
	 */
	void onStart(@Observes StartupEvent ev) {
		/*
		 * Initialize hash generator.
		 */
		Argon2Parameters.Builder builder = new Argon2Parameters.Builder(Argon2Parameters.ARGON2_id)
			.withVersion(Argon2Parameters.ARGON2_VERSION_13)
			.withIterations(SecretTriplet.ITERATIONS)
			.withMemoryAsKB(SecretTriplet.MEM_LIMIT)
			.withParallelism(SecretTriplet.PARALLELISM)
			.withSalt(SALT);

		Argon2BytesGenerator generator = new Argon2BytesGenerator();
		generator.init(builder.build());

		/*
		 * Calculate secret hash.
		 */
		byte[] secretHash = new byte[SecretTriplet.HASH_LEN];
		generator.generateBytes(SECRET, secretHash, 0, SecretTriplet.HASH_LEN);

		/*
		 * Store client in the DB.
		 */
		clientRepository
			.persist(new ClientEntity(
				CLIENT_ID,
				null,
				Base64.getEncoder().encodeToString(SALT),
				Base64.getEncoder().encodeToString(secretHash),
				DESCRIPTION,
				SUBJECT))
			.subscribe()
			.with(
				i -> Log.debugf("Client stored: %s", i),
				f -> Log.errorf(f, "Error storing client"));

		/*
		 * Store secretless client in the DB.
		 */
		clientRepository
			.persist(new ClientEntity(
				SECRETLESS_CLIENT_ID,
				null,
				null,
				null,
				SECRETLESS_DESCRIPTION,
				SECRETLESS_SUBJECT))
			.subscribe()
			.with(
				i -> Log.debugf("Client stored: %s", i),
				f -> Log.errorf(f, "Error storing client"));

		/*
		 * Store roles in the DB.
		 */
		rolesRepository.persist(new SetOfRolesEntity(
			SET_OF_ROLES_ID,
			NA,
			NA,
			CLIENT_ID,
			NA,
			NA,
			List.of("mil-auth-admin")))
			.subscribe()
			.with(
				i -> Log.debugf("Roles stored: %s", i),
				f -> Log.errorf(f, "Error storing roles"));

		/*
		 * Store roles for secretless client in the DB.
		 */
		rolesRepository.persist(new SetOfRolesEntity(
			SET_OF_ROLES_ID,
			NA,
			NA,
			SECRETLESS_CLIENT_ID,
			NA,
			NA,
			List.of("mil-auth-admin")))
			.subscribe()
			.with(
				i -> Log.debugf("Roles stored: %s", i),
				f -> Log.errorf(f, "Error storing roles"));

		/*
		 * Calculate password hash.
		 */
		byte[] passwordHash = new byte[SecretTriplet.HASH_LEN];
		generator.generateBytes(PASSWORD, passwordHash, 0, SecretTriplet.HASH_LEN);

		/*
		 * Store user in the DB.
		 */
		userRepository
			.persist(new UserEntity(
				USER_ID,
				USERNAME,
				null,
				Base64.getEncoder().encodeToString(SALT),
				Base64.getEncoder().encodeToString(passwordHash),
				null,
				null,
				SECRETLESS_CLIENT_ID))
			.subscribe()
			.with(
				i -> Log.debugf("User stored: %s", i),
				f -> Log.errorf(f, "Error storing user"));
	}
}