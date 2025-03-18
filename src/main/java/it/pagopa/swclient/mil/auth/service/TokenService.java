/*
 * TokenService.java
 *
 * 17 mag 2023
 */
package it.pagopa.swclient.mil.auth.service;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import io.quarkus.logging.Log;
import io.smallrye.mutiny.Context;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.unchecked.Unchecked;
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
	 * Decides whether the access token should contain the encrypted fiscal code (step 2).
	 * 
	 * @param request
	 * @param client
	 * @param roles
	 * @return
	 */
	private Uni<SignedJWT> generateAccessToken(GetAccessTokenRequest request, ClientEntity client, List<String> roles) {
		String fiscalCode = request.getFiscalCode();
		if (fiscalCode == null) {
			Log.trace("Fiscal code not present");
			return generateAccessToken(request, client, roles, null);
		} else {
			Log.trace("Fiscal code present");
			return claimEncryptor.encrypt(fiscalCode)
				.chain(encFiscalCode -> generateAccessToken(request, client, roles, encFiscalCode));
		}
	}

	/**
	 * Decides whether to generate the refresh token (step 1).
	 *
	 * @param request
	 * @param client
	 * @param roles
	 * @return
	 */
	private Uni<GetAccessTokenResponse> generateTokens(GetAccessTokenRequest request, ClientEntity client, List<String> roles) {
		if (Objects.equals(request.getScope(), Scope.OFFLINE_ACCESS) || request.getGrantType().equals(GrantType.REFRESH_TOKEN)) {
			/*
			 * With refresh token.
			 */
			Log.debug("Refresh token requested");
			return generateAccessToken(request, client, roles)
				.chain(accessToken -> generateRefreshToken(request, client)
					.map(refreshToken -> new GetAccessTokenResponse(accessToken, refreshToken, accessDuration)));
		} else {
			/*
			 * Without refresh token.
			 */
			Log.debug("Refresh token not requested");
			return generateAccessToken(request, client, roles)
				.map(accessToken -> new GetAccessTokenResponse(accessToken, null, accessDuration));
		}
	}

	/**
	 * Generates the access token (step 3).
	 * 
	 * @param request
	 * @param client
	 * @param roles
	 * @param encFiscalCode
	 * @return
	 */
	private Uni<SignedJWT> generateAccessToken(GetAccessTokenRequest request, ClientEntity client, List<String> roles, EncryptedClaim encFiscalCode) {
		Log.tracef("Access token generation");
		Log.tracef("Encrypted fiscal code: %s", encFiscalCode);
		Date now = new Date();
		JWTClaimsSet payload = new JWTClaimsSet.Builder()
			.subject(subject(request, client))
			.issueTime(now)
			.expirationTime(new Date(now.getTime() + accessDuration * 1000))
			.jwtID(UUID.randomUUID().toString())
			.claim(ClaimName.ACQUIRER_ID, request.getAcquirerId())
			.claim(ClaimName.CHANNEL, request.getChannel())
			.claim(ClaimName.MERCHANT_ID, request.getMerchantId())
			.claim(ClaimName.CLIENT_ID, request.getClientId())
			.claim(ClaimName.TERMINAL_ID, request.getTerminalId())
			.claim(ClaimName.GROUPS, roles)
			.claim(ClaimName.FISCAL_CODE, encFiscalCode != null ? encFiscalCode.toMap() : null)
			.issuer(baseUrl)
			.audience(audience)
			.build();
		Log.trace("Token signing");
		return tokenSigner.sign(payload);
	}

	/**
	 * Generates the refresh token (step 4).
	 * 
	 * @param request
	 * @param client
	 * @return
	 */
	private Uni<SignedJWT> generateRefreshToken(GetAccessTokenRequest request, ClientEntity client) {
		/*
		 * The following code block wrapper with Unchecked, cannot thrown ParseExeception here, because this
		 * exception would be thrown earlier!
		 */
		return Unchecked.supplier(() -> {
			Log.trace("Refresh token generation");
			String generationId = null;
			boolean returnTheRefreshTokenInTheCookie = false;
			if (request.getGrantType().equals(GrantType.REFRESH_TOKEN)) {
				JWTClaimsSet currentPayload = request.getTheRefreshToken().getJWTClaimsSet();
				generationId = currentPayload.getStringClaim(ClaimName.GENERATION_ID);
				Boolean returnTheRefreshTokenInTheCookieObj = currentPayload.getBooleanClaim(ClaimName.RETURNED_IN_THE_COOKIE);
				returnTheRefreshTokenInTheCookie = returnTheRefreshTokenInTheCookieObj != null ? returnTheRefreshTokenInTheCookieObj.booleanValue() : false;
			} else {
				generationId = UUID.randomUUID().toString();
				returnTheRefreshTokenInTheCookie = request.isReturnTheRefreshTokenInTheCookie();
			}
			Date now = new Date();
			JWTClaimsSet payload = new JWTClaimsSet.Builder()
				.subject(subject(request, client))
				.issueTime(now)
				.expirationTime(new Date(now.getTime() + refreshDuration * 1000))
				.jwtID(UUID.randomUUID().toString())
				.claim(ClaimName.GENERATION_ID, generationId)
				.claim(ClaimName.ACQUIRER_ID, request.getAcquirerId())
				.claim(ClaimName.CHANNEL, request.getChannel())
				.claim(ClaimName.MERCHANT_ID, request.getMerchantId())
				.claim(ClaimName.CLIENT_ID, request.getClientId())
				.claim(ClaimName.TERMINAL_ID, request.getTerminalId())
				.claim(ClaimName.SCOPE, Scope.OFFLINE_ACCESS)
				.claim(ClaimName.RETURNED_IN_THE_COOKIE, returnTheRefreshTokenInTheCookie)
				.issuer(baseUrl)
				.audience(audience)
				.build();
			Log.trace("Refresh token signing");
			return tokenSigner.sign(payload);
		}).get();
	}

	/**
	 * This method contains all common logic behind the access token generation (step 0).
	 *
	 * @param request
	 * @return
	 */
	public Uni<GetAccessTokenResponse> process(GetAccessTokenRequest request) {
		Context ctx = Context.of();
		return clientVerifier.verify(request.getClientId(), request.getChannel(), request.getClientSecret())
			.invoke(client -> ctx.put(CLIENT_CTX_KEY, client))
			.chain(() -> roleFinder.findRoles(request.getAcquirerId(), request.getChannel(), request.getClientId(), request.getMerchantId(), request.getTerminalId()))
			.chain(roleEntity -> generateTokens(request, ctx.get(CLIENT_CTX_KEY), roleEntity.getRoles()));
	}
}