package com.rpg.framework.session;

import java.util.concurrent.atomic.AtomicInteger;

import com.rpg.framework.config.ServerConfig;
import com.rpg.framework.handler.dispatcher.DispatcherEvent;
import com.rpg.framework.handler.dispatcher.HandlerDispatcherMapping;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

public abstract class AbstractUserSession<K> {

	protected ChannelHandlerContext channelContext;
	protected HandlerDispatcherMapping mapping;
	protected ServerConfig serverConfig;
	protected final SessionHolder<K> sessionHolder;

	protected K id;
	protected AtomicInteger messageCount = new AtomicInteger();

	public AbstractUserSession(ChannelHandlerContext channelContext, HandlerDispatcherMapping mapping,
			ServerConfig serverConfig, SessionHolder<K> sessionHolder) {
		this.channelContext = channelContext;
		this.mapping = mapping;
		this.serverConfig = serverConfig;
		this.sessionHolder = sessionHolder;
	}

	public abstract void execute(DispatcherEvent event);
	
	public void closeSession(){};

	public int incrementAndGet() {
		return messageCount.incrementAndGet();
	}

	public int decrementAndGet() {
		return messageCount.decrementAndGet();
	}

	public K getId() {
		return id;
	}

	public void setId(K id) {
		this.id = id;
	}

	public Channel getChannel() {
		return channelContext.channel();
	}

	public ChannelHandlerContext getChannelContext() {
		return channelContext;
	}
}
