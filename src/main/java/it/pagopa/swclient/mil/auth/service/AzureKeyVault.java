/*
 * AzureKeyVault.java
 *
 * 01 jul 2023
 */
package it.pagopa.swclient.mil.auth.service;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import com.azure.core.http.rest.PagedFlux;
import com.azure.identity.DefaultAzureCredentialBuilder;
import com.azure.security.keyvault.keys.KeyAsyncClient;
import com.azure.security.keyvault.keys.KeyClientBuilder;
import com.azure.security.keyvault.keys.models.ImportKeyOptions;
import com.azure.security.keyvault.keys.models.JsonWebKey;
import com.azure.security.keyvault.keys.models.KeyOperation;
import com.azure.security.keyvault.keys.models.KeyProperties;
import com.azure.security.keyvault.keys.models.KeyVaultKey;
import com.nimbusds.jose.util.Base64URL;

import io.quarkus.logging.Log;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import it.pagopa.swclient.mil.auth.bean.KeyPair;
import it.pagopa.swclient.mil.auth.bean.KeyType;
import it.pagopa.swclient.mil.auth.bean.KeyUse;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.InvocationContext;
import mutiny.zero.flow.adapters.AdaptersToFlow;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 
 * @author Antonio Tarricone
 */
@ApplicationScoped
public class AzureKeyVault implements KeyVault {
	/*
	 * 
	 */
	@ConfigProperty(name = "keyvault.uri")
	String keyVaultUri;

	/*
	 * 
	 */
	private KeyAsyncClient client;

	/*
	 * 
	 */
	private static final Map<KeyType, com.azure.security.keyvault.keys.models.KeyType> MY_KEYTYPE_TO_AZURE_KEYTYPE = Map.of(KeyType.RSA, com.azure.security.keyvault.keys.models.KeyType.RSA);

	/*
	 * 
	 */
	private static final Map<com.azure.security.keyvault.keys.models.KeyType, KeyType> AZURE_KEYTYPE_TO_MY_KEYTYPE = Map.of(com.azure.security.keyvault.keys.models.KeyType.RSA, KeyType.RSA);

	/*
	 * 
	 */
	private static final Map<KeyUse, KeyOperation> MY_KEYUSE_TO_AZURE_KEYUSE = Map.of(KeyUse.sig, KeyOperation.SIGN);

	/*
	 * 
	 */
	private static final Map<KeyOperation, KeyUse> AZURE_KEYUSE_TO_MY_KEYUSE = Map.of(KeyOperation.SIGN, KeyUse.sig);

	/**
	 * Initialize the client.
	 * 
	 * @param ctx
	 */
	@AroundInvoke
	private synchronized void init(InvocationContext ctx) {
		if (client == null) {
			Log.debug("Creating of key async client.");
			client = new KeyClientBuilder()
				.vaultUrl(keyVaultUri)
				.credential(new DefaultAzureCredentialBuilder().build())
				.buildAsyncClient();
		}
	}

	/**
	 * Converts a Mono (Reactor) to Uni (Mutiny).
	 * 
	 * @param <T>
	 * @param mono
	 * @return
	 */
	private <T> Uni<T> mono2uni(Mono<T> mono) {
		return Uni.createFrom().publisher(AdaptersToFlow.publisher(mono));
	}

	/**
	 * Converts Flux (Reactor) to Multi (Mutiny).
	 * 
	 * @param <T>
	 * @param flux
	 * @return
	 */
	private <T> Multi<T> flux2multi(Flux<T> flux) {
		return Multi.createFrom().publisher(AdaptersToFlow.publisher(flux));
	}

	/**
	 * 
	 */
	public Uni<Void> setex(String kid, long seconds, KeyPair keyPair) {
		JsonWebKey jsonWebKey = new JsonWebKey()
			.setD(Base64URL.from(keyPair.getD()).decode())
			.setE(Base64URL.from(keyPair.getE()).decode())
			.setId(keyPair.getKid())
			.setKeyType(MY_KEYTYPE_TO_AZURE_KEYTYPE.get(keyPair.getKty()))
			.setN(Base64URL.from(keyPair.getN()).decode())
			.setKeyOps(List.of(MY_KEYUSE_TO_AZURE_KEYUSE.get(keyPair.getUse())));

		ImportKeyOptions importKeyOptions = new ImportKeyOptions(keyPair.getKid(), jsonWebKey)
			.setEnabled(Boolean.TRUE)
			.setExpiresOn(OffsetDateTime.from(Instant.ofEpochMilli(keyPair.getExp())))
			.setNotBefore(OffsetDateTime.from(Instant.ofEpochMilli(keyPair.getIat())));

		Mono<KeyVaultKey> mono = client.importKey(importKeyOptions);

		/*
		 * Reactor -> Mutiny
		 */
		Uni<KeyVaultKey> uni = mono2uni(mono);

		return uni.map(k -> {
			return (Void) null;
		});
	}

	/**
	 * 
	 */
	public Uni<KeyPair> get(String kid) {
		Mono<KeyVaultKey> mono = client.getKey(kid);

		/*
		 * Reactor -> Mutiny
		 */
		Uni<KeyVaultKey> uni = mono2uni(mono);

		return uni.map(k -> {
			if (k != null) {
				JsonWebKey jsonWebKey = k.getKey();

				List<KeyUse> keyUses = jsonWebKey.getKeyOps().stream().map(AZURE_KEYUSE_TO_MY_KEYUSE::get).toList();
				KeyUse keyUse = keyUses.isEmpty() ? null : keyUses.get(0);

				return new KeyPair(
					Base64URL.encode(jsonWebKey.getD()).toJSONString().replace("\"", ""),
					Base64URL.encode(jsonWebKey.getE()).toJSONString().replace("\"", ""),
					keyUse,
					jsonWebKey.getId(),
					Base64URL.encode(jsonWebKey.getN()).toJSONString().replace("\"", ""),
					AZURE_KEYTYPE_TO_MY_KEYTYPE.get(jsonWebKey.getKeyType()),
					k.getProperties().getExpiresOn().toInstant().toEpochMilli(),
					k.getProperties().getNotBefore().toInstant().toEpochMilli());
			}

			return null;
		});
	}

	/**
	 * 
	 */
	@Override
	public Uni<List<String>> keys(String pattern) {
		PagedFlux<KeyProperties> flux = client.listPropertiesOfKeys();

		/*
		 * Reactor -> Mutiny
		 */
		Multi<KeyProperties> multi = flux2multi(flux);

		return multi.map(KeyProperties::getId)
			.collect()
			.asList();
	}
}