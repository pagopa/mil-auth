/*
 * Role.java
 *
 * 4 apr 2024
 */
package it.pagopa.swclient.mil.auth.bean;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 
 * @author Antonio Tarricone
 */
public enum Role {
	@JsonProperty("PayWithIDPay")
	PAY_WITH_IDPAY,

	@JsonProperty("NoticePayer")
	NOTICE_PAYER,

	@JsonProperty("SlavePos")
	SLAVE_POS,

	@JsonProperty("EnrollToIDPay")
	ENROLL_TO_IDPAY,

	@JsonProperty("pos_service_provider")
	POS_SERVICE_PROVIDER,

	@JsonProperty("public_administration")
	PUBLIC_ADMINISTRATION,

	@JsonProperty("pos_finder")
	POS_FINDER,

	@JsonProperty("atm_access_token_introspector")
	ATM_ACCESS_TOKEN_INTROSPECTOR
}
