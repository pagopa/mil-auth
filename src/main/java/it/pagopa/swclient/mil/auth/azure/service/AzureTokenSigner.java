/*
 * AzureTokenSigner.java
 *
 * 24 mar 2024
 */
package it.pagopa.swclient.mil.auth.azure.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.Objects;

import com.azure.core.exception.ResourceNotFoundException;
import com.azure.security.keyvault.keys.KeyAsyncClient;
import com.azure.security.keyvault.keys.cryptography.models.SignResult;
import com.azure.security.keyvault.keys.cryptography.models.SignatureAlgorithm;
import com.azure.security.keyvault.keys.cryptography.models.VerifyResult;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.util.Base64URL;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
import it.pagopa.swclient.mil.auth.azure.util.SignedJWTFactory;
import it.pagopa.swclient.mil.auth.bean.AuthErrorCode;
import it.pagopa.swclient.mil.auth.bean.PublicKey;
import it.pagopa.swclient.mil.auth.service.crypto.TokenSigner;
import it.pagopa.swclient.mil.auth.util.AuthError;
import it.pagopa.swclient.mil.auth.util.AuthException;
import it.pagopa.swclient.mil.auth.util.UniGenerator;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import mutiny.zero.flow.adapters.AdaptersToFlow;
import reactor.core.publisher.Mono;

/**
 * 
 * @author Antonio Tarricone
 */
@ApplicationScoped
public class AzureTokenSigner implements TokenSigner {
	/*
	 * Azure Key Vault client.
	 */
	private KeyAsyncClient keyClient;

	/*
	 * Key finder.
	 */
	private AzureKeyFinder keyFinder;

	/**
	 * Constructor.
	 * 
	 * @param keyFinder
	 */
	@Inject
	AzureTokenSigner(AzureKeyFinder keyFinder) {
		this.keyFinder = keyFinder;
		keyClient = keyFinder.getKeyClient();
	}

	/**
	 * 
	 * @param header
	 * @param payload
	 * @return
	 * @throws NoSuchAlgorithmException
	 */
	private byte[] getDigest(Base64URL header, Base64URL payload) throws NoSuchAlgorithmException {
		String stringToSign = header.toString() + "." + payload.toString();
		byte[] bytesToSign = stringToSign.getBytes(StandardCharsets.UTF_8);

		MessageDigest digest = MessageDigest.getInstance("SHA256");
		digest.update(bytesToSign);

		return digest.digest();
	}

	/**
	 * 
	 * @param keyName
	 * @param keyVersion
	 * @param digest
	 * @return
	 */
	private Uni<byte[]> sign(String keyName, String keyVersion, byte[] digest) {
		try {
			Mono<byte[]> signature = keyClient.getCryptographyAsyncClient(keyName, keyVersion)
				.sign(SignatureAlgorithm.RS256, digest)
				.onErrorMap(t -> {
					Log.errorf(t, AuthErrorCode.ERROR_CREATING_CRYPTOGRAPHY_ASYNC_CLIENT_MSG);
					return new AuthError(AuthErrorCode.ERROR_CREATING_CRYPTOGRAPHY_ASYNC_CLIENT, AuthErrorCode.ERROR_CREATING_CRYPTOGRAPHY_ASYNC_CLIENT_MSG);
				})
				.map(SignResult::getSignature);

			return Uni.createFrom().publisher(AdaptersToFlow.publisher(signature));
		} catch (ResourceNotFoundException | NullPointerException | UnsupportedOperationException e) {
			Log.errorf(e, AuthErrorCode.EXCEPTION_CREATING_CRYPTOGRAPHY_ASYNC_CLIENT_MSG);
			return UniGenerator.error(AuthErrorCode.EXCEPTION_CREATING_CRYPTOGRAPHY_ASYNC_CLIENT, AuthErrorCode.EXCEPTION_CREATING_CRYPTOGRAPHY_ASYNC_CLIENT_MSG);
		}
	}

	/**
	 * 
	 * @param key
	 * @param claimsSet
	 * @return
	 */
	private Uni<SignedJWT> sign(PublicKey key, JWTClaimsSet claimsSet) {
		String kid = key.getKid();
		String[] kidParts = kid.split("/");

		if (kidParts.length == 2) {
			Base64URL header = new JWSHeader(JWSAlgorithm.RS256, null, null, null, null, null, null, null, null, null, kid, true, null, null).toBase64URL();
			Base64URL payload = claimsSet.toPayload().toBase64URL();

			try {
				byte[] digest = getDigest(header, payload);
				return sign(kidParts[0], kidParts[1], digest)
					.map(resp -> {
						try {
							return SignedJWTFactory.createInstance(header, payload, Base64URL.encode(resp));
						} catch (ParseException e) {
							Log.errorf(e, AuthErrorCode.ERROR_GENERATING_TOKEN_MSG);
							throw new AuthError(AuthErrorCode.ERROR_GENERATING_TOKEN, AuthErrorCode.ERROR_GENERATING_TOKEN_MSG);
						}
					});
			} catch (NoSuchAlgorithmException e) {
				Log.errorf(e, AuthErrorCode.ERROR_GENERATING_TOKEN_MSG);
				return UniGenerator.error(AuthErrorCode.ERROR_GENERATING_TOKEN, AuthErrorCode.ERROR_GENERATING_TOKEN_MSG);
			}
		} else {
			Log.error(String.format(AuthErrorCode.INVALID_KID_MSG + " [%s]", kid));
			return UniGenerator.error(AuthErrorCode.INVALID_KID, AuthErrorCode.INVALID_KID_MSG);
		}
	}

	/**
	 * Signs the given token by means of the valid private key with the greatest expiration.
	 *
	 * @param payload
	 * @return
	 */
	@Override
	public Uni<SignedJWT> sign(JWTClaimsSet claimsSet) {
		Log.debug("Token signing");
		return keyFinder.findPublicKey()
			.chain(key -> sign(key, claimsSet));
	}

	/**
	 * 
	 * @param keyName
	 * @param keyVersion
	 * @param digest
	 * @param signature
	 * @return
	 */
	private Uni<Boolean> verify(String keyName, String keyVersion, byte[] digest, byte[] signature) {
		try {
			Mono<Boolean> isValid = keyClient.getCryptographyAsyncClient(keyName, keyVersion)
				.verify(SignatureAlgorithm.RS256, digest, signature)
				.onErrorMap(t -> {
					Log.errorf(t, AuthErrorCode.ERROR_CREATING_CRYPTOGRAPHY_ASYNC_CLIENT_MSG);
					return new AuthError(AuthErrorCode.ERROR_CREATING_CRYPTOGRAPHY_ASYNC_CLIENT, AuthErrorCode.ERROR_CREATING_CRYPTOGRAPHY_ASYNC_CLIENT_MSG);
				})
				.map(VerifyResult::isValid);

			return Uni.createFrom().publisher(AdaptersToFlow.publisher(isValid));
		} catch (ResourceNotFoundException | NullPointerException | UnsupportedOperationException e) {
			Log.errorf(e, AuthErrorCode.EXCEPTION_CREATING_CRYPTOGRAPHY_ASYNC_CLIENT_MSG);
			return UniGenerator.error(AuthErrorCode.EXCEPTION_CREATING_CRYPTOGRAPHY_ASYNC_CLIENT, AuthErrorCode.EXCEPTION_CREATING_CRYPTOGRAPHY_ASYNC_CLIENT_MSG);
		}
	}

	/**
	 * Verifies the token signature. If the verification succeeds, the method returns void, otherwise it
	 * returns a failure with specific error code.
	 *
	 * @param token
	 * @return
	 */
	@Override
	public Uni<Void> verify(SignedJWT token) {
		Log.debug("Signature verification");

		String kid = token.getHeader().getKeyID();
		String[] kidParts = kid.split("/");

		if (kidParts.length == 2) {
			try {
				byte[] digest = getDigest(token.getHeader().toBase64URL(), token.getJWTClaimsSet().toPayload().toBase64URL());
				byte[] signature = token.getSignature().decode();
				return verify(kidParts[0], kidParts[1], digest, signature)
					.map(resp -> {
						if (Objects.equals(resp, Boolean.TRUE)) {
							Log.debug("Signature has been successfully verified.");
							return null;
						} else {
							Log.warn(AuthErrorCode.WRONG_SIGNATURE_MSG);
							throw new AuthException(AuthErrorCode.WRONG_SIGNATURE, AuthErrorCode.WRONG_SIGNATURE_MSG);
						}
					});
			} catch (NoSuchAlgorithmException | ParseException e) {
				Log.errorf(e, AuthErrorCode.ERROR_VERIFING_SIGNATURE_MSG);
				return UniGenerator.error(AuthErrorCode.ERROR_VERIFING_SIGNATURE, AuthErrorCode.ERROR_VERIFING_SIGNATURE_MSG);
			}
		} else {
			Log.error(String.format(AuthErrorCode.INVALID_KID_MSG + " [%s]", kid));
			return UniGenerator.error(AuthErrorCode.INVALID_KID, AuthErrorCode.INVALID_KID_MSG);
		}
	}
}