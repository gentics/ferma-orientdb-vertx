package com.syncleus.ferma.ext.orientdb;

import com.syncleus.ferma.tx.Tx;
import com.syncleus.ferma.tx.TxFactory;
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
