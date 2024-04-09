/*
 * TerminalRegistryClientService.java
 *
 * 2 apr 2024
 */
package it.pagopa.swclient.mil.auth.client;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import io.quarkus.cache.CacheKey;
import io.quarkus.cache.CacheResult;
import io.smallrye.mutiny.Uni;
import it.pagopa.swclient.mil.auth.bean.Terminal;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * This service adds a cache on top of TerminalRegistryClient.
 * 
 * @author Antonio Tarricone
 */
@ApplicationScoped
public class TerminalRegistryClientService {
	/*
	 * 
	 */
	@RestClient
	TerminalRegistryClient registry;

	/**
	 * @see it.pagopa.swclient.mil.auth.client.TerminalRegistryClientService#find(String, String, String)
	 */
	@CacheResult(cacheName = "terminal-registry-cache")
	public Uni<Terminal> find(String accessToken, @CacheKey String terminalHandlerId, @CacheKey String terminalId) {
		return registry.find(accessToken, terminalHandlerId, terminalId);
	}

	/**
	 * @see it.pagopa.swclient.mil.auth.client.TerminalRegistryClientService#find(String, String)
	 */
	@CacheResult(cacheName = "terminal-registry-cache")
	public Uni<Terminal> find(String accessToken, @CacheKey String terminalUuid) {
		return registry.find(accessToken, terminalUuid);
	}
}