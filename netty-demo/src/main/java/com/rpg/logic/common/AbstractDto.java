package com.rpg.logic.common;

import com.rpg.framework.enums.SQLType;

public abstract class AbstractDto {

	protected SQLType sqlType;

	public SQLType getSqlType() {
		return sqlType;
	}

	public AbstractDto(SQLType sqlType) {
		this.sqlType = sqlType;
	}
	
}
