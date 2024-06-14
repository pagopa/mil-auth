/*
 * AuthDataRepository.java
 *
 * 23 ott 2023
 */
package it.pagopa.swclient.mil.auth.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.quarkus.cache.CacheResult;
import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
import it.pagopa.swclient.mil.auth.AuthErrorCode;
import it.pagopa.swclient.mil.auth.bean.Client;
import it.pagopa.swclient.mil.auth.bean.Role;
import it.pagopa.swclient.mil.auth.bean.User;
import it.pagopa.swclient.mil.auth.util.AuthError;
import it.pagopa.swclient.mil.azureservices.storageblob.service.AzureStorageBlobReactiveService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.Response;

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
	 */
	private ObjectMapper json2obj;

	/**
	 * 
	 * @param blobService
	 */
	AuthDataRepository(AzureStorageBlobReactiveService blobService) {
		this.blobService = blobService;
		json2obj = new ObjectMapper();
	}

	/**
	 * 
	 * @param r
	 * @return
	 */
	private Client response2Client(Response r) {
		if (r.getStatus() == 200) {
			try {
				return json2obj.readValue(r.readEntity(String.class), Client.class);
			} catch (JsonProcessingException e) {
				String message = String.format("[%s] Error deserializing client data", AuthErrorCode.ERROR_SEARCHING_FOR_CLIENT);
				Log.error(message);
				throw new AuthError(AuthErrorCode.ERROR_SEARCHING_FOR_CLIENT, message);
			}
		} else {
			String message = String.format("[%s] Error searching for client: %d", AuthErrorCode.ERROR_SEARCHING_FOR_CLIENT, r.getStatus());
			Log.error(message);
			throw new AuthError(AuthErrorCode.ERROR_SEARCHING_FOR_CLIENT, message);
		}
	}

	/**
	 * 
	 * @param clientId
	 * @return
	 */
	@CacheResult(cacheName = "client-role")
	public Uni<Client> getClient(String clientId) {
		Log.tracef("Search client %s", clientId);
		return blobService.getBlob("clients", clientId + ".json")
			.map(this::response2Client);
	}

	/**
	 * 
	 * @param r
	 * @return
	 */
	private Role response2Role(Response r) {
		if (r.getStatus() == 200) {
			try {
				return json2obj.readValue(r.readEntity(String.class), Role.class);
			} catch (JsonProcessingException e) {
				String message = String.format("[%s] Error deserializing roles data", AuthErrorCode.ERROR_SEARCHING_FOR_ROLES);
				Log.error(message);
				throw new AuthError(AuthErrorCode.ERROR_SEARCHING_FOR_ROLES, message);
			}
		} else {
			String message = String.format("[%s] Error searching for roles: %d", AuthErrorCode.ERROR_SEARCHING_FOR_ROLES, r.getStatus());
			Log.error(message);
			throw new AuthError(AuthErrorCode.ERROR_SEARCHING_FOR_ROLES, message);
		}
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
	public Uni<Role> getRoles(String acquirerId, String channel, String clientId, String merchantId, String terminalId) {
		return blobService.getBlob("roles", acquirerId, channel, clientId, merchantId, terminalId, "roles.json")
			.map(this::response2Role);
	}

	/**
	 * 
	 * @param r
	 * @return
	 */
	private User response2User(Response r) {
		if (r.getStatus() == 200) {
			try {
				return json2obj.readValue(r.readEntity(String.class), User.class);
			} catch (JsonProcessingException e) {
				String message = String.format("[%s] Error deserializing user data", AuthErrorCode.ERROR_SEARCHING_FOR_USER);
				Log.error(message);
				throw new AuthError(AuthErrorCode.ERROR_SEARCHING_FOR_USER, message);
			}
		} else {
			String message = String.format("[%s] Error searching for user: %d", AuthErrorCode.ERROR_SEARCHING_FOR_USER, r.getStatus());
			Log.error(message);
			throw new AuthError(AuthErrorCode.ERROR_SEARCHING_FOR_USER, message);
		}
	}

	/**
	 * 
	 * @param userHash
	 * @return
	 */
	@CacheResult(cacheName = "client-role")
	public Uni<User> getUser(String userHash) {
		return blobService.getBlob("users", userHash + ".json")
			.map(this::response2User);
	}
}