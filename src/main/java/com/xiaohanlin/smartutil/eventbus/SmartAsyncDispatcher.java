package com.xiaohanlin.smartutil.eventbus;

import java.util.Iterator;
import java.util.concurrent.Executor;

import com.xiaohanlin.smartutil.log.SmartLogger;

/**
 * 异步分发
 * @author jiaozi
 *
 */
public class SmartAsyncDispatcher extends SmartEventAbstractDispatcher {

	private Executor executor;

	public SmartAsyncDispatcher(Executor executor, SmartLogger logger) {
		super(logger, new LoggingHandler(logger));
		this.executor = executor;
	}

	public SmartAsyncDispatcher(Executor executor, SmartLogger logger, SmartSubscriberExceptionHandler exceptionHandler) {
		super(logger, exceptionHandler);
		this.executor = executor;
	}

	@Override
	public void dispatch(final Object event,final Iterator<SmartSubscriber> subscribers) {

		executor.execute(new Runnable() {
			@Override
			public void run() {
				SmartSubscriber sbscriber = null;
				while (subscribers.hasNext()) {
					try {
						sbscriber = subscribers.next();
						sbscriber.dispatchEvent(event);
					} catch (Throwable e) {
						try {
							exceptionHandler.handleException(e, new SmartSubscriberExceptionContext(event, sbscriber));
						} catch (Throwable ex) {
							logger.error("<<SmartAsyncDispatcher>>", ex);
						}
					}
				}
			}
		});
	}
}
