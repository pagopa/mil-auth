/*
 * ClientRepository.java
 *
 * 16 mar 2023
 */
package it.pagopa.swclient.mil.auth.dao;

import static it.pagopa.swclient.mil.auth.util.UniGenerator.item;

import java.util.Map;
import java.util.Optional;

import io.smallrye.mutiny.Uni;
import it.pagopa.swclient.mil.bean.Channel;
import jakarta.enterprise.context.ApplicationScoped;

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
		"5254f087-1214-45cd-94ae-fda53c835197", new ClientEntity("5254f087-1214-45cd-94ae-fda53c835197", Channel.POS, null, "SmartPOS"),
		"2f3cd44c-d11a-4ba6-a3e9-112a4710e12e", new ClientEntity("2f3cd44c-d11a-4ba6-a3e9-112a4710e12e", Channel.POS, null, "SoftPOS"),
		"92faf319-4219-455f-841b-bb692684672a", new ClientEntity("92faf319-4219-455f-841b-bb692684672a", null, "bc0be8fc-fbf4-4971-8f97-48a3a1c1e0da", "Other Server App"));

	/**
	 * 
	 * @param cliendId
	 * @return
	 */
	public Uni<Optional<ClientEntity>> findByIdOptional(String cliendId) {
		return item(Optional.ofNullable(REPOSITORY.get(cliendId)));
	}
}