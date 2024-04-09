/*
 * DeviceCodeResponse.java
 * 
 * 02 apr 2024
 */
package it.pagopa.swclient.mil.auth.bean;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response to the request to get device code and user code.
 * 
 * @author Antonio Tarricone
 */
@RegisterForReflection
@JsonInclude(Include.NON_NULL)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeviceCodeResponse {
	/*
	 * 
	 */
	public static final String DEVICE_CODE = "device_code";
	public static final String USER_CODE = "user_code";
	public static final String EXPIRES_IN = "expires_in";
	public static final String INTERVAL = "interval";

	/*
	 * Device code.
	 */
	@JsonProperty(DEVICE_CODE)
	private String deviceCode;

	/*
	 * Base20 user code to authenticate the terminal.
	 */
	@JsonProperty(USER_CODE)
	private String userCode;

	/*
	 * The lifetime in seconds of the user code and device code.
	 */
	@JsonProperty(EXPIRES_IN)
	private long expiresIn;

	/*
	 * The minimum amount of time in seconds that the client should wait between polling requests to the
	 * token endpoint.
	 */
	@JsonProperty(INTERVAL)
	private long interval;
}