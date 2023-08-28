/*
 * AzureKeyFinderTest.java
 *
 * 28 lug 2023
 */
package it.pagopa.swclient.mil.auth.azurekeyvault.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.helpers.test.UniAssertSubscriber;
import it.pagopa.swclient.mil.auth.azurekeyvault.bean.CreateKeyRequest;
import it.pagopa.swclient.mil.auth.azurekeyvault.bean.CreateKeyResponse;
import it.pagopa.swclient.mil.auth.azurekeyvault.bean.GetAccessTokenResponse;
import it.pagopa.swclient.mil.auth.azurekeyvault.bean.GetKeyResponse;
import it.pagopa.swclient.mil.auth.azurekeyvault.bean.GetKeyVersionsResponse;
import it.pagopa.swclient.mil.auth.azurekeyvault.bean.GetKeysResponse;
import it.pagopa.swclient.mil.auth.azurekeyvault.bean.Key;
import it.pagopa.swclient.mil.auth.azurekeyvault.bean.KeyAttributes;
import it.pagopa.swclient.mil.auth.azurekeyvault.bean.KeyDetails;
import it.pagopa.swclient.mil.auth.azurekeyvault.bean.KeyVersion;
import it.pagopa.swclient.mil.auth.azurekeyvault.client.AzureAuthClient;
import it.pagopa.swclient.mil.auth.azurekeyvault.client.AzureKeyVaultClient;
import it.pagopa.swclient.mil.auth.bean.KeyType;
import it.pagopa.swclient.mil.auth.bean.KeyUse;
import it.pagopa.swclient.mil.auth.bean.PublicKey;
import it.pagopa.swclient.mil.auth.bean.PublicKeys;
import it.pagopa.swclient.mil.auth.util.AuthError;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

/**
 * 
 * @author Antonio Tarricone
 */
@QuarkusTest
@TestInstance(Lifecycle.PER_CLASS)
class AzureKeyFinderTest {
	/*
	 * 
	 */
	@InjectMock
	@RestClient
	AzureAuthClient authClient;

	/*
	 * 
	 */
	@InjectMock
	@RestClient
	AzureKeyVaultClient keyVaultClient;

	/*
	 * 
	 */
	@Inject
	AzureKeyFinder azureKeyFinder;

	/*
	 * 
	 */
	private long now;

	/*
	 * 
	 */
	private static final String K1 = "0709643f49394529b92c19a68c8e184a";
	private static final String K1_V2 = "82f646ef6bd44b5db8b95d76bb151926";
	private static final String K1_V3 = "4171620a6a8d4f93a1622f95dc7f1ada";
	private static final String K1_V4 = "e400f109f7ab4c2e9a28916d40ed1925";
	private static final String K1_V5 = "9df5a14b79c147358f6a3626ea63b8a8";
	private static final String K1_V7 = "6e1795083aec4467adfd0a5beb05ec06";
	private static final String K1_V8 = "68afc030808c4bb69b15ff5dcdfb7c3c";
	private static final String K1_V9 = "eeb5eb71fb7d46eca705601dd6c09426";
	private static final String K1_V10 = "c47b20cdeba643b59a5b038cb8270c89";
	private static final String K1_V11 = "6581c704deda4979943c3b34468df7c2"; // Valid key version with the greatest expiration.
	private static final String K1_V12 = "4a6b1ee949ad40c28f85493328952691";

	private static final String K2 = "10b33e516ee3470cbf5edc9ace919450";
	private static final String K2_V1 = "6e6075cfe78948f79dc7cbe0b58ec14d";
	private static final String K2_V2 = "6e6075cfe78948f79dc7cbe0b58ec14d";
	private static final String K2_V3 = "b59f1231befc4e3cbd5f500a287e290a";
	private static final String K2_V4 = "90630f81e7244cdb9508969fc9aeb372";
	private static final String K2_V5 = "2aef3230ed4147238f3e993f626dc2e7";
	private static final String K2_V7 = "3abba745219d4ce2a45b70321646928f";
	private static final String K2_V8 = "9af4b74a9c4f4cea93abb5574fb5a884";
	private static final String K2_V9 = "fa541b9da1794558982a5c4d9e55a17a";
	private static final String K2_V10 = "a14a9fc70e714ab4b48e6b9baf761b2d"; // Valid key version.

	private static final String K3 = "ed2dae1359334729a37e4cafb5bc1e11";

	private static final String K4 = "fc3db9157e92403db23fd12074730a0b";

	/*
	 * 
	 */
	private static final String KEY_URL = "https://quarkus-azure-test-kv.vault.azure.net/keys/";
	
	/*
	 * 
	 */
	private static final String AZURE_ACCESS_TOKEN = "this_is_the_token";
	private static final String AUTHORIZATION_HDR_VALUE = "Bearer " + AZURE_ACCESS_TOKEN;

	/*
	 * 
	 */
	private Key keyWithKidWithoutName1;
	private Key keyWithKidWithoutName2;
	private Key keyWithValidKid1;
	private Key keyWithValidKid2;
	private Key keyWithValidKidWithoutVersions;

	/*
	 * 
	 */
	private KeyVersion nullVersionK1V1;
	private KeyVersion versionWithNullEnabledAttributeK1V2;
	private KeyVersion versionWithNullCreationTimestampAttributeK1V3;
	private KeyVersion versionWithNullExpiredTimestampAttributeK1V4;
	private KeyVersion versionWithNullNotBeforeAttributeK1V5;
	private KeyVersion versionWithNullKidK1V6;
	private KeyVersion versionWithNullDetailsK1V7;
	private KeyVersion versionWithDetailsWithNoRsaKeyTypeK1V8;
	private KeyVersion versionWithDetailsWithoutSignOpK1V9;
	private KeyVersion versionWithDetailsWithoutSignAndVerifyOpK1V10;
	private KeyVersion versionWithValidDetailsWithGreatestExpirationK1V11;
	private KeyVersion versionWith500OnGetKeyK1V12;

	private KeyVersion versionWithNullAttributesK2V1;
	private KeyVersion versionWithFalseEnabledAttributeK2V2;
	private KeyVersion versionWithNotCoherentCreationTimestampAttributeK2V3;
	private KeyVersion expiredVersionK2V4;
	private KeyVersion versionWithUnmetNotBeforeAttributeK2V5;
	private KeyVersion versionWithInvalidKidK2V6;
	private KeyVersion versionWithExpiredDetailsK2V7;
	private KeyVersion versionWithDetailsWithNullOpsK2V8;
	private KeyVersion versionWithDetailsWithoutVerifyOpK2V9;
	private KeyVersion versionWithValidDetailsK2V10;

	/*
	 * 
	 */
	private KeyDetails detailsWithNoRsaKeyTypeK1V8;
	private KeyDetails detailsWithoutSignOpK1V9;
	private KeyDetails detailsWithoutSignAndVerifyOpK1V10;
	private KeyDetails validDetailsWithGreatestExpirationK1V11;

	private KeyDetails expiredDetailsK2V7;
	private KeyDetails detailsWithNullOpsK2V8;
	private KeyDetails detailsWithoutVerifyOpK2V9;
	private KeyDetails validDetailsK2V10;
	private KeyDetails detailsWithBadKidK2V11;

	/**
	 * 
	 */
	@BeforeAll
	void setup() {
		now = Instant.now().getEpochSecond();

		/*
		 * Keys returned by getKeys.
		 */
		keyWithKidWithoutName1 = new Key(
			null,
			new KeyAttributes(
				now - 300,
				now + 300,
				now - 300,
				now - 300,
				Boolean.TRUE,
				"Purgeable",
				0,
				Boolean.FALSE));

		keyWithKidWithoutName2 = new Key(
			"",
			new KeyAttributes(
				now - 300,
				now + 300,
				now - 300,
				now - 300,
				Boolean.TRUE,
				"Purgeable",
				0,
				Boolean.FALSE));

		keyWithValidKid1 = new Key(
			KEY_URL + K1,
			new KeyAttributes(
				now - 300,
				now + 600,
				now - 300,
				now - 300,
				Boolean.TRUE,
				"Purgeable",
				0,
				Boolean.FALSE));

		keyWithValidKid2 = new Key(
			KEY_URL + K2,
			new KeyAttributes(
				now - 300,
				now + 300,
				now - 300,
				now - 300,
				Boolean.TRUE,
				"Purgeable",
				0,
				Boolean.FALSE));

		keyWithValidKidWithoutVersions = new Key(
			KEY_URL + K3,
			new KeyAttributes(
				now - 300,
				now + 300,
				now - 300,
				now - 300,
				Boolean.TRUE,
				"Purgeable",
				0,
				Boolean.FALSE));

		/*
		 * Versions returned by getKeyVersions.
		 */
		nullVersionK1V1 = null;

		versionWithNullAttributesK2V1 = new KeyVersion(
			KEY_URL + K2 + "/" + K2_V1,
			null);

		versionWithNullEnabledAttributeK1V2 = new KeyVersion(
			KEY_URL + K1 + "/" + K1_V2,
			new KeyAttributes(
				now - 300,
				now + 300,
				now - 300,
				now - 300,
				null,
				"Purgeable",
				0,
				Boolean.FALSE));

		versionWithFalseEnabledAttributeK2V2 = new KeyVersion(
			KEY_URL + K2 + "/" + K2_V2,
			new KeyAttributes(
				now - 300,
				now + 300,
				now - 300,
				now - 300,
				Boolean.FALSE,
				"Purgeable",
				0,
				Boolean.FALSE));

		versionWithNullCreationTimestampAttributeK1V3 = new KeyVersion(
			KEY_URL + K1 + "/" + K1_V3,
			new KeyAttributes(
				null,
				now + 300,
				now - 300,
				now - 300,
				Boolean.TRUE,
				"Purgeable",
				0,
				Boolean.FALSE));

		versionWithNotCoherentCreationTimestampAttributeK2V3 = new KeyVersion(
			KEY_URL + K2 + "/" + K2_V3,
			new KeyAttributes(
				now + 300,
				now + 300,
				now - 300,
				now - 300,
				Boolean.TRUE,
				"Purgeable",
				0,
				Boolean.FALSE));

		versionWithNullExpiredTimestampAttributeK1V4 = new KeyVersion(
			KEY_URL + K1 + "/" + K1_V4,
			new KeyAttributes(
				now - 300,
				null,
				now - 300,
				now - 300,
				Boolean.TRUE,
				"Purgeable",
				0,
				Boolean.FALSE));

		expiredVersionK2V4 = new KeyVersion(
			KEY_URL + K2 + "/" + K2_V4,
			new KeyAttributes(
				now - 300,
				now - 100,
				now - 300,
				now - 300,
				Boolean.TRUE,
				"Purgeable",
				0,
				Boolean.FALSE));

		versionWithNullNotBeforeAttributeK1V5 = new KeyVersion(
			KEY_URL + K1 + "/" + K1_V5,
			new KeyAttributes(
				now - 300,
				now + 300,
				null,
				now - 300,
				Boolean.TRUE,
				"Purgeable",
				0,
				Boolean.FALSE));

		versionWithUnmetNotBeforeAttributeK2V5 = new KeyVersion(
			KEY_URL + K2 + "/" + K2_V5,
			new KeyAttributes(
				now - 300,
				now + 300,
				now + 100,
				now - 300,
				Boolean.TRUE,
				"Purgeable",
				0,
				Boolean.FALSE));

		versionWithNullKidK1V6 = new KeyVersion(
			null,
			new KeyAttributes(
				now - 300,
				now + 300,
				now - 300,
				now - 300,
				Boolean.TRUE,
				"Purgeable",
				0,
				Boolean.FALSE));

		versionWithInvalidKidK2V6 = new KeyVersion(
			"",
			new KeyAttributes(
				now - 300,
				now + 300,
				now - 300,
				now - 300,
				Boolean.TRUE,
				"Purgeable",
				0,
				Boolean.FALSE));

		versionWithNullDetailsK1V7 = new KeyVersion(
			KEY_URL + K1 + "/" + K1_V7,
			new KeyAttributes(
				now - 300,
				now + 300,
				now - 300,
				now - 300,
				Boolean.TRUE,
				"Purgeable",
				0,
				Boolean.FALSE));

		versionWithExpiredDetailsK2V7 = new KeyVersion(
			KEY_URL + K2 + "/" + K2_V7,
			new KeyAttributes(
				now - 300,
				now + 300,
				now - 300,
				now - 300,
				Boolean.TRUE,
				"Purgeable",
				0,
				Boolean.FALSE));

		versionWithDetailsWithNoRsaKeyTypeK1V8 = new KeyVersion(
			KEY_URL + K1 + "/" + K1_V8,
			new KeyAttributes(
				now - 300,
				now + 300,
				now - 300,
				now - 300,
				Boolean.TRUE,
				"Purgeable",
				0,
				Boolean.FALSE));

		versionWithDetailsWithNullOpsK2V8 = new KeyVersion(
			KEY_URL + K2 + "/" + K2_V8,
			new KeyAttributes(
				now - 300,
				now + 300,
				now - 300,
				now - 300,
				Boolean.TRUE,
				"Purgeable",
				0,
				Boolean.FALSE));

		versionWithDetailsWithoutSignOpK1V9 = new KeyVersion(
			KEY_URL + K1 + "/" + K1_V9,
			new KeyAttributes(
				now - 300,
				now + 300,
				now - 300,
				now - 300,
				Boolean.TRUE,
				"Purgeable",
				0,
				Boolean.FALSE));

		versionWithDetailsWithoutVerifyOpK2V9 = new KeyVersion(
			KEY_URL + K2 + "/" + K2_V9,
			new KeyAttributes(
				now - 300,
				now + 300,
				now - 300,
				now - 300,
				Boolean.TRUE,
				"Purgeable",
				0,
				Boolean.FALSE));

		versionWithDetailsWithoutSignAndVerifyOpK1V10 = new KeyVersion(
			KEY_URL + K1 + "/" + K1_V10,
			new KeyAttributes(
				now - 300,
				now + 300,
				now - 300,
				now - 300,
				Boolean.TRUE,
				"Purgeable",
				0,
				Boolean.FALSE));

		versionWithValidDetailsK2V10 = new KeyVersion(
			KEY_URL + K2 + "/" + K2_V10,
			new KeyAttributes(
				now - 300,
				now + 300,
				now - 300,
				now - 300,
				Boolean.TRUE,
				"Purgeable",
				0,
				Boolean.FALSE));

		versionWithValidDetailsWithGreatestExpirationK1V11 = new KeyVersion(
			KEY_URL + K1 + "/" + K1_V11,
			new KeyAttributes(
				now - 300,
				now + 600,
				now - 300,
				now - 300,
				Boolean.TRUE,
				"Purgeable",
				0,
				Boolean.FALSE));

		versionWith500OnGetKeyK1V12 = new KeyVersion(
			KEY_URL + K1 + "/" + K1_V12,
			new KeyAttributes(
				now - 300,
				now + 300,
				now - 300,
				now - 300,
				Boolean.TRUE,
				"Purgeable",
				0,
				Boolean.FALSE));
		
		versionWith500OnGetKeyK1V12 = new KeyVersion(
			KEY_URL + K1 + "/" + K1_V12,
			new KeyAttributes(
				now - 300,
				now + 300,
				now - 300,
				now - 300,
				Boolean.TRUE,
				"Purgeable",
				0,
				Boolean.FALSE));
		
		/*
		 * Details returned by getKey.
		 */
		expiredDetailsK2V7 = new KeyDetails(
			KEY_URL + K2 + "/" + K2_V7,
			"RSA",
			new String[] {
				"sign", "verify"
			},
			"this_is_the_modulus",
			"this_is_the_exponent",
			new KeyAttributes(
				now - 300,
				now - 100,
				now - 300,
				now - 300,
				Boolean.TRUE,
				"Purgeable",
				0,
				Boolean.FALSE));

		detailsWithNoRsaKeyTypeK1V8 = new KeyDetails(
			KEY_URL + K1 + "/" + K1_V8,
			"non-RSA",
			new String[] {
				"sign", "verify"
			},
			"this_is_the_modulus",
			"this_is_the_exponent",
			new KeyAttributes(
				now - 300,
				now + 300,
				now - 300,
				now - 300,
				Boolean.TRUE,
				"Purgeable",
				0,
				Boolean.FALSE));

		detailsWithNullOpsK2V8 = new KeyDetails(
			KEY_URL + K2 + "/" + K2_V8,
			"RSA",
			null,
			"this_is_the_modulus",
			"this_is_the_exponent",
			new KeyAttributes(
				now - 300,
				now + 300,
				now - 300,
				now - 300,
				Boolean.TRUE,
				"Purgeable",
				0,
				Boolean.FALSE));

		detailsWithoutSignOpK1V9 = new KeyDetails(
			KEY_URL + K1 + "/" + K1_V9,
			"RSA",
			new String[] {
				"verify"
			},
			"this_is_the_modulus",
			"this_is_the_exponent",
			new KeyAttributes(
				now - 300,
				now + 300,
				now - 300,
				now - 300,
				Boolean.TRUE,
				"Purgeable",
				0,
				Boolean.FALSE));

		detailsWithoutVerifyOpK2V9 = new KeyDetails(
			KEY_URL + K2 + "/" + K2_V9,
			"RSA",
			new String[] {
				"sign"
			},
			"this_is_the_modulus",
			"this_is_the_exponent",
			new KeyAttributes(
				now - 300,
				now + 300,
				now - 300,
				now - 300,
				Boolean.TRUE,
				"Purgeable",
				0,
				Boolean.FALSE));

		detailsWithoutSignAndVerifyOpK1V10 = new KeyDetails(
			KEY_URL + K1 + "/" + K1_V10,
			"RSA",
			new String[] {},
			"this_is_the_modulus",
			"this_is_the_exponent",
			new KeyAttributes(
				now - 300,
				now + 300,
				now - 300,
				now - 300,
				Boolean.TRUE,
				"Purgeable",
				0,
				Boolean.FALSE));

		validDetailsK2V10 = new KeyDetails(
			KEY_URL + K2 + "/" + K2_V10,
			"RSA",
			new String[] {
				"verify", "sign"
			},
			"this_is_the_modulus",
			"this_is_the_exponent",
			new KeyAttributes(
				now - 300,
				now + 300,
				now - 300,
				now - 300,
				Boolean.TRUE,
				"Purgeable",
				0,
				Boolean.FALSE));

		validDetailsWithGreatestExpirationK1V11 = new KeyDetails(
			KEY_URL + K1 + "/" + K1_V11,
			"RSA",
			new String[] {
				"verify", "sign"
			},
			"this_is_the_modulus",
			"this_is_the_exponent",
			new KeyAttributes(
				now - 300,
				now + 600,
				now - 300,
				now - 300,
				Boolean.TRUE,
				"Purgeable",
				0,
				Boolean.FALSE));

		detailsWithBadKidK2V11 = new KeyDetails(
			"",
			"RSA",
			new String[] {
				"verify", "sign"
			},
			"this_is_the_modulus",
			"this_is_the_exponent",
			new KeyAttributes(
				now - 300,
				now + 300,
				now - 300,
				now - 300,
				Boolean.TRUE,
				"Purgeable",
				0,
				Boolean.FALSE));
	}

	/**
	 * 
	 */
	private void mostCommonSetup() {
		when(authClient.getAccessToken(anyString(), anyString(), anyString(), anyString(), anyString()))
			.thenReturn(Uni.createFrom().item(new GetAccessTokenResponse("Bearer", 3599, 3599, AZURE_ACCESS_TOKEN)));
	
		when(keyVaultClient.getKeys(AUTHORIZATION_HDR_VALUE))
			.thenReturn(Uni.createFrom().item(new GetKeysResponse(new Key[] {
				keyWithKidWithoutName1,
				keyWithKidWithoutName2,
				keyWithValidKid1,
				keyWithValidKid2,
				keyWithValidKidWithoutVersions
			})));

		when(keyVaultClient.getKeyVersions(AUTHORIZATION_HDR_VALUE, K1))
			.thenReturn(Uni.createFrom().item(new GetKeyVersionsResponse(new KeyVersion[] {
				nullVersionK1V1,
				versionWithNullEnabledAttributeK1V2,
				versionWithNullCreationTimestampAttributeK1V3,
				versionWithNullExpiredTimestampAttributeK1V4,
				versionWithNullNotBeforeAttributeK1V5,
				versionWithNullKidK1V6,
				versionWithNullDetailsK1V7,
				versionWithDetailsWithNoRsaKeyTypeK1V8,
				versionWithDetailsWithoutSignOpK1V9,
				versionWithDetailsWithoutSignAndVerifyOpK1V10,
				versionWithValidDetailsWithGreatestExpirationK1V11
			})));
	
		when(keyVaultClient.getKeyVersions(AUTHORIZATION_HDR_VALUE, K2))
			.thenReturn(Uni.createFrom().item(new GetKeyVersionsResponse(new KeyVersion[] {
				versionWithNullAttributesK2V1,
				versionWithFalseEnabledAttributeK2V2,
				versionWithNotCoherentCreationTimestampAttributeK2V3,
				expiredVersionK2V4,
				versionWithUnmetNotBeforeAttributeK2V5,
				versionWithInvalidKidK2V6,
				versionWithExpiredDetailsK2V7,
				versionWithDetailsWithNullOpsK2V8,
				versionWithDetailsWithoutVerifyOpK2V9,
				versionWithValidDetailsK2V10
			})));
	
		when(keyVaultClient.getKeyVersions(AUTHORIZATION_HDR_VALUE, K3))
			.thenReturn(Uni.createFrom().item(new GetKeyVersionsResponse(new KeyVersion[] {})));
	
		when(keyVaultClient.getKeyVersions(AUTHORIZATION_HDR_VALUE, K4))
			.thenReturn(Uni.createFrom().item(new GetKeyVersionsResponse(null)));
	
		when(keyVaultClient.getKey(AUTHORIZATION_HDR_VALUE, K1, K1_V7))
			.thenReturn(Uni.createFrom().item(new GetKeyResponse(null)));

		when(keyVaultClient.getKey(AUTHORIZATION_HDR_VALUE, K1, K1_V8))
			.thenReturn(Uni.createFrom().item(new GetKeyResponse(detailsWithNoRsaKeyTypeK1V8)));

		when(keyVaultClient.getKey(AUTHORIZATION_HDR_VALUE, K1, K1_V9))
			.thenReturn(Uni.createFrom().item(new GetKeyResponse(detailsWithoutSignOpK1V9)));

		when(keyVaultClient.getKey(AUTHORIZATION_HDR_VALUE, K1, K1_V10))
			.thenReturn(Uni.createFrom().item(new GetKeyResponse(detailsWithoutSignAndVerifyOpK1V10)));

		when(keyVaultClient.getKey(AUTHORIZATION_HDR_VALUE, K1, K1_V11))
			.thenReturn(Uni.createFrom().item(new GetKeyResponse(validDetailsWithGreatestExpirationK1V11)));
		
		when(keyVaultClient.getKey(AUTHORIZATION_HDR_VALUE, K2, K2_V7))
			.thenReturn(Uni.createFrom().item(new GetKeyResponse(expiredDetailsK2V7)));

		when(keyVaultClient.getKey(AUTHORIZATION_HDR_VALUE, K2, K2_V8))
			.thenReturn(Uni.createFrom().item(new GetKeyResponse(detailsWithNullOpsK2V8)));

		when(keyVaultClient.getKey(AUTHORIZATION_HDR_VALUE, K2, K2_V9))
			.thenReturn(Uni.createFrom().item(new GetKeyResponse(detailsWithoutVerifyOpK2V9)));

		when(keyVaultClient.getKey(AUTHORIZATION_HDR_VALUE, K2, K2_V10))
			.thenReturn(Uni.createFrom().item(new GetKeyResponse(validDetailsK2V10)));
	}

	/**
	 * On get access token a null token string is returned.
	 */
	@Test
	void testFindPublicKeysWithNullAccessToken() {
		/*
		 * Setup.
		 */
		when(authClient.getAccessToken(anyString(), anyString(), anyString(), anyString(), anyString()))
			.thenReturn(Uni.createFrom().item(new GetAccessTokenResponse("Bearer", 3599, 3599, null)));
		
		/*
		 * Test.
		 */
		azureKeyFinder.findPublicKeys()
			.subscribe()
			.withSubscriber(UniAssertSubscriber.create())
			.assertFailedWith(AuthError.class);
	}

	/**
	 * On get access token a HTTP 401 is returned.
	 */
	@Test
	void testFindPublicKeysWith401OnGetAccessToken() {
		/*
		 * Setup.
		 */
		when(authClient.getAccessToken(anyString(), anyString(), anyString(), anyString(), anyString()))
			.thenReturn(Uni.createFrom().failure(new WebApplicationException(Response.status(Status.UNAUTHORIZED).build())));
		
		/*
		 * Test.
		 */
		azureKeyFinder.findPublicKeys()
			.subscribe()
			.withSubscriber(UniAssertSubscriber.create())
			.assertFailedWith(AuthError.class);
	}

	/**
	 * 
	 */
	@Test
	void testFindPublicKeysWithSomeWrongKeysAndNoHttpErrors() {
		/*
		 * Setup.
		 */
		mostCommonSetup();

		/*
		 * Test.
		 */
		PublicKey key1 = new PublicKey(
			validDetailsWithGreatestExpirationK1V11.getExponent(),
			KeyUse.sig,
			K1 + "/" + K1_V11,
			validDetailsWithGreatestExpirationK1V11.getModulus(),
			KeyType.RSA,
			validDetailsWithGreatestExpirationK1V11.getAttributes().getExp(),
			validDetailsWithGreatestExpirationK1V11.getAttributes().getCreated());

		PublicKey key2 = new PublicKey(
			validDetailsK2V10.getExponent(),
			KeyUse.sig,
			K2 + "/" + K2_V10,
			validDetailsK2V10.getModulus(),
			KeyType.RSA,
			validDetailsK2V10.getAttributes().getExp(),
			validDetailsK2V10.getAttributes().getCreated());

		PublicKeys expected = new PublicKeys(List.of(key1, key2));

		azureKeyFinder.findPublicKeys()
			.subscribe()
			.withSubscriber(UniAssertSubscriber.create())
			.assertCompleted()
			.assertItem(expected);
	}

	/**
	 * 
	 */
	@Test
	void testFindPublicKeysWithHttpError() {
		/*
		 * Setup.
		 */
		mostCommonSetup();

		when(keyVaultClient.getKeyVersions(AUTHORIZATION_HDR_VALUE, K1))
			.thenReturn(Uni.createFrom().item(new GetKeyVersionsResponse(new KeyVersion[] {
				nullVersionK1V1,
				versionWithNullEnabledAttributeK1V2,
				versionWithNullCreationTimestampAttributeK1V3,
				versionWithNullExpiredTimestampAttributeK1V4,
				versionWithNullNotBeforeAttributeK1V5,
				versionWithNullKidK1V6,
				versionWithNullDetailsK1V7,
				versionWithDetailsWithNoRsaKeyTypeK1V8,
				versionWithDetailsWithoutSignOpK1V9,
				versionWithDetailsWithoutSignAndVerifyOpK1V10,
				versionWithValidDetailsWithGreatestExpirationK1V11,
				versionWith500OnGetKeyK1V12
			})));

		when(keyVaultClient.getKey(AUTHORIZATION_HDR_VALUE, K1, K1_V12))
			.thenReturn(Uni.createFrom().failure(new WebApplicationException(Response.status(Status.UNAUTHORIZED).build())));

		/*
		 * Test.
		 */
		azureKeyFinder.findPublicKeys()
			.subscribe()
			.withSubscriber(UniAssertSubscriber.create())
			.assertFailedWith(AuthError.class);
	}

	/**
	 * 
	 */
	@Test
	void testFindPublicKeysWith401OnGetKeys() {
		/*
		 * Setup.
		 */
		when(authClient.getAccessToken(anyString(), anyString(), anyString(), anyString(), anyString()))
			.thenReturn(Uni.createFrom().item(new GetAccessTokenResponse("Bearer", 3599, 3599, AZURE_ACCESS_TOKEN)));
		
		when(keyVaultClient.getKeys(AUTHORIZATION_HDR_VALUE))
			.thenReturn(Uni.createFrom().failure(new WebApplicationException(Response.status(Status.UNAUTHORIZED).build())));

		/*
		 * Test.
		 */
		azureKeyFinder.findPublicKeys()
			.subscribe()
			.withSubscriber(UniAssertSubscriber.create())
			.assertFailedWith(AuthError.class);
	}

	/**
	 * 
	 */
	@Test
	void testFindValidPublicKeyWithGreatestExpiration() {
		/*
		 * Setup.
		 */
		mostCommonSetup();

		/*
		 * Test.
		 */
		PublicKey expected = new PublicKey(
			validDetailsWithGreatestExpirationK1V11.getExponent(),
			KeyUse.sig,
			K1 + "/" + K1_V11,
			validDetailsWithGreatestExpirationK1V11.getModulus(),
			KeyType.RSA,
			validDetailsWithGreatestExpirationK1V11.getAttributes().getExp(),
			validDetailsWithGreatestExpirationK1V11.getAttributes().getCreated());

		PublicKey actual = azureKeyFinder.findValidPublicKeyWithGreatestExpiration()
			.subscribe()
			.withSubscriber(UniAssertSubscriber.create())
			.assertCompleted()
			.getItem()
			.get();

		assertEquals(expected, actual);
	}

	/**
	 * 
	 */
	@Test
	void testFindValidPublicKeyWithGreatestExpiration2() {
		/*
		 * Setup.
		 */
		mostCommonSetup();

		KeyDetails validDetails1 = new KeyDetails(
			KEY_URL + K1 + "/" + K1_V11,
			"RSA",
			new String[] {
				"verify", "sign"
			},
			"this_is_the_modulus",
			"this_is_the_exponent",
			new KeyAttributes(
				now - 300,
				now + 300,
				now - 300,
				now - 300,
				Boolean.TRUE,
				"Purgeable",
				0,
				Boolean.FALSE));

		KeyDetails validDetails2 = new KeyDetails(
			KEY_URL + K2 + "/" + K2_V10,
			"RSA",
			new String[] {
				"verify", "sign"
			},
			"this_is_the_modulus",
			"this_is_the_exponent",
			new KeyAttributes(
				now - 300,
				now + 300,
				now - 300,
				now - 300,
				Boolean.TRUE,
				"Purgeable",
				0,
				Boolean.FALSE));

		when(keyVaultClient.getKey(AUTHORIZATION_HDR_VALUE, K1, K1_V11))
			.thenReturn(Uni.createFrom().item(new GetKeyResponse(validDetails1)));

		when(keyVaultClient.getKey(AUTHORIZATION_HDR_VALUE, K2, K2_V10))
			.thenReturn(Uni.createFrom().item(new GetKeyResponse(validDetails2)));

		/*
		 * Test.
		 */
		PublicKey expected = new PublicKey(
			validDetails1.getExponent(),
			KeyUse.sig,
			K1 + "/" + K1_V11,
			validDetails1.getModulus(),
			KeyType.RSA,
			validDetails1.getAttributes().getExp(),
			validDetails1.getAttributes().getCreated());

		PublicKey actual = azureKeyFinder.findValidPublicKeyWithGreatestExpiration()
			.subscribe()
			.withSubscriber(UniAssertSubscriber.create())
			.assertCompleted()
			.getItem()
			.get();

		assertEquals(expected, actual);
	}

	/**
	 * 
	 */
	@Test
	void testFindValidPublicKeyWithGreatestExpiration3() {
		/*
		 * Setup.
		 */
		mostCommonSetup();

		KeyDetails validDetails1 = new KeyDetails(
			KEY_URL + K1 + "/" + K1_V11,
			"RSA",
			new String[] {
				"verify", "sign"
			},
			"this_is_the_modulus",
			"this_is_the_exponent",
			new KeyAttributes(
				now - 300,
				now + 300,
				now - 300,
				now - 300,
				Boolean.TRUE,
				"Purgeable",
				0,
				Boolean.FALSE));

		KeyDetails validDetails2 = new KeyDetails(
			KEY_URL + K2 + "/" + K2_V10,
			"RSA",
			new String[] {
				"verify", "sign"
			},
			"this_is_the_modulus",
			"this_is_the_exponent",
			new KeyAttributes(
				now - 300,
				now + 600,
				now - 300,
				now - 300,
				Boolean.TRUE,
				"Purgeable",
				0,
				Boolean.FALSE));

		when(keyVaultClient.getKey(AUTHORIZATION_HDR_VALUE, K1, K1_V11))
			.thenReturn(Uni.createFrom().item(new GetKeyResponse(validDetails1)));

		when(keyVaultClient.getKey(AUTHORIZATION_HDR_VALUE, K2, K2_V10))
			.thenReturn(Uni.createFrom().item(new GetKeyResponse(validDetails2)));

		/*
		 * Test.
		 */
		PublicKey expected = new PublicKey(
			validDetails2.getExponent(),
			KeyUse.sig,
			K2 + "/" + K2_V10,
			validDetails2.getModulus(),
			KeyType.RSA,
			validDetails2.getAttributes().getExp(),
			validDetails2.getAttributes().getCreated());

		PublicKey actual = azureKeyFinder.findValidPublicKeyWithGreatestExpiration()
			.subscribe()
			.withSubscriber(UniAssertSubscriber.create())
			.assertCompleted()
			.getItem()
			.get();

		assertEquals(expected, actual);
	}

	/**
	 * 
	 */
	@Test
	void testFindValidPublicKeyWithGreatestExpirationWithNoKeys() {
		/*
		 * Setup.
		 */
		when(authClient.getAccessToken(anyString(), anyString(), anyString(), anyString(), anyString()))
			.thenReturn(Uni.createFrom().item(new GetAccessTokenResponse("Bearer", 3599, 3599, AZURE_ACCESS_TOKEN)));
	
		when(keyVaultClient.getKeys(AUTHORIZATION_HDR_VALUE))
			.thenReturn(Uni.createFrom().item(new GetKeysResponse(new Key[] {})));
		
		when(keyVaultClient.createKey(eq(AUTHORIZATION_HDR_VALUE), anyString(), any(CreateKeyRequest.class)))
			.thenReturn(Uni.createFrom().item(new CreateKeyResponse(validDetailsWithGreatestExpirationK1V11)));

		/*
		 * Test.
		 */
		azureKeyFinder.findValidPublicKeyWithGreatestExpiration()
			.subscribe()
			.withSubscriber(UniAssertSubscriber.create())
			.assertCompleted();
	}

	/**
	 * 
	 */
	@Test
	void testFindValidPublicKeyWithGreatestExpirationWithNoKeysAndExpiredKeyIsCreated() {
		/*
		 * Setup.
		 */
		when(authClient.getAccessToken(anyString(), anyString(), anyString(), anyString(), anyString()))
			.thenReturn(Uni.createFrom().item(new GetAccessTokenResponse("Bearer", 3599, 3599, AZURE_ACCESS_TOKEN)));
	
		when(keyVaultClient.getKeys(AUTHORIZATION_HDR_VALUE))
			.thenReturn(Uni.createFrom().item(new GetKeysResponse(new Key[] {})));
		
		when(keyVaultClient.createKey(eq(AUTHORIZATION_HDR_VALUE), anyString(), any(CreateKeyRequest.class)))
			.thenReturn(Uni.createFrom().item(new CreateKeyResponse(expiredDetailsK2V7)));

		/*
		 * Test.
		 */
		azureKeyFinder.findValidPublicKeyWithGreatestExpiration()
			.subscribe()
			.withSubscriber(UniAssertSubscriber.create())
			.assertFailedWith(AuthError.class);
	}

	/**
	 * 
	 */
	@Test
	void testFindValidPublicKeyWithGreatestExpirationWithNoKeysAndNonRsaKeyIsCreated() {
		/*
		 * Setup.
		 */
		when(authClient.getAccessToken(anyString(), anyString(), anyString(), anyString(), anyString()))
			.thenReturn(Uni.createFrom().item(new GetAccessTokenResponse("Bearer", 3599, 3599, AZURE_ACCESS_TOKEN)));
	
		when(keyVaultClient.getKeys(AUTHORIZATION_HDR_VALUE))
			.thenReturn(Uni.createFrom().item(new GetKeysResponse(new Key[] {})));
		
		when(keyVaultClient.createKey(eq(AUTHORIZATION_HDR_VALUE), anyString(), any(CreateKeyRequest.class)))
			.thenReturn(Uni.createFrom().item(new CreateKeyResponse(detailsWithNoRsaKeyTypeK1V8)));

		/*
		 * Test.
		 */
		azureKeyFinder.findValidPublicKeyWithGreatestExpiration()
			.subscribe()
			.withSubscriber(UniAssertSubscriber.create())
			.assertFailedWith(AuthError.class);
	}

	/**
	 * 
	 */
	@Test
	void testFindValidPublicKeyWithGreatestExpirationWithNoKeysAndKeyWithBadKidIsCreated() {
		/*
		 * Setup.
		 */
		when(authClient.getAccessToken(anyString(), anyString(), anyString(), anyString(), anyString()))
			.thenReturn(Uni.createFrom().item(new GetAccessTokenResponse("Bearer", 3599, 3599, AZURE_ACCESS_TOKEN)));
	
		when(keyVaultClient.getKeys(AUTHORIZATION_HDR_VALUE))
			.thenReturn(Uni.createFrom().item(new GetKeysResponse(new Key[] {})));
		
		when(keyVaultClient.createKey(eq(AUTHORIZATION_HDR_VALUE), anyString(), any(CreateKeyRequest.class)))
			.thenReturn(Uni.createFrom().item(new CreateKeyResponse(detailsWithBadKidK2V11)));

		/*
		 * Test.
		 */
		azureKeyFinder.findValidPublicKeyWithGreatestExpiration()
			.subscribe()
			.withSubscriber(UniAssertSubscriber.create())
			.assertFailedWith(AuthError.class);
	}

	/**
	 * 
	 */
	@Test
	void testFindValidPublicKeyWithGreatestExpirationWithNoKeysAndErrorOnCreation() {
		/*
		 * Setup.
		 */
		when(authClient.getAccessToken(anyString(), anyString(), anyString(), anyString(), anyString()))
			.thenReturn(Uni.createFrom().item(new GetAccessTokenResponse("Bearer", 3599, 3599, AZURE_ACCESS_TOKEN)));
	
		when(keyVaultClient.getKeys(AUTHORIZATION_HDR_VALUE))
			.thenReturn(Uni.createFrom().item(new GetKeysResponse(new Key[] {})));
		
		
		
		when(keyVaultClient.createKey(eq(AUTHORIZATION_HDR_VALUE), anyString(), any(CreateKeyRequest.class)))
			.thenReturn(Uni.createFrom().failure(new WebApplicationException(Response.status(Status.UNAUTHORIZED).build())));

		/*
		 * Test.
		 */
		azureKeyFinder.findValidPublicKeyWithGreatestExpiration()
			.subscribe()
			.withSubscriber(UniAssertSubscriber.create())
			.assertFailedWith(AuthError.class);
	}

	/**
	 * 
	 */
	@Test
	void findPublicKeyWithBadKid() {
		/*
		 * Test.
		 */
		azureKeyFinder.findPublicKey("")
			.subscribe()
			.withSubscriber(UniAssertSubscriber.create())
			.assertCompleted()
			.assertItem(Optional.empty());
	}

	/**
	 * 
	 */
	@Test
	void findPublicKey() {
		/*
		 * Setup.
		 */
		mostCommonSetup();

		/*
		 * Test.
		 */
		PublicKey expected = new PublicKey(
			validDetailsWithGreatestExpirationK1V11.getExponent(),
			KeyUse.sig,
			K1 + "/" + K1_V11,
			validDetailsWithGreatestExpirationK1V11.getModulus(),
			KeyType.RSA,
			validDetailsWithGreatestExpirationK1V11.getAttributes().getExp(),
			validDetailsWithGreatestExpirationK1V11.getAttributes().getCreated());

		azureKeyFinder.findPublicKey(K1 + "/" + K1_V11)
			.subscribe()
			.withSubscriber(UniAssertSubscriber.create())
			.assertCompleted()
			.assertItem(Optional.of(expected));
	}

	/**
	 * 
	 */
	@Test
	void findPublicKeyWithInvalidKey() {
		/*
		 * Setup.
		 */
		mostCommonSetup();

		/*
		 * Test.
		 */
		azureKeyFinder.findPublicKey(K2 + "/" + K2_V7)
			.subscribe()
			.withSubscriber(UniAssertSubscriber.create())
			.assertCompleted()
			.assertItem(Optional.empty());
	}
}