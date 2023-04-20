package it.gov.pagopa.swclient.mil.idp.resource;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.anyString;

import java.util.ArrayList;
import java.util.Optional;

import javax.ws.rs.core.MediaType;

import io.quarkus.test.junit.QuarkusMock;
import it.gov.pagopa.swclient.mil.idp.bean.*;
import it.gov.pagopa.swclient.mil.idp.service.KeyRetriever;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;

import com.nimbusds.jose.JOSEException;

import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.smallrye.mutiny.Uni;
import it.gov.pagopa.swclient.mil.idp.ErrorCode;
import it.gov.pagopa.swclient.mil.idp.client.PoyntClient;
import it.gov.pagopa.swclient.mil.idp.service.RedisClient;

@QuarkusTest
@TestHTTPEndpoint(TokenResource.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TokenGenerationTest {
    /*
     *
     */
    private static final String CLIENT_ID = "5254f087-1214-45cd-94ae-fda53c835197";
    private static final String ACQUIRER_ID = "4585625";
    private static final String CHANNEL = "POS";
    private static final String MERCHANT_ID = "28405fHfk73x88D";
    private static final String TERMINAL_ID = "12345678";
    private static final String BUSINESS_ID = "4b7eb94b-10c9-4f11-a10e-7292b29ab115";

    private static final String USERNAME = "antonio.tarricone";

    private static final String PASSWORD = "antonio";

    /*
     *
     */
    @InjectMock
    RedisClient redisClient;

    /*
     *
     */
    @InjectMock
    @RestClient
    PoyntClient poyntClient;

    /**
     *
     */
    @BeforeAll
    public void setup() {
        Mockito
                .when(redisClient.keys("*"))
                .thenReturn(Uni.createFrom().item(new ArrayList<String>()));

        KeyPair k = new KeyPair("OCCNJzAIHdSrjQFPvgCR0vvGHWKz7RZSLdmRhN4HAyZLC-OBi2lRZDz8LTtaLlm5bpQmevIRRnKCLXYxcNRK-Ei3dpRRnWVt8w2JnboehCAu-mTjFutcCA2BFMg8u1hI0ibknEH7ju8QSTH1INpGPKoCob7SrpasZZ61zcJQPeii0zdGWC3eWfRWpbj8t6JM4oY1qfN6Pd9M14yl1OMo_Rz_gttj1uSDwtg4-4NHF6Kz0o7RsSohf147-pvjSao58L3ab1Z0uhHCkEbD_vbcole8GJFvdXBgSPCDBWdeCoD8_0C7KZfC2CQVYRPH8uZSJWYOcKSbjKhun06SeW-qwyTiQZpL91txpkh7XNbx3RrKBMbX7NfJF6eTdcv2zCljzwUjrzhlvKZQXypXKjE9by4n6bRH49ZlO2dlq5B8f5jiwSTEl3C4t1NgT8gL9_Hy2BkmlrudXEZtNfONOkCKK47DBiwh0SFxNVsCMWF_qHvwUvQIBjjPpZABzSJSBch7rFVxepk1ch706Y9MuzKJ4Lc9po2KCnW4Bm2XHbUQjFONZUA4aL34RB8TIRlFFrISf9cOkWEx74BUUm3gPuniNwASQeJG9LbQEHfv4it979htxN8kGuMDqUlV4-X_rNSjYjaAoyJjsEnxZb7h96BtQ80LiY9P-xmUDNwhJWD5Isc",
                "AQAB",
                KeyUse.sig,
                "82f6353e-39b6-4a60-9fe5-cdce55a290bd",
                "ndDlP0YJEVxZ57ZVKZ4Ft6f3siaX3tCJkrUBABbsUysSC0LKrnmuABZTJ3fhWe-T8UPz57X20H9b6o-EyRgvDHlN4SklxTI9Nd87kKKyqAVgvelTwpaNz96lxgeBtC_CKgd9ww2P9Q0_1lAMndS7k8-mKcBnXWa6hDENYX_BAy04L4GnpsR4_gfe4z7ov2sbNO53i6nm1NQ1MqfQ1KQEbsLIva3V_iOay6Kqb6tm_WjlXi2nOPxGOkxSwJ-jUCMaIEAQWqMjgSYsXRS62C-96MqcL6Br09BqoHJlTQn8AksMEs_I2OMDcOLug8ynocN_CorPgSRuqgO9Ddr1I7e21Q",
                "q49l5Yf4ta8JVuLCqHr5FwYAZKH_0QPOdAaS3z0NxgVbnH8I5iiojPg6mahnDclZQHlX8P_niUikeg-PQs4EIa7eGbp6TWrx0-KZoEEB2d5jwoB9O-ueCquHi1o13EzM7vadTBP_6gUIJt0MWXFTKzMZeuHtZS7FyNEDSN3vTdLekZzW8bbHTQUhBM0ZO0WFOHKToHcK3h1PxyAgddrJizJ-VHvr1hugt7BjJmI4YO20gJCmKSLWm13y2-D8tnPW8WH4ko8BGtMaiW7slo5ABeOAMmfesAcF-WnsEEs3vNoydmdXzFKjtR0lppFX9qCRjEma4zOhI-7vP_Ai6qY3hQ",
                "i5x1lYLgZXwOdGZb0m8Bx1IqsedN4P9qLSsokxmqFxu6My5bFGhhVWA4OHdtTPACfYVNoRY85aH7RJOKe06PIuSct4nkAS0VBCOWamaJ0_Z9H083fiLrfPEwViNVW8GGnlzxmNkPZ4TAP8K7ceI660NSe-0OsK87seRLEpfgGAb2yFxR5y1MMsO9lHxVfW7BlfAYpF8zvOAafq-OwwZ8Av4Tz2NwPZDcl6vQ_XE5l6yXD8zRJxn7oeycEj7zavx5WLFzSl9Ox6oxHb_nfi3i2mWMmkBU8sUItDm729vyeUJ1njjpkDkY4KtLJvf_txJ3XKbTwQJ-fUCmawlcCaPS0D71t6YdnUI3GdhApq9-xL8J5S8sgDZdbFDhKOnpWpOd1hd_igb8KZG2eRguFY2R0hjGmWUIiyJ-Vc8XmD1QZwOvutKsaL8pwgpeEJ-C1x4ltJ5QyDeUSE5hsZWrg_c-KqC7u-FxvPE05X-O4aepksTuk0D0eo9TOlA7zw61x3V-A_zAYBPpevsANf2oPIrTdgWbODGVC3cIJuRJMN4QELGqYN7gcLho_1uiR6XcOmfynYfSFRXVou78tEyV_27U7cvUXmY6-Xl5FmqMy3thqMd1-Ak_UW7GXhkakradcP8PvT9fwozIG53seH14X4WDzjvXIKaS0NMOUi0dHKIOpWk",
                "vKI7tSmL0Hn4C1sm36irGQr83O2RJXmg76SBX4R_h2Ne7mIYTaMu1ozjM2ajJHzbciMnTUfBWpQ2xEf5UR-SgZgBPn6PxzAcbN0qs6w8OUw_eeEZY7ZOhltLpmFbM83w3Lg32lPZpuJbLHnkhJFhPRg499KV3SkYkChFyPxvbyQzRM0h2iA2_lno_LZLpfZ4B71ZEOYsbI7-AXYhzjbIDPUZrBUe9If3CDI6rAOBO1LQBs_oIDat7AwgJUnMLF3WnHVTKsZZH24XmlVeAmvYL3oBQwCt2ukpUxDc-H-Ps_wSB-r87lKWHdFdC_oKK8WylEbUHuiIZs2sZTk1fa76Jw",
                KeyType.RSA,
                "vXhbi-11pjdnjWdppWEHMhk-LP8Yk6g78kBghSVw6Pfe8hgy5I-qx7S6VmD2yUHjNS9t_ZnLqv-IdnZCq3XPY4lQHPU5TDu8l-fUvAgejiwly1YHNvjsxzV-hiwYFTOkxFpezF4HA71vovqEkhQWqMXF2yxf8GJWQrBbFbReO8Q-ltk8rJVwxm1yHO4WqJohwtOyHrWzqjRT_IOOp402C6FxuHvmFyNKuUgMChDsNmHhtVsdQ9z4Kq0e8GibnIE8Se2hgkn1FbwWLRiSj5BqzCKkP_w1WtV21a3v8b5XZsahOXqEjvVcoyQaR45PzvYEqax16cAwaw5T13IrNBvt7w",
                "hsnut4VDuwCg-IEGbzthY6oVjJwUuSMs6atdyyBMcnWKV-sqYUqIP8iAqQBjFYcEwsVCbb_AubAbHrlQm42T2XCweTmnvZXqI7PMK5_aYp9DTwqI30Sr-usgjgQJkyMUG7jQj-EvY99xkYX7j9ilrslnkuE01OY0PWkvR_Jo09seG68EUBq3TBSOdSP_Cuwi9JxripytPVOXfnsdn4zuTxxjE8ZSxq6Ql6nm5tQ-Ni6JdQWzE6JqCTuwUtPGYP6hcCeASTGKTusy7t3zMkC1hdT5o77dlTGkbZt60gA-alRQoy8f0UkuDXgiIwDr9FILTyZHiDqu1UavDA-PfJ_PSA",
                1680695652675l, 1680609252675l);
        // Need to simulate a key pair object.
        // Prepare request

        KeyRetriever customMock = new KeyRetriever() {
            @Override
            public Uni<KeyPair> getKeyPair() {
                return Uni.createFrom().item(k);
            }

            @Override
            public Uni<Optional<PublicKey>> getPublicKey(String s) {
                PublicKey pk = new PublicKey(k.getE(), k.getUse(), k.getKid(), k.getN(), k.getKty(), k.getExp(), k.getIat());

                return Uni.createFrom().item(Optional.of(pk));
            }
        };
        QuarkusMock.installMockForType(customMock, KeyRetriever.class);

    }

    @Test
    public void createTokenWithoutRefreshByPassword() {
        given()
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .header("RequestId", "00000000-0000-0000-0000-000000000001")
                .header("AcquirerId", ACQUIRER_ID)
                .header("Channel", CHANNEL)
                .header("MerchantId", MERCHANT_ID)
                .header("TerminalId", TERMINAL_ID)
                .formParam("client_id", CLIENT_ID)
                .formParam("grant_type", "password")
                .formParam("username", USERNAME)
                .formParam("password", PASSWORD)
                .when()
                .post()
                .then()
                .statusCode(200)
                .contentType(MediaType.APPLICATION_JSON)
                .body("access_token", notNullValue())
                .body("token_type", equalTo("Bearer"))
                .body("expires_in", notNullValue(Long.class))
                .body("refresh_token", nullValue());
    }

    @Test()
    public void createTokenWithRefreshByPassword() {
        given()
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .header("RequestId", "00000000-0000-0000-0000-000000000001")
                .header("AcquirerId", ACQUIRER_ID)
                .header("Channel", CHANNEL)
                .header("MerchantId", MERCHANT_ID)
                .header("TerminalId", TERMINAL_ID)
                .formParam("client_id", CLIENT_ID)
                .formParam("grant_type", "password")
                .formParam("username", USERNAME)
                .formParam("password", PASSWORD)
                .formParam("scope", "offline_access")
                .when()
                .post()
                .then()
                .statusCode(200)
                .contentType(MediaType.APPLICATION_JSON)
                .body("access_token", notNullValue())
                .body("token_type", equalTo("Bearer"))
                .body("expires_in", notNullValue(Long.class))
                .body("refresh_token", notNullValue());
    }

    @Test()
    public void CreateTokenWithWrongClientId() {
        String clientId = "5254f087-1214-45cd-94ae-fda53c835198";
        given().contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .formParam("client_id", clientId)
                .formParam("username", USERNAME)
                .formParam("password", PASSWORD)
                .formParam("grant_type", "password")
                .header("AcquirerId", ACQUIRER_ID)
                .header("TerminalId", TERMINAL_ID)
                .header("MerchantId", MERCHANT_ID)
                .header("RequestId", "aB1aB1aB-aB1a-aB1a-aB1a-aB1aB1aB1abb")
                .header("Channel", CHANNEL)
                .when()
                .log()
                .all()
                .post()
                .then()
                .statusCode(401)
                .body("errors", hasItem(ErrorCode.CLIENT_ID_NOT_FOUND));


    }

    @Test()
    public void CreateTokenWithWrongChannel() {

        String channel = "TOTEM";
        given().contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .formParam("client_id", CLIENT_ID)
                .formParam("username", USERNAME)
                .formParam("password", PASSWORD)
                .formParam("grant_type", "password")
                .header("AcquirerId", ACQUIRER_ID)
                .header("TerminalId", TERMINAL_ID)
                .header("MerchantId", MERCHANT_ID)
                .header("RequestId", "aB1aB1aB-aB1a-aB1a-aB1a-aB1aB1aB1abb")
                .header("Channel", channel)
                .when()
                .log()
                .all()
                .post()
                .then()
                .statusCode(401)
                .body("errors", hasItem(ErrorCode.CREDENTIALS_INCONSISTENCY));
    }

    @Test
    public void CreateTokenWithWrongUser() {

        given().contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .formParam("client_id", CLIENT_ID)
                .formParam("username", "wronguser")
                .formParam("password", PASSWORD)
                .formParam("grant_type", "password")
                .header("AcquirerId", ACQUIRER_ID)
                .header("TerminalId", TERMINAL_ID)
                .header("MerchantId", MERCHANT_ID)
                .header("RequestId", "aB1aB1aB-aB1a-aB1a-aB1a-aB1aB1aB1abb")
                .header("Channel", CHANNEL)
                .when()
                .log()
                .all()
                .post()
                .then()
                .statusCode(401)
                .body("errors", hasItem(ErrorCode.WRONG_CREDENTIALS));
    }

    @Test
    public void CreateTokenWithWrongMerchant() {

        // Prepare request
        String merchantId = "28405fHfk73x88E";
        given().contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .formParam("client_id", CLIENT_ID)
                .formParam("username", USERNAME)
                .formParam("password", PASSWORD)
                .formParam("grant_type", "password")
                .header("AcquirerId", ACQUIRER_ID)
                .header("TerminalId", TERMINAL_ID)
                .header("MerchantId", merchantId)
                .header("RequestId", "aB1aB1aB-aB1a-aB1a-aB1a-aB1aB1aB1abb")
                .header("Channel", CHANNEL)
                .when()
                .log()
                .all()
                .post()
                .then()
                .statusCode(401)
                .body("errors", hasItem(ErrorCode.CREDENTIALS_INCONSISTENCY));
    }

    @Test
    public void CreateTokenFromRefresh() {


        KeyPair k = new KeyPair("OCCNJzAIHdSrjQFPvgCR0vvGHWKz7RZSLdmRhN4HAyZLC-OBi2lRZDz8LTtaLlm5bpQmevIRRnKCLXYxcNRK-Ei3dpRRnWVt8w2JnboehCAu-mTjFutcCA2BFMg8u1hI0ibknEH7ju8QSTH1INpGPKoCob7SrpasZZ61zcJQPeii0zdGWC3eWfRWpbj8t6JM4oY1qfN6Pd9M14yl1OMo_Rz_gttj1uSDwtg4-4NHF6Kz0o7RsSohf147-pvjSao58L3ab1Z0uhHCkEbD_vbcole8GJFvdXBgSPCDBWdeCoD8_0C7KZfC2CQVYRPH8uZSJWYOcKSbjKhun06SeW-qwyTiQZpL91txpkh7XNbx3RrKBMbX7NfJF6eTdcv2zCljzwUjrzhlvKZQXypXKjE9by4n6bRH49ZlO2dlq5B8f5jiwSTEl3C4t1NgT8gL9_Hy2BkmlrudXEZtNfONOkCKK47DBiwh0SFxNVsCMWF_qHvwUvQIBjjPpZABzSJSBch7rFVxepk1ch706Y9MuzKJ4Lc9po2KCnW4Bm2XHbUQjFONZUA4aL34RB8TIRlFFrISf9cOkWEx74BUUm3gPuniNwASQeJG9LbQEHfv4it979htxN8kGuMDqUlV4-X_rNSjYjaAoyJjsEnxZb7h96BtQ80LiY9P-xmUDNwhJWD5Isc",
                "AQAB",
                KeyUse.sig,
                "82f6353e-39b6-4a60-9fe5-cdce55a290bd",
                "ndDlP0YJEVxZ57ZVKZ4Ft6f3siaX3tCJkrUBABbsUysSC0LKrnmuABZTJ3fhWe-T8UPz57X20H9b6o-EyRgvDHlN4SklxTI9Nd87kKKyqAVgvelTwpaNz96lxgeBtC_CKgd9ww2P9Q0_1lAMndS7k8-mKcBnXWa6hDENYX_BAy04L4GnpsR4_gfe4z7ov2sbNO53i6nm1NQ1MqfQ1KQEbsLIva3V_iOay6Kqb6tm_WjlXi2nOPxGOkxSwJ-jUCMaIEAQWqMjgSYsXRS62C-96MqcL6Br09BqoHJlTQn8AksMEs_I2OMDcOLug8ynocN_CorPgSRuqgO9Ddr1I7e21Q",
                "q49l5Yf4ta8JVuLCqHr5FwYAZKH_0QPOdAaS3z0NxgVbnH8I5iiojPg6mahnDclZQHlX8P_niUikeg-PQs4EIa7eGbp6TWrx0-KZoEEB2d5jwoB9O-ueCquHi1o13EzM7vadTBP_6gUIJt0MWXFTKzMZeuHtZS7FyNEDSN3vTdLekZzW8bbHTQUhBM0ZO0WFOHKToHcK3h1PxyAgddrJizJ-VHvr1hugt7BjJmI4YO20gJCmKSLWm13y2-D8tnPW8WH4ko8BGtMaiW7slo5ABeOAMmfesAcF-WnsEEs3vNoydmdXzFKjtR0lppFX9qCRjEma4zOhI-7vP_Ai6qY3hQ",
                "i5x1lYLgZXwOdGZb0m8Bx1IqsedN4P9qLSsokxmqFxu6My5bFGhhVWA4OHdtTPACfYVNoRY85aH7RJOKe06PIuSct4nkAS0VBCOWamaJ0_Z9H083fiLrfPEwViNVW8GGnlzxmNkPZ4TAP8K7ceI660NSe-0OsK87seRLEpfgGAb2yFxR5y1MMsO9lHxVfW7BlfAYpF8zvOAafq-OwwZ8Av4Tz2NwPZDcl6vQ_XE5l6yXD8zRJxn7oeycEj7zavx5WLFzSl9Ox6oxHb_nfi3i2mWMmkBU8sUItDm729vyeUJ1njjpkDkY4KtLJvf_txJ3XKbTwQJ-fUCmawlcCaPS0D71t6YdnUI3GdhApq9-xL8J5S8sgDZdbFDhKOnpWpOd1hd_igb8KZG2eRguFY2R0hjGmWUIiyJ-Vc8XmD1QZwOvutKsaL8pwgpeEJ-C1x4ltJ5QyDeUSE5hsZWrg_c-KqC7u-FxvPE05X-O4aepksTuk0D0eo9TOlA7zw61x3V-A_zAYBPpevsANf2oPIrTdgWbODGVC3cIJuRJMN4QELGqYN7gcLho_1uiR6XcOmfynYfSFRXVou78tEyV_27U7cvUXmY6-Xl5FmqMy3thqMd1-Ak_UW7GXhkakradcP8PvT9fwozIG53seH14X4WDzjvXIKaS0NMOUi0dHKIOpWk",
                "vKI7tSmL0Hn4C1sm36irGQr83O2RJXmg76SBX4R_h2Ne7mIYTaMu1ozjM2ajJHzbciMnTUfBWpQ2xEf5UR-SgZgBPn6PxzAcbN0qs6w8OUw_eeEZY7ZOhltLpmFbM83w3Lg32lPZpuJbLHnkhJFhPRg499KV3SkYkChFyPxvbyQzRM0h2iA2_lno_LZLpfZ4B71ZEOYsbI7-AXYhzjbIDPUZrBUe9If3CDI6rAOBO1LQBs_oIDat7AwgJUnMLF3WnHVTKsZZH24XmlVeAmvYL3oBQwCt2ukpUxDc-H-Ps_wSB-r87lKWHdFdC_oKK8WylEbUHuiIZs2sZTk1fa76Jw",
                KeyType.RSA,
                "vXhbi-11pjdnjWdppWEHMhk-LP8Yk6g78kBghSVw6Pfe8hgy5I-qx7S6VmD2yUHjNS9t_ZnLqv-IdnZCq3XPY4lQHPU5TDu8l-fUvAgejiwly1YHNvjsxzV-hiwYFTOkxFpezF4HA71vovqEkhQWqMXF2yxf8GJWQrBbFbReO8Q-ltk8rJVwxm1yHO4WqJohwtOyHrWzqjRT_IOOp402C6FxuHvmFyNKuUgMChDsNmHhtVsdQ9z4Kq0e8GibnIE8Se2hgkn1FbwWLRiSj5BqzCKkP_w1WtV21a3v8b5XZsahOXqEjvVcoyQaR45PzvYEqax16cAwaw5T13IrNBvt7w",
                "hsnut4VDuwCg-IEGbzthY6oVjJwUuSMs6atdyyBMcnWKV-sqYUqIP8iAqQBjFYcEwsVCbb_AubAbHrlQm42T2XCweTmnvZXqI7PMK5_aYp9DTwqI30Sr-usgjgQJkyMUG7jQj-EvY99xkYX7j9ilrslnkuE01OY0PWkvR_Jo09seG68EUBq3TBSOdSP_Cuwi9JxripytPVOXfnsdn4zuTxxjE8ZSxq6Ql6nm5tQ-Ni6JdQWzE6JqCTuwUtPGYP6hcCeASTGKTusy7t3zMkC1hdT5o77dlTGkbZt60gA-alRQoy8f0UkuDXgiIwDr9FILTyZHiDqu1UavDA-PfJ_PSA",
                1680695652675l, 1680609252675l);
        // Need to simulate a key pair object.
        // Prepare request

        KeyRetriever customMock = new KeyRetriever() {
            @Override
            public Uni<KeyPair> getKeyPair() {
                return Uni.createFrom().item(k);
            }

            @Override
            public Uni<Optional<PublicKey>> getPublicKey(String s) {
                PublicKey pk = new PublicKey(k.getE(), k.getUse(), k.getKid(), k.getN(), k.getKty(), k.getExp(), k.getIat());

                return Uni.createFrom().item(Optional.of(pk));
            }
        };
        QuarkusMock.installMockForType(customMock, KeyRetriever.class);

        /*
        * Had to do the override again because the @Order annotation wouldn't work.
        * */
        Response resp = given().contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .formParam("client_id", CLIENT_ID)
                .formParam("username", USERNAME)
                .formParam("password", PASSWORD)
                .formParam("scope", "offline_access")
                .formParam("grant_type", "password")
                .header("AcquirerId", ACQUIRER_ID)
                .header("TerminalId", TERMINAL_ID)
                .header("MerchantId", MERCHANT_ID)
                .header("RequestId", "aB1aB1aB-aB1a-aB1a-aB1a-aB1aB1aB1abb")
                .header("Channel", CHANNEL)
                .when()
                .log()
                .all()
                .post();

        resp
                .then()
                .statusCode(200)
                .body("", hasKey("refresh_token"))
                .body("", hasKey("access_token"))
                .body("", hasKey("token_type"))
                .body("", hasKey("expires_in"));

        String refreshToken = resp.body().jsonPath().getString("refresh_token");

        given().contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .formParam("client_id", CLIENT_ID)
                .formParam("scope", "offline_access")
                .formParam("refresh_token", refreshToken)
                .formParam("grant_type", "refresh_token")
                .header("AcquirerId", ACQUIRER_ID)
                .header("TerminalId", TERMINAL_ID)
                .header("MerchantId", MERCHANT_ID)
                .header("RequestId", "aB1aB1aB-aB1a-aB1a-aB1a-aB1aB1aB1abb")
                .header("Channel", CHANNEL)
                .when()
                .log()
                .all()
                .post()
                .then()
                .statusCode(200);

    }

    @Test
    public void CreateTokenFromRefreshExpiredKey() {
        KeyRetriever customMock = new KeyRetriever() {
            /*
            Simulating an expired key by returning no public keys.
             */
            @Override
            public Uni<KeyPair> getKeyPair() {
                KeyPair k = new KeyPair("OCCNJzAIHdSrjQFPvgCR0vvGHWKz7RZSLdmRhN4HAyZLC-OBi2lRZDz8LTtaLlm5bpQmevIRRnKCLXYxcNRK-Ei3dpRRnWVt8w2JnboehCAu-mTjFutcCA2BFMg8u1hI0ibknEH7ju8QSTH1INpGPKoCob7SrpasZZ61zcJQPeii0zdGWC3eWfRWpbj8t6JM4oY1qfN6Pd9M14yl1OMo_Rz_gttj1uSDwtg4-4NHF6Kz0o7RsSohf147-pvjSao58L3ab1Z0uhHCkEbD_vbcole8GJFvdXBgSPCDBWdeCoD8_0C7KZfC2CQVYRPH8uZSJWYOcKSbjKhun06SeW-qwyTiQZpL91txpkh7XNbx3RrKBMbX7NfJF6eTdcv2zCljzwUjrzhlvKZQXypXKjE9by4n6bRH49ZlO2dlq5B8f5jiwSTEl3C4t1NgT8gL9_Hy2BkmlrudXEZtNfONOkCKK47DBiwh0SFxNVsCMWF_qHvwUvQIBjjPpZABzSJSBch7rFVxepk1ch706Y9MuzKJ4Lc9po2KCnW4Bm2XHbUQjFONZUA4aL34RB8TIRlFFrISf9cOkWEx74BUUm3gPuniNwASQeJG9LbQEHfv4it979htxN8kGuMDqUlV4-X_rNSjYjaAoyJjsEnxZb7h96BtQ80LiY9P-xmUDNwhJWD5Isc",
                        "AQAB",
                        KeyUse.sig,
                        "82f6353e-39b6-4a60-9fe5-cdce55a290bd",
                        "ndDlP0YJEVxZ57ZVKZ4Ft6f3siaX3tCJkrUBABbsUysSC0LKrnmuABZTJ3fhWe-T8UPz57X20H9b6o-EyRgvDHlN4SklxTI9Nd87kKKyqAVgvelTwpaNz96lxgeBtC_CKgd9ww2P9Q0_1lAMndS7k8-mKcBnXWa6hDENYX_BAy04L4GnpsR4_gfe4z7ov2sbNO53i6nm1NQ1MqfQ1KQEbsLIva3V_iOay6Kqb6tm_WjlXi2nOPxGOkxSwJ-jUCMaIEAQWqMjgSYsXRS62C-96MqcL6Br09BqoHJlTQn8AksMEs_I2OMDcOLug8ynocN_CorPgSRuqgO9Ddr1I7e21Q",
                        "q49l5Yf4ta8JVuLCqHr5FwYAZKH_0QPOdAaS3z0NxgVbnH8I5iiojPg6mahnDclZQHlX8P_niUikeg-PQs4EIa7eGbp6TWrx0-KZoEEB2d5jwoB9O-ueCquHi1o13EzM7vadTBP_6gUIJt0MWXFTKzMZeuHtZS7FyNEDSN3vTdLekZzW8bbHTQUhBM0ZO0WFOHKToHcK3h1PxyAgddrJizJ-VHvr1hugt7BjJmI4YO20gJCmKSLWm13y2-D8tnPW8WH4ko8BGtMaiW7slo5ABeOAMmfesAcF-WnsEEs3vNoydmdXzFKjtR0lppFX9qCRjEma4zOhI-7vP_Ai6qY3hQ",
                        "i5x1lYLgZXwOdGZb0m8Bx1IqsedN4P9qLSsokxmqFxu6My5bFGhhVWA4OHdtTPACfYVNoRY85aH7RJOKe06PIuSct4nkAS0VBCOWamaJ0_Z9H083fiLrfPEwViNVW8GGnlzxmNkPZ4TAP8K7ceI660NSe-0OsK87seRLEpfgGAb2yFxR5y1MMsO9lHxVfW7BlfAYpF8zvOAafq-OwwZ8Av4Tz2NwPZDcl6vQ_XE5l6yXD8zRJxn7oeycEj7zavx5WLFzSl9Ox6oxHb_nfi3i2mWMmkBU8sUItDm729vyeUJ1njjpkDkY4KtLJvf_txJ3XKbTwQJ-fUCmawlcCaPS0D71t6YdnUI3GdhApq9-xL8J5S8sgDZdbFDhKOnpWpOd1hd_igb8KZG2eRguFY2R0hjGmWUIiyJ-Vc8XmD1QZwOvutKsaL8pwgpeEJ-C1x4ltJ5QyDeUSE5hsZWrg_c-KqC7u-FxvPE05X-O4aepksTuk0D0eo9TOlA7zw61x3V-A_zAYBPpevsANf2oPIrTdgWbODGVC3cIJuRJMN4QELGqYN7gcLho_1uiR6XcOmfynYfSFRXVou78tEyV_27U7cvUXmY6-Xl5FmqMy3thqMd1-Ak_UW7GXhkakradcP8PvT9fwozIG53seH14X4WDzjvXIKaS0NMOUi0dHKIOpWk",
                        "vKI7tSmL0Hn4C1sm36irGQr83O2RJXmg76SBX4R_h2Ne7mIYTaMu1ozjM2ajJHzbciMnTUfBWpQ2xEf5UR-SgZgBPn6PxzAcbN0qs6w8OUw_eeEZY7ZOhltLpmFbM83w3Lg32lPZpuJbLHnkhJFhPRg499KV3SkYkChFyPxvbyQzRM0h2iA2_lno_LZLpfZ4B71ZEOYsbI7-AXYhzjbIDPUZrBUe9If3CDI6rAOBO1LQBs_oIDat7AwgJUnMLF3WnHVTKsZZH24XmlVeAmvYL3oBQwCt2ukpUxDc-H-Ps_wSB-r87lKWHdFdC_oKK8WylEbUHuiIZs2sZTk1fa76Jw",
                        KeyType.RSA,
                        "vXhbi-11pjdnjWdppWEHMhk-LP8Yk6g78kBghSVw6Pfe8hgy5I-qx7S6VmD2yUHjNS9t_ZnLqv-IdnZCq3XPY4lQHPU5TDu8l-fUvAgejiwly1YHNvjsxzV-hiwYFTOkxFpezF4HA71vovqEkhQWqMXF2yxf8GJWQrBbFbReO8Q-ltk8rJVwxm1yHO4WqJohwtOyHrWzqjRT_IOOp402C6FxuHvmFyNKuUgMChDsNmHhtVsdQ9z4Kq0e8GibnIE8Se2hgkn1FbwWLRiSj5BqzCKkP_w1WtV21a3v8b5XZsahOXqEjvVcoyQaR45PzvYEqax16cAwaw5T13IrNBvt7w",
                        "hsnut4VDuwCg-IEGbzthY6oVjJwUuSMs6atdyyBMcnWKV-sqYUqIP8iAqQBjFYcEwsVCbb_AubAbHrlQm42T2XCweTmnvZXqI7PMK5_aYp9DTwqI30Sr-usgjgQJkyMUG7jQj-EvY99xkYX7j9ilrslnkuE01OY0PWkvR_Jo09seG68EUBq3TBSOdSP_Cuwi9JxripytPVOXfnsdn4zuTxxjE8ZSxq6Ql6nm5tQ-Ni6JdQWzE6JqCTuwUtPGYP6hcCeASTGKTusy7t3zMkC1hdT5o77dlTGkbZt60gA-alRQoy8f0UkuDXgiIwDr9FILTyZHiDqu1UavDA-PfJ_PSA",
                        1680695652675L, 1680609252675L);
                return Uni.createFrom().item(k);
            }

            @Override
            public Uni<Optional<PublicKey>> getPublicKey(String s) {
                return Uni.createFrom().item(Optional.empty());
            }
        };
        QuarkusMock.installMockForType(customMock, KeyRetriever.class);

        RequestSpecification request = given()
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .formParam("client_id", CLIENT_ID)
                .formParam("username", USERNAME)
                .formParam("password", PASSWORD)
                .formParam("scope", "offline_access")
                .formParam("grant_type", "password")
                .header("AcquirerId", ACQUIRER_ID)
                .header("TerminalId", TERMINAL_ID)
                .header("MerchantId", MERCHANT_ID)
                .header("RequestId", "aB1aB1aB-aB1a-aB1a-aB1a-aB1aB1aB1abb")
                .header("Channel", CHANNEL);

        Response resp = request
                .when()
                .log()
                .all()
                .post();

        resp
                .then()
                .statusCode(200)
                .body("", hasKey("refresh_token"))
                .body("", hasKey("access_token"))
                .body("", hasKey("token_type"))
                .body("", hasKey("expires_in"));
        String refreshToken = resp.body().jsonPath().getString("refresh_token");
        System.out.println(refreshToken);

         given()
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .formParam("client_id", CLIENT_ID)
                .formParam("scope", "offline_access")
                .formParam("refresh_token", refreshToken)
                .formParam("grant_type", "refresh_token")
                .header("AcquirerId", ACQUIRER_ID)
                .header("TerminalId", TERMINAL_ID)
                .header("MerchantId", MERCHANT_ID)
                .header("RequestId", "aB1aB1aB-aB1a-aB1a-aB1a-aB1aB1aB1abb")
                .header("Channel", CHANNEL)
                 .when()
                 .log()
                 .all()
                 .post()
                 .then()
                 .statusCode(401)
                 .body("errors", hasItem(ErrorCode.KEY_NOT_FOUND));

    }

    @Test
    public void CreateTokenWithNoRefresh() {


        given()
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .formParam("client_id", CLIENT_ID)
                .formParam("scope", "offline_access")
                .formParam("refresh_token", "")
                .formParam("grant_type", "refresh_token")
                .header("AcquirerId", ACQUIRER_ID)
                .header("TerminalId", TERMINAL_ID)
                .header("MerchantId", MERCHANT_ID)
                .header("RequestId", "aB1aB1aB-aB1a-aB1a-aB1a-aB1aB1aB1abb")
                .header("Channel", CHANNEL)
                .when()
                .log()
                .all()
                .post()
                .then()
                .statusCode(400)
                .body("errors", hasItem(ErrorCode.REFRESH_TOKEN_MUST_MATCH_REGEXP));

    }

    @Test
    public void CreateTokenWithWrongAcquirer() {

        String username = "antonio.tarricone";
        String password = "antonio";
        String acquirerId = "4585627";
        given().contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .formParam("client_id", CLIENT_ID)
                .formParam("username", username)
                .formParam("password", password)
                .formParam("grant_type", "password")
                .header("AcquirerId", acquirerId)
                .header("TerminalId", TERMINAL_ID)
                .header("MerchantId", MERCHANT_ID)
                .header("RequestId", "aB1aB1aB-aB1a-aB1a-aB1a-aB1aB1aB1abb")
                .header("Channel", CHANNEL)
                .when()
                .log()
                .all()
                .post()
                .then()
                .statusCode(401)
                .body("errors", hasItem(ErrorCode.CREDENTIALS_INCONSISTENCY));
    }

    /**
     * @throws JOSEException
     */
    @Test
    public void createTokenWithoutRefreshByPoyntToken() throws JOSEException {
        /*
         * Setup
         */
        Mockito
                .when(poyntClient
                        .getBusinessObject(anyString(), anyString()))
                .thenReturn(Uni
                        .createFrom()
                        .item(javax.ws.rs.core.Response
                                .ok()
                                .build()));

        /*
         * Test
         */
        given()
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .header("RequestId", "00000000-0000-0000-0000-000000000002")
                .header("AcquirerId", ACQUIRER_ID)
                .header("Channel", CHANNEL)
                .header("MerchantId", MERCHANT_ID)
                .header("TerminalId", TERMINAL_ID)
                .formParam("client_id", CLIENT_ID)
                .formParam("grant_type", "poynt_token")
                .formParam("ext_token", "eyJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJOZXhpIiwicG95bnQuZGlkIjoidXJuOnRpZDo1NTYyYjhlZC1lODljLTMzMmEtYThkYy1jYTA4MTcxMzUxMTAiLCJwb3ludC5kc3QiOiJEIiwicG95bnQub3JnIjoiMGU2Zjc4ODYtMDk1Ni00NDA1LWJjNDgtYzE5ODY4ZDdlZTIyIiwicG95bnQuc2N0IjoiVSIsImlzcyI6Imh0dHBzOlwvXC9zZXJ2aWNlcy1ldS5wb3ludC5uZXQiLCJwb3ludC51cmUiOiJPIiwicG95bnQua2lkIjozOTMyNDI1MjY4MDY5NDA5MjM0LCJwb3ludC5zY3YiOiJOZXhpIiwicG95bnQuc3RyIjoiZDNmZDNmZDMtMTg5ZC00N2M4LThjMzYtYjY4NWRkNjBkOTY0IiwiYXVkIjoidXJuOnRpZDo1NTYyYjhlZC1lODljLTMzMmEtYThkYy1jYTA4MTcxMzUxMTAiLCJwb3ludC51aWQiOjM3MzY1NzQsInBveW50LmJpeiI6IjRiN2ViOTRiLTEwYzktNGYxMS1hMTBlLTcyOTJiMjlhYjExNSIsImV4cCI6MTY4MDc4MzUzNiwiaWF0IjoxNjgwNjk3MTM2LCJqdGkiOiI3MjBmMDFlZS1iZDk4LTRjYjItOTU2Mi0xZjI4YWY2NGJhZGYifQ.dTuvtzqy9oPWIN4NIBdhIR09Xpm70dgRCP-ybuVLo24DwqaysPKNmEHDXcq2gGE1w2L6e783_PXRK3RI0j1TQRFeLRbiPzN5imBdrJ2LlV8QNdkElOl2x32j652YeFcoAitBzFss_Do0_rquU_008eeIXWa-B-AiMsdAqgLUiMigsTT42rQYr7Mb8Am_NWwvZ9-DWiox6HbuUNUo3TStBmLervqlQ5j2_3AzcOILp8cJX0699fw7Y6gcu_pNHgjswqD0UVPSAmHf_bqFAH6b98qHVKe3isMSoktYi4FfWdpG1ykviEp9Ii0QKfeLnqyIR6g2o2XzGlDv7Usv5ouiXg")
                .formParam("add_data", BUSINESS_ID)
                .when()
                .post()
                .then()
                .statusCode(200)
                .contentType(MediaType.APPLICATION_JSON)
                .body("access_token", notNullValue())
                .body("token_type", equalTo("Bearer"))
                .body("expires_in", notNullValue(Long.class))
                .body("refresh_token", nullValue());
    }

    /**
     * @throws JOSEException
     */
    @Test
    public void createTokenWithRefreshByPoyntToken() throws JOSEException {
        /*
         * Setup
         */
        Mockito
                .when(poyntClient
                        .getBusinessObject(anyString(), anyString()))
                .thenReturn(Uni
                        .createFrom()
                        .item(javax.ws.rs.core.Response
                                .ok()
                                .build()));

        /*
         * Test
         */
        given()
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .header("RequestId", "00000000-0000-0000-0000-000000000002")
                .header("AcquirerId", ACQUIRER_ID)
                .header("Channel", CHANNEL)
                .header("MerchantId", MERCHANT_ID)
                .header("TerminalId", TERMINAL_ID)
                .formParam("client_id", CLIENT_ID)
                .formParam("grant_type", "poynt_token")
                .formParam("ext_token", "eyJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJOZXhpIiwicG95bnQuZGlkIjoidXJuOnRpZDo1NTYyYjhlZC1lODljLTMzMmEtYThkYy1jYTA4MTcxMzUxMTAiLCJwb3ludC5kc3QiOiJEIiwicG95bnQub3JnIjoiMGU2Zjc4ODYtMDk1Ni00NDA1LWJjNDgtYzE5ODY4ZDdlZTIyIiwicG95bnQuc2N0IjoiVSIsImlzcyI6Imh0dHBzOlwvXC9zZXJ2aWNlcy1ldS5wb3ludC5uZXQiLCJwb3ludC51cmUiOiJPIiwicG95bnQua2lkIjozOTMyNDI1MjY4MDY5NDA5MjM0LCJwb3ludC5zY3YiOiJOZXhpIiwicG95bnQuc3RyIjoiZDNmZDNmZDMtMTg5ZC00N2M4LThjMzYtYjY4NWRkNjBkOTY0IiwiYXVkIjoidXJuOnRpZDo1NTYyYjhlZC1lODljLTMzMmEtYThkYy1jYTA4MTcxMzUxMTAiLCJwb3ludC51aWQiOjM3MzY1NzQsInBveW50LmJpeiI6IjRiN2ViOTRiLTEwYzktNGYxMS1hMTBlLTcyOTJiMjlhYjExNSIsImV4cCI6MTY4MDc4MzUzNiwiaWF0IjoxNjgwNjk3MTM2LCJqdGkiOiI3MjBmMDFlZS1iZDk4LTRjYjItOTU2Mi0xZjI4YWY2NGJhZGYifQ.dTuvtzqy9oPWIN4NIBdhIR09Xpm70dgRCP-ybuVLo24DwqaysPKNmEHDXcq2gGE1w2L6e783_PXRK3RI0j1TQRFeLRbiPzN5imBdrJ2LlV8QNdkElOl2x32j652YeFcoAitBzFss_Do0_rquU_008eeIXWa-B-AiMsdAqgLUiMigsTT42rQYr7Mb8Am_NWwvZ9-DWiox6HbuUNUo3TStBmLervqlQ5j2_3AzcOILp8cJX0699fw7Y6gcu_pNHgjswqD0UVPSAmHf_bqFAH6b98qHVKe3isMSoktYi4FfWdpG1ykviEp9Ii0QKfeLnqyIR6g2o2XzGlDv7Usv5ouiXg")
                .formParam("add_data", BUSINESS_ID)
                .formParam("scope", "offline_access")
                .when()
                .post()
                .then()
                .statusCode(200)
                .contentType(MediaType.APPLICATION_JSON)
                .body("access_token", notNullValue())
                .body("token_type", equalTo("Bearer"))
                .body("expires_in", notNullValue(Long.class))
                .body("refresh_token", notNullValue());
    }
}
