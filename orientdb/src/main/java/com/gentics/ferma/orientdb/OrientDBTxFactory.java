package com.gentics.ferma.orientdb;

import com.gentics.ferma.Tx;
import com.gentics.ferma.TxFactory;
import com.syncleus.ferma.FramedGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;

public class OrientDBTxFactory implements TxFactory {

	/**
	 * Thread local that is used to store references to the used graph.
	 */
	public static ThreadLocal<FramedGraph> threadLocalGraph = new ThreadLocal<>();

	public static void setThreadLocalGraph(FramedGraph graph) {
		OrientDBTxFactory.threadLocalGraph.set(graph);
	}

	/**
	 * Return the current active graph. A transaction should be the only place where this threadlocal is updated.
	 * 
	 * @return
	 */
	public static FramedGraph getThreadLocalGraph() {
		return OrientDBTxFactory.threadLocalGraph.get();
	}

	protected OrientGraphFactory factory;

	private OrientDBTypeResolver typeResolver;

	public OrientDBTxFactory(OrientGraphFactory factory, String... basePaths) {
		this.factory = factory;
		this.typeResolver = new OrientDBTypeResolver(basePaths);
	}

	@Override
	public Tx tx() {
		return new OrientDBTx(factory, typeResolver);
	}

}
