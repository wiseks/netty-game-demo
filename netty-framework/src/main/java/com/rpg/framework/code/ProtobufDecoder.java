package com.rpg.framework.code;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.frame.FrameDecoder;


import com.google.protobuf.Message;
import com.rpg.framework.protobuf.ProtobufMapping;

public class ProtobufDecoder extends FrameDecoder {

	private final Log log = LogFactory.getLog(this.getClass());

	private ProtobufMapping protobufMapping;

	public ProtobufDecoder(ProtobufMapping protobufMapping) {
		this.protobufMapping = protobufMapping;
	}

	@Override
	protected Object decode(ChannelHandlerContext ctx, Channel channel, ChannelBuffer buffer) throws Exception {
		// 包头9个字节
		// len 4
		// flag 1
		// cmd 2
		// error 2
		if (buffer.readableBytes() < ProtobufMapping.HEAD_SZIE) {
			return null;
		}

		int len = buffer.readInt();

		// 防止超过1024
		if (buffer.readableBytes() < len - 4) {
			buffer.resetReaderIndex();
			return null;
		}

		byte flag = buffer.readByte();
		short cmd = buffer.readShort();
		short error = buffer.readShort();

		// log.info("len:" + len + ",flag:" + flag + ",cmd:" + cmd + ",error:"
		// + error);

		byte[] conData = new byte[len - ProtobufMapping.HEAD_SZIE];

		buffer.readBytes(conData);

		Message msg = protobufMapping.message(cmd);

		if (msg != null) {
			Message message = null;
			try {
				message = msg.getParserForType().parseFrom(conData);
			} catch (Exception e) {
				ctx.getChannel().close();
				e.printStackTrace();
			}

			// log.info("msg=" + message.toString());

			return new Request(len, flag, cmd, error, message);
		} else {
			ctx.getChannel().close();
			log.error("ProtobufDecoder error! cmd:["+cmd+"] is not exist");
		}

		return null;
	}

}
