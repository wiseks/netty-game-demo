package com.rpg.framework.util;


import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.Reader;
import java.lang.reflect.Type;

public final class GsonUtil {
	private static Gson gson = new Gson();

	public static String beanToJson(Object bean) {
		return gson.toJson(bean);
	}

	/**
	 * 转成数值表前端可以用的json字符串
	 * 
	 * @param bean
	 * @return
	 */
	public static String beanToParsedJson(Object bean) {
		String json = gson.toJson(bean);
		return parse(json);
	}

	/**
	 * 转成可以保存到数据的json字符串
	 * 
	 * @param bean
	 * @return
	 */
	public static String beanToJsonForSave(Object bean) {
		String json = gson.toJson(bean);
		String saveJson = json.replace("\"", "\\\"");
		return saveJson;
	}

	public static <T> T jsonToBean(String json, Class<T> classOfT) {
		try {
			return gson.fromJson(json, classOfT);
		} catch (JsonSyntaxException e) {
			return null;
		}
	}

	public static <T> T jsonToBean(String json, Type typeOfT) {
		try {
			return gson.fromJson(json, typeOfT);
		} catch (JsonSyntaxException e) {
			return null;
		}
	}

	public static <T> T jsonToBean(Reader json, Class<T> classOfT) {
		try {
			return gson.fromJson(json, classOfT);
		} catch (JsonSyntaxException e) {
			return null;
		}
	}

	private static String parse(String json) {
		String result = "";
		char lastCh = ' ';
		int count = 0;
		char[] array = json.toCharArray();
		boolean flag = false;
		for (int i = 0; i < array.length; ++i) {
			char ch = array[i];
			if (ch == '[') {
				++count;
				flag = true;
			} else if (ch == ']')
				--count;
			if (ch == '"' && lastCh == '{') {
				result += "\n\t";
			}
			result += ch;
			if (count == 1 && array[i + 1] != ',' && array[i + 1] != ']')
				result += "\n\t\t";
			else if (count == 1 && array[i + 1] == ']')
				result += "\n\t";
			else if (count == 0 && flag)
				result += "\n";
			else if (!flag && ch == ',')
				result += "\n\t";
			lastCh = ch;
		}
		return result;
	}

}
