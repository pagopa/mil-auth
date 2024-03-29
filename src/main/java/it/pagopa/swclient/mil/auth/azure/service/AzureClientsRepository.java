/*
 * 
 * 
 */
package it.pagopa.swclient.mil.auth.azure.service;

import com.azure.storage.blob.BlobContainerAsyncClient;

import io.smallrye.mutiny.Uni;
import it.pagopa.swclient.mil.auth.bean.Client;
import it.pagopa.swclient.mil.auth.service.ClientsRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 * 
 * @author Antonio Tarricone
 */
@ApplicationScoped
public class AzureClientsRepository extends AzureBlobRepository implements ClientsRepository {
	/**
	 * Constructor.
	 * 
	 * @param blobClient
	 */
	@Inject
	AzureClientsRepository(BlobContainerAsyncClient blobClient) {
		super(blobClient);
	}

	/**
	 * 
	 */
	@Override
	public Uni<Client> getClient(String clientId) {
		String fileName = String.format("clients/%s.json", clientId);
		return getFile(fileName, Client.class);
	}
}