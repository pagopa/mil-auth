/*
 * KeyPair.java
 *
 * 21 mar 2023
 */
package it.pagopa.swclient.mil.auth.bean;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.*;

/**
 * @author Antonio Tarricone
 */
@RegisterForReflection
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
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
     * @return
     */
    public PublicKey publicKey() {
        return new PublicKey(e, use, kid, n, kty, exp, iat);
    }
}