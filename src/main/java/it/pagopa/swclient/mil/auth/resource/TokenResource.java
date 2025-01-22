/*
 * TokenResource.java
 *
 * 16 mar 2023
 */
package it.pagopa.swclient.mil.auth.resource;

import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.unchecked.Unchecked;
import it.pagopa.swclient.mil.auth.AuthErrorCode;
import it.pagopa.swclient.mil.auth.bean.AuthCookieParamName;
import it.pagopa.swclient.mil.auth.bean.ClaimName;
import it.pagopa.swclient.mil.auth.bean.GetAccessTokenRequest;
import it.pagopa.swclient.mil.auth.bean.GrantType;
import it.pagopa.swclient.mil.auth.qualifier.ClientCredentials;
import it.pagopa.swclient.mil.auth.qualifier.Password;
import it.pagopa.swclient.mil.auth.qualifier.RefreshToken;
import it.pagopa.swclient.mil.auth.service.TokenService;
import it.pagopa.swclient.mil.auth.util.AuthError;
import it.pagopa.swclient.mil.auth.util.AuthException;
import it.pagopa.swclient.mil.bean.Errors;
import jakarta.annotation.security.PermitAll;
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
import jakarta.ws.rs.core.NewCookie;
import jakarta.ws.rs.core.NewCookie.SameSite;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.ResponseBuilder;
import jakarta.ws.rs.core.Response.Status;

/**
 * @author Antonio Tarricone
 */
@SuppressWarnings("serial")
@Path("/token")
@PermitAll
public class TokenResource {
	/*
	 *
	 */
	private static Map<String, AnnotationLiteral<?>> qualifiers = new HashMap<>();

	static {
		qualifiers.put(GrantType.CLIENT_CREDENTIALS, new AnnotationLiteral<ClientCredentials>() {
		});
		qualifiers.put(GrantType.PASSWORD, new AnnotationLiteral<Password>() {
		});
		qualifiers.put(GrantType.REFRESH_TOKEN, new AnnotationLiteral<RefreshToken>() {
		});
	}

	/*
	 *
	 */
	private Instance<TokenService> tokenService;

	/*
	 * mil-auth base URL.
	 */
	@ConfigProperty(name = "base-url", defaultValue = "")
	String baseUrl;

	/**
	 * 
	 * @param tokenService
	 */
	@Inject
	TokenResource(@Any Instance<TokenService> tokenService) {
		this.tokenService = tokenService;
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
	public Uni<Response> createOrRefreshToken(@Valid @BeanParam GetAccessTokenRequest getAccessToken) {
		/*
		 * If the flow reaches this point, the input is validated!
		 */
		return tokenService.select(qualifiers.get(getAccessToken.getGrantType()))
			.get()
			.process(getAccessToken)
			.map(Unchecked.function(resp -> {
				ResponseBuilder respBuilder = Response.ok(resp);
				SignedJWT currentRefreshToken = getAccessToken.getTheRefreshToken();
				SignedJWT newRefreshToken = resp.getRefreshToken();
				boolean returnRefreshTokenInTheCookie = newRefreshToken != null
					&& (getAccessToken.isReturnTheRefreshTokenInTheCookie()
						|| (currentRefreshToken != null
							&& Objects.equals(currentRefreshToken.getJWTClaimsSet().getBooleanClaim(ClaimName.RETURNED_IN_THE_COOKIE), Boolean.TRUE)));
				if (returnRefreshTokenInTheCookie) {
					Log.debug("Refresh token is returned within cookie");

					/*
					 * Build cookie.
					 */
					URI tokenUri = new URI(baseUrl.replaceAll("\\/$", "") + "/token");

					@SuppressWarnings("null")
					JWTClaimsSet claimsSet = newRefreshToken.getJWTClaimsSet();
					Date expiry = claimsSet.getExpirationTime();

					NewCookie cookie = new NewCookie.Builder(AuthCookieParamName.REFRESH_COOKIE)
						.domain(tokenUri.getHost())
						.path(tokenUri.getPath())
						.expiry(expiry)
						.maxAge((int) TimeUnit.SECONDS.convert(new Date().getTime() - expiry.getTime(), TimeUnit.MILLISECONDS))
						.httpOnly(true)
						.secure(true)
						.sameSite(SameSite.STRICT)
						.value(newRefreshToken.serialize())
						.build();

					respBuilder.cookie(cookie);

					/*
					 * Remove refresh token from the body.
					 */
					resp.setRefreshToken(null);
				}
				return respBuilder.build();
			}))
			.onFailure(t -> !(t instanceof AuthError || t instanceof AuthException))
			.transform(t -> {
				Log.errorf(t, "Unexpected error.");
				return new InternalServerErrorException(Response.status(Status.INTERNAL_SERVER_ERROR)
					.entity(new Errors(AuthErrorCode.UNEXPECTED_ERROR))
					.build());
			})
			.onFailure(AuthError.class)
			.transform(t -> {
				AuthError e = (AuthError) t;
				return new InternalServerErrorException(Response.status(Status.INTERNAL_SERVER_ERROR)
					.entity(new Errors(e.getCode(), e.getMessage()))
					.build());
			})
			.onFailure(AuthException.class)
			.transform(t -> {
				AuthException e = (AuthException) t;
				return new NotAuthorizedException(Response.status(Status.UNAUTHORIZED)
					.entity(new Errors(e.getCode(), e.getMessage()))
					.build());
			});
	}
}