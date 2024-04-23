/*
 * AzureKeyVaultKeysService.java
 *
 * 12 apr 2024
 */
package it.pagopa.swclient.mil.auth.azure.keyvaultkeys.service;

import io.smallrye.mutiny.Uni;
import it.pagopa.swclient.mil.auth.azure.keyvaultkeys.bean.KeyBundle;
import it.pagopa.swclient.mil.auth.azure.keyvaultkeys.bean.KeyCreateParameters;
import it.pagopa.swclient.mil.auth.azure.keyvaultkeys.bean.KeyListResult;
import it.pagopa.swclient.mil.auth.azure.keyvaultkeys.bean.KeyOperationResult;
import it.pagopa.swclient.mil.auth.azure.keyvaultkeys.bean.KeySignParameters;
import it.pagopa.swclient.mil.auth.azure.keyvaultkeys.bean.KeyVerifyParameters;
import it.pagopa.swclient.mil.auth.azure.keyvaultkeys.bean.KeyVerifyResult;

/**
 * 
 * @author Antonio Tarricone
 */
public interface AzureKeyVaultKeys {
	/**
	 * 
	 * @param keyName
	 * @param keyCreateParameters
	 * @return
	 */
	public Uni<KeyBundle> createKey(String keyName, KeyCreateParameters keyCreateParameters);

	/**
	 * 
	 * @return
	 */
	public Uni<KeyListResult> getKeys();

	/**
	 * 
	 * @param keyName
	 * @param keyVersion
	 * @return
	 */
	public Uni<KeyBundle> getKey(String keyName, String keyVersion);

	/**
	 * 
	 * @param keyName
	 * @return
	 */
	public Uni<KeyListResult> getKeyVersions(String keyName);

	/**
	 * 
	 * @param keyName
	 * @param keyVersion
	 * @param keySignParameters
	 * @return
	 */
	public Uni<KeyOperationResult> sign(String keyName, String keyVersion, KeySignParameters keySignParameters);

	/**
	 * 
	 * @param keyName
	 * @param keyVersion
	 * @param verifySignatureRequest
	 * @return
	 */
	public Uni<KeyVerifyResult> verifySignature(String keyName, String keyVersion, KeyVerifyParameters verifySignatureRequest);
}
