package de.jotschi.ferma;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;

public interface TrxFactory {
	/**
	 * Return a new autoclosable transaction handler. This object should be used within a try-with-resource block.
	 * 
	 * <pre>
	 * {
	 * 	&#64;code
	 * 	try(Trx tx = db.trx()) {
	 * 	  // interact with graph db here
	 *  }
	 * }
	 * </pre>
	 * 
	 * @return
	 */
	Trx trx();

	/**
	 * Execute the txHandler within the scope of the no transaction and call the result handler once the transaction handler code has finished.
	 * 
	 * @param txHandler
	 *            Handler that will be executed within the scope of the transaction.
	 * @param resultHandler
	 *            Handler that is being invoked when the transaction has been committed
	 * @return
	 */
	<T> void trx(TrxHandler<Future<T>> txHandler, Handler<AsyncResult<T>> resultHandler);

	/**
	 * Asynchronously execute the txHandler within the scope of a transaction and invoke the result handler after the transaction code handler finishes or
	 * fails.
	 * 
	 * @param txHandler
	 *            Handler that will be executed within the scope of the transaction.
	 * @param resultHandler
	 * @return
	 */
	<T> void asyncTrx(TrxHandler<Future<T>> txHandler, Handler<AsyncResult<T>> resultHandler);

	/**
	 * Return a autoclosable transaction handler. Please note that this method will return a non transaction handler. All actions invoked are executed atomic
	 * and no rollback can be performed. This object should be used within a try-with-resource block.
	 * 
	 * <pre>
	 * {
	 * 	&#64;code
	 * 	try(NoTrx tx = db.noTrx()) {
	 * 	  // interact with graph db here
	 *  }
	 * }
	 * </pre>
	 * 
	 * @return
	 */
	NoTrx noTrx();

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
	 * @return
	 */
	<T> void asyncNoTrx(TrxHandler<Future<T>> txHandler, Handler<AsyncResult<T>> resultHandler);
}
