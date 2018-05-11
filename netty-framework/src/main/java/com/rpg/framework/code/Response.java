package com.rpg.framework.code;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

import com.google.protobuf.Message;
import com.rpg.framework.protobuf.ProtobufMapping;

public class Response {

	private final Log log = LogFactory.getLog(this.getClass());

	// 具体业务逻辑

	private byte flag; // TODO 或许会用来表示是否压缩
	private short cmd;
	private short error; // 0正常 大于0为错误码

	private Message message;

	private Response( Message message) {
		this.cmd = ProtobufMapping.classCmd(message.getClass().getName());
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

	// /**
	// * 带错误码提示
	// */
	// public static Response createErrResponse(ErrorCode code) {
	// return new Response(code, null);
	// }

	public ChannelBuffer encode() {
		// 包头9个字节
		// len 4
		// flag 1
		// cmd 2
		// error 2
		ChannelBuffer totalBuffer = ChannelBuffers.dynamicBuffer();
		byte[] data = new byte[0];
		if (message != null)
			data = message.toByteArray();

		totalBuffer.writeInt(data.length + ProtobufMapping.HEAD_SZIE);
		totalBuffer.writeByte(flag);
		totalBuffer.writeShort(cmd);
		totalBuffer.writeShort(error);
		totalBuffer.writeBytes(data);

		// TODO 测试打印压缩
		// byte[] c_data = ZLibUtils.compress(data);
		//
		// if (data.length > 1024)
		// log.info("cmd " + cmd + " data packet size:" + (data.length +
		// ProtobufMapping.HEAD_SZIE)
		// + " -> compress size:" + (c_data.length +
		// ProtobufMapping.HEAD_SZIE));

		return totalBuffer;
	}

	public Message getMessage() {
		return message;
	}

	public short getCmd() {
		return cmd;
	}

}
