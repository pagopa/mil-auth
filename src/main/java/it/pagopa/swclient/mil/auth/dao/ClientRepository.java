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
 * FOR DEMO ONLY. THIS WILL BE REPLACED BY DB.
 * 
 * @author Antonio Tarricone
 */
@ApplicationScoped
public class ClientRepository {
	/*
	 * 
	 */
	private static final Map<String, ClientEntity> REPOSITORY = Map.of(
		"5254f087-1214-45cd-94ae-fda53c835197", new ClientEntity("5254f087-1214-45cd-94ae-fda53c835197", Channel.POS, null, null, "SmartPOS"),
		"2f3cd44c-d11a-4ba6-a3e9-112a4710e12e", new ClientEntity("2f3cd44c-d11a-4ba6-a3e9-112a4710e12e", Channel.POS, null, null, "SoftPOS"),
		"92faf319-4219-455f-841b-bb692684672a", new ClientEntity("92faf319-4219-455f-841b-bb692684672a", null, "qZdnjBPUllZyjbcRiUCSf8Pa1OAfANlb9IryD1zLHVJWGzMxCxLiJtUxJay1zSL+/S2YFDHPFRIEAaJ6+OjYAg==", "lnui7t7kPoej37wgzrl4va7Pu0+A4zlRVQpb22vKM2A=", "Nodo"),
		"83c0b10f-b398-4cc8-b356-a3e0f0291679", new ClientEntity("83c0b10f-b398-4cc8-b356-a3e0f0291679", Channel.ATM, "zfN59oSr9RfFiiSASUO1YIcv8bARsj1OAV8tEydQiKC3su5Mlz1TsjbFwvWrGCjXdkDUsbeXGnYZDavJuTKw6Q==", "EIBJ3uXU/tot3EeDeaoBQ0HTuotsfqJqspOMNfNyycU=", "ATM Layer"),
		"3965df56-ca9a-49e5-97e8-061433d4a25b", new ClientEntity("3965df56-ca9a-49e5-97e8-061433d4a25b", Channel.POS, "aGw/h/8Fm9S2aNvlvIaxJyhKP67ZU4FEm6mDVhL3aEVrahXFif9x2BkQ4OY87Z9tWVyWbSB/JeztYVmTshrFWQ==", "G3oYMwnLVR9+m7WB4/pvoVeHxzsTdeyhndpVoruHzog=", "VAS Layer"),
		"b9d189ec-fc47-4792-8018-db914057d964", new ClientEntity("b9d189ec-fc47-4792-8018-db914057d964", null, "GTEguCGAl26phPgy5CltzL5S5HSZvYW374BKT9S5kpKU0or5QOLX3C2MEcHStW/ha4YvtOGVeKRzyCFZPQsCdA==", "whdzGzRYlZQKpA4sESCNFKBV5PirHS0fFya2Qxxc0zQ=", "Other Server App"));

	/**
	 * 
	 * @param cliendId
	 * @return
	 */
	public Uni<Optional<ClientEntity>> findByIdOptional(String cliendId) {
		return item(Optional.ofNullable(REPOSITORY.get(cliendId)));
	}
}