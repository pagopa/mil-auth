/*
 * Password.java
 *
 * 16 mag 2023
 */
package it.pagopa.swclient.mil.auth.qualifier;

import jakarta.inject.Qualifier;

import java.lang.annotation.*;

/**
 * @author Antonio Tarricone
 */
@Qualifier
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({
        ElementType.TYPE, ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER
})
public @interface Password {
}