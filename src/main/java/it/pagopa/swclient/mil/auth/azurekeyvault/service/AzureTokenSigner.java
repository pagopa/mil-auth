/*
 * AzureTokenSigner.java
 *
 * 1 ago 2023
 */
package it.pagopa.swclient.mil.auth.azurekeyvault.service;

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
import it.pagopa.swclient.mil.auth.AuthErrorCode;
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

	/**
	 * @param header
	 * @param payload
	 * @return
	 * @throws NoSuchAlgorithmException
	 */
	private String getDerDigestInfo(Base64URL header, Base64URL payload) throws NoSuchAlgorithmException {
		String stringToSign = header.toString() + "." + payload.toString();
		byte[] bytesToSign = stringToSign.getBytes(StandardCharsets.UTF_8);

		MessageDigest digest = MessageDigest.getInstance("SHA256");
		digest.update(bytesToSign);
		byte[] hash = digest.digest();

		return Base64.getUrlEncoder().encodeToString(hash);
	}

	/**
	 * Signs the given token by means of the valid private key with the greatest expiration.
	 *
	 * @param payload
	 * @return
	 */
	@Override
	public Uni<SignedJWT> sign(JWTClaimsSet claimsSet) {
		Log.debug("Token signing.");
		return keyFinder.findValidPublicKeyWithGreatestExpiration()
			.chain(item -> {
				String kid = item.get().getKid();
				String[] components = kid.split("/");
				String keyName = components[components.length - 2];
				String keyVersion = components[components.length - 1];

				Base64URL header = new JWSHeader(JWSAlgorithm.RS256, null, null, null, null, null, null, null, null, null, kid, true, null, null).toBase64URL();
				Base64URL payload = claimsSet.toPayload().toBase64URL();

				try {
					String derDigestInfoBase64 = getDerDigestInfo(header, payload);

					SignRequest req = new SignRequest(JWSAlgorithm.RS256.getName(), derDigestInfoBase64);

					return keyVaultService.sign(item.context().get(AzureKeyFinder.TOKEN), keyName, keyVersion, req)
						.map(resp -> {
							try {
								return SignedJWTFactory.createInstance(header, payload, Base64URL.from(resp.getSignature()));
							} catch (ParseException e) {
								String message = String.format("[%s] Error generating token.", AuthErrorCode.ERROR_GENERATING_TOKEN);
								Log.errorf(e, message);
								throw new AuthError(AuthErrorCode.ERROR_GENERATING_TOKEN, message);
							}
						});
				} catch (NoSuchAlgorithmException e) {
					String message = String.format("[%s] Error generating token.", AuthErrorCode.ERROR_GENERATING_TOKEN);
					Log.errorf(e, message);
					throw new AuthError(AuthErrorCode.ERROR_GENERATING_TOKEN, message);
				}
			});
	}

	/**
	 * This class verifies the token signature.
	 * <p>
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
					String message = String.format("[%s] Azure access token not valid.", AuthErrorCode.AZURE_ACCESS_TOKEN_IS_NULL);
					Log.error(message);
					throw new AuthError(AuthErrorCode.AZURE_ACCESS_TOKEN_IS_NULL, message);
				}
			}) // Getting the access token.
			.chain(azureToken -> {
				try {
					String derDigestInfoBase64 = getDerDigestInfo(token.getHeader().toBase64URL(), token.getJWTClaimsSet().toPayload().toBase64URL());
					String signatureBase64 = Base64.getUrlEncoder().encodeToString(token.getSignature().decode());
					VerifySignatureRequest req = new VerifySignatureRequest(JWSAlgorithm.RS256.getName(), derDigestInfoBase64, signatureBase64);
					return keyVaultService.verifySignature(azureToken, keyName, keyVersion, req)
						.map(resp -> {
							if (Objects.equals(resp.getOk(), Boolean.TRUE)) {
								Log.debug("Signature has been successfully verified.");
								return null;
							} else {
								String message = String.format("[%s] Wrong signature.", AuthErrorCode.WRONG_SIGNATURE);
								Log.warn(message);
								throw new AuthException(AuthErrorCode.WRONG_SIGNATURE, message);
							}
						});
				} catch (NoSuchAlgorithmException | ParseException e) {
					String message = String.format("[%s] Error verifing signature.", AuthErrorCode.ERROR_VERIFING_SIGNATURE);
					Log.errorf(e, message);
					throw new AuthError(AuthErrorCode.ERROR_VERIFING_SIGNATURE, message);
				}
			});
	}
}
