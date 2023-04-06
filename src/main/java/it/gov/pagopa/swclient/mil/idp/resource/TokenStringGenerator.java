package it.gov.pagopa.swclient.mil.idp.resource;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateKeySpec;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import javax.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.util.Base64URL;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
import it.gov.pagopa.swclient.mil.bean.CommonHeader;
import it.gov.pagopa.swclient.mil.idp.bean.GetAccessToken;
import it.gov.pagopa.swclient.mil.idp.bean.KeyPair;
import it.gov.pagopa.swclient.mil.idp.dao.GrantEntity;

@ApplicationScoped
public class TokenStringGenerator {
	@ConfigProperty(name = "issuer")
	private String issuer;

	/*
	 * 
	 */
	@ConfigProperty(name = "access.audience")
	private List<String> accessAudience;


	/*
	 * .
	 */
	@ConfigProperty(name = "access.duration")
	private long accessDuration;
	

	

	
	private String concat(List<String> strings) {
		StringBuffer buffer = new StringBuffer();
		strings.forEach(x -> {
			buffer.append(x);
			buffer.append(" ");
		});
		return buffer.toString().trim();
	}
	
	public SignedJWT generateAccessToken(JWSHeader header,  KeyPair key, GetAccessToken getAccessToken, CommonHeader commonHeader, GrantEntity grantEntity, JWSSigner signer)
	{
		Log.debug("Generate access token.");
		Date now = new Date();

		JWTClaimsSet accessPayload = new JWTClaimsSet.Builder()
			.issuer(issuer)
			.audience(accessAudience)
			.issueTime(now)
			.expirationTime(new Date(now.getTime() + accessDuration * 1000))
			.claim("scope", concat(grantEntity.getGrants()))
			.build();
		SignedJWT accessToken = new SignedJWT(header, accessPayload);
		try {
			Log.debug("Sign access token.");
			accessToken.sign(signer);
				
				return accessToken;
			}
		catch (JOSEException e) {
			Log.errorf(e, "Error during tokens signing.");
			return null;
		}
	}
	
}

