/*
 * ClientResource.java
 *
 * 25 lug 2024
 */
package it.pagopa.swclient.mil.auth.admin.resource;

import java.util.List;
import java.util.UUID;

import com.mongodb.MongoWriteException;

import io.quarkus.logging.Log;
import io.quarkus.panache.common.Sort;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.tuples.Tuple2;
import it.pagopa.swclient.mil.ErrorCode;
import it.pagopa.swclient.mil.auth.admin.AuthAdminErrorCode;
import it.pagopa.swclient.mil.auth.admin.bean.AdminQueryParamName;
import it.pagopa.swclient.mil.auth.admin.bean.Client;
import it.pagopa.swclient.mil.auth.admin.bean.CreateClientRequest;
import it.pagopa.swclient.mil.auth.admin.bean.CreateClientResponse;
import it.pagopa.swclient.mil.auth.admin.bean.PageMetadata;
import it.pagopa.swclient.mil.auth.admin.bean.PageOfClients;
import it.pagopa.swclient.mil.auth.dao.ClientEntity;
import it.pagopa.swclient.mil.auth.dao.ClientRepository;
import it.pagopa.swclient.mil.auth.util.SecretTriplet;
import it.pagopa.swclient.mil.bean.Errors;
import it.pagopa.swclient.mil.bean.HeaderParamName;
import it.pagopa.swclient.mil.bean.ValidationPattern;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.InternalServerErrorException;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import jakarta.ws.rs.core.UriInfo;

/**
 * 
 * @author Antonio Tarricone
 */
@Path("/admin/clients")
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed({
	"mil-auth-admin"
})
public class ClientResource {
	/*
	 * 
	 */
	private ClientRepository repository;

	/**
	 * 
	 * @param repository
	 */
	@Inject
	ClientResource(ClientRepository repository) {
		this.repository = repository;
	}

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
				Log.warnf(m, AuthAdminErrorCode.DUPLICATE_CLIENT_ID_MSG);
				return new WebApplicationException(Response
					.status(Status.CONFLICT)
					.entity(new Errors(AuthAdminErrorCode.DUPLICATE_CLIENT_ID, AuthAdminErrorCode.DUPLICATE_CLIENT_ID_MSG))
					.build());
			} else {
				/*
				 * Other error
				 */
				Log.errorf(m, AuthAdminErrorCode.ERROR_STORING_CLIENT_MSG);
				return new InternalServerErrorException(Response
					.status(Status.INTERNAL_SERVER_ERROR)
					.entity(new Errors(AuthAdminErrorCode.ERROR_STORING_CLIENT, AuthAdminErrorCode.ERROR_STORING_CLIENT_MSG))
					.build());
			}
		} else {
			/*
			 * Unexpected error
			 */
			Log.errorf(t, AuthAdminErrorCode.ERROR_CREATING_CLIENT_MSG);
			return new InternalServerErrorException(Response
				.status(Status.INTERNAL_SERVER_ERROR)
				.entity(new Errors(AuthAdminErrorCode.ERROR_CREATING_CLIENT, AuthAdminErrorCode.ERROR_CREATING_CLIENT_MSG))
				.build());
		}
	}

	/**
	 * 
	 * @param requestId
	 * @param version
	 * @param req
	 * @param uriInfo
	 * @return
	 */
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Uni<Response> create(
		@HeaderParam(HeaderParamName.REQUEST_ID)
		@Pattern(regexp = ValidationPattern.REQUEST_ID, message = ErrorCode.REQUEST_ID_MUST_MATCH_REGEXP_MSG) String requestId,
		@HeaderParam(HeaderParamName.VERSION)
		@Pattern(regexp = ValidationPattern.VERSION, message = ErrorCode.VERSION_MUST_MATCH_REGEXP_MSG) String version,
		CreateClientRequest req,
		@Context UriInfo uriInfo) {
		Log.tracef("Create a new client: %s", req.toString());

		/*
		 * Generate random client id.
		 */
		Log.tracef("Generate random client id");
		String clientId = UUID.randomUUID().toString();

		/*
		 * Generate secret triplet (secret, salt and hash).
		 */
		Log.tracef("Generate secret, salt and hash");
		SecretTriplet triplet = SecretTriplet.generate();

		/*
		 * Store client in the DB.
		 */
		Log.tracef("Store new client in the DB");
		ClientEntity entity = new ClientEntity(
			clientId,
			req.getChannel(),
			triplet.getSalt(),
			triplet.getHash(),
			req.getDescription(),
			req.getSubject());

		return repository.persist(entity)
			.map(e -> {
				CreateClientResponse res = new CreateClientResponse(clientId, triplet.getSecret());
				Log.debugf("Client created successfully: %s", res.toString());
				return Response.created(uriInfo.getAbsolutePathBuilder().path(clientId).build())
					.entity(res)
					.build();
			})
			.onFailure().transform(this::onPersistError);
	}

	/**
	 * 
	 * @param page
	 * @param size
	 * @param tuple
	 * @return
	 */
	private PageOfClients tuple2Page(int page, int size, Tuple2<Long, List<ClientEntity>> tuple) {
		/*
		 * Page metadata
		 */
		Long totalElements = tuple.getItem1();
		long totalPages = (int) Math.ceil(totalElements.doubleValue() / size);
		PageMetadata pageMetadata = new PageMetadata(totalElements, totalPages, page, size);

		/*
		 * Page of data
		 */
		List<Client> clients = tuple.getItem2()
			.stream()
			.map(entity -> new Client(
				entity.getClientId(),
				entity.getChannel(),
				entity.getSalt(),
				entity.getSecretHash(),
				entity.getDescription(),
				entity.getSubject()))
			.toList();

		return new PageOfClients(clients, pageMetadata);
	}
	
	/**
	 * 
	 * @param t
	 * @return
	 */
	private WebApplicationException onFindError(Throwable t) {
		Log.errorf(t, AuthAdminErrorCode.ERROR_READING_CLIENTS_MSG);
		return new InternalServerErrorException(Response
			.status(Status.INTERNAL_SERVER_ERROR)
			.entity(new Errors(AuthAdminErrorCode.ERROR_READING_CLIENTS, AuthAdminErrorCode.ERROR_READING_CLIENTS_MSG))
			.build());
	}

	/**
	 * 
	 * @param requestId
	 * @param version
	 * @param page
	 * @param size
	 * @return
	 */
	@GET
	public Uni<PageOfClients> read(
		@HeaderParam(HeaderParamName.REQUEST_ID)
		@Pattern(regexp = ValidationPattern.REQUEST_ID, message = ErrorCode.REQUEST_ID_MUST_MATCH_REGEXP_MSG) String requestId,
		@HeaderParam(HeaderParamName.VERSION)
		@Pattern(regexp = ValidationPattern.VERSION, message = ErrorCode.VERSION_MUST_MATCH_REGEXP_MSG) String version,
		@QueryParam(AdminQueryParamName.PAGE)
		@Min(value = 1, message = AuthAdminErrorCode.PAGE_MUST_BE_GREATER_THAN_OR_EQUAL_TO_VALUE_MSG)
		@Max(value = Integer.MAX_VALUE, message = AuthAdminErrorCode.PAGE_MUST_BE_LESS_THAN_OR_EQUAL_TO_VALUE_MSG)
		@DefaultValue("1") int page,
		@QueryParam(AdminQueryParamName.PAGE)
		@Min(value = 1, message = AuthAdminErrorCode.SIZE_MUST_BE_GREATER_THAN_OR_EQUAL_TO_VALUE_MSG)
		@Max(value = 128, message = AuthAdminErrorCode.SIZE_MUST_BE_LESS_THAN_OR_EQUAL_TO_VALUE_MSG)
		@DefaultValue("20") int size) {
		Log.tracef("Read clients: page = %d, size = %d", page, size);

		return Uni.combine()
			.all()
			.unis(
				repository.count(),
				repository.findAll(Sort.ascending(ClientEntity.CLIENT_ID_PRP))
					.page(page, size)
					.list())
			.asTuple()
			.map(tuple -> tuple2Page(page, size, tuple))
			.invoke(res -> Log.debugf("Clients read successfully: %s", res.toString()))
			.onFailure().transform(this::onFindError);
	}
	
	// TODO
	
	@PATCH
	public Uni<Response> update(
	@HeaderParam(HeaderParamName.REQUEST_ID)
	@Pattern(regexp = ValidationPattern.REQUEST_ID, message = ErrorCode.REQUEST_ID_MUST_MATCH_REGEXP_MSG) String requestId,
	@HeaderParam(HeaderParamName.VERSION)
	@Pattern(regexp = ValidationPattern.VERSION, message = ErrorCode.VERSION_MUST_MATCH_REGEXP_MSG) String version,
	UpdateClientRequest req) {
		return null;
	}
	
	// TODO
	
	@DELETE
	public Uni<Response> delete(@HeaderParam(HeaderParamName.REQUEST_ID)
	@Pattern(regexp = ValidationPattern.REQUEST_ID, message = ErrorCode.REQUEST_ID_MUST_MATCH_REGEXP_MSG) String requestId,
	@HeaderParam(HeaderParamName.VERSION)
	@Pattern(regexp = ValidationPattern.VERSION, message = ErrorCode.VERSION_MUST_MATCH_REGEXP_MSG) String version,
	@QueryParam(CLIENT ID)
	
}
