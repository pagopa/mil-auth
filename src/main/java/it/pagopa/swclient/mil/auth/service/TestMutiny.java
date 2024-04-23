/*
 * TestMutiny.java
 *
 * 10 apr 2024
 */
package it.pagopa.swclient.mil.auth.service;

import io.smallrye.mutiny.Context;
import io.smallrye.mutiny.Uni;

/**
 * 
 * @author antonio.tarricone
 */
public class TestMutiny {
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Context pipelineCtx = Context.of();
		
		Uni.createFrom().item("this is a test")
			.invoke(s->pipelineCtx.put("X", s.toUpperCase()))
			.invoke(s-> System.out.println("ctx: " + pipelineCtx.<String>get("X")))
			.subscribe().with(
				item -> System.out.println("item: " + item),
				failure -> System.out.println("failure: " + failure));
	}
}