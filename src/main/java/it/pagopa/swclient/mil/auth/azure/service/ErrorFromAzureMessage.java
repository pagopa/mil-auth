/*
 * ErrorFromAzureMessage.java
 * 
 * 29 mar 2024
 */
package it.pagopa.swclient.mil.auth.azure.service;

/**
 * 
 * @author Antonio Tarricone
 */
public class ErrorFromAzureMessage {
	/**
	 * 
	 */
	private ErrorFromAzureMessage() {
	}
	
	/**
	 * Error message when something went wrong with Azure.
	 * 
	 * @param errorCode
	 * @return
	 */
	public static String get(String errorCode) {
		return String.format("[%s] Error from Azure.", errorCode);
	}
}
