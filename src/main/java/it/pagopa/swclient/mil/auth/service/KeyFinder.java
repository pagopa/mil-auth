/*
 * KeyFinder.java
 *
 * 7 ago 2023
 */
package it.pagopa.swclient.mil.auth.service;

import java.util.Optional;

import io.smallrye.mutiny.Uni;
import it.pagopa.swclient.mil.auth.bean.PublicKey;
import it.pagopa.swclient.mil.auth.bean.PublicKeys;

/**
 * 
 */
public interface KeyFinder {
	/**
	 * Finds all valid public keys.
	 * 
	 * @return
	 */
	public Uni<PublicKeys> findPublicKeys();

	/**
	 * Finds the public key having the given kid.
	 * 
	 * @param kid
	 * @return
	 */
	public Uni<Optional<PublicKey>> findPublicKey(String kid);
}