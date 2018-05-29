package com.rpg.framework.handler.dispatcher.disruptor.event;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lmax.disruptor.EventHandler;
import com.rpg.framework.config.ServerConfig;
import com.rpg.framework.handler.dispatcher.DispatcherEvent;
import com.rpg.framework.handler.dispatcher.HandlerDispatcherMapping;
import com.rpg.framework.session.SessionHolder;
import com.rpg.framework.session.AbstractUserSession;
import com.rpg.framework.session.DisruptorUserSession;

import io.netty.channel.ChannelHandlerContext;

/**
 * 
 * @author wudeji
 *
 */
public class DisruptorBossEventHandler<K> implements EventHandler<DispatcherEvent> {
	
	private final Log log = LogFactory.getLog(this.getClass());
	
	

	public DisruptorBossEventHandler(SessionHolder<K> sessionHolder) {
		this.sessionHolder = sessionHolder;
	}

	
	protected final SessionHolder<K> sessionHolder;
	
	
	@Override
	public void onEvent(DispatcherEvent event, long sequence, boolean endOfBatch) throws Exception {
		ChannelHandlerContext context = event.getContext();
		AbstractUserSession<K> session = sessionHolder.get(context.channel());
		if(session!=null){
			session.execute(event);
		}else{
			context.channel().close();
			log.error("session is null : event="+event);
		}
	}

}
