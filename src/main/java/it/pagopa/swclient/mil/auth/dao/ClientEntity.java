/*
 * ClientEntity.java
 *
 * 16 mar 2023
 */
package it.pagopa.swclient.mil.auth.dao;

/**
 * 
 * @author Antonio Tarricone
 */
public class ClientEntity {
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
	private String secret;

	/*
	 * 
	 */
	private String description;

	/**
	 * 
	 */
	public ClientEntity() {
	}

	/**
	 * @param id
	 * @param channel
	 * @param secret
	 * @param description
	 */
	public ClientEntity(String id, String channel, String secret, String description) {
		this.id = id;
		this.channel = channel;
		this.secret = secret;
		this.description = description;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the channel
	 */
	public String getChannel() {
		return channel;
	}

	/**
	 * @param channel the channel to set
	 */
	public void setChannel(String channel) {
		this.channel = channel;
	}

	/**
	 * @return the secret
	 */
	public String getSecret() {
		return secret;
	}

	/**
	 * @param secret the secret to set
	 */
	public void setSecret(String secret) {
		this.secret = secret;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return new StringBuilder("ClientEntity [id=")
			.append(id)
			.append(", channel=")
			.append(channel)
			.append(", secret=")
			.append("***")
			.append(", description=")
			.append(description)
			.append("]")
			.toString();
	}
}