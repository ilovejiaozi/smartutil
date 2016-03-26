package com.xiaohanlin.smartutil.eventbus;

import java.util.concurrent.Executor;

import com.xiaohanlin.smartutil.log.SmartLogger;

/**
 * 抽象基类,定义基本的属性
 * @author jiaozi
 *
 */
public abstract class SmartEventAbstractDispatcher implements SmartEventDispatcher {
	protected SmartLogger logger;
	protected SmartSubscriberExceptionHandler exceptionHandler;

	public SmartEventAbstractDispatcher(SmartLogger logger, SmartSubscriberExceptionHandler exceptionHandler) {
		this.logger = logger;
		this.exceptionHandler = exceptionHandler;
	}
	
	/**
	 * 异步分发
	 * 
	 * @return
	 */
	public static SmartEventDispatcher Async(Executor executor, SmartLogger logger) {
		return new SmartAsyncDispatcher(executor, logger);
	}

	public static SmartEventDispatcher Async(Executor executor, SmartLogger logger, SmartSubscriberExceptionHandler exceptionHandler) {
		return new SmartAsyncDispatcher(executor, logger, exceptionHandler);
	}

	/**
	 * 同步分发
	 * 
	 * @author zhuokongming
	 */
	public static SmartEventDispatcher Immediate(SmartLogger logger, SmartSubscriberExceptionHandler exceptionHandler) {
		return new SmartImmediateDispatcher(logger, exceptionHandler);
	}

	public static SmartEventDispatcher Immediate(SmartLogger logger) {
		return new SmartImmediateDispatcher(logger);
	}
}
