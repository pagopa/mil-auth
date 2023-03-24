/*
 * KeyRetriever.java
 *
 * 22 mar 2023
 */
package it.gov.pagopa.swclient.mil.idp.service;

import java.time.Instant;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.nimbusds.jose.JOSEException;

import io.quarkus.logging.Log;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import it.gov.pagopa.swclient.mil.idp.bean.KeyPair;
import it.gov.pagopa.swclient.mil.idp.bean.PublicKeys;

/**
 * 
 * @author Antonio Tarricone
 */
@ApplicationScoped
public class KeyRetriever {
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
	 * Return the valid (not expired yet) key pair with the greatest expiration. If there are no valid
	 * key pair a new one is generated.
	 * 
	 * @return
	 */
	public Uni<KeyPair> getKeyPair() {
		Log.debug("Retrieve kids.");
		return redisClient.keys("*") // Retrieve kids.
			.log()
			.onItem().transformToMulti(kids -> Multi.createFrom().items(kids.stream())) // Transform the list of kids in a stream of events (one event for a kid).
			.log()
			.onItem().transformToUniAndMerge(redisClient::get) // For each kid retrieve the key pair.
			.log()
			.filter(keyPair -> keyPair.getExp() > Instant.now().getEpochSecond()) // Filter expired key pairs.
			.log()
			.collect() // Collect all key pairs.
			.asList() // Convert the key pair events in an event that is the list of key pair.
			.log()
			.chain(keyPairs -> {
				if (keyPairs.isEmpty()) {
					/*
					 * If there are no valid key pair, generate one and store it in Redis.
					 */
					Log.debugf("There are no valid key pairs: I generate one.");
					try {
						// Generate a new key pair.
						KeyPair keyPair = keyPairGenerator.generateRsaKey();

						// Add it to key list.
						keyPairs.add(keyPair);

						// Store it in Redis.
						Log.debug("Store generated key pair in Redis.");
						return redisClient.set(keyPair.getKid(), keyPair)
							.log()
							.chain(() -> {
								// Set when Redis has to remove it.
								Log.debug("Set when Redis has to remove generated key pair.");
								redisClient.expireat(keyPair.getKid(), keyPair.getExp());
								return Uni.createFrom().item(keyPair);
							});
					} catch (JOSEException e) {
						Log.fatalf(e, "Error while generating key pair.");
						return Uni.createFrom().failure(e);
					}
				} else {
					/*
					 * If there are valid key pairs, return the pair with the greatest expiration.
					 */
					Log.debugf("There are valid key pairs: return the pair with the greatest expiration.");
					keyPairs.sort((x, y) -> {
						if (x.getExp() < y.getExp()) {
							return 1;
						} else if (x.getExp() == y.getExp()) {
							return 0;
						} else {
							return -1;
						}
					});
					return Uni.createFrom().item(keyPairs.get(0));
				}
			})
			.log();
	}

	/**
	 * 
	 * @return
	 */
	public Uni<PublicKeys> getPublicKeys() {
		Log.debug("Retrieve public keys.");
		return redisClient.keys("*") // Retrieve kids.
			.onItem().transformToMulti(kids -> Multi.createFrom().items(kids.stream())) // Transform the list of kids in a stream of events (one event for a kid).
			.onItem().transformToUniAndMerge(redisClient::get) // For each kid retrieve the key pair.
			.filter(keyPair -> keyPair.getExp() > Instant.now().getEpochSecond()) // Filter expired key pairs.
			.map(keyPair -> keyPair.getPublicKey()) // Extract the public key from the key pair.
			.collect() // Collect all key pairs.
			.asList() // Convert the key pair events in an event that is the list of key pair.
			.map(publicKeys -> new PublicKeys(publicKeys));
	}
}