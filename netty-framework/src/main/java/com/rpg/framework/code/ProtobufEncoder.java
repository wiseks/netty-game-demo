package com.rpg.framework.code;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelDownstreamHandler;


public class ProtobufEncoder extends SimpleChannelDownstreamHandler {


	@Override
	public void writeRequested(ChannelHandlerContext ctx, MessageEvent e) throws Exception {

		ChannelBuffer totalBuffer = ChannelBuffers.dynamicBuffer();
		Response response = (Response) e.getMessage();
		totalBuffer.writeBytes(response.encode());
		Channels.write(ctx, e.getFuture(), totalBuffer);

		// 包头9个字节
		// len 4
		// flag 1
		// cmd 2
		// error 2
		// ChannelBuffer totalBuffer = ChannelBuffers.dynamicBuffer();
		//
		// Object msg = e.getMessage();
		// Class<?> msgClass = msg.getClass();
		//
		// Message message = null;
		// if (msg instanceof Message) {
		// message = (Message) msg;
		// }
		// if (msg instanceof Builder) {
		// message = ((Builder) msg).build();
		// msgClass = message.getClass();
		// }
		//
		// short cmd = protobufMapping.messageCmd(msgClass);
		//
		// byte[] con = message.toByteArray();
		//
		// int msgLen = con.length;
		// totalBuffer.writeInt(CmdId.HEAD_SZIE + msgLen);
		// totalBuffer.writeByte(0);
		// totalBuffer.writeShort(cmd);
		// totalBuffer.writeShort(2);
		// totalBuffer.writeBytes(con);
		// Channels.write(ctx, e.getFuture(), totalBuffer);
	}
}
