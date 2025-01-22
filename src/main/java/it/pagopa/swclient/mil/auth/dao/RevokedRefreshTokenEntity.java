/*
 * RevokedRefreshTokenEntity.java
 *
 * 13 gen 2025
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
@MongoEntity(database = "mil", collection = "revokedRefreshTokens")
public class RevokedRefreshTokenEntity {
	/*
	 * Properties name.
	 */
	public static final String JWT_ID = "jwtId";

	/*
	 * Used by MongoDB for the _id field.
	 */
	@BsonId
	public ObjectId id;

	/*
	 *
	 */
	@BsonProperty(value = JWT_ID)
	public String jwtId;
}
