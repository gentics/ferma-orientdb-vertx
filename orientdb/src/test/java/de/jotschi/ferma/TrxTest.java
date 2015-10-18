package de.jotschi.ferma;

import static de.jotschi.ferma.util.TestUtils.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import de.jotschi.ferma.model.Person;
import de.jotschi.ferma.orientdb.OrientDBTrxFactory;
import io.vertx.core.AsyncResult;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

public class TrxTest extends AbstractOrientDBTest {

	private static final Logger log = LoggerFactory.getLogger(TrxTest.class);

	@Test
	public void testAsyncTestErrorHandling() throws Exception {
		CompletableFuture<AsyncResult<Object>> fut = new CompletableFuture<>();
		graph.asyncTrx(trx -> {
			trx.fail("blub");
		} , rh -> {
			fut.complete(rh);
		});

		AsyncResult<Object> result = fut.get(5, TimeUnit.SECONDS);
		assertTrue(result.failed());
		assertNotNull(result.cause());
		assertEquals("blub", result.cause().getMessage());
	}

	@Test
	public void testAsyncTestSuccessHandling() throws Exception {
		CompletableFuture<AsyncResult<Object>> fut = new CompletableFuture<>();
		graph.asyncTrx(trx -> {
			trx.complete("test");
		} , rh -> {
			fut.complete(rh);
		});
		AsyncResult<Object> result = fut.get(5, TimeUnit.SECONDS);
		assertTrue(result.succeeded());
		assertNull(result.cause());
		assertEquals("test", result.result());
	}

	@Test
	public void testConcurrentUpdate() throws Exception {
		final int nThreads = 10;
		final int nRuns = 200;

		try (Trx tx2 = graph.trx()) {
			Person person = tx2.getGraph().addFramedVertex(Person.class);
			for (int r = 1; r <= nRuns; r++) {
				final int currentRun = r;
				CountDownLatch latch = new CountDownLatch(nThreads);

				// Start two threads with a retry trx
				for (int i = 0; i < nThreads; i++) {
					final int threadNo = i;
					if (log.isTraceEnabled()) {
						log.trace("Thread [" + threadNo + "] Starting");
					}
					graph.asyncTrx(trx -> {
						manipulatePerson(OrientDBTrxFactory.getThreadLocalGraph(), person);
						trx.complete(person);
					} , rh -> {
						assertEquals(Person.class, rh.result().getClass());
						latch.countDown();
					});
				}

				log.debug("Waiting on lock");
				failingLatch(latch);

				try (Trx tx = graph.trx()) {
					int expect = nThreads * r;
					assertEquals("Expected {" + expect + "} tags since this is the " + r + "th run.", expect, person.getFriends().size());
				}
			}
		}
	}

	// @Test
	// public void testTransaction() throws InterruptedException {
	// AtomicInteger i = new AtomicInteger(0);
	//
	// UserRoot root;
	// try (Trx tx = graph.trx()) {
	// root = person
	// }
	// int e = i.incrementAndGet();
	// try (Trx tx = graph.trx()) {
	// assertNotNull(root.create("testuser" + e, group(), user()));
	// assertNotNull(boot.userRoot().findByUsername("testuser" + e));
	// tx.success();
	// }
	// try (Trx tx = graph.trx()) {
	// assertNotNull(boot.userRoot().findByUsername("testuser" + e));
	// }
	// int u = i.incrementAndGet();
	// Runnable task = () -> {
	// try (Trx tx = graph.trx()) {
	// assertNotNull(root.create("testuser" + u, group(), user()));
	// assertNotNull(boot.userRoot().findByUsername("testuser" + u));
	// tx.failure();
	// }
	// assertNull(boot.userRoot().findByUsername("testuser" + u));
	//
	// };
	// Thread t = new Thread(task);
	// t.start();
	// t.join();
	// try (Trx tx = graph.trx()) {
	// assertNull(boot.userRoot().findByUsername("testuser" + u));
	// System.out.println("RUN: " + i.get());
	// }
	//
	// }

	// @Test
	// public void testMultiThreadedModifications() throws InterruptedException {
	// User user = user();
	//
	// Runnable task2 = () -> {
	// try (Trx tx = graph.trx()) {
	// user.setUsername("test2");
	// assertNotNull(boot.userRoot().findByUsername("test2"));
	// tx.success();
	// }
	// assertNotNull(boot.userRoot().findByUsername("test2"));
	//
	// Runnable task = () -> {
	// try (Trx tx = db.trx()) {
	// user.setUsername("test3");
	// assertNotNull(boot.userRoot().findByUsername("test3"));
	// tx.failure();
	// }
	// assertNotNull(boot.userRoot().findByUsername("test2"));
	// assertNull(boot.userRoot().findByUsername("test3"));
	//
	// };
	// Thread t = new Thread(task);
	// t.start();
	// try {
	// t.join();
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// };
	// Thread t2 = new Thread(task2);
	// t2.start();
	// t2.join();
	// try (Trx tx = graph.trx()) {
	// assertNull(boot.userRoot().findByUsername("test3"));
	// assertNotNull(boot.userRoot().findByUsername("test2"));
	// }
	//
	// }
	//
	@Test
	public void testAsyncTrxFailed() throws Throwable {
		CompletableFuture<AsyncResult<Object>> cf = new CompletableFuture<>();
		graph.asyncTrx(trx -> {
			trx.failed();
		} , rh -> {
			cf.complete(rh);
		});
		assertFalse(cf.get().succeeded());
	}

	@Test(expected = RuntimeException.class)
	public void testAsyncTrxWithError() throws Throwable {
		CompletableFuture<Throwable> cf = new CompletableFuture<>();
		graph.asyncTrx(trx -> {
			throw new RuntimeException("error");
		} , rh -> {
			cf.complete(rh.cause());
		});
		assertEquals("error", cf.get().getMessage());
		throw cf.get();
	}

	@Test(expected = RuntimeException.class)
	public void testAsyncNoTrxWithError() throws Throwable {
		CompletableFuture<Throwable> cf = new CompletableFuture<>();
		graph.asyncNoTrx(noTrx -> {
			throw new RuntimeException("error");
		} , rh -> {
			cf.complete(rh.cause());
		});
		assertEquals("error", cf.get().getMessage());
		throw cf.get();
	}

	@Test
	public void testAsyncNoTrxNestedAsync() throws InterruptedException, ExecutionException {
		CompletableFuture<AsyncResult<Object>> cf = new CompletableFuture<>();
		graph.asyncNoTrx(noTrx -> {
			run(() -> {
				sleep(1000);
				noTrx.complete("OK");
			});
		} , rh -> {
			cf.complete(rh);
		});
		assertTrue(cf.get().succeeded());
		assertEquals("OK", cf.get().result());
	}

	@Test
	public void testAsyncTrxNestedAsync() throws InterruptedException, ExecutionException {
		CompletableFuture<AsyncResult<Object>> cf = new CompletableFuture<>();
		graph.asyncTrx(trx -> {
			run(() -> {
				sleep(1000);
				trx.complete("OK");
			});
		} , rh -> {
			System.out.println("Completed async trx");
			cf.complete(rh);
		});
		assertTrue(cf.get().succeeded());
		assertEquals("OK", cf.get().result());
	}

	@Test
	public void testAsyncNoTrxSuccess() throws Throwable {
		CompletableFuture<AsyncResult<Object>> cf = new CompletableFuture<>();
		graph.asyncNoTrx(noTrx -> {
			noTrx.complete("OK");
		} , rh -> {
			cf.complete(rh);
		});
		assertEquals("OK", cf.get().result());
	}

}
