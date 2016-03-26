package com.xiaohanlin.smartutil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.RandomUtils;

/**
 * 产生随机数，打乱List顺序,随机取值
 * @author jiaozi
 *
 */
public class SmartRandomUtil extends RandomUtils {
	/**
	 * Returns a random long List within the specified range.
	 * 
	 * @return
	 */
	public static List<Long> randomDiffInRange(final long startInclusive, final long endExclusive, int randomCount) {
		if (randomCount < 1) {
			return Collections.emptyList();
		}

		if (startInclusive >= endExclusive) {
			throw new RuntimeException("<<KuRandomUtil>> randomLong , endExclusive >= startInclusive, " + endExclusive
					+ " >= " + startInclusive);
		}

		if (randomCount >= (endExclusive - startInclusive)) {
			ArrayList<Long> result = new ArrayList<Long>();
			for (long i = startInclusive; i < endExclusive; i++) {
				result.add(i);
			}
			return result;
		} else {
			HashSet<Long> set = new HashSet<Long>(randomCount);
			int i = 0;
			while (i < randomCount) {
				long nextLong = nextLong(startInclusive, endExclusive);
				if (set.add(nextLong)) {
					i++;
				}
			}
			return new ArrayList<Long>(set);
		}
	}

	public static List<Integer> randomDiffInRange(final int startInclusive, final int endExclusive, int randomCount) {
		if (randomCount < 1) {
			return Collections.emptyList();
		}

		if (startInclusive >= endExclusive) {
			throw new RuntimeException("<<KuRandomUtil>> randomLong , endExclusive >= startInclusive, " + endExclusive
					+ " >= " + startInclusive);
		}

		if (randomCount >= (endExclusive - startInclusive)) {
			ArrayList<Integer> result = new ArrayList<Integer>();
			for (int i = startInclusive; i < endExclusive; i++) {
				result.add(i);
			}
			return result;
		} else {
			HashSet<Integer> set = new HashSet<Integer>(randomCount);
			int i = 0;
			while (i < randomCount) {
				int nextLong = nextInt(startInclusive, endExclusive);
				if (set.add(nextLong)) {
					i++;
				}
			}
			return new ArrayList<Integer>(set);
		}
	}

	/**
	 * 随机取一个
	 * 
	 * @param list
	 * @return
	 */
	public static <T> T randomOne(List<T> list) {
		List<T> randomDifferent = randomDiffInRearrange(list, 1);
		if (randomDifferent.isEmpty()) {
			return null;
		}
		return randomDifferent.get(0);
	}

	/**
	 * 打乱顺序，重新排列List, 返回的还是原来的list
	 * 
	 * @param list
	 * @return
	 */
	public static <T> List<T> rearrange(List<T> list) {
		List<T> result = new ArrayList<T>(list);
		int size = list.size(), j = -1;
		T element = null;
		Random random = new Random();
		for (int i = 0; i < size / 2; i++) {
			j = random.nextInt(size - i) + i;
			if (j != i) {
				element = result.get(i);
				result.set(i, result.get(j));
				result.set(j, element);
			}
		}
		return result;
	}

	/**
	 * 打乱顺序，并从中随机选取几个
	 * 
	 * @param list
	 * @param totalRandomCount
	 * @return
	 */
	public static <K> List<K> randomDiffInRearrange(List<K> list, int totalRandomCount) {
		if (CollectionUtils.isEmpty(list) || totalRandomCount < 1) {
			return Collections.emptyList();
		}

		if (totalRandomCount >= list.size()) {
			return rearrange(list);
		}
		Random random = new Random();
		ArrayList<K> res = new ArrayList<K>(totalRandomCount);
		int len = list.size();
		for (int i = 0; i < len; i++) {
			if (random.nextInt(len - i) < totalRandomCount) {
				res.add(list.get(i));
				totalRandomCount--;
				if (totalRandomCount == 0) {
					break;
				}
			}
		}
		return res;
	}

}
