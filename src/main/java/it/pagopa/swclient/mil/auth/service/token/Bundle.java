/*
 * Bundle.java
 *
 * 9 apr 2024
 */
package it.pagopa.swclient.mil.auth.service.token;

import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import it.pagopa.swclient.mil.auth.bean.AccessTokenRequest;
import it.pagopa.swclient.mil.auth.bean.AccessTokenResponse;
import it.pagopa.swclient.mil.auth.bean.Client;
import it.pagopa.swclient.mil.auth.bean.PublicKey;
import it.pagopa.swclient.mil.auth.bean.Roles;
import it.pagopa.swclient.mil.auth.bean.Terminal;
import it.pagopa.swclient.mil.auth.bean.TokenizationResponse;
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
public class Bundle {
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

	/*
	 * 
	 */
	private PublicKey key;

	/*
	 * 
	 */
	private TokenizationResponse tokenizationResponse;

	/*
	 * 
	 */
	private Terminal terminal;

	/*
	 * 
	 */
	private JWTClaimsSet accessTokenPayload;

	/*
	 * 
	 */
	private JWTClaimsSet refreshTokenPayload;

	/*
	 * 
	 */
	private SignedJWT signedAccessToken;

	/*
	 * 
	 */
	private SignedJWT signedRefreshToken;
}