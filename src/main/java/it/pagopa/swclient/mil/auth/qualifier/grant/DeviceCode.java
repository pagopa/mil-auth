/*
 * DeviceCode.java
 *
 * 3 apr 2024
 */
package it.pagopa.swclient.mil.auth.qualifier.grant;

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
public @interface DeviceCode {
}