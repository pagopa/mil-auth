/*
 * GetAccessTokenResponse.java
 *
 * 21 lug 2023
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
public class GetAccessTokenResponse {
    /*
     *
     */
    @JsonProperty("token_type")
    private String type;

    /*
     *
     */
    @JsonProperty("expires_in")
    private long expiresIn;

    /*
     *
     */
    @JsonProperty("ext_expires_in")
    private long extExpiresIn;

    /*
     *
     */
    @JsonProperty("access_token")
    private String token;
}