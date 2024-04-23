/*
 * AzureIdentityService.java
 *
 * 12 apr 2024
 */
package it.pagopa.swclient.mil.auth.azure.identity.service;

import java.time.Instant;
import java.util.EnumMap;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import io.smallrye.mutiny.Uni;
import it.pagopa.swclient.mil.auth.azure.identity.bean.AccessToken;
import it.pagopa.swclient.mil.auth.azure.identity.bean.Scope;
import it.pagopa.swclient.mil.auth.azure.identity.client.AzureIdentityClient;
import it.pagopa.swclient.mil.auth.util.UniGenerator;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * 
 * @author Antonio Tarricone
 */
@ApplicationScoped
public class AzureIdentityService {
	/*
	 * 
	 */
	private AzureIdentityClient client;

	/*
	 * 
	 */
	private EnumMap<Scope, AccessToken> accessTokens;

	/**
	 * 
	 * @param client
	 */
	AzureIdentityService(@RestClient AzureIdentityClient client) {
		this.client = client;
		accessTokens = new EnumMap<>(Scope.class);
	}

	/**
	 * 
	 * @param scope
	 * @return
	 */
	public Uni<AccessToken> getAccessToken(Scope scope) {
		AccessToken accessToken = accessTokens.get(scope);
		if (accessToken != null && accessToken.getExpiresOn() > Instant.now().getEpochSecond()) {
			return UniGenerator.item(accessToken);
		} else {
			return client.getAccessToken(scope)
				.invoke(i -> accessTokens.put(scope, i));
		}
	}
}