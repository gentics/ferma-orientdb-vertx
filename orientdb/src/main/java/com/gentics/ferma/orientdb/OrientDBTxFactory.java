package com.gentics.ferma.orientdb;

import com.gentics.ferma.Tx;
import com.gentics.ferma.TxFactory;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;

public class OrientDBTxFactory implements TxFactory {

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
