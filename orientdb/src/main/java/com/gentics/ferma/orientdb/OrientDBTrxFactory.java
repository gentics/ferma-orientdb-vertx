package com.gentics.ferma.orientdb;

import com.gentics.ferma.NoTrx;
import com.gentics.ferma.Trx;
import com.gentics.ferma.TrxFactory;
import com.syncleus.ferma.FramedGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;

public class OrientDBTrxFactory implements TrxFactory {

	/**
	 * Thread local that is used to store references to the used graph.
	 */
	public static ThreadLocal<FramedGraph> threadLocalGraph = new ThreadLocal<>();

	public static void setThreadLocalGraph(FramedGraph graph) {
		OrientDBTrxFactory.threadLocalGraph.set(graph);
	}

	/**
	 * Return the current active graph. A transaction should be the only place where this threadlocal is updated.
	 * 
	 * @return
	 */
	public static FramedGraph getThreadLocalGraph() {
		return OrientDBTrxFactory.threadLocalGraph.get();
	}

	protected OrientGraphFactory factory;

	private OrientDBTypeResolver typeResolver;

	public OrientDBTrxFactory(OrientGraphFactory factory, String... basePaths) {
		this.factory = factory;
		this.typeResolver = new OrientDBTypeResolver(basePaths);
	}

	@Override
	public Trx trx() {
		return new OrientDBTrx(factory, typeResolver);
	}

	@Override
	public NoTrx noTrx() {
		return new OrientDBNoTrx(factory, typeResolver);
	}

}
