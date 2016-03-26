package com.xiaohanlin.smartutil.thread.scheduler;

import java.util.Collection;
import java.util.concurrent.CountDownLatch;

public class SmartCountDownLatchExecutor {
	private CountDownLatch latch;
	private Collection<? extends Runnable> list;

	public SmartCountDownLatchExecutor(Collection<? extends Runnable> list) {
		if (list == null) {
			throw new IllegalArgumentException();
		}
		this.list = list;
		latch = new CountDownLatch(list.size());
	}

	public void start() {
		for (Runnable r : list) {
			new Thread(new CountDownLatchRunnable(r, latch)).start();
		}
	}

	public void await() {
		try {
			this.latch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
			throw new Error(e);
		}
	}
	
	private class CountDownLatchRunnable implements Runnable {
		private final Runnable r;
		private final CountDownLatch latch;

		public CountDownLatchRunnable(Runnable r, CountDownLatch latch) {
			this.r = r;
			this.latch = latch;
		}

		@Override
		public void run() {
			try {
				r.run();
			} finally {
				latch.countDown();
			}
		}
	}
}
