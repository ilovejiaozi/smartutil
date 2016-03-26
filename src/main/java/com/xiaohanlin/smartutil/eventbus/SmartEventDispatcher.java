package com.xiaohanlin.smartutil.eventbus;

import java.util.Iterator;

/**
 * 分发器接口， 内部默认实现两种分发机制，异步SmartAsyncDispatcher和同步SmartImmediateDispatcher
 * 
 * @author jiaozi
 */
public interface SmartEventDispatcher {

	public void dispatch(Object event, Iterator<SmartSubscriber> subscribers);

}
