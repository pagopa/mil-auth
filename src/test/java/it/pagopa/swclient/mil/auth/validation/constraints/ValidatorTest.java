/*
 * ValidatorTest.java
 *
 * 10 giu 2024
 */
package it.pagopa.swclient.mil.auth.validation.constraints;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

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
				.setExtToken(null)
				.setAddData(null)
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
				.setExtToken(null)
				.setAddData(null)
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
				.setExtToken(null)
				.setAddData(null)
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
				.setExtToken(null)
				.setAddData(null)
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
				.setExtToken(null)
				.setAddData(null)
				.setRefreshToken(null)
				.setUsername("username")
				.setPassword("password"),
				null));
	}

	@Test
	void given_grantTypePasswordAndChannelPos_when_extTokenIsNotNull_then_getNotValid() {
		assertFalse(new Validator()
			.isValid(new GetAccessTokenRequest()
				.setGrantType(GrantType.PASSWORD)
				.setChannel(Channel.POS)
				.setAcquirerId("acquirer_id")
				.setMerchantId("merchant_id")
				.setTerminalId("terminal_id")
				.setClientSecret(null)
				.setExtToken("ext_token")
				.setAddData(null)
				.setRefreshToken(null)
				.setUsername("username")
				.setPassword("password"),
				null));
	}

	@Test
	void given_grantTypePasswordAndChannelPos_when_addDataIsNotNull_then_getNotValid() {
		assertFalse(new Validator()
			.isValid(new GetAccessTokenRequest()
				.setGrantType(GrantType.PASSWORD)
				.setChannel(Channel.POS)
				.setAcquirerId("acquirer_id")
				.setMerchantId("merchant_id")
				.setTerminalId("terminal_id")
				.setClientSecret(null)
				.setExtToken(null)
				.setAddData("add_data")
				.setRefreshToken(null)
				.setUsername("username")
				.setPassword("password"),
				null));
	}

	@Test
	void given_grantTypePasswordAndChannelPos_when_refreshTokenIsNotNull_then_getNotValid() {
		assertFalse(new Validator()
			.isValid(new GetAccessTokenRequest()
				.setGrantType(GrantType.PASSWORD)
				.setChannel(Channel.POS)
				.setAcquirerId("acquirer_id")
				.setMerchantId("merchant_id")
				.setTerminalId("terminal_id")
				.setClientSecret(null)
				.setExtToken(null)
				.setAddData(null)
				.setRefreshToken("refresh_token")
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
				.setExtToken(null)
				.setAddData(null)
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
				.setExtToken(null)
				.setAddData(null)
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
				.setExtToken(null)
				.setAddData(null)
				.setRefreshToken("refresh_token")
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
				.setExtToken(null)
				.setAddData(null)
				.setRefreshToken("refresh_token")
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
				.setExtToken(null)
				.setAddData(null)
				.setRefreshToken("refresh_token")
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
				.setExtToken(null)
				.setAddData(null)
				.setRefreshToken("refresh_token")
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
				.setExtToken(null)
				.setAddData(null)
				.setRefreshToken("refresh_token")
				.setUsername(null)
				.setPassword(null)
				.setScope(null),
				null));
	}

	@Test
	void given_grantTypeRefreshTokenAndChannelPos_when_extTokenIsNotNull_then_getNotValid() {
		assertFalse(new Validator()
			.isValid(new GetAccessTokenRequest()
				.setGrantType(GrantType.REFRESH_TOKEN)
				.setChannel(Channel.POS)
				.setAcquirerId("acquirer_id")
				.setMerchantId("merchant_id")
				.setTerminalId("terminal_id")
				.setClientSecret(null)
				.setExtToken("ext_token")
				.setAddData(null)
				.setRefreshToken("refresh_token")
				.setUsername(null)
				.setPassword(null)
				.setScope(null),
				null));
	}

	@Test
	void given_grantTypeRefreshTokenAndChannelPos_when_addDataIsNotNull_then_getNotValid() {
		assertFalse(new Validator()
			.isValid(new GetAccessTokenRequest()
				.setGrantType(GrantType.REFRESH_TOKEN)
				.setChannel(Channel.POS)
				.setAcquirerId("acquirer_id")
				.setMerchantId("merchant_id")
				.setTerminalId("terminal_id")
				.setClientSecret(null)
				.setExtToken(null)
				.setAddData("add_data")
				.setRefreshToken("refresh_token")
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
				.setExtToken(null)
				.setAddData(null)
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
				.setExtToken(null)
				.setAddData(null)
				.setRefreshToken("refresh_token")
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
				.setExtToken(null)
				.setAddData(null)
				.setRefreshToken("refresh_token")
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
				.setExtToken(null)
				.setAddData(null)
				.setRefreshToken("refresh_token")
				.setUsername(null)
				.setPassword(null)
				.setScope("scope"),
				null));
	}

	/*
	 * GRANT TYPE = POYNT TOKEN + CHANNEL = POS
	 */
	@Test
	void given_grantTypePoyntTokenAndChannelPos_when_allIsOk_then_getValid() {
		assertTrue(new Validator()
			.isValid(new GetAccessTokenRequest()
				.setGrantType(GrantType.POYNT_TOKEN)
				.setChannel(Channel.POS)
				.setAcquirerId("acquirer_id")
				.setMerchantId("merchant_id")
				.setTerminalId("terminal_id")
				.setClientSecret(null)
				.setExtToken("ext_token")
				.setAddData("add_data")
				.setRefreshToken(null)
				.setUsername(null)
				.setPassword(null),
				null));
	}

	@Test
	void given_grantTypePoyntTokenAndChannelPos_when_acquirerIdIsNull_then_getNotValid() {
		assertFalse(new Validator()
			.isValid(new GetAccessTokenRequest()
				.setGrantType(GrantType.POYNT_TOKEN)
				.setChannel(Channel.POS)
				.setAcquirerId(null)
				.setMerchantId("merchant_id")
				.setTerminalId("terminal_id")
				.setClientSecret(null)
				.setExtToken("ext_token")
				.setAddData("add_data")
				.setRefreshToken(null)
				.setUsername(null)
				.setPassword(null),
				null));
	}

	@Test
	void given_grantTypePoyntTokenAndChannelPos_when_merchantIdIsNull_then_getNotValid() {
		assertFalse(new Validator()
			.isValid(new GetAccessTokenRequest()
				.setGrantType(GrantType.POYNT_TOKEN)
				.setChannel(Channel.POS)
				.setAcquirerId("acquirer_id")
				.setMerchantId(null)
				.setTerminalId("terminal_id")
				.setClientSecret(null)
				.setExtToken("ext_token")
				.setAddData("add_data")
				.setRefreshToken(null)
				.setUsername(null)
				.setPassword(null),
				null));
	}

	@Test
	void given_grantTypePoyntTokenAndChannelPos_when_terminalIdIsNull_then_getNotValid() {
		assertFalse(new Validator()
			.isValid(new GetAccessTokenRequest()
				.setGrantType(GrantType.POYNT_TOKEN)
				.setChannel(Channel.POS)
				.setAcquirerId("acquirer_id")
				.setMerchantId("merchant_id")
				.setTerminalId(null)
				.setClientSecret(null)
				.setExtToken("ext_token")
				.setAddData("add_data")
				.setRefreshToken(null)
				.setUsername(null)
				.setPassword(null),
				null));
	}

	@Test
	void given_grantTypePoyntTokenAndChannelPos_when_clientSecretIsNotNull_then_getNotValid() {
		assertFalse(new Validator()
			.isValid(new GetAccessTokenRequest()
				.setGrantType(GrantType.POYNT_TOKEN)
				.setChannel(Channel.POS)
				.setAcquirerId("acquirer_id")
				.setMerchantId("merchant_id")
				.setTerminalId("terminal_id")
				.setClientSecret("client_secret")
				.setExtToken("ext_token")
				.setAddData("add_data")
				.setRefreshToken(null)
				.setUsername(null)
				.setPassword(null),
				null));
	}

	@Test
	void given_grantTypePoyntTokenAndChannelPos_when_extTokenIsNull_then_getNotValid() {
		assertFalse(new Validator()
			.isValid(new GetAccessTokenRequest()
				.setGrantType(GrantType.POYNT_TOKEN)
				.setChannel(Channel.POS)
				.setAcquirerId("acquirer_id")
				.setMerchantId("merchant_id")
				.setTerminalId("terminal_id")
				.setClientSecret(null)
				.setExtToken(null)
				.setAddData("add_data")
				.setRefreshToken(null)
				.setUsername(null)
				.setPassword(null),
				null));
	}

	@Test
	void given_grantTypePoyntTokenAndChannelPos_when_addDataIsNull_then_getNotValid() {
		assertFalse(new Validator()
			.isValid(new GetAccessTokenRequest()
				.setGrantType(GrantType.POYNT_TOKEN)
				.setChannel(Channel.POS)
				.setAcquirerId("acquirer_id")
				.setMerchantId("merchant_id")
				.setTerminalId("terminal_id")
				.setClientSecret(null)
				.setExtToken("ext_token")
				.setAddData(null)
				.setRefreshToken(null)
				.setUsername(null)
				.setPassword(null),
				null));
	}

	@Test
	void given_grantTypePoyntTokenAndChannelPos_when_refreshTokenIsNotNull_then_getNotValid() {
		assertFalse(new Validator()
			.isValid(new GetAccessTokenRequest()
				.setGrantType(GrantType.POYNT_TOKEN)
				.setChannel(Channel.POS)
				.setAcquirerId("acquirer_id")
				.setMerchantId("merchant_id")
				.setTerminalId("terminal_id")
				.setClientSecret(null)
				.setExtToken("ext_token")
				.setAddData("add_data")
				.setRefreshToken("refresh_token")
				.setUsername(null)
				.setPassword(null),
				null));
	}

	@Test
	void given_grantTypePoyntTokenAndChannelPos_when_usernameIsNotNull_then_getNotValid() {
		assertFalse(new Validator()
			.isValid(new GetAccessTokenRequest()
				.setGrantType(GrantType.POYNT_TOKEN)
				.setChannel(Channel.POS)
				.setAcquirerId("acquirer_id")
				.setMerchantId("merchant_id")
				.setTerminalId("terminal_id")
				.setClientSecret(null)
				.setExtToken("ext_token")
				.setAddData("add_data")
				.setRefreshToken(null)
				.setUsername("username")
				.setPassword(null),
				null));
	}

	@Test
	void given_grantTypePoyntTokenAndChannelPos_when_passwordIsNotNull_then_getNotValid() {
		assertFalse(new Validator()
			.isValid(new GetAccessTokenRequest()
				.setGrantType(GrantType.POYNT_TOKEN)
				.setChannel(Channel.POS)
				.setAcquirerId("acquirer_id")
				.setMerchantId("merchant_id")
				.setTerminalId("terminal_id")
				.setClientSecret(null)
				.setExtToken("ext_token")
				.setAddData("add_data")
				.setRefreshToken(null)
				.setUsername(null)
				.setPassword("password"),
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
				.setExtToken(null)
				.setAddData(null)
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
				.setExtToken(null)
				.setAddData(null)
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
				.setExtToken(null)
				.setAddData(null)
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
				.setExtToken(null)
				.setAddData(null)
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
				.setExtToken(null)
				.setAddData(null)
				.setRefreshToken(null)
				.setUsername(null)
				.setPassword(null)
				.setScope(null),
				null));
	}

	@Test
	void given_grantTypeClientCredetialsAndChannelAtm_when_extTokenIsNotNull_then_getNotValid() {
		assertFalse(new Validator()
			.isValid(new GetAccessTokenRequest()
				.setGrantType(GrantType.CLIENT_CREDENTIALS)
				.setChannel(Channel.ATM)
				.setAcquirerId("acquirer_id")
				.setMerchantId(null)
				.setTerminalId("terminal_id")
				.setClientSecret("client_secret")
				.setExtToken("ext_token")
				.setAddData(null)
				.setRefreshToken(null)
				.setUsername(null)
				.setPassword(null)
				.setScope(null),
				null));
	}

	@Test
	void given_grantTypeClientCredetialsAndChannelAtm_when_addDataIsNotNull_then_getNotValid() {
		assertFalse(new Validator()
			.isValid(new GetAccessTokenRequest()
				.setGrantType(GrantType.CLIENT_CREDENTIALS)
				.setChannel(Channel.ATM)
				.setAcquirerId("acquirer_id")
				.setMerchantId(null)
				.setTerminalId("terminal_id")
				.setClientSecret("client_secret")
				.setExtToken(null)
				.setAddData("add_data")
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
				.setExtToken(null)
				.setAddData(null)
				.setRefreshToken("refresh_token")
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
				.setExtToken(null)
				.setAddData(null)
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
				.setExtToken(null)
				.setAddData(null)
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
				.setExtToken(null)
				.setAddData(null)
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
				.setExtToken(null)
				.setAddData(null)
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
				.setExtToken(null)
				.setAddData(null)
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
				.setExtToken(null)
				.setAddData(null)
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
				.setExtToken(null)
				.setAddData(null)
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
				.setExtToken(null)
				.setAddData(null)
				.setRefreshToken(null)
				.setUsername(null)
				.setPassword(null)
				.setScope(null),
				null));
	}

	@Test
	void given_grantTypeClientCredetialsAndChannelPos_when_extTokenIsNotNull_then_getNotValid() {
		assertFalse(new Validator()
			.isValid(new GetAccessTokenRequest()
				.setGrantType(GrantType.CLIENT_CREDENTIALS)
				.setChannel(Channel.POS)
				.setAcquirerId("acquirer_id")
				.setMerchantId("merchant_id")
				.setTerminalId("terminal_id")
				.setClientSecret("client_secret")
				.setExtToken("ext_token")
				.setAddData(null)
				.setRefreshToken(null)
				.setUsername(null)
				.setPassword(null)
				.setScope(null),
				null));
	}

	@Test
	void given_grantTypeClientCredetialsAndChannelPos_when_addDataIsNotNull_then_getNotValid() {
		assertFalse(new Validator()
			.isValid(new GetAccessTokenRequest()
				.setGrantType(GrantType.CLIENT_CREDENTIALS)
				.setChannel(Channel.POS)
				.setAcquirerId("acquirer_id")
				.setMerchantId("merchant_id")
				.setTerminalId("terminal_id")
				.setClientSecret("client_secret")
				.setExtToken(null)
				.setAddData("add_data")
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
				.setExtToken(null)
				.setAddData(null)
				.setRefreshToken("refresh_token")
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
				.setExtToken(null)
				.setAddData(null)
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
				.setExtToken(null)
				.setAddData(null)
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
				.setExtToken(null)
				.setAddData(null)
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
				.setExtToken(null)
				.setAddData(null)
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
				.setExtToken(null)
				.setAddData(null)
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
				.setExtToken(null)
				.setAddData(null)
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
				.setExtToken(null)
				.setAddData(null)
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
				.setExtToken(null)
				.setAddData(null)
				.setRefreshToken(null)
				.setUsername(null)
				.setPassword(null)
				.setScope(null),
				null));
	}

	@Test
	void given_grantTypeClientCredetialsAndChannelNull_when_extTokenIsNotNull_then_getNotValid() {
		assertFalse(new Validator()
			.isValid(new GetAccessTokenRequest()
				.setGrantType(GrantType.CLIENT_CREDENTIALS)
				.setChannel(null)
				.setAcquirerId(null)
				.setMerchantId(null)
				.setTerminalId(null)
				.setClientSecret("client_secret")
				.setExtToken("ext_token")
				.setAddData(null)
				.setRefreshToken(null)
				.setUsername(null)
				.setPassword(null)
				.setScope(null),
				null));
	}

	@Test
	void given_grantTypeClientCredetialsAndChannelNull_when_addDataIsNotNull_then_getNotValid() {
		assertFalse(new Validator()
			.isValid(new GetAccessTokenRequest()
				.setGrantType(GrantType.CLIENT_CREDENTIALS)
				.setChannel(null)
				.setAcquirerId(null)
				.setMerchantId(null)
				.setTerminalId(null)
				.setClientSecret("client_secret")
				.setExtToken(null)
				.setAddData("add_data")
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
				.setExtToken(null)
				.setAddData(null)
				.setRefreshToken("refresh_token")
				.setUsername(null)
				.setPassword(null)
				.setScope(null),
				null));
	}

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
				.setExtToken(null)
				.setAddData(null)
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
				.setExtToken(null)
				.setAddData(null)
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
				.setExtToken(null)
				.setAddData(null)
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
				.setExtToken(null)
				.setAddData(null)
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
				.setExtToken(null)
				.setAddData(null)
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
				.setExtToken(null)
				.setAddData(null)
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
				.setExtToken(null)
				.setAddData(null)
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
				.setExtToken(null)
				.setAddData(null)
				.setRefreshToken(null)
				.setUsername("username")
				.setPassword("password"),
				null));
	}

	@Test
	void given_grantTypePasswordAndChannelNull_when_extTokentIsNotNull_then_getNotValid() {
		assertFalse(new Validator()
			.isValid(new GetAccessTokenRequest()
				.setGrantType(GrantType.PASSWORD)
				.setChannel(null)
				.setAcquirerId(null)
				.setMerchantId(null)
				.setTerminalId(null)
				.setClientSecret(null)
				.setExtToken("ext_token")
				.setAddData(null)
				.setRefreshToken(null)
				.setUsername("username")
				.setPassword("password"),
				null));
	}

	@Test
	void given_grantTypePasswordAndChannelNull_when_addDataIsNotNull_then_getNotValid() {
		assertFalse(new Validator()
			.isValid(new GetAccessTokenRequest()
				.setGrantType(GrantType.PASSWORD)
				.setChannel(null)
				.setAcquirerId(null)
				.setMerchantId(null)
				.setTerminalId(null)
				.setClientSecret(null)
				.setExtToken(null)
				.setAddData("add_data")
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
				.setExtToken(null)
				.setAddData(null)
				.setRefreshToken("refresh_token")
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
				.setExtToken(null)
				.setAddData(null)
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
				.setExtToken(null)
				.setAddData(null)
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
				.setExtToken(null)
				.setAddData(null)
				.setRefreshToken("refresh_token")
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
				.setExtToken(null)
				.setAddData(null)
				.setRefreshToken("refresh_token")
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
				.setExtToken(null)
				.setAddData(null)
				.setRefreshToken("refresh_token")
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
				.setExtToken(null)
				.setAddData(null)
				.setRefreshToken("refresh_token")
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
				.setExtToken(null)
				.setAddData(null)
				.setRefreshToken("refresh_token")
				.setUsername(null)
				.setPassword(null)
				.setScope(null),
				null));
	}

	@Test
	void given_grantTypeRefreshTokenAndChannelNull_when_extTokenIsNotNull_then_getNotValid() {
		assertFalse(new Validator()
			.isValid(new GetAccessTokenRequest()
				.setGrantType(GrantType.REFRESH_TOKEN)
				.setChannel(null)
				.setAcquirerId(null)
				.setMerchantId(null)
				.setTerminalId(null)
				.setClientSecret(null)
				.setExtToken("ext_token")
				.setAddData(null)
				.setRefreshToken("refresh_token")
				.setUsername(null)
				.setPassword(null)
				.setScope(null),
				null));
	}

	@Test
	void given_grantTypeRefreshTokenAndChannelNull_when_addDatasNotNull_then_getNotValid() {
		assertFalse(new Validator()
			.isValid(new GetAccessTokenRequest()
				.setGrantType(GrantType.REFRESH_TOKEN)
				.setChannel(null)
				.setAcquirerId(null)
				.setMerchantId(null)
				.setTerminalId(null)
				.setClientSecret(null)
				.setExtToken(null)
				.setAddData("add_data")
				.setRefreshToken("refresh_token")
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
				.setExtToken(null)
				.setAddData(null)
				.setRefreshToken("refresh_token")
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
				.setExtToken(null)
				.setAddData(null)
				.setRefreshToken("refresh_token")
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
				.setExtToken(null)
				.setAddData(null)
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
				.setExtToken(null)
				.setAddData(null)
				.setRefreshToken("refresh_token")
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
