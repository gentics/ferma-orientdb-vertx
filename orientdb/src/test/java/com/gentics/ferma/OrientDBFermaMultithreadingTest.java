package com.gentics.ferma;

import static com.gentics.ferma.util.TestUtils.runAndWait;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.Test;

import com.gentics.ferma.Trx;
import com.gentics.ferma.model.Person;
import com.syncleus.ferma.VertexFrame;

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
		try (Trx tx = graph.trx()) {
			p = addPersonWithFriends(tx.getGraph(), "SomePerson");
			p.setName("joe");
			tx.success();
		}
		runAndWait(() -> {
			try (Trx tx = graph.trx()) {
				manipulatePerson(tx.getGraph(), p);
			}
		});
	}

	@Test
	public void testOrientThreadedTransactionalGraphWrapper() {

		// Test creation of user in current thread
		try (Trx tx = graph.trx()) {
			Person p = addPersonWithFriends(tx.getGraph(), "Person2");
			manipulatePerson(tx.getGraph(), p);
			tx.success();
		}

		AtomicReference<Person> reference = new AtomicReference<>();
		runAndWait(() -> {
			try (Trx tx = graph.trx()) {
				manipulatePerson(tx.getGraph(), p);
			}
			try (Trx tx = graph.trx()) {
				Person p2 = addPersonWithFriends(tx.getGraph(), "Person3");
				tx.success();
				reference.set(p2);
			}
			runAndWait(() -> {
				try (Trx tx = graph.trx()) {
					manipulatePerson(tx.getGraph(), p);
				}
			});
		});

		try (Trx tx = graph.trx()) {
			for (VertexFrame vertex : tx.getGraph().v().toList()) {
				System.out.println(vertex.toString());
			}
		}
		// try (Trx tx = db.trx()) {
		// manipulatePerson(tx.getGraph(), reference.get());
		// }
	}

}
