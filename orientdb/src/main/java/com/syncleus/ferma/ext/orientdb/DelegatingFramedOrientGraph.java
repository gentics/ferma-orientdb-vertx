package com.syncleus.ferma.ext.orientdb;

import com.syncleus.ferma.ClassInitializer;
import com.syncleus.ferma.DefaultClassInitializer;
import com.syncleus.ferma.DelegatingFramedTransactionalGraph;
import com.syncleus.ferma.VertexFrame;
import com.syncleus.ferma.WrapperFramedTransactionalGraph;
import com.syncleus.ferma.typeresolvers.TypeResolver;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;

public class DelegatingFramedOrientGraph extends DelegatingFramedTransactionalGraph<OrientGraph>
		implements WrapperFramedTransactionalGraph<OrientGraph> {

	public DelegatingFramedOrientGraph(OrientGraph delegate, TypeResolver typeResolver) {
		super(delegate, typeResolver);
	}

	@Override
	public <T> T addFramedVertex(Object id, final ClassInitializer<T> initializer) {
		return frameNewElement(this.getBaseGraph().addVertex(id), initializer);
	}

	@Override
	public <T> T addFramedEdge(Object id, VertexFrame source, VertexFrame destination, String label, ClassInitializer<T> initializer) {
		return frameNewElement(this.getBaseGraph().addEdge(id, source.getElement(), destination.getElement(), label), initializer);
	}

	@Override
	public <T> T addFramedVertex(final Class<T> kind) {
		return this.addFramedVertex("class:" + kind.getSimpleName(), new DefaultClassInitializer<>(kind));
	}

	@Override
	public <T> T addFramedEdge(VertexFrame source, VertexFrame destination, String label, Class<T> kind) {
		return super.addFramedEdge(source, destination, label, kind);
	}

	@Override
	public void stopTransaction(Conclusion conclusion) {
		getBaseGraph().stopTransaction(conclusion);
	}

	@Override
	public void commit() {
		getBaseGraph().commit();
	}

	@Override
	public void rollback() {
		getBaseGraph().rollback();
	}

}
