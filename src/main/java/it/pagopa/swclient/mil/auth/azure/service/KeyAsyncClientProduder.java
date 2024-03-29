/*
 * KeyAsyncClientProduder.java
 *
 * 26 mar 2024
 */
package it.pagopa.swclient.mil.auth.azure.service;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import com.azure.identity.ManagedIdentityCredentialBuilder;
import com.azure.security.keyvault.keys.KeyAsyncClient;
import com.azure.security.keyvault.keys.KeyClientBuilder;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;

/**
 * Producer of KeyAsyncClient.
 * 
 * @author Antonio Tarricone
 */
@ApplicationScoped
public class KeyAsyncClientProduder {
	/*
	 * 
	 */
	private String vaultUrl;
	
	/**
	 * 
	 * @param vaultUrl
	 */
	KeyAsyncClientProduder(@ConfigProperty(name = "azure-key-vault.url") String vaultUrl) {
		this.vaultUrl = vaultUrl;
	}

	/**
	 * 
	 * @return
	 */
	@Produces
	public KeyAsyncClient createInstance() {
		return new KeyClientBuilder()
			.vaultUrl(vaultUrl)
			.credential(new ManagedIdentityCredentialBuilder().build())
			.buildAsyncClient();
	}
}