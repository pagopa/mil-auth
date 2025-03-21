/*
 * ValidatorTest.java
 *
 * 10 giu 2024
 */
package it.pagopa.swclient.mil.auth.validation.constraints;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.text.ParseException;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import com.nimbusds.jwt.SignedJWT;

import io.quarkus.test.junit.QuarkusTest;
import it.pagopa.swclient.mil.auth.bean.GetAccessTokenRequest;
import it.pagopa.swclient.mil.auth.bean.GrantType;
import it.pagopa.swclient.mil.bean.Channel;

/**
 * 
 * @author Antonio Tarricone
 */
@QuarkusTest
class ValidatorTest {
	/*
	 * 
	 */
	private static SignedJWT refreshToken;

	/**
	 * 
	 * @throws ParseException
	 */
	@BeforeAll
	static void setup() throws ParseException {
		refreshToken = SignedJWT.parse("eyJraWQiOiIzOGE1ZDA4ZGM4NzU0MGVhYjc3ZGViNGQ5ZWFiMjM4MC8zNzExY2U3NWFiYmI0MWM5YmZhOTEwMzM0Y2FiMDMzZSIsImFsZyI6IlJTMjU2In0.eyJzdWIiOiI0NTg1NjI1LzI4NDA1ZkhmazczeDg4RC8wMTIzNDU2NyIsImF1ZCI6Im1pbC5wYWdvcGEuaXQiLCJjbGllbnRJZCI6IjUyNTRmMDg3LTEyMTQtNDVjZC05NGFlLWZkYTUzYzgzNTE5NyIsIm1lcmNoYW50SWQiOiIyODQwNWZIZms3M3g4OEQiLCJzY29wZSI6Im9mZmxpbmVfYWNjZXNzIiwiY2hhbm5lbCI6IlBPUyIsImlzcyI6Imh0dHBzOi8vbWlsLWQtYXBpbS5henVyZS1hcGkubmV0L21pbC1hdXRoIiwidGVybWluYWxJZCI6IjAxMjM0NTY3IiwiZXhwIjoxNzM1OTEwMTcxLCJhY3F1aXJlcklkIjoiNDU4NTYyNSIsImlhdCI6MTczNTkwNjU3MX0.Ztu8SlQCjXErum9xRsqUMOd0ucGvfeKhDHAjR3lzo9KV0KiRdy8RckcR-Zg6Yt1Pu4jIl59xlMIE0KZFoHBTFqIzJp0h6HiSvvus8fArJ6Fu5YfMmtOoq9yEkw1GfBWHiYXt-y4LMw9gfus5DA2fEttY6kQVK7mznDUL3eGzTM2OSQlS3rrrnJUuxVR_8RsS1bYVpsUmu36W0Uf0Jd49GvnuqCKakJpr4rzcyvt358NVWrNH4Qqtjg4dCAyXPkM_MHez4XtaMXRh6O8UkOym9DI9n7zkmkkmx-ZccHDkAMmsGJKwviaIMVyrQJ2S3RXzAbcXZS13nb3djskN-3XC5Q");
	}

	/**
	 * 
	 * @param testInfo
	 */
	@BeforeEach
	void init(TestInfo testInfo) {
		String frame = "*".repeat(testInfo.getDisplayName().length() + 11);
		System.out.println(frame);
		System.out.printf("* %s: START *%n", testInfo.getDisplayName());
		System.out.println(frame);
	}

	/*
	 * GRANT TYPE = PASSWORD + CHANNEL = POS
	 */
	@Test
	void given_grantTypePasswordAndChannelPos_when_allIsOk_then_getValid() {
		assertTrue(new Validator()
			.isValid(new GetAccessTokenRequest()
				.setGrantType(GrantType.PASSWORD)
				.setChannel(Channel.POS)
				.setAcquirerId("acquirer_id")
				.setMerchantId("merchant_id")
				.setTerminalId("terminal_id")
				.setClientSecret(null)
				.setRefreshToken(null)
				.setUsername("username")
				.setPassword("password"),
				null));
	}

	@Test
	void given_grantTypePasswordAndChannelPos_when_acquirerIdIsNull_then_getNotValid() {
		assertFalse(new Validator()
			.isValid(new GetAccessTokenRequest()
				.setGrantType(GrantType.PASSWORD)
				.setChannel(Channel.POS)
				.setAcquirerId(null)
				.setMerchantId("merchant_id")
				.setTerminalId("terminal_id")
				.setClientSecret(null)
				.setRefreshToken(null)
				.setUsername("username")
				.setPassword("password"),
				null));
	}

	@Test
	void given_grantTypePasswordAndChannelPos_when_merchantIdIsNull_then_getNotValid() {
		assertFalse(new Validator()
			.isValid(new GetAccessTokenRequest()
				.setGrantType(GrantType.PASSWORD)
				.setChannel(Channel.POS)
				.setAcquirerId("acquirer_id")
				.setMerchantId(null)
				.setTerminalId("terminal_id")
				.setClientSecret(null)
				.setRefreshToken(null)
				.setUsername("username")
				.setPassword("password"),
				null));
	}

	@Test
	void given_grantTypePasswordAndChannelPos_when_terminalIdIsNull_then_getNotValid() {
		assertFalse(new Validator()
			.isValid(new GetAccessTokenRequest()
				.setGrantType(GrantType.PASSWORD)
				.setChannel(Channel.POS)
				.setAcquirerId("acquirer_id")
				.setMerchantId("merchant_id")
				.setTerminalId(null)
				.setClientSecret(null)
				.setRefreshToken(null)
				.setUsername("username")
				.setPassword("password"),
				null));
	}

	@Test
	void given_grantTypePasswordAndChannelPos_when_clientSecretIsNotNull_then_getNotValid() {
		assertFalse(new Validator()
			.isValid(new GetAccessTokenRequest()
				.setGrantType(GrantType.PASSWORD)
				.setChannel(Channel.POS)
				.setAcquirerId("acquirer_id")
				.setMerchantId("merchant_id")
				.setTerminalId("terminal_id")
				.setClientSecret("client_secret")
				.setRefreshToken(null)
				.setUsername("username")
				.setPassword("password"),
				null));
	}

	@Test
	void given_grantTypePasswordAndChannelPos_when_refreshTokenIsNotNull_then_getNotValid() throws ParseException {
		assertFalse(new Validator()
			.isValid(new GetAccessTokenRequest()
				.setGrantType(GrantType.PASSWORD)
				.setChannel(Channel.POS)
				.setAcquirerId("acquirer_id")
				.setMerchantId("merchant_id")
				.setTerminalId("terminal_id")
				.setClientSecret(null)
				.setRefreshToken(SignedJWT.parse("eyJraWQiOiIzOGE1ZDA4ZGM4NzU0MGVhYjc3ZGViNGQ5ZWFiMjM4MC8zNzExY2U3NWFiYmI0MWM5YmZhOTEwMzM0Y2FiMDMzZSIsImFsZyI6IlJTMjU2In0.eyJzdWIiOiI0NTg1NjI1LzI4NDA1ZkhmazczeDg4RC8wMTIzNDU2NyIsImF1ZCI6Im1pbC5wYWdvcGEuaXQiLCJjbGllbnRJZCI6IjUyNTRmMDg3LTEyMTQtNDVjZC05NGFlLWZkYTUzYzgzNTE5NyIsIm1lcmNoYW50SWQiOiIyODQwNWZIZms3M3g4OEQiLCJzY29wZSI6Im9mZmxpbmVfYWNjZXNzIiwiY2hhbm5lbCI6IlBPUyIsImlzcyI6Imh0dHBzOi8vbWlsLWQtYXBpbS5henVyZS1hcGkubmV0L21pbC1hdXRoIiwidGVybWluYWxJZCI6IjAxMjM0NTY3IiwiZXhwIjoxNzM1OTEwMTcxLCJhY3F1aXJlcklkIjoiNDU4NTYyNSIsImlhdCI6MTczNTkwNjU3MX0.Ztu8SlQCjXErum9xRsqUMOd0ucGvfeKhDHAjR3lzo9KV0KiRdy8RckcR-Zg6Yt1Pu4jIl59xlMIE0KZFoHBTFqIzJp0h6HiSvvus8fArJ6Fu5YfMmtOoq9yEkw1GfBWHiYXt-y4LMw9gfus5DA2fEttY6kQVK7mznDUL3eGzTM2OSQlS3rrrnJUuxVR_8RsS1bYVpsUmu36W0Uf0Jd49GvnuqCKakJpr4rzcyvt358NVWrNH4Qqtjg4dCAyXPkM_MHez4XtaMXRh6O8UkOym9DI9n7zkmkkmx-ZccHDkAMmsGJKwviaIMVyrQJ2S3RXzAbcXZS13nb3djskN-3XC5Q"))
				.setUsername("username")
				.setPassword("password"),
				null));
	}

	@Test
	void given_grantTypePasswordAndChannelPos_when_usernameIsNotNull_then_getNotValid() {
		assertFalse(new Validator()
			.isValid(new GetAccessTokenRequest()
				.setGrantType(GrantType.PASSWORD)
				.setChannel(Channel.POS)
				.setAcquirerId("acquirer_id")
				.setMerchantId("merchant_id")
				.setTerminalId("terminal_id")
				.setClientSecret(null)
				.setRefreshToken(null)
				.setUsername(null)
				.setPassword("password"),
				null));
	}

	@Test
	void given_grantTypePasswordAndChannelPos_when_passwordIsNotNull_then_getNotValid() {
		assertFalse(new Validator()
			.isValid(new GetAccessTokenRequest()
				.setGrantType(GrantType.PASSWORD)
				.setChannel(Channel.POS)
				.setAcquirerId("acquirer_id")
				.setMerchantId("merchant_id")
				.setTerminalId("terminal_id")
				.setClientSecret(null)
				.setRefreshToken(null)
				.setUsername("username")
				.setPassword(null),
				null));
	}

	/*
	 * GRANT TYPE = REFRESH TOKEN + CHANNEL = POS
	 */
	@Test
	void given_grantTypeRefreshTokenAndChannelPos_when_allIsOk_then_getValid() {
		assertTrue(new Validator()
			.isValid(new GetAccessTokenRequest()
				.setGrantType(GrantType.REFRESH_TOKEN)
				.setChannel(Channel.POS)
				.setAcquirerId("acquirer_id")
				.setMerchantId("merchant_id")
				.setTerminalId("terminal_id")
				.setClientSecret(null)
				.setRefreshToken(refreshToken)
				.setUsername(null)
				.setPassword(null)
				.setScope(null),
				null));
	}

	@Test
	void given_grantTypeRefreshTokenAndChannelPos_when_acquirerIdIsNull_then_getNotValid() {
		assertFalse(new Validator()
			.isValid(new GetAccessTokenRequest()
				.setGrantType(GrantType.REFRESH_TOKEN)
				.setChannel(Channel.POS)
				.setAcquirerId(null)
				.setMerchantId("merchant_id")
				.setTerminalId("terminal_id")
				.setClientSecret(null)
				.setRefreshToken(refreshToken)
				.setUsername(null)
				.setPassword(null)
				.setScope(null),
				null));
	}

	@Test
	void given_grantTypeRefreshTokenAndChannelPos_when_merchantIdIsNull_then_getNotValid() {
		assertFalse(new Validator()
			.isValid(new GetAccessTokenRequest()
				.setGrantType(GrantType.REFRESH_TOKEN)
				.setChannel(Channel.POS)
				.setAcquirerId("acquirer_id")
				.setMerchantId(null)
				.setTerminalId("terminal_id")
				.setClientSecret(null)
				.setRefreshToken(refreshToken)
				.setUsername(null)
				.setPassword(null)
				.setScope(null),
				null));
	}

	@Test
	void given_grantTypeRefreshTokenAndChannelPos_when_terminalIdIsNull_then_getNotValid() {
		assertFalse(new Validator()
			.isValid(new GetAccessTokenRequest()
				.setGrantType(GrantType.REFRESH_TOKEN)
				.setChannel(Channel.POS)
				.setAcquirerId("acquirer_id")
				.setMerchantId("merchant_id")
				.setTerminalId(null)
				.setClientSecret(null)
				.setRefreshToken(refreshToken)
				.setUsername(null)
				.setPassword(null)
				.setScope(null),
				null));
	}

	@Test
	void given_grantTypeRefreshTokenAndChannelPos_when_clientSecretIsNotNull_then_getNotValid() {
		assertFalse(new Validator()
			.isValid(new GetAccessTokenRequest()
				.setGrantType(GrantType.REFRESH_TOKEN)
				.setChannel(Channel.POS)
				.setAcquirerId("acquirer_id")
				.setMerchantId("merchant_id")
				.setTerminalId("terminal_id")
				.setClientSecret("client_secret")
				.setRefreshToken(refreshToken)
				.setUsername(null)
				.setPassword(null)
				.setScope(null),
				null));
	}

	@Test
	void given_grantTypeRefreshTokenAndChannelPos_when_refreshTokenIsNull_then_getNotValid() {
		assertFalse(new Validator()
			.isValid(new GetAccessTokenRequest()
				.setGrantType(GrantType.REFRESH_TOKEN)
				.setChannel(Channel.POS)
				.setAcquirerId("acquirer_id")
				.setMerchantId("merchant_id")
				.setTerminalId("terminal_id")
				.setClientSecret(null)
				.setRefreshToken(null)
				.setUsername(null)
				.setPassword(null)
				.setScope(null),
				null));
	}

	@Test
	void given_grantTypeRefreshTokenAndChannelPos_when_usernameIsNotNull_then_getNotValid() {
		assertFalse(new Validator()
			.isValid(new GetAccessTokenRequest()
				.setGrantType(GrantType.REFRESH_TOKEN)
				.setChannel(Channel.POS)
				.setAcquirerId("acquirer_id")
				.setMerchantId("merchant_id")
				.setTerminalId("terminal_id")
				.setClientSecret(null)
				.setRefreshToken(refreshToken)
				.setUsername("username")
				.setPassword(null)
				.setScope(null),
				null));
	}

	@Test
	void given_grantTypeRefreshTokenAndChannelPos_when_passwordIsNotNull_then_getNotValid() {
		assertFalse(new Validator()
			.isValid(new GetAccessTokenRequest()
				.setGrantType(GrantType.REFRESH_TOKEN)
				.setChannel(Channel.POS)
				.setAcquirerId("acquirer_id")
				.setMerchantId("merchant_id")
				.setTerminalId("terminal_id")
				.setClientSecret(null)
				.setRefreshToken(refreshToken)
				.setUsername(null)
				.setPassword("password")
				.setScope(null),
				null));
	}

	@Test
	void given_grantTypeRefreshTokenAndChannelPos_when_scopeIsNotNull_then_getNotValid() {
		assertFalse(new Validator()
			.isValid(new GetAccessTokenRequest()
				.setGrantType(GrantType.REFRESH_TOKEN)
				.setChannel(Channel.POS)
				.setAcquirerId("acquirer_id")
				.setMerchantId("merchant_id")
				.setTerminalId("terminal_id")
				.setClientSecret(null)
				.setRefreshToken(refreshToken)
				.setUsername(null)
				.setPassword(null)
				.setScope("scope"),
				null));
	}

	/*
	 * GRANT TYPE = CLIENT CREDENTIALS + CHANNEL = ATM
	 */
	@Test
	void given_grantTypeClientCredetialsAndChannelAtm_when_allIsOk_then_getValid() {
		assertTrue(new Validator()
			.isValid(new GetAccessTokenRequest()
				.setGrantType(GrantType.CLIENT_CREDENTIALS)
				.setChannel(Channel.ATM)
				.setAcquirerId("acquirer_id")
				.setMerchantId(null)
				.setTerminalId("terminal_id")
				.setClientSecret("client_secret")
				.setRefreshToken(null)
				.setUsername(null)
				.setPassword(null)
				.setScope(null),
				null));
	}

	@Test
	void given_grantTypeClientCredetialsAndChannelAtm_when_acquirerIdIsNull_then_getNotValid() {
		assertFalse(new Validator()
			.isValid(new GetAccessTokenRequest()
				.setGrantType(GrantType.CLIENT_CREDENTIALS)
				.setChannel(Channel.ATM)
				.setAcquirerId(null)
				.setMerchantId(null)
				.setTerminalId("terminal_id")
				.setClientSecret("client_secret")
				.setRefreshToken(null)
				.setUsername(null)
				.setPassword(null)
				.setScope(null),
				null));
	}

	@Test
	void given_grantTypeClientCredetialsAndChannelAtm_when_merchantIdIsNotNull_then_getNotValid() {
		assertFalse(new Validator()
			.isValid(new GetAccessTokenRequest()
				.setGrantType(GrantType.CLIENT_CREDENTIALS)
				.setChannel(Channel.ATM)
				.setAcquirerId("acquirer_id")
				.setMerchantId("merchant_id")
				.setTerminalId("terminal_id")
				.setClientSecret("client_secret")
				.setRefreshToken(null)
				.setUsername(null)
				.setPassword(null)
				.setScope(null),
				null));
	}

	@Test
	void given_grantTypeClientCredetialsAndChannelAtm_when_terminalIdIsNull_then_getNotValid() {
		assertFalse(new Validator()
			.isValid(new GetAccessTokenRequest()
				.setGrantType(GrantType.CLIENT_CREDENTIALS)
				.setChannel(Channel.ATM)
				.setAcquirerId("acquirer_id")
				.setMerchantId(null)
				.setTerminalId(null)
				.setClientSecret("client_secret")
				.setRefreshToken(null)
				.setUsername(null)
				.setPassword(null)
				.setScope(null),
				null));
	}

	@Test
	void given_grantTypeClientCredetialsAndChannelAtm_when_clientSecretIsNull_then_getNotValid() {
		assertFalse(new Validator()
			.isValid(new GetAccessTokenRequest()
				.setGrantType(GrantType.CLIENT_CREDENTIALS)
				.setChannel(Channel.ATM)
				.setAcquirerId("acquirer_id")
				.setMerchantId(null)
				.setTerminalId("terminal_id")
				.setClientSecret(null)
				.setRefreshToken(null)
				.setUsername(null)
				.setPassword(null)
				.setScope(null),
				null));
	}

	@Test
	void given_grantTypeClientCredetialsAndChannelAtm_when_refreshTokenIsNotNull_then_getNotValid() {
		assertFalse(new Validator()
			.isValid(new GetAccessTokenRequest()
				.setGrantType(GrantType.CLIENT_CREDENTIALS)
				.setChannel(Channel.ATM)
				.setAcquirerId("acquirer_id")
				.setMerchantId(null)
				.setTerminalId("terminal_id")
				.setClientSecret("client_secret")
				.setRefreshToken(refreshToken)
				.setUsername(null)
				.setPassword(null)
				.setScope(null),
				null));
	}

	@Test
	void given_grantTypeClientCredetialsAndChannelAtm_when_usernameIsNotNull_then_getNotValid() {
		assertFalse(new Validator()
			.isValid(new GetAccessTokenRequest()
				.setGrantType(GrantType.CLIENT_CREDENTIALS)
				.setChannel(Channel.ATM)
				.setAcquirerId("acquirer_id")
				.setMerchantId(null)
				.setTerminalId("terminal_id")
				.setClientSecret("client_secret")
				.setRefreshToken(null)
				.setUsername("username")
				.setPassword(null)
				.setScope(null),
				null));
	}

	@Test
	void given_grantTypeClientCredetialsAndChannelAtm_when_passwordIsNotNull_then_getNotValid() {
		assertFalse(new Validator()
			.isValid(new GetAccessTokenRequest()
				.setGrantType(GrantType.CLIENT_CREDENTIALS)
				.setChannel(Channel.ATM)
				.setAcquirerId("acquirer_id")
				.setMerchantId(null)
				.setTerminalId("terminal_id")
				.setClientSecret("client_secret")
				.setRefreshToken(null)
				.setUsername(null)
				.setPassword("password")
				.setScope(null),
				null));
	}

	@Test
	void given_grantTypeClientCredetialsAndChannelAtm_when_scopeIsNotNull_then_getNotValid() {
		assertFalse(new Validator()
			.isValid(new GetAccessTokenRequest()
				.setGrantType(GrantType.CLIENT_CREDENTIALS)
				.setChannel(Channel.ATM)
				.setAcquirerId("acquirer_id")
				.setMerchantId(null)
				.setTerminalId("terminal_id")
				.setClientSecret("client_secret")
				.setRefreshToken(null)
				.setUsername(null)
				.setPassword(null)
				.setScope("scope"),
				null));
	}

	/*
	 * GRANT TYPE = CLIENT CREDENTIALS + CHANNEL = POS
	 */
	@Test
	void given_grantTypeClientCredetialsAndChannelPos_when_allIsOk_then_getValid() {
		assertTrue(new Validator()
			.isValid(new GetAccessTokenRequest()
				.setGrantType(GrantType.CLIENT_CREDENTIALS)
				.setChannel(Channel.POS)
				.setAcquirerId("acquirer_id")
				.setMerchantId("merchant_id")
				.setTerminalId("terminal_id")
				.setClientSecret("client_secret")
				.setRefreshToken(null)
				.setUsername(null)
				.setPassword(null)
				.setScope(null),
				null));
	}

	@Test
	void given_grantTypeClientCredetialsAndChannelPos_when_acquirerIdIsNull_then_getNotValid() {
		assertFalse(new Validator()
			.isValid(new GetAccessTokenRequest()
				.setGrantType(GrantType.CLIENT_CREDENTIALS)
				.setChannel(Channel.POS)
				.setAcquirerId(null)
				.setMerchantId("merchant_id")
				.setTerminalId("terminal_id")
				.setClientSecret("client_secret")
				.setRefreshToken(null)
				.setUsername(null)
				.setPassword(null)
				.setScope(null),
				null));
	}

	@Test
	void given_grantTypeClientCredetialsAndChannelPos_when_merchantIdIsNull_then_getNotValid() {
		assertFalse(new Validator()
			.isValid(new GetAccessTokenRequest()
				.setGrantType(GrantType.CLIENT_CREDENTIALS)
				.setChannel(Channel.POS)
				.setAcquirerId("acquirer_id")
				.setMerchantId(null)
				.setTerminalId("terminal_id")
				.setClientSecret("client_secret")
				.setRefreshToken(null)
				.setUsername(null)
				.setPassword(null)
				.setScope(null),
				null));
	}

	@Test
	void given_grantTypeClientCredetialsAndChannelPos_when_terminalIdIsNull_then_getNotValid() {
		assertFalse(new Validator()
			.isValid(new GetAccessTokenRequest()
				.setGrantType(GrantType.CLIENT_CREDENTIALS)
				.setChannel(Channel.POS)
				.setAcquirerId("acquirer_id")
				.setMerchantId("merchant_id")
				.setTerminalId(null)
				.setClientSecret("client_secret")
				.setRefreshToken(null)
				.setUsername(null)
				.setPassword(null)
				.setScope(null),
				null));
	}

	@Test
	void given_grantTypeClientCredetialsAndChannelPos_when_clientSecretdIsNull_then_getNotValid() {
		assertFalse(new Validator()
			.isValid(new GetAccessTokenRequest()
				.setGrantType(GrantType.CLIENT_CREDENTIALS)
				.setChannel(Channel.POS)
				.setAcquirerId("acquirer_id")
				.setMerchantId("merchant_id")
				.setTerminalId("terminal_id")
				.setClientSecret(null)
				.setRefreshToken(null)
				.setUsername(null)
				.setPassword(null)
				.setScope(null),
				null));
	}

	@Test
	void given_grantTypeClientCredetialsAndChannelPos_when_refreshTokenIsNotNull_then_getNotValid() {
		assertFalse(new Validator()
			.isValid(new GetAccessTokenRequest()
				.setGrantType(GrantType.CLIENT_CREDENTIALS)
				.setChannel(Channel.POS)
				.setAcquirerId("acquirer_id")
				.setMerchantId("merchant_id")
				.setTerminalId("terminal_id")
				.setClientSecret("client_secret")
				.setRefreshToken(refreshToken)
				.setUsername(null)
				.setPassword(null)
				.setScope(null),
				null));
	}

	@Test
	void given_grantTypeClientCredetialsAndChannelPos_when_usernameIsNotNull_then_getNotValid() {
		assertFalse(new Validator()
			.isValid(new GetAccessTokenRequest()
				.setGrantType(GrantType.CLIENT_CREDENTIALS)
				.setChannel(Channel.POS)
				.setAcquirerId("acquirer_id")
				.setMerchantId("merchant_id")
				.setTerminalId("terminal_id")
				.setClientSecret("client_secret")
				.setRefreshToken(null)
				.setUsername("username")
				.setPassword(null)
				.setScope(null),
				null));
	}

	@Test
	void given_grantTypeClientCredetialsAndChannelPos_when_passwordIsNotNull_then_getNotValid() {
		assertFalse(new Validator()
			.isValid(new GetAccessTokenRequest()
				.setGrantType(GrantType.CLIENT_CREDENTIALS)
				.setChannel(Channel.POS)
				.setAcquirerId("acquirer_id")
				.setMerchantId("merchant_id")
				.setTerminalId("terminal_id")
				.setClientSecret("client_secret")
				.setRefreshToken(null)
				.setUsername(null)
				.setPassword("password")
				.setScope(null),
				null));
	}

	@Test
	void given_grantTypeClientCredetialsAndChannelPos_when_scopeIsNotNull_then_getNotValid() {
		assertFalse(new Validator()
			.isValid(new GetAccessTokenRequest()
				.setGrantType(GrantType.CLIENT_CREDENTIALS)
				.setChannel(Channel.POS)
				.setAcquirerId("acquirer_id")
				.setMerchantId("merchant_id")
				.setTerminalId("terminal_id")
				.setClientSecret("client_secret")
				.setRefreshToken(null)
				.setUsername(null)
				.setPassword(null)
				.setScope("scope"),
				null));
	}

	/*
	 * GRANT TYPE = CLIENT CREDENTIALS + CHANNEL = null
	 */
	@Test
	void given_grantTypeClientCredetialsAndChannelNull_when_allIsOk_then_getValid() {
		assertTrue(new Validator()
			.isValid(new GetAccessTokenRequest()
				.setGrantType(GrantType.CLIENT_CREDENTIALS)
				.setChannel(null)
				.setAcquirerId(null)
				.setMerchantId(null)
				.setTerminalId(null)
				.setClientSecret("client_secret")
				.setRefreshToken(null)
				.setUsername(null)
				.setPassword(null)
				.setScope(null),
				null));
	}

	@Test
	void given_grantTypeClientCredetialsAndChannelNull_when_acquirerIdIsNotNull_then_getNotValid() {
		assertFalse(new Validator()
			.isValid(new GetAccessTokenRequest()
				.setGrantType(GrantType.CLIENT_CREDENTIALS)
				.setChannel(null)
				.setAcquirerId("acquirer_id")
				.setMerchantId(null)
				.setTerminalId(null)
				.setClientSecret("client_secret")
				.setRefreshToken(null)
				.setUsername(null)
				.setPassword(null)
				.setScope(null),
				null));
	}

	@Test
	void given_grantTypeClientCredetialsAndChannelNull_when_merchantIdIsNotNull_then_getNotValid() {
		assertFalse(new Validator()
			.isValid(new GetAccessTokenRequest()
				.setGrantType(GrantType.CLIENT_CREDENTIALS)
				.setChannel(null)
				.setAcquirerId(null)
				.setMerchantId("merchant_id")
				.setTerminalId(null)
				.setClientSecret("client_secret")
				.setRefreshToken(null)
				.setUsername(null)
				.setPassword(null)
				.setScope(null),
				null));
	}

	@Test
	void given_grantTypeClientCredetialsAndChannelNull_when_terminalIdIsNotNull_then_getNotValid() {
		assertFalse(new Validator()
			.isValid(new GetAccessTokenRequest()
				.setGrantType(GrantType.CLIENT_CREDENTIALS)
				.setChannel(null)
				.setAcquirerId(null)
				.setMerchantId(null)
				.setTerminalId("terminal_id")
				.setClientSecret("client_secret")
				.setRefreshToken(null)
				.setUsername(null)
				.setPassword(null)
				.setScope(null),
				null));
	}

	@Test
	void given_grantTypeClientCredetialsAndChannelNull_when_clientSecretIsNull_then_getNotValid() {
		assertFalse(new Validator()
			.isValid(new GetAccessTokenRequest()
				.setGrantType(GrantType.CLIENT_CREDENTIALS)
				.setChannel(null)
				.setAcquirerId(null)
				.setMerchantId(null)
				.setTerminalId(null)
				.setClientSecret(null)
				.setRefreshToken(null)
				.setUsername(null)
				.setPassword(null)
				.setScope(null),
				null));
	}

	@Test
	void given_grantTypeClientCredetialsAndChannelNull_when_refreshTokenIsNotNull_then_getNotValid() {
		assertFalse(new Validator()
			.isValid(new GetAccessTokenRequest()
				.setGrantType(GrantType.CLIENT_CREDENTIALS)
				.setChannel(null)
				.setAcquirerId(null)
				.setMerchantId(null)
				.setTerminalId(null)
				.setClientSecret("client_secret")
				.setRefreshToken(refreshToken)
				.setUsername(null)
				.setPassword(null)
				.setScope(null),
				null));
	}

	// @Test
	// void given_grantTypeClientCredetialsAndChannelNull_when_refreshCookieIsNotNull_then_getNotValid()
	// {
	// assertFalse(new Validator()
	// .isValid(new GetAccessTokenRequest()
	// .setGrantType(GrantType.CLIENT_CREDENTIALS)
	// .setChannel(null)
	// .setAcquirerId(null)
	// .setMerchantId(null)
	// .setTerminalId(null)
	// .setClientSecret("client_secret")
	// .setRefreshCookie(refreshToken)
	// .setUsername(null)
	// .setPassword(null)
	// .setScope(null),
	// null));
	// }

	@Test
	void given_grantTypeClientCredetialsAndChannelNull_when_usernameIsNotNull_then_getNotValid() {
		assertFalse(new Validator()
			.isValid(new GetAccessTokenRequest()
				.setGrantType(GrantType.CLIENT_CREDENTIALS)
				.setChannel(null)
				.setAcquirerId(null)
				.setMerchantId(null)
				.setTerminalId(null)
				.setClientSecret("client_secret")
				.setRefreshToken(null)
				.setUsername("username")
				.setPassword(null)
				.setScope(null),
				null));
	}

	@Test
	void given_grantTypeClientCredetialsAndChannelNull_when_passwordIsNotNull_then_getNotValid() {
		assertFalse(new Validator()
			.isValid(new GetAccessTokenRequest()
				.setGrantType(GrantType.CLIENT_CREDENTIALS)
				.setChannel(null)
				.setAcquirerId(null)
				.setMerchantId(null)
				.setTerminalId(null)
				.setClientSecret("client_secret")
				.setRefreshToken(null)
				.setUsername(null)
				.setPassword("password")
				.setScope(null),
				null));
	}

	@Test
	void given_grantTypeClientCredetialsAndChannelNull_when_scopeIsNotNull_then_getNotValid() {
		assertFalse(new Validator()
			.isValid(new GetAccessTokenRequest()
				.setGrantType(GrantType.CLIENT_CREDENTIALS)
				.setChannel(null)
				.setAcquirerId(null)
				.setMerchantId(null)
				.setTerminalId(null)
				.setClientSecret("client_secret")
				.setRefreshToken(null)
				.setUsername(null)
				.setPassword(null)
				.setScope("scope"),
				null));
	}

	/*
	 * GRANT TYPE = PASSWORD + CHANNEL = null
	 */
	@Test
	void given_grantTypePasswordAndChannelNull_when_allIsOk_then_getValid() {
		assertTrue(new Validator()
			.isValid(new GetAccessTokenRequest()
				.setGrantType(GrantType.PASSWORD)
				.setChannel(null)
				.setAcquirerId(null)
				.setMerchantId(null)
				.setTerminalId(null)
				.setClientSecret(null)
				.setRefreshToken(null)
				.setUsername("username")
				.setPassword("password"),
				null));
	}

	@Test
	void given_grantTypePasswordAndChannelNull_when_acquirerIdIsNotNull_then_getNotValid() {
		assertFalse(new Validator()
			.isValid(new GetAccessTokenRequest()
				.setGrantType(GrantType.PASSWORD)
				.setChannel(null)
				.setAcquirerId("acquirer_id")
				.setMerchantId(null)
				.setTerminalId(null)
				.setClientSecret(null)
				.setRefreshToken(null)
				.setUsername("username")
				.setPassword("password"),
				null));
	}

	@Test
	void given_grantTypePasswordAndChannelNull_when_merchantIdIsNotNull_then_getNotValid() {
		assertFalse(new Validator()
			.isValid(new GetAccessTokenRequest()
				.setGrantType(GrantType.PASSWORD)
				.setChannel(null)
				.setAcquirerId(null)
				.setMerchantId("merchant_id")
				.setTerminalId(null)
				.setClientSecret(null)
				.setRefreshToken(null)
				.setUsername("username")
				.setPassword("password"),
				null));
	}

	@Test
	void given_grantTypePasswordAndChannelNull_when_terminalIdIsNotNull_then_getNotValid() {
		assertFalse(new Validator()
			.isValid(new GetAccessTokenRequest()
				.setGrantType(GrantType.PASSWORD)
				.setChannel(null)
				.setAcquirerId(null)
				.setMerchantId(null)
				.setTerminalId("terminal_id")
				.setClientSecret(null)
				.setRefreshToken(null)
				.setUsername("username")
				.setPassword("password"),
				null));
	}

	@Test
	void given_grantTypePasswordAndChannelNull_when_clientSecretIsNotNull_then_getNotValid() {
		assertFalse(new Validator()
			.isValid(new GetAccessTokenRequest()
				.setGrantType(GrantType.PASSWORD)
				.setChannel(null)
				.setAcquirerId(null)
				.setMerchantId(null)
				.setTerminalId(null)
				.setClientSecret("client_secret")
				.setRefreshToken(null)
				.setUsername("username")
				.setPassword("password"),
				null));
	}

	@Test
	void given_grantTypePasswordAndChannelNull_when_refreshTokenIsNotNull_then_getNotValid() {
		assertFalse(new Validator()
			.isValid(new GetAccessTokenRequest()
				.setGrantType(GrantType.PASSWORD)
				.setChannel(null)
				.setAcquirerId(null)
				.setMerchantId(null)
				.setTerminalId(null)
				.setClientSecret(null)
				.setRefreshToken(refreshToken)
				.setUsername("username")
				.setPassword("password"),
				null));
	}

	@Test
	void given_grantTypePasswordAndChannelNull_when_usernameIsNull_then_getNotValid() {
		assertFalse(new Validator()
			.isValid(new GetAccessTokenRequest()
				.setGrantType(GrantType.PASSWORD)
				.setChannel(null)
				.setAcquirerId(null)
				.setMerchantId(null)
				.setTerminalId(null)
				.setClientSecret(null)
				.setRefreshToken(null)
				.setUsername(null)
				.setPassword("password"),
				null));
	}

	@Test
	void given_grantTypePasswordAndChannelNull_when_passwordIsNull_then_getNotValid() {
		assertFalse(new Validator()
			.isValid(new GetAccessTokenRequest()
				.setGrantType(GrantType.PASSWORD)
				.setChannel(null)
				.setAcquirerId(null)
				.setMerchantId(null)
				.setTerminalId(null)
				.setClientSecret(null)
				.setRefreshToken(null)
				.setUsername("username")
				.setPassword(null),
				null));
	}

	/*
	 * GRANT TYPE = REFRESH TOKEN + CHANNEL = null
	 */
	@Test
	void given_grantTypeRefreshTokenAndChannelNull_when_allIsOk_then_getValid() {
		assertTrue(new Validator()
			.isValid(new GetAccessTokenRequest()
				.setGrantType(GrantType.REFRESH_TOKEN)
				.setChannel(null)
				.setAcquirerId(null)
				.setMerchantId(null)
				.setTerminalId(null)
				.setClientSecret(null)
				.setRefreshToken(refreshToken)
				.setUsername(null)
				.setPassword(null)
				.setScope(null),
				null));
	}

	@Test
	void given_grantTypeRefreshTokenAndChannelNull_when_acquirerIdIsNotNull_then_getNotValid() {
		assertFalse(new Validator()
			.isValid(new GetAccessTokenRequest()
				.setGrantType(GrantType.REFRESH_TOKEN)
				.setChannel(null)
				.setAcquirerId("acquirer_id")
				.setMerchantId(null)
				.setTerminalId(null)
				.setClientSecret(null)
				.setRefreshToken(refreshToken)
				.setUsername(null)
				.setPassword(null)
				.setScope(null),
				null));
	}

	@Test
	void given_grantTypeRefreshTokenAndChannelNull_when_merchsntIdIsNotNull_then_getNotValid() {
		assertFalse(new Validator()
			.isValid(new GetAccessTokenRequest()
				.setGrantType(GrantType.REFRESH_TOKEN)
				.setChannel(null)
				.setAcquirerId(null)
				.setMerchantId("merchant_id")
				.setTerminalId(null)
				.setClientSecret(null)
				.setRefreshToken(refreshToken)
				.setUsername(null)
				.setPassword(null)
				.setScope(null),
				null));
	}

	@Test
	void given_grantTypeRefreshTokenAndChannelNull_when_terminalIdIsNotNull_then_getNotValid() {
		assertFalse(new Validator()
			.isValid(new GetAccessTokenRequest()
				.setGrantType(GrantType.REFRESH_TOKEN)
				.setChannel(null)
				.setAcquirerId(null)
				.setMerchantId(null)
				.setTerminalId("terminal_id")
				.setClientSecret(null)
				.setRefreshToken(refreshToken)
				.setUsername(null)
				.setPassword(null)
				.setScope(null),
				null));
	}

	@Test
	void given_grantTypeRefreshTokenAndChannelNull_when_clientSecretIsNotNull_then_getNotValid() {
		assertFalse(new Validator()
			.isValid(new GetAccessTokenRequest()
				.setGrantType(GrantType.REFRESH_TOKEN)
				.setChannel(null)
				.setAcquirerId(null)
				.setMerchantId(null)
				.setTerminalId(null)
				.setClientSecret("client_secret")
				.setRefreshToken(refreshToken)
				.setUsername(null)
				.setPassword(null)
				.setScope(null),
				null));
	}

	@Test
	void given_grantTypeRefreshTokenAndChannelNull_when_usernamerIsNotNull_then_getNotValid() {
		assertFalse(new Validator()
			.isValid(new GetAccessTokenRequest()
				.setGrantType(GrantType.REFRESH_TOKEN)
				.setChannel(null)
				.setAcquirerId(null)
				.setMerchantId(null)
				.setTerminalId(null)
				.setClientSecret(null)
				.setRefreshToken(refreshToken)
				.setUsername("username")
				.setPassword(null)
				.setScope(null),
				null));
	}

	@Test
	void given_grantTypeRefreshTokenAndChannelNull_when_passwordNotNull_then_getNotValid() {
		assertFalse(new Validator()
			.isValid(new GetAccessTokenRequest()
				.setGrantType(GrantType.REFRESH_TOKEN)
				.setChannel(null)
				.setAcquirerId(null)
				.setMerchantId(null)
				.setTerminalId(null)
				.setClientSecret(null)
				.setRefreshToken(refreshToken)
				.setUsername(null)
				.setPassword("password")
				.setScope(null),
				null));
	}

	@Test
	void given_grantTypeRefreshTokenAndChannelNull_when_refreshTokenIsNull_then_getNotValid() {
		assertFalse(new Validator()
			.isValid(new GetAccessTokenRequest()
				.setGrantType(GrantType.REFRESH_TOKEN)
				.setChannel(null)
				.setAcquirerId(null)
				.setMerchantId(null)
				.setTerminalId(null)
				.setClientSecret(null)
				.setRefreshToken(null)
				.setUsername(null)
				.setPassword(null)
				.setScope(null),
				null));
	}

	@Test
	void given_grantTypeRefreshTokenAndChannelNull_when_scopeIsNotNull_then_getNotValid() {
		assertFalse(new Validator()
			.isValid(new GetAccessTokenRequest()
				.setGrantType(GrantType.REFRESH_TOKEN)
				.setChannel(null)
				.setAcquirerId(null)
				.setMerchantId(null)
				.setTerminalId(null)
				.setClientSecret(null)
				.setRefreshToken(refreshToken)
				.setUsername(null)
				.setPassword(null)
				.setScope("scope"),
				null));
	}

	/*
	 * OTHER GRANT TYPE + CHANNEL
	 */
	@Test
	void given_grantTypeOther_when_invokeValidator_then_getNotValid() {
		assertFalse(new Validator()
			.isValid(new GetAccessTokenRequest()
				.setGrantType("other"),
				null));
	}
}
