/*
 * Key.java
 *
 * 23 lug 2023
 */
package it.pagopa.swclient.mil.auth.azurekeyvault.bean;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.*;

/**
 * @author Antonio Tarricone
 */
@RegisterForReflection
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Key {
    /*
     *
     */
    @JsonProperty("kid")
    private String kid;

    /*
     *
     */
    @JsonProperty("attributes")
    private KeyAttributes attributes;
}