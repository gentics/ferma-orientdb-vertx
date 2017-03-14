package com.gentics.ferma;

import org.junit.Test;

import com.gentics.ferma.Trx;
import com.gentics.ferma.model.IJob;
import com.gentics.ferma.model.Job;
import com.syncleus.ferma.VertexFrame;

public class FermaOrientDBTypeResolvingTest extends AbstractOrientDBTest {

	@Test
	public void testCasting() {
		try (Trx tx = graph.trx()) {
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
