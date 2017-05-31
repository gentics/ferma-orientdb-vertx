package com.gentics.ferma;

@FunctionalInterface
public interface TxHandler<E> {

	/**
	 * Something has happened, so handle it.
	 *
	 * @param event
	 *            the event to handle
	 */
	void handle(E event) throws Exception;
}
