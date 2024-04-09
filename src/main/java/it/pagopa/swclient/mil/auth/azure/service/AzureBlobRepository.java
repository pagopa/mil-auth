/*
 * AzureBlobRepository.java
 * 
 * 29 mar 2024
 */
package it.pagopa.swclient.mil.auth.azure.service;

import com.azure.core.util.BinaryData;
import com.azure.storage.blob.BlobAsyncClient;
import com.azure.storage.blob.BlobContainerAsyncClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
import it.pagopa.swclient.mil.auth.bean.AuthErrorCode;
import it.pagopa.swclient.mil.auth.util.AuthError;
import it.pagopa.swclient.mil.auth.util.AuthException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import mutiny.zero.flow.adapters.AdaptersToFlow;
import reactor.core.publisher.Mono;

/**
 * 
 * @author Antonio Tarricone
 */
@ApplicationScoped
public class AzureBlobRepository {
	/*
	 * Azure Storage Containter BLOB client.
	 */
	private BlobContainerAsyncClient blobContainerClient;

	/*
	 * JSON -> Object
	 */
	private ObjectMapper objMapper;

	/**
	 * Constructor.
	 * 
	 * @param blobContainerClient
	 */
	@Inject
	AzureBlobRepository(BlobContainerAsyncClient blobContainerClient) {
		this.blobContainerClient = blobContainerClient;
		objMapper = new ObjectMapper();
	}

	/**
	 * 
	 * @param <T>
	 * @param fileName
	 * @param clazz
	 * @return
	 */
	<T> Uni<T> getFile(String fileName, Class<T> clazz) {
		Log.debugf("Get [%s]", fileName);
		BlobAsyncClient blobClient = blobContainerClient.getBlobAsyncClient(fileName);
		Mono<T> obj = blobClient.exists()
			.flatMap(isExistent -> {
				if (isExistent.equals(Boolean.FALSE)) {
					Log.warnf(AuthErrorCode.ROLES_NOT_FOUND_MSG + " [%s]", fileName);
					throw new AuthException(AuthErrorCode.ROLES_NOT_FOUND, AuthErrorCode.ROLES_NOT_FOUND_MSG);
				} else {
					return blobClient.downloadContent()
						.onErrorMap(t -> {
							Log.errorf(t, AuthErrorCode.ERROR_CREATING_BLOB_ASYNC_CLIENT_MSG);
							return new AuthError(AuthErrorCode.ERROR_CREATING_BLOB_ASYNC_CLIENT, AuthErrorCode.ERROR_CREATING_BLOB_ASYNC_CLIENT_MSG);
						})
						.map(BinaryData::toString)
						.map(json -> {
							try {
								Log.infof("Retrieved [%s]", fileName);
								return objMapper.readValue(json, clazz);
							} catch (JsonProcessingException e) {
								Log.errorf(e, AuthErrorCode.JSON_DESERIALIZATION_ERROR_MSG);
								throw new AuthError(AuthErrorCode.JSON_DESERIALIZATION_ERROR, AuthErrorCode.JSON_DESERIALIZATION_ERROR_MSG);
							}
						});
				}
			});

		return Uni.createFrom().publisher(AdaptersToFlow.publisher(obj));
	}
}