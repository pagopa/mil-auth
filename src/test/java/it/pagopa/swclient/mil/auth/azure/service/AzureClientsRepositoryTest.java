/*
 *
 * 
 * 
 */
package it.pagopa.swclient.mil.auth.azure.service;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import com.azure.core.util.BinaryData;
import com.azure.storage.blob.BlobAsyncClient;
import com.azure.storage.blob.BlobContainerAsyncClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.mutiny.helpers.test.UniAssertSubscriber;
import it.pagopa.swclient.mil.auth.bean.Client;
import it.pagopa.swclient.mil.auth.util.AuthError;
import reactor.core.publisher.Mono;

/**
 * 
 * @author Antonio Tarricone
 */
@QuarkusTest
@TestInstance(Lifecycle.PER_CLASS)
class AzureClientsRepositoryTest {
	/**
	 * 
	 * @throws JsonProcessingException
	 */
	@Test
	void givenClientFile_whenGetClient_thenReturnClient() throws JsonProcessingException {
		/*
		 * Data preparation.
		 */
		final String clientId = "3965df56-ca9a-49e5-97e8-061433d4a25b";
		final String fileName = String.format("clients/%s.json", clientId);
		final Client client = new Client(clientId, "POS", "aGw/h/8Fm9S2aNvlvIaxJyhKP67ZU4FEm6mDVhL3aEVrahXFif9x2BkQ4OY87Z9tWVyWbSB/JeztYVmTshrFWQ==", "G3oYMwnLVR9+m7WB4/pvoVeHxzsTdeyhndpVoruHzog=", "VAS Layer");
		final String json = new ObjectMapper().writeValueAsString(client);

		/*
		 * Setup mocks.
		 */
		BlobAsyncClient blobAsyncClient = mock(BlobAsyncClient.class);
		when(blobAsyncClient.downloadContent())
			.thenReturn(Mono.just(BinaryData.fromString(json)));

		BlobContainerAsyncClient blobClient = mock(BlobContainerAsyncClient.class);
		when(blobClient.getBlobAsyncClient(fileName))
			.thenReturn(blobAsyncClient);

		/*
		 * Test.
		 */
		AzureClientsRepository repository = new AzureClientsRepository(blobClient);
		repository.getClient(clientId)
			.subscribe().withSubscriber(UniAssertSubscriber.create())
			.awaitItem()
			.assertItem(client);
	}

	/**
	 * 
	 */
	@Test
	void givenErroFromAzure_whenGetClient_thenReturnFailure() {
		/*
		 * Data preparation.
		 */
		final String clientId = "3965df56-ca9a-49e5-97e8-061433d4a25b";
		final String fileName = String.format("clients/%s.json", clientId);

		/*
		 * Setup mocks.
		 */
		BlobAsyncClient blobAsyncClient = mock(BlobAsyncClient.class);
		when(blobAsyncClient.downloadContent())
			.thenReturn(Mono.error(new Exception("azure exception")));

		BlobContainerAsyncClient blobClient = mock(BlobContainerAsyncClient.class);
		when(blobClient.getBlobAsyncClient(fileName))
			.thenReturn(blobAsyncClient);

		/*
		 * Test.
		 */
		AzureClientsRepository repository = new AzureClientsRepository(blobClient);
		repository.getClient(clientId)
			.subscribe().withSubscriber(UniAssertSubscriber.create())
			.awaitFailure()
			.assertFailedWith(AuthError.class);
	}

	/**
	 * 
	 * @throws JsonProcessingException
	 */
	@Test
	void givenInvalidClientFile_whenGetClient_thenReturnFailure() throws JsonProcessingException {
		/*
		 * Data preparation.
		 */
		final String clientId = "3965df56-ca9a-49e5-97e8-061433d4a25b";
		final String fileName = String.format("clients/%s.json", clientId);
		final String json = "\"\"";

		/*
		 * Setup mocks.
		 */
		BlobAsyncClient blobAsyncClient = mock(BlobAsyncClient.class);
		when(blobAsyncClient.downloadContent())
			.thenReturn(Mono.just(BinaryData.fromString(json)));

		BlobContainerAsyncClient blobClient = mock(BlobContainerAsyncClient.class);
		when(blobClient.getBlobAsyncClient(fileName))
			.thenReturn(blobAsyncClient);

		/*
		 * Test.
		 */
		AzureClientsRepository repository = new AzureClientsRepository(blobClient);
		repository.getClient(clientId)
			.subscribe().withSubscriber(UniAssertSubscriber.create())
			.awaitFailure()
			.assertFailedWith(AuthError.class);
	}
}
