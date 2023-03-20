/*
 * GetAccessToken.java
 *
 * 16 mar 2023
 */
package it.gov.pagopa.swclient.mil.idp.bean;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.ws.rs.FormParam;

import it.gov.pagopa.swclient.mil.idp.ErrorCode;

/**
 * 
 * @author Antonio Tarricone
 */
public abstract class GetAccessToken {
	/*
	 * client_id
	 */
	@FormParam("client_id")
	@NotNull(message = "[" + ErrorCode.CLIENT_ID_MUST_NOT_BE_NULL + "] client_id must not be null")
	@Pattern(regexp = "^[a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12}$", message = "[" + ErrorCode.CLIENT_ID_MUST_MATCH_REGEXP + "] client_id must match \"{regexp}\"")
	private String clientId;

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
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return new StringBuilder("GetAccessToken [clientId=")
			.append(clientId)
			.append("]")
			.toString();
	}
}