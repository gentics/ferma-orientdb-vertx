package de.jotschi.ferma;

import static de.jotschi.ferma.util.TestUtils.runAndWait;

import org.junit.Test;

import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientElement;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;

public class OrientDBTinkerpopMultithreadingTest extends AbstractOrientDBTest {

	private OrientGraphFactory factory = new OrientGraphFactory("memory:tinkerpop").setupPool(4, 10);

	@Test
	public void testMultithreading() {
		OrientGraph graph = factory.getTx();
		Vertex v2 = graph.addVertex(null);
		Vertex v = graph.addVertex(null);
		graph.commit();

		// Object id = v.getId();
		runAndWait(() -> {
			graph.getRawGraph().activateOnCurrentThread();
			graph.attach((OrientElement) v);
			v.setProperty("sfaf", "dxgvasdg");
			v.addEdge("adadsg", v2);
			// Vertex e = memoryGraph.getVertex(id);
			// assertNotNull(e);
			// assertEquals("marko", e.getProperty("name"));
			// e.setProperty("name", "joe");
			// memoryGraph.rollback();
		});
		// assertEquals("marko", v.getProperty("name"));

	}

}
