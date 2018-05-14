package com.rpg.framework.code;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.protobuf.Message;

public class Response {

	private final Log log = LogFactory.getLog(this.getClass());
	
	private static final char CMD_SEPARATOR = '_';

	// 具体业务逻辑

	private byte flag; // TODO 或许会用来表示是否压缩
	private short cmd;
	private short error; // 0正常 大于0为错误码

	private Message message;

	private Response(Message message) {
		this.cmd = this.classCmd(message.getClass().getName());
		this.message = message;
	}

	public static Response createResponse(Message message) {
		return new Response(message);
	}

	/**
	 * 带错误码提示
	 */
	public static Response createErrResponse(Message message) {
		return new Response(message);
	}
	
	private Short classCmd(String className) {
		String cmd = className.substring(className.indexOf(CMD_SEPARATOR) + 1);
		return Short.valueOf(cmd);
	}

	// /**
	// * 带错误码提示
	// */
	// public static Response createErrResponse(ErrorCode code) {
	// return new Response(code, null);
	// }


	public Message getMessage() {
		return message;
	}

	public short getCmd() {
		return cmd;
	}

	public byte getFlag() {
		return flag;
	}

	public short getError() {
		return error;
	}
	

}
