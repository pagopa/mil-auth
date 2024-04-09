/*
 * PagoPaConf.java
 * 
 * 02 apr 2024
 */
package it.pagopa.swclient.mil.auth.bean;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @author Antonio Tarricone
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PagoPaConf {
	/*
	 * 
	 */
	public static final String PSP_ID = "pspId";
	public static final String BROKER_ID = "brokerId";
	public static final String CHANNEL_ID = "channelId";
	
	/*
	 * 
	 */
	@JsonProperty(PSP_ID)
	private String pspId;
	
	/*
	 * 
	 */
	@JsonProperty(BROKER_ID)
	private String brokerId;
	
	/*
	 * 
	 */
	@JsonProperty(CHANNEL_ID)
	private String channelId;
}