/*
 * SignRequest.java
 *
 * 25 lug 2023
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
public class SignResponse {
    /*
     *
     */
    @JsonProperty("kid")
    private String kid;

    /*
     * Signature (Base64 URL-safe).
     */
    @JsonProperty("value")
    private String signature;
}
