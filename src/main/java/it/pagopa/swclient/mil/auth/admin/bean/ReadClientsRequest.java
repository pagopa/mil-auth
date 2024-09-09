/*
 * ReadClientsRequest.java
 *
 * 8 ago 2024
 */
package it.pagopa.swclient.mil.auth.admin.bean;

import org.hibernate.validator.constraints.Range;

import io.quarkus.runtime.annotations.RegisterForReflection;
import it.pagopa.swclient.mil.auth.admin.AdminErrorCode;
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
	@Range(min = 0, max = Integer.MAX_VALUE, message = AdminErrorCode.PAGE_MUST_BE_BETWEEN_MIN_AND_MAX_MSG)
	@DefaultValue("0")
	private int page;

	/**
	 * <p>
	 * Max number of clients contained in a page.
	 * </p>
	 */
	@QueryParam(AdminQueryParamName.SIZE)
	@Range(min = 1, max = 128, message = AdminErrorCode.SIZE_MUST_BE_BETWEEN_MIN_AND_MAX_MSG)
	@DefaultValue("20")
	private int size;
}
