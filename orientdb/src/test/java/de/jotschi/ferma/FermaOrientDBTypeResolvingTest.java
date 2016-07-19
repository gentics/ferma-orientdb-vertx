package de.jotschi.ferma;

import org.junit.Test;

import com.syncleus.ferma.VertexFrame;

import de.jotschi.ferma.model.IJob;
import de.jotschi.ferma.model.Job;

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
