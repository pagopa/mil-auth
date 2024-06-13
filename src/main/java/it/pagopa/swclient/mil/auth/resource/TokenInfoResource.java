/*
 * TokenInfoResource.java
 *
 * 24 mag 2024
 */
package it.pagopa.swclient.mil.auth.resource;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

import com.nimbusds.jwt.SignedJWT;

import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
import it.pagopa.swclient.mil.ErrorCode;
import it.pagopa.swclient.mil.auth.AuthErrorCode;
import it.pagopa.swclient.mil.auth.bean.ClaimName;
import it.pagopa.swclient.mil.auth.bean.EncryptedClaim;
import it.pagopa.swclient.mil.auth.bean.HeaderParamName;
import it.pagopa.swclient.mil.auth.bean.TokenInfoRequest;
import it.pagopa.swclient.mil.auth.bean.TokenInfoResponse;
import it.pagopa.swclient.mil.auth.service.ClaimEncryptor;
import it.pagopa.swclient.mil.auth.util.AuthError;
import it.pagopa.swclient.mil.auth.util.AuthException;
import it.pagopa.swclient.mil.auth.util.UniGenerator;
import it.pagopa.swclient.mil.bean.Errors;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.constraints.Pattern;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.HeaderParam;
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
@Path("/token_info")
public class TokenInfoResource {
	/*
	 * 
	 */
	private ClaimEncryptor claimEncryptor;

	/**
	 * 
	 * @param claimEncryptor
	 */
	@Inject
	TokenInfoResource(ClaimEncryptor claimEncryptor) {
		this.claimEncryptor = claimEncryptor;
	}

	/**
	 * 
	 * @param request
	 * @return
	 */
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@RolesAllowed("token_info")
	public Uni<Response> getTokenInfo(
		@HeaderParam(HeaderParamName.REQUEST_ID)
		@Pattern(regexp = "^[a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12}$", message = ErrorCode.REQUEST_ID_MUST_MATCH_REGEXP_MSG) String requestId,
		TokenInfoRequest request) {
		try {
			SignedJWT token = SignedJWT.parse(request.getToken());
			Map<String, Object> map = token.getJWTClaimsSet()
				.getJSONObjectClaim(ClaimName.FISCAL_CODE);
			if (map != null) {
				Log.tracef("Encrypted fiscal code present: %s", map);
				EncryptedClaim encFiscalCode = new EncryptedClaim().fromMap(token.getJWTClaimsSet()
					.getJSONObjectClaim(ClaimName.FISCAL_CODE));

				return claimEncryptor.decrypt(encFiscalCode)
					.map(fiscalCode -> Response.ok(new TokenInfoResponse()
						.setFiscalCode(fiscalCode)).build())
					.onFailure(t -> !(t instanceof AuthError || t instanceof AuthException))
					.transform(t -> {
						Log.errorf(t, "Unexpected error.");
						return new InternalServerErrorException(Response.status(Status.INTERNAL_SERVER_ERROR)
							.entity(new Errors(List.of(AuthErrorCode.UNEXPECTED_ERROR)))
							.build());
					})
					.onFailure(AuthError.class)
					.transform(t -> {
						Log.errorf(t, "Handled error.");
						AuthError e = (AuthError) t;
						return new InternalServerErrorException(Response.status(Status.INTERNAL_SERVER_ERROR)
							.entity(new Errors(List.of(e.getCode()), List.of(e.getMessage())))
							.build());
					})
					.onFailure(AuthException.class)
					.transform(t -> {
						Log.errorf(t, "Handled exception.");
						AuthException e = (AuthException) t;
						return new InternalServerErrorException(Response.status(Status.INTERNAL_SERVER_ERROR)
							.entity(new Errors(List.of(e.getCode()), List.of(e.getMessage())))
							.build());
					});
			} else {
				/*
				 * 204
				 */
				Log.trace("Encrypted fiscal code isn't present");
				return UniGenerator.item(Response.noContent().build());
			}
		} catch (ParseException e) {
			/*
			 * 400 TODO INCLUDERE IL BODY JSON CON L'ERRORE
			 */
			Log.errorf(e, "Error parsing token. Offending token: %s", request.getToken());
			return Uni.createFrom().failure(new BadRequestException("[" + AuthErrorCode.ERROR_PARSING_TOKEN + "] error parsing token"));
		}
	}
}
