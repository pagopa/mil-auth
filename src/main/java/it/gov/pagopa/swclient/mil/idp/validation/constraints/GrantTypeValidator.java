/*
 * GrantTypeValidator.java
 *
 * 28 mar 2023
 */
package it.gov.pagopa.swclient.mil.idp.validation.constraints;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import it.gov.pagopa.swclient.mil.idp.bean.GetAccessToken;
import it.gov.pagopa.swclient.mil.idp.bean.GrantType;

/**
 * +---------------+----------+----------+---------------+----------------------+
 * | grant_type    | username | password | refresh_token | ext_token | add_data |
 * +---------------+----------+----------+---------------+-----------+----------+
 * | password      | not null | not null | null          | null      | null     |
 * | refresh_token | null     | null     | not null      | null      | null     |
 * | poynt_token   | null     | null     | null          | not null  | not null |
 * +---------------+----------+----------+---------------+-----------+----------+
 * 
 * @author Antonio Tarricone
 */
public class GrantTypeValidator implements ConstraintValidator<it.gov.pagopa.swclient.mil.idp.validation.constraints.GrantTypeInterface, GetAccessToken> {
	/*
	 * 
	 */
	private static final Map<String, Predicate<GetAccessToken>> VALIDATOR = new HashMap<>();
	static {
		VALIDATOR.put(GrantType.PASSWORD, new Predicate<GetAccessToken>() {
			@Override
			public boolean test(GetAccessToken getAccessToken) {
				return getAccessToken.getUsername() != null
					&& getAccessToken.getPassword() != null
					&& getAccessToken.getRefreshToken() == null
					&& getAccessToken.getExtToken() == null
					&& getAccessToken.getAddData() == null;
			}
		
		});
		
		VALIDATOR.put(GrantType.REFRESH_TOKEN, new Predicate<GetAccessToken>() {
			@Override
			public boolean test(GetAccessToken getAccessToken) {
				return getAccessToken.getUsername() == null
					&& getAccessToken.getPassword() == null
					&& getAccessToken.getRefreshToken() != null
					&& getAccessToken.getExtToken() == null
					&& getAccessToken.getAddData() == null;
			}
		
		});
		
		VALIDATOR.put(GrantType.POYNT_TOKEN, new Predicate<GetAccessToken>() {
			@Override
			public boolean test(GetAccessToken getAccessToken) {
				return getAccessToken.getUsername() == null
					&& getAccessToken.getPassword() == null
					&& getAccessToken.getRefreshToken() == null
					&& getAccessToken.getExtToken() != null
					&& getAccessToken.getAddData() != null;
			}
		});
	}
	
	/**
	 * @see javax.validation.ConstraintValidator#isValid(Object, ConstraintValidatorContext)
	 */
	@Override
	public boolean isValid(GetAccessToken getAccessToken, ConstraintValidatorContext context) {
		return VALIDATOR.getOrDefault(getAccessToken.getGrantType(), new Predicate<GetAccessToken>() {
			@Override
			public boolean test(GetAccessToken t) {
				return false;
			}
		}).test(getAccessToken);
	}
}