/*
 * PageMetadata.java
 *
 * 29 lug 2024
 */
package it.pagopa.swclient.mil.auth.admin.bean;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * <p>
 * Metadata of page of items.
 * </p>
 * 
 * @author Antonio Tarricone
 */
@RegisterForReflection
@JsonInclude(Include.NON_NULL)
@NoArgsConstructor
@AllArgsConstructor
@Data
@Accessors(chain = true)
public class PageMetadata {
	/**
	 * <p>
	 * Total number of items.
	 * </p>
	 */
	@JsonProperty(AdminJsonPropertyName.TOTAL_ELEMENTS)
	private long totalElements;

	/**
	 * <p>
	 * Total number of pages.
	 * </p>
	 */
	@JsonProperty(AdminJsonPropertyName.TOTAL_PAGES)
	private long totalPages;

	/**
	 * <p>
	 * Page number.
	 * </p>
	 */
	@JsonProperty(AdminJsonPropertyName.PAGE_NUMBER)
	private int page;

	/**
	 * <p>
	 * Max number of items of a page.
	 * </p>
	 */
	@JsonProperty(AdminJsonPropertyName.PAGE_SIZE)
	private int size;
}