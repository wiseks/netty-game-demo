package com.rpg.logic.utils;

import java.io.InputStreamReader;
import java.util.Properties;

import org.apache.commons.lang.math.NumberUtils;

/**
 * 
 * 系统信息配置文件 先讀取語言版本下的記錄，找不到時再看全局配置
 * 
 */
public final class ConfigLib {

	private ConfigLib() {
	}

	/**
	 * 全局配置信息
	 */
	private static Properties setting;
	/**
	 * 全局配置信息
	 */
	private static Properties properties;

	static {
		load();
	}

	public static void load() {
		try {
			Properties temp = new Properties();
			temp.load(new InputStreamReader(ConfigLib.class.getResourceAsStream("/setting.properties"), "UTF-8"));
			setting = temp;

			temp = new Properties();
			temp.load(new InputStreamReader(ConfigLib.class.getResourceAsStream("/config.properties"), "UTF-8"));
			properties = temp;

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String strValue(String key) {
		if (setting != null && setting.getProperty(key) != null) {
			return setting.getProperty(key);
		}

		if (properties != null) {
			return properties.getProperty(key);
		}
		return "";
	}


	public static long longValue(String key) {
		String value = null;
		if (setting != null)
			value = setting.getProperty(key);
		if (value == null && properties != null)
			value = properties.getProperty(key);
		if (value != null && NumberUtils.isDigits(value)) {
			return Long.valueOf(value);
		}
		return 0;
	}

	public static Boolean boolValue(String key) {
		String value = null;
		if (setting != null)
			value = setting.getProperty(key);
		if (value == null && properties != null)
			value = properties.getProperty(key);
		if (value != null) {
			try {
				return Boolean.valueOf(value);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	public static double doubleValue(String key) {
		String value = null;
		if (setting != null)
			value = setting.getProperty(key);
		if (value == null && properties != null)
			value = properties.getProperty(key);
		if (value != null) {
			try {
				return Double.valueOf(value);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return 0.0;
	}

}
