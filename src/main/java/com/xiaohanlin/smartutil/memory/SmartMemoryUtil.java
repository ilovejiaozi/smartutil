package com.xiaohanlin.smartutil.memory;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.IdentityHashMap;
import java.util.Stack;

import sun.misc.Unsafe;

/**
 * Usage: MemoryUtil.sizeOf( object ) MemoryUtil.deepSizeOf( object )
 * MemoryUtil.ADDRESS_MODE.
 * 
 * 计算每个对象所占用内容的大小, 对于引用类型的属性会递归寻找下去，直到基本类型
 */
@SuppressWarnings("restriction")
public class SmartMemoryUtil {
	private SmartMemoryUtil() {
	}

	public static enum AddressMode {
		/** Unknown address mode. Size calculations may be unreliable. */
		UNKNOWN,
		/** 32-bit address mode using 32-bit references. */
		MEM_32BIT,
		/** 64-bit address mode using 64-bit references. */
		MEM_64BIT,
		/** 64-bit address mode using 32-bit compressed references. */
		MEM_64BIT_COMPRESSED_OOPS
	}

	/** The detected runtime address mode. */
	public static final AddressMode ADDRESS_MODE;

	private static final Unsafe UNSAFE;

	private static final long ADDRESS_SIZE; // The size in bytes of a native
											// pointer: 4 for 32 bit, 8 for 64
											// bit
	private static final long REFERENCE_SIZE; // The size of a Java reference: 4
												// for 32 bit, 4 for 64 bit
												// compressed oops, 8 for 64 bit
	private static final long OBJECT_BASE_SIZE; // The minimum size of an
												// Object: 8 for 32 bit, 12 for
												// 64 bit compressed oops, 16
												// for 64 bit
	private static final long OBJECT_ALIGNMENT = 8;

	/**
	 * Use the offset of a known field to determine the minimum size of an
	 * object.
	 */
	private static final Object HELPER_OBJECT = new Object() {
		@SuppressWarnings("unused")
		byte b;
	};

	static {
		try {
			// Use reflection to get a reference to the 'Unsafe' object.
			Field f = Unsafe.class.getDeclaredField("theUnsafe");
			f.setAccessible(true);
			UNSAFE = (Unsafe) f.get(null);

			OBJECT_BASE_SIZE = UNSAFE.objectFieldOffset(HELPER_OBJECT.getClass().getDeclaredField("b"));

			ADDRESS_SIZE = UNSAFE.addressSize();
			REFERENCE_SIZE = UNSAFE.arrayIndexScale(Object[].class);

			if (ADDRESS_SIZE == 4) {
				ADDRESS_MODE = AddressMode.MEM_32BIT;
			} else if (ADDRESS_SIZE == 8 && REFERENCE_SIZE == 8) {
				ADDRESS_MODE = AddressMode.MEM_64BIT;
			} else if (ADDRESS_SIZE == 8 && REFERENCE_SIZE == 4) {
				ADDRESS_MODE = AddressMode.MEM_64BIT_COMPRESSED_OOPS;
			} else {
				ADDRESS_MODE = AddressMode.UNKNOWN;
			}
		} catch (Exception e) {
			throw new Error(e);
		}
	}

	/** Return the size of the object excluding any referenced objects. */
	public static long shallowSizeOf(final Object object) {
		Class<?> objectClass = object.getClass();
		if (objectClass.isArray()) {
			// Array size is base offset + length * element size
			long size = UNSAFE.arrayBaseOffset(objectClass) + UNSAFE.arrayIndexScale(objectClass) * Array.getLength(object);
			return padSize(size);
		} else {
			// Object size is the largest field offset padded out to 8 bytes
			long size = OBJECT_BASE_SIZE;
			do {
				for (Field field : objectClass.getDeclaredFields()) {
					if ((field.getModifiers() & Modifier.STATIC) == 0) {
						long offset = UNSAFE.objectFieldOffset(field);
						if (offset >= size) {
							size = offset + 1; // Field size is between 1 and
												// PAD_SIZE bytes. Padding will
												// round up to padding size.
						}
					}
				}
				objectClass = objectClass.getSuperclass();
			} while (objectClass != null);

			return padSize(size);
		}
	}

	private static final long padSize(final long size) {
		return (size + (OBJECT_ALIGNMENT - 1)) & ~(OBJECT_ALIGNMENT - 1);
	}

	/** Return the size of the object including any referenced objects. */
	public static long deepSizeOf(final Object object) {
		IdentityHashMap<Object, Object> visited = new IdentityHashMap<Object, Object>();
		Stack<Object> stack = new Stack<Object>();
		if (object != null)
			stack.push(object);

		long size = 0;
		while (!stack.isEmpty()) {
			size += internalSizeOf(stack.pop(), stack, visited);
		}
		return size;
	}

	private static long internalSizeOf(final Object object, final Stack<Object> stack, final IdentityHashMap<Object, Object> visited) {
		// Scan for object references and add to stack
		Class<?> c = object.getClass();
		if (c.isArray() && !c.getComponentType().isPrimitive()) {
			// Add unseen array elements to stack
			for (int i = Array.getLength(object) - 1; i >= 0; i--) {
				Object val = Array.get(object, i);
				if (val != null && visited.put(val, val) == null) {
					stack.add(val);
				}
			}
		} else {
			// Add unseen object references to the stack
			for (; c != null; c = c.getSuperclass()) {
				for (Field field : c.getDeclaredFields()) {
					if ((field.getModifiers() & Modifier.STATIC) == 0 && !field.getType().isPrimitive()) {
						field.setAccessible(true);
						try {
							Object val = field.get(object);
							if (val != null && visited.put(val, val) == null) {
								stack.add(val);
							}
						} catch (IllegalArgumentException e) {
							throw new RuntimeException(e);
						} catch (IllegalAccessException e) {
							throw new RuntimeException(e);
						}
					}
				}
			}
		}

		return shallowSizeOf(object);
	}
}