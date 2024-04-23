/*
 * KeyAttributes.java
 *
 * 10 apr 2024
 */
package it.pagopa.swclient.mil.auth.azure.keyvaultkeys.bean;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * The attributes of a key managed by the key vault service.
 * 
 * @see <a href=
 *      "https://learn.microsoft.com/en-us/rest/api/keyvault/keys/create-key/create-key?view=rest-keyvault-keys-7.4&tabs=HTTP#keyattributes">Microsoft
 *      Azure Documentation</a>
 * @author Antonio Tarricone
 */
@RegisterForReflection
@Getter
@Setter
@Accessors(chain = true)
@JsonInclude(value = Include.NON_NULL)
public class KeyAttributes {
	/*
	 * 
	 */
	public static final String CREATED = "created";
	public static final String ENABLED = "enabled";
	public static final String EXP = "exp";
	public static final String EXPORTABLE = "exportable";
	public static final String NBF = "nbf";
	public static final String RECOVERABLE_DAYS = "recoverableDays";
	public static final String RECOVERY_LEVEL = "recoveryLevel";
	public static final String UPDATED = "updated";

	/*
	 * Creation time in UTC (Unix epoch in seconds).
	 */
	@JsonProperty(CREATED)
	private Long created;

	/*
	 * Determines whether the object is enabled.
	 */
	@JsonProperty(ENABLED)
	private Boolean enabled;

	/*
	 * Expiry date in UTC (Unix epoch in seconds).
	 */
	@JsonProperty(EXP)
	private Long exp;

	/*
	 * Indicates if the private key can be exported. Release policy must be provided when creating the
	 * first version of an exportable key.
	 */
	@JsonProperty(EXPORTABLE)
	private Boolean exportable;

	/*
	 * Not before date in UTC (Unix epoch in seconds).
	 */
	@JsonProperty(NBF)
	private Long nbf;

	/*
	 * softDelete data retention days. Value should be >=7 and <=90 when softDelete enabled, otherwise
	 * 0.
	 */
	@JsonProperty(RECOVERABLE_DAYS)
	private Integer recoverableDays;

	/*
	 * Reflects the deletion recovery level currently in effect for keys in the current vault. If it
	 * contains 'Purgeable' the key can be permanently deleted by a privileged user; otherwise, only the
	 * system can purge the key, at the end of the retention interval.
	 */
	@JsonProperty(RECOVERY_LEVEL)
	private DeletionRecoveryLevel recoveryLevel;

	/*
	 * Last updated time in UTC (Unix epoch in seconds).
	 */
	@JsonProperty(UPDATED)
	private Long updated;
}