/*
 * Validator.java
 *
 * 28 mar 2023
 */
package it.pagopa.swclient.mil.auth.validation.constraints;

import static it.pagopa.swclient.mil.auth.bean.GrantType.CLIENT_CREDENTIALS;
import static it.pagopa.swclient.mil.auth.bean.GrantType.PASSWORD;
import static it.pagopa.swclient.mil.auth.bean.GrantType.POYNT_TOKEN;
import static it.pagopa.swclient.mil.auth.bean.GrantType.REFRESH_TOKEN;
import static it.pagopa.swclient.mil.bean.Channel.ATM;
import static it.pagopa.swclient.mil.bean.Channel.POS;

import java.util.HashMap;
import java.util.Map;

import io.quarkus.logging.Log;
import it.pagopa.swclient.mil.auth.bean.GetAccessToken;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * 
 * @author Antonio Tarricone
 */
public class Validator implements ConstraintValidator<ValidationTarget, GetAccessToken> {
	/*
	 * 
	 */
	private static final Map<String, Verifier> VALIDATOR = new HashMap<>();
	static {
		VALIDATOR.put(PASSWORD + "/" + POS, new Verifier() {
			@Override
			public boolean test(GetAccessToken getAccessToken) {
				return acquirerIdMustNotBeNull(getAccessToken)
					&& merchantIdMustNotBeNull(getAccessToken)
					&& terminalIdMustNotBeNull(getAccessToken)
					&& clientSecretMustBeNull(getAccessToken)
					&& extTokenMustBeNull(getAccessToken)
					&& addDataMustBeNull(getAccessToken)
					&& refreshTokenMustBeNull(getAccessToken)
					&& usernameMustNotBeNull(getAccessToken)
					&& passwordMustNotBeNull(getAccessToken);
			}
		});

		VALIDATOR.put(REFRESH_TOKEN + "/" + POS, new Verifier() {
			@Override
			public boolean test(GetAccessToken getAccessToken) {
				return acquirerIdMustNotBeNull(getAccessToken)
					&& merchantIdMustNotBeNull(getAccessToken)
					&& terminalIdMustNotBeNull(getAccessToken)
					&& clientSecretMustBeNull(getAccessToken)
					&& extTokenMustBeNull(getAccessToken)
					&& addDataMustBeNull(getAccessToken)
					&& refreshTokenMustNotBeNull(getAccessToken)
					&& usernameMustBeNull(getAccessToken)
					&& passwordMustBeNull(getAccessToken)
					&& scopedMustBeNull(getAccessToken);
			}

		});

		VALIDATOR.put(POYNT_TOKEN + "/" + POS, new Verifier() {
			@Override
			public boolean test(GetAccessToken getAccessToken) {
				return acquirerIdMustNotBeNull(getAccessToken)
					&& merchantIdMustNotBeNull(getAccessToken)
					&& terminalIdMustNotBeNull(getAccessToken)
					&& clientSecretMustBeNull(getAccessToken)
					&& extTokenMustNotBeNull(getAccessToken)
					&& addDataMustNotBeNull(getAccessToken)
					&& refreshTokenMustBeNull(getAccessToken)
					&& usernameMustBeNull(getAccessToken)
					&& passwordMustBeNull(getAccessToken);
			}
		});

		VALIDATOR.put(CLIENT_CREDENTIALS + "/" + ATM, new Verifier() {
			@Override
			public boolean test(GetAccessToken getAccessToken) {
				return acquirerIdMustNotBeNull(getAccessToken)
					&& merchantIdMustBeNull(getAccessToken)
					&& terminalIdMustNotBeNull(getAccessToken)
					&& clientSecretMustNotBeNull(getAccessToken)
					&& extTokenMustBeNull(getAccessToken)
					&& addDataMustBeNull(getAccessToken)
					&& refreshTokenMustBeNull(getAccessToken)
					&& usernameMustBeNull(getAccessToken)
					&& passwordMustBeNull(getAccessToken)
					&& scopedMustBeNull(getAccessToken);
			}
		});

		VALIDATOR.put(CLIENT_CREDENTIALS + "/" + POS, new Verifier() {
			@Override
			public boolean test(GetAccessToken getAccessToken) {
				return acquirerIdMustNotBeNull(getAccessToken)
					&& merchantIdMustNotBeNull(getAccessToken)
					&& terminalIdMustNotBeNull(getAccessToken)
					&& clientSecretMustNotBeNull(getAccessToken)
					&& extTokenMustBeNull(getAccessToken)
					&& addDataMustBeNull(getAccessToken)
					&& refreshTokenMustBeNull(getAccessToken)
					&& usernameMustBeNull(getAccessToken)
					&& passwordMustBeNull(getAccessToken)
					&& scopedMustBeNull(getAccessToken);
			}
		});

		VALIDATOR.put(CLIENT_CREDENTIALS + "/null", new Verifier() {
			@Override
			public boolean test(GetAccessToken getAccessToken) {
				return acquirerIdMustBeNull(getAccessToken)
					&& merchantIdMustBeNull(getAccessToken)
					&& terminalIdMustBeNull(getAccessToken)
					&& clientSecretMustNotBeNull(getAccessToken)
					&& extTokenMustBeNull(getAccessToken)
					&& addDataMustBeNull(getAccessToken)
					&& refreshTokenMustBeNull(getAccessToken)
					&& usernameMustBeNull(getAccessToken)
					&& passwordMustBeNull(getAccessToken)
					&& scopedMustBeNull(getAccessToken);
			}
		});
	}

	/**
	 * @see jakarta.validation.ConstraintValidator#isValid(Object, ConstraintValidatorContext)
	 */
	@Override
	public boolean isValid(GetAccessToken getAccessToken, ConstraintValidatorContext context) {
		return VALIDATOR.getOrDefault(getAccessToken.getGrantType() + "/" + getAccessToken.getChannel(), new Verifier() {
			@Override
			public boolean test(GetAccessToken t) {
				Log.warn("Default validator in use.");
				return false;
			}
		}).test(getAccessToken);
	}
}