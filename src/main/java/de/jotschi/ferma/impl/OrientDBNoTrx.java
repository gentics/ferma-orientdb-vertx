package de.jotschi.ferma.impl;

import com.syncleus.ferma.FramedGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;

import de.jotschi.ferma.AbstractNoTrx;
import de.jotschi.ferma.orientdb.DelegatingFramedOrientGraph;

public class OrientDBNoTrx extends AbstractNoTrx implements AutoCloseable {


	public OrientDBNoTrx(OrientGraphFactory factory) {
		FramedGraph graph = new DelegatingFramedOrientGraph<>(factory.getNoTx(), true, false);
		init(graph);
	}

	@Override
	public void close() {
		OrientDBTrxFactory.setThreadLocalGraph(getOldGraph());
		getGraph().shutdown();
	}
}
