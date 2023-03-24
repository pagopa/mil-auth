/*
 * ResourceOwnerCredentialsEntity.java
 *
 * 20 mar 2023
 */
package it.gov.pagopa.swclient.mil.idp.dao;

/**
 * 
 * @author Antonio Tarricone
 */
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
	 */
	public ResourceOwnerCredentialsEntity() {
	}

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
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * 
	 * @param username the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
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
	 * @param salt the salt to set
	 */
	public void setSalt(String salt) {
		this.salt = salt;
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
	 * @param passwordHash the passwordHash to set
	 */
	public void setPasswordHash(String passwordHash) {
		this.passwordHash = passwordHash;
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
	 * @param acquirerId the acquirerId to set
	 */
	public void setAcquirerId(String acquirerId) {
		this.acquirerId = acquirerId;
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
	 * @param channel the channel to set
	 */
	public void setChannel(String channel) {
		this.channel = channel;
	}

	/**
	 * 
	 * @return the merchantId
	 */
	public String getMerchantId() {
		return merchantId;
	}

	/**
	 * @param merchantId the merchantId to set
	 */
	public void setMerchantId(String merchantId) {
		this.merchantId = merchantId;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return new StringBuilder("ResourceOwnerCredentialsEntity [username=")
			.append("***")
			.append(", salt=")
			.append("***")
			.append(", passwordHash=")
			.append("***")
			.append(", acquirerId=")
			.append(acquirerId)
			.append(", channel=")
			.append(channel)
			.append(", merchantId=")
			.append(merchantId)
			.append("]")
			.toString();
	}
}