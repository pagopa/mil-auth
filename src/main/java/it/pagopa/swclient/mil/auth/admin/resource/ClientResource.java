/*
 * CreateClientResource.java
 *
 * 25 lug 2024
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
import it.pagopa.swclient.mil.auth.AuthErrorCode;
import it.pagopa.swclient.mil.auth.admin.AdminErrorCode;
import it.pagopa.swclient.mil.auth.admin.bean.AdminPathParamName;
import it.pagopa.swclient.mil.auth.admin.bean.Client;
import it.pagopa.swclient.mil.auth.admin.bean.CreateClientRequest;
import it.pagopa.swclient.mil.auth.admin.bean.CreateClientResponse;
import it.pagopa.swclient.mil.auth.admin.bean.PageMetadata;
import it.pagopa.swclient.mil.auth.admin.bean.PageOfClients;
import it.pagopa.swclient.mil.auth.admin.bean.ReadClientsRequest;
import it.pagopa.swclient.mil.auth.admin.bean.UpdateClientRequest;
import it.pagopa.swclient.mil.auth.admin.util.ClientConverter;
import it.pagopa.swclient.mil.auth.admin.util.SecretHolder;
import it.pagopa.swclient.mil.auth.bean.AuthValidationPattern;
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
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

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
	 * mil-auth base URL.
	 */
	@ConfigProperty(name = "base-url", defaultValue = "")
	String baseUrl;

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
				Log.warnf(m, AdminErrorCode.DUPLICATE_CLIENT_ID_MSG);
				return new WebApplicationException(
					Response
						.status(Status.CONFLICT)
						.entity(new Errors(AdminErrorCode.DUPLICATE_CLIENT_ID, AdminErrorCode.DUPLICATE_CLIENT_ID_MSG))
						.build());
			} else {
				/*
				 * Other error
				 */
				Log.errorf(m, AdminErrorCode.ERROR_STORING_CLIENT_MSG);
				return new InternalServerErrorException(
					Response
						.status(Status.INTERNAL_SERVER_ERROR)
						.entity(new Errors(AdminErrorCode.ERROR_STORING_CLIENT, AdminErrorCode.ERROR_STORING_CLIENT_MSG))
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
	public Uni<Response> create(@Valid CreateClientRequest req) {
		Log.tracef("Create a new client: %s", req.toString());

		/*
		 * Generate random client id.
		 */
		Log.tracef("Generate random client id");
		String clientId = UUID.randomUUID().toString();

		String salt = null;
		String hash = null;
		SecretHolder secretHolder = new SecretHolder();
		if (!req.isSecretless()) {
			/*
			 * Generate secret triplet (secret, salt and hash).
			 */
			Log.tracef("Generate secret, salt and hash");
			SecretTriplet triplet = SecretTriplet.generate();
			salt = triplet.getSalt();
			hash = triplet.getHash();
			secretHolder.setSecret(triplet.getSecret());
		}

		/*
		 * Store client in the DB.
		 */
		Log.tracef("Store new client in the DB");
		ClientEntity entity = new ClientEntity(
			clientId,
			req.getChannel(),
			salt,
			hash,
			req.getDescription(),
			req.getSubject());

		return repository
			.persist(entity)
			.map(e -> {

				CreateClientResponse res = new CreateClientResponse(clientId, secretHolder.getSecret());
				Log.debugf("Client created successfully: %s", res.toString());
				return Response.created(URI.create(baseUrl + "/admin/clients/" + clientId))
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
		List<Client> clients = tuple
			.getItem2()
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
		Log.errorf(t, AdminErrorCode.ERROR_READING_CLIENTS_MSG);
		return new InternalServerErrorException(
			Response
				.status(Status.INTERNAL_SERVER_ERROR)
				.entity(new Errors(AdminErrorCode.ERROR_READING_CLIENTS, AdminErrorCode.ERROR_READING_CLIENTS_MSG))
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
				repository.findAll(req.getPage(), req.getSize()))
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
		Log.error(AdminErrorCode.CLIENT_NOT_FOUND_MSG);
		return new NotFoundException(Response
			.status(Status.NOT_FOUND)
			.entity(new Errors(AdminErrorCode.CLIENT_NOT_FOUND, AdminErrorCode.CLIENT_NOT_FOUND_MSG))
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
		@Pattern(regexp = AuthValidationPattern.CLIENT_ID, message = AuthErrorCode.CLIENT_ID_MUST_MATCH_REGEXP_MSG) String clientId) {
		Log.tracef("Read client %s", clientId);
		return repository.findByClientId(clientId)
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
		Log.errorf(t, AdminErrorCode.ERROR_UPDATING_CLIENT_MSG);
		return new InternalServerErrorException(Response
			.status(Status.INTERNAL_SERVER_ERROR)
			.entity(new Errors(AdminErrorCode.ERROR_UPDATING_CLIENT, AdminErrorCode.ERROR_UPDATING_CLIENT_MSG))
			.build());
	}

	/**
	 * 
	 * @param req
	 * @return
	 */
	@PATCH
	@Path("/{" + AdminPathParamName.CLIENT_ID + "}")
	public Uni<Response> update(
		@PathParam(AdminPathParamName.CLIENT_ID)
		@Pattern(regexp = AuthValidationPattern.CLIENT_ID, message = AuthErrorCode.CLIENT_ID_MUST_MATCH_REGEXP_MSG) String clientId,
		UpdateClientRequest req) {
		Log.tracef("Update client %s: %s", clientId, req);
		return repository.updateByClientId(
			clientId,
			req.getNewChannel(),
			req.getNewDescription(),
			req.getNewSubject())
			.onFailure().transform(this::onUpdateError)
			.map(n -> {
				if (n > 0) {
					Log.debugf("Client %s updated successfully: %s", clientId, req);
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
		Log.errorf(t, AdminErrorCode.ERROR_DELETING_CLIENT_MSG);
		return new InternalServerErrorException(Response
			.status(Status.INTERNAL_SERVER_ERROR)
			.entity(new Errors(AdminErrorCode.ERROR_DELETING_CLIENT, AdminErrorCode.ERROR_DELETING_CLIENT_MSG))
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
		@Pattern(regexp = AuthValidationPattern.CLIENT_ID, message = AuthErrorCode.CLIENT_ID_MUST_MATCH_REGEXP_MSG) String clientId) {
		Log.tracef("Delete client %s", clientId);
		return repository.deleteByClientId(clientId)
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
