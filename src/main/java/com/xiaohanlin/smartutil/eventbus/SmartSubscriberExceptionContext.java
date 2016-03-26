package com.xiaohanlin.smartutil.eventbus;

public class SmartSubscriberExceptionContext {
	private final Object event;
	private final Object subscriber;

	SmartSubscriberExceptionContext(Object event, Object subscriber) {
		this.event = event;
		this.subscriber = subscriber;
	}

	public Object getEvent() {
		return event;
	}

	public Object getSubscriber() {
		return subscriber;
	}

}
