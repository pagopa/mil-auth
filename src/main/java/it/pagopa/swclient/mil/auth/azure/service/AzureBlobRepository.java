/*
 * 
 * 
 */
package it.pagopa.swclient.mil.auth.azure.service;

import com.azure.core.util.BinaryData;
import com.azure.storage.blob.BlobContainerAsyncClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
import it.pagopa.swclient.mil.auth.AuthErrorCode;
import it.pagopa.swclient.mil.auth.util.AuthError;
import mutiny.zero.flow.adapters.AdaptersToFlow;
import reactor.core.publisher.Mono;

/**
 * 
 * @author Antonio Tarricone
 */
public class AzureBlobRepository {
	/*
	 * Azure Storage Containter BLOB client.
	 */
	private BlobContainerAsyncClient blobClient;

	/*
	 * JSON -> Object
	 */
	private ObjectMapper objMapper;

	/**
	 * Constructor.
	 * 
	 * @param blobClient
	 */
	AzureBlobRepository(BlobContainerAsyncClient blobClient) {
		this.blobClient = blobClient;
		objMapper = new ObjectMapper();
	}

	/**
	 * 
	 */
	AzureBlobRepository() {
	}

	/**
	 * 
	 * @param <T>
	 * @param fileName
	 * @param clazz
	 * @return
	 */
	protected <T> Uni<T> getFile(String fileName, Class<T> clazz) {
		Mono<T> obj = blobClient.getBlobAsyncClient(fileName)
			.downloadContent()
			.onErrorMap(t -> {
				String message = ErrorFromAzureMessage.get(AuthErrorCode.ERROR_FROM_AZURE_POF_00A);
				Log.errorf(t, message);
				return new AuthError(AuthErrorCode.ERROR_FROM_AZURE_POF_00A, message);
			})
			.map(BinaryData::toString)
			.map(json -> {
				try {
					return objMapper.readValue(json, clazz);
				} catch (JsonProcessingException e) {
					String message = String.format("[%s] JSON deserialization error.", AuthErrorCode.CLIENT_DESERIALIZATION_ERROR);
					Log.errorf(e, message);
					throw new AuthError(AuthErrorCode.JSON_DESERIALIZATION_ERROR, message);
				}
			});

		return Uni.createFrom().publisher(AdaptersToFlow.publisher(obj));
	}
}