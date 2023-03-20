/*
 * RefreshAccessToken.java
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
public class RefreshAccessToken extends GetAccessToken {
	/*
	 * grant_type
	 */
	@FormParam("grant_type")
	@NotNull(message = "[" + ErrorCode.GRANT_TYPE_MUST_NOT_BE_NULL + "] grant_type must not be null")
	@Pattern(regexp = "^refresh_token$", message = "[" + ErrorCode.GRANT_TYPE_MUST_MATCH_REGEXP + "] grant_type must match \"{regexp}\"")
	private String grantType;

	/*
	 * refresh_token
	 */
	@FormParam("refresh_token")
	@NotNull(message = "[" + ErrorCode.REFRESH_TOKEN_MUST_NOT_BE_NULL + "] refresh_token must not be null")
	@Pattern(regexp = "^[a-zA-Z0-9_-]{1,1024}\\\\.[a-zA-Z0-9_-]{1,1024}\\\\.[a-zA-Z0-9_-]{1,1024}$", message = "[" + ErrorCode.REFRESH_TOKEN_MUST_MATCH_REGEXP + "] refresh_token must match \"{regexp}\"")
	private String refreshToken;

	/**
	 * 
	 * @return the grantType
	 */
	public String getGrantType() {
		return grantType;
	}

	/**
	 * 
	 * @param grantType the grantType to set
	 */
	public void setGrantType(String grantType) {
		this.grantType = grantType;
	}

	/**
	 * 
	 * @return the refreshToken
	 */
	public String getRefreshToken() {
		return refreshToken;
	}

	/**
	 * 
	 * @param refreshToken the refreshToken to set
	 */
	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return new StringBuilder("RefreshAccessToken [grantType=")
			.append(grantType)
			.append(", refreshToken=")
			.append(refreshToken)
			.append(", clientId=")
			.append(getClientId())
			.append("]")
			.toString();
	}
}