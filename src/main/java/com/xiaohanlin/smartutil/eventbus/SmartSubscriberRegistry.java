package com.xiaohanlin.smartutil.eventbus;


import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;

import com.xiaohanlin.smartutil.SmartStringUtil;

/**
 * 管理所有订阅者,Subscribers<br>
 * 因为有优先级，所有使用ConcurrentSkipListMap<br>
 * 
 * @author jiaozi
 *
 */
final class SmartSubscriberRegistry {
	private static final short MAX_PRORITY = Byte.MAX_VALUE + 1;
	private final ConcurrentHashMap<Class<?>, ConcurrentSkipListMap<Short, List<SmartSubscriber>>> eventTypeSubscribers = new ConcurrentHashMap<Class<?>, ConcurrentSkipListMap<Short, List<SmartSubscriber>>>();
	private final SmartEventBus bus;

	SmartSubscriberRegistry(SmartEventBus bus) {
		this.bus = bus;
	}

	// ==========反射==========
	final synchronized void registerByReflect(Object listener) {
		HashMap<Short, List<Method>> prioritySubscribers = getAnnotatedMethods(listener);
		for (Map.Entry<Short, List<Method>> en : prioritySubscribers.entrySet()) {
			short priotity = en.getKey();
			for (Method m : en.getValue()) {
				Class<?> eventType = m.getParameters()[0].getType();
				List<SmartSubscriber> sortedMapSubscribers = getEventTypeSubscribers(eventType, priotity);
				sortedMapSubscribers.add(SmartReflectMethodSubscriber.create(bus, listener, m));
			}
		}
	}

	final synchronized void unregisterByReflect(Object listener) {
		HashMap<Short, List<Method>> prioritySubscribers = getAnnotatedMethods(listener);
		for (Map.Entry<Short, List<Method>> en : prioritySubscribers.entrySet()) {
			short priotity = en.getKey();
			for (Method m : en.getValue()) {
				Class<?> eventType = m.getParameterTypes()[0];
				List<SmartSubscriber> sortedMapSubscribers = getEventTypeSubscribers(eventType, priotity);
				sortedMapSubscribers.remove(SmartReflectMethodSubscriber.create(bus, listener, m));
			}
		}
	}

	// =========接口interface==========

	final synchronized void registerByInterface(SmartSubscriber subscriber, Class<?> eventClass, byte priority) {
		short finalPriority = (short) (MAX_PRORITY - priority);
		List<SmartSubscriber> kuSubscriberList = getEventTypeSubscribers(eventClass, finalPriority);
		kuSubscriberList.add(subscriber);
	}

	final synchronized void unregisterByInterface(SmartSubscriber kuSubscriber, Class<?> eventClass) {
		unregisterWithoutPriorty(kuSubscriber, eventClass);
	}

	final synchronized void unregisterByInterface(SmartSubscriber kuSubscriber, Class<?> eventClass, byte priority) {
		short finalPriority = (short) (MAX_PRORITY - priority);
		unregisterWithPriorty(kuSubscriber, eventClass, finalPriority);
	}

	// ==========内部其他方法==========

	private void unregisterWithoutPriorty(SmartSubscriber subscriber, Class<?> eventClass) {
		ConcurrentSkipListMap<Short, List<SmartSubscriber>> sortedMap = eventTypeSubscribers.get(eventClass);
		if (sortedMap == null) {
			return;
		}
		for (List<SmartSubscriber> subscribersList : sortedMap.values()) {
			if (subscribersList.remove(subscriber)) {
				return;
			}
		}
	}

	private void unregisterWithPriorty(SmartSubscriber subscriber, Class<?> eventClass, short proprity) {
		ConcurrentSkipListMap<Short, List<SmartSubscriber>> sortedMap = eventTypeSubscribers.get(eventClass);
		if (sortedMap == null) {
			return;
		}

		List<SmartSubscriber> list = sortedMap.get(proprity);
		if (list == null) {
			return;
		}
		list.remove(subscriber);
	}

	private List<SmartSubscriber> getEventTypeSubscribers(Class<?> eventType, short priority) {
		ConcurrentSkipListMap<Short, List<SmartSubscriber>> sortedMap = eventTypeSubscribers.get(eventType);
		if (sortedMap == null) {
			sortedMap = new ConcurrentSkipListMap<Short, List<SmartSubscriber>>();
			eventTypeSubscribers.put(eventType, sortedMap);
		}
		List<SmartSubscriber> list = sortedMap.get(priority);
		if (list == null) {
			list = new ArrayList<SmartSubscriber>();
			sortedMap.put(priority, list);
		}
		return list;
	}

	private HashMap<Short, List<Method>> getAnnotatedMethods(Object listener) {
		HashMap<Short, List<Method>> priorityMethods = new HashMap<Short, List<Method>>();
		for (Class<?> clazz = listener.getClass(); clazz != null && clazz != Object.class; clazz = clazz.getSuperclass()) {
			Method[] declaredMethods = clazz.getDeclaredMethods();
			if (declaredMethods != null) {
				for (Method method : declaredMethods) {
					if (method.isAnnotationPresent(SmartSubscribe.class)) {
						SmartSubscribe[] annotations = method.getAnnotationsByType(SmartSubscribe.class);
						// 指定了某个特定的EventBus的identifier
						String busidentifier = annotations[0].smartEventBus();
						if (SmartStringUtil.isNotEmpty(busidentifier) && !SmartStringUtil.equals(busidentifier, this.bus.identifier())) {
							continue;
						}

						short priority = (short) (MAX_PRORITY - annotations[0].priority());
						List<Method> list = priorityMethods.get(priority);
						if (list == null) {
							list = new ArrayList<Method>();
							priorityMethods.put(priority, list);
						}
						list.add(method);
					}
				}
			}
		}
		return priorityMethods;
	}

	final Iterator<SmartSubscriber> getSubscribers(Class<?> clazz) {
		ConcurrentSkipListMap<Short, List<SmartSubscriber>> concurrentSkipListMap = eventTypeSubscribers.get(clazz);
		if (concurrentSkipListMap == null || concurrentSkipListMap.isEmpty()) {
			return Collections.emptyIterator();
		}
		ArrayList<SmartSubscriber> list = new ArrayList<SmartSubscriber>(concurrentSkipListMap.size());
		for (Map.Entry<Short, List<SmartSubscriber>> entry : concurrentSkipListMap.entrySet()) {
			list.addAll(entry.getValue());
		}
		return list.iterator();
	}
}
