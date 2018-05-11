package com.rpg.logic.utils;

import java.io.InputStream;

/**
 * 保存各种配置文件名称
 * 
 * 
 */
public class FileConstants {

	/**
	 * 存放用来测试的配置文件的路径
	 */
	public static final String CONFIG_TEST = "config_test";


	/**
	 * 系统配置文件 无国际化影响
	 */
	public static final String CONFIG_RES = "config";

	/**
	 * 敏感词
	 */
	public static final String BAD_WORDS = "/game_resources/words.property";
	/**
	 * 敏感词
	 */
	public static final String BAD_NAMES = "/game_resources/names.property";

	/**
	 * 随机名字
	 */
	public static final String PLAYER_RANDOM_NAME = "/game_resources/PlayerNames.properties";

	public static InputStream loadResouce(String fileName) {
		InputStream file = null;

		String path = "/" + FileConstants.CONFIG_TEST + fileName;
		try {
			file = FileConstants.class.getResourceAsStream(path);
			if (file == null) {
				path = "/" + LanguageType.get().getLoaclPath() + fileName;
				file = FileConstants.class.getResourceAsStream(path);
				if (file == null) {
					path = "/" + LanguageType.get().getMainPath() + fileName;
					file = FileConstants.class.getResourceAsStream(path);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return file;
	}
}
