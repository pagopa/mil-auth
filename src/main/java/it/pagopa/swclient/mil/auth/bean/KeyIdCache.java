/*
 * KeyIdCache.java
 *
 * 14 giu 2024
 */
package it.pagopa.swclient.mil.auth.bean;

import java.time.Instant;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import io.quarkus.logging.Log;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 
 * @author Antonio Tarricone
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Accessors(chain = true)
public class KeyIdCache {
	/*
	 * 
	 */
	@ConfigProperty(name = "expire-after-write")
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