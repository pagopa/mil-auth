/*
 * TestResource.java
 *
 * 5 apr 2023
 */
package it.gov.pagopa.swclient.mil.idp.resource;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.nimbusds.jose.JOSEException;

import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
import it.gov.pagopa.swclient.mil.idp.bean.KeyPair;
import it.gov.pagopa.swclient.mil.idp.bean.PublicKey;
import it.gov.pagopa.swclient.mil.idp.service.KeyPairGenerator;
import it.gov.pagopa.swclient.mil.idp.service.RedisClient;

/**
 * 
 * @author Antonio Tarricone
 */
@Path("/test")
public class TestResource {
	/*
	 * 
	 */
	@Inject
	KeyPairGenerator keyPairGenerator;

	/*
	 * 
	 */
	@Inject
	RedisClient redisClient;

	/**
	 * 
	 * @return
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Uni<List<String>> get() {
		Log.debugf("get - Input parameters: n/a");
		return redisClient.keys("*").log();
	}

	/**
	 * 
	 * @param kid
	 * @return
	 */
	@GET
	@Path("{kid}")
	@Produces(MediaType.APPLICATION_JSON)
	public Uni<PublicKey> get(@PathParam("kid") String kid) {
		Log.debugf("get - Input parameters: kid = %s", kid);
		return redisClient.get(kid).log().map(p -> p != null ? p.publicKey() : null).log();
	}

	/**
	 * 
	 * @return
	 * @throws JOSEException
	 */
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Uni<String> post() throws JOSEException {
		Log.debugf("post - Input parameters: n/a");
		KeyPair keyPair = keyPairGenerator.generateRsaKey();
		String kid = keyPair.getKid();
		return redisClient.setex(kid, 600, keyPair).chain(() -> {
			return Uni.createFrom().item(kid);
		});
	}
}