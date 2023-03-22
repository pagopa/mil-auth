/*
 * KeyRetriever.java
 *
 * 22 mar 2023
 */
package it.gov.pagopa.swclient.mil.service;

import java.time.Instant;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.nimbusds.jose.JOSEException;

import io.quarkus.logging.Log;
import io.quarkus.redis.datasource.ReactiveRedisDataSource;
import io.quarkus.redis.datasource.keys.ReactiveKeyCommands;
import io.quarkus.redis.datasource.value.ReactiveValueCommands;
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
	private ReactiveKeyCommands<String> keyCommands;

	/*
	 * 
	 */
	private ReactiveValueCommands<String, KeyPair> valueCommands;

	/*
	 * 
	 */
	@Inject
	KeyPairGenerator keyPairGenerator;
	
	/**
	 * 
	 * @param reactive
	 */
	public KeyRetriever(ReactiveRedisDataSource reactive) { 
        valueCommands = reactive.value(KeyPair.class); 
        keyCommands = reactive.key();  
    }
	
	/**
	 * 
	 * @return
	 */
	public Uni<KeyPair> getKeyPair() {
		Log.debug("Retrieve kids.");
		return keyCommands.keys("*") // Retrieve kids.
			.onItem().transformToMulti(kids -> Multi.createFrom().items(kids.stream())) // Transform the list of kids in a stream of events (one event for a kid).
			.onItem().transformToUniAndMerge(valueCommands::get) // For each kid retrieve the key pair.
			.filter(keyPair -> keyPair.getExp() > Instant.now().getEpochSecond()) // Filter expired key pairs.
			.collect() // Collect all key pairs.
			.asList() // Convert the key pair events in an event that is the list of key pair.
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
						valueCommands.set(keyPair.getKid(), keyPair);

						// Set when Redis has to remove it.
						Log.debug("Set when Redis has to remove generated key pair.");
						keyCommands.expireat(keyPair.getKid(), keyPair.getExp());

						return Uni.createFrom().item(keyPair);
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
			});
	}

	/**
	 * 
	 * @return
	 */
	public Uni<PublicKeys> getPublicKeys() {
		Log.debug("Retrieve public keys.");
		return keyCommands.keys("*") // Retrieve kids.
			.onItem().transformToMulti(kids -> Multi.createFrom().items(kids.stream())) // Transform the list of kids in a stream of events (one event for a kid).
			.onItem().transformToUniAndMerge(valueCommands::get) // For each kid retrieve the key pair.
			.filter(keyPair -> keyPair.getExp() > Instant.now().getEpochSecond()) // Filter expired key pairs.
			.map(keyPair -> keyPair.getPublicKey()) // Extract the public key from the key pair.
			.collect() // Collect all key pairs.
			.asList() // Convert the key pair events in an event that is the list of key pair.
			.map(publicKeys -> new PublicKeys(publicKeys));
	}
}