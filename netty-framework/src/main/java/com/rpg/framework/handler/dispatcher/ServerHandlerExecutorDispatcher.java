package com.rpg.framework.handler.dispatcher;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rpg.framework.code.Request;
import com.rpg.framework.config.ServerConfig;
import com.rpg.framework.executor.GameExecutorService;
import com.rpg.framework.session.AbstractUserSession;
import com.rpg.framework.session.SessionHolder;
import com.rpg.framework.session.UserSession;
import com.rpg.framework.util.IDFactory;

import io.netty.channel.ChannelHandlerContext;


/**
 * 命令分发器
 */
@Service
public class ServerHandlerExecutorDispatcher implements IHandlerDispatcher<Integer>{

	private final Log log = LogFactory.getLog(this.getClass());
	
	@Autowired
	protected SessionHolder<Integer> sessionHolder;
	@Autowired
	private GameExecutorService gameExecutorService;

	public void dispatch(Request cmd, ChannelHandlerContext context) {
		if (cmd == null)
			return;
		
		AbstractUserSession<Integer> session = sessionHolder.get(context.channel());
		if(session!=null){
			UserSession<Integer> userssion = (UserSession<Integer>)session;
			userssion.getId();
//			userssion.addMessage(new DispatcherEvent(context,cmd));
			//service.submit(new DispatchThread1(context));
			
			gameExecutorService.execute(userssion.getId(), new DispatchThread(cmd,context));
		}
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
				AbstractUserSession<Integer> session = sessionHolder.get(context.channel());
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
	
	class DispatchThread1 implements Runnable {
		private ChannelHandlerContext context;
		
		DispatchThread1(ChannelHandlerContext context) {
			this.context = context;
		}
		
		public void run() {
			try {
				AbstractUserSession<Integer> session = sessionHolder.get(context.channel());
				if(session!=null){
					session.execute(null);
				}else{
//					log.error("session is null:cmd="+event.getRequest().getCmd());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}


	@Override
	public void channelActive(ChannelHandlerContext channelContext, HandlerDispatcherMapping mapping,
			SessionHolder<Integer> sessionHolder, ServerConfig serverConfig) {
		AbstractUserSession<Integer> session =  sessionHolder.get(channelContext.channel());
		if(session==null){
			session = new UserSession<Integer>(channelContext,mapping,sessionHolder,serverConfig);
			session.setId(IDFactory.genId());
			sessionHolder.put(session.getChannel(),session);
		}
	}

}
