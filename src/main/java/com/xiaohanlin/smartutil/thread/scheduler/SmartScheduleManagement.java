package com.xiaohanlin.smartutil.thread.scheduler;

import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;

import com.xiaohanlin.smartutil.log.SmartLogger;
import com.xiaohanlin.smartutil.thread.SmartThreadFactory;

public class SmartScheduleManagement {
	private ScheduledExecutorService scheduler;
	private int threadNum;
	private SmartLogger logger;

	public static SmartScheduleManagement create(SmartLogger logger, int threadNum) {
		return new SmartScheduleManagement(logger, threadNum);
	}

	private SmartScheduleManagement(SmartLogger logger, int threadNum) {
		if (threadNum < 1) {
			throw new IllegalArgumentException("<<SmartScheduleManagement>> thread num is negative!");
		}
		this.threadNum = threadNum;
		this.logger = logger;
	}

	public synchronized void start() {
		if (scheduler == null) {
			scheduler = Executors.newScheduledThreadPool(threadNum, SmartThreadFactory.create("<<SmartScheduleManagement>>"));
		}
	}

	public synchronized void shutdown() {
		if (scheduler != null && !scheduler.isShutdown()) {
			scheduler.shutdown();
		}
	}

	/**
	 * 周期任务 或 单次任务
	 */
	public Future<?> register(SmartJobContext jobContext) {
		switch (jobContext.scheduleType) {
		case Once:
			return scheduler.schedule(new ScheduleJob(jobContext.runnable, logger), jobContext.delay, jobContext.timeUnit);
		case WithFixedDelay:
			return scheduler.scheduleWithFixedDelay(new ScheduleJob(jobContext.runnable, logger), jobContext.delay, jobContext.period, jobContext.timeUnit);
		case AtFixedRate:
			return scheduler.scheduleAtFixedRate(new ScheduleJob(jobContext.runnable, logger), jobContext.delay, jobContext.period, jobContext.timeUnit);
		default:
			throw new IllegalArgumentException("SmartScheduleType not exist!");
		}
	}

	private class ScheduleJob implements Runnable {
		private final Runnable runnable;
		private SmartLogger logger;

		private ScheduleJob(Runnable runnable, SmartLogger logger) {
			this.runnable = runnable;
			this.logger = logger;
		}

		@Override
		public void run() {
			try {
				runnable.run();
			} catch (Throwable t) {
				logger.error("<SmartScheduleManagement.ScheduleJob> run error.", t);
			}
		}
	}

}
