package com.xiaohanlin.smartutil.eventbus;

public interface SmartSubscriberExceptionHandler {

  void handleException(Throwable exception, SmartSubscriberExceptionContext context);
}
