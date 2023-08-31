/*
 * PoyntToken.java
 *
 * 16 mag 2023
 */
package it.pagopa.swclient.mil.auth.qualifier;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.inject.Qualifier;

/**
 * 
 * @author Antonio Tarricone
 */
@Qualifier
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({
	ElementType.TYPE, ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER
})
public @interface PoyntToken {
}