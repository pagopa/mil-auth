/*
 * AzureKeyVaultKeysDevService.java
 *
 * 13 apr 2024
 */
package it.pagopa.swclient.mil.auth.azure.keyvaultkeys.service;

import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.RSAKeyGenParameterSpec;
import java.time.Instant;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SequencedMap;
import java.util.UUID;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
import it.pagopa.swclient.mil.auth.azure.identity.bean.AccessToken;
import it.pagopa.swclient.mil.auth.azure.identity.bean.Scope;
import it.pagopa.swclient.mil.auth.azure.identity.service.AzureIdentityService;
import it.pagopa.swclient.mil.auth.azure.keyvaultkeys.bean.JsonWebKey;
import it.pagopa.swclient.mil.auth.azure.keyvaultkeys.bean.JsonWebKeyType;
import it.pagopa.swclient.mil.auth.azure.keyvaultkeys.bean.KeyAttributes;
import it.pagopa.swclient.mil.auth.azure.keyvaultkeys.bean.KeyBundle;
import it.pagopa.swclient.mil.auth.azure.keyvaultkeys.bean.KeyCreateParameters;
import it.pagopa.swclient.mil.auth.azure.keyvaultkeys.bean.KeyItem;
import it.pagopa.swclient.mil.auth.azure.keyvaultkeys.bean.KeyListResult;
import it.pagopa.swclient.mil.auth.azure.keyvaultkeys.bean.KeyOperationResult;
import it.pagopa.swclient.mil.auth.azure.keyvaultkeys.bean.KeySignParameters;
import it.pagopa.swclient.mil.auth.azure.keyvaultkeys.bean.KeyVerifyParameters;
import it.pagopa.swclient.mil.auth.azure.keyvaultkeys.bean.KeyVerifyResult;
import it.pagopa.swclient.mil.auth.azure.keyvaultkeys.client.AzureKeyVaultKeysClient;
import it.pagopa.swclient.mil.auth.bean.AuthErrorCode;
import it.pagopa.swclient.mil.auth.util.UniGenerator;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 * Implementation of AzureKeyVaultKeys for dev and test profile.
 * 
 * @author Antonio Tarricone
 */
public class AzureKeyVaultKeysDevService implements AzureKeyVaultKeys {
	/*
	 * 
	 */
	private static final String MY_VAULT = "https://myvault.vault.azure.net/keys/";

	/*
	 * 
	 */
	private Map<String, SequencedMap<String, KeyBundle>> keyStore;

	/**
	 * 
	 */
	public AzureKeyVaultKeysDevService() {
		keyStore = new HashMap<>();
	}

	/**
	 * 
	 * @param keyName
	 * @param keyCreateParameters
	 * @return
	 */
	public Uni<KeyBundle> createKey(String keyName, KeyCreateParameters keyCreateParameters) {
		JsonWebKeyType kty = keyCreateParameters.getKty();
		if (kty.equals(JsonWebKeyType.RSA) || kty.equals(JsonWebKeyType.RSA_HSM)) {
			/*
			 * Initialize key pair generator.
			 */
			KeyPairGenerator keyPairGenerator;
			try {
				keyPairGenerator = KeyPairGenerator.getInstance("RSA");
			} catch (NoSuchAlgorithmException e) {
				Log.errorf(e, AuthErrorCode.ERROR_CREATING_RSA_KEY_MSG);
				return UniGenerator.error(AuthErrorCode.ERROR_CREATING_RSA_KEY, AuthErrorCode.ERROR_CREATING_RSA_KEY_MSG);
			}
			int keySize = keyCreateParameters.getKeySize();
			Integer publicExponent = keyCreateParameters.getPublicExponent();
			if (publicExponent != null) {
				RSAKeyGenParameterSpec spec = new RSAKeyGenParameterSpec(keySize, BigInteger.valueOf(publicExponent));
				try {
					keyPairGenerator.initialize(spec);
				} catch (InvalidAlgorithmParameterException e) {
					Log.errorf(e, AuthErrorCode.ERROR_CREATING_RSA_KEY_MSG);
					return UniGenerator.error(AuthErrorCode.ERROR_CREATING_RSA_KEY, AuthErrorCode.ERROR_CREATING_RSA_KEY_MSG);
				}
			} else {
				keyPairGenerator.initialize(keySize);
			}

			/*
			 * Generate key pair.
			 */
			KeyPair keyPair = keyPairGenerator.generateKeyPair();

			/*
			 * Get public and prive key from key pair.
			 */
			RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
			RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();

			/*
			 * 
			 */
			KeyAttributes keyAttributes = keyCreateParameters.getAttributes();

			/*
			 * Generate key version and kid.
			 */
			String keyVersion = UUID.randomUUID().toString().replace("-", "");
			String kid = MY_VAULT + keyName + "/" + keyVersion;

			/*
			 * 
			 */
			long now = Instant.now().getEpochSecond();

			/*
			 * Generate key bundle.
			 */
			KeyBundle keyBundle = new KeyBundle()
				.setAttributes(new KeyAttributes()
					.setCreated(now)
					.setEnabled(keyAttributes.getEnabled())
					.setExp(keyAttributes.getExp())
					.setExportable(keyAttributes.getExportable())
					.setNbf(keyAttributes.getNbf())
					.setRecoverableDays(keyAttributes.getRecoverableDays())
					.setRecoveryLevel(keyAttributes.getRecoveryLevel())
					.setUpdated(now))
				.setKey(new JsonWebKey()
					.setCrv(keyCreateParameters.getCrv())
					.setD(privateKey.getPrivateExponent().toByteArray())
					.setE(publicKey.getPublicExponent().toByteArray())
					.setKeyOps(keyCreateParameters.getKeyOps())
					.setKid(kid)
					.setKty(kty)
					.setN(publicKey.getModulus().toByteArray()))
				.setManaged(false)
				.setReleasePolicy(keyCreateParameters.getReleasePolicy())
				.setTags(keyCreateParameters.getTags());

			/*
			 * Store key bundle.
			 */
			SequencedMap<String, KeyBundle> keyVersions = keyStore.get(keyName);
			if (keyVersions == null) {
				keyVersions = new LinkedHashMap<>();
			}
			keyVersions.putLast(keyVersion, keyBundle);

			return UniGenerator.item(keyBundle);
		} else {
			/*
			 * Unsupported key type.
			 */
			String errorMsg = String.format("Only %s and %s key types are supported", JsonWebKeyType.RSA.name(), JsonWebKeyType.RSA_HSM.name());
			Log.error(errorMsg);
			return UniGenerator.error(AuthErrorCode.ERROR_CREATING_RSA_KEY, errorMsg);
		}
	}

	/**
	 * 
	 * @return
	 */
	public Uni<KeyListResult> getKeys() {
		return UniGenerator.item(new KeyListResult()
			.setValue(keyStore.values()
				.stream()
				.map(SequencedMap::lastEntry)
				.map(Map.Entry::getValue)
				.map(keyBundle -> new KeyItem()
					.setAttributes(keyBundle.getAttributes())
					.setKid(keyBundle.getKey().getKid())
					.setManaged(keyBundle.getManaged())
					.setTags(keyBundle.getTags()))
				.toList()));
	}

	/**
	 * 
	 * @param keyName
	 * @param keyVersion
	 * @return
	 */
	public Uni<KeyBundle> getKey(String keyName, String keyVersion) {
		SequencedMap<String, KeyBundle> keyVersions = keyStore.get(keyName);
		if (keyVersions != null) {
			KeyBundle keyBundle = keyVersions.get(keyVersion);
			if (keyBundle != null) {
				return UniGenerator.item(keyBundle);
			} else {
				return UniGenerator.exception(AuthErrorCode.KEY_NOT_FOUND, AuthErrorCode.KEY_NOT_FOUND);
			}
		} else {
			return UniGenerator.exception(AuthErrorCode.KEY_NOT_FOUND, AuthErrorCode.KEY_NOT_FOUND);
		}
	}

	/**
	 * 
	 * @param keyName
	 * @return
	 */
	public Uni<KeyListResult> getKeyVersions(String keyName) {
		SequencedMap<String, KeyBundle> keyVersions = keyStore.get(keyName);
		if (keyVersions != null) {
			return UniGenerator.item(new KeyListResult()
				.setValue(
					keyVersions.values()
						.stream()
						.map(keyBundle -> new KeyItem()
							.setAttributes(keyBundle.getAttributes())
							.setKid(keyBundle.getKey().getKid())
							.setManaged(keyBundle.getManaged())
							.setTags(keyBundle.getTags()))
						.toList()));
		} else {
			return UniGenerator.exception(AuthErrorCode.KEY_NOT_FOUND, AuthErrorCode.KEY_NOT_FOUND);
		}
	}

	/**
	 * 
	 * @param keyName
	 * @param keyVersion
	 * @param keySignParameters
	 * @return
	 */
	public Uni<KeyOperationResult> sign(String keyName, String keyVersion, KeySignParameters keySignParameters) {
		return getAccessToken()
			.chain(accessToken -> keyClient.sign(accessToken, keyName, keyVersion, keySignParameters));
	}

	/**
	 * 
	 * @param keyName
	 * @param keyVersion
	 * @param verifySignatureRequest
	 * @return
	 */
	public Uni<KeyVerifyResult> verifySignature(String keyName, String keyVersion, KeyVerifyParameters verifySignatureRequest) {
		return getAccessToken()
			.chain(accessToken -> keyClient.verifySignature(accessToken, keyName, keyVersion, verifySignatureRequest));
	}
}
