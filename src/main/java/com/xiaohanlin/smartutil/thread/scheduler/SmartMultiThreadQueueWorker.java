package com.xiaohanlin.smartutil.thread.scheduler;

import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import com.xiaohanlin.smartutil.log.SmartLogger;

/**
 * 多线程队列处理器<BR>
 * 特点：按数据接收顺序处理，但对于同一K值的V数据处理，既保证顺序，也是线程安全的<BR>
 * <B>要求：一个V对象唯一对应一个键值</B><BR>
 * 功能类似扩展线程的处理，实现参考引擎的ServerWriter
 * 
 * @author jiaozi
 */
public class SmartMultiThreadQueueWorker<K, V> implements ISmartControl {

	private final LinkedBlockingQueue<K> keyQueue = new LinkedBlockingQueue<K>();
	private final ConcurrentHashMap<K, ValueQueueWrap<V>> key_ValueQueueWrap = new ConcurrentHashMap<K, ValueQueueWrap<V>>();
	protected final String name;
	private final Thread[] workers;
	private volatile boolean running;
	private final int warnSize;
	private final IProcessor<K, V> processor;
	private final SmartLogger logger;

	public SmartMultiThreadQueueWorker(String name, int threadNum, int warnSize, IProcessor<K, V> processor, SmartLogger logger) {
		this.name = name;
		this.warnSize = warnSize;
		this.processor = processor;
		this.logger = logger;
		workers = new Thread[threadNum];
		running = false;
		for (int i = 0; i < workers.length; i++) {
			String threadName = String.format("%s-Pool%d-Th%d", name, threadNum, i);
			workers[i] = new Thread(new WorkRunnable(), threadName);
			workers[i].setDaemon(true);
		}
	}

	/**
	 * 只能开启一次，关闭后再开会抛异常
	 */
	@Override
	public synchronized void start() {
		if (!running) {
			running = true;
			for (int i = 0; i < workers.length; i++) {
				workers[i].start();
			}
			logger.warn("<SmartMultiThreadQueueWorker> worker " + name + " started, " + workers.length + " thread(s)");
		}
	}

	@Override
	public synchronized void shutdown() {
		if (running) {
			running = false;
			logger.warn("<SmartMultiThreadQueueWorker> shutting down worker" + name);
			for (int i = 0; i < workers.length; i++) {
				workers[i].interrupt();
			}
		}
	}

	@Override
	public boolean isRunning() {
		return running;
	}

	public void accept(K key, V value) {
		ValueQueueWrap<V> valueQueueWrap = key_ValueQueueWrap.get(key);
		if (valueQueueWrap == null) {
			valueQueueWrap = new ValueQueueWrap<V>();
			ValueQueueWrap<V> tmp = key_ValueQueueWrap.putIfAbsent(key, valueQueueWrap);
			if (tmp != null) {
				valueQueueWrap = tmp;
			}
		}
		synchronized (valueQueueWrap) {
			int size = valueQueueWrap.valueQueue.size();
			if (size == 0 && !valueQueueWrap.hasBeenAppendedToKeyQueue) {
				if (keyQueue.offer(key)) {
					valueQueueWrap.valueQueue.addLast(value);
					valueQueueWrap.hasBeenAppendedToKeyQueue = true;
				} else {
					logger.warn(String.format("<SmartMultiThreadQueueWorker> Workers %s, thread : %s , offer fail while accept. key:%s", name, Thread.currentThread().getName(), key));
				}
			} else {
				if (size > 0 && size % warnSize == 0) {
					logger.warn(String.format("<SmartMultiThreadQueueWorker> Workers %s, thread : %s , offer fail while accept. key:%s , size: %s", name, Thread.currentThread().getName(), key, size));
				}
				valueQueueWrap.valueQueue.addLast(value);
			}
		}
	}

	public void remove(K key) {
		key_ValueQueueWrap.remove(key);
	}

	private void process() {
		while (running) {
			try {
				K key = (K) keyQueue.take();
				ValueQueueWrap<V> valueQueueWrap = key_ValueQueueWrap.get(key);
				if (valueQueueWrap != null) {
					V value = null;
					synchronized (valueQueueWrap) {
						value = valueQueueWrap.valueQueue.poll();
					}
					if (value != null) {
						processor.process(key, value);
					}
					synchronized (valueQueueWrap) {
						// 这里才重置hasAddedToKeyQueue，保证对K的处理是单线程的
						valueQueueWrap.hasBeenAppendedToKeyQueue = false;
						if (valueQueueWrap.valueQueue.size() > 0) {
							if (keyQueue.offer(key)) {
								valueQueueWrap.hasBeenAppendedToKeyQueue = true;
							} else {
								logger.warn(String.format("<SmartMultiThreadQueueWorker> Workers{}, thread : {} , offer fail after process.", name, Thread.currentThread().getName()));
							}
						}
					}
				}
			} catch (Throwable t) {
				if (running) {
					logger.error(String.format("<KuMultiThreadQueueWorker> Workers %s, thread : %s , work process error.", name, Thread.currentThread().getName()));
				}
			}
		}
	}

	private class WorkRunnable implements Runnable {
		@Override
		public void run() {
			process();
		}
	}

	public interface IProcessor<K, V> {
		public void process(K key, V value);
	}

	private static class ValueQueueWrap<V> {
		public final LinkedList<V> valueQueue;
		public volatile boolean hasBeenAppendedToKeyQueue;

		public ValueQueueWrap() {
			this.valueQueue = new LinkedList<V>();
			this.hasBeenAppendedToKeyQueue = false;
		}
	}

}
