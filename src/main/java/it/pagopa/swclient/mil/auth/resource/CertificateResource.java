/*
 * CertificateResource.java
 *
 * 21 mar 2023
 */
package it.pagopa.swclient.mil.auth.resource;

import java.util.UUID;

import org.jboss.logging.MDC;

import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
import it.pagopa.swclient.mil.auth.azure.auth.service.AzureAuthService;
import it.pagopa.swclient.mil.auth.azure.keyvault.service.AzureKeyVaultService;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * @author Antonio Tarricone
 */
@Path("/certificates")
public class CertificateResource {
	/*
	 *
	 */
	AzureAuthService authService;
	
	/*
	 * 
	 */
	AzureKeyVaultService keyVaultService;
	
	/**
	 * 
	 * @param authService
	 * @param keyVaultService
	 */
	@Inject
	CertificateResource(AzureAuthService authService, AzureKeyVaultService keyVaultService) {
		this.authService=authService;
		this.keyVaultService=keyVaultService;
	}

	/**
	 * @return
	 */
	@Path("/{certificateName}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Uni<Response> get(@PathParam("certificateName") String certificateName) {
		String correlationId = UUID.randomUUID().toString();
		MDC.put("requestId", correlationId);
		Log.debug("get - Input parameters: n/a");
		return authService.getAccessToken().chain(x -> keyVaultService.getCertificate(x.getToken(), certificateName));
	}
}