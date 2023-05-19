/*
 * PublicKeys.java
 *
 * 21 mar 2023
 */
package it.pagopa.swclient.mil.auth.bean;

import java.util.List;
import java.util.Objects;

import io.quarkus.runtime.annotations.RegisterForReflection;

/**
 * 
 * @author Antonio Tarricone
 */
@RegisterForReflection
public class PublicKeys {
	/*
	 * 
	 */
	private List<PublicKey> keys;

	/**
	 * 
	 */
	public PublicKeys() {
	}

	/**
	 * 
	 * @param keys
	 */
	public PublicKeys(List<PublicKey> keys) {
		this.keys = keys;
	}

	/**
	 * 
	 * @return the keys
	 */
	public List<PublicKey> getKeys() {
		return keys;
	}

	/**
	 * 
	 * @param keys the keys to set
	 */
	public void setKeys(List<PublicKey> keys) {
		this.keys = keys;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return new StringBuilder("PublicKeys [keys=")
			.append(keys)
			.append("]")
			.toString();
	}

	/**
	 * @see java.lang.Object#equals(Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PublicKeys other = (PublicKeys) obj;
		return Objects.equals(keys, other.keys);
	}
}