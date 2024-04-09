/*
 * BlobContainerAsyncClientProducer.java
 *
 * 26 mar 2024
 */
package it.pagopa.swclient.mil.auth.azure.producer;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import com.azure.identity.ManagedIdentityCredentialBuilder;
import com.azure.storage.blob.BlobContainerAsyncClient;
import com.azure.storage.blob.BlobContainerClientBuilder;

import io.quarkus.arc.DefaultBean;
import io.quarkus.arc.profile.IfBuildProfile;
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
	@DefaultBean
	public BlobContainerAsyncClient createInstance() {
		return new BlobContainerClientBuilder()
			.endpoint(blobUrl)
			.credential(new ManagedIdentityCredentialBuilder().build())
			.buildAsyncClient();
	}
	
	/**
	 * 
	 * @return
	 */
	@Produces
	@IfBuildProfile("dev")
	public BlobContainerAsyncClient createInstanceForDev() {
		return new BlobContainerClientBuilder()
			.endpoint(blobUrl)
			.credential(new ManagedIdentityCredentialBuilder().build())
			.buildAsyncClient();
	}
}