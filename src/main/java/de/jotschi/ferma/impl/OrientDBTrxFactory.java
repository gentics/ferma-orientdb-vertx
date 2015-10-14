package de.jotschi.ferma.impl;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import com.orientechnologies.orient.core.exception.OConcurrentModificationException;
import com.orientechnologies.orient.core.exception.OSchemaException;
import com.syncleus.ferma.FramedGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;
import com.tinkerpop.gremlin.Tokens.T;

import de.jotschi.ferma.NoTrx;
import de.jotschi.ferma.Trx;
import de.jotschi.ferma.TrxFactory;
import de.jotschi.ferma.TrxHandler;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

public class OrientDBTrxFactory implements TrxFactory {

	private static final Logger log = LoggerFactory.getLogger(OrientDBTrxFactory.class);

	private int maxRetry = 25;

	/**
	 * Thread local that is used to store references to the used graph.
	 */
	public static ThreadLocal<FramedGraph> threadLocalGraph = new ThreadLocal<>();

	public static void setThreadLocalGraph(FramedGraph graph) {
		OrientDBTrxFactory.threadLocalGraph.set(graph);
	}

	/**
	 * Return the current active graph. A transaction should be the only place where this threadlocal is updated.
	 * 
	 * @return
	 */
	public static FramedGraph getThreadLocalGraph() {
		return OrientDBTrxFactory.threadLocalGraph.get();
	}

	protected OrientGraphFactory factory;

	protected Vertx vertx;

	public OrientDBTrxFactory(OrientGraphFactory factory, Vertx vertx) {
		this.factory = factory;
		this.vertx = vertx;
	}

	@Override
	public Trx trx() {
		return new OrientDBTrx(factory);
	}

	@Override
	public NoTrx noTrx() {
		return new OrientDBNoTrx(factory);
	}

	@Override
	public <T> void trx(TrxHandler<Future<T>> txHandler, Handler<AsyncResult<T>> resultHandler) {
		/**
		 * OrientDB uses the MVCC pattern which requires a retry of the code that manipulates the graph in cases where for example an
		 * {@link OConcurrentModificationException} is thrown.
		 */
		Future<T> currentTransactionCompleted = null;
		for (int retry = 0; retry < maxRetry; retry++) {
			currentTransactionCompleted = Future.future();
			try (Trx tx = trx()) {
				// TODO FIXME get rid of the countdown latch
				CountDownLatch latch = new CountDownLatch(1);
				currentTransactionCompleted.setHandler(rh -> {
					if (rh.succeeded()) {
						tx.success();
					} else {
						tx.failure();
					}
					latch.countDown();
				});
				txHandler.handle(currentTransactionCompleted);
				latch.await(30, TimeUnit.SECONDS);
				break;
			} catch (OSchemaException e) {
				log.error("OrientDB schema exception detected.");
				// TODO maybe we should invoke a metadata getschema reload?
				// factory.getTx().getRawGraph().getMetadata().getSchema().reload();
				// Database.getThreadLocalGraph().getMetadata().getSchema().reload();
			} catch (OConcurrentModificationException e) {
				if (log.isTraceEnabled()) {
					log.trace("Error while handling transaction. Retrying " + retry, e);
				}
			} catch (Exception e) {
				log.error("Error handling transaction", e);
				resultHandler.handle(Future.failedFuture(e));
				return;
			}
			if (log.isDebugEnabled()) {
				log.debug("Retrying .. {" + retry + "}");
			}
		}
		if (currentTransactionCompleted != null && currentTransactionCompleted.isComplete()) {
			resultHandler.handle(currentTransactionCompleted);
			return;
		}
		resultHandler.handle(Future.failedFuture("retry limit for trx exceeded"));
		return;

	}

	@Override
	public <T> void asyncTrx(TrxHandler<Future<T>> txHandler, Handler<AsyncResult<T>> resultHandler) {
		vertx.executeBlocking(bh -> {
			trx(txHandler, rh -> {
				if (rh.succeeded()) {
					bh.complete(rh.result());
				} else {
					bh.fail(rh.cause());
				}
			});
		} , false, resultHandler);
		return;
	}

	@Override
	public <T> Future<T> noTrx(TrxHandler<Future<T>> txHandler) {
		Future<T> future = Future.future();
		try (NoTrx noTx = noTrx()) {
			txHandler.handle(future);
		} catch (Exception e) {
			log.error("Error while handling no-transaction.", e);
			return Future.failedFuture(e);
		}
		return future;
	}

	@Override
	public <T> void asyncNoTrx(TrxHandler<Future<T>> txHandler, Handler<AsyncResult<T>> resultHandler) {
		vertx.executeBlocking(bh -> {
			Future<T> future = noTrx(txHandler);
			future.setHandler(rh -> {
				if (rh.failed()) {
					bh.fail(rh.cause());
				} else {
					bh.complete(rh.result());
				}
			});
		} , false, resultHandler);
		return;
	}

}
