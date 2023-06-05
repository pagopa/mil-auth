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
public class PublicKey implements Cloneable {
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
	 * @param e the e to set
	 */
	public void setE(String e) {
		this.e = e;
	}

	/**
	 * @param use the use to set
	 */
	public void setUse(KeyUse use) {
		this.use = use;
	}

	/**
	 * @param kid the kid to set
	 */
	public void setKid(String kid) {
		this.kid = kid;
	}

	/**
	 * @param n the n to set
	 */
	public void setN(String n) {
		this.n = n;
	}

	/**
	 * @param kty the kty to set
	 */
	public void setKty(KeyType kty) {
		this.kty = kty;
	}

	/**
	 * @param exp the exp to set
	 */
	public void setExp(long exp) {
		this.exp = exp;
	}

	/**
	 * @param iat the iat to set
	 */
	public void setIat(long iat) {
		this.iat = iat;
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

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Object clone() throws CloneNotSupportedException {
		return new PublicKey(e, use, kid, n, kty, exp, iat);
	}
}