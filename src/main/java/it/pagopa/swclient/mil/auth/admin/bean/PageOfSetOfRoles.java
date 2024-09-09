/*
 * PageOfSetOfRoles.java
 *
 * 19 ago 2024
 */
package it.pagopa.swclient.mil.auth.admin.bean;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 
 * @author Antonio Tarricone
 */
@RegisterForReflection
@JsonInclude(Include.NON_NULL)
@NoArgsConstructor
@AllArgsConstructor
@Data
@Accessors(chain = true)
public class PageOfSetOfRoles {
	/**
	 * <p>
	 * List of roles.
	 * </p>
	 */
	@JsonProperty(AdminJsonPropertyName.ROLES)
	private List<SetOfRoles> setsOfRoles;

	/**
	 * <p>
	 * Page metadata.
	 * </p>
	 */
	@JsonProperty(AdminJsonPropertyName.PAGE)
	private PageMetadata page;
}