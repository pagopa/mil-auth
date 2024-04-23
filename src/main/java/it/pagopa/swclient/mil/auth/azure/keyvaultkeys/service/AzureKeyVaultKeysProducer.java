/*
 * AzureKeyVaultKeysProducer.java
 *
 * 13 apr 2024
 */
package it.pagopa.swclient.mil.auth.azure.keyvaultkeys.service;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import com.azure.identity.ManagedIdentityCredentialBuilder;
import com.azure.storage.blob.BlobContainerAsyncClient;
import com.azure.storage.blob.BlobContainerClientBuilder;

import io.quarkus.arc.DefaultBean;
import io.quarkus.arc.profile.IfBuildProfile;
import it.pagopa.swclient.mil.auth.azure.identity.service.AzureIdentityService;
import it.pagopa.swclient.mil.auth.azure.keyvaultkeys.client.AzureKeyVaultKeysClient;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;

/**
 * 
 * @author Antonio Tarricone
 */
@ApplicationScoped
public class AzureKeyVaultKeysProducer {
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
	@Inject
	AzureKeyVaultKeysProducer(AzureIdentityService identityService, @RestClient AzureKeyVaultKeysClient keyClient) {
		this.identityService = identityService;
		this.keyClient = keyClient;
	}

	/**
	 * 
	 * @return
	 */
	@Produces
	@ApplicationScoped
	@DefaultBean
	public AzureKeyVaultKeys createInstance() {
		return new AzureKeyVaultKeysService(identityService, keyClient);
	}

	/**
	 * 
	 * @return
	 */
	@Produces
	@ApplicationScoped
	@IfBuildProfile(anyOf = {
		"dev", "test"
	})
	public AzureKeyVaultKeys createDevInstance() {
		return new AzureKeyVaultKeysDevService();
	}
}