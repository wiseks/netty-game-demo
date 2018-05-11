package com.rpg.logic.utils;

import java.io.InputStreamReader;
import java.util.Properties;

public enum LanguageType {

	TW("中文繁体", 1, "tw"), EN("英文", 2, "en"), KR("韩文", 3, "kr"), VN("越南文", 4, "vn"), JP("日文", 5, "jp"), TH("泰文", 6, "th"), CN(
			"中文简体", 7, "cn");

	private String name;
	private int column;
	private String suffix;

	private final static String LOCAL;

	static {
		Properties setting = new Properties();
		try {
			setting.load(new InputStreamReader(ConfigLib.class.getResourceAsStream("/setting.properties"), "UTF-8"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		LOCAL = setting.getProperty("LOCALE");
	}

	private LanguageType(String name, int column, String suffix) {
		this.name = name;
		this.column = column;
		this.suffix = suffix;
	}

	public String getName() {
		return name;
	}

	public String getSuffix() {
		return suffix;
	}

	public int getColumn() {
		return column;
	}

	public String getMainPath() {
		return "resource";
	}

	public String getLoaclPath() {
		return "resource_" + this.suffix;
	}

	public static LanguageType get() {
		return LanguageType.valueOf(LOCAL == null ? "tw".toUpperCase() : LOCAL.toUpperCase().trim());
	}

}
