package com.rpg.framework.test;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


import com.google.protobuf.TextFormat;
import com.rpg.framework.code.Request;
import com.rpg.logic.common.ErrorCode;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class ClientHandler extends ChannelInboundHandlerAdapter {

	private final Log log = LogFactory.getLog(this.getClass());
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		final Request command = (Request) msg;
		int cmd = command.getCmd();//TestGame.protobufMapping.messageCmd(command.getMessage().getClass()).shortValue();
		if (command.getError() == 0) {
			// if (cmd != 12002)
			//log.warn("cmd:" + cmd + "->" + TextFormat.printToString(command.getMessage().toBuilder()));
		} else {
			ErrorCode error = ErrorCode.getErrorCode(command.getError());
			log.warn("cmd:" + cmd + ",error:" + error.getCode() + "->" + error.getDesc());
		}
		if (cmd == TestGame.cmd + 1)
			TestGame.signal.set(false);
	}


}
