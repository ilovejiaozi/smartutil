package com.xiaohanlin.smartutil.eventbus;

import com.xiaohanlin.smartutil.log.SmartLogger;

/**
 * 默认的异常处理为记录日志
 * @author jiaozi
 *
 */
class LoggingHandler implements SmartSubscriberExceptionHandler {
	private SmartLogger logger;

	public LoggingHandler(SmartLogger logger) {
		this.logger = logger;
	}

	@Override
	public void handleException(Throwable exception, SmartSubscriberExceptionContext context) {
		logger.error(message(context), exception);
	}

	private static String message(SmartSubscriberExceptionContext context) {
		return "<<SmartEventBus>>  dispatching event error , subscriber : " + context.getSubscriber() + " , event: " + context.getEvent();
	}
}
