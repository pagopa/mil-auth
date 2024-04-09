/*
 * Flow.java
 *
 * 9 apr 2024
 */
package it.pagopa.swclient.mil.auth.service.token;

/**
 * 
 * @author Antonio Tarricone
 */
public abstract class Flow {
	/**
	 * Retrieves client data.
	 * 
	 * @param bundle
	 * @return
	 */
	public Bundle retrieveClient(Bundle bundle);
	
	/**
	 * Verifies client expiration.
	 * 
	 * @param bundle
	 * @return
	 */
	public Bundle verifyClientExpiration(Bundle bundle);
	
	/**
	 * Verifies consistency between client and request data.
	 * 
	 * @param bundle
	 * @return
	 */
	public Bundle verifyRequestConsistency(Bundle bundle);
	
	/**
	 * Verifies client secret.
	 * 
	 * @param bundle
	 * @return
	 */
	public Bundle verifyClientSecret(Bundle bundle);
	
	/**
	 * Verifies refresh token.
	 * 
	 * @param bundle
	 * @return
	 */
	public Bundle verifyRefreshToken(Bundle bundle);
	
	/**
	 * Retrieves terminal data (if any).
	 * 
	 * @param bundle
	 * @return
	 */
	public Bundle retrieveTerminal(Bundle bundle);
	
	/**
	 * Retrieves roles.
	 * 
	 * @param bundle
	 * @return
	 */
	public Bundle retrieveRoles(Bundle bundle);
	
	
	/**
	 * Protects user tax code.
	 * 
	 * @param bundle
	 * @return
	 */
	public Bundle protectUserTaxCode(Bundle bundle);
	
	/**
	 * Generates access token payload.
	 * 
	 * @param bundle
	 * @return
	 */
	public Bundle generateAccessTokenPayload(Bundle bundle);
	
	/**
	 * Retrieves key to sign the token.
	 * 
	 * @param bundle
	 * @return
	 */
	public Bundle retrieveKey(Bundle bundle);
	
	/**
	 * Creates a new key.
	 * 
	 * @param bundle
	 * @return
	 */
	public Bundle createKey(Bundle bundle);
	
	/**
	 * Signs the token.
	 * 
	 * @param bundle
	 * @return
	 */
	public Bundle signToken(Bundle bundle);
	
	/**
	 * Main method: generates token (access and refresh if any).
	 * 
	 * @param bundle
	 * @return
	 */
	public Bundle generateToken(Bundle bundle);
}