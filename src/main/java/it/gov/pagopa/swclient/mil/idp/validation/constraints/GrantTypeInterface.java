/*
 * GrantType.java
 *
 * 28 mar 2023
 */
package it.gov.pagopa.swclient.mil.idp.validation.constraints;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

/**
 * If grant_type = password, username and password must not be null and refresh_token must be null.
 * If grant_type = refresh_token, refresh token must not be null and username and password must be
 * null.
 * 
 * @author Antonio Tarricone
 */
@Documented
@Retention(RUNTIME)
@Target(TYPE)
@Constraint(validatedBy = {
	GrantTypeValidator.class
})
public @interface GrantType {
	String message() default "";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}