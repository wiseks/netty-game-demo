package com.rpg.logic.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.Collection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.FormattingTuple;
import org.slf4j.helpers.MessageFormatter;

public abstract class JsonUtils {
	private static final Logger logger = LoggerFactory.getLogger(JsonUtils.class);

	private static final ObjectMapper mapper = new ObjectMapper();

	private static final TypeFactory typeFactory = TypeFactory.defaultInstance();

	static {
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}

	public static ObjectMapper getObjectMapper() {
		return mapper;
	}

	public static String object2String(Object obj) {
		try {
			return mapper.writeValueAsString(obj);
		} catch (Exception ex) {
			FormattingTuple message = MessageFormatter.format("对象转换成json字符串异常: {}", ex.getMessage());
			logger.error(message.getMessage(), ex);
			return null;
		}
	}

	public static <T> T string2Object(String jsonString, Class<T> valueType) {
		try {
			return mapper.readValue(jsonString, valueType);
		} catch (Exception ex) {
			FormattingTuple message = MessageFormatter.format("json字符串[{}]转换成对象异常: {}", jsonString, ex.getMessage());
			logger.error(message.getMessage(), ex);
			return null;
		}
	}

	public static <T> T string2Object(String jsonString, TypeReference<T> valueTypeRef) {
		try {
			return mapper.readValue(jsonString, valueTypeRef);
		} catch (Exception ex) {
			FormattingTuple message = MessageFormatter.format("json字符串[{}]转换成对象异常: {}", jsonString, ex.getMessage());
			logger.error(message.getMessage(), ex);
			return null;
		}
	}

	public static byte[] object2Bytes(Object obj) {
		try {
			return mapper.writeValueAsBytes(obj);
		} catch (Exception ex) {
			FormattingTuple message = MessageFormatter.format("对象转换成字节数组异常: {}", ex.getMessage());
			logger.error(message.getMessage(), ex);
			return null;
		}
	}

	public static Object bytes2Object(byte[] data, JavaType valueType) {
		if (data == null || data.length == 0) {
			return null;
		}

		try {
			return mapper.readValue(data, 0, data.length, valueType);
		} catch (Exception ex) {
			FormattingTuple message = MessageFormatter.format("字节数组转换成对象异常: {}", ex.getMessage());
			logger.error(message.getMessage(), ex);
			return null;
		}
	}

	public static Object bytes2Object(byte[] data, Type valueType) {
		if (data == null || data.length == 0) {
			return null;
		}

		try {
			return mapper.readValue(data, 0, data.length, typeFactory.constructType(valueType));
		} catch (Exception ex) {
			FormattingTuple message = MessageFormatter.format("字节数组转换成对象异常: {}", ex.getMessage());
			logger.error(message.getMessage(), ex);
			return null;
		}
	}

	public static <T> T bytes2Object(byte[] data, TypeReference<T> valueTypeRef) {
		if (data == null || data.length == 0) {
			return null;
		}

		try {
			return mapper.readValue(data, valueTypeRef);
		} catch (Exception ex) {
			FormattingTuple message = MessageFormatter.format("字节数组转换成对象异常: {}", ex.getMessage());
			logger.error(message.getMessage(), ex);
			return null;
		}
	}

	public static <T> T readValue(InputStream src, JavaType valueType) {
		try {
			return mapper.readValue(src, valueType);
		} catch (Exception ex) {
			FormattingTuple message = MessageFormatter.format("输入流转换成对象异常: {}", ex.getMessage());
			logger.error(message.getMessage(), ex);
			return null;
		}
	}

	public static CollectionType constructCollectionType(Class<? extends Collection<?>> collectionClass,
			Class<?> elementClass) {
		return typeFactory.constructCollectionType(collectionClass, elementClass);
	}
}
