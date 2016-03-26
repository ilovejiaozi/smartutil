package com.xiaohanlin.smartutil.thread.scheduler;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import com.xiaohanlin.smartutil.log.SmartLogger;

/**
 * 多线程数据定时循环处理器<BR>
 * 可以通过参数的调整，调整适宜的线程数处理数据 
 * @author jiaozi
 */
public class SmartMultiThreadLoopWorker<K, V> implements ISmartControl {

	private final String name;
	private final int divideMapNum;
	private final int threadNum;
	private final long threadInterval;
	private final long processPerformance;
	private final IProcessor<K, V> processor;

	private volatile boolean running;
	private final ArrayList<ConcurrentHashMap<K, V>> mapList;// 自维护队列
	private final Thread[] threads;
	private final SmartLogger logger;

	public SmartMultiThreadLoopWorker(String name, int divideMapNum, int threadNum, long threadInterval, long processPerformance, IProcessor<K, V> processor, SmartLogger logger) {
		this.name = name;
		this.divideMapNum = divideMapNum;
		this.threadNum = Math.min(threadNum, divideMapNum);
		this.threadInterval = threadInterval;
		this.processPerformance = processPerformance;
		this.processor = processor;
		this.logger = logger;
		running = false;

		mapList = new ArrayList<ConcurrentHashMap<K, V>>(divideMapNum);
		for (int i = 0; i < divideMapNum; i++) {
			mapList.add(new ConcurrentHashMap<K, V>());
		}
		threads = new Thread[threadNum];
		for (int i = 0; i < threads.length; i++) {
			String threadName = String.format("%s-Pool%d-%d", name, threadNum, i);
			threads[i] = new Thread(new MyRunnable(i), threadName);
			threads[i].setDaemon(true);
		}
	}

	/**
	 * 只能开启一次，关闭后再开会抛异常
	 */
	@Override
	public synchronized void start() {
		if (!running) {
			running = true;
			for (int i = 0; i < threads.length; i++) {
				threads[i].start();
			}
			logger.warn(String.format("<<SmartMultiThreadLoopWorker>> worker %s started, %s thread(s)", name, threads.length));
		}
	}

	@Override
	public synchronized void shutdown() {
		if (running) {
			running = false;
			logger.warn(String.format("<<SmartMultiThreadLoopWorker>> shutting down worker{}", name));
			for (int i = 0; i < threads.length; i++) {
				threads[i].interrupt();
			}
		}
	}

	@Override
	public boolean isRunning() {
		return running;
	}

	public V putIfAbsent(K key, V value) {
		int index = Math.abs(key.hashCode()) % divideMapNum;
		ConcurrentHashMap<K, V> map = mapList.get(index);
		return map.putIfAbsent(key, value);
	}

	public V put(K key, V value) {
		int index = Math.abs(key.hashCode()) % divideMapNum;
		ConcurrentHashMap<K, V> map = mapList.get(index);
		return map.put(key, value);
	}

	public V get(K key) {
		int index = Math.abs(key.hashCode()) % divideMapNum;
		ConcurrentHashMap<K, V> map = mapList.get(index);
		return map.get(key);
	}

	public V remove(K key) {
		int index = Math.abs(key.hashCode()) % divideMapNum;
		ConcurrentHashMap<K, V> map = mapList.get(index);
		return map.remove(key);
	}

	private class MyRunnable implements Runnable {
		private final ArrayList<ConcurrentHashMap<K, V>> list = new ArrayList<ConcurrentHashMap<K, V>>();

		private MyRunnable(int index) {
			for (int i = index; i < mapList.size(); i += threadNum) {
				list.add(mapList.get(i));
			}
		}

		public void run() {
			while (running) {
				long startTime = System.currentTimeMillis();
				int entryCount = 0;
				for (int i = 0; i < list.size() && running; i++) {
					ConcurrentHashMap<K, V> map = list.get(i);
					entryCount += map.size();
					for (Iterator<Entry<K, V>> it = map.entrySet().iterator(); it.hasNext() && running;) {
						Entry<K, V> entry = it.next();
						try {
							K key = entry.getKey();
							V value = entry.getValue();
							if (value == null || processor.process(key, value)) {
								it.remove();
							}
						} catch (Throwable t) {
							if (running) {
								logger.error(String.format("<SmartMultiThreadLoopWorker> LoopWorker %s ,thread:%s, process error.", t, name,Thread.currentThread().getName()));
							}
						}
					}
				}
				long costTime = System.currentTimeMillis() - startTime;
				if (costTime > processPerformance) {
					logger.warn(String.format("<SmartMultiThreadLoopWorker> LoopWorker %s, thread:%s, use time:%s ms entry count:%s", name, Thread.currentThread().getName(), costTime, entryCount));
				}
				try {
					Thread.sleep(threadInterval);
				} catch (InterruptedException e) {
					// 这里报错不用记日志
					e.printStackTrace();
				}
			}
		}
	}

	public interface IProcessor<K, V> {
		/**
		 * @return true:将从处理队列中移除key对应数据
		 */
		public boolean process(K key, V value);
	}
}
