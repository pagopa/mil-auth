/*
 * ValidationTarget.java
 *
 * 28 mar 2023
 */
package it.pagopa.swclient.mil.auth.validation.constraints;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

/**
 * 
 * @author Antonio Tarricone
 */
@Documented
@Retention(RUNTIME)
@Target(TYPE)
@Constraint(validatedBy = {
	Validator.class
})
public @interface ValidationTarget {
	String message() default "";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}