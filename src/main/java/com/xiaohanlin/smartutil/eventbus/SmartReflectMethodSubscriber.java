package com.xiaohanlin.smartutil.eventbus;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class SmartReflectMethodSubscriber implements SmartSubscriber {

	static SmartReflectMethodSubscriber create(SmartEventBus bus, Object listener, Method method) {
		return new SmartReflectMethodSubscriber(bus, listener, method);
	}

	private SmartEventBus bus;

	final Object target;

	private final Method method;

	public SmartReflectMethodSubscriber(SmartEventBus bus, Object target, Method method) {
		this.bus = bus;
		this.target = target;
		this.method = method;
		this.method.setAccessible(true);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((bus == null) ? 0 : bus.hashCode());
		result = prime * result + ((method == null) ? 0 : method.hashCode());
		result = prime * result + ((target == null) ? 0 : target.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SmartReflectMethodSubscriber other = (SmartReflectMethodSubscriber) obj;
		if (bus == null) {
			if (other.bus != null)
				return false;
		} else if (!bus.equals(other.bus))
			return false;
		if (method == null) {
			if (other.method != null)
				return false;
		} else if (!method.equals(other.method))
			return false;
		if (target == null) {
			if (other.target != null)
				return false;
		} else if (!target.equals(other.target))
			return false;
		return true;
	}

	@Override
	public void dispatchEvent(Object event) {
		try {
			method.invoke(target, event);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String getSmartSubscriberDescription() {
		StringBuilder sb = new StringBuilder();
		sb.append("SmartEventBus identify : ").append(this.bus.identifier()).append(" , subscriber class : " + target.getClass().getName());
		return sb.toString();

	}

}
