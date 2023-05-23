/*
 * PublicKey.java
 *
 * 21 mar 2023
 */
package it.pagopa.swclient.mil.auth.bean;

import java.util.Objects;

import io.quarkus.runtime.annotations.RegisterForReflection;

/**
 * 
 * @author Antonio Tarricone
 */
@RegisterForReflection
public class PublicKey {
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
	 * Modulus
	 */
	private String n;

	/*
	 * Key type
	 */
	private KeyType kty;

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
	 * @param e
	 * @param use
	 * @param kid
	 * @param n
	 * @param kty
	 * @param exp
	 * @param iat
	 */
	public PublicKey(String e, KeyUse use, String kid, String n, KeyType kty, long exp, long iat) {
		this.e = e;
		this.use = use;
		this.kid = kid;
		this.n = n;
		this.kty = kty;
		this.exp = exp;
		this.iat = iat;
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
	 * 
	 * @return the iat
	 */
	public long getIat() {
		return iat;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return new StringBuilder("PublicKey [e=")
			.append(e)
			.append(", use=")
			.append(use)
			.append(", kid=")
			.append(kid)
			.append(", n=")
			.append(n)
			.append(", kty=")
			.append(kty)
			.append(", exp=")
			.append(exp)
			.append(", iat=")
			.append(iat)
			.append("]")
			.toString();
	}

	/**
	 * @see java.lang.Object#equals(Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PublicKey other = (PublicKey) obj;
		return Objects.equals(e, other.e)
			&& exp == other.exp
			&& iat == other.iat
			&& Objects.equals(kid, other.kid)
			&& kty == other.kty
			&& Objects.equals(n, other.n)
			&& use == other.use;
	}
}