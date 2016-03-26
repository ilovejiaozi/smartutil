package com.xiaohanlin.smartutil.thread.scheduler;

/**
 * 
 * @author jiaozi
 *
 */
public enum SmartScheduleType {

	/** 定时只调度一次 */
	Once,
	/** 设定了相邻两次执行的结束和开始的最小间隔 */
	WithFixedDelay,
	/**
	 * 设定了相邻两次执行的开始时间最小间隔。<br>
	 * 如果每次执行很耗时，接近甚至超过间隔时间，则可能会将CPU时间片抢占完。<br>
	 * 所以从CPU负载方面考虑，不推荐使用这种定时调度。
	 */
	AtFixedRate,
	
	
	;

}
