package com.gentics.ferma.orientdb.vertx;

import com.gentics.ferma.TrxHandler;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;

public interface TrxVertxFactory {

	/**
	 * Execute the txHandler within the scope of the no transaction and call the result handler once the transaction handler code has finished.
	 * 
	 * @param txHandler
	 *            Handler that will be executed within the scope of the transaction.
	 * @param resultHandler
	 *            Handler that is being invoked when the transaction has been committed
	 */
	<T> void trx(TrxHandler<Future<T>> txHandler, Handler<AsyncResult<T>> resultHandler);

	/**
	 * Asynchronously execute the txHandler within the scope of a transaction and invoke the result handler after the transaction code handler finishes or
	 * fails.
	 * 
	 * @param txHandler
	 *            Handler that will be executed within the scope of the transaction.
	 * @param resultHandler
	 */
	<T> void asyncTrx(TrxHandler<Future<T>> txHandler, Handler<AsyncResult<T>> resultHandler);

	/**
	 * Execute the given handler within the scope of a no transaction.
	 * 
	 * @param txHandler
	 *            handler that is invoked within the scope of the no-transaction.
	 * @return
	 */
	<T> Future<T> noTrx(TrxHandler<Future<T>> txHandler);

	/**
	 * Asynchronously execute the txHandler within the scope of a non transaction and invoke the result handler after the transaction code handler finishes.
	 * 
	 * @param txHandler
	 * @param resultHandler
	 */
	<T> void asyncNoTrx(TrxHandler<Future<T>> txHandler, Handler<AsyncResult<T>> resultHandler);
}
