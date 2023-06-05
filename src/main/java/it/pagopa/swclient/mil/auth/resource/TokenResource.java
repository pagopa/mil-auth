/*
 * TokenResource.java
 *
 * 16 mar 2023
 */
package it.pagopa.swclient.mil.auth.resource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
import it.pagopa.swclient.mil.auth.ErrorCode;
import it.pagopa.swclient.mil.auth.bean.AccessToken;
import it.pagopa.swclient.mil.auth.bean.GetAccessToken;
import it.pagopa.swclient.mil.auth.bean.GrantType;
import it.pagopa.swclient.mil.auth.qualifier.ClientCredentials;
import it.pagopa.swclient.mil.auth.qualifier.Password;
import it.pagopa.swclient.mil.auth.qualifier.PoyntToken;
import it.pagopa.swclient.mil.auth.qualifier.RefreshToken;
import it.pagopa.swclient.mil.auth.service.TokenService;
import it.pagopa.swclient.mil.auth.util.AuthError;
import it.pagopa.swclient.mil.auth.util.AuthException;
import it.pagopa.swclient.mil.bean.Errors;
import jakarta.enterprise.inject.Any;
import jakarta.enterprise.inject.Instance;
import jakarta.enterprise.util.AnnotationLiteral;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.BeanParam;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.InternalServerErrorException;
import jakarta.ws.rs.NotAuthorizedException;
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
@SuppressWarnings("serial")
@Path("/token")
public class TokenResource {
	/*
	 * 
	 */
	@Inject
	@Any
	Instance<TokenService> tokenService;

	/*
	 * 
	 */
	private static Map<String, AnnotationLiteral<?>> QUALIFIERS = new HashMap<>();
	static {
		QUALIFIERS.put(GrantType.CLIENT_CREDENTIALS, new AnnotationLiteral<ClientCredentials>() {
		});
		QUALIFIERS.put(GrantType.PASSWORD, new AnnotationLiteral<Password>() {
		});
		QUALIFIERS.put(GrantType.POYNT_TOKEN, new AnnotationLiteral<PoyntToken>() {
		});
		QUALIFIERS.put(GrantType.REFRESH_TOKEN, new AnnotationLiteral<RefreshToken>() {
		});
	}

	/**
	 * Dispatches the request to the right method.
	 *
	 * @param getAccessToken
	 * @return
	 */
	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Uni<AccessToken> createOrRefreshToken(@Valid @BeanParam GetAccessToken getAccessToken) {
		/*
		 * If the flow reaches this point, the input is validated!
		 */
		return tokenService.select(QUALIFIERS.get(getAccessToken.getGrantType()))
			.get()
			.process(getAccessToken)
			.onFailure(t -> !(t instanceof AuthError || t instanceof AuthException))
			.transform(t -> {
				Log.errorf(t, "Unexpected error.");
				return new InternalServerErrorException(Response.status(Status.INTERNAL_SERVER_ERROR)
					.entity(new Errors(List.of(ErrorCode.UNEXPECTED_ERROR)))
					.build());
			})
			.onFailure(AuthError.class)
			.transform(t -> {
				AuthError e = (AuthError) t;
				return new InternalServerErrorException(Response.status(Status.INTERNAL_SERVER_ERROR)
					.entity(new Errors(List.of(e.getCode()), List.of(e.getMessage())))
					.build());
			})
			.onFailure(AuthException.class)
			.transform(t -> {
				AuthException e = (AuthException) t;
				return new NotAuthorizedException(Response.status(Status.UNAUTHORIZED)
					.entity(new Errors(List.of(e.getCode()), List.of(e.getMessage())))
					.build());
			});
	}
}