package com.rpg.framework.handler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rpg.framework.annotation.EventClass;
import com.rpg.framework.annotation.EventMethod;
import com.rpg.framework.code.Request;
import com.rpg.framework.handler.dispatcher.IHandlerDispatcher;
import com.rpg.framework.server.ServerStopEvent;
import com.rpg.framework.session.SessionHolder;
import com.rpg.framework.session.UserSession;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;


/**
 * 消息接受处理器
 */
@io.netty.channel.ChannelHandler.Sharable
@Service
@EventClass
public class ServerHandler<K> extends ChannelInboundHandlerAdapter {

	private final Log log = LogFactory.getLog(this.getClass());

	@Autowired
	protected SessionHolder<K> sessionHolder;

	private static volatile boolean SERVER_RUN = true;
	
	@Autowired
	private IHandlerDispatcher handlerDispatcher;
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		UserSession<K> session =  sessionHolder.get(ctx.channel());
		if(session==null){
			session = new UserSession<K>(ctx);
			sessionHolder.put(session.getChannel(),session);
		}
	}
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		if (!SERVER_RUN)
			ctx.channel().close();
		boolean isConn = ctx.channel().isActive();
		if(!isConn){
			return;
		}
		if (msg instanceof Request){
			handlerDispatcher.dispatch((Request) msg, ctx);
		}
	}
	
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		log.info(this.getClass().getName()+" |>>  channel closed……"+sessionHolder.get(ctx.channel()).getId());
		sessionHolder.onChannelClose(ctx);
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		log.info(this.getClass().getName()+" |>> exceptionCaught:"+cause.getMessage(), cause.getCause());
		if(ctx.channel().isActive())
			ctx.channel().close();
		sessionHolder.onChannelClose(ctx);
	}

	
	@EventMethod
	private void serverStop(ServerStopEvent event){
		SERVER_RUN = false;
		log.info("server close,messageReceived close");
	}

}
