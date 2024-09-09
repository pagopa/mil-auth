/*
 * JwksResource.java
 *
 * 21 mar 2023
 */
package it.pagopa.swclient.mil.auth.resource;

import java.time.Instant;
import java.util.List;
import java.util.OptionalLong;

import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
import it.pagopa.swclient.mil.auth.AuthErrorCode;
import it.pagopa.swclient.mil.auth.bean.PublicKeys;
import it.pagopa.swclient.mil.auth.util.KeyUtils;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.JsonWebKeyOperation;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.JsonWebKeyType;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.service.AzureKeyVaultKeysExtReactiveService;
import it.pagopa.swclient.mil.bean.Errors;
import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.InternalServerErrorException;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.CacheControl;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

/**
 * @author Antonio Tarricone
 */
@Path("/.well-known/jwks.json")
@PermitAll
public class JwksResource {
	/*
	 * Skew in seconds.
	 */
	private static final long SKEW = 5 * 60L;

	/*
	 *
	 */
	private AzureKeyVaultKeysExtReactiveService keyExtService;

	/**
	 * 
	 * @param keyExtService
	 */
	@Inject
	JwksResource(AzureKeyVaultKeysExtReactiveService keyExtService) {
		this.keyExtService = keyExtService;
	}

	/**
	 * @param t
	 * @return
	 */
	private InternalServerErrorException errorOnRetrievingKeys(Throwable t) {
		String message = String.format("[%s] Error searching for keys", AuthErrorCode.ERROR_SEARCHING_FOR_KEYS);
		Log.errorf(t, message);
		return new InternalServerErrorException(Response
			.status(Status.INTERNAL_SERVER_ERROR)
			.entity(new Errors(AuthErrorCode.ERROR_SEARCHING_FOR_KEYS, message))
			.build());
	}

	/**
	 * 
	 * @return
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Uni<Response> get() {
		Log.debug("get - Input parameters: n/a");
		return keyExtService.getKeys(
			KeyUtils.DOMAIN_VALUE,
			List.of(JsonWebKeyOperation.SIGN, JsonWebKeyOperation.VERIFY),
			List.of(JsonWebKeyType.RSA))
			.map(KeyUtils::keyBundle2PublicKey)
			.collect()
			.asList()
			.map(l -> {
				/*
				 * Search the key that expires first to set Cache-Control/max-age
				 */
				OptionalLong minExp = l.stream()
					.map(k -> k.getExp())
					.mapToLong(e -> e)
					.min();

				long maxAge = 0;
				if (minExp.isPresent()) {
					/*
					 * To be sure that will not be cached keys that will expire in a while, subtract SKEW.
					 */
					maxAge = (minExp.getAsLong() - SKEW - Instant.now().getEpochSecond());
				}

				CacheControl cacheControl = new CacheControl();
				if (maxAge > 0) {
					cacheControl.setMaxAge((int) maxAge);
				} else {
					cacheControl.setNoCache(true);
				}

				return Response
					.status(Status.OK)
					.cacheControl(cacheControl)
					.entity(new PublicKeys(l))
					.build();
			})
			.onFailure().transform(this::errorOnRetrievingKeys); // Error while retrieving keys.
	}
}