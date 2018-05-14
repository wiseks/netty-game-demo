package com.rpg.framework.code;

import java.util.List;


import com.rpg.framework.config.ServerConfig;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

public class ProtobufDecoder extends ByteToMessageDecoder {


	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		// 包头9个字节
		// len 4
		// flag 1
		// cmd 2
		// error 2
		if (in.readableBytes() < ServerConfig.HEAD_SZIE) {
			return ;
		}
		in.markReaderIndex();
		int len = in.readInt();
		// 防止超过1024
		if (in.readableBytes() < len - 4) {
			in.resetReaderIndex();
			return ;
		}

		byte flag = in.readByte();
		short cmd = in.readShort();
		short error = in.readShort();
		byte[] conData = new byte[len - ServerConfig.HEAD_SZIE];
		in.readBytes(conData);
		out.add(new Request(len, flag, cmd, error, conData));

	}

}
