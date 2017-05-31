package com.gentics.ferma;

@FunctionalInterface
public interface TxHandler2 {

	void handle(Tx tx) throws Exception;

}
