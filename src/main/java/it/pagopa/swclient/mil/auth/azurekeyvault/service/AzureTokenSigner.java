/*
 * AzureTokenSigner.java
 *
 * 1 ago 2023
 */
package it.pagopa.swclient.mil.auth.azurekeyvault.service;

import static it.pagopa.swclient.mil.auth.ErrorCode.*;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.Base64;
import java.util.Objects;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.util.Base64URL;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
import it.pagopa.swclient.mil.auth.azurekeyvault.bean.SignRequest;
import it.pagopa.swclient.mil.auth.azurekeyvault.bean.VerifySignatureRequest;
import it.pagopa.swclient.mil.auth.azurekeyvault.util.SignedJWTFactory;
import it.pagopa.swclient.mil.auth.service.TokenSigner;
import it.pagopa.swclient.mil.auth.util.AuthError;
import it.pagopa.swclient.mil.auth.util.AuthException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 * 
 */
@ApplicationScoped
public class AzureTokenSigner implements TokenSigner {
	/*
	 * 
	 */
	@Inject
	AzureKeyFinder keyFinder;

	/*
	 * 
	 */
	@Inject
	AzureKeyVaultService keyVaultService;

	/*
	 * 
	 */
	@Inject
	AzureAuthService authService;
	
	/*
	 * 
	 */
	private static final byte[] ID = new byte[] {
		0x30, 0x31, 0x30, 0x0d, 0x06, 0x09, 0x60, (byte) 0x86, 0x48, 0x01, 0x65, 0x03, 0x04, 0x02, 0x01, 0x05, 0x00, 0x04, 0x20
	};
	
	/**
	 * 
	 * @param header
	 * @param payload
	 * @return
	 * @throws NoSuchAlgorithmException
	 */
	private String getDerDigestInfo(JWSHeader header, JWTClaimsSet payload) throws NoSuchAlgorithmException {
		String headerBase64 = Base64.getUrlEncoder().encodeToString(header.toString().getBytes(StandardCharsets.UTF_8));
		String payloadBase64 = Base64.getUrlEncoder().encodeToString(payload.toString().getBytes(StandardCharsets.UTF_8));

		String stringToSign = headerBase64 + "." + payloadBase64;
		byte[] bytesToSign = stringToSign.getBytes(StandardCharsets.UTF_8);

		MessageDigest digest = MessageDigest.getInstance("SHA256");
		digest.update(bytesToSign);
		byte[] hash = digest.digest();

		byte[] derDigestInfo = new byte[ID.length + hash.length];
		System.arraycopy(ID, 0, derDigestInfo, 0, ID.length);
		System.arraycopy(hash, 0, derDigestInfo, ID.length, hash.length);

		return Base64.getUrlEncoder().encodeToString(derDigestInfo);
	}

	/**
	 * Signs the given token by means of the valid private key with the greatest expiration.
	 * 
	 * @param payload
	 * @return
	 */
	@Override
	public Uni<SignedJWT> sign(JWTClaimsSet payload) {
		Log.debug("Token signing.");
		return keyFinder.findValidPublicKeyWithGreatestExpiration()
			.chain(item -> {
				String kid = item.get().getKid();
				String[] components = kid.split("/");
				String keyName = components[components.length - 2];
				String keyVersion = components[components.length - 1];

				JWSHeader header = new JWSHeader(JWSAlgorithm.RS256, null, null, null, null, null, null, null, null, null, kid, true, null, null);

				try {
					String derDigestInfoBase64 = getDerDigestInfo(header, payload);

					SignRequest req = new SignRequest(JWSAlgorithm.RS256.getName(), derDigestInfoBase64);
					
					return keyVaultService.sign(item.context().get(AzureKeyFinder.TOKEN), keyName, keyVersion, req)
						.map(resp -> {
							try {
								return SignedJWTFactory.createInstance(header.toBase64URL(), payload.toPayload().toBase64URL(), Base64URL.from(resp.getSignature()));
							} catch (ParseException e) {
								String message = String.format("[%s] Error generating token.", ERROR_GENERATING_TOKEN);
								Log.errorf(e, message);
								throw new AuthError(ERROR_GENERATING_TOKEN, message);
							}
						});
				} catch (NoSuchAlgorithmException e) {
					String message = String.format("[%s] Error generating token.", ERROR_GENERATING_TOKEN);
					Log.errorf(e, message);
					throw new AuthError(ERROR_GENERATING_TOKEN, message);
				}
			});
	}

	/**
	 * This class verifies the token signature.
	 * 
	 * If the verification succeeds, the method returns void, otherwise it returns a failure with
	 * specific error code.
	 * 
	 * @param token
	 * @return
	 */
	@Override
	public Uni<Void> verify(SignedJWT token) {
		Log.debug("Signature verification.");

		String kid = token.getHeader().getKeyID();
		String[] components = kid.split("/");
		String keyName = components[components.length - 2];
		String keyVersion = components[components.length - 1];

		return authService.getAccessToken()
			.invoke(x -> Log.debug(x))
			.map(x -> {
				String t = x.getToken();
				if (t != null) {
					return t;
				} else {
					String message = String.format("[%s] Azure access token not valid.", AZURE_ACCESS_TOKEN_IS_NULL);
					Log.error(message);
					throw new AuthError(AZURE_ACCESS_TOKEN_IS_NULL, message);
				}
			}) // Getting the access token.
			.chain(azureToken -> {
				try {
					String derDigestInfoBase64 = getDerDigestInfo(token.getHeader(), token.getJWTClaimsSet());
					String signatureBase64 = Base64.getUrlEncoder().encodeToString(token.getSignature().decode());
					VerifySignatureRequest req = new VerifySignatureRequest(JWSAlgorithm.RS256.getName(), derDigestInfoBase64, signatureBase64);
					return keyVaultService.verifySignature(azureToken, keyName, keyVersion, req)
						.map(resp -> {
							if (Objects.equals(resp.getOk(), Boolean.TRUE)) {
								Log.debug("Signature has been successfully verified.");
								return null;
							} else {
								String message = String.format("[%s] Wrong signature.", WRONG_SIGNATURE);
								Log.warn(message);
								throw new AuthException(WRONG_SIGNATURE, message);
							}
						});
				} catch (NoSuchAlgorithmException | ParseException e) {
					String message = String.format("[%s] Error verifing signature.", ERROR_VERIFING_SIGNATURE);
					Log.errorf(e, message);
					throw new AuthError(ERROR_VERIFING_SIGNATURE, message);
				}
			});
	}
}
