package com.syncleus.ferma.ext.orientdb;

import org.junit.Test;

import com.syncleus.ferma.VertexFrame;
import com.syncleus.ferma.ext.orientdb.model.IJob;
import com.syncleus.ferma.ext.orientdb.model.Job;
import com.syncleus.ferma.tx.Tx;

public class FermaOrientDBTypeResolvingTest extends AbstractOrientDBTest {

	@Test
	public void testCasting() {
		try (Tx tx = graph.tx()) {
			Job jobCTO = tx.getGraph().addFramedVertex(Job.class);
			jobCTO.setName("Chief Technology Officer");
			
			VertexFrame frame = tx.getGraph().v().has(Job.class).next();
			System.out.println(frame.getClass().getName());

			//IJob job = (IJob) fg.v().has(Job.class).nextOrDefaultExplicit(Job.class, null);
			IJob job = tx.getGraph().v().has(Job.class).nextOrDefaultExplicit(Job.class, null);
			System.out.println(job.getName());
		}
	}

}
