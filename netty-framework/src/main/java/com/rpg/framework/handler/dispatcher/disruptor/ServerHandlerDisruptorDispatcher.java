package com.rpg.framework.handler.dispatcher.disruptor;

import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;

import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import com.rpg.framework.code.Request;
import com.rpg.framework.config.ServerConfig;
import com.rpg.framework.handler.dispatcher.HandlerDispatcherMapping;
import com.rpg.framework.handler.dispatcher.IHandlerDispatcher;
import com.rpg.framework.handler.dispatcher.disruptor.event.DispatcherEvent;
import com.rpg.framework.handler.dispatcher.disruptor.event.DisruptorBossEventHandler;
import com.rpg.framework.handler.dispatcher.disruptor.event.DisruptorEventFactory;
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

	@Autowired
	private ServerConfig serverConfig;


	@Autowired
	protected SessionHolder<Object> sessionHolder;

	private Disruptor<DispatcherEvent> disruptor;

	final AtomicInteger index = new AtomicInteger(1);



	private void publishEvent(DispatcherEvent event) {
		final RingBuffer<DispatcherEvent> ringBuffer = disruptor.getRingBuffer();
		long next = ringBuffer.next();
		try {
			DispatcherEvent commandEvent = ringBuffer.get(next);
			commandEvent.setContext(event.getContext());
			commandEvent.setRequest(event.getRequest());
		} finally {
			ringBuffer.publish(next);
		}
	}

	@Override
	public void dispatch(Request command, ChannelHandlerContext context) {
		DispatcherEvent event = new DispatcherEvent();
		event.setContext(context);
		event.setRequest(command);
		this.publishEvent(event);
	}

	@PostConstruct
	public void init() {
		int bufferSize = serverConfig.getBufferSize();
		disruptor = new Disruptor<>(new DisruptorEventFactory(), bufferSize, r -> {
			AtomicInteger index = new AtomicInteger(1);
			return new Thread(null, r, "disruptor-thread-" + index.getAndIncrement());
		}, ProducerType.MULTI, new YieldingWaitStrategy());

		DisruptorBossEventHandler<Object> eventHandler = new DisruptorBossEventHandler<Object>( sessionHolder);
		disruptor.handleEventsWith(eventHandler);
		disruptor.start();
	}

	@Override
	public void destroy() throws Exception {
		disruptor.shutdown();
	}

	@Override
	public void channelActive(ChannelHandlerContext channelContext, HandlerDispatcherMapping mapping,
			SessionHolder<Object> sessionHolder, ServerConfig serverConfig) {
		AbstractUserSession<Object> session =  sessionHolder.get(channelContext.channel());
		if(session==null){
			session = new DisruptorUserSession<Object>(channelContext,mapping,sessionHolder,serverConfig);
			sessionHolder.put(session.getChannel(),session);
		}
	}

}
