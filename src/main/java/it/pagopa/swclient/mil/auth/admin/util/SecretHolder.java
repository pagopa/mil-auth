/*
 * SecretHolder.java
 *
 * 13 dic 2024
 */
package it.pagopa.swclient.mil.auth.admin.util;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * 
 * @author Antonio Tarricone
 */
@Getter
@Setter
@Accessors(chain = true)
public class SecretHolder {
	private String secret = null;
}
