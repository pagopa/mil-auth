/*
 * GrantRepository.java
 *
 * 20 mar 2023
 */
package it.gov.pagopa.swclient.mil.idp.dao;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.bson.Document;

import io.smallrye.mutiny.Uni;

/**
 * 
 * @author Antonio Tarricone
 */
public class GrantRepository {
	/*
	 * 
	 */
	private static final Map<Document, GrantEntity> REPOSITORY;
	static {
		Document doc1 = new Document(Map.of(
			"acquirerId", "4585627",
			"channel", "POS",
			"clientId", "5254f087-1214-45cd-94ae-fda53c835197",
			"merchantId", "28405fHfk73x88D",
			"terminalId", "*"
		));
		
		List<String> grants1 = List.of(
			"verifyByQrCode",
			"activateByQrCode",
			"verifyByTaxCodeAndNoticeNumber",
			"activateByTaxCodeAndNoticeNumber",
			"close",
			"getPaymentStatus",
			"getFee"
		);
		
		Document doc2 = new Document(Map.of(
			"acquirerId", "4585627",
			"channel", "POS",
			"clientId", "2f3cd44c-d11a-4ba6-a3e9-112a4710e12e",
			"merchantId", "28405fHfk73x88D",
			"terminalId", "*"
		));
		
		List<String> grants2 = List.of(
			"verifyByQrCode",
			"activateByQrCode",
			"verifyByTaxCodeAndNoticeNumber",
			"activateByTaxCodeAndNoticeNumber",
			"close",
			"getPaymentStatus",
			"getFee"
		);
		
		REPOSITORY = Map.of(
			doc1, new GrantEntity("4585627", "POS", "5254f087-1214-45cd-94ae-fda53c835197", "28405fHfk73x88D", "*", grants1),
			doc2, new GrantEntity("4585627", "POS", "2f3cd44c-d11a-4ba6-a3e9-112a4710e12e", "28405fHfk73x88D", "*", grants2)
		);
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
