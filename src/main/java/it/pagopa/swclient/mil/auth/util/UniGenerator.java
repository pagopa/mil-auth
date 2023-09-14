/*
 * UniGenerator.java
 *
 * 17 mag 2023
 */
package it.pagopa.swclient.mil.auth.util;

import io.smallrye.mutiny.Uni;

/**
 * @author Antonio Tarricone
 */
public class UniGenerator {
	/**
	 *
	 */
	private UniGenerator() {
	}

	/**
	 * To be used if a check fails.
	 *
	 * @param <T>
	 * @param code
	 * @param message
	 * @return
	 */
	public static <T> Uni<T> exception(String code, String message) {
		return Uni.createFrom().failure(new AuthException(code, message));
	}

	/**
	 * To be used if an application error occurs.
	 *
	 * @param <T>
	 * @param code
	 * @param message
	 * @return
	 */
	public static <T> Uni<T> error(String code, String message) {
		return Uni.createFrom().failure(new AuthError(code, message));
	}

	/**
	 * @param <T>
	 * @param t
	 * @return
	 */
	public static <T> Uni<T> item(T t) {
		return Uni.createFrom().item(t);
	}

	/**
	 * @return
	 */
	public static Uni<Void> voidItem() {
		return Uni.createFrom().voidItem();
	}
}