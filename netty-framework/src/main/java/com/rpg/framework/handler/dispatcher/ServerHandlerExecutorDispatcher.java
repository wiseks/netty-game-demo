package com.rpg.framework.handler.dispatcher;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.rpg.framework.code.Request;
import com.rpg.framework.config.CoreThreadFactory;
import com.rpg.framework.config.ServerConfig;
import com.rpg.framework.session.AbstractUserSession;
import com.rpg.framework.session.SessionHolder;
import com.rpg.framework.session.UserSession;

import io.netty.channel.ChannelHandlerContext;


/**
 * 命令分发器
 */
public class ServerHandlerExecutorDispatcher implements IHandlerDispatcher<Object>{

	private final Log log = LogFactory.getLog(this.getClass());
	
	@Autowired
	protected SessionHolder<Object> sessionHolder;
	

	@Autowired
	private ServerConfig serverConfig;
	
	private ExecutorService service;


	public void dispatch(Request cmd, ChannelHandlerContext context) {
		if (cmd == null)
			return;
		service.submit(new DispatchThread(cmd, context));
	}

	@PostConstruct
	private void init() {
		
		service = Executors.newFixedThreadPool(serverConfig.getThreadNum(),new CoreThreadFactory("GameHandlerPool"));
	}
	
	class DispatchThread implements Runnable {
		private Request cmd;
		private ChannelHandlerContext context;

		DispatchThread(Request command, ChannelHandlerContext ctx) {
			this.cmd = command;
			this.context = ctx;
		}

		public void run() {
			try {
				AbstractUserSession<Object> session = sessionHolder.get(context.channel());
				if(session!=null){
					session.execute(new DispatcherEvent(context,cmd));
				}else{
					log.error("session is null:cmd="+cmd.getCmd());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}


	@Override
	public void channelActive(ChannelHandlerContext channelContext, HandlerDispatcherMapping mapping,
			SessionHolder<Object> sessionHolder, ServerConfig serverConfig) {
		AbstractUserSession<Object> session =  sessionHolder.get(channelContext.channel());
		if(session==null){
			session = new UserSession<Object>(channelContext,mapping,sessionHolder,serverConfig);
			sessionHolder.put(session.getChannel(),session);
		}
	}

}
