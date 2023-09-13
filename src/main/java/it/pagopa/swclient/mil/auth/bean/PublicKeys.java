/*
 * PublicKeys.java
 *
 * 21 mar 2023
 */
package it.pagopa.swclient.mil.auth.bean;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

/**
 * @author Antonio Tarricone
 */
@RegisterForReflection
@AllArgsConstructor
@Getter
@ToString
@EqualsAndHashCode
public class PublicKeys {
    /*
     *
     */
    private List<PublicKey> keys;
}