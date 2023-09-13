/*
 * ValidationTarget.java
 *
 * 28 mar 2023
 */
package it.pagopa.swclient.mil.auth.validation.constraints;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

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