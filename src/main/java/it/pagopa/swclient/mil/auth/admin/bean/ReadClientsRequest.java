/*
 * ReadClientsRequest.java
 *
 * 8 ago 2024
 */
package it.pagopa.swclient.mil.auth.admin.bean;

import io.quarkus.runtime.annotations.RegisterForReflection;
import it.pagopa.swclient.mil.auth.admin.AuthAdminErrorCode;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.QueryParam;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * <p>
 * Request to read a page of clients.
 * </p>
 * 
 * @author Antonio Tarricone
 */
@RegisterForReflection
@NoArgsConstructor
@AllArgsConstructor
@Data
@Accessors(chain = true)
public class ReadClientsRequest {
	/**
	 * <p>
	 * Page number.
	 * </p>
	 */
	@QueryParam(AdminQueryParamName.PAGE)
	@Min(value = 1, message = AuthAdminErrorCode.PAGE_MUST_BE_GREATER_THAN_OR_EQUAL_TO_VALUE_MSG)
	@Max(value = Integer.MAX_VALUE, message = AuthAdminErrorCode.PAGE_MUST_BE_LESS_THAN_OR_EQUAL_TO_VALUE_MSG)
	@DefaultValue("1")
	private int page;

	/**
	 * <p>
	 * Max number of clients contained in a page.
	 * </p>
	 */
	@QueryParam(AdminQueryParamName.SIZE)
	@Min(value = 1, message = AuthAdminErrorCode.SIZE_MUST_BE_GREATER_THAN_OR_EQUAL_TO_VALUE_MSG)
	@Max(value = 128, message = AuthAdminErrorCode.SIZE_MUST_BE_LESS_THAN_OR_EQUAL_TO_VALUE_MSG)
	@DefaultValue("20")
	private int size;
}
