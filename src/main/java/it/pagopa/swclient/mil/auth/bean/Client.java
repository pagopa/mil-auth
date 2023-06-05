/*
 * Client.java
 *
 * 16 mar 2023
 */
package it.pagopa.swclient.mil.auth.bean;

/**
 * 
 * @author Antonio Tarricone
 */
@SuppressWarnings("unused")
public class Client {
	/*
	 * 
	 */
	private String id;

	/*
	 * 
	 */
	private String channel;

	/*
	 * 
	 */
	private String salt;

	/*
	 * 
	 */
	private String secretHash;

	/*
	 * 
	 */
	private String description;

	/**
	 * @param id
	 * @param channel
	 * @param secret
	 * @param description
	 */
	public Client(String id, String channel, String salt, String secretHash, String description) {
		this.id = id;
		this.channel = channel;
		this.salt = salt;
		this.secretHash = secretHash;
		this.description = description;
	}

	/**
	 * @return the channel
	 */
	public String getChannel() {
		return channel;
	}

	/**
	 * @return the salt
	 */
	public String getSalt() {
		return salt;
	}

	/**
	 * @return the secretHash
	 */
	public String getSecretHash() {
		return secretHash;
	}
}