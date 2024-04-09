/*
 * Roles.java
 *
 * 20 mar 2023
 */
package it.pagopa.swclient.mil.auth.bean;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @author Antonio Tarricone
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Roles {
	public static final String ROLES = "roles";
	
	@JsonProperty(value = ROLES, required = true)
	private List<Roles> listOfRoles;
}