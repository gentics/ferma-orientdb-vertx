package com.orientechnologies.orient.core.exception;

import static com.syncleus.ferma.ext.orientdb.util.TestUtils.run;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

import com.gentics.ferma.ext.orientdb.vertx.AbstractOrientDBVertexTest;
import com.syncleus.ferma.ext.orientdb.model.Person;
import com.syncleus.ferma.tx.Tx;

import io.vertx.core.AsyncResult;

public class OrientDBTxTest extends AbstractOrientDBVertexTest {

	private Person p;

	@Test
	public void testAsyncTxRetryHandling() throws Exception {
		CompletableFuture<AsyncResult<Object>> fut = new CompletableFuture<>();
		AtomicInteger e = new AtomicInteger(0);
		graph.asyncTx(tx -> {
			e.incrementAndGet();
			if (e.get() == 1) {
				String msg = "Cannot UPDATE the record #13:8 because the version is not the latest. Probably you are updating an old record or it has been modified by another user (db=v7 your=v6)";
				// "test #9:1 blub adsd"
				throw new OConcurrentModificationException(msg);
			} else {
				tx.complete("OK");
			}
		}, rh -> {
			fut.complete(rh);
		});
		AsyncResult<Object> result = fut.get(5, TimeUnit.SECONDS);
		assertEquals(2, e.get());
		assertEquals("OK", result.result());
		assertTrue(result.succeeded());
		assertNull(result.cause());
	}

	@Test
	public void testAsyncTxRetryHandling2() throws Exception {
		// Test creation of user in current thread
		int nFriendsBefore;
		try (Tx tx = graph.tx()) {
			p = addPersonWithFriends(tx.getGraph(), "Person2");
			manipulatePerson(tx.getGraph(), p);
			tx.success();
			nFriendsBefore = p.getFriends().size();
		}

		CyclicBarrier b = new CyclicBarrier(3);
		AtomicInteger i = new AtomicInteger(0);

		run(() -> {
			graph.tx(tx -> {
				i.incrementAndGet();

				System.out.println("Tx1");
				addFriend(Tx.getActive().getGraph(), p);
				tx.complete();
				if (i.get() <= 2) {
					b.await(1, TimeUnit.SECONDS);
				}
			}, rh -> {
				System.out.println("Completed");
			});
		});

		run(() -> {
			graph.tx(tx -> {
				i.incrementAndGet();

				System.out.println("Tx2");
				addFriend(Tx.getActive().getGraph(), p);
				tx.complete();
				if (i.get() <= 2) {
					b.await(1, TimeUnit.SECONDS);
				}
			}, rh -> {
				System.out.println("Completed");
			});
		});

		b.await();
		Thread.sleep(1000);
		System.out.println("Asserting");
		try (Tx tx = graph.tx()) {
			p = tx.getGraph().getFramedVertexExplicit(Person.class, p.getId());
			int nFriendsAfter = p.getFriends().size();
			assertEquals(nFriendsBefore + 2, nFriendsAfter);
		}

	}
}
