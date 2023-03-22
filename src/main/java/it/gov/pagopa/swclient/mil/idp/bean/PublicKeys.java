/*
 * PublicKeys.java
 *
 * 21 mar 2023
 */
package it.gov.pagopa.swclient.mil.idp.bean;

import java.util.List;

/**
 * 
 * @author Antonio Tarricone
 */
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
}