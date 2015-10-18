package de.jotschi.ferma.util;

import static org.junit.Assert.fail;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class TestUtils {

	public static void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static void runAndWait(Runnable runnable) {
		Thread thread = run(runnable);
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("Done waiting");
	}

	public static Thread run(Runnable runnable) {
		Thread thread = new Thread(runnable);
		thread.start();
		return thread;
	}

	public static void failingLatch(CountDownLatch latch) throws Exception {
		if (!latch.await(10, TimeUnit.SECONDS)) {
			fail("Latch timeout reached");
		}
	}

}
