package com.rpg.framework.handler.dispatcher.disruptor.event;

import com.rpg.framework.code.Request;

import io.netty.channel.ChannelHandlerContext;

public class DispatcherEvent {
	
	private ChannelHandlerContext context;

	private Request request;
	

	public DispatcherEvent() {
		
	}

	public DispatcherEvent(ChannelHandlerContext context, Request request) {
		this.context = context;
		this.request = request;
	}
	
	

	public Request getRequest() {
		return request;
	}

	public void setRequest(Request request) {
		this.request = request;
	}

	public ChannelHandlerContext getContext() {
		return context;
	}

	public void setContext(ChannelHandlerContext context) {
		this.context = context;
	}
	
}
