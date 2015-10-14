package de.jotschi.ferma.impl;

import com.orientechnologies.orient.core.exception.OConcurrentModificationException;
import com.syncleus.ferma.DelegatingFramedTransactionalGraph;
import com.syncleus.ferma.FramedTransactionalGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;

import de.jotschi.ferma.AbstractTrx;

public class OrientDBTrx extends AbstractTrx {

	public OrientDBTrx(OrientGraphFactory factory) {
		FramedTransactionalGraph transaction = new DelegatingFramedTransactionalGraph<>(factory.getTx(), true, false);
		// ((OrientGraph)((DelegatingFramedTransactionalGraph)txGraph).getBaseGraph()).getRawGraph().activateOnCurrentThread();
		init(transaction);
	}

	@Override
	public void close() {
		try {
			if (isSuccess()) {
				commit();
			} else {
				rollback();
			}
		} catch (OConcurrentModificationException e) {
			throw e;
		} finally {
			// Restore the old graph that was previously swapped with the current graph
			getGraph().shutdown();
			OrientDBTrxFactory.setThreadLocalGraph(getOldGraph());
		}
	}
}
