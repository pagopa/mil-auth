/*
 * ReadSetOfRolesRequest.java
 *
 * 19 ago 2024
 */
package it.pagopa.swclient.mil.auth.admin.bean;

import org.hibernate.validator.constraints.Range;

import io.quarkus.runtime.annotations.RegisterForReflection;
import it.pagopa.swclient.mil.ErrorCode;
import it.pagopa.swclient.mil.auth.AuthErrorCode;
import it.pagopa.swclient.mil.auth.admin.AdminErrorCode;
import it.pagopa.swclient.mil.auth.bean.AuthValidationPattern;
import jakarta.validation.constraints.Pattern;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.QueryParam;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 
 * @author Antonio Tarricone
 */
@RegisterForReflection
@NoArgsConstructor
@AllArgsConstructor
@Data
@Accessors(chain = true)
public class ReadSetOfRolesRequest {
	/**
	 * <p>
	 * Page number.
	 * </p>
	 */
	@QueryParam(AdminQueryParamName.PAGE)
	@Range(min = 0, max = Integer.MAX_VALUE, message = AdminErrorCode.PAGE_MUST_BE_BETWEEN_MIN_AND_MAX_MSG)
	@DefaultValue("0")
	private int page;

	/**
	 * <p>
	 * Max number of roles contained in a page.
	 * </p>
	 */
	@QueryParam(AdminQueryParamName.SIZE)
	@Range(min = 1, max = 128, message = AdminErrorCode.SIZE_MUST_BE_BETWEEN_MIN_AND_MAX_MSG)
	@DefaultValue("20")
	private int size;

	/**
	 * <p>
	 * Client ID.
	 * </p>
	 */
	@QueryParam(AdminPathParamName.CLIENT_ID)
	@Pattern(regexp = AuthValidationPattern.CLIENT_ID, message = AuthErrorCode.CLIENT_ID_MUST_MATCH_REGEXP_MSG)
	private String clientId;

	/**
	 * <p>
	 * Acquirer ID.
	 * </p>
	 */
	@QueryParam(AdminQueryParamName.ACQUIRER_ID)
	@Pattern(regexp = AdminValidationPattern.NA_ACQUIRER_ID, message = ErrorCode.ACQUIRER_ID_MUST_MATCH_REGEXP_MSG)
	private String acquirerId;

	/**
	 * <p>
	 * Channel.
	 * </p>
	 */
	@QueryParam(AdminQueryParamName.CHANNEL)
	@Pattern(regexp = AdminValidationPattern.NA_CHANNEL, message = ErrorCode.CHANNEL_MUST_MATCH_REGEXP_MSG)
	private String channel;

	/**
	 * <p>
	 * Merchant ID.
	 * </p>
	 */
	@QueryParam(AdminQueryParamName.MERCHANT_ID)
	@Pattern(regexp = AdminValidationPattern.NA_MERCHANT_ID, message = ErrorCode.MERCHANT_ID_MUST_MATCH_REGEXP_MSG)
	private String merchantId;

	/**
	 * <p>
	 * Terminal ID.
	 * </p>
	 */
	@QueryParam(AdminQueryParamName.TERMINAL_ID)
	@Pattern(regexp = AdminValidationPattern.NA_TERMINAL_ID, message = ErrorCode.TERMINAL_ID_MUST_MATCH_REGEXP_MSG)
	private String terminalId;
}
