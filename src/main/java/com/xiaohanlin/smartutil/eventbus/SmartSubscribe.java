package com.xiaohanlin.smartutil.eventbus;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 可以设置监听者的优先级
 * 
 * @author zhuokongming
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface SmartSubscribe {
	
	public static final byte DEFAULT_PRIORITY = 0;
	
	/**
	 * 优先级，byte类型，默认是0<br>
	 * 值越大优先级越高,越优先被触发<br>
	 * 同级别按订阅的先后顺序触发
	 * 
	 * @return
	 */
	public byte priority() default DEFAULT_PRIORITY;

	/**
	 * 指定某个eventBus
	 * 
	 * @return
	 */
	public String smartEventBus() default "";
}
