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
	 * @param remainingLife
	 * @param expireAfterWrite
	 * @return
	 */
	public boolean isValid(long remainingLife, long expireAfterWrite) {
		if (kid == null) {
			Log.debug("kid is null");
			return false;
		}

		long now = Instant.now().getEpochSecond();
		if ((exp - now) < remainingLife) {
			Log.debugf("Key is expired: exp = %d, now = %d, remainingLife = %d", exp, now, remainingLife);
			return false;
		}

		if ((now - storedAt) > expireAfterWrite) {
			Log.debugf("Cache is expired: now = %d, storedAt = %d, expireAfterWrite = %d", now, storedAt, expireAfterWrite);
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