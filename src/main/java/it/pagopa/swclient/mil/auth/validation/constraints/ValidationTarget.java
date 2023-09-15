/*
 * ValidationTarget.java
 *
 * 28 mar 2023
 */
package it.pagopa.swclient.mil.auth.validation.constraints;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

/**
 * @author Antonio Tarricone
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Constraint(validatedBy = {
	Validator.class
})
public @interface ValidationTarget {
	String message() default "";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}