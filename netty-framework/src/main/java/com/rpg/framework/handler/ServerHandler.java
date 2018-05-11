package com.rpg.framework.handler;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rpg.framework.annotation.EventClass;
import com.rpg.framework.annotation.EventMethod;
import com.rpg.framework.code.Request;
import com.rpg.framework.config.CoreThreadFactory;
import com.rpg.framework.config.ServerConfig;
import com.rpg.framework.dispatch.CommandDispatcher;
import com.rpg.framework.server.ServerStopEvent;
import com.rpg.framework.session.SessionHolder;


/**
 * 游戏消息接受处理器
 */
@Service
@EventClass
public class ServerHandler<K> extends SimpleChannelHandler {

	private final Log log = LogFactory.getLog(this.getClass());

	@Autowired
	private CommandDispatcher dispatcher;

	@Autowired
	private ServerConfig serverConfig;

	@Autowired
	protected SessionHolder<K> sessionHolder;

	private ExecutorService service;
	
	private static volatile boolean SERVER_RUN = true;

	@PostConstruct
	public void init() {
		service = Executors.newFixedThreadPool(serverConfig.getThreadNum(),
				new CoreThreadFactory("GameHandlerPool"));
	}

	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e)
			throws Exception {

		if (!SERVER_RUN)
			ctx.getChannel().close();

		if (e.getMessage() == null)
			return;

		if (e.getMessage() instanceof Request)
			service.submit(new DispatchThread((Request) e.getMessage(), ctx));

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

	@Override
	public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e)
			throws Exception {
		sessionHolder.onChannelClose(ctx);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e)
			throws Exception {
		log.debug("exceptionCaught:", e.getCause());
	}
}
