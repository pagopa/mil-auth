/*
 * AzureKeyVaultKeysService.java
 *
 * 12 apr 2024
 */
package it.pagopa.swclient.mil.auth.azure.keyvaultkeys.service;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import io.smallrye.mutiny.Uni;
import it.pagopa.swclient.mil.auth.azure.identity.bean.AccessToken;
import it.pagopa.swclient.mil.auth.azure.identity.bean.Scope;
import it.pagopa.swclient.mil.auth.azure.identity.service.AzureIdentityService;
import it.pagopa.swclient.mil.auth.azure.keyvaultkeys.bean.KeyBundle;
import it.pagopa.swclient.mil.auth.azure.keyvaultkeys.bean.KeyCreateParameters;
import it.pagopa.swclient.mil.auth.azure.keyvaultkeys.bean.KeyListResult;
import it.pagopa.swclient.mil.auth.azure.keyvaultkeys.bean.KeyOperationResult;
import it.pagopa.swclient.mil.auth.azure.keyvaultkeys.bean.KeySignParameters;
import it.pagopa.swclient.mil.auth.azure.keyvaultkeys.bean.KeyVerifyParameters;
import it.pagopa.swclient.mil.auth.azure.keyvaultkeys.bean.KeyVerifyResult;
import it.pagopa.swclient.mil.auth.azure.keyvaultkeys.client.AzureKeyVaultKeysClient;

/**
 * 
 * @author Antonio Tarricone
 */
public class AzureKeyVaultKeysService implements AzureKeyVaultKeys {
	/*
	 * 
	 */
	private AzureIdentityService identityService;

	/*
	 * 
	 */
	private AzureKeyVaultKeysClient keyClient;

	/**
	 * 
	 * @param identityService
	 * @param keyClient
	 */
	AzureKeyVaultKeysService(AzureIdentityService identityService, @RestClient AzureKeyVaultKeysClient keyClient) {
		this.identityService = identityService;
		this.keyClient = keyClient;
	}

	/**
	 * 
	 * @return
	 */
	private Uni<String> getAccessToken() {
		return identityService.getAccessToken(Scope.VAULT)
			.map(AccessToken::getAccessToken);
	}

	/**
	 * 
	 * @param keyName
	 * @param keyCreateParameters
	 * @return
	 */
	public Uni<KeyBundle> createKey(String keyName, KeyCreateParameters keyCreateParameters) {
		return getAccessToken()
			.chain(accessToken -> keyClient.createKey(accessToken, keyName, keyCreateParameters));
	}

	/**
	 * 
	 * @return
	 */
	public Uni<KeyListResult> getKeys() {
		return getAccessToken()
			.chain(accessToken -> keyClient.getKeys(accessToken));
	}

	/**
	 * 
	 * @param keyName
	 * @param keyVersion
	 * @return
	 */
	public Uni<KeyBundle> getKey(String keyName, String keyVersion) {
		return getAccessToken()
			.chain(accessToken -> keyClient.getKey(accessToken, keyName, keyVersion));
	}

	/**
	 * 
	 * @param keyName
	 * @return
	 */
	public Uni<KeyListResult> getKeyVersions(String keyName) {
		return getAccessToken()
			.chain(accessToken -> keyClient.getKeyVersions(accessToken, keyName));
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
