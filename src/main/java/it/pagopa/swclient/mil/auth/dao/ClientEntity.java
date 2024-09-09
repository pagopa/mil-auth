/*
 * ClientEntity.java
 *
 * 9 lug 2024
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
@MongoEntity(database = "mil", collection = "clients")
public class ClientEntity {
	/*
	 * Properties name.
	 */
	public static final String CLIENT_ID_PRP = "clientId";
	public static final String CHANNEL_PRP = "channel";
	public static final String SALT_PRP = "salt";
	public static final String SECRET_HASH_PRP = "secretHash";
	public static final String DESCRIPTION_PRP = "description";
	public static final String SUBJECT_PRP = "subject";

	/*
	 * Used by MongoDB for the _id field.
	 */
	@BsonId
	public ObjectId id;

	/*
	 *
	 */
	@BsonProperty(value = CLIENT_ID_PRP)
	public String clientId;

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
	@BsonProperty(value = SECRET_HASH_PRP)
	public String secretHash;

	/*
	 *
	 */
	@BsonProperty(value = DESCRIPTION_PRP)
	public String description;

	/*
	 * 
	 */
	@BsonProperty(value = SUBJECT_PRP)
	public String subject;

	/**
	 * 
	 * @param clientId
	 * @param channel
	 * @param salt
	 * @param secretHash
	 * @param description
	 * @param subject
	 */
	public ClientEntity(String clientId, String channel, String salt, String secretHash, String description, String subject) {
		this.clientId = clientId;
		this.channel = channel;
		this.salt = salt;
		this.secretHash = secretHash;
		this.description = description;
		this.subject = subject;
	}
}