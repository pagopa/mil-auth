/*
 * User.java
 *
 * 20 mar 2023
 */
package it.pagopa.swclient.mil.auth.bean;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * @author Antonio Tarricone
 */
@AllArgsConstructor
@Getter
@ToString
public class User {
    /*
     *
     */
    private String username;

    /*
     *
     */
    private String salt;

    /*
     *
     */
    private String passwordHash;

    /*
     *
     */
    private String acquirerId;

    /*
     *
     */
    private String channel;

    /*
     *
     */
    private String merchantId;
}