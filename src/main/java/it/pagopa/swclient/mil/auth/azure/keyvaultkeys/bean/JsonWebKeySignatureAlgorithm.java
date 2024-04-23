/*
 * JsonWebKeySignatureAlgorithm.java
 *
 * 11 apr 2024
 */
package it.pagopa.swclient.mil.auth.azure.keyvaultkeys.bean;

/**
 * The signing/verification algorithm identifier. For more information on possible algorithm types,
 * see JsonWebKeySignatureAlgorithm.
 * 
 * @see <a href=
 *      "https://learn.microsoft.com/en-us/rest/api/keyvault/keys/sign/sign?view=rest-keyvault-keys-7.4&tabs=HTTP#jsonwebkeysignaturealgorithm">Microsoft
 *      Azure Documentation</a>
 * @author Antonio Tarricone
 */
public enum JsonWebKeySignatureAlgorithm {
	/*
	 * ECDSA using P-256 and SHA-256, as described in https://tools.ietf.org/html/rfc7518.
	 */
	ES256,

	/*
	 * ECDSA using P-256K and SHA-256, as described in https://tools.ietf.org/html/rfc7518
	 */
	ES256K,

	/*
	 * ECDSA using P-384 and SHA-384, as described in https://tools.ietf.org/html/rfc7518
	 */
	ES384,

	/*
	 * ECDSA using P-521 and SHA-512, as described in https://tools.ietf.org/html/rfc7518
	 */

	ES512,

	/*
	 * RSASSA-PSS using SHA-256 and MGF1 with SHA-256, as described in
	 * https://tools.ietf.org/html/rfc7518
	 */
	PS256,

	/*
	 * RSASSA-PSS using SHA-384 and MGF1 with SHA-384, as described in
	 * https://tools.ietf.org/html/rfc7518
	 */
	PS384,

	/*
	 * RSASSA-PSS using SHA-512 and MGF1 with SHA-512, as described in
	 * https://tools.ietf.org/html/rfc7518
	 */
	PS512,

	/*
	 * RSASSA-PKCS1-v1_5 using SHA-256, as described in https://tools.ietf.org/html/rfc7518
	 */
	RS256,

	/*
	 * RSASSA-PKCS1-v1_5 using SHA-384, as described in https://tools.ietf.org/html/rfc7518
	 */
	RS384,

	/*
	 * RSASSA-PKCS1-v1_5 using SHA-512, as described in https://tools.ietf.org/html/rfc7518
	 */
	RS512,

	/*
	 * Reserved.
	 */
	RSNULL;
}