/*
 * ClientCredentialsGrantFlow.java
 *
 * 2 apr 2024
 */
package it.pagopa.swclient.mil.auth.service;

import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Date;

import javax.security.sasl.AuthenticationException;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import io.quarkus.logging.Log;
import io.smallrye.mutiny.Context;
import io.smallrye.mutiny.Uni;
import it.pagopa.swclient.mil.auth.bean.AccessTokenRequest;
import it.pagopa.swclient.mil.auth.bean.AuthErrorCode;
import it.pagopa.swclient.mil.auth.bean.Channel;
import it.pagopa.swclient.mil.auth.bean.ClaimName;
import it.pagopa.swclient.mil.auth.bean.Client;
import it.pagopa.swclient.mil.auth.bean.GrantType;
import it.pagopa.swclient.mil.auth.bean.Roles;
import it.pagopa.swclient.mil.auth.qualifier.grant.ClientCredentials;
import it.pagopa.swclient.mil.auth.service.crypto.TokenSigner;
import it.pagopa.swclient.mil.auth.service.role.RolesFinder;
import it.pagopa.swclient.mil.auth.util.AuthError;
import it.pagopa.swclient.mil.auth.util.AuthException;
import it.pagopa.swclient.mil.auth.util.PasswordVerifier;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 * 
 * @author Antonio Tarricone
 */
@ClientCredentials
@ApplicationScoped
public class ClientCredentialsGrantFlow {
	/*
	 * 
	 */
	private ClientRepositoryService clientRepository;

	/*
	 * 
	 */
	private RolesFinder rolesFinder;

	/*
	 * 
	 */
	private TokenSigner tokenSigner;

	/*
	 * Access token duration.
	 */
	private long duration;

	/*
	 * mil-auth base URL.
	 */
	private String baseUrl;

	/*
	 * Token audience.
	 */
	private String audience;

	/**
	 * Constructor.
	 * 
	 * @param duration
	 * @param baseUrl
	 * @param audience
	 * @param clientRepository
	 * @param rolesFinder
	 * @param tokenSigner
	 */
	@Inject
	ClientCredentialsGrantFlow(
		@ConfigProperty(name = "client_credentials_grant.access_token.duration") long duration,
		@ConfigProperty(name = "base-url") String baseUrl,
		@ConfigProperty(name = "audience") String audience,
		ClientRepositoryService clientRepository,
		RolesFinder rolesFinder,
		TokenSigner tokenSigner) {
		this.duration = duration;
		this.baseUrl = baseUrl;
		this.audience = audience;
		this.clientRepository = clientRepository;
		this.rolesFinder = rolesFinder;
		this.tokenSigner = tokenSigner;
	}

	/**
	 * 
	 * @param client
	 * @return
	 */
	private Client verifyExpiration(Client client) {
		if (client.getSecretExp() > Instant.now().getEpochSecond()) {
			Log.debug("Client not expired");
			return client;
		} else {
			Log.warn(AuthErrorCode.CLIENT_EXPIRED_MSG);
			throw new AuthException(AuthErrorCode.CLIENT_EXPIRED, AuthErrorCode.CLIENT_EXPIRED_MSG);
		}
	}

	/**
	 * 
	 * @param client
	 * @return
	 */
	private Client verifyGrantType(Client client) {
		if (client.getGrantTypes().contains(GrantType.CLIENT_CREDENTIALS)) {
			Log.debug("Grant type matches");
			return client;
		} else {
			Log.warn(AuthErrorCode.WRONG_GRANT_TYPE_MSG);
			throw new AuthException(AuthErrorCode.WRONG_GRANT_TYPE, AuthErrorCode.WRONG_GRANT_TYPE_MSG);
		}
	}

	/**
	 * 
	 * @param client
	 * @param secret
	 * @return
	 */
	private Client verifySecret(Client client, String secret) {
		try {
			if (PasswordVerifier.verify(secret, client.getSalt(), client.getSecretHash())) {
				Log.debug("Secret matches");
				return client;
			} else {
				Log.warn(AuthErrorCode.WRONG_CREDENTIALS_MSG);
				throw new AuthException(AuthErrorCode.WRONG_CREDENTIALS, AuthErrorCode.WRONG_CREDENTIALS_MSG);
			}
		} catch (NoSuchAlgorithmException e) {
			Log.errorf(e, AuthErrorCode.ERROR_VERIFING_SECRET);
			throw new AuthError(AuthErrorCode.ERROR_VERIFING_SECRET, AuthErrorCode.ERROR_VERIFING_SECRET_MSG);
		}
	}

	/**
	 * 
	 * @param clientId
	 * @param secret
	 * @return
	 */
	private Uni<Client> verifyClient(String clientId, String secret) {
		return clientRepository.getClient(clientId)
			.map(this::verifyExpiration)
			.map(this::verifyGrantType)
			.map(client -> verifySecret(client, secret));
	}

	/**
	 * 
	 * @param client
	 * @param accessTokenRequest
	 */
	private void verifyRequestConsistentWithChannel(Client client, AccessTokenRequest accessTokenRequest) {
		if (client.getChannel().equals(Channel.ATM)) {
			if (accessTokenRequest.getBankId() != null && accessTokenRequest.getTerminalId() != null) {
				Log.debug("Request is consistent with channel");
			} else {
				Log.warn(AuthErrorCode.REQUEST_INCONSISTENT_WITH_CHANNEL_MSG);
				throw new AuthException(AuthErrorCode.REQUEST_INCONSISTENT_WITH_CHANNEL, AuthErrorCode.REQUEST_INCONSISTENT_WITH_CHANNEL_MSG);
			}
		} else {
			if (accessTokenRequest.getBankId() == null && accessTokenRequest.getTerminalId() == null) {
				Log.debug("Request is consistent with channel");
			} else {
				Log.warn(AuthErrorCode.REQUEST_INCONSISTENT_WITH_CHANNEL_MSG);
				throw new AuthException(AuthErrorCode.REQUEST_INCONSISTENT_WITH_CHANNEL, AuthErrorCode.REQUEST_INCONSISTENT_WITH_CHANNEL_MSG);
			}
		}
	}

	private Uni<String> generateAccessToken(Client client, Roles roles, AccessTokenRequest accessTokenRequest) {
		Date now = new Date();
		Channel channel = client.getChannel();
		
		JWTClaimsSet.Builder builder = new JWTClaimsSet.Builder()
			.issueTime(now)
			.issuer(baseUrl)
			.audience(audience)
			.expirationTime(new Date(now.getTime() + duration * 1000))
			.claim(ClaimName.CHANNEL, client.getChannel())
			.claim(ClaimName.GROUPS, roles.getListOfRoles());
			
		String subject = null;	
		if (channel.equals(Channel.ATM)) {
				String bankId = accessTokenRequest.getBankId();
				String terminalId = accessTokenRequest.getTerminalId();
				subject = bankId+terminalId;
				builder.claim(ClaimName.BANK_ID, bankId);
				builder.claim(ClaimName.TERMINAL_ID, terminalId);
				// TODO: builder.claim(ClaimName.USER_CODE_TOKEN, userCodeToken);
			} else {
				subject = client.getSub();
			}
		builder.subject(subject);
			
		JWTClaimsSet payload= builder.build();
		Log.debug("Token signing.");
		return tokenSigner.sign(payload).map(SignedJWT::serialize);
	}

	public void process(AccessTokenRequest accessTokenRequest) {
		AccessTokenBundle bundle = new AccessTokenBundle()
			.setAccessTokenRequest(accessTokenRequest);
		
		
		retrieveClient()
			.map(this::verifyClientExpiration)
			.map(this::verifyRequestConsistency)
			.map(this::verifySecret)
			.map(this::retrieveRoles)
			.map(this::generateTokenPayload)
			.map(this::retrieveKey)
			.map(this::signToken)
			.map(AccessTokenBundle::getAccessTokenResponse);
			
		
		Context ctx = Context.of();
		verifyClient(accessTokenRequest.getClientId(), accessTokenRequest.getClientSecret())
			.invoke(client -> ctx.put("client", client))
			.chain(client -> rolesFinder.getRoles(client, accessTokenRequest));

	}

}
