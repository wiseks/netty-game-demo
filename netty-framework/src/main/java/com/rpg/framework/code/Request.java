package com.rpg.framework.code;

import org.jboss.netty.channel.ChannelHandlerContext;


import com.google.protobuf.Message;

public class Request {

	private ChannelHandlerContext ctx;
	private int len;
	private byte flag;
	private short cmd; // 看看是用short还是int
	private short error;

	private Message message;

	public Request() {
	}

	public Request(int len, byte flag, short cmd, short error, Message message) {
		this.len = len;
		this.flag = flag;
		this.error = error;
		this.cmd = cmd;
		this.message = message;
	}

	public ChannelHandlerContext getCtx() {
		return ctx;
	}

	public void setCtx(ChannelHandlerContext ctx) {
		this.ctx = ctx;
	}

	public int getLen() {
		return len;
	}

	public short getCmd() {
		return this.cmd;
	}

	public Message getMessage() {
		return message;
	}

	public short getError() {
		return error;
	}

}
