package com.xiaohanlin.smartutil.eventbus;

import java.util.Iterator;

import com.xiaohanlin.smartutil.log.SmartLogger;

/**
 * 同步的分发器
 * @author jiaozi
 *
 */
public class SmartImmediateDispatcher extends SmartEventAbstractDispatcher {
	SmartImmediateDispatcher(SmartLogger logger, SmartSubscriberExceptionHandler exceptionHandler) {
		super(logger, exceptionHandler);
	}

	SmartImmediateDispatcher(SmartLogger logger) {
		super(logger, new LoggingHandler(logger));
	}

	@Override
	public void dispatch(Object event, Iterator<SmartSubscriber> subscribers) {
		SmartSubscriber subscriber = null;
		while (subscribers.hasNext()) {
			try {
				subscriber = subscribers.next();
				subscriber.dispatchEvent(event);
			} catch (Throwable e) {
				try {
					exceptionHandler.handleException(e, new SmartSubscriberExceptionContext(event, subscriber));
				} catch (Throwable ex) {
					logger.error("<<SmartImmediateDispatcher>>", ex);
				}
			}
		}
	}
}

