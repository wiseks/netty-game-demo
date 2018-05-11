package com.rpg.logic.utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Random;

public class PlayerNameUtil {
	private static String[] firstNames;
	private static String[] manLastNames1;
	private static String[] manLastNames2;
	private static String[] womanLastNames1;
	private static String[] womanLastNames2;

	public static final char[] characters = new char[] { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L',
			'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g',
			'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1',
			'2', '3', '4', '5', '6', '7', '8', '9' };

	/**
	 * 随机性别
	 * 
	 * @return 随机获取名称
	 */
	public static String randomName() {
		return randomName(new Random().nextInt(2) + 1);
	}

	/**
	 * @param gender
	 *            性别,1男 2女
	 * @return 随机获取名称
	 */
	public static String randomName(int gender) {
		if (gender != 1 && gender != 2)
			throw new IllegalArgumentException("gender must match : 1 or 2.");
		String[] firstNames = PlayerNameUtil.firstNames;
		String[] lastNames1 = gender == 1 ? PlayerNameUtil.manLastNames1 : PlayerNameUtil.womanLastNames1;
		String[] lastNames2 = gender == 1 ? PlayerNameUtil.manLastNames2 : PlayerNameUtil.womanLastNames2;
		Random random = new Random();
		StringBuilder builder = new StringBuilder();
		builder.append(firstNames[random.nextInt(firstNames.length)]);
		// 名长度
		int len = random.nextInt(2) + 1;
		if (len == 1) {
			// 一位名,随机选取数组
			String[] tmp = random.nextInt(2) == 0 ? lastNames1 : lastNames2;
			builder.append(tmp[random.nextInt(tmp.length)]);
		} else {
			// 二位名
			builder.append(lastNames1[random.nextInt(lastNames1.length)]);
			builder.append(lastNames2[random.nextInt(lastNames2.length)]);
		}
		return builder.toString();
	}

	public static void load() {
		try {
			InputStream is = PlayerNameUtil.class.getResourceAsStream(FileConstants.PLAYER_RANDOM_NAME);
			Properties prop = new Properties();
			prop.load(is);
			firstNames = prop.getProperty("n1").split("\\,");
			manLastNames1 = prop.getProperty("n2").split("\\,");
			manLastNames2 = prop.getProperty("n3").split("\\,");
			womanLastNames1 = prop.getProperty("n4").split("\\,");
			womanLastNames2 = prop.getProperty("n5").split("\\,");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		load();
	}
}
