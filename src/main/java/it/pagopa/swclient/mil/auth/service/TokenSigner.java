/*
 * TokenSigner.java
 *
 * 1 giu 2024
 */
package it.pagopa.swclient.mil.auth.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.List;
import java.util.Objects;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.util.Base64URL;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
import it.pagopa.swclient.mil.auth.AuthErrorCode;
import it.pagopa.swclient.mil.auth.util.AuthError;
import it.pagopa.swclient.mil.auth.util.AuthException;
import it.pagopa.swclient.mil.auth.util.KeyUtils;
import it.pagopa.swclient.mil.auth.util.SignedJWTFactory;
import it.pagopa.swclient.mil.auth.util.UniGenerator;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.JsonWebKeyOperation;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.JsonWebKeySignatureAlgorithm;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeySignParameters;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.KeyVerifyParameters;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.service.AzureKeyVaultKeysExtReactiveService;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.service.AzureKeyVaultKeysReactiveService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 *
 */
@ApplicationScoped
public class TokenSigner extends KeyManCapabilities {
	/*
	 * 
	 */
	private static final String ERROR_MSG_TEMPL = "[%s] Error signing token";
	
	/**
	 * 
	 */
	TokenSigner() {
		super();
	}
	
	/**
	 * 
	 * @param keysExtService
	 * @param keysService
	 */
	@Inject
	TokenSigner(AzureKeyVaultKeysExtReactiveService keysExtService, AzureKeyVaultKeysReactiveService keysService) {
		super(keysExtService, keysService);
	}

	/**
	 * Calculate hash which will be signed.
	 * 
	 * @param header
	 * @param payload
	 * @return
	 * @throws NoSuchAlgorithmException
	 */
	private byte[] hash(Base64URL header, Base64URL payload) throws NoSuchAlgorithmException {
		String stringToSign = header.toString() + "." + payload.toString();
		byte[] bytesToSign = stringToSign.getBytes(StandardCharsets.UTF_8);
		MessageDigest digest = MessageDigest.getInstance("SHA256");
		digest.update(bytesToSign);
		return digest.digest();
	}

	/**
	 * 
	 * @param azureKid
	 * @param claimsSet
	 * @return
	 */
	private Uni<SignedJWT> sign(String azureKid, JWTClaimsSet claimsSet) {
		Log.tracef("Sign with kid = %s", azureKid);
		String[] keyNameAndVersion = KeyUtils.azureKid2KeyNameVersion(azureKid);
		String myKid = KeyUtils.azureKid2MyKid(azureKid);

		Base64URL header = new JWSHeader(JWSAlgorithm.RS256, null, null, null, null, null, null, null, null, null, myKid, true, null, null).toBase64URL();
		Base64URL payload = claimsSet.toPayload().toBase64URL();
		try {
			/*
			 * Calculate hash.
			 */
			byte[] hash = hash(header, payload);

			/*
			 * Sign.
			 */
			return keysService.sign(
				keyNameAndVersion[0],
				keyNameAndVersion[1],
				new KeySignParameters()
					.setAlg(JsonWebKeySignatureAlgorithm.RS256)
					.setValue(hash))
				.map(keyOperationResult -> {
					try {
						return SignedJWTFactory.createInstance(
							header,
							payload,
							Base64URL.encode(keyOperationResult.getValue()));
					} catch (ParseException e) {
						String message = String.format(ERROR_MSG_TEMPL, AuthErrorCode.ERROR_SIGNING_TOKEN);
						Log.errorf(e, message);
						throw new AuthError(AuthErrorCode.ERROR_SIGNING_TOKEN, message);
					}
				});
		} catch (NoSuchAlgorithmException e) {
			String message = String.format(ERROR_MSG_TEMPL, AuthErrorCode.ERROR_SIGNING_TOKEN);
			Log.errorf(e, message);
			return UniGenerator.error(AuthErrorCode.ERROR_SIGNING_TOKEN, message);
		}
	}

	/**
	 * Signs the given token by means of the valid private key with the greatest expiration.
	 * 
	 * @param claimsSet
	 * @return
	 */
	public Uni<SignedJWT> sign(JWTClaimsSet claimsSet) {
		Log.trace("Token signature generation");
		return retrieveKey(List.of(JsonWebKeyOperation.SIGN, JsonWebKeyOperation.VERIFY))
			.chain(azureKid -> sign(azureKid, claimsSet))
			.onFailure().invoke(this::cleanCache)
			.onFailure(t -> !(t instanceof AuthError))
			.transform(t -> {
				String message = String.format(ERROR_MSG_TEMPL, AuthErrorCode.ERROR_ENCRYPTING_CLAIM);
				Log.errorf(t, message);
				return new AuthError(AuthErrorCode.ERROR_ENCRYPTING_CLAIM, message);
			});
	}

	/**
	 * If the verification succeeds, the method returns void, otherwise it returns a failure with
	 * specific error code.
	 *
	 * @param token
	 * @return
	 */
	public Uni<Void> verify(SignedJWT token) {
		Log.trace("Signature verification");

		String myKid = token.getHeader().getKeyID();
		String[] keyNameVersion = KeyUtils.myKid2KeyNameVersion(myKid);
		try {
			byte[] hash = hash(token.getHeader().toBase64URL(), token.getJWTClaimsSet().toPayload().toBase64URL());
			byte[] signature = token.getSignature().decode();

			return keysService.verify(
				keyNameVersion[0],
				keyNameVersion[1],
				new KeyVerifyParameters()
					.setAlg(JsonWebKeySignatureAlgorithm.RS256)
					.setDigest(hash)
					.setValue(signature))
				.map(keyVerifyResult -> {
					if (Objects.equals(keyVerifyResult.getValue(), Boolean.TRUE)) {
						Log.debug("Signature has been successfully verified");
						return null;
					} else {
						String message = String.format("[%s] Wrong signature", AuthErrorCode.WRONG_SIGNATURE);
						Log.warn(message);
						throw new AuthException(AuthErrorCode.WRONG_SIGNATURE, message);
					}
				});
		} catch (NoSuchAlgorithmException | ParseException e) {
			String message = String.format("[%s] Error verifing signature", AuthErrorCode.ERROR_VERIFING_SIGNATURE);
			Log.errorf(e, message);
			return UniGenerator.error(AuthErrorCode.ERROR_VERIFING_SIGNATURE, message);
		}
	}
}