package com.xiaohanlin.smartutil;

import java.io.Serializable;

import org.apache.commons.lang3.SerializationUtils;

/**
 * 继承自apache common的序列化工具类，并支持null的序列化为null
 * 
 * @author jiaozi
 *
 */
public class SmartSerializationUtil extends SerializationUtils {
	public static <T> T deserializeEnabledNull(final byte[] objectData) {
		if (objectData == null) {
			return null;
		}
		return deserialize(objectData);
	}

	public static byte[] serializeEnabledNull(final Serializable obj) {
		if (obj == null) {
			return null;
		}
		return serialize(obj);
	}
}
