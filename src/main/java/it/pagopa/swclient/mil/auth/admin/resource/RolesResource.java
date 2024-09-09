/*
 * RolesResource.java
 *
 * 11 ago 2024
 */
package it.pagopa.swclient.mil.auth.admin.resource;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import com.mongodb.MongoWriteException;

import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.tuples.Tuple2;
import it.pagopa.swclient.mil.auth.admin.AdminErrorCode;
import it.pagopa.swclient.mil.auth.admin.bean.AdminPathParamName;
import it.pagopa.swclient.mil.auth.admin.bean.AdminValidationPattern;
import it.pagopa.swclient.mil.auth.admin.bean.CreateOrUpdateSetOfRolesRequest;
import it.pagopa.swclient.mil.auth.admin.bean.PageMetadata;
import it.pagopa.swclient.mil.auth.admin.bean.PageOfSetOfRoles;
import it.pagopa.swclient.mil.auth.admin.bean.ReadSetOfRolesRequest;
import it.pagopa.swclient.mil.auth.admin.bean.SetOfRoles;
import it.pagopa.swclient.mil.auth.admin.util.RolesConverter;
import it.pagopa.swclient.mil.auth.dao.RolesRepository;
import it.pagopa.swclient.mil.auth.dao.SetOfRolesEntity;
import it.pagopa.swclient.mil.bean.Errors;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import jakarta.ws.rs.BeanParam;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.InternalServerErrorException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

/**
 * <p>
 * Controller for admin operations on roles.
 * </p>
 * 
 * @author Antonio Tarricone
 */
@Path("/admin/roles")
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed({
	"mil-auth-admin"
})
public class RolesResource {
	/*
	 * mil-auth base URL.
	 */
	@ConfigProperty(name = "base-url", defaultValue = "")
	String baseUrl;
	
	/**
	 * <p>
	 * Repository of roles entities.
	 * </p>
	 */
	private RolesRepository repository;

	/**
	 * 
	 * @param repository
	 */
	@Inject
	RolesResource(RolesRepository repository) {
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
				Log.warnf(m, AdminErrorCode.DUPLICATE_ROLES_MSG);
				return new WebApplicationException(Response
					.status(Status.CONFLICT)
					.entity(new Errors(AdminErrorCode.DUPLICATE_ROLES, AdminErrorCode.DUPLICATE_ROLES_MSG))
					.build());
			} else {
				/*
				 * Other error
				 */
				Log.errorf(m, AdminErrorCode.ERROR_STORING_ROLES_MSG);
				return new InternalServerErrorException(Response
					.status(Status.INTERNAL_SERVER_ERROR)
					.entity(new Errors(AdminErrorCode.ERROR_STORING_ROLES, AdminErrorCode.ERROR_STORING_ROLES_MSG))
					.build());
			}
		} else {
			/*
			 * Unexpected error
			 */
			Log.errorf(t, AdminErrorCode.ERROR_CREATING_ROLES_MSG);
			return new InternalServerErrorException(Response
				.status(Status.INTERNAL_SERVER_ERROR)
				.entity(new Errors(AdminErrorCode.ERROR_CREATING_ROLES, AdminErrorCode.ERROR_CREATING_ROLES_MSG))
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
	public Uni<Response> create(@Valid CreateOrUpdateSetOfRolesRequest req) {
		Log.tracef("Create a new list of roles: %s", req.toString());

		String id = UUID.randomUUID().toString();
		Log.debugf("Assigned ID: ", id);

		/*
		 * Store list of roles in the DB.
		 */
		Log.tracef("Store new list of roles in the DB");
		SetOfRolesEntity entity = new SetOfRolesEntity(
			id,
			req.getAcquirerId(),
			req.getChannel(),
			req.getClientId(),
			req.getMerchantId(),
			req.getTerminalId(),
			req.getRoles());

		return repository.persist(entity)
			.map(e -> Response.created(URI.create(baseUrl + "/admin/roles/" + id)).build())
			.invoke(() -> Log.debug("List of roles created successfully"))
			.onFailure().transform(this::onPersistError);
	}

	/*
	 * >>>>>>>>>>>>>>>> READ <<<<<<<<<<<<<<<<
	 */

	/**
	 * 
	 * @param page
	 * @param size
	 * @param tuple
	 * @return
	 */
	private PageOfSetOfRoles tuple2Page(int page, int size, Tuple2<Long, List<SetOfRolesEntity>> tuple) {
		/*
		 * Page metadata
		 */
		Long totalElements = tuple.getItem1();
		long totalPages = (int) Math.ceil(totalElements.doubleValue() / size);
		PageMetadata pageMetadata = new PageMetadata(totalElements, totalPages, page, size);

		/*
		 * Page of data
		 */
		List<SetOfRoles> roles = tuple.getItem2()
			.stream()
			.map(RolesConverter::convert)
			.toList();

		return new PageOfSetOfRoles(roles, pageMetadata);
	}

	/**
	 * 
	 * @param t
	 * @return
	 */
	private WebApplicationException onFindError(Throwable t) {
		Log.errorf(t, AdminErrorCode.ERROR_READING_ROLES_MSG);
		return new InternalServerErrorException(Response
			.status(Status.INTERNAL_SERVER_ERROR)
			.entity(new Errors(AdminErrorCode.ERROR_READING_ROLES, AdminErrorCode.ERROR_READING_ROLES_MSG))
			.build());
	}

	/**
	 * 
	 * @return
	 */
	private WebApplicationException onNotFoundError() {
		Log.error(AdminErrorCode.ROLES_NOT_FOUND_MSG);
		return new NotFoundException(Response
			.status(Status.NOT_FOUND)
			.entity(new Errors(AdminErrorCode.ROLES_NOT_FOUND, AdminErrorCode.ROLES_NOT_FOUND_MSG))
			.build());
	}

	/**
	 * 
	 * @param req
	 * @return
	 */
	@GET
	public Uni<PageOfSetOfRoles> read(@Valid @BeanParam ReadSetOfRolesRequest req) {
		Log.tracef("Read set of roles: %s", req);
		return repository.findByParameters(
			req.getPage(),
			req.getSize(),
			req.getAcquirerId(),
			req.getChannel(),
			req.getClientId(),
			req.getMerchantId(),
			req.getTerminalId())
			.map(tuple -> tuple2Page(req.getPage(), req.getSize(), tuple))
			.invoke(res -> Log.debugf("Set of roles read successfully: %s", res.toString()))
			.onFailure().transform(this::onFindError);
	}

	@GET
	@Path("/{" + AdminPathParamName.SET_OF_ROLES_ID + "}")
	public Uni<SetOfRoles> read(
		@PathParam(AdminPathParamName.SET_OF_ROLES_ID)
		@Pattern(regexp = AdminValidationPattern.SET_OF_ROLES_ID, message = AdminErrorCode.SET_OF_ROLES_ID_MUST_MATCH_REGEXP_MSG) String id) {

		Log.tracef("Read set of roles %s", id);
		return repository
			.findBySetOfRolesId(id)
			.onFailure().transform(this::onFindError)
			.map(opt -> opt.orElseThrow(this::onNotFoundError))
			.map(RolesConverter::convert)
			.invoke(res -> Log.debugf("Set of roles %s read successfully: %s", id, res.toString()));
	}

	/*
	 * >>>>>>>>>>>>>>>> UPDATE <<<<<<<<<<<<<<<<
	 */

	/**
	 * 
	 * @param t
	 * @return
	 */
	private WebApplicationException onUpdateError(Throwable t) {
		Log.errorf(t, AdminErrorCode.ERROR_UPDATING_ROLES_MSG);
		return new InternalServerErrorException(Response
			.status(Status.INTERNAL_SERVER_ERROR)
			.entity(new Errors(AdminErrorCode.ERROR_UPDATING_ROLES, AdminErrorCode.ERROR_UPDATING_ROLES_MSG))
			.build());
	}

	/**
	 * 
	 * @param req
	 * @return
	 */
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/{" + AdminPathParamName.SET_OF_ROLES_ID + "}")
	public Uni<Response> update(
		@PathParam(AdminPathParamName.SET_OF_ROLES_ID)
		@Pattern(regexp = AdminValidationPattern.SET_OF_ROLES_ID, message = AdminErrorCode.SET_OF_ROLES_ID_MUST_MATCH_REGEXP_MSG) String id,
		CreateOrUpdateSetOfRolesRequest req) {

		Log.tracef("Update set of roles %s: %s", id, req);
		return repository
			.updateBySetOfRolesId(
				id,
				req.getAcquirerId(),
				req.getChannel(),
				req.getClientId(),
				req.getMerchantId(),
				req.getTerminalId(),
				req.getRoles())
			.onFailure().transform(this::onUpdateError)
			.map(n -> {
				if (n > 0) {
					Log.debugf("Set of roles %s updated successfully", id);
					return Response.noContent().build();
				} else {
					throw onNotFoundError();
				}
			});
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
		Log.errorf(t, AdminErrorCode.ERROR_DELETING_ROLES_MSG);
		return new InternalServerErrorException(Response
			.status(Status.INTERNAL_SERVER_ERROR)
			.entity(new Errors(AdminErrorCode.ERROR_DELETING_ROLES, AdminErrorCode.ERROR_DELETING_ROLES_MSG))
			.build());
	}

	/**
	 * 
	 * @param req
	 * @return
	 */
	@DELETE
	@Path("/{" + AdminPathParamName.SET_OF_ROLES_ID + "}")
	public Uni<Response> delete(
		@PathParam(AdminPathParamName.SET_OF_ROLES_ID)
		@Pattern(regexp = AdminValidationPattern.SET_OF_ROLES_ID, message = AdminErrorCode.SET_OF_ROLES_ID_MUST_MATCH_REGEXP_MSG) String id) {

		Log.tracef("Delete roles %s", id);
		return repository
			.deleteBySetOfRolesId(id)
			.onFailure().transform(this::onDeleteError)
			.map(n -> {
				if (n > 0) {
					Log.debugf("Set of roles %s deleted successfully", id);
					return Response.noContent().build();
				} else {
					throw onNotFoundError();
				}
			});
	}
}
