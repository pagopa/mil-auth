/*
 * KeyVault.java
 *
 * 2 lug 2023
 */
package it.pagopa.swclient.mil.auth.service;

import java.util.List;

import io.smallrye.mutiny.Uni;
import it.pagopa.swclient.mil.auth.bean.KeyPair;

/**
 * 
 */
public interface KeyVault {
	/**
	 * 
	 * @param pattern
	 * @return
	 */
	Uni<List<String>> keys(String pattern);

	/**
	 * 
	 * @param kid
	 * @return
	 */
	Uni<KeyPair> get(String kid);

	/**
	 * 
	 * @param kid
	 * @param seconds
	 * @param keyPair
	 * @return
	 */
	Uni<Void> setex(String kid, long seconds, KeyPair keyPair);
}