package it.gov.pagopa.swclient.mil.idp.service;

import java.util.Date;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import io.quarkus.logging.Log;
import it.gov.pagopa.swclient.mil.bean.CommonHeader;
import it.gov.pagopa.swclient.mil.idp.bean.GetAccessToken;

@ApplicationScoped
public class RefreshTokenStringGenerator {
	@ConfigProperty(name = "issuer")
	private String issuer;;

	/*
	 * 
	 */
	@ConfigProperty(name = "refresh.audience")
	private List<String> refreshAudience;

	/*
	 * Duration of refresh tokens in seconds.
	 */
	@ConfigProperty(name = "refresh.duration")
	private long refreshDuration;

	public String generateRefreshTokenString(CommonHeader commonHeader, GetAccessToken getAccessToken, JWSHeader header,
			JWSSigner signer) {
		Date now = new Date();
		Log.debug("Generate refresh token.");
		JWTClaimsSet refreshPayload = new JWTClaimsSet.Builder().issuer(issuer).audience(refreshAudience).issueTime(now)
				.expirationTime(new Date(now.getTime() + refreshDuration * 1000)).claim("scope", "offline_access")
				.claim("acquirerId", commonHeader.getAcquirerId()).claim("channel", commonHeader.getChannel())
				.claim("merchantId", commonHeader.getMerchantId()).claim("clientId", getAccessToken.getClientId())
				.build();

		SignedJWT refreshToken = new SignedJWT(header, refreshPayload);

		Log.debug("Sign refresh token.");
		try {
			refreshToken.sign(signer);
			return refreshToken.serialize();

		} catch (JOSEException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

	}

}
