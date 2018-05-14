package com.rpg.framework.session;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;


/**
 * 用戶通道
 */
public class UserSession<K> {
	
	private Lock lock = new ReentrantLock();

	private ChannelHandlerContext channelContext;
	private K id;
	
	private AtomicInteger messageCount = new AtomicInteger();
	
	public int incrementAndGet(){
		return messageCount.incrementAndGet();
	}
	
	public int decrementAndGet(){
		return messageCount.decrementAndGet();
	}

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
		return channelContext.channel();
	}

	public ChannelHandlerContext getChannelContext() {
		return channelContext;
	}

	public Lock getLock() {
		return lock;
	}
	
}
