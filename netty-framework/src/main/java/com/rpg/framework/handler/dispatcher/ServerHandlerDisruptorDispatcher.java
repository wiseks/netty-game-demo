package com.rpg.framework.handler.dispatcher;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;

import com.lmax.disruptor.dsl.Disruptor;
import com.rpg.framework.code.Request;
import com.rpg.framework.config.ServerConfig;
import com.rpg.framework.session.AbstractUserSession;
import com.rpg.framework.session.DisruptorUserSession;
import com.rpg.framework.session.SessionHolder;

import io.netty.channel.ChannelHandlerContext;

/**
 * DisruptorDispatcher
 * 
 * @author wudeji
 *
 */
public class ServerHandlerDisruptorDispatcher implements IHandlerDispatcher<Object>, DisposableBean {

	private final Log log = LogFactory.getLog(this.getClass());

	@Autowired
	protected SessionHolder<Object> sessionHolder;

	private Disruptor<DispatcherEvent> disruptor;

	final AtomicInteger index = new AtomicInteger(1);

	ThreadFactory factory = new ThreadFactory() {

		@Override
		public Thread newThread(Runnable r) {
			return new Thread(null, r, "disruptor-worker-thread-" + index.getAndIncrement());
		}
	};

	@Override
	public void dispatch(Request command, ChannelHandlerContext context) {

		AbstractUserSession<Object> session = sessionHolder.get(context.channel());
		if (session != null) {
			DispatcherEvent event = new DispatcherEvent(context, command);
			session.execute(event);
		} else {
			log.error("session is null,cmd=" + command.getCmd());
		}

	}

	@Override
	public void destroy() throws Exception {
		disruptor.shutdown();
	}

	@Override
	public void channelActive(ChannelHandlerContext channelContext, HandlerDispatcherMapping mapping,
			SessionHolder<Object> sessionHolder, ServerConfig serverConfig) {
		AbstractUserSession<Object> session = sessionHolder.get(channelContext.channel());
		if (session == null) {
			DisruptorUserSession<Object> session1 = new DisruptorUserSession<Object>(channelContext, mapping, sessionHolder, serverConfig);
			session1.setFactory(factory);
			session1.start();
			sessionHolder.put(session1.getChannel(), session1);
		}
	}

}
