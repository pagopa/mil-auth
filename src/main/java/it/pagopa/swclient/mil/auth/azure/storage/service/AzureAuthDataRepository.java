/*
 * 
 */
package it.pagopa.swclient.mil.auth.azure.storage.service;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
import it.pagopa.swclient.mil.auth.AuthErrorCode;
import it.pagopa.swclient.mil.auth.azure.auth.service.AzureAuthService;
import it.pagopa.swclient.mil.auth.azure.storage.client.AzureAuthDataRepositoryClient;
import it.pagopa.swclient.mil.auth.bean.Client;
import it.pagopa.swclient.mil.auth.bean.Role;
import it.pagopa.swclient.mil.auth.bean.User;
import it.pagopa.swclient.mil.auth.service.AuthDataRepository;
import it.pagopa.swclient.mil.auth.util.AuthError;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 * @author Antonio Tarricone
 */
@ApplicationScoped
public class AzureAuthDataRepository implements AuthDataRepository {
	/*
	 *
	 */
	private AzureAuthService authService;

	/*
	 *
	 */
	private AzureAuthDataRepositoryClient dataRepo;

	/**
	 * 
	 * @param dataRepo
	 * @param authService
	 */
	@Inject
	AzureAuthDataRepository(
		@RestClient AzureAuthDataRepositoryClient dataRepo,
		AzureAuthService authService) {
		this.dataRepo = dataRepo;
		this.authService = authService;
	}

	/**
	 * Returns the authorization header value, invoking identity endpoint.
	 * 
	 * @return
	 */
	private Uni<String> getAuthorization() {
		return authService.getAccessTokenForStorage()
			.map(x -> {
				String t = x.getToken();
				if (t != null) {
					Log.debug("Successfully authenticated.");
					return t;
				} else {
					String message = String.format("[%s] Azure access token not valid.", AuthErrorCode.AZURE_ACCESS_TOKEN_IS_NULL);
					Log.error(message);
					throw new AuthError(AuthErrorCode.AZURE_ACCESS_TOKEN_IS_NULL, message);
				}
			});
	}

	/**
	 * 
	 */
	@Override
	public Uni<Client> getClient(String clientId) {
		return getAuthorization().chain(authorization -> dataRepo.getClient(authorization, clientId));
	}

	/**
	 * 
	 */
	@Override
	public Uni<Role> getRoles(String acquirerId, String channel, String clientId, String merchantId, String terminalId) {
		return getAuthorization().chain(authorization -> dataRepo.getRoles(authorization, acquirerId, channel, clientId, merchantId, terminalId));
	}

	/**
	 * 
	 */
	@Override
	public Uni<User> getUser(String userHash) {
		return getAuthorization().chain(authorization -> dataRepo.getUser(authorization, userHash));
	}
}
