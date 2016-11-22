package com.gentics.ferma;

import org.junit.Test;

import com.gentics.ferma.model.Job;
import com.tinkerpop.blueprints.impls.orient.OrientEdge;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;

public class OrientDBTest {

	protected OrientGraphFactory graphFactory = new OrientGraphFactory("memory:tinkerpop").setupPool(4, 10);

	@Test
	public void testName() throws Exception {
		OrientGraphNoTx noTx = graphFactory.getNoTx();

		OrientVertex job = noTx.addVertex("class:Job");
		OrientVertex person = noTx.addVertex("class:Person");
		OrientVertex person2 = noTx.addVertex("class:Person");
		OrientEdge edge = (OrientEdge) job.addEdge("HAS_JOB", job);
		OrientEdge edge2 = (OrientEdge) job.addEdge("HAS_JOB", job);
		edge2.setProperty("type", Job.class.getSimpleName());
		long start = System.currentTimeMillis();
		for (int i = 0; i < 1000000; i++) {
			edge2.reload();
			//edge2.getProperty("type");
			 edge2.getType().getName();
			// edge2.getType().getSuperClass().getName();
		}
		System.out.println("Duration: " + (System.currentTimeMillis() - start));
		noTx.shutdown();

	}
}
