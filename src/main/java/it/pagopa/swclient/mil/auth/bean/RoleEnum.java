/*
 * RoleEnum.java
 *
 * 22 mag 2023
 */
package it.pagopa.swclient.mil.auth.bean;

/**
 * 
 * @author Antonio Tarricone
 */
public enum RoleEnum {
	NODO("Nodo"),
	NOTICE_PAYER("NoticePayer"),
	INSTITUTION_PORTAL("InstitutionPortal"),
	SERVICE_LIST_REQUESTER("ServiceListRequester"),
	SLAVE_POS("SlavePos");
	
	/*
	 * String value.
	 */
	private String string;
	
	/**
	 * 
	 * @param string
	 */
	private RoleEnum(String string) {
		this.string = string;
	}
	
	/**
	 * 
	 */
	public String toString() {
		return string;
	}
}