/*
 * GrantRepository.java
 *
 * 20 mar 2023
 */
package it.gov.pagopa.swclient.mil.idp.dao;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;

import org.bson.Document;

import io.smallrye.mutiny.Uni;

/**
 * 
 * @author Antonio Tarricone
 */
@ApplicationScoped
public class GrantRepository {
	/*
	 * 
	 */
	private static final Map<Document, GrantEntity> REPOSITORY;
	static {
		Document doc10 = new Document(Map.of(
			"acquirerId", "4585625",
			"channel", "POS",
			"clientId", "5254f087-1214-45cd-94ae-fda53c835197",
			"merchantId", "28405fHfk73x88D",
			"terminalId", "*"));

		Document doc11 = new Document(Map.of(
			"acquirerId", "4585625",
			"channel", "POS",
			"clientId", "5254f087-1214-45cd-94ae-fda53c835197",
			"merchantId", "12346789",
			"terminalId", "*"));

		Document doc12 = new Document(Map.of(
			"acquirerId", "4585625",
			"channel", "POS",
			"clientId", "5254f087-1214-45cd-94ae-fda53c835197",
			"merchantId", "999999600307",
			"terminalId", "*"));

		List<String> grants1 = List.of(
			"verifyByQrCode",
			"activateByQrCode",
			"verifyByTaxCodeAndNoticeNumber",
			"activateByTaxCodeAndNoticeNumber",
			"close",
			"getPaymentStatus",
			"getFee");

		Document doc2 = new Document(Map.of(
			"acquirerId", "4585625",
			"channel", "POS",
			"clientId", "2f3cd44c-d11a-4ba6-a3e9-112a4710e12e",
			"merchantId", "28405fHfk73x88D",
			"terminalId", "*"));

		List<String> grants2 = List.of(
			"verifyByQrCode",
			"activateByQrCode",
			"verifyByTaxCodeAndNoticeNumber",
			"activateByTaxCodeAndNoticeNumber",
			"close",
			"getPaymentStatus",
			"getFee");

		REPOSITORY = Map.of(
			doc10, new GrantEntity("4585625", "POS", "5254f087-1214-45cd-94ae-fda53c835197", "28405fHfk73x88D", "*", grants1),
			doc11, new GrantEntity("4585625", "POS", "5254f087-1214-45cd-94ae-fda53c835197", "12346789", "*", grants1),
			doc12, new GrantEntity("4585625", "POS", "5254f087-1214-45cd-94ae-fda53c835197", "999999600307", "*", grants1),
			doc2, new GrantEntity("4585625", "POS", "2f3cd44c-d11a-4ba6-a3e9-112a4710e12e", "28405fHfk73x88D", "*", grants2));
	}

	/**
	 * ReactivePanacheQuery<Entity>.find(Document).singleResultOptional()
	 * 
	 * @param document
	 * @return
	 */
	public Uni<Optional<GrantEntity>> findSingleResultOptional(Document document) {
		return Uni.createFrom().item(Optional.ofNullable(REPOSITORY.get(document)));
	}
}
