/*
 * Terminal.java
 * 
 * 02 apr 2024
 */
package it.pagopa.swclient.mil.auth.bean;

import java.util.List;

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
public class Terminal {
	/*
	 * 
	 */
	private static final String TERMINAL_UUID = "terminalUuid";
	private static final String SERVICE_PROVIDER_ID = "serviceProviderId";
	private static final String TERMINAL_HANDLER_ID = "terminalHandlerId";
	private static final String TERMINAL_ID = "terminalId";
	private static final String ENABLED = "enabled";
	private static final String PAYEE_CODE = "payeeCode";
	private static final String SLAVE = "slave";
	private static final String WORKSTATIONS = "workstations";
	private static final String PAGOPA = "pagoPa";
	private static final String PAGOPA_CONF = "pagoPaConf";
	private static final String IDPAY = "idpay";

	/*
	 * 
	 */
	@JsonProperty(TERMINAL_UUID)
	private String terminalUuid;

	/*
	 * 
	 */
	@JsonProperty(SERVICE_PROVIDER_ID)
	private String serviceProviderId;

	/*
	 * 
	 */
	@JsonProperty(TERMINAL_HANDLER_ID)
	private String terminalHandlerId;

	/*
	 * 
	 */
	@JsonProperty(TERMINAL_ID)
	private String terminalId;

	/*
	 * 
	 */
	@JsonProperty(ENABLED)
	private boolean enabled;

	/*
	 * 
	 */
	@JsonProperty(PAYEE_CODE)
	private String payeeCode;

	/*
	 * 
	 */
	@JsonProperty(SLAVE)
	private boolean slave;

	/*
	 * 
	 */
	@JsonProperty(WORKSTATIONS)
	private List<String> workstations;

	/*
	 * 
	 */
	@JsonProperty(PAGOPA)
	private boolean pagoPa;

	/*
	 * 
	 */
	@JsonProperty(PAGOPA_CONF)
	private PagoPaConf pagoPaConf;

	/*
	 * 
	 */
	@JsonProperty(IDPAY)
	private boolean idpay;
}