/*
 * GrantEntity.java
 *
 * 20 mar 2023
 */
package it.pagopa.swclient.mil.auth.dao;

import java.util.List;

/**
 * @author antonio.tarricone
 *
 */
public class GrantEntity {
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
	private String clientId;

	/*
	 * 
	 */
	private String merchantId;

	/*
	 * 
	 */
	private String terminalId;

	/*
	 * 
	 */
	private List<String> grants;

	/**
	 * 
	 */
	public GrantEntity() {
	}

	/**
	 * 
	 * @param acquirerId
	 * @param channel
	 * @param clientId
	 * @param merchantId
	 * @param terminalId
	 * @param grants
	 */
	public GrantEntity(String acquirerId, String channel, String clientId, String merchantId, String terminalId, List<String> grants) {
		this.acquirerId = acquirerId;
		this.channel = channel;
		this.clientId = clientId;
		this.merchantId = merchantId;
		this.terminalId = terminalId;
		this.grants = grants;
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
	 * @return the merchantId
	 */
	public String getMerchantId() {
		return merchantId;
	}

	/**
	 * 
	 * @param merchantId the merchantId to set
	 */
	public void setMerchantId(String merchantId) {
		this.merchantId = merchantId;
	}

	/**
	 * 
	 * @return the terminalId
	 */
	public String getTerminalId() {
		return terminalId;
	}

	/**
	 * 
	 * @param terminalId the terminalId to set
	 */
	public void setTerminalId(String terminalId) {
		this.terminalId = terminalId;
	}

	/**
	 * 
	 * @return the grants
	 */
	public List<String> getGrants() {
		return grants;
	}

	/**
	 * 
	 * @param grants the grants to set
	 */
	public void setGrants(List<String> grants) {
		this.grants = grants;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return new StringBuilder("GrantEntity [acquirerId=")
			.append(acquirerId)
			.append(", channel=")
			.append(channel)
			.append(", clientId=")
			.append(clientId)
			.append(", merchantId=")
			.append(merchantId)
			.append(", terminalId=")
			.append(terminalId)
			.append(", grants=")
			.append(grants)
			.append("]")
			.toString();
	}

}
