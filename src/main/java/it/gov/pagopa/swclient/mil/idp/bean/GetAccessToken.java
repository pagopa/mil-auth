/*
 * GetAccessToken.java
 *
 * 16 mar 2023
 */
package it.gov.pagopa.swclient.mil.idp.bean;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.ws.rs.FormParam;

import io.quarkus.runtime.annotations.RegisterForReflection;
import it.gov.pagopa.swclient.mil.idp.ErrorCode;

/**
 * 
 * @author Antonio Tarricone
 */
@RegisterForReflection
@it.gov.pagopa.swclient.mil.idp.validation.constraints.GrantTypeInterface(message = "[" + ErrorCode.GRANT_TYPE_INCONSISTENT + "] If grant_type equals to password, username and password must not be null and refresh_token must be null. If grant_type equals to refresh_token, refresh_token must not be null and username and password must be null.")
public class GetAccessToken {
	/*
	 * grant_type
	 */
	@FormParam("grant_type")
	@NotNull(message = "[" + ErrorCode.GRANT_TYPE_MUST_NOT_BE_NULL + "] grant_type must not be null")
	@Pattern(regexp = "^" + GrantType.PASSWORD + "|" + GrantType.REFRESH_TOKEN + "|" + GrantType.POYNT_TOKEN + "$", message = "[" + ErrorCode.GRANT_TYPE_MUST_MATCH_REGEXP + "] grant_type must match \"{regexp}\"")
	private String grantType;

	/*
	 * username
	 */
	@FormParam("username")
	@Pattern(regexp = "^[ -~]{1,64}$", message = "[" + ErrorCode.USERNAME_MUST_MATCH_REGEXP + "] username must match \"{regexp}\"")
	private String username;

	/*
	 * password
	 */
	@FormParam("password")
	@Pattern(regexp = "^[ -~]{1,64}$", message = "[" + ErrorCode.PASSWORD_MUST_MATCH_REGEXP + "] password must match \"{regexp}\"")
	private String password;

	/*
	 * refresh_token
	 */
	@FormParam("refresh_token")
	@Pattern(regexp = "^[a-zA-Z0-9_-]{1,1024}\\.[a-zA-Z0-9_-]{1,1024}\\.[a-zA-Z0-9_-]{1,1024}$", message = "[" + ErrorCode.REFRESH_TOKEN_MUST_MATCH_REGEXP + "] refresh_token must match \"{regexp}\"")
	private String refreshToken;

	/*
	 * poynt_token
	 */
	@FormParam("ext_token")
	//@Pattern(regexp = "^[ -~]{1,4096}$", message = "[" + ErrorCode.EXT_TOKEN_MUST_MATCH_REGEXP + "] ext_token must match \"{regexp}\"")
	private String extToken;

	/*
	 * add_data
	 */
	@FormParam("add_data")
	//@Pattern(regexp = "^[ -~]{1,4096}$", message = "[" + ErrorCode.ADD_DATA_MUST_MATCH_REGEXP + "] add_data must match \"{regexp}\"")
	private String addData;

	/*
	 * client_id
	 */
	@FormParam("client_id")
	@NotNull(message = "[" + ErrorCode.CLIENT_ID_MUST_NOT_BE_NULL + "] client_id must not be null")
	@Pattern(regexp = "^[a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12}$", message = "[" + ErrorCode.CLIENT_ID_MUST_MATCH_REGEXP + "] client_id must match \"{regexp}\"")
	private String clientId;

	/*
	 * scope
	 */
	@FormParam("scope")
	@Pattern(regexp = "^offline_access$", message = "[" + ErrorCode.SCOPE_MUST_MATCH_REGEXP + "] scope must match \"{regexp}\"")
	private String scope;

	/**
	 * 
	 */
	public GetAccessToken() {
	}

	/**
	 * @return the grantType
	 */
	public String getGrantType() {
		return grantType;
	}

	/**
	 * @param grantType the grantType to set
	 */
	public void setGrantType(String grantType) {
		this.grantType = grantType;
	}

	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @param username the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @return the refreshToken
	 */
	public String getRefreshToken() {
		return refreshToken;
	}

	/**
	 * @param refreshToken the refreshToken to set
	 */
	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	/**
	 * @return the clientId
	 */
	public String getClientId() {
		return clientId;
	}

	/**
	 * @param clientId the clientId to set
	 */
	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	/**
	 * @return the scope
	 */
	public String getScope() {
		return scope;
	}

	/**
	 * @param scope the scope to set
	 */
	public void setScope(String scope) {
		this.scope = scope;
	}

	/**
	 * @return the extToken
	 */
	public String getExtToken() {
		return extToken;
	}

	/**
	 * @param extToken the extToken to set
	 */
	public void setExtToken(String extToken) {
		this.extToken = extToken;
	}

	/**
	 * @return the addData
	 */
	public String getAddData() {
		return addData;
	}

	/**
	 * @param addData the addData to set
	 */
	public void setAddData(String addData) {
		this.addData = addData;
	}
	
	/**
	 * @return
	 */
	public boolean isPasswordGrantType() {
		return grantType.equals(GrantType.PASSWORD);
	}
	
	/**
	 * @return
	 */
	public boolean isRefreshTokenGrantType() {
		return grantType.equals(GrantType.REFRESH_TOKEN);
	}
	
	/**
	 * @return
	 */
	public boolean isPoyntTokenGrantType() {
		return grantType.equals(GrantType.POYNT_TOKEN);
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return new StringBuilder("GetAccessToken [grantType=")
			.append(grantType)
			.append(", username=***")
			.append(", password=***")
			.append(", refreshToken=")
			.append(refreshToken)
			.append(", extToken=")
			.append(extToken)
			.append(", addData=")
			.append(addData)
			.append(", clientId=")
			.append(clientId)
			.append(", scope=")
			.append(scope)
			.append("]")
			.toString();
	}
}