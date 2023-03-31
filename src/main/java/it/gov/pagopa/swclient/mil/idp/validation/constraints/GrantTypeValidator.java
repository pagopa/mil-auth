/*
 * GrantTypeValidator.java
 *
 * 28 mar 2023
 */
package it.gov.pagopa.swclient.mil.idp.validation.constraints;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import it.gov.pagopa.swclient.mil.idp.bean.GetAccessToken;

/**
 * If grant_type = password, username and password must not be null and refresh_token must be null.
 * If grant_type = refresh_token, refresh token must not be null and username and password must be
 * null.
 * 
 * @author Antonio Tarricone
 */
public class GrantTypeValidator implements ConstraintValidator<GrantType, GetAccessToken> {
	@Override
	public boolean isValid(GetAccessToken getAccessToken, ConstraintValidatorContext context) {
		boolean isValid = false;
		if (getAccessToken.getGrantType().equals("password")) {
			isValid = (getAccessToken.getUsername() != null && getAccessToken.getPassword() != null && getAccessToken.getRefreshToken() == null);
		} else {
			isValid = (getAccessToken.getUsername() == null && getAccessToken.getPassword() == null && getAccessToken.getRefreshToken() != null);
		}
		return isValid;
	}
}
