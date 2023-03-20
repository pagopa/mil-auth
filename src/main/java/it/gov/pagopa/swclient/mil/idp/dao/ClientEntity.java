/*
 * ClientEntity.java
 *
 * 16 mar 2023
 */
package it.gov.pagopa.swclient.mil.idp.dao;

/**
 * 
 * @author Antonio Tarricone
 */
public class ClientEntity {
	/*
	 * 
	 */
	public String clientId;

	/*
	 * 
	 */
	public String channel;
	
	/*
	 * 
	 */
	public String description;

	/**
	 * 
	 */
	public ClientEntity() {
	}
	
	/**
	 * 
	 * @param clientId
	 * @param channel
	 * @param description
	 */
	public ClientEntity(String clientId, String channel, String description) {
		this.clientId = clientId;
		this.channel = channel;
		this.description = description;
	}

	/**
	 * 
	 * @return the clientId
	 */
	public String getClientId() {
		return clientId;
	}

	/**
	 * 
	 * @param clientId the clientId to set
	 */
	public void setClientId(String clientId) {
		this.clientId = clientId;
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
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * 
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
		return new StringBuilder("ClientEntity [clientId=")
			.append(clientId)
			.append(", channel=")
			.append(channel)
			.append(", description=")
			.append(description)
			.append("]")
			.toString();
	}
}