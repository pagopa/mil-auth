/*
 * AuthDataRepository.java
 *
 * 23 ott 2023
 */
package it.pagopa.swclient.mil.auth.dao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.quarkus.cache.CacheResult;
import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
import it.pagopa.swclient.mil.auth.AuthErrorCode;
import it.pagopa.swclient.mil.auth.util.AuthError;
import it.pagopa.swclient.mil.azureservices.storageblob.service.AzureStorageBlobReactiveService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.Response;

/**
 * 
 * @author Antonio Tarricone
 */
@ApplicationScoped
public class UserRepository {
	/*
	 * 
	 */
	private AzureStorageBlobReactiveService blobService;

	/*
	 * 
	 */
	private ObjectMapper json2obj;

	/**
	 * 
	 * @param blobService
	 */
	UserRepository(AzureStorageBlobReactiveService blobService) {
		this.blobService = blobService;
		json2obj = new ObjectMapper();
	}

	/**
	 * 
	 * @param r
	 * @return
	 */
	private UserEntity response2User(Response r) {
		if (r.getStatus() == 200) {
			try {
				return json2obj.readValue(r.readEntity(String.class), UserEntity.class);
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
	public Uni<UserEntity> getUser(String userHash) {
		return blobService.getBlob("users", userHash + ".json")
			.map(this::response2User);
	}
}