/*
 * AtmAccessTokenGenerator.java
 *
 * 9 apr 2024
 */
package it.pagopa.swclient.mil.auth.service;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
import it.pagopa.swclient.mil.auth.bean.AccessTokenRequest;
import it.pagopa.swclient.mil.auth.bean.ClaimName;
import it.pagopa.swclient.mil.auth.bean.Client;
import it.pagopa.swclient.mil.auth.bean.Roles;
import it.pagopa.swclient.mil.auth.bean.TokenizationRequest;
import it.pagopa.swclient.mil.auth.bean.TokenizationResponse;
import it.pagopa.swclient.mil.auth.client.PersonalDataVaultClient;
import it.pagopa.swclient.mil.auth.qualifier.channel.Atm;
import it.pagopa.swclient.mil.auth.service.crypto.TokenSigner;
import it.pagopa.swclient.mil.auth.util.UniGenerator;
import jakarta.inject.Inject;

/**
 * @see it.pagopa.swclient.mil.auth.service.AccessTokenGenerator
 * @author Antonio Tarricone
 */
@Atm
public class AtmAccessTokenGenerator extends AccessTokenGenerator {
	/*
	 * 
	 */
	private PersonalDataVaultClient personalDataVaultClient;

	/**
	 * Constructor.
	 * 
	 * @param duration
	 * @param baseUrl
	 * @param audience
	 * @param personalDataVaultClient
	 * @param tokenSigner
	 */
	@Inject
	AtmAccessTokenGenerator(
		@ConfigProperty(name = "client_credentials_grant.access_token.duration") long duration,
		@ConfigProperty(name = "base-url") String baseUrl,
		@ConfigProperty(name = "audience") String audience,
		@RestClient PersonalDataVaultClient personalDataVaultClient,
		TokenSigner tokenSigner) {
		super(duration, baseUrl, audience, tokenSigner);
		this.personalDataVaultClient = personalDataVaultClient;
	}

	/**
	 * 
	 * @param accessTokenRequest
	 * @return
	 */
	private Uni<String> tokenizeUserTaxCode(AccessTokenRequest accessTokenRequest) {
		String userTaxCode = accessTokenRequest.getUserCode();
		if (userTaxCode == null) {
			Log.debug("User tax code not present");
			return UniGenerator.item((String) null);
		} else {
			Log.debug("Protection of user tax code");
			return personalDataVaultClient.tokenize(new TokenizationRequest(userTaxCode))
				.map(TokenizationResponse::getToken);
		}
	}

	/**
	 * 
	 * @param client
	 * @param roles
	 * @param accessTokenRequest
	 * @param userTaxCodeToken
	 * @return
	 */
	private Uni<String> generate(Client client, Roles roles, AccessTokenRequest accessTokenRequest, String userTaxCodeToken) {
		String bankId = accessTokenRequest.getBankId();
		String terminalId = accessTokenRequest.getTerminalId();

		return signPayload(prepareJwtClaimSetBuilder(client, roles)
			.subject(bankId + terminalId)
			.claim(ClaimName.BANK_ID, bankId)
			.claim(ClaimName.TERMINAL_ID, terminalId)
			.claim(ClaimName.USER_CODE_TOKEN, userTaxCodeToken)
			.build());
	}

	/**
	 * @see it.pagopa.swclient.mil.auth.service.AccessTokenGenerator#generate(Client, Roles,
	 *      AccessTokenRequest)
	 */
	@Override
	public Uni<String> generate(Client client, Roles roles, AccessTokenRequest accessTokenRequest) {
		return tokenizeUserTaxCode(accessTokenRequest)
			.chain(userTaxCodeToken -> generate(client, roles, accessTokenRequest, userTaxCodeToken));
	}
}
