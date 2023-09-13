/*
 * AuthError.java
 *
 * 27 apr 2023
 */
package it.pagopa.swclient.mil.auth.util;

/**
 * To be used if an application error occurs.
 *
 * @author Antonio Tarricone
 */
public class AuthError extends Error {
    /*
     *
     */

    private static final long serialVersionUID = 7785715776960328601L;

    /*
     *
     */
    private final String code;

    /**
     * @param code
     * @param message
     */
    public AuthError(String code, String message) {
        super(message);
        this.code = code;
    }

    /**
     * @return
     */
    public String getCode() {
        return code;
    }
}