package com.rpg.framework.handler;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.PostConstruct;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rpg.framework.annotation.EventClass;
import com.rpg.framework.annotation.EventMethod;
import com.rpg.framework.code.Request;
import com.rpg.framework.config.CoreThreadFactory;
import com.rpg.framework.config.ServerConfig;
import com.rpg.framework.server.ServerStopEvent;
import com.rpg.framework.session.SessionHolder;
import com.rpg.framework.session.UserSession;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;


/**
 * 游戏消息接受处理器
 */
@io.netty.channel.ChannelHandler.Sharable
@Service
@EventClass
public class ServerHandler<K> extends ChannelInboundHandlerAdapter {

	private final Log log = LogFactory.getLog(this.getClass());

	@Autowired
	private ServerHandlerDispatcher dispatcher;

	@Autowired
	private ServerConfig serverConfig;

	@Autowired
	protected SessionHolder<K> sessionHolder;

	private ExecutorService service;
	
	private static volatile boolean SERVER_RUN = true;
	
	private AtomicInteger i = new AtomicInteger();

	@PostConstruct
	public void init() {
		service = Executors.newFixedThreadPool(serverConfig.getThreadNum(),
				new CoreThreadFactory("GameHandlerPool"));
	}
	
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
			service.submit(new DispatchThread((Request) msg, ctx));
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


	class DispatchThread implements Runnable {
		private Request command;
		private ChannelHandlerContext ctx;

		DispatchThread(Request command, ChannelHandlerContext ctx) {
			this.command = command;
			this.ctx = ctx;
		}

		public void run() {
			try {
				dispatcher.dispatch(command, ctx);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	@EventMethod
	private void serverStop(ServerStopEvent event){
		SERVER_RUN = false;
		log.info("server close,messageReceived close");
	}

}
