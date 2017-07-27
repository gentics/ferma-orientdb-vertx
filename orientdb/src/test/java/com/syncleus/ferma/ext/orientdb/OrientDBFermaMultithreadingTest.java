package com.syncleus.ferma.ext.orientdb;

import static com.syncleus.ferma.ext.orientdb.util.TestUtils.runAndWait;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.Test;

import com.syncleus.ferma.VertexFrame;
import com.syncleus.ferma.ext.orientdb.model.Person;
import com.syncleus.ferma.tx.Tx;

public class OrientDBFermaMultithreadingTest extends AbstractOrientDBTest {

	Person p;

	@Test
	public void testCyclicBarrier() throws InterruptedException, BrokenBarrierException {
		int nThreads = 3;
		CyclicBarrier barrier = new CyclicBarrier(nThreads);
		for (int i = 0; i < nThreads; i++) {
			Thread.sleep(1000);
			TestThread t = new TestThread(i, barrier);
			t.start();
		}
		Thread.sleep(4000);
	}

	@Test
	public void testMultithreading() {
		try (Tx tx = graph.tx()) {
			p = addPersonWithFriends(tx.getGraph(), "SomePerson");
			p.setName("joe");
			tx.success();
		}
		runAndWait(() -> {
			try (Tx tx = graph.tx()) {
				manipulatePerson(tx.getGraph(), p);
			}
		});
	}

	@Test
	public void testOrientThreadedTransactionalGraphWrapper() {

		// Test creation of user in current thread
		try (Tx tx = graph.tx()) {
			Person p = addPersonWithFriends(tx.getGraph(), "Person2");
			manipulatePerson(tx.getGraph(), p);
			tx.success();
		}

		AtomicReference<Person> reference = new AtomicReference<>();
		runAndWait(() -> {
			try (Tx tx = graph.tx()) {
				manipulatePerson(tx.getGraph(), p);
			}
			try (Tx tx = graph.tx()) {
				Person p2 = addPersonWithFriends(tx.getGraph(), "Person3");
				tx.success();
				reference.set(p2);
			}
			runAndWait(() -> {
				try (Tx tx = graph.tx()) {
					manipulatePerson(tx.getGraph(), p);
				}
			});
		});

		try (Tx tx = graph.tx()) {
			for (VertexFrame vertex : tx.getGraph().v().toList()) {
				System.out.println(vertex.toString());
			}
		}
		// try (Tx tx = db.tx()) {
		// manipulatePerson(tx.getGraph(), reference.get());
		// }
	}

}
