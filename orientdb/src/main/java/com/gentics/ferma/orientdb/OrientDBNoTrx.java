package com.gentics.ferma.orientdb;

import com.gentics.ferma.AbstractNoTrx;
import com.syncleus.ferma.FramedGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;

public class OrientDBNoTrx extends AbstractNoTrx implements AutoCloseable {


	public OrientDBNoTrx(OrientGraphFactory factory,OrientDBTypeResolver resolver) {
		FramedGraph graph = new DelegatingFramedOrientGraph<>(factory.getNoTx(), resolver);
		init(graph);
	}

	@Override
	public void close() {
		OrientDBTrxFactory.setThreadLocalGraph(getOldGraph());
		getGraph().shutdown();
	}
}
