/*
 * Validator.java
 *
 * 28 mar 2023
 */
package it.pagopa.swclient.mil.auth.validation.constraints;

import java.util.EnumMap;
import java.util.Map;

import io.quarkus.logging.Log;
import it.pagopa.swclient.mil.auth.bean.AccessTokenRequest;
import it.pagopa.swclient.mil.auth.bean.GrantType;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * 
 * @author Antonio Tarricone
 */
public class Validator implements ConstraintValidator<ValidationTarget, AccessTokenRequest> {
	/*
	 *
	 */
	private static final Map<GrantType, Verifier> VALIDATORS = new EnumMap<>(GrantType.class);

	static {
		VALIDATORS.put(GrantType.REFRESH_TOKEN, new Verifier() {
			@Override
			public boolean test(AccessTokenRequest accessTokenRequest) {
				return bankIdMustBeNull(accessTokenRequest)
					&& clientSecretMustBeNull(accessTokenRequest)
					&& deviceCodeMustBeNull(accessTokenRequest)
					&& refreshTokenMustNotBeNull(accessTokenRequest)
					&& terminalIdMustBeNull(accessTokenRequest);
			}

		});

		VALIDATORS.put(GrantType.DEVICE_CODE, new Verifier() {
			@Override
			public boolean test(AccessTokenRequest accessTokenRequest) {
				return bankIdMustBeNull(accessTokenRequest)
					&& clientSecretMustBeNull(accessTokenRequest)
					&& deviceCodeMustNotBeNull(accessTokenRequest)
					&& refreshTokenMustBeNull(accessTokenRequest)
					&& terminalIdMustBeNull(accessTokenRequest);
			}
		});

		VALIDATORS.put(GrantType.CLIENT_CREDENTIALS, new Verifier() {
			@Override
			public boolean test(AccessTokenRequest accessTokenRequest) {
				return clientSecretMustNotBeNull(accessTokenRequest)
					&& deviceCodeMustBeNull(accessTokenRequest)
					&& refreshTokenMustBeNull(accessTokenRequest)
					&& ((bankIdMustBeNull(accessTokenRequest) && terminalIdMustBeNull(accessTokenRequest))
						|| (bankIdMustNotBeNull(accessTokenRequest) && terminalIdMustNotBeNull(accessTokenRequest)));
			}
		});
	}

	/**
	 * @see jakarta.validation.ConstraintValidator#isValid(Object, ConstraintValidatorContext)
	 */
	@Override
	public boolean isValid(AccessTokenRequest accessTokenRequest, ConstraintValidatorContext context) {
		return VALIDATORS.getOrDefault(accessTokenRequest.getGrantType(), new Verifier() {
			@Override
			public boolean test(AccessTokenRequest t) {
				Log.warn("Default validator in use");
				return false;
			}
		}).test(accessTokenRequest);
	}
}