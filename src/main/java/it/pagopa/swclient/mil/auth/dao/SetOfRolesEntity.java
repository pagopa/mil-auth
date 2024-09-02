/*
 * SetOfRolesEntity.java
 *
 * 9 lug 2024
 */
package it.pagopa.swclient.mil.auth.dao;

import java.util.List;

import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;

import io.quarkus.mongodb.panache.common.MongoEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 
 * @author Antonio Tarricone
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Accessors(chain = true)
@MongoEntity(database = "mil", collection = "roles")
public class SetOfRolesEntity {
	/*
	 * Properties name.
	 */
	public static final String ID_PRP = "id";
	public static final String ACQUIRER_ID_PRP = "acquirerId";
	public static final String CHANNEL_PRP = "channel";
	public static final String CLIENT_ID_PRP = "clientId";
	public static final String MERCHANT_ID_PRP = "merchantId";
	public static final String TERMINAL_ID_PRP = "terminalId";
	public static final String ROLES_PRP = "roles";

	/*
	 * Used by MongoDB for the _id field.
	 */
	@BsonId
	public ObjectId id;

	/*
	 *
	 */
	@BsonProperty(value = ID_PRP)
	public String setOfRolesId;

	/*
	 *
	 */
	@BsonProperty(value = ACQUIRER_ID_PRP)
	public String acquirerId;

	/*
	 *
	 */
	@BsonProperty(value = CHANNEL_PRP)
	public String channel;

	/*
	 *
	 */
	@BsonProperty(value = CLIENT_ID_PRP)
	public String clientId;

	/*
	 *
	 */
	@BsonProperty(value = MERCHANT_ID_PRP)
	public String merchantId;

	/*
	 *
	 */
	@BsonProperty(value = TERMINAL_ID_PRP)
	public String terminalId;

	/*
	 *
	 */
	@BsonProperty(value = ROLES_PRP)
	public List<String> roles;

	/**
	 * 
	 * @param setOfRolesId
	 * @param acquirerId
	 * @param channel
	 * @param clientId
	 * @param merchantId
	 * @param terminalId
	 * @param roles
	 */
	public SetOfRolesEntity(String setOfRolesId, String acquirerId, String channel, String clientId, String merchantId, String terminalId, List<String> roles) {
		this.setOfRolesId = setOfRolesId;
		this.acquirerId = acquirerId;
		this.channel = channel;
		this.clientId = clientId;
		this.merchantId = merchantId;
		this.terminalId = terminalId;
		this.roles = roles;
	}
}