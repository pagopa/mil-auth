/*
 * Main.java
 *
 * 12 apr 2024
 */
package it.pagopa.swclient.mil.auth.azure.service;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;

/**
 * 
 * @author antonio.tarricone
 */
public class Main {
	public static Uni<List<String>> getKeys() {
		return Uni.createFrom().item(List.of("a", "b", "c"));
	}

	public static Uni<List<String>> getVersions(String key) {
		if (key.equals("a")) {
			return Uni.createFrom().item(List.of("a1", "a2", "a3"));
		} else if (key.equals("b")) {
			return Uni.createFrom().item(List.of("b1", "b2", "b3"));
		} else if (key.equals("c")) {
			return Uni.createFrom().item(List.of("c1", "c2", "c3"));
		} else {
			return Uni.createFrom().item(List.of("?1", "?2", "?3"));
		}
	}

	public static void main(String[] args) {
		getKeys()
			.onItem().transformToMulti(keys -> Multi.createFrom().items(keys.stream()))
			.onItem().transformToUniAndConcatenate(Main::getVersions)
			.onItem().transformToMultiAndConcatenate(versions -> Multi.createFrom().items(versions.stream()))
			.collect()
			.asList()
			.subscribe().with(
				item -> System.out.println("item: " + item),
				failure -> System.err.println("failure: " + failure));

	}
}
