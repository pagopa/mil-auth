/*
 * ResourceOwnerCredentialsEntity.java
 *
 * 20 mar 2023
 */
package it.pagopa.swclient.mil.auth.dao;

/**
 * 
 * FOR DEMO ONLY. THIS WILL BE REPLACED BY DB.
 * 
 * @author Antonio Tarricone
 */
@SuppressWarnings("unused")
public class ResourceOwnerCredentialsEntity {
	/*
	 * 
	 */
	private String username;

	/*
	 * 
	 */
	private String salt;

	/*
	 * 
	 */
	private String passwordHash;

	/*
	 * 
	 */
	private String acquirerId;

	/*
	 * 
	 */
	private String channel;

	/*
	 * 
	 */
	private String merchantId;

	/**
	 * 
	 * @param username
	 * @param salt
	 * @param passwordHash
	 * @param acquirerId
	 * @param channel
	 * @param merchantId
	 */
	public ResourceOwnerCredentialsEntity(String username, String salt, String passwordHash, String acquirerId, String channel, String merchantId) {
		this.username = username;
		this.salt = salt;
		this.passwordHash = passwordHash;
		this.acquirerId = acquirerId;
		this.channel = channel;
		this.merchantId = merchantId;
	}

	/**
	 * 
	 * @return the salt
	 */
	public String getSalt() {
		return salt;
	}

	/**
	 * 
	 * @return the passwordHash
	 */
	public String getPasswordHash() {
		return passwordHash;
	}

	/**
	 * 
	 * @return the acquirerId
	 */
	public String getAcquirerId() {
		return acquirerId;
	}

	/**
	 * 
	 * @return the channel
	 */
	public String getChannel() {
		return channel;
	}

	/**
	 * 
	 * @return the merchantId
	 */
	public String getMerchantId() {
		return merchantId;
	}
}