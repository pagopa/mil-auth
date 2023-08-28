/*
 * SignatureTests.java
 *
 * 24 lug 2023
 */
package it.pagopa.swclient.mil.auth;

import static it.pagopa.swclient.mil.auth.util.KeyPairUtil.getPublicKey;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.Signature;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.util.Base64URL;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import io.quarkus.logging.Log;
import it.pagopa.swclient.mil.auth.azurekeyvault.bean.GetAccessTokenResponse;
import it.pagopa.swclient.mil.auth.bean.PublicKey;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * 
 */
public class SignatureTests {
	/*
	 * 
	 */
	private static final String AUTH_URL = "https://login.microsoftonline.com/7788edaf-0346-4068-9d79-c868aed15b3d/oauth2/v2.0/token";
	private static final String CLIENT_ID = "0f76211d-1ace-4df1-9e75-797354aacc2e";
	private static final String CLIENT_SECRET = "b6p8Q~jdWT4jEqQMygJhNmKtzJEYqtQPtBqvXcoF";
	private static final String SCOPE = "https://vault.azure.net/.default";
	private static final String AUTH_REQ_BODY_TEMPL = "grant_type=client_credentials&client_id=%s&client_secret=%s&scope=%s";
	
	/*
	 * 
	 */
	private static JWSHeader header;
	private static JWTClaimsSet payload;

	/*
	 * 
	 */
	private static String headerBase64;
	private static String payloadBase64;

	/*
	 * 
	 */
	private static RSAPrivateKey privateKey;
	private static RSAPublicKey publicKey;

	/**
	 * 
	 * @throws Exception
	 */
	private static void init() throws Exception {
		/*
		 * 
		 */
		header = new JWSHeader(JWSAlgorithm.RS256, null, null, null, null, null, null, null, null, null, "KID", true, null, null);

		payload = new JWTClaimsSet.Builder()
			.subject("CLIENT")
			.issueTime(new Date(1690276228000L))
			.expirationTime(new Date(1690276228000L + 5 * 60 * 1000))
			.claim("acquirerId", "ACQUIRER")
			.claim("channel", "CHANNEL")
			.claim("merchantId", "MERCHANT")
			.claim("clientId", "CLIENT")
			.claim("terminalId", "TERMINAL")
			.claim("scope", "SCOPES")
			.claim("groups", List.of("GROUP#1", "GROUP#2"))
			.build();

		headerBase64 = Base64.getUrlEncoder().encodeToString(header.toString().getBytes(StandardCharsets.UTF_8));
		payloadBase64 = Base64.getUrlEncoder().encodeToString(payload.toString().getBytes(StandardCharsets.UTF_8));

		System.out.printf("header.......: %s%n", headerBase64);
		System.out.printf("payloadBase64: %s%n", payloadBase64);

		/*
		 * 
		 */
		KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
		generator.initialize(4096);
		KeyPair keyPair = generator.generateKeyPair();
		privateKey = (RSAPrivateKey) keyPair.getPrivate();
		publicKey = (RSAPublicKey) keyPair.getPublic();
	}

	/**
	 * 
	 * @return
	 * @throws Exception
	 */
	private static String signWithNimbus() throws Exception {
		System.out.println("method......: NIMBUS");

		SignedJWT token = new SignedJWT(header, payload);

		JWSSigner signer = new RSASSASigner(privateKey);
		token.sign(signer);

		String[] components = token.serialize().split("\\.");
		String signatureBase64 = components[2];

		System.out.printf("signature....: %s%n", signatureBase64);
		return signatureBase64;
	}

	/**
	 * 
	 * @param signatureBase64
	 * @return
	 * @throws Exception
	 */
	private static boolean verifyWithNimbus(String signatureBase64) throws Exception {
		System.out.println("method......: NIMBUS");

		SignedJWT token = new SignedJWT(Base64URL.from(headerBase64), Base64URL.from(payloadBase64), Base64URL.from(signatureBase64));

		JWSVerifier verifier = new RSASSAVerifier(publicKey);
		boolean verifyOk = token.verify(verifier);

		System.out.printf("verification.: %s%n", verifyOk);
		return verifyOk;
	}

	/**
	 * 
	 * @return
	 * @throws Exception
	 */
	private static String signWithSha256WithRsa() throws Exception {
		System.out.println("method......: SHA256withRSA");

		String stringToSign = headerBase64 + "." + payloadBase64;
		byte[] bytesToSign = stringToSign.getBytes(StandardCharsets.UTF_8);

		Signature signature = Signature.getInstance("SHA256withRSA");
		signature.initSign(privateKey);
		signature.update(bytesToSign);
		byte[] signatureBytes = signature.sign();
		String signatureBase64 = Base64.getUrlEncoder().encodeToString(signatureBytes);

		System.out.printf("signature....: %s%n", signatureBase64);
		return signatureBase64;
	}

	/**
	 * 
	 * @param signatureBase64
	 * @return
	 * @throws Exception
	 */
	private static boolean verifyWithSha256WithRsa(String signatureBase64) throws Exception {
		System.out.println("method......: SHA256withRSA");

		String stringToSign = headerBase64 + "." + payloadBase64;
		byte[] bytesToSign = stringToSign.getBytes(StandardCharsets.UTF_8);

		Signature signature = Signature.getInstance("SHA256withRSA");
		signature.initVerify(publicKey);
		signature.update(bytesToSign);
		byte[] signatureBytes = Base64.getUrlDecoder().decode(signatureBase64);
		boolean verifyOk = signature.verify(signatureBytes);

		System.out.printf("verification.: %s%n", verifyOk);
		return verifyOk;
	}

	/**
	 * 
	 * @return
	 * @throws Exception
	 */
	private static String signWithNoneWithRsa() throws Exception {
		System.out.println("method......: NONEwithRSA");

		String stringToSign = headerBase64 + "." + payloadBase64;
		byte[] bytesToSign = stringToSign.getBytes(StandardCharsets.UTF_8);

		MessageDigest digest = MessageDigest.getInstance("SHA256");
		digest.update(bytesToSign);
		byte[] hash = digest.digest();
		System.out.printf("hashBase64...: %s%n", Base64.getUrlEncoder().encodeToString(hash));

		byte[] id = new byte[] {
			0x30, 0x31, 0x30, 0x0d, 0x06, 0x09, 0x60, (byte) 0x86, 0x48, 0x01, 0x65, 0x03, 0x04, 0x02, 0x01, 0x05, 0x00, 0x04, 0x20
		};
		byte[] derDigestInfo = new byte[id.length + hash.length];
		System.arraycopy(id, 0, derDigestInfo, 0, id.length);
		System.arraycopy(hash, 0, derDigestInfo, id.length, hash.length);

		Signature signature = Signature.getInstance("NONEwithRSA");
		signature.initSign(privateKey);
		signature.update(derDigestInfo);

		byte[] signatureBytes = signature.sign();
		String signatureBase64 = Base64.getUrlEncoder().encodeToString(signatureBytes);

		System.out.printf("signature....: %s%n", signatureBase64);
		return signatureBase64;
	}
	
	/**
	 * 
	 * @param signatureBase64
	 * @return
	 * @throws Exception
	 */
	private static boolean verifyWithNoneWithRsa(String signatureBase64) throws Exception {
		System.out.println("method......: NONEwithRSA");

		String stringToSign = headerBase64 + "." + payloadBase64;
		byte[] bytesToSign = stringToSign.getBytes(StandardCharsets.UTF_8);

		MessageDigest digest = MessageDigest.getInstance("SHA256");
		digest.update(bytesToSign);
		byte[] hash = digest.digest();

		byte[] id = new byte[] {
			0x30, 0x31, 0x30, 0x0d, 0x06, 0x09, 0x60, (byte) 0x86, 0x48, 0x01, 0x65, 0x03, 0x04, 0x02, 0x01, 0x05, 0x00, 0x04, 0x20
		};
		byte[] derDigestInfo = new byte[id.length + hash.length];
		System.arraycopy(id, 0, derDigestInfo, 0, id.length);
		System.arraycopy(hash, 0, derDigestInfo, id.length, hash.length);

		Signature signature = Signature.getInstance("NONEwithRSA");
		signature.initVerify(publicKey);
		signature.update(derDigestInfo);

		byte[] signatureBytes = Base64.getUrlDecoder().decode(signatureBase64);
		boolean verifyOk = signature.verify(signatureBytes);

		System.out.printf("verification.: %s%n", verifyOk);
		return verifyOk;
	}
	
	/**
	 * 
	 * @return
	 */
	public static String getAccessToken() {
		String accessToken = null;
		HttpURLConnection con = null;
		try {
			con = (HttpURLConnection) new URL(AUTH_URL).openConnection();
			con.setConnectTimeout(5000);
			con.setReadTimeout(5000);
			con.setRequestMethod("POST");
			con.setDoOutput(true);
			con.setRequestProperty("Content-Type", MediaType.APPLICATION_FORM_URLENCODED + ";charset=" + StandardCharsets.UTF_8);
			con.setRequestProperty("Accept", MediaType.APPLICATION_JSON + ";charset=" + StandardCharsets.UTF_8);
			byte[] req = String.format(AUTH_REQ_BODY_TEMPL, CLIENT_ID, CLIENT_SECRET, SCOPE).getBytes(StandardCharsets.UTF_8);
			try (OutputStream os = con.getOutputStream()) {
				os.write(req, 0, req.length);
			}
			int statusCode = con.getResponseCode();
			if (statusCode == Response.Status.OK.getStatusCode()) {
				ObjectMapper mapper = new ObjectMapper();
				
				
				TypeReference<HashMap<String, String>> typeRef 
				  = new TypeReference<HashMap<String, String>>() {};
				Map<String, String> map = mapper.readValue(con.getInputStream(), typeRef);
				accessToken = map.get("access_token");
			} else {
				System.err.printf("Response Code = %d%n", statusCode);
			}
		} catch (IOException e) {
			e.printStackTrace(System.err);
		} finally {
			if (con != null) {
				con.disconnect();
			}
		}
		return accessToken;
	}

	/**
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		init();
		String signatureWithNimbus = signWithNimbus();
		String signatureWithSha256WithRsa = signWithSha256WithRsa();
		String signatureWithNoneWithRsa = signWithNoneWithRsa();
		verifyWithNimbus(signatureWithNimbus);
		verifyWithNimbus(signatureWithSha256WithRsa);
		verifyWithNimbus(signatureWithNoneWithRsa);
		verifyWithSha256WithRsa(signatureWithNimbus);
		verifyWithSha256WithRsa(signatureWithSha256WithRsa);
		verifyWithSha256WithRsa(signatureWithNoneWithRsa);
		verifyWithNoneWithRsa(signatureWithNimbus);
		verifyWithNoneWithRsa(signatureWithSha256WithRsa);
		verifyWithNoneWithRsa(signatureWithNoneWithRsa);
	}
}
