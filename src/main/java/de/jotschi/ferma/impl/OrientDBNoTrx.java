package de.jotschi.ferma.impl;

import com.syncleus.ferma.DelegatingFramedGraph;
import com.syncleus.ferma.FramedGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;

import de.jotschi.ferma.AbstractNoTrx;

public class OrientDBNoTrx extends AbstractNoTrx implements AutoCloseable {


	public OrientDBNoTrx(OrientGraphFactory factory) {
		FramedGraph graph = new DelegatingFramedGraph<>(factory.getNoTx(), true, false);
		init(graph);
	}

	@Override
	public void close() {
		OrientDBTrxFactory.setThreadLocalGraph(getOldGraph());
		getGraph().shutdown();
	}
}
