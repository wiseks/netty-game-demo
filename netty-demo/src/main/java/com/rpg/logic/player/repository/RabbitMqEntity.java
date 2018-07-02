package com.rpg.logic.player.repository;

import java.io.Serializable;

import com.rpg.framework.enums.SQLType;

public class RabbitMqEntity implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Object objct;
	
	private int sqlType;

	public RabbitMqEntity(Object objct, SQLType sqlType) {
		this.objct = objct;
		this.sqlType = 0;
	}

	public Object getObjct() {
		return objct;
	}

	public int getSqlType() {
		return sqlType;
	}

}
