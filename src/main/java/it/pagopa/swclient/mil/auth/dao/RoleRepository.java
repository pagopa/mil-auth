/*
 * RoleRepository.java
 *
 * 20 mar 2023
 */
package it.pagopa.swclient.mil.auth.dao;

import static it.pagopa.swclient.mil.auth.util.UniGenerator.item;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.bson.Document;

import io.smallrye.mutiny.Uni;
import it.pagopa.swclient.mil.auth.bean.Role;
import it.pagopa.swclient.mil.bean.Channel;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * 
 * FOR DEMO ONLY. THIS WILL BE REPLACED BY DB.
 * 
 * @author Antonio Tarricone
 */
@ApplicationScoped
public class RoleRepository {
	/*
	 * 
	 */
	private static final Map<Document, RoleEntity> REPOSITORY;
	static {
		/*
		 * SmartPOS
		 */
		Document smartPos1Doc = new Document(Map.of(
			"acquirerId", "4585625",
			"channel", Channel.POS,
			"clientId", "5254f087-1214-45cd-94ae-fda53c835197",
			"merchantId", "28405fHfk73x88D",
			"terminalId", "NA"));

		Document smartPos2Doc = new Document(Map.of(
			"acquirerId", "4585625",
			"channel", Channel.POS,
			"clientId", "5254f087-1214-45cd-94ae-fda53c835197",
			"merchantId", "12346789",
			"terminalId", "NA"));

		Document smartPos3Doc = new Document(Map.of(
			"acquirerId", "4585625",
			"channel", Channel.POS,
			"clientId", "5254f087-1214-45cd-94ae-fda53c835197",
			"merchantId", "999999600307",
			"terminalId", "NA"));

		List<String> smartPosRoles = List.of(
			"verifyByQrCode",
			"activateByQrCode",
			"verifyByTaxCodeAndNoticeNumber",
			"activateByTaxCodeAndNoticeNumber",
			"close",
			"getPaymentStatus",
			"getFee");

		/*
		 * SoftPOS
		 */
		Document softPosDoc = new Document(Map.of(
			"acquirerId", "4585625",
			"channel", Channel.POS,
			"clientId", "2f3cd44c-d11a-4ba6-a3e9-112a4710e12e",
			"merchantId", "28405fHfk73x88D",
			"terminalId", "NA"));

		List<String> softPosRoles = List.of(Role.NoticePayer.name());

		/*
		 * Nodo
		 */
		Document nodoDoc = new Document(Map.of(
			"acquirerId", "NA",
			"channel", "NA",
			"clientId", "92faf319-4219-455f-841b-bb692684672a",
			"merchantId", "NA",
			"terminalId", "NA"));

		List<String> nodoRoles = List.of(Role.Nodo.name());

		/*
		 * Other Server App
		 */
		Document otherServerAppDoc = new Document(Map.of(
			"acquirerId", "NA",
			"channel", "NA",
			"clientId", "b9d189ec-fc47-4792-8018-db914057d964",
			"merchantId", "NA",
			"terminalId", "NA"));

		List<String> otherServerAppRoles = List.of(Role.ServiceListRequester.name());

		/*
		 * Repository
		 */
		REPOSITORY = Map.of(
			softPosDoc, new RoleEntity("4585625", Channel.POS, "2f3cd44c-d11a-4ba6-a3e9-112a4710e12e", "28405fHfk73x88D", "NA", softPosRoles),
			nodoDoc, new RoleEntity("NA", "NA", "92faf319-4219-455f-841b-bb692684672a", "NA", "NA", nodoRoles),
			smartPos1Doc, new RoleEntity("4585625", Channel.POS, "5254f087-1214-45cd-94ae-fda53c835197", "28405fHfk73x88D", "NA", smartPosRoles),
			smartPos2Doc, new RoleEntity("4585625", Channel.POS, "5254f087-1214-45cd-94ae-fda53c835197", "12346789", "NA", smartPosRoles),
			smartPos3Doc, new RoleEntity("4585625", Channel.POS, "5254f087-1214-45cd-94ae-fda53c835197", "999999600307", "NA", smartPosRoles),
			otherServerAppDoc, new RoleEntity("NA", "NA", "b9d189ec-fc47-4792-8018-db914057d964", "NA", "NA", otherServerAppRoles));
	}

	/**
	 * ReactivePanacheQuery<Entity>.find(Document).singleResultOptional()
	 * 
	 * @param document
	 * @return
	 */
	public Uni<Optional<RoleEntity>> findSingleResultOptional(Document document) {
		return item(Optional.ofNullable(REPOSITORY.get(document)));
	}
}