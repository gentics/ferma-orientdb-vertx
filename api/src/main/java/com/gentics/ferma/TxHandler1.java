package com.gentics.ferma;

@FunctionalInterface
public interface TxHandler1<T> {
	T handle() throws Exception;
}
