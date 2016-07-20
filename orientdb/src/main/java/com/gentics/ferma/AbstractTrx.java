package com.gentics.ferma;

import com.gentics.ferma.Trx;
import com.gentics.ferma.orientdb.OrientDBTrxFactory;
import com.syncleus.ferma.FramedTransactionalGraph;

/**
 * An abstract class that can be used to implement vendor specific graph database Trx classes.
 */
public abstract class AbstractTrx extends AbstractTrxBase<FramedTransactionalGraph>implements Trx {

	private boolean isSuccess = false;

	@Override
	public void success() {
		isSuccess = true;
	}

	@Override
	public void failure() {
		isSuccess = false;
	}

	/**
	 * Return the state of the success status flag.
	 * 
	 * @return
	 */
	protected boolean isSuccess() {
		return isSuccess;
	}

	@Override
	public void close() {
		OrientDBTrxFactory.setThreadLocalGraph(getOldGraph());
		if (isSuccess()) {
			commit();
		} else {
			rollback();
		}
		// Restore the old graph that was previously swapped with the current graph
		getGraph().close();
		getGraph().shutdown();
	}

	/**
	 * Invoke a commit on the database of this transaction.
	 */
	protected void commit() {
		long start = System.currentTimeMillis();
		if (getGraph() instanceof FramedTransactionalGraph) {
			((FramedTransactionalGraph) getGraph()).commit();
		}
		long duration = System.currentTimeMillis() - start;
	}

	/**
	 * Invoke a rollback on the database of this transaction.
	 */
	protected void rollback() {
		if (getGraph() instanceof FramedTransactionalGraph) {
			((FramedTransactionalGraph) getGraph()).rollback();
		}
	}

}
