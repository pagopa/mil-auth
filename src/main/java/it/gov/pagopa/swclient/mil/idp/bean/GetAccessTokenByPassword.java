/*
 * GetAccessTokenByPassword.java
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
public class GetAccessTokenByPassword extends GetAccessToken {
	/*
	 * grant_type
	 */
	@FormParam("grant_type")
	@NotNull(message = "[" + ErrorCode.GRANT_TYPE_MUST_NOT_BE_NULL + "] grant_type must not be null")
	@Pattern(regexp = "^password$", message = "[" + ErrorCode.GRANT_TYPE_MUST_MATCH_REGEXP + "] grant_type must match \"{regexp}\"")
	private String grantType;

	/*
	 * username
	 */
	@FormParam("username")
	@NotNull(message = "[" + ErrorCode.USERNAME_MUST_NOT_BE_NULL + "] username must not be null")
	@Pattern(regexp = "^[ -~]{1,64}$", message = "[" + ErrorCode.USERNAME_MUST_MATCH_REGEXP + "] username must match \"{regexp}\"")
	private String username;

	/*
	 * password
	 */
	@FormParam("password")
	@NotNull(message = "[" + ErrorCode.PASSWORD_MUST_NOT_BE_NULL + "] password must not be null")
	@Pattern(regexp = "^[ -~]{1,64}$", message = "[" + ErrorCode.PASSWORD_MUST_MATCH_REGEXP + "] password must match \"{regexp}\"")
	private String password;

	/*
	 * scope
	 */
	@FormParam("scope")
	@Pattern(regexp = "^offline_access$", message = "[" + ErrorCode.SCOPE_MUST_MATCH_REGEXP + "] scope must match \"{regexp}\"")
	private String scope;

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
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * 
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * 
	 * @return the scope
	 */
	public String getScope() {
		return scope;
	}

	/**
	 * 
	 * @param scope the scope to set
	 */
	public void setScope(String scope) {
		this.scope = scope;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return new StringBuilder("GetAccessTokenByPassword [grantType=")
			.append(grantType)
			.append(", username=")
			.append("***")
			.append(", password=")
			.append("***")
			.append(", scope=")
			.append(scope)
			.append(", clientId=")
			.append(getClientId())
			.append("]").toString();
	}
}