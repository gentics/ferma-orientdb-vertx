package com.gentics.ferma;

import org.junit.Before;

import com.gentics.ferma.ext.orientdb.vertx.OrientDBTxVertexFactory;
import com.syncleus.ferma.ext.orientdb.TestDateHelper;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;

import io.vertx.core.Vertx;

public class AbstractOrientDBVertexTest implements TestDateHelper {

	protected OrientGraphFactory graphFactory;
	protected OrientDBTxVertexFactory graph;

	@Before
	public void setupDB() {
		graphFactory = new OrientGraphFactory("memory:tinkerpop").setupPool(4, 10);
		graph = new OrientDBTxVertexFactory(graphFactory, Vertx.vertx(), "com.syncleus.ferma.ext.orientdb.model");
	}

}
