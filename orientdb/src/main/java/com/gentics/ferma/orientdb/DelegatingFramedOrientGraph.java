package com.gentics.ferma.orientdb;

import com.syncleus.ferma.typeresolvers.TypeResolver;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;

public class DelegatingFramedOrientGraph<G extends OrientGraphNoTx> extends AbstractDelegatingFramedOrientGraph<G> {

	public DelegatingFramedOrientGraph(final G delegate, final TypeResolver typeResolver) {
		super(delegate, typeResolver);
	}

}
