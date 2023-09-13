/*
 * CreateKeyRequest.java
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
public class CreateKeyRequest {
    /*
     *
     */
    @JsonProperty("kty")
    private String kty;

    /*
     *
     */
    @JsonProperty("key_size")
    private Integer keySize;

    /*
     *
     */
    @JsonProperty("key_ops")
    private String[] keyOps;

    /*
     *
     */
    @JsonProperty("attributes")
    private KeyAttributes attributes;
}