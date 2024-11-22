/*
 * UserResource.java
 *
 * 20 nov 2024
 */
package it.pagopa.swclient.mil.auth.admin.resource;

import java.net.URI;
import java.util.UUID;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import com.mongodb.MongoWriteException;

import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
import it.pagopa.swclient.mil.auth.AuthErrorCode;
import it.pagopa.swclient.mil.auth.admin.AdminErrorCode;
import it.pagopa.swclient.mil.auth.admin.bean.AdminQueryParamName;
import it.pagopa.swclient.mil.auth.admin.bean.CreateUserRequest;
import it.pagopa.swclient.mil.auth.admin.bean.CreateUserResponse;
import it.pagopa.swclient.mil.auth.bean.AuthValidationPattern;
import it.pagopa.swclient.mil.auth.dao.UserEntity;
import it.pagopa.swclient.mil.auth.dao.UserRepository;
import it.pagopa.swclient.mil.auth.util.SecretTriplet;
import it.pagopa.swclient.mil.bean.Errors;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.InternalServerErrorException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

/**
 * 
 * @author antonio.tarricone
 */
@Path("/admin/users")
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed({
	"mil-auth-admin"
})
public class UserResource {
	/*
	 * Lenght of generated password.
	 */
	private static final int PASSWORD_LEN = 12;

	/*
	 * mil-auth base URL.
	 */
	@ConfigProperty(name = "base-url", defaultValue = "")
	String baseUrl;

	/*
	 * 
	 */
	private UserRepository repository;

	/**
	 * 
	 * @param repository
	 */
	@Inject
	UserResource(UserRepository repository) {
		this.repository = repository;
	}

	/*
	 * >>>>>>>>>>>>>>>> CREATE <<<<<<<<<<<<<<<<
	 */

	/**
	 * 
	 * @param t
	 * @return
	 */
	private WebApplicationException onPersistError(Throwable t) {
		if (t instanceof MongoWriteException m) {
			if (m.getCode() == 11000) {
				/*
				 * Duplicate key
				 */
				Log.warnf(m, AdminErrorCode.DUPLICATE_USER_MSG);
				return new WebApplicationException(
					Response
						.status(Status.CONFLICT)
						.entity(new Errors(AdminErrorCode.DUPLICATE_USER, AdminErrorCode.DUPLICATE_USER_MSG))
						.build());
			} else {
				/*
				 * Other error
				 */
				Log.errorf(m, AdminErrorCode.ERROR_STORING_USER_MSG);
				return new InternalServerErrorException(
					Response
						.status(Status.INTERNAL_SERVER_ERROR)
						.entity(new Errors(AdminErrorCode.ERROR_STORING_USER, AdminErrorCode.ERROR_STORING_USER_MSG))
						.build());
			}
		} else {
			/*
			 * Unexpected error
			 */
			Log.errorf(t, AdminErrorCode.ERROR_CREATING_CLIENT_MSG);
			return new InternalServerErrorException(
				Response
					.status(Status.INTERNAL_SERVER_ERROR)
					.entity(new Errors(AdminErrorCode.ERROR_CREATING_CLIENT, AdminErrorCode.ERROR_CREATING_CLIENT_MSG))
					.build());
		}
	}

	/**
	 * 
	 * @param req
	 * @return
	 */
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Uni<Response> create(@Valid CreateUserRequest req) {
		Log.tracef("Create a new user: %s", req.toString());

		/*
		 * Generate random user id.
		 */
		Log.tracef("Generate random user id");
		String userId = UUID.randomUUID().toString();

		/*
		 * Generate secret triplet (password, salt and hash).
		 */
		Log.tracef("Generate password, salt and hash");
		SecretTriplet triplet = SecretTriplet.generate(PASSWORD_LEN);

		/*
		 * Store client in the DB.
		 */
		Log.tracef("Store new user in the DB");
		UserEntity entity = new UserEntity(
			userId,
			req.getUsername(),
			req.getChannel(),
			triplet.getSalt(),
			triplet.getHash(),
			req.getAcquirerId(),
			req.getMerchantId());

		return repository
			.persist(entity)
			.map(e -> {
				CreateUserResponse res = new CreateUserResponse(triplet.getSecret());
				Log.debugf("User created successfully: %s", res.toString());
				return Response.created(URI.create(baseUrl + "/admin/users/" + userId))
					.entity(res)
					.build();
			})
			.onFailure().transform(this::onPersistError);
	}

	/*
	 * >>>>>>>>>>>>>>>> DELETE <<<<<<<<<<<<<<<<
	 */

	/**
	 * 
	 * @param t
	 * @return
	 */
	private WebApplicationException onDeleteError(Throwable t) {
		Log.errorf(t, AdminErrorCode.ERROR_DELETING_USER_MSG);
		return new InternalServerErrorException(Response
			.status(Status.INTERNAL_SERVER_ERROR)
			.entity(new Errors(AdminErrorCode.ERROR_DELETING_USER, AdminErrorCode.ERROR_DELETING_USER_MSG))
			.build());
	}

	/**
	 * 
	 * @return
	 */
	private WebApplicationException onNotFoundError() {
		Log.error(AuthErrorCode.USER_NOT_FOUND_MSG);
		return new NotFoundException(Response
			.status(Status.NOT_FOUND)
			.entity(new Errors(AuthErrorCode.USER_NOT_FOUND, AuthErrorCode.USER_NOT_FOUND_MSG))
			.build());
	}

	/**
	 * 
	 * @param username
	 * @return
	 */
	@DELETE
	public Uni<Response> delete(
		@QueryParam(AdminQueryParamName.USERNAME)
		@Pattern(regexp = AuthValidationPattern.USERNAME, message = AuthErrorCode.USERNAME_MUST_MATCH_REGEXP_MSG) String username) {
		Log.trace("Delete user");
		return repository.deleteByUsername(username)
			.onFailure().transform(this::onDeleteError)
			.map(n -> {
				if (n > 0) {
					Log.debug("User deleted successfully");
					return Response.noContent().build();
				} else {
					throw onNotFoundError();
				}
			});
	}
}