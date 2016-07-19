package de.jotschi.ferma.orientdb;

import com.syncleus.ferma.FramedGraph;
import com.syncleus.ferma.typeresolvers.TypeResolver;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;

import de.jotschi.ferma.AbstractNoTrx;

public class OrientDBNoTrx extends AbstractNoTrx implements AutoCloseable {


	public OrientDBNoTrx(OrientGraphFactory factory,TypeResolver resolver) {
		FramedGraph graph = new DelegatingFramedOrientGraph<>(factory.getNoTx(), resolver);
		init(graph);
	}

	@Override
	public void close() {
		OrientDBTrxFactory.setThreadLocalGraph(getOldGraph());
		getGraph().shutdown();
	}
}
