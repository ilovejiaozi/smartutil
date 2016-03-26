package com.xiaohanlin.smartutil;

import java.util.Map;

import org.apache.commons.collections.CollectionUtils;

public class SmartCollectionUtil extends CollectionUtils {
	public static <K, V> boolean isEmptyMap(Map<K, V> map) {
		if (map == null || map.size() == 0) {
			return true;
		}
		return false;
	}

	public static <K, V> boolean isNotEmptyMap(Map<K, V> map) {
		return !isEmptyMap(map);
	}
}
