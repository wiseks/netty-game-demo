package com.rpg.framework.handler.dispatcher.executor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;

import javax.annotation.PostConstruct;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.protobuf.Message;
import com.rpg.framework.code.Request;
import com.rpg.framework.code.Response;
import com.rpg.framework.config.CoreThreadFactory;
import com.rpg.framework.config.ServerConfig;
import com.rpg.framework.handler.dispatcher.CommandHandlerHolder;
import com.rpg.framework.handler.dispatcher.HandlerDispatcherMapping;
import com.rpg.framework.handler.dispatcher.IHandlerDispatcher;
import com.rpg.framework.session.SessionHolder;
import com.rpg.framework.session.UserSession;

import io.netty.channel.ChannelHandlerContext;


/**
 * 命令分发器
 */
//@Service
public class ServerHandlerExecutorDispatcher implements IHandlerDispatcher{

	private final Log log = LogFactory.getLog(this.getClass());
	
	@Autowired
	protected SessionHolder<Object> sessionHolder;
	
	@Autowired
	private HandlerDispatcherMapping mapping;

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
	

	public static String BinToHex(byte[] buf) {
		final char[] digit = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
		if (buf.length < 1)
			return " ";
		StringBuffer result = new StringBuffer(buf.length * 2);
		for (int i = 0; i < buf.length; i++) {
			result.append(digit[(buf[i] >> 4 & 0x0F)]);
			result.append(digit[(buf[i] & 0x0F)]);
			if ((i + 1) % 4 == 0) {
				result.append(" ");
			}
		}

		return result.toString().toLowerCase();
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

				long starttime = System.currentTimeMillis();
				Message msg = mapping.message(cmd.getCmd(),cmd.getBytes());
				if(msg==null){
					return;
				}
				boolean isCheck = mapping.check(context, msg);
				if(!isCheck){
					return;
				}
				CommandHandlerHolder holder = mapping.getHolder(msg.getClass());
				UserSession<Object> session =  sessionHolder.get(context.channel());
				Lock lock = null;
				if(session==null){
					context.channel().close();
					log.error("session is null");
					return;
				}
				//int messageCount = session.incrementAndGet();
				//System.out.println(session+",messageCount:"+messageCount);
//				if(messageCount>serverConfig.getMessageCount()){
//					session.getChannel().close();
//					log.error("session message count is out of ["+serverConfig.getMessageCount()+"],current message count is:"+messageCount+",sessionId="+session.getId());
//					return;
//				}
				lock = session.getLock();
				try {
					cmd.setCtx(context);
					lock.lock();
					if (null != context && context.channel().isActive()) {
						Object returnValue = null;
						if (holder.getParamSize() == 1)
							returnValue = holder.getMethod().invoke(holder.getOwner(), msg);
						if (holder.getParamSize() == 2)
							returnValue = holder.getMethod().invoke(holder.getOwner(), session, msg);
						else if (holder.getParamSize() == 3)
							returnValue = holder.getMethod().invoke(holder.getOwner(), context, session, msg);

						if (returnValue != null && returnValue instanceof Response)
							context.channel().write(returnValue);
						else {
							if (returnValue != null)
								context.channel().close();
						}
						session.decrementAndGet();
					}
				} catch (Exception e) {
					log.error("ERROR|Dispatcher method error occur. head:" + cmd.getCmd(), e);
				} finally {
					if (lock != null)
						lock.unlock();
				}

				long endtime = System.currentTimeMillis();
				long usetime = endtime - starttime;
				if (usetime > 500)
					log.info(">>>>>>>cmd:" + cmd.getCmd() + " use time:" + usetime);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
