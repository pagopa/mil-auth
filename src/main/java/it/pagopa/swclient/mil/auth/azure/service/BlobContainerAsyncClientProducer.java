/*
 * BlobContainerAsyncClientProducer.java
 *
 * 26 mar 2024
 */
package it.pagopa.swclient.mil.auth.azure.service;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import com.azure.identity.ManagedIdentityCredentialBuilder;
import com.azure.storage.blob.BlobContainerAsyncClient;
import com.azure.storage.blob.BlobContainerClientBuilder;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;

/**
 * Producer of BlobContainerAsyncClient.
 * 
 * @author Antonio Tarricone
 */
@ApplicationScoped
public class BlobContainerAsyncClientProducer {
	/*
	 * 
	 */
	private String blobUrl;
	
	/**
	 * 
	 * @param blobUrl
	 */
	BlobContainerAsyncClientProducer(@ConfigProperty(name = "azure-blob-container.url") String blobUrl) {
		this.blobUrl = blobUrl;
	}

	/**
	 * 
	 * @return
	 */
	@Produces
	public BlobContainerAsyncClient createInstance() {
		return new BlobContainerClientBuilder()
			.endpoint(blobUrl)
			.credential(new ManagedIdentityCredentialBuilder().build())
			.buildAsyncClient();
	}
}