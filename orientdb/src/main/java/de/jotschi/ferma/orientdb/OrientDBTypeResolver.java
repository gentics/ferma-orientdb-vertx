package de.jotschi.ferma.orientdb;

import java.util.Set;

import com.syncleus.ferma.VertexFrame;
import com.syncleus.ferma.traversals.EdgeTraversal;
import com.syncleus.ferma.traversals.TraversalFunction;
import com.syncleus.ferma.traversals.VertexTraversal;
import com.syncleus.ferma.typeresolvers.TypeResolver;
import com.tinkerpop.blueprints.Element;
import com.tinkerpop.blueprints.impls.orient.OrientEdge;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;
import com.tinkerpop.gremlin.Tokens;

public class OrientDBTypeResolver implements TypeResolver {

	@Override
	public <T> Class<? extends T> resolve(Element element, Class<T> kind) {
		System.out.println("Resolve " + kind.getName());
		if (element instanceof OrientVertex) {
			OrientVertex orientVertex = (OrientVertex) element;
			String name = orientVertex.getType().getName();
			try {
				Class<T> classOfT = (Class<T>) Class.forName("de.jotschi.ferma.model." + name);
				System.out.println("Resolved to: " + classOfT.getName());
				return classOfT;
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		if (element instanceof OrientEdge) {
			OrientEdge orientEdge = (OrientEdge) element;
			String name = orientEdge.getType().getName();
		}
		System.out.println(element.getClass());
		// TODO Auto-generated method stub
		return kind;
	}

	@Override
	public Class<?> resolve(Element element) {
		if (element instanceof OrientVertex) {
			OrientVertex orientVertex = (OrientVertex) element;
			String name = orientVertex.getType().getName();
			try {
				Class<?> classOfT = Class.forName("de.jotschi.ferma.model." + name);
				System.out.println("Resolved to: " + classOfT.getName());
				return classOfT;
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		if (element instanceof OrientEdge) {
			OrientEdge orientEdge = (OrientEdge) element;
			String name = orientEdge.getType().getName();
		}
		return null;
	}

	@Override
	public void init(Element element, Class<?> kind) {

	}

	@Override
	public void deinit(Element element) {
		System.out.println("DeInit");
	}

	@Override
	public VertexTraversal<?, ?, ?> hasType(VertexTraversal<?, ?, ?> traverser, Class<?> type) {
		System.out.println("TYPE:" + type);
		return traverser.filter(vertex -> {
			Class<?> vertexType = resolve(vertex.getElement());
			if (vertexType == type) {
				return true;
			}
			return false;
		});
	}

	@Override
	public EdgeTraversal<?, ?, ?> hasType(EdgeTraversal<?, ?, ?> traverser, Class<?> type) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public VertexTraversal<?, ?, ?> hasNotType(VertexTraversal<?, ?, ?> traverser, Class<?> type) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EdgeTraversal<?, ?, ?> hasNotType(EdgeTraversal<?, ?, ?> traverser, Class<?> type) {
		// TODO Auto-generated method stub
		return null;
	}

}
