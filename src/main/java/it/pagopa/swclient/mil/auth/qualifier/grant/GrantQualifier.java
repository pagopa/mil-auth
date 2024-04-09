/*
 * GrantQualifier.java
 *
 * 3 apr 2024
 */
package it.pagopa.swclient.mil.auth.qualifier.grant;

import java.util.Map;

import it.pagopa.swclient.mil.auth.bean.GrantType;
import jakarta.enterprise.util.AnnotationLiteral;

/**
 * 
 * @author Antonio Tarricone
 */
public class GrantQualifier {
	/*
	 *
	 */
	@SuppressWarnings("serial")
	private static final Map<GrantType, AnnotationLiteral<?>> QUALIFIERS = Map.of(
		GrantType.CLIENT_CREDENTIALS, new AnnotationLiteral<ClientCredentials>() { },
		GrantType.DEVICE_CODE, new AnnotationLiteral<DeviceCode>() { },
		GrantType.REFRESH_TOKEN, new AnnotationLiteral<RefreshToken>() { });
	
	/**
	 * 
	 */
	private GrantQualifier() {
	}
	
	/**
	 * 
	 * @param grantType
	 * @return
	 */
	public static AnnotationLiteral<?> get(GrantType grantType) {
		return QUALIFIERS.get(grantType);
	}
}