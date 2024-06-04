/*
 * AuthDataRepository.java
 *
 * 23 ott 2023
 */
package it.pagopa.swclient.mil.auth.service;

import io.quarkus.cache.CacheResult;
import io.smallrye.mutiny.Uni;
import it.pagopa.swclient.mil.auth.bean.Client;
import it.pagopa.swclient.mil.auth.bean.Role;
import it.pagopa.swclient.mil.auth.bean.User;
import it.pagopa.swclient.mil.azureservices.storageblob.service.AzureStorageBlobReactiveService;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * 
 * @author Antonio Tarricone
 */
@ApplicationScoped
public class AuthDataRepository {
	/*
	 * 
	 */
	private AzureStorageBlobReactiveService blobService;

	/**
	 * 
	 * @param blobService
	 */
	AuthDataRepository(AzureStorageBlobReactiveService blobService) {
		this.blobService = blobService;
	}

	/**
	 * 
	 * @param clientId
	 * @return
	 */
	@CacheResult(cacheName = "client-role")
	public Uni<Client> getClient(String clientId) {
		return blobService.getBlob("clients", clientId+".json")
			.map(r -> r.readEntity(Client.class));
	}

	/**
	 * 
	 * @param acquirerId
	 * @param channel
	 * @param merchantId
	 * @param clientId
	 * @param terminalId
	 * @return
	 */
	@CacheResult(cacheName = "client-role")
	public Uni<Role> getRoles(String acquirerId, String channel, String clientId, String merchantId, String terminalId) {
		return blobService.getBlob("roles", acquirerId, channel, clientId, merchantId, terminalId, "roles.json")
			.map(r -> r.readEntity(Role.class));
	}

	/**
	 * 
	 * @param userHash
	 * @return
	 */
	@CacheResult(cacheName = "client-role")
	public Uni<User> getUser(String userHash) {
		return blobService.getBlob("users", userHash+".json")
			.map(r -> r.readEntity(User.class));
	}
}