/*
 * RedisClient.java
 *
 * 22 mar 2023
 */
package it.pagopa.swclient.mil.auth.service;

import java.util.List;

import io.quarkus.redis.datasource.ReactiveRedisDataSource;
import io.quarkus.redis.datasource.keys.ReactiveKeyCommands;
import io.quarkus.redis.datasource.value.ReactiveValueCommands;
import io.smallrye.mutiny.Uni;
import it.pagopa.swclient.mil.auth.bean.KeyPair;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * 
 * @author Antonio Tarricone
 */
@ApplicationScoped
public class RedisClient implements KeyVault {
	/*
	 * 
	 */
	private ReactiveKeyCommands<String> keyCommands;

	/*
	 * 
	 */
	private ReactiveValueCommands<String, KeyPair> valueCommands;

	/**
	 * 
	 * @param reactive
	 */
	public RedisClient(ReactiveRedisDataSource reactive) {
		valueCommands = reactive.value(String.class, KeyPair.class);
		keyCommands = reactive.key(String.class);
	}

	/**
	 * 
	 * @param pattern
	 * @return
	 */
	@Override
	public Uni<List<String>> keys(String pattern) {
		return keyCommands.keys(pattern);
	}

	/**
	 * 
	 * @param kid
	 * @return
	 */
	@Override
	public Uni<KeyPair> get(String kid) {
		return valueCommands.get(kid);
	}

	/**
	 * 
	 * @param kid
	 * @param seconds
	 * @param keyPair
	 * @return
	 */
	@Override
	public Uni<Void> setex(String kid, long seconds, KeyPair keyPair) {
		return valueCommands.setex(kid, seconds, keyPair);
	}
}