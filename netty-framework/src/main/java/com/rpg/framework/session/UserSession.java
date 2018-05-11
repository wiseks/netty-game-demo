package com.rpg.framework.session;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;

/**
 * 用戶通道
 */
public class UserSession<K> {
	
	private Lock lock = new ReentrantLock();

	private ChannelHandlerContext channelContext;
	private K id;

	public UserSession(ChannelHandlerContext channelContext) {
		this.channelContext = channelContext;
	}

	public K getId() {
		return id;
	}
	

	public void setId(K id) {
		this.id = id;
	}

	public Channel getChannel() {
		return channelContext.getChannel();
	}

	public ChannelHandlerContext getChannelContext() {
		return channelContext;
	}

	public Lock getLock() {
		return lock;
	}
	
}
