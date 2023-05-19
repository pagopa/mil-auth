/*
 * GrantRepository.java
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
import jakarta.enterprise.context.ApplicationScoped;

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
			"terminalId", "NA"));

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
			"terminalId", "NA"));

		List<String> grants2 = List.of(
			"verifyByQrCode",
			"activateByQrCode",
			"verifyByTaxCodeAndNoticeNumber",
			"activateByTaxCodeAndNoticeNumber",
			"close",
			"getPaymentStatus",
			"getFee");

		Document doc3 = new Document(Map.of(
			"acquirerId", "NA",
			"channel", "NA",
			"clientId", "92faf319-4219-455f-841b-bb692684672a",
			"merchantId", "NA",
			"terminalId", "NA"));

		List<String> grants3 = List.of(
			"verifyByQrCode",
			"activateByQrCode",
			"verifyByTaxCodeAndNoticeNumber",
			"activateByTaxCodeAndNoticeNumber",
			"close",
			"getPaymentStatus",
			"getFee");

		REPOSITORY = Map.of(
			doc2, new GrantEntity("4585625", "POS", "2f3cd44c-d11a-4ba6-a3e9-112a4710e12e", "28405fHfk73x88D", "NA", grants2),
			doc3, new GrantEntity("NA", "NA", "92faf319-4219-455f-841b-bb692684672a", "NA", "NA", grants3),
			doc10, new GrantEntity("4585625", "POS", "5254f087-1214-45cd-94ae-fda53c835197", "28405fHfk73x88D", "NA", grants1),
			doc11, new GrantEntity("4585625", "POS", "5254f087-1214-45cd-94ae-fda53c835197", "12346789", "NA", grants1),
			doc12, new GrantEntity("4585625", "POS", "5254f087-1214-45cd-94ae-fda53c835197", "999999600307", "NA", grants1));
	}

	/**
	 * ReactivePanacheQuery<Entity>.find(Document).singleResultOptional()
	 * 
	 * @param document
	 * @return
	 */
	public Uni<Optional<GrantEntity>> findSingleResultOptional(Document document) {
		return item(Optional.ofNullable(REPOSITORY.get(document)));
	}
}