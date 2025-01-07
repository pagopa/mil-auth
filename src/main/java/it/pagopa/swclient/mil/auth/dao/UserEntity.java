/*
 * User.java
 *
 * 20 mar 2023
 */
package it.pagopa.swclient.mil.auth.dao;

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
@MongoEntity(database = "mil", collection = "users")
public class UserEntity {
	/*
	 * Properties name.
	 */
	public static final String USER_ID_PRP = "userId";
	public static final String USERNAME_PRP = "username";
	public static final String CHANNEL_PRP = "channel";
	public static final String SALT_PRP = "salt";
	public static final String PASSWORD_HASH_PRP = "passwordHash";
	public static final String ACQUIRER_ID_PRP = "acquirerId";
	public static final String MERCHANT_ID_PRP = "merchantId";
	public static final String CLIENT_ID_PRP = "clientId";

	/*
	 * Used by MongoDB for the _id field.
	 */
	@BsonId
	public ObjectId id;

	/*
	 *
	 */
	@BsonProperty(value = USER_ID_PRP)
	public String userId;

	/*
	 *
	 */
	@BsonProperty(value = USERNAME_PRP)
	public String username;

	/*
	 *
	 */
	@BsonProperty(value = CHANNEL_PRP)
	public String channel;

	/*
	 * Base64 string.
	 */
	@BsonProperty(value = SALT_PRP)
	public String salt;

	/*
	 * Base64 string.
	 */
	@BsonProperty(value = PASSWORD_HASH_PRP)
	public String passwordHash;

	/*
	 *
	 */
	@BsonProperty(value = ACQUIRER_ID_PRP)
	public String acquirerId;

	/*
	 *
	 */
	@BsonProperty(value = MERCHANT_ID_PRP)
	public String merchantId;

	/*
	 *
	 */
	@BsonProperty(value = CLIENT_ID_PRP)
	public String clientId;

	/**
	 * 
	 * @param userId
	 * @param username
	 * @param channel
	 * @param salt
	 * @param passwordHash
	 * @param acquirerId
	 * @param merchantId
	 * @param clientId
	 */
	public UserEntity(String userId, String username, String channel, String salt, String passwordHash, String acquirerId, String merchantId, String clientId) { // NOSONAR
		this.userId = userId;
		this.username = username;
		this.channel = channel;
		this.salt = salt;
		this.passwordHash = passwordHash;
		this.acquirerId = acquirerId;
		this.merchantId = merchantId;
		this.clientId = clientId;
	}
}