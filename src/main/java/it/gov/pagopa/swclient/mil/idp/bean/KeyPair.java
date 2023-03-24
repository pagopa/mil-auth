/*
 * KeyPair.java
 *
 * 21 mar 2023
 */
package it.gov.pagopa.swclient.mil.idp.bean;

/**
 * 
 * @author Antonio Tarricone
 */
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
	 */
	public KeyPair() {
	}

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
	 * @param d the d to set
	 */
	public void setD(String d) {
		this.d = d;
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
	 * @param e the e to set
	 */
	public void setE(String e) {
		this.e = e;
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
	 * @param use the use to set
	 */
	public void setUse(KeyUse use) {
		this.use = use;
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
	 * @return the dp
	 */
	public String getDp() {
		return dp;
	}

	/**
	 * 
	 * @param dp the dp to set
	 */
	public void setDp(String dp) {
		this.dp = dp;
	}

	/**
	 * 
	 * @return the dq
	 */
	public String getDq() {
		return dq;
	}

	/**
	 * 
	 * @param dq the dq to set
	 */
	public void setDq(String dq) {
		this.dq = dq;
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
	 * @param n the n to set
	 */
	public void setN(String n) {
		this.n = n;
	}

	/**
	 * 
	 * @return the p
	 */
	public String getP() {
		return p;
	}

	/**
	 * 
	 * @param p the p to set
	 */
	public void setP(String p) {
		this.p = p;
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
	 * @param kty the kty to set
	 */
	public void setKty(KeyType kty) {
		this.kty = kty;
	}

	/**
	 * 
	 * @return the q
	 */
	public String getQ() {
		return q;
	}

	/**
	 * 
	 * @param q the q to set
	 */
	public void setQ(String q) {
		this.q = q;
	}

	/**
	 * 
	 * @return the qi
	 */
	public String getQi() {
		return qi;
	}

	/**
	 * 
	 * @param qi the qi to set
	 */
	public void setQi(String qi) {
		this.qi = qi;
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
	 * @param iat the iat to set
	 */
	public void setIat(long iat) {
		this.iat = iat;
	}

	/**
	 * 
	 * @return
	 */
	public PublicKey getPublicKey() {
		return new PublicKey(e, use, kid, n, kty, exp, iat);
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return new StringBuilder("KeyPair [d=")
			.append("***")
			.append(", e=")
			.append(e)
			.append(", use=")
			.append(use.name())
			.append(", kid=")
			.append(kid)
			.append(", dp=")
			.append("***")
			.append(", dq=")
			.append("***")
			.append(", n=")
			.append(n)
			.append(", p=")
			.append("***")
			.append(", kty=")
			.append(kty.name())
			.append(", q=")
			.append("***")
			.append(", qi=")
			.append("***")
			.append(", exp=")
			.append(exp)
			.append(", iat=")
			.append(iat)
			.append("]")
			.toString();
	}
}