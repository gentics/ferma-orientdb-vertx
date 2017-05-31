package com.gentics.ferma.orientdb.vertx;

@FunctionalInterface
public interface AsyncTxHandler<E> {

	void handle(E event) throws Exception;
}
