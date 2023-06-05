/*
 * KeyPair.java
 *
 * 21 mar 2023
 */
package it.pagopa.swclient.mil.auth.bean;

import io.quarkus.runtime.annotations.RegisterForReflection;

/**
 * 
 * @author Antonio Tarricone
 */
@RegisterForReflection
@SuppressWarnings("unused")
public class KeyPair {
	/*
	 * Private exponent
	 */
	private String d;

	/*
	 * Public exponent
	 */
	private String e;

	/*
	 * Public key use
	 */
	private KeyUse use;

	/*
	 * Key ID
	 */
	private String kid;

	/*
	 * Chinese remainder theorem exponent of the first factor
	 */
	private String dp;

	/*
	 * Chinese remainder theorem exponent of the second factor
	 */
	private String dq;

	/*
	 * Modulus
	 */
	private String n;

	/*
	 * First prime factor
	 */
	private String p;

	/*
	 * Key type
	 */
	private KeyType kty;

	/*
	 * Second prime factor
	 */
	private String q;

	/*
	 * First Chinese remainder theorem coefficient
	 */
	private String qi;

	/*
	 * Expiration time
	 */
	private long exp;

	/*
	 * Issued at
	 */
	private long iat;

	/**
	 * 
	 * @param d
	 * @param e
	 * @param use
	 * @param kid
	 * @param dp
	 * @param dq
	 * @param n
	 * @param p
	 * @param kty
	 * @param q
	 * @param qi
	 * @param exp
	 * @param iat
	 */
	public KeyPair(String d, String e, KeyUse use, String kid, String dp, String dq, String n, String p, KeyType kty, String q, String qi, long exp, long iat) {
		this.d = d;
		this.e = e;
		this.use = use;
		this.kid = kid;
		this.dp = dp;
		this.dq = dq;
		this.n = n;
		this.p = p;
		this.kty = kty;
		this.q = q;
		this.qi = qi;
		this.exp = exp;
		this.iat = iat;
	}

	/**
	 * 
	 * @return the d
	 */
	public String getD() {
		return d;
	}

	/**
	 * 
	 * @return the e
	 */
	public String getE() {
		return e;
	}

	/**
	 * 
	 * @return the use
	 */
	public KeyUse getUse() {
		return use;
	}

	/**
	 * 
	 * @return the kid
	 */
	public String getKid() {
		return kid;
	}

	/**
	 * 
	 * @param kid the kid to set
	 */
	public void setKid(String kid) {
		this.kid = kid;
	}

	/**
	 * 
	 * @return the n
	 */
	public String getN() {
		return n;
	}

	/**
	 * 
	 * @return the kty
	 */
	public KeyType getKty() {
		return kty;
	}

	/**
	 * 
	 * @return the exp
	 */
	public long getExp() {
		return exp;
	}

	/**
	 * @param exp the exp to set
	 */
	public void setExp(long exp) {
		this.exp = exp;
	}

	/**
	 * 
	 * @return the iat
	 */
	public long getIat() {
		return iat;
	}

	/**
	 * 
	 * @return
	 */
	public PublicKey publicKey() {
		return new PublicKey(e, use, kid, n, kty, exp, iat);
	}
}