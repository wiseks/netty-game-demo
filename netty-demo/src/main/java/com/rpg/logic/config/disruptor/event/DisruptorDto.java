package com.rpg.logic.config.disruptor.event;

import java.util.HashMap;
import java.util.Map;

import com.rpg.framework.enums.SQLType;
import com.rpg.logic.common.AbstractDto;

public class DisruptorDto<T> extends AbstractDto {

	private String json;
	
	Class<T> clazz;
	
	
	private Map<String,Object> params = new HashMap<>();

	public String getJson() {
		return json;
	}

	public DisruptorDto(String json, SQLType sqlType, Map<String, Object> params) {
		super(sqlType);
		this.json = json;
		this.params = params;
		
	}



	public SQLType getSqlType() {
		return sqlType;
	}

	public Map<String, Object> getParams() {
		return params;
	}
	
}
