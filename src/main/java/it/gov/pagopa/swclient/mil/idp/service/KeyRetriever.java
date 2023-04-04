/*
 * KeyRetriever.java
 *
 * 22 mar 2023
 */
package it.gov.pagopa.swclient.mil.idp.service;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import com.nimbusds.jose.JOSEException;

import io.quarkus.logging.Log;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import it.gov.pagopa.swclient.mil.idp.bean.KeyPair;
import it.gov.pagopa.swclient.mil.idp.bean.KeyType;
import it.gov.pagopa.swclient.mil.idp.bean.KeyUse;
import it.gov.pagopa.swclient.mil.idp.bean.PublicKey;
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

	/*
	 * For POC.
	 */
	@ConfigProperty(name = "poc", defaultValue = "false")
	boolean poc;

	@ConfigProperty(name = "d")
	Optional<String> d;

	@ConfigProperty(name = "e")
	Optional<String> e;

	@ConfigProperty(name = "use")
	Optional<String> use;

	@ConfigProperty(name = "kid")
	Optional<String> kid;

	@ConfigProperty(name = "dp")
	Optional<String> dp;

	@ConfigProperty(name = "dq")
	Optional<String> dq;

	@ConfigProperty(name = "n")
	Optional<String> n;

	@ConfigProperty(name = "p")
	Optional<String> p;

	@ConfigProperty(name = "kty")
	Optional<String> kty;

	@ConfigProperty(name = "q")
	Optional<String> q;

	@ConfigProperty(name = "qi")
	Optional<String> qi;

	@ConfigProperty(name = "exp")
	Optional<Long> exp;

	@ConfigProperty(name = "iat")
	Optional<Long> iat;

	/**
	 * 
	 * @return
	 */
	private KeyPair getConfigKeyPair() {
		return new KeyPair(d.get(), e.get(), KeyUse.valueOf(use.get()), kid.get(), dp.get(), dq.get(), n.get(), p.get(), KeyType.valueOf(kty.get()), q.get(), qi.get(), exp.get(), iat.get());
	}

	/**
	 * Return the valid (not expired yet) key pair with the greatest expiration. If there are no valid
	 * key pair a new one is generated.
	 * 
	 * @return
	 */
	public Uni<KeyPair> getKeyPair() {
		Log.debug("Retrieve kids.");
		if (poc) {
			Log.warn("**** POC MODE ****");
			return Uni.createFrom().item(getConfigKeyPair());
		}
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
		if (poc) {
			Log.warn("**** POC MODE ****");
			return Uni.createFrom().item(new PublicKeys(List.of(getConfigKeyPair().getPublicKey())));
		}
		return redisClient.keys("*") // Retrieve kids.
			.onItem().transformToMulti(kids -> Multi.createFrom().items(kids.stream())) // Transform the list of kids in a stream of events (one event for a kid).
			.onItem().transformToUniAndMerge(redisClient::get) // For each kid retrieve the key pair.
			.filter(keyPair -> keyPair.getExp() > Instant.now().getEpochSecond()) // Filter expired key pairs.
			.map(keyPair -> keyPair.getPublicKey()) // Extract the public key from the key pair.
			.collect() // Collect all key pairs.
			.asList() // Convert the key pair events in an event that is the list of key pair.
			.map(publicKeys -> new PublicKeys(publicKeys));
	}

	/**
	 * 
	 * @return
	 */
	public Uni<Optional<PublicKey>> getPublicKey(String kid) {
		Log.debugf("Retrieve public key: ", kid);
		if (poc) {
			Log.warn("**** POC MODE ****");
			return Uni.createFrom().item(Optional.of(getConfigKeyPair().getPublicKey()));
		}
		return redisClient.get(kid)
			.map(keyPair -> {
				if (keyPair == null || keyPair.getExp() < new Date().getTime()) {
					Log.warnf("Key %s not found or expired.", kid);
					return Optional.empty();
				} else {
					Log.debugf("Key %s found.", kid);
					return Optional.of(keyPair.getPublicKey());
				}
			});
	}
}