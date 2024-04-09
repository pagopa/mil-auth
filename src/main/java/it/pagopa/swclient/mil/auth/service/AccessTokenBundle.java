/*
 * AccessTokenBundle.java
 *
 * 9 apr 2024
 */
package it.pagopa.swclient.mil.auth.service;

import com.nimbusds.oauth2.sdk.AccessTokenResponse;

import it.pagopa.swclient.mil.auth.bean.AccessTokenRequest;
import it.pagopa.swclient.mil.auth.bean.Client;
import it.pagopa.swclient.mil.auth.bean.Roles;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * 
 * @author Antonio Tarricone
 */
@NoArgsConstructor
@Getter
@Setter
@Accessors(chain = true)
public class AccessTokenBundle {
	/*
	 * 
	 */
	private AccessTokenRequest accessTokenRequest;
	
	/*
	 * 
	 */
	private AccessTokenResponse accessTokenResponse;
	
	/*
	 * 
	 */
	private Client client;
	
	/*
	 * 
	 */
	private Roles roles;
}