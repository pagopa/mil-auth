/*
 * AdminValidationPattern.java
 *
 * 26 lug 2024
 */
package it.pagopa.swclient.mil.auth.admin.bean;

import it.pagopa.swclient.mil.bean.Channel;

/**
 * <p>
 * Parameters patterns.
 * </p>
 * 
 * @author Antonio Tarricone
 */
public class AdminValidationPattern {
	/**
	 * <p>
	 * Acquirer ID.
	 * </p>
	 */
	public static final String NA_ACQUIRER_ID = "^(\\d{1,11}|NA)$";

	/**
	 * <p>
	 * Merchant ID.
	 * </p>
	 */
	public static final String NA_MERCHANT_ID = "^([0-9a-zA-Z]{1,15}|NA)$";

	/**
	 * <p>
	 * Terminal ID.
	 * </p>
	 */
	public static final String NA_TERMINAL_ID = "^([0-9a-zA-Z]{1,8}|NA)$";

	/**
	 * <p>
	 * Channel.
	 * </p>
	 */
	public static final String NA_CHANNEL = "^(" + Channel.ATM + "|" + Channel.POS + "|" + Channel.TOTEM + "|" + Channel.CASH_REGISTER + "|" + Channel.CSA + "|NA)$";

	/**
	 * <p>
	 * Client description.
	 * </p>
	 */
	public static final String DESCRIPTION = "^[ -~]{1,256}$";

	/**
	 * <p>
	 * Client subject.
	 * </p>
	 */
	public static final String SUBJECT = "^[ -~]{1,256}$";

	/**
	 * <p>
	 * Role.
	 * </p>
	 */
	public static final String ROLE = "^[ -~]{1,64}$";

	/**
	 * 
	 */
	public static final String SET_OF_ROLES_ID = "^[a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12}$";

	/**
	 * <p>
	 * This class contains only constants.
	 * </p>
	 */
	private AdminValidationPattern() {
	}
}
