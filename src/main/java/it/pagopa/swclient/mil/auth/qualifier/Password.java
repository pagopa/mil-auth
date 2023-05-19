/*
 * Password.java
 *
 * 16 mag 2023
 */
package it.pagopa.swclient.mil.auth.qualifier;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import jakarta.inject.Qualifier;

/**
 * 
 * @author Antonio Tarricone
 */
@Qualifier
@Documented
@Retention(RUNTIME)
@Target({
	TYPE, METHOD, FIELD, PARAMETER
})
public @interface Password {
}