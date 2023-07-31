/*
 * AzureKeyVaultService.java
 *
 * 27 lug 2023
 */
package it.pagopa.swclient.mil.auth.azurekeyvault.service;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import io.smallrye.mutiny.Uni;
import it.pagopa.swclient.mil.auth.azurekeyvault.bean.CreateKeyRequest;
import it.pagopa.swclient.mil.auth.azurekeyvault.bean.CreateKeyResponse;
import it.pagopa.swclient.mil.auth.azurekeyvault.bean.GetKeyResponse;
import it.pagopa.swclient.mil.auth.azurekeyvault.bean.GetKeyVersionsResponse;
import it.pagopa.swclient.mil.auth.azurekeyvault.bean.GetKeysResponse;
import it.pagopa.swclient.mil.auth.azurekeyvault.bean.SignRequest;
import it.pagopa.swclient.mil.auth.azurekeyvault.bean.SignResponse;
import it.pagopa.swclient.mil.auth.azurekeyvault.bean.VerifySignatureRequest;
import it.pagopa.swclient.mil.auth.azurekeyvault.bean.VerifySignatureResponse;
import it.pagopa.swclient.mil.auth.azurekeyvault.client.AzureKeyVaultClient;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * 
 * @author Antonio Tarricone
 */
@ApplicationScoped
public class AzureKeyVaultService implements AzureKeyVaultClient {
	/*
	 * 
	 */
	@RestClient
	AzureKeyVaultClient client;
	
	/*
	 * 
	 */
	private static final String BEARER = "Bearer ";

	/**
	 * 
	 */
	@Override
	public Uni<CreateKeyResponse> createKey(String accessToken, String keyName, CreateKeyRequest createKeyRequest) {
		return client.createKey(BEARER + accessToken, keyName, createKeyRequest);
	}

	/**
	 * 
	 */
	@Override
	public Uni<GetKeysResponse> getKeys(String accessToken) {
		return client.getKeys(BEARER + accessToken);
	}

	/**
	 * 
	 */
	@Override
	public Uni<GetKeyResponse> getKey(String accessToken, String keyName, String keyVersion) {
		return client.getKey(BEARER + accessToken, keyName, keyVersion);
	}
	
	/**
	 * 
	 */
	@Override
	public Uni<GetKeyVersionsResponse> getKeyVersions(String accessToken, String keyName) {
		return client.getKeyVersions(BEARER + accessToken, keyName);
	}

	/**
	 * 
	 */
	@Override
	public Uni<SignResponse> sign(String accessToken, String keyName, String keyVersion, SignRequest signRequest) {
		return client.sign(BEARER + accessToken, keyName, keyVersion, signRequest);
	}

	/**
	 * 
	 */
	@Override
	public Uni<VerifySignatureResponse> verifySignature(String accessToken, String keyName, String keyVersion, VerifySignatureRequest verifySignatureRequest) {
		return client.verifySignature(BEARER + accessToken, keyName, keyVersion, verifySignatureRequest);
	}
}
