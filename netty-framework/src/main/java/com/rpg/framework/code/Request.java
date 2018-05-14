package com.rpg.framework.code;



import io.netty.channel.ChannelHandlerContext;

public class Request {

	private ChannelHandlerContext ctx;
	private int len;
	private byte flag;
	private short cmd; // 看看是用short还是int
	private short error;

	
	private byte[] bytes;

	public Request(int len, byte flag, short cmd, short error, byte[] bytes) {
		this.len = len;
		this.flag = flag;
		this.error = error;
		this.cmd = cmd;
		this.bytes = bytes;
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

	public byte[] getBytes() {
		return bytes;
	}

	public short getError() {
		return error;
	}

}
