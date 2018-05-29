package com.rpg.framework.handler.dispatcher;

import java.lang.reflect.Method;

import com.rpg.framework.annotation.MessageRequest;

public class CommandHandlerHolder {
	public CommandHandlerHolder(Object owner, Method m, int paramSize, MessageRequest cmd) {
		this.owner = owner;
		this.method = m;
		this.command = cmd;
		this.paramSize = paramSize;
	}

	private Object owner;
	private Method method;
	private MessageRequest command;
	private int paramSize;
	
	

	public Object getOwner() {
		return owner;
	}

	public Method getMethod() {
		return method;
	}

	public MessageRequest getCommand() {
		return command;
	}

	public int getParamSize() {
		return paramSize;
	}

	/**
	 * 管理员协议
	 */
	public boolean isAdmin() {
		return command.admin();
	}

	/**
	 * 是否需要登錄
	 * 
	 * @return
	 */
	public boolean checkLogin() {
		return command.checkLogin();
	}

	/**
	 * 不需要任何安全验证
	 * 
	 * @return
	 */
	public boolean noCheck() {
		return command.nocheck();
	}
}
