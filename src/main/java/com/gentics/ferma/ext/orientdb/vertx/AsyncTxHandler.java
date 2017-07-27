package com.gentics.ferma.ext.orientdb.vertx;

@FunctionalInterface
public interface AsyncTxHandler<E> {

	void handle(E event) throws Exception;
}
