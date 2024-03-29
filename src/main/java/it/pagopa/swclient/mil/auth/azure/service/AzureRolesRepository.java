/*
 * 
 * 
 */
package it.pagopa.swclient.mil.auth.azure.service;

import com.azure.storage.blob.BlobContainerAsyncClient;

import io.smallrye.mutiny.Uni;
import it.pagopa.swclient.mil.auth.bean.Roles;
import it.pagopa.swclient.mil.auth.service.RolesRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 * 
 * @author Antonio Tarricone
 */
@ApplicationScoped
public class AzureRolesRepository extends AzureBlobRepository implements RolesRepository {
	/**
	 * Constructor.
	 */
	@Inject
	AzureRolesRepository(BlobContainerAsyncClient blobClient) {
		super(blobClient);
	}

	/**
	 * 
	 */
	@Override
	public Uni<Roles> getRoles(String acquirerId, String channel, String clientId, String merchantId, String terminalId) {
		String fileName = String.format("roles/%s/%s/%s/%s/%s/roles.json", acquirerId, channel, clientId, merchantId, terminalId);
		return getFile(fileName, Roles.class);
	}
}