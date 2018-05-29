package com.rpg.framework.handler.dispatcher;

import com.rpg.framework.code.Request;
import com.rpg.framework.config.ServerConfig;
import com.rpg.framework.session.SessionHolder;

import io.netty.channel.ChannelHandlerContext;

/**
 * 消息分发
 * @author wudeji
 *
 */
public interface IHandlerDispatcher<K> {

	
	public void dispatch(Request command,ChannelHandlerContext ctx);
	
	public void channelActive(ChannelHandlerContext channelContext,HandlerDispatcherMapping mapping, SessionHolder<K> sessionHolder,
			ServerConfig serverConfig);
}
