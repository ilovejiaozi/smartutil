package com.xiaohanlin.smartutil.eventbus;

public interface SmartSubscriber {
	public void dispatchEvent(final Object event);
	
	public String getSmartSubscriberDescription();
}
