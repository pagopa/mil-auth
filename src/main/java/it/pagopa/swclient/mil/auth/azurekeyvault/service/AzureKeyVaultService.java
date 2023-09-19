/*
 * AzureKeyVaultService.java
 *
 * 27 lug 2023
 */
package it.pagopa.swclient.mil.auth.azurekeyvault.service;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
import it.pagopa.swclient.mil.auth.azurekeyvault.bean.CreateKeyRequest;
import it.pagopa.swclient.mil.auth.azurekeyvault.bean.DetailedKey;
import it.pagopa.swclient.mil.auth.azurekeyvault.bean.GetKeysResponse;
import it.pagopa.swclient.mil.auth.azurekeyvault.bean.SignRequest;
import it.pagopa.swclient.mil.auth.azurekeyvault.bean.SignResponse;
import it.pagopa.swclient.mil.auth.azurekeyvault.bean.VerifySignatureRequest;
import it.pagopa.swclient.mil.auth.azurekeyvault.bean.VerifySignatureResponse;
import it.pagopa.swclient.mil.auth.azurekeyvault.client.AzureKeyVaultClient;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * @author Antonio Tarricone
 */
@ApplicationScoped
public class AzureKeyVaultService {
	/*
	 *
	 */
	private static final String BEARER = "Bearer ";
	/*
	 *
	 */
	@RestClient
	AzureKeyVaultClient client;

	/**
	 * @param accessToken
	 * @param keyName
	 * @param createKeyRequest
	 * @return
	 */
	public Uni<DetailedKey> createKey(String accessToken, String keyName, CreateKeyRequest createKeyRequest) {
		Log.debugf("Creating a new key [%s]: [%s]", keyName, createKeyRequest);
		return client.createKey(BEARER + accessToken, keyName, createKeyRequest);
	}

	/**
	 * @param accessToken
	 * @return
	 */
	public Uni<GetKeysResponse> getKeys(String accessToken) {
		Log.debug("Retrieving the list of keys.");
		return client.getKeys(BEARER + accessToken);
	}

	/**
	 * @param accessToken
	 * @param keyName
	 * @param keyVersion
	 * @return
	 */
	public Uni<DetailedKey> getKey(String accessToken, String keyName, String keyVersion) {
		Log.debugf("Retrieving details of version [%s] of the key [%s].", keyVersion, keyName);
		return client.getKey(BEARER + accessToken, keyName, keyVersion);
	}

	/**
	 * @param accessToken
	 * @param keyName
	 * @return
	 */
	public Uni<GetKeysResponse> getKeyVersions(String accessToken, String keyName) {
		Log.debugf("Retrieving versions of the key [%s].", keyName);
		return client.getKeyVersions(BEARER + accessToken, keyName);
	}

	/**
	 * @param accessToken
	 * @param keyName
	 * @param keyVersion
	 * @param signRequest
	 * @return
	 */
	public Uni<SignResponse> sign(String accessToken, String keyName, String keyVersion, SignRequest signRequest) {
		Log.debugf("Signing data with key [%s/%s]: [%s]", keyName, keyVersion, signRequest);
		return client.sign(BEARER + accessToken, keyName, keyVersion, signRequest);
	}

	/**
	 * @param accessToken
	 * @param keyName
	 * @param keyVersion
	 * @param verifySignatureRequest
	 * @return
	 */
	public Uni<VerifySignatureResponse> verifySignature(String accessToken, String keyName, String keyVersion, VerifySignatureRequest verifySignatureRequest) {
		Log.debugf("Verifing signature with key [%s/%s]: [%s]", keyName, keyVersion, verifySignatureRequest);
		return client.verifySignature(BEARER + accessToken, keyName, keyVersion, verifySignatureRequest);
	}
}
