/*
 * 
 */
package it.pagopa.swclient.mil.auth.azure.storage.service;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import com.azure.core.util.BinaryData;
import com.azure.identity.ManagedIdentityCredentialBuilder;
import com.azure.storage.blob.BlobContainerAsyncClient;
import com.azure.storage.blob.BlobContainerClientBuilder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
import it.pagopa.swclient.mil.auth.AuthErrorCode;
import it.pagopa.swclient.mil.auth.bean.Client;
import it.pagopa.swclient.mil.auth.bean.Role;
import it.pagopa.swclient.mil.auth.service.AuthDataRepository;
import it.pagopa.swclient.mil.auth.util.AuthError;
import jakarta.enterprise.context.ApplicationScoped;
import mutiny.zero.flow.adapters.AdaptersToFlow;
import reactor.core.publisher.Mono;

/**
 * @author Antonio Tarricone
 */
@ApplicationScoped
public class AzureAuthDataRepository implements AuthDataRepository {
	/*
	 * Azure Storage Container BLOB URL.
	 */
	@ConfigProperty(name = "azure-blob.url")
	String blobUrl;

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
	 */
	AzureAuthDataRepository() {
		blobClient = new BlobContainerClientBuilder()
			.endpoint(blobUrl)
			.credential(new ManagedIdentityCredentialBuilder().build())
			.buildAsyncClient();
		objMapper = new ObjectMapper();
	}
	
	/**
	 * 
	 * @param <T>
	 * @param fileName
	 * @param clazz
	 * @return
	 */
	private <T> Uni<T> getFile(String fileName, Class<T> clazz) {
		Mono<T> obj = blobClient.getBlobAsyncClient(fileName)
			.downloadContent()
			.onErrorMap(t -> {
				String message = String.format("[%s] Error from Azure.", AuthErrorCode.ERROR_FROM_AZURE);
				Log.errorf(t, message);
				return new AuthError(AuthErrorCode.ERROR_FROM_AZURE, message);
			})
			.map(BinaryData::toString)
			.map(json -> {
				try {
					return objMapper.readValue(json, clazz);
				} catch (JsonProcessingException e) {
					String message = String.format("[%s] JSON deserialization error.", AuthErrorCode.CLIENT_DESERIALIZATION_ERROR);
					Log.errorf(e, message);
					throw new AuthError(AuthErrorCode.ERROR_FROM_AZURE, message);
				}
			});

		return Uni.createFrom().publisher(AdaptersToFlow.publisher(obj));
	}

	/**
	 * 
	 */
	@Override
	public Uni<Client> getClient(String clientId) {
		String fileName = String.format("clients/%s.json", clientId);
		return getFile(fileName, Client.class);
	}

	/**
	 * 
	 */
	@Override
	public Uni<Role> getRoles(String acquirerId, String channel, String clientId, String merchantId, String terminalId) {
		String fileName = String.format("roles/%s/%s/%s/%s/%s/roles.json", acquirerId, channel, clientId, merchantId, terminalId);
		return getFile(fileName, Role.class);
	}
}