/*
 * TokenService.java
 *
 * 17 mag 2023
 */
package it.pagopa.swclient.mil.auth.service;

import java.util.Date;
import java.util.List;
import java.util.Objects;

import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
import it.pagopa.swclient.mil.auth.bean.ClaimName;
import it.pagopa.swclient.mil.auth.bean.GetAccessTokenRequest;
import it.pagopa.swclient.mil.auth.bean.GetAccessTokenResponse;
import it.pagopa.swclient.mil.auth.bean.GrantType;
import it.pagopa.swclient.mil.auth.bean.Scope;
import it.pagopa.swclient.mil.pdv.bean.PersonalData;
import it.pagopa.swclient.mil.pdv.bean.Token;
import it.pagopa.swclient.mil.pdv.client.Tokenizer;

/**
 * This class generates access token string and refresh token string if any and signs them.
 *
 * @author Antonio Tarricone
 */
public abstract class TokenService {
	/*
	 * Access token duration.
	 */
	private long accessDuration;

	/*
	 * Duration of refresh tokens in seconds.
	 */
	private long refreshDuration;

	/*
	 * mil-auth base URL.
	 */
	private String baseUrl;

	/*
	 * Token audience.
	 */
	private String audience;

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
	private Tokenizer tokenizer;

	/*
	 * 
	 */
	private boolean protectFiscalCode;

	/**
	 * 
	 */
	TokenService() {
	}

	/**
	 * 
	 * @param accessDuration
	 * @param refreshDuration
	 * @param baseUrl
	 * @param audience
	 * @param clientVerifier
	 * @param roleFinder
	 * @param tokenSigner
	 * @param tokenizer
	 * @param protectFiscalCode
	 */
	TokenService(
		long accessDuration,
		long refreshDuration,
		String baseUrl,
		String audience,
		ClientVerifier clientVerifier,
		RolesFinder roleFinder,
		TokenSigner tokenSigner,
		Tokenizer tokenizer,
		boolean protectFiscalCode) {
		this.accessDuration = accessDuration;
		this.refreshDuration = refreshDuration;
		this.baseUrl = baseUrl;
		this.audience = audience;
		this.clientVerifier = clientVerifier;
		this.roleFinder = roleFinder;
		this.tokenSigner = tokenSigner;
		this.tokenizer = tokenizer;
		this.protectFiscalCode = protectFiscalCode;
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
	 * @param roles
	 * @param scopes
	 * @return
	 */
	private Uni<String> generate(GetAccessTokenRequest request, long duration, List<String> roles, List<String> scopes) {
		String fiscalCode = request.getFiscalCode();
		if (fiscalCode == null) {
			return generate(request, duration, roles, scopes, null);
		} else {
			if (protectFiscalCode) {
				return tokenizer.tokenize(new PersonalData(fiscalCode))
					.map(Token::getValue)
					.chain(fiscalCodeToken -> generate(request, duration, roles, scopes, fiscalCodeToken));
			} else {
				return generate(request, duration, roles, scopes, fiscalCode);
			}
		}
	}

	/**
	 * 
	 * @param request
	 * @param duration
	 * @param roles
	 * @param scopes
	 * @param fiscalCodeToken
	 * @return
	 */
	private Uni<String> generate(GetAccessTokenRequest request, long duration, List<String> roles, List<String> scopes, String fiscalCodeToken) {
		Date now = new Date();
		JWTClaimsSet payload = new JWTClaimsSet.Builder()
			.subject(request.getClientId())
			.issueTime(now)
			.expirationTime(new Date(now.getTime() + duration * 1000))
			.claim(ClaimName.ACQUIRER_ID, request.getAcquirerId())
			.claim(ClaimName.CHANNEL, request.getChannel())
			.claim(ClaimName.MERCHANT_ID, request.getMerchantId())
			.claim(ClaimName.CLIENT_ID, request.getClientId())
			.claim(ClaimName.TERMINAL_ID, request.getTerminalId())
			.claim(ClaimName.SCOPE, concat(scopes))
			.claim(ClaimName.GROUPS, roles)
			.claim(ClaimName.FISCAL_CODE, fiscalCodeToken)
			.issuer(baseUrl)
			.audience(audience)
			.build();
		Log.debug("Token signing.");
		return tokenSigner.sign(payload).map(SignedJWT::serialize);
	}

	/**
	 * This method generates access token string and refresh token string if any and signs them.
	 *
	 * @param request
	 * @param roles
	 * @return
	 */
	private Uni<GetAccessTokenResponse> generateToken(GetAccessTokenRequest request, List<String> roles) {
		Log.debug("Access token generation.");
		if (Objects.equals(request.getScope(), Scope.OFFLINE_ACCESS) || request.getGrantType().equals(GrantType.REFRESH_TOKEN)) {
			/*
			 * With refresh token.
			 */
			return generate(request, accessDuration, roles, null)
				.chain(accessToken -> {
					Log.debug("Refresh token generation.");
					return generate(request, refreshDuration, null, List.of(Scope.OFFLINE_ACCESS))
						.map(refreshToken -> new GetAccessTokenResponse(accessToken, refreshToken, accessDuration));
				});
		} else {
			/*
			 * Without refresh token.
			 */
			return generate(request, accessDuration, roles, null)
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
		return clientVerifier.verify(request.getClientId(), request.getChannel(), request.getClientSecret())
			.chain(() -> roleFinder.findRoles(request.getAcquirerId(), request.getChannel(), request.getClientId(), request.getMerchantId(), request.getTerminalId()))
			.chain(roleEntity -> generateToken(request, roleEntity.getRoles()));
	}
}