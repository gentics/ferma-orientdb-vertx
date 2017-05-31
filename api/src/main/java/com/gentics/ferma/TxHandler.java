package com.gentics.ferma;

@FunctionalInterface
public interface TxHandler<T> {

	T handle(Tx tx) throws Exception;

}