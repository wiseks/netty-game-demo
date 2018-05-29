package com.rpg.framework.session;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.protobuf.Message;
import com.rpg.framework.code.Response;
import com.rpg.framework.config.ServerConfig;
import com.rpg.framework.handler.dispatcher.CommandHandlerHolder;
import com.rpg.framework.handler.dispatcher.DispatcherEvent;
import com.rpg.framework.handler.dispatcher.HandlerDispatcherMapping;

import io.netty.channel.ChannelHandlerContext;

/**
 * 用戶通道
 */
public class UserSession<K> extends AbstractUserSession<K> {

	private final Log log = LogFactory.getLog(this.getClass());

	private Lock lock = new ReentrantLock();


	public UserSession(ChannelHandlerContext channelContext, HandlerDispatcherMapping mapping,
			SessionHolder<K> sessionHolder, ServerConfig serverConfig) {
		super(channelContext, mapping,serverConfig,sessionHolder);
	}

	public void execute(DispatcherEvent event) {
		Message msg = mapping.message(event.getRequest().getCmd(), event.getRequest().getBytes());
		if(msg==null){
			return;
		}
		boolean isCheck = mapping.check(channelContext, msg);
		if(!isCheck){
			return;
		}
//		int messageCount = this.incrementAndGet();
//		System.out.println(this + ",messageCount:" + messageCount);
//		if (messageCount > serverConfig.getMessageCount()) {
//			this.getChannel().close();
//			log.error("session message count is out of [" + serverConfig.getMessageCount()
//					+ "],current message count is:" + messageCount + ",sessionId=" + this.getId());
//			return;
//		}
		CommandHandlerHolder holder = mapping.getHolder(msg.getClass());
		long starttime = System.currentTimeMillis();
		try {
			lock.lock();
			if (null != channelContext && channelContext.channel().isActive()) {
				Object returnValue = null;
				if (holder.getParamSize() == 1)
					returnValue = holder.getMethod().invoke(holder.getOwner(), msg);
				if (holder.getParamSize() == 2)
					returnValue = holder.getMethod().invoke(holder.getOwner(), this, msg);
				else if (holder.getParamSize() == 3)
					returnValue = holder.getMethod().invoke(holder.getOwner(), channelContext, this, msg);

				if (returnValue != null && returnValue instanceof Response)
					channelContext.channel().write(returnValue);
				else {
					if (returnValue != null)
						channelContext.channel().close();
				}
				this.decrementAndGet();
			}
		} catch (Exception e) {
			log.error("ERROR|Dispatcher method error occur. head:" + event.getRequest().getCmd(), e);
		} finally {
			if (lock != null)
				lock.unlock();
		}

		long endtime = System.currentTimeMillis();
		long usetime = endtime - starttime;
		if (usetime > 500)
			log.info(">>>>>>>cmd:" + event.getRequest().getCmd() + " use time:" + usetime);
	}

}
