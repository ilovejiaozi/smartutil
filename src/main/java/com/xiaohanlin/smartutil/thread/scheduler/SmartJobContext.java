package com.xiaohanlin.smartutil.thread.scheduler;

import java.util.concurrent.TimeUnit;

public class SmartJobContext {

	public final Runnable runnable;
	public final SmartScheduleType scheduleType;
	public final long delay;
	public final long period;
	public final TimeUnit timeUnit;

	public SmartJobContext(Runnable runnable, SmartScheduleType scheduleType, long delay, long period, TimeUnit timeUnit) {
		this.runnable = runnable;
		this.scheduleType = scheduleType;
		this.delay = delay;
		this.period = period;
		this.timeUnit = timeUnit;
	}

}
