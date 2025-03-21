/*
 * Validator.java
 *
 * 28 mar 2023
 */
package it.pagopa.swclient.mil.auth.validation.constraints;

import java.util.HashMap;
import java.util.Map;

import io.quarkus.logging.Log;
import it.pagopa.swclient.mil.auth.bean.GetAccessTokenRequest;
import it.pagopa.swclient.mil.auth.bean.GrantType;
import it.pagopa.swclient.mil.bean.Channel;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * @author Antonio Tarricone
 */
public class Validator implements ConstraintValidator<ValidationTarget, GetAccessTokenRequest> {
	/*
	 *
	 */
	private static final Map<String, Verifier> VALIDATORS = new HashMap<>();

	static {
		VALIDATORS.put(GrantType.PASSWORD + "/" + Channel.POS, new Verifier() {
			@Override
			public boolean test(GetAccessTokenRequest getAccessToken) {
				return acquirerIdMustNotBeNull(getAccessToken)
					&& merchantIdMustNotBeNull(getAccessToken)
					&& terminalIdMustNotBeNull(getAccessToken)
					&& clientSecretMustBeNull(getAccessToken)
					&& refreshTokenMustBeNull(getAccessToken)
					&& usernameMustNotBeNull(getAccessToken)
					&& passwordMustNotBeNull(getAccessToken);
			}
		});

		VALIDATORS.put(GrantType.PASSWORD + "/null", new Verifier() {
			@Override
			public boolean test(GetAccessTokenRequest getAccessToken) {
				return acquirerIdMustBeNull(getAccessToken)
					&& merchantIdMustBeNull(getAccessToken)
					&& terminalIdMustBeNull(getAccessToken)
					&& clientSecretMustBeNull(getAccessToken)
					&& refreshTokenMustBeNull(getAccessToken)
					&& usernameMustNotBeNull(getAccessToken)
					&& passwordMustNotBeNull(getAccessToken);
			}
		});

		VALIDATORS.put(GrantType.REFRESH_TOKEN + "/" + Channel.POS, new Verifier() {
			@Override
			public boolean test(GetAccessTokenRequest getAccessToken) {
				return acquirerIdMustNotBeNull(getAccessToken)
					&& merchantIdMustNotBeNull(getAccessToken)
					&& terminalIdMustNotBeNull(getAccessToken)
					&& clientSecretMustBeNull(getAccessToken)
					&& oneOfRefreshTokenAndRefreshCookieMustNotBeNull(getAccessToken)
					&& usernameMustBeNull(getAccessToken)
					&& passwordMustBeNull(getAccessToken)
					&& scopeMustBeNull(getAccessToken);
			}
		});

		VALIDATORS.put(GrantType.REFRESH_TOKEN + "/null", new Verifier() {
			@Override
			public boolean test(GetAccessTokenRequest getAccessToken) {
				return acquirerIdMustBeNull(getAccessToken)
					&& merchantIdMustBeNull(getAccessToken)
					&& terminalIdMustBeNull(getAccessToken)
					&& clientSecretMustBeNull(getAccessToken)
					&& oneOfRefreshTokenAndRefreshCookieMustNotBeNull(getAccessToken)
					&& usernameMustBeNull(getAccessToken)
					&& passwordMustBeNull(getAccessToken)
					&& scopeMustBeNull(getAccessToken);
			}
		});

		VALIDATORS.put(GrantType.CLIENT_CREDENTIALS + "/" + Channel.ATM, new Verifier() {
			@Override
			public boolean test(GetAccessTokenRequest getAccessToken) {
				return acquirerIdMustNotBeNull(getAccessToken)
					&& merchantIdMustBeNull(getAccessToken)
					&& terminalIdMustNotBeNull(getAccessToken)
					&& clientSecretMustNotBeNull(getAccessToken)
					&& refreshTokenMustBeNull(getAccessToken)
					&& usernameMustBeNull(getAccessToken)
					&& passwordMustBeNull(getAccessToken)
					&& scopeMustBeNull(getAccessToken);
			}
		});

		VALIDATORS.put(GrantType.CLIENT_CREDENTIALS + "/" + Channel.POS, new Verifier() {
			@Override
			public boolean test(GetAccessTokenRequest getAccessToken) {
				return acquirerIdMustNotBeNull(getAccessToken)
					&& merchantIdMustNotBeNull(getAccessToken)
					&& terminalIdMustNotBeNull(getAccessToken)
					&& clientSecretMustNotBeNull(getAccessToken)
					&& refreshTokenMustBeNull(getAccessToken)
					&& usernameMustBeNull(getAccessToken)
					&& passwordMustBeNull(getAccessToken)
					&& scopeMustBeNull(getAccessToken);
			}
		});

		VALIDATORS.put(GrantType.CLIENT_CREDENTIALS + "/null", new Verifier() {
			@Override
			public boolean test(GetAccessTokenRequest getAccessToken) {
				return acquirerIdMustBeNull(getAccessToken)
					&& merchantIdMustBeNull(getAccessToken)
					&& terminalIdMustBeNull(getAccessToken)
					&& clientSecretMustNotBeNull(getAccessToken)
					&& refreshTokenMustBeNull(getAccessToken)
					&& usernameMustBeNull(getAccessToken)
					&& passwordMustBeNull(getAccessToken)
					&& scopeMustBeNull(getAccessToken);
			}
		});
	}

	/**
	 * @see jakarta.validation.ConstraintValidator#isValid(Object, ConstraintValidatorContext)
	 */
	@Override
	public boolean isValid(GetAccessTokenRequest getAccessToken, ConstraintValidatorContext context) {
		Log.tracef("Get validator for %s/%s", getAccessToken.getGrantType(), getAccessToken.getChannel());
		return VALIDATORS.getOrDefault(getAccessToken.getGrantType() + "/" + getAccessToken.getChannel(), new Verifier() {
			@Override
			public boolean test(GetAccessTokenRequest t) {
				Log.warn("Default validator in use.");
				return false;
			}
		}).test(getAccessToken);
	}
}