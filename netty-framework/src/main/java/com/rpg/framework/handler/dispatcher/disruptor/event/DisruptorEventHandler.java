package com.rpg.framework.handler.dispatcher.disruptor.event;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.protobuf.Message;
import com.lmax.disruptor.EventHandler;
import com.rpg.framework.code.Request;
import com.rpg.framework.code.Response;
import com.rpg.framework.config.ServerConfig;
import com.rpg.framework.handler.dispatcher.CommandHandlerHolder;
import com.rpg.framework.handler.dispatcher.HandlerDispatcherMapping;
import com.rpg.framework.handler.dispatcher.IHandlerDispatcher;
import com.rpg.framework.session.SessionHolder;
import com.rpg.framework.session.UserSession;

import io.netty.channel.ChannelHandlerContext;

/**
 * 
 * @author wudeji
 *
 */
public class DisruptorEventHandler implements EventHandler<DisruptorEvent> {
	
	private final Log log = LogFactory.getLog(this.getClass());
	
	

	public DisruptorEventHandler(HandlerDispatcherMapping mapping, SessionHolder<Object> sessionHolder,
			ServerConfig serverConfig) {
		this.mapping = mapping;
		this.sessionHolder = sessionHolder;
		this.serverConfig = serverConfig;
	}

	private final HandlerDispatcherMapping mapping;
	
	protected final SessionHolder<Object> sessionHolder;
	
	private final ServerConfig serverConfig;
	
	@Override
	public void onEvent(DisruptorEvent event, long sequence, boolean endOfBatch) throws Exception {
		Request command = event.getRequest();
		ChannelHandlerContext context = event.getContext();
		try {
			long starttime = System.currentTimeMillis();
			Message msg = mapping.message(command.getCmd(),command.getBytes());
			if(msg==null){
				return;
			}
			boolean isCheck = mapping.check(context, msg);
			if(!isCheck){
				return;
			}
			CommandHandlerHolder holder = mapping.getHolder(msg.getClass());
			UserSession<Object> session =  sessionHolder.get(context.channel());
			if(session==null){
				context.channel().close();
				log.error("session is null");
				return;
			}
			int messageCount = session.incrementAndGet();
			//System.out.println("messageCount:"+messageCount);
			if(messageCount>serverConfig.getMessageCount()){
				session.getChannel().close();
				log.error("session message count is out of ["+serverConfig.getMessageCount()+"],current message count is:"+messageCount+",sessionId="+session.getId());
				return;
			}
			try {
				command.setCtx(context);
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
				log.error("ERROR|Dispatcher method error occur. head:" + command.getCmd(), e);
			} 

			long endtime = System.currentTimeMillis();
			long usetime = endtime - starttime;
			if (usetime > 500)
				log.info(">>>>>>>cmd:" + command.getCmd() + " use time:" + usetime);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
