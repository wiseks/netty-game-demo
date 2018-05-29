package com.rpg.framework.handler.dispatcher;

import com.rpg.framework.code.Request;

import io.netty.channel.ChannelHandlerContext;

/**
 * 消息分发
 * @author wudeji
 *
 */
public interface IHandlerDispatcher {

	
	public void dispatch(Request command,ChannelHandlerContext ctx);
}
