/*
 * KeyIdCache.java
 *
 * 14 giu 2024
 */
package it.pagopa.swclient.mil.auth.bean;

import java.time.Instant;

import io.quarkus.logging.Log;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * 
 * @author Antonio Tarricone
 */
@Getter
@Setter
@Accessors(chain = true)
public class KeyIdCache {
	/*
	 * 
	 */
	int expireAfterWrite;

	/*
	 * 
	 */
	private String kid;

	/*
	 * 
	 */
	private long exp;

	/*
	 * 
	 */
	private long storedAt;

	/**
	 * 
	 * @param expireAfterWrite
	 */
	public KeyIdCache(int expireAfterWrite) {
		this.expireAfterWrite = expireAfterWrite;
	}

	/**
	 * 
	 * @param remainingLife
	 * @return
	 */
	public boolean isValid(long remainingLife) {
		if (kid == null) {
			Log.debug("kid is null");
			return false;
		}

		long now = Instant.now().getEpochSecond();
		if ((exp - now) < remainingLife) {
			Log.debug("Key is expired");
			return false;
		}

		if ((now - storedAt) > expireAfterWrite) {
			Log.debug("Cache is expired");
			return false;
		}

		return true;
	}

	/**
	 * 
	 */
	public void clean() {
		kid = null;
		exp = 0;
		storedAt = 0;
	}
}