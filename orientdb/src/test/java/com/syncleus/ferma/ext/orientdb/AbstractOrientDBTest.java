package com.syncleus.ferma.ext.orientdb;

import org.junit.Before;

import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;

public class AbstractOrientDBTest implements TestDateHelper {

	protected OrientGraphFactory graphFactory;
	protected OrientDBTxFactory graph;

	@Before
	public void setupDB() {
		graphFactory = new OrientGraphFactory("memory:tinkerpop").setupPool(4, 10);
		graph = new OrientDBTxFactory(graphFactory, "com.syncleus.ferma.ext.orientdb.model");
	}

}
