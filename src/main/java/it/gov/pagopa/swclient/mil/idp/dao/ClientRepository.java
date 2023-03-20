/*
 * ClientRepository.java
 *
 * 16 mar 2023
 */
package it.gov.pagopa.swclient.mil.idp.dao;

import java.util.Map;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;

import io.smallrye.mutiny.Uni;

/**
 * 
 * @author Antonio Tarricone
 */
@ApplicationScoped
public class ClientRepository {
	/*
	 * 
	 */
	private static final Map<String, ClientEntity> REPOSITORY = Map.of(
		"5254f087-1214-45cd-94ae-fda53c835197", new ClientEntity("5254f087-1214-45cd-94ae-fda53c835197", "POS", "SmartPOS"),
		"2f3cd44c-d11a-4ba6-a3e9-112a4710e12e", new ClientEntity("2f3cd44c-d11a-4ba6-a3e9-112a4710e12e", "POS", "SoftPOS")
	);
	
	
	/**
	 * 
	 * @param cliendId
	 * @return
	 */
	public Uni<Optional<ClientEntity>> findByIdOptional(String cliendId) {
		return Uni.createFrom().item(Optional.ofNullable(REPOSITORY.get(cliendId)));
	}
}