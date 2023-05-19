/*
 * GetAccessToken.java
 *
 * 16 mar 2023
 */
package it.pagopa.swclient.mil.auth.bean;

import static it.pagopa.swclient.mil.ErrorCode.ACQUIRER_ID_MUST_MATCH_REGEXP_MSG;
import static it.pagopa.swclient.mil.ErrorCode.CHANNEL_MUST_MATCH_REGEXP_MSG;
import static it.pagopa.swclient.mil.ErrorCode.MERCHANT_ID_MUST_MATCH_REGEXP_MSG;
import static it.pagopa.swclient.mil.ErrorCode.REQUEST_ID_MUST_MATCH_REGEXP_MSG;
import static it.pagopa.swclient.mil.ErrorCode.TERMINAL_ID_MUST_MATCH_REGEXP_MSG;
import static it.pagopa.swclient.mil.ErrorCode.VERSION_MUST_MATCH_REGEXP_MSG;
import static it.pagopa.swclient.mil.ErrorCode.VERSION_SIZE_MUST_BE_AT_MOST_MAX_MSG;
import static it.pagopa.swclient.mil.auth.ErrorCode.ADD_DATA_MUST_MATCH_REGEXP;
import static it.pagopa.swclient.mil.auth.ErrorCode.CLIENT_ID_MUST_MATCH_REGEXP;
import static it.pagopa.swclient.mil.auth.ErrorCode.CLIENT_ID_MUST_NOT_BE_NULL;
import static it.pagopa.swclient.mil.auth.ErrorCode.CLIENT_SECRET_MUST_MATCH_REGEXP;
import static it.pagopa.swclient.mil.auth.ErrorCode.EXT_TOKEN_MUST_MATCH_REGEXP;
import static it.pagopa.swclient.mil.auth.ErrorCode.GRANT_TYPE_MUST_MATCH_REGEXP;
import static it.pagopa.swclient.mil.auth.ErrorCode.GRANT_TYPE_MUST_NOT_BE_NULL;
import static it.pagopa.swclient.mil.auth.ErrorCode.INCONSISTENT_REQUEST;
import static it.pagopa.swclient.mil.auth.ErrorCode.PASSWORD_MUST_MATCH_REGEXP;
import static it.pagopa.swclient.mil.auth.ErrorCode.REFRESH_TOKEN_MUST_MATCH_REGEXP;
import static it.pagopa.swclient.mil.auth.ErrorCode.SCOPE_MUST_MATCH_REGEXP;
import static it.pagopa.swclient.mil.auth.ErrorCode.USERNAME_MUST_MATCH_REGEXP;
import static it.pagopa.swclient.mil.auth.bean.Channel.ATM;
import static it.pagopa.swclient.mil.auth.bean.Channel.POS;

import io.quarkus.runtime.annotations.RegisterForReflection;
import it.pagopa.swclient.mil.auth.validation.constraints.ValidationTarget;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.HeaderParam;

/**
 * 
 * @author Antonio Tarricone
 */
@RegisterForReflection
@ValidationTarget(message = "[" + INCONSISTENT_REQUEST + "] Inconsistent request.")
public class GetAccessToken {
	/*
	 * Request ID
	 */
	@HeaderParam("RequestId")
	@Pattern(regexp = "^[a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12}$", message = REQUEST_ID_MUST_MATCH_REGEXP_MSG)
	private String requestId;

	/*
	 * Version of the required API
	 */
	@HeaderParam("Version")
	@Size(max = 64, message = VERSION_SIZE_MUST_BE_AT_MOST_MAX_MSG)
	@Pattern(regexp = "^(0|[1-9]\\d*)\\.(0|[1-9]\\d*)\\.(0|[1-9]\\d*)(?:-((?:0|[1-9]\\d*|\\d*[a-zA-Z-][0-9a-zA-Z-]*)(?:\\.(?:0|[1-9]\\d*|\\d*[a-zA-Z-][0-9a-zA-Z-]*))*))?(?:\\+([0-9a-zA-Z-]+(?:\\.[0-9a-zA-Z-]+)*))?$", message = VERSION_MUST_MATCH_REGEXP_MSG)
	private String version;

	/*
	 * Acquirer ID assigned by PagoPA
	 */
	@HeaderParam("AcquirerId")
	@Pattern(regexp = "^\\d{1,11}$", message = ACQUIRER_ID_MUST_MATCH_REGEXP_MSG)
	private String acquirerId;

	/*
	 * Channel originating the request
	 */
	@HeaderParam("Channel")
	@Pattern(regexp = "^(" + ATM + "|" + POS + ")$", message = CHANNEL_MUST_MATCH_REGEXP_MSG)
	private String channel;

	/*
	 * Merchant ID originating the transaction. If Channel equals to POS, MerchantId must not be null.
	 */
	@HeaderParam("MerchantId")
	@Pattern(regexp = "^[0-9a-zA-Z]{1,15}$", message = MERCHANT_ID_MUST_MATCH_REGEXP_MSG)
	private String merchantId;

	/*
	 * ID of the terminal originating the transaction. It must be unique per acquirer, channel and
	 * merchant if present.
	 */
	@HeaderParam("TerminalId")
	@Pattern(regexp = "^[0-9a-zA-Z]{1,8}$", message = TERMINAL_ID_MUST_MATCH_REGEXP_MSG)
	private String terminalId;

	/*
	 * grant_type
	 */
	@FormParam("grant_type")
	@NotNull(message = "[" + GRANT_TYPE_MUST_NOT_BE_NULL + "] grant_type must not be null")
	@Pattern(regexp = "^" + GrantType.PASSWORD + "|" + GrantType.REFRESH_TOKEN + "|" + GrantType.POYNT_TOKEN + "|" + GrantType.CLIENT_CREDENTIALS + "$", message = "[" + GRANT_TYPE_MUST_MATCH_REGEXP + "] grant_type must match \"{regexp}\"")
	private String grantType;

	/*
	 * username
	 */
	@FormParam("username")
	@Pattern(regexp = "^[ -~]{1,64}$", message = "[" + USERNAME_MUST_MATCH_REGEXP + "] username must match \"{regexp}\"")
	private String username;

	/*
	 * password
	 */
	@FormParam("password")
	@Pattern(regexp = "^[ -~]{1,64}$", message = "[" + PASSWORD_MUST_MATCH_REGEXP + "] password must match \"{regexp}\"")
	private String password;

	/*
	 * refresh_token
	 */
	@FormParam("refresh_token")
	@Pattern(regexp = "^[a-zA-Z0-9_-]{1,1024}\\.[a-zA-Z0-9_-]{1,1024}\\.[a-zA-Z0-9_-]{1,1024}$", message = "[" + REFRESH_TOKEN_MUST_MATCH_REGEXP + "] refresh_token must match \"{regexp}\"")
	private String refreshToken;

	/*
	 * poynt_token
	 */
	@FormParam("ext_token")
	@Pattern(regexp = "^[ -~]{1,4096}$", message = "[" + EXT_TOKEN_MUST_MATCH_REGEXP + "] ext_token must match \"{regexp}\"")
	private String extToken;

	/*
	 * add_data
	 */
	@FormParam("add_data")
	@Pattern(regexp = "^[ -~]{1,4096}$", message = "[" + ADD_DATA_MUST_MATCH_REGEXP + "] add_data must match \"{regexp}\"")
	private String addData;

	/*
	 * client_id
	 */
	@FormParam("client_id")
	@NotNull(message = "[" + CLIENT_ID_MUST_NOT_BE_NULL + "] client_id must not be null")
	@Pattern(regexp = "^[a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12}$", message = "[" + CLIENT_ID_MUST_MATCH_REGEXP + "] client_id must match \"{regexp}\"")
	private String clientId;

	/*
	 * scope
	 */
	@FormParam("scope")
	@Pattern(regexp = "^offline_access$", message = "[" + SCOPE_MUST_MATCH_REGEXP + "] scope must match \"{regexp}\"")
	private String scope;

	/*
	 * client_secret
	 */
	@FormParam("client_secret")
	@Pattern(regexp = "^[a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12}$", message = "[" + CLIENT_SECRET_MUST_MATCH_REGEXP + "] client_secret must match \"{regexp}\"")
	private String clientSecret;

	/**
	 * 
	 */
	public GetAccessToken() {
	}

	/**
	 * @return the requestId
	 */
	public String getRequestId() {
		return requestId;
	}

	/**
	 * @param requestId the requestId to set
	 */
	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	/**
	 * @return the version
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * @param version the version to set
	 */
	public void setVersion(String version) {
		this.version = version;
	}

	/**
	 * @return the acquirerId
	 */
	public String getAcquirerId() {
		return acquirerId;
	}

	/**
	 * @param acquirerId the acquirerId to set
	 */
	public void setAcquirerId(String acquirerId) {
		this.acquirerId = acquirerId;
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
	 * @return the terminalId
	 */
	public String getTerminalId() {
		return terminalId;
	}

	/**
	 * @param terminalId the terminalId to set
	 */
	public void setTerminalId(String terminalId) {
		this.terminalId = terminalId;
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
	 * @return the clientSecret
	 */
	public String getClientSecret() {
		return clientSecret;
	}

	/**
	 * @param clientSecret the clientSecret to set
	 */
	public void setClientSecret(String clientSecret) {
		this.clientSecret = clientSecret;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return new StringBuilder("GetAccessToken [requestId=").append(requestId)
			.append(", version=").append(version)
			.append(", acquirerId=").append(acquirerId)
			.append(", channel=").append(channel)
			.append(", merchantId=").append(merchantId)
			.append(", terminalId=").append(terminalId)
			.append(", clientId=").append(clientId)
			.append(", grantType=").append(grantType)
			.append(", client_secret=").append("***")
			.append(", username=").append("***")
			.append(", password=").append("***")
			.append(", extToken=").append(extToken)
			.append(", addData=").append(addData)
			.append(", refreshToken=").append(refreshToken)
			.append(", scope=").append(scope)
			.append("]")
			.toString();
	}
}