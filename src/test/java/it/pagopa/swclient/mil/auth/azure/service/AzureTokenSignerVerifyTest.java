/*
 * AzureTokenSignerVerifyTest.java
 *
 * 28 mar 2024
 */
package it.pagopa.swclient.mil.auth.azure.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.text.ParseException;
import java.util.Date;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.mockito.MockedStatic;

import com.azure.core.exception.ResourceNotFoundException;
import com.azure.security.keyvault.keys.KeyAsyncClient;
import com.azure.security.keyvault.keys.cryptography.CryptographyAsyncClient;
import com.azure.security.keyvault.keys.cryptography.models.SignResult;
import com.azure.security.keyvault.keys.cryptography.models.SignatureAlgorithm;
import com.azure.security.keyvault.keys.cryptography.models.VerifyResult;
import com.azure.security.keyvault.keys.models.KeyVaultKey;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.util.Base64URL;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.mutiny.helpers.test.UniAssertSubscriber;
import it.pagopa.swclient.mil.auth.azure.util.SignedJWTFactory;
import it.pagopa.swclient.mil.auth.bean.PublicKey;
import it.pagopa.swclient.mil.auth.util.AuthError;
import it.pagopa.swclient.mil.auth.util.AuthException;
import it.pagopa.swclient.mil.auth.util.UniGenerator;
import reactor.core.publisher.Mono;

/**
 * 
 * @author Antonio Tarricone
 */
@QuarkusTest
@TestInstance(Lifecycle.PER_CLASS)
class AzureTokenSignerVerifyTest extends AzureKeyVaultTest {
	/**
	 * 
	 * @throws JOSEException
	 * @throws NoSuchAlgorithmException
	 */
	@Test
	void givenSignedToken_whenVerify_thenReturnVerificationResult() throws JOSEException, NoSuchAlgorithmException {
		/*
		 * Data preparation.
		 */
		KeyBundle keyBundle = prepareValidKey("valid_key_name", "v1", 0);
		KeyVaultKey keyVaultKey = keyBundle.getKeyVaultKey();
		PublicKey publicKey = keyBundle.getPublicKey();
		RSAPrivateKey rsaPrivateKey = keyBundle.getPrivateKey();

		Date now = new Date();
		JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
			.subject("<subject>")
			.audience("<audience>")
			.issuer("<issuer>")
			.issueTime(now)
			.expirationTime(new Date(now.getTime() + 5 * 60 * 1000))
			.build();

		JWSHeader header = new JWSHeader(JWSAlgorithm.RS256, null, null, null, null, null, null, null, null, null, publicKey.getKid(), true, null, null);
		Payload payload = claimsSet.toPayload();

		SignedJWT signedJwt = new SignedJWT(header, claimsSet);
		JWSSigner signer = new RSASSASigner(rsaPrivateKey);
		signedJwt.sign(signer);
		byte[] signature = signedJwt.getSignature().decode();

		String stringToSign = header.toBase64URL().toString() + "." + payload.toBase64URL().toString();
		byte[] bytesToSign = stringToSign.getBytes(StandardCharsets.UTF_8);

		MessageDigest messageDigest = MessageDigest.getInstance("SHA256");
		messageDigest.update(bytesToSign);

		byte[] digest = messageDigest.digest();

		/*
		 * Setup mocks.
		 */
		CryptographyAsyncClient cryptoClient = mock(CryptographyAsyncClient.class);
		when(cryptoClient.verify(SignatureAlgorithm.RS256, digest, signature))
			.thenReturn(Mono.just(new VerifyResult(Boolean.TRUE, SignatureAlgorithm.RS256, publicKey.getKid())));

		KeyAsyncClient keyClient = mock(KeyAsyncClient.class);
		when(keyClient.getCryptographyAsyncClient(keyVaultKey.getProperties().getName(), keyVaultKey.getProperties().getVersion()))
			.thenReturn(cryptoClient);

		AzureKeyFinder keyFinder = mock(AzureKeyFinder.class);
		when(keyFinder.findPublicKey())
			.thenReturn(UniGenerator.item(publicKey));
		when(keyFinder.getKeyClient())
			.thenReturn(keyClient);

		/*
		 * Test.
		 */
		AzureTokenSigner tokenSigner = new AzureTokenSigner(keyFinder);
		tokenSigner.verify(signedJwt)
			.subscribe().withSubscriber(UniAssertSubscriber.create())
			.awaitItem()
			.assertItem(null);
	}

	/**
	 * 
	 * @throws JOSEException
	 * @throws NoSuchAlgorithmException
	 */
	@Test
	void givenSignedTokenWithWrongSignature_whenVerify_thenReturnFailure() throws JOSEException, NoSuchAlgorithmException {
		/*
		 * Data preparation.
		 */
		KeyBundle keyBundle = prepareValidKey("valid_key_name", "v1", 0);
		KeyVaultKey keyVaultKey = keyBundle.getKeyVaultKey();
		PublicKey publicKey = keyBundle.getPublicKey();
		RSAPrivateKey rsaPrivateKey = keyBundle.getPrivateKey();

		Date now = new Date();
		JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
			.subject("<subject>")
			.audience("<audience>")
			.issuer("<issuer>")
			.issueTime(now)
			.expirationTime(new Date(now.getTime() + 5 * 60 * 1000))
			.build();

		JWSHeader header = new JWSHeader(JWSAlgorithm.RS256, null, null, null, null, null, null, null, null, null, publicKey.getKid(), true, null, null);
		Payload payload = claimsSet.toPayload();

		SignedJWT signedJwt = new SignedJWT(header, claimsSet);
		JWSSigner signer = new RSASSASigner(rsaPrivateKey);
		signedJwt.sign(signer);
		byte[] signature = signedJwt.getSignature().decode();

		String stringToSign = header.toBase64URL().toString() + "." + payload.toBase64URL().toString();
		byte[] bytesToSign = stringToSign.getBytes(StandardCharsets.UTF_8);

		MessageDigest messageDigest = MessageDigest.getInstance("SHA256");
		messageDigest.update(bytesToSign);

		byte[] digest = messageDigest.digest();

		/*
		 * Setup mocks.
		 */
		CryptographyAsyncClient cryptoClient = mock(CryptographyAsyncClient.class);
		when(cryptoClient.verify(SignatureAlgorithm.RS256, digest, signature))
			.thenReturn(Mono.just(new VerifyResult(Boolean.FALSE, SignatureAlgorithm.RS256, publicKey.getKid())));

		KeyAsyncClient keyClient = mock(KeyAsyncClient.class);
		when(keyClient.getCryptographyAsyncClient(keyVaultKey.getProperties().getName(), keyVaultKey.getProperties().getVersion()))
			.thenReturn(cryptoClient);

		AzureKeyFinder keyFinder = mock(AzureKeyFinder.class);
		when(keyFinder.findPublicKey())
			.thenReturn(UniGenerator.item(publicKey));
		when(keyFinder.getKeyClient())
			.thenReturn(keyClient);

		/*
		 * Test.
		 */
		AzureTokenSigner tokenSigner = new AzureTokenSigner(keyFinder);
		tokenSigner.verify(signedJwt)
			.subscribe().withSubscriber(UniAssertSubscriber.create())
			.awaitFailure()
			.assertFailedWith(AuthException.class);
	}

	/**
	 * 
	 * @throws NoSuchAlgorithmException
	 * @throws JOSEException
	 */
	@Test
	void givenErrorFromKeyVault_whenVerify_thenReturnFailure() throws NoSuchAlgorithmException, JOSEException {
		/*
		 * Data preparation.
		 */
		KeyBundle keyBundle = prepareValidKey("valid_key_name", "v1", 0);
		KeyVaultKey keyVaultKey = keyBundle.getKeyVaultKey();
		PublicKey publicKey = keyBundle.getPublicKey();
		RSAPrivateKey rsaPrivateKey = keyBundle.getPrivateKey();

		Date now = new Date();
		JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
			.subject("<subject>")
			.audience("<audience>")
			.issuer("<issuer>")
			.issueTime(now)
			.expirationTime(new Date(now.getTime() + 5 * 60 * 1000))
			.build();

		JWSHeader header = new JWSHeader(JWSAlgorithm.RS256, null, null, null, null, null, null, null, null, null, publicKey.getKid(), true, null, null);
		Payload payload = claimsSet.toPayload();

		SignedJWT signedJwt = new SignedJWT(header, claimsSet);
		JWSSigner signer = new RSASSASigner(rsaPrivateKey);
		signedJwt.sign(signer);
		byte[] signature = signedJwt.getSignature().decode();

		String stringToSign = header.toBase64URL().toString() + "." + payload.toBase64URL().toString();
		byte[] bytesToSign = stringToSign.getBytes(StandardCharsets.UTF_8);

		MessageDigest messageDigest = MessageDigest.getInstance("SHA256");
		messageDigest.update(bytesToSign);

		byte[] digest = messageDigest.digest();

		/*
		 * Setup mocks.
		 */
		CryptographyAsyncClient cryptoClient = mock(CryptographyAsyncClient.class);
		when(cryptoClient.verify(SignatureAlgorithm.RS256, digest, signature))
			.thenReturn(Mono.error(new Exception("error from azure")));

		KeyAsyncClient keyClient = mock(KeyAsyncClient.class);
		when(keyClient.getCryptographyAsyncClient(keyVaultKey.getProperties().getName(), keyVaultKey.getProperties().getVersion()))
			.thenReturn(cryptoClient);

		AzureKeyFinder keyFinder = mock(AzureKeyFinder.class);
		when(keyFinder.findPublicKey())
			.thenReturn(UniGenerator.item(publicKey));
		when(keyFinder.getKeyClient())
			.thenReturn(keyClient);

		/*
		 * Test.
		 */
		AzureTokenSigner tokenSigner = new AzureTokenSigner(keyFinder);
		tokenSigner.verify(signedJwt)
			.subscribe().withSubscriber(UniAssertSubscriber.create())
			.awaitFailure()
			.assertFailedWith(AuthError.class);
	}

	/**
	 * 
	 * @throws NoSuchAlgorithmException
	 * @throws JOSEException
	 */
	@Test
	void givenExceptionFromKeyVault_whenVerify_thenReturnFailure() throws NoSuchAlgorithmException, JOSEException {
		/*
		 * Data preparation.
		 */
		KeyBundle keyBundle = prepareValidKey("valid_key_name", "v1", 0);
		KeyVaultKey keyVaultKey = keyBundle.getKeyVaultKey();
		PublicKey publicKey = keyBundle.getPublicKey();
		RSAPrivateKey rsaPrivateKey = keyBundle.getPrivateKey();

		Date now = new Date();
		JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
			.subject("<subject>")
			.audience("<audience>")
			.issuer("<issuer>")
			.issueTime(now)
			.expirationTime(new Date(now.getTime() + 5 * 60 * 1000))
			.build();

		JWSHeader header = new JWSHeader(JWSAlgorithm.RS256, null, null, null, null, null, null, null, null, null, publicKey.getKid(), true, null, null);
		Payload payload = claimsSet.toPayload();

		SignedJWT signedJwt = new SignedJWT(header, claimsSet);
		JWSSigner signer = new RSASSASigner(rsaPrivateKey);
		signedJwt.sign(signer);
		byte[] signature = signedJwt.getSignature().decode();

		String stringToSign = header.toBase64URL().toString() + "." + payload.toBase64URL().toString();
		byte[] bytesToSign = stringToSign.getBytes(StandardCharsets.UTF_8);

		MessageDigest messageDigest = MessageDigest.getInstance("SHA256");
		messageDigest.update(bytesToSign);

		byte[] digest = messageDigest.digest();

		/*
		 * Setup mocks.
		 */
		CryptographyAsyncClient cryptoClient = mock(CryptographyAsyncClient.class);
		when(cryptoClient.verify(SignatureAlgorithm.RS256, digest, signature))
			.thenThrow(ResourceNotFoundException.class);

		KeyAsyncClient keyClient = mock(KeyAsyncClient.class);
		when(keyClient.getCryptographyAsyncClient(keyVaultKey.getProperties().getName(), keyVaultKey.getProperties().getVersion()))
			.thenReturn(cryptoClient);

		AzureKeyFinder keyFinder = mock(AzureKeyFinder.class);
		when(keyFinder.findPublicKey())
			.thenReturn(UniGenerator.item(publicKey));
		when(keyFinder.getKeyClient())
			.thenReturn(keyClient);

		/*
		 * Test.
		 */
		AzureTokenSigner tokenSigner = new AzureTokenSigner(keyFinder);
		tokenSigner.verify(signedJwt)
			.subscribe().withSubscriber(UniAssertSubscriber.create())
			.awaitFailure()
			.assertFailedWith(AuthError.class);
	}

	/**
	 * 
	 * @throws JOSEException
	 */
	@Test
	void givenInvalidKid_whenVerify_thenReturnFailure() throws JOSEException {
		/*
		 * Data preparation.
		 */
		KeyBundle keyBundle = prepareValidKey("valid_key_name/bad", "v1", 0);
		PublicKey publicKey = keyBundle.getPublicKey();
		RSAPrivateKey rsaPrivateKey = keyBundle.getPrivateKey();

		Date now = new Date();
		JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
			.subject("<subject>")
			.audience("<audience>")
			.issuer("<issuer>")
			.issueTime(now)
			.expirationTime(new Date(now.getTime() + 5 * 60 * 1000))
			.build();

		JWSHeader header = new JWSHeader(JWSAlgorithm.RS256, null, null, null, null, null, null, null, null, null, publicKey.getKid(), true, null, null);

		SignedJWT signedJwt = new SignedJWT(header, claimsSet);
		JWSSigner signer = new RSASSASigner(rsaPrivateKey);
		signedJwt.sign(signer);

		/*
		 * Setup mock.
		 */
		AzureKeyFinder keyFinder = mock(AzureKeyFinder.class);
		when(keyFinder.findPublicKey())
			.thenReturn(UniGenerator.item(publicKey));

		/*
		 * Test.
		 */
		AzureTokenSigner tokenSigner = new AzureTokenSigner(keyFinder);
		tokenSigner.verify(signedJwt)
			.subscribe().withSubscriber(UniAssertSubscriber.create())
			.awaitFailure()
			.assertFailedWith(AuthError.class);
	}

	/**
	 * 
	 * @throws JOSEException
	 */
	@Test
	void givenUnsupportedDigestAlgorithm_whenVerify_thenReturnFailure() throws JOSEException {
		/*
		 * Data preparation.
		 */
		KeyBundle keyBundle = prepareValidKey("valid_key_name", "v1", 0);
		PublicKey publicKey = keyBundle.getPublicKey();
		RSAPrivateKey rsaPrivateKey = keyBundle.getPrivateKey();

		Date now = new Date();
		JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
			.subject("<subject>")
			.audience("<audience>")
			.issuer("<issuer>")
			.issueTime(now)
			.expirationTime(new Date(now.getTime() + 5 * 60 * 1000))
			.build();

		JWSHeader header = new JWSHeader(JWSAlgorithm.RS256, null, null, null, null, null, null, null, null, null, publicKey.getKid(), true, null, null);

		SignedJWT signedJwt = new SignedJWT(header, claimsSet);
		JWSSigner signer = new RSASSASigner(rsaPrivateKey);
		signedJwt.sign(signer);

		/*
		 * Setup mocks.
		 */
		AzureKeyFinder keyFinder = mock(AzureKeyFinder.class);
		when(keyFinder.findPublicKey())
			.thenReturn(UniGenerator.item(publicKey));

		try (MockedStatic<MessageDigest> digest = mockStatic(MessageDigest.class)) {
			digest.when(() -> MessageDigest.getInstance("SHA256"))
				.thenThrow(NoSuchAlgorithmException.class);

			/*
			 * Test.
			 */
			AzureTokenSigner tokenSigner = new AzureTokenSigner(keyFinder);
			tokenSigner.verify(signedJwt)
				.subscribe().withSubscriber(UniAssertSubscriber.create())
				.awaitFailure()
				.assertFailedWith(AuthError.class);
		}
	}

	/**
	 * 
	 * @throws JOSEException
	 * @throws NoSuchAlgorithmException
	 */
	@Test
	void givenBadToken_whenSign_thenReturnFailure() throws JOSEException, NoSuchAlgorithmException {
		/*
		 * Data preparation.
		 */
		KeyBundle keyBundle = prepareValidKey("valid_key_name", "v1", 0);
		KeyVaultKey keyVaultKey = keyBundle.getKeyVaultKey();
		PublicKey publicKey = keyBundle.getPublicKey();
		RSAPrivateKey rsaPrivateKey = keyBundle.getPrivateKey();

		Date now = new Date();
		JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
			.subject("<subject>")
			.audience("<audience>")
			.issuer("<issuer>")
			.issueTime(now)
			.expirationTime(new Date(now.getTime() + 5 * 60 * 1000))
			.build();

		JWSHeader header = new JWSHeader(JWSAlgorithm.RS256, null, null, null, null, null, null, null, null, null, publicKey.getKid(), true, null, null);
		Payload payload = claimsSet.toPayload();

		SignedJWT signedJwt = new SignedJWT(header, claimsSet);
		JWSSigner signer = new RSASSASigner(rsaPrivateKey);
		signedJwt.sign(signer);
		byte[] signature = signedJwt.getSignature().decode();

		String stringToSign = header.toBase64URL().toString() + "." + payload.toBase64URL().toString();
		byte[] bytesToSign = stringToSign.getBytes(StandardCharsets.UTF_8);

		MessageDigest messageDigest = MessageDigest.getInstance("SHA256");
		messageDigest.update(bytesToSign);

		byte[] digest = messageDigest.digest();

		/*
		 * Setup mocks.
		 */
		CryptographyAsyncClient cryptoClient = mock(CryptographyAsyncClient.class);
		when(cryptoClient.sign(SignatureAlgorithm.RS256, digest))
			.thenReturn(Mono.just(new SignResult(signature, SignatureAlgorithm.RS256, publicKey.getKid())));

		KeyAsyncClient keyClient = mock(KeyAsyncClient.class);
		when(keyClient.getCryptographyAsyncClient(keyVaultKey.getProperties().getName(), keyVaultKey.getProperties().getVersion()))
			.thenReturn(cryptoClient);

		AzureKeyFinder keyFinder = mock(AzureKeyFinder.class);
		when(keyFinder.findPublicKey())
			.thenReturn(UniGenerator.item(publicKey));
		when(keyFinder.getKeyClient())
			.thenReturn(keyClient);

		try (MockedStatic<SignedJWTFactory> factory = mockStatic(SignedJWTFactory.class)) {
			factory.when(() -> SignedJWTFactory.createInstance(eq(header.toBase64URL()), eq(payload.toBase64URL()), any(Base64URL.class)))
				.thenThrow(new ParseException("bad token", 0));

			/*
			 * Test.
			 */
			AzureTokenSigner tokenSigner = new AzureTokenSigner(keyFinder);
			tokenSigner.sign(claimsSet)
				.subscribe().withSubscriber(UniAssertSubscriber.create())
				.awaitFailure()
				.assertFailedWith(AuthError.class);
		}
	}
}