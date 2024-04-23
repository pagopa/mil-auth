/*
 * DeletionRecoveryLevel.java
 *
 * 10 apr 2024
 */
package it.pagopa.swclient.mil.auth.azure.keyvaultkeys.bean;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Reflects the deletion recovery level currently in effect for keys in the current vault. If it
 * contains 'Purgeable' the key can be permanently deleted by a privileged user; otherwise, only the
 * system can purge the key, at the end of the retention interval.
 * 
 * @see <a href=
 *      "https://learn.microsoft.com/en-us/rest/api/keyvault/keys/create-key/create-key?view=rest-keyvault-keys-7.4&tabs=HTTP#deletionrecoverylevel">Microsoft
 *      Azure Documentation</a>
 * @author Antonio Tarricone
 */
public enum DeletionRecoveryLevel {
	/*
	 * Denotes a vault state in which deletion is recoverable without the possibility for immediate and
	 * permanent deletion (i.e. purge when 7<= SoftDeleteRetentionInDays < 90).This level guarantees the
	 * recoverability of the deleted entity during the retention interval and while the subscription is
	 * still available.
	 */
	@JsonProperty("CustomizedRecoverable")
	CUSTOMIZED_RECOVERABLE("CustomizedRecoverable"),

	/*
	 * Denotes a vault and subscription state in which deletion is recoverable, immediate and permanent
	 * deletion (i.e. purge) is not permitted, and in which the subscription itself cannot be
	 * permanently canceled when 7<= SoftDeleteRetentionInDays < 90. This level guarantees the
	 * recoverability of the deleted entity during the retention interval, and also reflects the fact
	 * that the subscription itself cannot be cancelled.
	 */
	@JsonProperty("CustomizedRecoverable+ProtectedSubscription")
	CUSTOMIZED_RECOVERABLE_PROTECTED_SUBSCRIPTION("CustomizedRecoverable+ProtectedSubscription"),

	/*
	 * Denotes a vault state in which deletion is recoverable, and which also permits immediate and
	 * permanent deletion (i.e. purge when 7<= SoftDeleteRetentionInDays < 90). This level guarantees
	 * the recoverability of the deleted entity during the retention interval, unless a Purge operation
	 * is requested, or the subscription is cancelled.
	 */
	@JsonProperty("CustomizedRecoverable+Purgeable")
	CUSTOMIZED_RECOVERABLE_PURGEABLE("CustomizedRecoverable+Purgeable"),

	/*
	 * Denotes a vault state in which deletion is an irreversible operation, without the possibility for
	 * recovery. This level corresponds to no protection being available against a Delete operation; the
	 * data is irretrievably lost upon accepting a Delete operation at the entity level or higher
	 * (vault, resource group, subscription etc.)
	 */
	@JsonProperty("Purgeable")
	PURGEABLE("Purgeable"),

	/*
	 * Denotes a vault state in which deletion is recoverable without the possibility for immediate and
	 * permanent deletion (i.e. purge). This level guarantees the recoverability of the deleted entity
	 * during the retention interval(90 days) and while the subscription is still available. System will
	 * permanently delete it after 90 days, if not recovered.
	 */
	@JsonProperty("Recoverable")
	RECOVERABLE("Recoverable"),

	/*
	 * Denotes a vault and subscription state in which deletion is recoverable within retention interval
	 * (90 days), immediate and permanent deletion (i.e. purge) is not permitted, and in which the
	 * subscription itself cannot be permanently canceled. System will permanently delete it after 90
	 * days, if not recovered.
	 */
	@JsonProperty("Recoverable+ProtectedSubscription")
	RECOVERABLE_PROTECTED_SUBSCRIPTION("Recoverable+ProtectedSubscription"),

	/*
	 * Denotes a vault state in which deletion is recoverable, and which also permits immediate and
	 * permanent deletion (i.e. purge). This level guarantees the recoverability of the deleted entity
	 * during the retention interval (90 days), unless a Purge operation is requested, or the
	 * subscription is cancelled. System will permanently delete it after 90 days, if not recovered.
	 */
	@JsonProperty("Recoverable+Purgeable")
	RECOVERABLE_PURGEABLE("Recoverable+Purgeable");

	/*
	 * 
	 */
	private final String value;

	/**
	 * 
	 * @param value
	 */
	private DeletionRecoveryLevel(String value) {
		this.value = value;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return value;
	}
}
