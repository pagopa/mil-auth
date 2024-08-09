/*
 * CreateClientResource.java
 *
 * 25 lug 2024
 */
package it.pagopa.swclient.mil.auth.admin.resource;

import java.util.List;
import java.util.UUID;

import org.bson.Document;

import com.mongodb.MongoWriteException;

import io.quarkus.logging.Log;
import io.quarkus.panache.common.Sort;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.tuples.Tuple2;
import it.pagopa.swclient.mil.auth.AuthErrorCode;
import it.pagopa.swclient.mil.auth.admin.AuthAdminErrorCode;
import it.pagopa.swclient.mil.auth.admin.bean.AdminPathParamName;
import it.pagopa.swclient.mil.auth.admin.bean.AuthAdminValidationPattern;
import it.pagopa.swclient.mil.auth.admin.bean.Client;
import it.pagopa.swclient.mil.auth.admin.bean.CreateClientResponse;
import it.pagopa.swclient.mil.auth.admin.bean.CreateOrUpdateClientRequest;
import it.pagopa.swclient.mil.auth.admin.bean.PageMetadata;
import it.pagopa.swclient.mil.auth.admin.bean.PageOfClients;
import it.pagopa.swclient.mil.auth.admin.bean.ReadClientsRequest;
import it.pagopa.swclient.mil.auth.admin.util.ClientConverter;
import it.pagopa.swclient.mil.auth.dao.ClientEntity;
import it.pagopa.swclient.mil.auth.dao.ClientRepository;
import it.pagopa.swclient.mil.auth.util.SecretTriplet;
import it.pagopa.swclient.mil.bean.Errors;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.InternalServerErrorException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
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
	 * @param req
	 * @param uriInfo
	 * @return
	 */
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Uni<Response> create(@Valid CreateOrUpdateClientRequest req, @Context UriInfo uriInfo) {
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
			.map(ClientConverter::convert)
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
	 * @param req
	 * @return
	 */
	@GET
	public Uni<PageOfClients> read(@Valid ReadClientsRequest req) {
		Log.tracef("Read clients: %s", req);

		return Uni.combine()
			.all()
			.unis(
				repository.count(),
				repository.findAll(Sort.ascending(ClientEntity.CLIENT_ID_PRP))
					.page(req.getPage(), req.getSize())
					.list())
			.asTuple()
			.map(tuple -> tuple2Page(req.getPage(), req.getSize(), tuple))
			.invoke(res -> Log.debugf("Clients read successfully: %s", res.toString()))
			.onFailure().transform(this::onFindError);
	}

	/**
	 * 
	 * @return
	 */
	private WebApplicationException onNotFoundError() {
		Log.error(AuthAdminErrorCode.CLIENT_NOT_FOUND_MSG);
		return new NotFoundException(Response
			.status(Status.NOT_FOUND)
			.entity(new Errors(AuthAdminErrorCode.CLIENT_NOT_FOUND, AuthAdminErrorCode.CLIENT_NOT_FOUND_MSG))
			.build());
	}

	/**
	 * 
	 * @param clientId
	 * @return
	 */
	@GET
	@Path("/{" + AdminPathParamName.CLIENT_ID + "}")
	public Uni<Client> read(
		@PathParam(AdminPathParamName.CLIENT_ID)
		@Pattern(regexp = AuthAdminValidationPattern.CLIENT_ID, message = AuthErrorCode.CLIENT_ID_MUST_MATCH_REGEXP_MSG) String clientId) {
		Log.tracef("Read client %s", clientId);
		return repository.find(ClientEntity.CLIENT_ID_PRP, clientId)
			.firstResultOptional()
			.onFailure().transform(this::onFindError)
			.map(opt -> opt.orElseThrow(this::onNotFoundError))
			.map(ClientConverter::convert)
			.invoke(res -> Log.debugf("Client read successfully: %s", res.toString()));
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
		Log.errorf(t, AuthAdminErrorCode.ERROR_UPDATING_CLIENT_MSG);
		return new InternalServerErrorException(Response
			.status(Status.INTERNAL_SERVER_ERROR)
			.entity(new Errors(AuthAdminErrorCode.ERROR_UPDATING_CLIENT, AuthAdminErrorCode.ERROR_UPDATING_CLIENT_MSG))
			.build());
	}

	/**
	 * 
	 * @param clientId
	 * @param req
	 * @return
	 */
	@PATCH
	@Path("/{" + AdminPathParamName.CLIENT_ID + "}")
	public Uni<Response> update(
		@PathParam(AdminPathParamName.CLIENT_ID)
		@Pattern(regexp = AuthAdminValidationPattern.CLIENT_ID, message = AuthErrorCode.CLIENT_ID_MUST_MATCH_REGEXP_MSG) String clientId,
		@Valid CreateOrUpdateClientRequest req) {
		Log.tracef("Update client %s: %s", clientId, req);

		Document update = new Document("$set", new Document()
			.append(ClientEntity.CHANNEL_PRP, req.getChannel())
			.append(ClientEntity.DESCRIPTION_PRP, req.getDescription())
			.append(ClientEntity.SUBJECT_PRP, req.getSubject()));

		return repository.update(update)
			.where(ClientEntity.CLIENT_ID_PRP, clientId)
			.onFailure().transform(this::onUpdateError)
			.map(n -> {
				if (n > 0) {
					Log.debugf("Client %s updated successfully", clientId);
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
		Log.errorf(t, AuthAdminErrorCode.ERROR_DELETING_CLIENT_MSG);
		return new InternalServerErrorException(Response
			.status(Status.INTERNAL_SERVER_ERROR)
			.entity(new Errors(AuthAdminErrorCode.ERROR_DELETING_CLIENT, AuthAdminErrorCode.ERROR_DELETING_CLIENT_MSG))
			.build());
	}

	/**
	 * 
	 * @param clientId
	 * @return
	 */
	@DELETE
	@Path("/{" + AdminPathParamName.CLIENT_ID + "}")
	public Uni<Response> delete(
		@PathParam(AdminPathParamName.CLIENT_ID)
		@Pattern(regexp = AuthAdminValidationPattern.CLIENT_ID, message = AuthErrorCode.CLIENT_ID_MUST_MATCH_REGEXP_MSG) String clientId) {
		Log.tracef("Delete client %s", clientId);
		return repository.delete(ClientEntity.CLIENT_ID_PRP, clientId)
			.onFailure().transform(this::onDeleteError)
			.map(n -> {
				if (n > 0) {
					Log.debugf("Client %s deleted successfully", clientId);
					return Response.noContent().build();
				} else {
					throw onNotFoundError();
				}
			});
	}
}
