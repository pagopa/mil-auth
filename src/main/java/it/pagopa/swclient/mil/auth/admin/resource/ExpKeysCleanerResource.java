/*
 * ExpKeysCleanerResource.java
 *
 * 22 lug 2024
 */
package it.pagopa.swclient.mil.auth.admin.resource;

import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
import it.pagopa.swclient.mil.auth.admin.AuthAdminErrorCode;
import it.pagopa.swclient.mil.auth.admin.bean.DeletedKeys;
import it.pagopa.swclient.mil.auth.util.KeyUtils;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.DeletedKeyBundle;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.bean.JsonWebKey;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.service.AzureKeyVaultKeysExtReactiveService;
import it.pagopa.swclient.mil.bean.Errors;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.InternalServerErrorException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

/**
 * 
 * @author Antonio Tarricone
 */
@Path("/admin/cleanexpkeys")
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed({
	"mil-auth-admin"
})
public class ExpKeysCleanerResource {
	/*
	 *
	 */
	private AzureKeyVaultKeysExtReactiveService keyExtService;

	/**
	 * 
	 * @param keyExtService
	 */
	@Inject
	ExpKeysCleanerResource(AzureKeyVaultKeysExtReactiveService keyExtService) {
		this.keyExtService = keyExtService;
	}

	/**
	 * 
	 * @param t
	 * @return
	 */
	private InternalServerErrorException onError(Throwable t) {
		Log.errorf(t, AuthAdminErrorCode.ERROR_DELETING_EXP_KEYS_MSG);
		return new InternalServerErrorException(Response
			.status(Status.INTERNAL_SERVER_ERROR)
			.entity(new Errors(AuthAdminErrorCode.ERROR_DELETING_EXP_KEYS, AuthAdminErrorCode.ERROR_DELETING_EXP_KEYS_MSG))
			.build());
	}

	/**
	 * 
	 * @return
	 */
	@POST
	public Uni<DeletedKeys> clean() {
		Log.trace("Delete expired key");
		return keyExtService.deleteExpiredKeys(KeyUtils.DOMAIN_VALUE)
			.map(DeletedKeyBundle::getKey)
			.map(JsonWebKey::getKid)
			.map(KeyUtils::azureKid2MyKid)
			.collect()
			.asList()
			.invoke(l -> Log.debugf("Deleted %d expired key/s", l.size()))
			.map(DeletedKeys::new)
			.onFailure().transform(this::onError);
	}
}