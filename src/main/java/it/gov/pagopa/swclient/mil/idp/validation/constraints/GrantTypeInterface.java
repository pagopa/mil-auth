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
@Documented
@Retention(RUNTIME)
@Target(TYPE)
@Constraint(validatedBy = {
	GrantTypeValidator.class
})
public @interface GrantTypeInterface {
	String message() default "";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}