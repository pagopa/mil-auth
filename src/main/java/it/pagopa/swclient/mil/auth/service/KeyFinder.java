/*
 * KeyFinder.java
 *
 * 22 mar 2023
 */
package it.pagopa.swclient.mil.auth.service;

import static it.pagopa.swclient.mil.auth.ErrorCode.ERROR_GENERATING_KEY_PAIR;
import static it.pagopa.swclient.mil.auth.util.UniGenerator.error;
import static it.pagopa.swclient.mil.auth.util.UniGenerator.item;

import java.time.Instant;
import java.util.Optional;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import com.nimbusds.jose.JOSEException;

import io.quarkus.logging.Log;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import it.pagopa.swclient.mil.auth.bean.KeyPair;
import it.pagopa.swclient.mil.auth.bean.PublicKey;
import it.pagopa.swclient.mil.auth.bean.PublicKeys;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 * 
 * @author Antonio Tarricone
 */
@ApplicationScoped
public class KeyFinder {
	/*
	 * Access token duration.
	 */
	@ConfigProperty(name = "access.duration")
	long accessDuration;

	/*
	 * 
	 */
	@Inject
	RedisClient redisClient;

	/*
	 * 
	 */
	@Inject
	KeyPairGenerator keyPairGenerator;

	/**
	 * Returns the valid (not expired yet) key pair with the greatest expiration. If there are no valid
	 * key pair a new one is generated.
	 * 
	 * @return
	 */
	public Uni<KeyPair> findKeyPair() {
		Log.debug("Search for the key pair with greatest expiration not expired yet.");
		return redisClient.keys("*") // Loading kids.
			.onItem().transformToMulti(kids -> Multi.createFrom().items(kids.stream())) // Transforming the list of kids in a stream of events (one event for a kid).
			.onItem().transformToUniAndMerge(redisClient::get) // For each kid, getting the key pair.
			.filter(k -> k.getExp() > Instant.now().toEpochMilli() - accessDuration * 1000) // Filtering expired key pairs or that will expire before the expiration of the access token.
			.collect() // Collecting all key pairs.
			.asList() // Converting the key pair events in an event that is the list of key pair.
			.chain(l -> {
				if (l.isEmpty()) {
					/*
					 * There are no valid key pairs: generation of the pair.
					 */
					Log.debug("There are no valid key pairs: generation of the pair.");
					try {
						// Generating a new key pair.
						KeyPair keyPair = keyPairGenerator.generate();

						// Adding it to key list.
						l.add(keyPair);

						// Key pair storage in Redis.
						Log.debug("Key pair storage.");
						return redisClient.setex(keyPair.getKid(), keyPair.getExp(), keyPair)
							.chain(() -> item(keyPair));
					} catch (JOSEException e) {
						String message = String.format("[%s] Error generating the key pair.", ERROR_GENERATING_KEY_PAIR);
						Log.fatalf(e, message);
						return error(ERROR_GENERATING_KEY_PAIR, message);
					}
				} else {
					/*
					 * If there are valid key pairs, search for the pair with the greatest expiration.
					 */
					Log.debug("Search for the pair with the greatest expiration.");
					l.sort((x, y) -> {
						if (x.getExp() < y.getExp()) {
							return 1;
						} else if (x.getExp() == y.getExp()) {
							return 0;
						} else {
							return -1;
						}
					});
					return item(l.get(0));
				}
			});
	}

	/**
	 * 
	 * @return
	 */
	public Uni<PublicKeys> findPublicKeys() {
		Log.debug("Search for the public keys.");
		return redisClient.keys("*") // Loading kids.
			.onItem().transformToMulti(kids -> Multi.createFrom().items(kids.stream())) // Transforming the list of kids in a stream of events (one event for a kid).
			.onItem().transformToUniAndMerge(redisClient::get) // For each kid, getting the key pair.
			.filter(k -> k.getExp() > Instant.now().toEpochMilli()) // Filtering expired key pairs.
			.map(KeyPair::publicKey) // Getting the public key from the key pair.
			.collect() // Collecting all public keys.
			.asList() // Converting the public key events in an event that is the list of public keys.
			.invoke(l -> Log.debugf("Found %d valid key/s.", l.size()))
			.map(PublicKeys::new);
	}

	/**
	 * 
	 * @return
	 */
	public Uni<Optional<PublicKey>> findPublicKey(String kid) {
		Log.debugf("Search for the public key %s.", kid);
		return redisClient.get(kid)
			.map(k -> {
				if (k != null) {
					long threshold = Instant.now().toEpochMilli();
					if (k.getExp() > threshold) {
						Log.debugf("Key %s found. Found expiration %d, threshold %d.", kid, k.getExp(), threshold);
						return Optional.of(k.publicKey());
					} else {
						Log.warnf("Key %s expired.", kid);
						return Optional.empty();
					}
				} else {
					Log.warnf("Key %s not found.", kid);
					return Optional.empty();
				}
			});
	}
}