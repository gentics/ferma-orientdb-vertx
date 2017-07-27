package com.syncleus.ferma.ext.orientdb.vertx;

@FunctionalInterface
public interface AsyncTxHandler<E> {

	void handle(E event) throws Exception;
}
