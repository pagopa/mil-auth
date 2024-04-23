/*
 * JsonWebKey.java
 *
 * 11 apr 2024
 */
package it.pagopa.swclient.mil.auth.azure.keyvaultkeys.bean;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import io.quarkus.runtime.annotations.RegisterForReflection;
import it.pagopa.swclient.mil.auth.azure.keyvaultkeys.util.ByteArrayDeserializer;
import it.pagopa.swclient.mil.auth.azure.keyvaultkeys.util.ByteArraySerializer;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * As of http://tools.ietf.org/html/draft-ietf-jose-json-web-key-18
 * 
 * @see <a href=
 *      "https://learn.microsoft.com/en-us/rest/api/keyvault/keys/create-key/create-key?view=rest-keyvault-keys-7.4&tabs=HTTP#jsonwebkey">Microsoft
 *      Azure Documentation</a>
 * @author Antonio Tarricone
 */
@RegisterForReflection
@Getter
@Setter
@Accessors(chain = true)
@JsonInclude(value = Include.NON_NULL)
public class JsonWebKey {
	/*
	 * 
	 */
	public static final String CRV = "crv";
	public static final String D = "d";
	public static final String DP = "dp";
	public static final String DQ = "dq";
	public static final String E = "e";
	public static final String K = "k";
	public static final String KEY_HSM = "key_hsm";
	public static final String KEY_OPS = "key_ops";
	public static final String KID = "kid";
	public static final String KTY = "kty";
	public static final String N = "n";
	public static final String P = "p";
	public static final String Q = "q";
	public static final String QI = "qi";
	public static final String X = "x";
	public static final String Y = "y";

	/*
	 * Elliptic curve name. For valid values, see JsonWebKeyCurveName.
	 */
	@JsonProperty(CRV)
	private JsonWebKeyCurveName crv;

	/*
	 * RSA private exponent, or the D component of an EC private key.
	 */
	@JsonProperty(D)
	@JsonSerialize(using = ByteArraySerializer.class)
	@JsonDeserialize(using = ByteArrayDeserializer.class)
	private byte[] d;

	/*
	 * RSA private key parameter.
	 */
	@JsonProperty(DP)
	@JsonSerialize(using = ByteArraySerializer.class)
	@JsonDeserialize(using = ByteArrayDeserializer.class)
	private byte[] dp;

	/*
	 * RSA private key parameter.
	 */
	@JsonProperty(DQ)
	@JsonSerialize(using = ByteArraySerializer.class)
	@JsonDeserialize(using = ByteArrayDeserializer.class)
	private byte[] dq;

	/*
	 * RSA public exponent.
	 */
	@JsonProperty(E)
	@JsonSerialize(using = ByteArraySerializer.class)
	@JsonDeserialize(using = ByteArrayDeserializer.class)
	private byte[] e;

	/*
	 * Symmetric key.
	 */
	@JsonProperty(K)
	@JsonSerialize(using = ByteArraySerializer.class)
	@JsonDeserialize(using = ByteArrayDeserializer.class)
	private byte[] k;

	/*
	 * Protected Key, used with 'Bring Your Own Key'.
	 */
	@JsonProperty(KEY_HSM)
	@JsonSerialize(using = ByteArraySerializer.class)
	@JsonDeserialize(using = ByteArrayDeserializer.class)
	private byte[] keyHsm;

	/*
	 * Supported key operations.
	 */
	@JsonProperty(KEY_OPS)
	private List<JsonWebKeyOperation> keyOps;

	/*
	 * Key identifier.
	 */
	@JsonProperty(KID)
	private String kid;

	/*
	 * JsonWebKey Key Type (kty), as defined in
	 * https://tools.ietf.org/html/draft-ietf-jose-json-web-algorithms-40.
	 */
	@JsonProperty(KTY)
	private JsonWebKeyType kty;

	/*
	 * RSA modulus.
	 */
	@JsonProperty(N)
	@JsonSerialize(using = ByteArraySerializer.class)
	@JsonDeserialize(using = ByteArrayDeserializer.class)
	private byte[] n;

	/*
	 * RSA secret prime.
	 */
	@JsonProperty(P)
	@JsonSerialize(using = ByteArraySerializer.class)
	@JsonDeserialize(using = ByteArrayDeserializer.class)
	private byte[] p;

	/*
	 * RSA secret prime, with p < q.
	 */
	@JsonProperty(Q)
	@JsonSerialize(using = ByteArraySerializer.class)
	@JsonDeserialize(using = ByteArrayDeserializer.class)
	private byte[] q;

	/*
	 * RSA private key parameter.
	 */
	@JsonProperty(QI)
	@JsonSerialize(using = ByteArraySerializer.class)
	@JsonDeserialize(using = ByteArrayDeserializer.class)
	private byte[] qi;

	/*
	 * X component of an EC public key.
	 */
	@JsonProperty(X)
	@JsonSerialize(using = ByteArraySerializer.class)
	@JsonDeserialize(using = ByteArrayDeserializer.class)
	private byte[] x;

	/*
	 * Y component of an EC public key.
	 */
	@JsonProperty(Y)
	@JsonSerialize(using = ByteArraySerializer.class)
	@JsonDeserialize(using = ByteArrayDeserializer.class)
	private byte[] y;
}