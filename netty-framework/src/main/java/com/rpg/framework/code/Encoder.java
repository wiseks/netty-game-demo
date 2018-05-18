package com.rpg.framework.code;

import com.google.protobuf.Message;
import com.rpg.framework.config.ServerConfig;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class Encoder extends MessageToByteEncoder<Response> {



	@Override
	protected void encode(ChannelHandlerContext ctx, Response msg, ByteBuf out) throws Exception {
		byte[] data = new byte[0];
		Message message = msg.getMessage();
		if (message != null)
			data = message.toByteArray();

		out.writeShort(data.length + ServerConfig.HEAD_SZIE);
		out.writeByte(msg.getFlag());
		out.writeShort(msg.getCmd());
		out.writeShort(msg.getError());
		out.writeBytes(data);
	}
}
