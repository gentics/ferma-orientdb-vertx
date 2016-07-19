package de.jotschi.ferma.orientdb;

import com.syncleus.ferma.FramedGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;

import de.jotschi.ferma.AbstractNoTrx;

public class OrientDBNoTrx extends AbstractNoTrx implements AutoCloseable {


	public OrientDBNoTrx(OrientGraphFactory factory) {
		FramedGraph graph = new DelegatingFramedOrientGraph<>(factory.getNoTx(), new OrientDBTypeResolver());
		init(graph);
	}

	@Override
	public void close() {
		OrientDBTrxFactory.setThreadLocalGraph(getOldGraph());
		getGraph().shutdown();
	}
}
