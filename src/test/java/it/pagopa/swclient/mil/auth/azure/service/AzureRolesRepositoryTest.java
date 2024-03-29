/*
 *
 * 
 * 
 */
package it.pagopa.swclient.mil.auth.azure.service;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

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
import it.pagopa.swclient.mil.auth.bean.Roles;
import reactor.core.publisher.Mono;

/**
 * 
 * @author Antonio Tarricone
 */
@QuarkusTest
@TestInstance(Lifecycle.PER_CLASS)
class AzureRolesRepositoryTest {
	/**
	 * 
	 * @throws JsonProcessingException
	 */
	@Test
	void givenRoleFile_whenGetClient_thenReturnRole() throws JsonProcessingException {
		/*
		 * Data preparation.
		 */
		final String acquirerId = "4585625";
		final String channel = "POS";
		final String merchantId = "28405fHfk73x88D";
		final String terminalId = "NA";
		final List<String> roles = List.of("NoticePayer", "SlavePos", "PayWithIDPay");
		final String clientId = "3965df56-ca9a-49e5-97e8-061433d4a25b";
		final String fileName = String.format("roles/%s/%s/%s/%s/%s/roles.json", acquirerId, channel, clientId, merchantId, terminalId);
		final Roles role = new Roles(acquirerId, channel, clientId, merchantId, terminalId, roles);
		final String json = new ObjectMapper().writeValueAsString(role);

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
		AzureRolesRepository repository = new AzureRolesRepository(blobClient);
		repository.getRoles(acquirerId, channel, clientId, merchantId, terminalId)
			.subscribe().withSubscriber(UniAssertSubscriber.create())
			.awaitItem()
			.assertItem(role);
	}
}
