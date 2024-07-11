/*
 * TokenService.java
 *
 * 17 mag 2023
 */
package it.pagopa.swclient.mil.auth.service;

import java.util.Date;
import java.util.List;
import java.util.Objects;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import io.quarkus.logging.Log;
import io.smallrye.mutiny.Context;
import io.smallrye.mutiny.Uni;
import it.pagopa.swclient.mil.auth.bean.ClaimName;
import it.pagopa.swclient.mil.auth.bean.EncryptedClaim;
import it.pagopa.swclient.mil.auth.bean.GetAccessTokenRequest;
import it.pagopa.swclient.mil.auth.bean.GetAccessTokenResponse;
import it.pagopa.swclient.mil.auth.bean.GrantType;
import it.pagopa.swclient.mil.auth.bean.Scope;
import it.pagopa.swclient.mil.auth.dao.ClientEntity;
import it.pagopa.swclient.mil.bean.Channel;

/**
 * This class generates access token string and refresh token string if any and signs them.
 *
 * @author Antonio Tarricone
 */
public abstract class TokenService {
	/*
	 * Access token duration.
	 */
	@ConfigProperty(name = "access.duration")
	long accessDuration;

	/*
	 * Duration of refresh tokens in seconds.
	 */
	@ConfigProperty(name = "refresh.duration")
	long refreshDuration;

	/*
	 * mil-auth base URL.
	 */
	@ConfigProperty(name = "base-url", defaultValue = "")
	String baseUrl;

	/*
	 * Token audience.
	 */
	@ConfigProperty(name = "token-audience", defaultValue = "mil.pagopa.it")
	String audience;

	/*
	 *
	 */
	private ClientVerifier clientVerifier;

	/*
	 *
	 */
	private RolesFinder roleFinder;

	/*
	 *
	 */
	protected TokenSigner tokenSigner;

	/*
	 * 
	 */
	private ClaimEncryptor claimEncryptor;

	/*
	 * 
	 */
	private static final String CLIENT_CTX_KEY = "client";

	/**
	 * 
	 */
	TokenService() {
	}

	/**
	 * 
	 * @param clientVerifier
	 * @param roleFinder
	 * @param tokenSigner
	 * @param claimEncryptor
	 */
	TokenService(ClientVerifier clientVerifier, RolesFinder roleFinder, TokenSigner tokenSigner, ClaimEncryptor claimEncryptor) {
		this.clientVerifier = clientVerifier;
		this.roleFinder = roleFinder;
		this.tokenSigner = tokenSigner;
		this.claimEncryptor = claimEncryptor;
	}

	/**
	 * @param strings
	 * @return
	 */
	private String concat(List<String> strings) {
		if (strings == null) {
			return null;
		}
		return String.join(" ", strings);
	}

	/**
	 * 
	 * @param request
	 * @param duration
	 * @param client
	 * @param roles
	 * @param scopes
	 * @return
	 */
	private Uni<String> generate(GetAccessTokenRequest request, long duration, ClientEntity client, List<String> roles, List<String> scopes) {
		String fiscalCode = request.getFiscalCode();
		if (fiscalCode == null) {
			Log.trace("Fiscal code not present");
			return generate(request, duration, client, roles, scopes, null);
		} else {
			Log.trace("Fiscal code present");
			return claimEncryptor.encrypt(fiscalCode)
				.chain(encFiscalCode -> generate(request, duration, client, roles, scopes, encFiscalCode));
		}
	}

	/**
	 * 
	 * @param request
	 * @param client
	 * @return
	 */
	private String subject(GetAccessTokenRequest request, ClientEntity client) {
		String subject = null;
		String channel = request.getChannel();
		if (channel != null) {
			if (channel.equals(Channel.ATM)) {
				subject = request.getAcquirerId() + "/" + request.getTerminalId();
			} else if (channel.equals(Channel.POS)) {
				subject = request.getAcquirerId() + "/" + request.getMerchantId() + "/" + request.getTerminalId();
			}
		}
		if (subject == null) {
			subject = client.getSubject();
			if (subject == null) {
				subject = request.getClientId();
			}
		}
		return subject;
	}

	/**
	 * 
	 * @param request
	 * @param duration
	 * @param client
	 * @param roles
	 * @param scopes
	 * @param encFiscalCode
	 * @return
	 */
	private Uni<String> generate(GetAccessTokenRequest request, long duration, ClientEntity client, List<String> roles, List<String> scopes, EncryptedClaim encFiscalCode) {
		Log.tracef("Encrypted fiscal code: %s", encFiscalCode);
		Date now = new Date();
		JWTClaimsSet payload = new JWTClaimsSet.Builder()
			.subject(subject(request, client))
			.issueTime(now)
			.expirationTime(new Date(now.getTime() + duration * 1000))
			.claim(ClaimName.ACQUIRER_ID, request.getAcquirerId())
			.claim(ClaimName.CHANNEL, request.getChannel())
			.claim(ClaimName.MERCHANT_ID, request.getMerchantId())
			.claim(ClaimName.CLIENT_ID, request.getClientId())
			.claim(ClaimName.TERMINAL_ID, request.getTerminalId())
			.claim(ClaimName.SCOPE, concat(scopes))
			.claim(ClaimName.GROUPS, roles)
			.claim(ClaimName.FISCAL_CODE, encFiscalCode != null ? encFiscalCode.toMap() : null)
			.issuer(baseUrl)
			.audience(audience)
			.build();
		Log.trace("Token signing");
		return tokenSigner.sign(payload).map(SignedJWT::serialize);
	}

	/**
	 * This method generates access token string and refresh token string if any and signs them.
	 *
	 * @param request
	 * @param client
	 * @param roles
	 * @return
	 */
	private Uni<GetAccessTokenResponse> generateToken(GetAccessTokenRequest request, ClientEntity client, List<String> roles) {
		Log.trace("Access token generation");
		if (Objects.equals(request.getScope(), Scope.OFFLINE_ACCESS) || request.getGrantType().equals(GrantType.REFRESH_TOKEN)) {
			/*
			 * With refresh token.
			 */
			return generate(request, accessDuration, client, roles, null)
				.chain(accessToken -> {
					Log.trace("Refresh token generation");
					return generate(request, refreshDuration, client, null, List.of(Scope.OFFLINE_ACCESS))
						.map(refreshToken -> new GetAccessTokenResponse(accessToken, refreshToken, accessDuration));
				});
		} else {
			/*
			 * Without refresh token.
			 */
			return generate(request, accessDuration, client, roles, null)
				.map(accessToken -> new GetAccessTokenResponse(accessToken, null, accessDuration));
		}
	}

	/**
	 * This method contains all common logic behind the access token generation.
	 *
	 * @param request
	 * @return
	 */
	public Uni<GetAccessTokenResponse> process(GetAccessTokenRequest request) {
		Context ctx = Context.of();
		return clientVerifier.verify(request.getClientId(), request.getChannel(), request.getClientSecret())
			.invoke(client -> ctx.put(CLIENT_CTX_KEY, client))
			.chain(() -> roleFinder.findRoles(request.getAcquirerId(), request.getChannel(), request.getClientId(), request.getMerchantId(), request.getTerminalId()))
			.chain(roleEntity -> generateToken(request, ctx.get(CLIENT_CTX_KEY), roleEntity.getRoles()));
	}
}