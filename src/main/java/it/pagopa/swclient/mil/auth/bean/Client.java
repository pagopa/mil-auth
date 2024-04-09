/*
 * Client.java
 *
 * 16 mar 2023
 */
package it.pagopa.swclient.mil.auth.bean;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 
 * @author Antonio Tarricone
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Client {
	/*
	 * 
	 */
	public static final String ID = "id";
	public static final String CHANNEL = "channel";
	public static final String GRANT_TYPES = "grantTypes";
	public static final String SUB = "sub";
	public static final String SALT = "salt";
	public static final String SECRET_HASH = "secretHash";
	public static final String SECRET_EXP = "secretExp";

	/*
	 *
	 */
	@JsonProperty(value = ID, required = true)
	private String id;

	/*
	 *
	 */
	@JsonProperty(value = CHANNEL, required = true)
	private Channel channel;

	/*
	 * 
	 */
	@JsonProperty(value = GRANT_TYPES, required = true)
	private List<GrantType> grantTypes;

	/*
	 * 
	 */
	@JsonProperty(value = SUB)
	private String sub;

	/*
	 *
	 */
	@JsonProperty(value = SALT)
	@ToString.Exclude
	private String salt;

	/*
	 *
	 */
	@JsonProperty(value = SECRET_HASH)
	@ToString.Exclude
	private String secretHash;

	/*
	 *
	 */
	@JsonProperty(value = SECRET_EXP, required = true)
	private long secretExp;
}