package com.gentics.ferma;

import com.syncleus.ferma.FramedTransactionalGraph;

/**
 * A {@link Tx} is an interface for autoclosable transactions.
 */
public interface Tx extends AutoCloseable {

	/**
	 * Thread local that is used to store references to the used graph.
	 */
	public static ThreadLocal<Tx> threadLocalGraph = new ThreadLocal<>();

	public static void setActive(Tx tx) {
		Tx.threadLocalGraph.set(tx);
	}

	/**
	 * Return the current active graph. A transaction should be the only place where this threadlocal is updated.
	 * 
	 * @return
	 */
	public static Tx getActive() {
		return Tx.threadLocalGraph.get();
	}

	/**
	 * Mark the transaction as succeeded. The autoclosable will invoke a commit when completing.
	 */
	void success();

	/**
	 * Mark the transaction as failed. The autoclosable will invoke a rollback when completing.
	 */
	void failure();

	/**
	 * Return the framed graph that is bound to the transaction.
	 * 
	 * @return
	 */
	FramedTransactionalGraph getGraph();

	/**
	 * Invoke rollback or commit when closing the autoclosable. By default a rollback will be invoked.
	 */
	@Override
	void close();

	/**
	 * Add new isolated vertex to the graph.
	 * 
	 * @param classOfT
	 * @return
	 */
	default <T> T addVertex(Class<T> classOfT) {
		return getGraph().addFramedVertex(classOfT);
	}

}
