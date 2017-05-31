package com.gentics.ferma.orientdb;

import com.gentics.ferma.AbstractTx;
import com.orientechnologies.orient.core.exception.OConcurrentModificationException;
import com.syncleus.ferma.FramedTransactionalGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;

public class OrientDBTx extends AbstractTx {

	public OrientDBTx(OrientGraphFactory factory, OrientDBTypeResolver typeResolver) {
		FramedTransactionalGraph transaction = new DelegatingFramedOrientGraph(factory.getTx(), typeResolver);
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
			OrientDBTxFactory.setThreadLocalGraph(getOldGraph());
		}
	}
}
