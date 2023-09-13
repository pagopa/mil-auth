/*
 * KeyNameAndVersion.java
 *
 * 27 lug 2023
 */
package it.pagopa.swclient.mil.auth.azurekeyvault.bean;

import lombok.*;

/**
 * @author Antonio Tarricone
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class KeyNameAndVersion {
    /*
     *
     */
    private String name;

    /*
     *
     */
    private String version;

    /**
     * @return
     */
    public boolean isValid() {
        return name != null && version != null;
    }
}