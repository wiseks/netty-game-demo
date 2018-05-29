package com.rpg.framework.session;

import java.util.concurrent.atomic.AtomicInteger;

import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import com.rpg.framework.config.ServerConfig;
import com.rpg.framework.handler.dispatcher.DispatcherEvent;
import com.rpg.framework.handler.dispatcher.HandlerDispatcherMapping;
import com.rpg.framework.handler.dispatcher.disruptor.event.DisruptorEventFactory;
import com.rpg.framework.handler.dispatcher.disruptor.event.DisruptorWorkerEventHandler;

import io.netty.channel.ChannelHandlerContext;


/**
 * 用戶通道
 */
public class DisruptorUserSession<K> extends AbstractUserSession<K> {
	

	private Disruptor<DispatcherEvent> disruptor;
	
	
	public DisruptorUserSession(ChannelHandlerContext channelContext,HandlerDispatcherMapping mapping, SessionHolder<K> sessionHolder,
			ServerConfig serverConfig) {
		super(channelContext,mapping,serverConfig,sessionHolder);
		disruptor = new Disruptor<>(new DisruptorEventFactory(), 256, r -> {
			AtomicInteger index = new AtomicInteger(1);
			return new Thread(null, r, "disruptor-thread-" + index.getAndIncrement());
		}, ProducerType.MULTI, new YieldingWaitStrategy());

		DisruptorWorkerEventHandler<K> eventHandler = new DisruptorWorkerEventHandler<K>(mapping, sessionHolder, serverConfig);
		disruptor.handleEventsWith(eventHandler);
		disruptor.start();
	}
	
	public void execute(DispatcherEvent event){
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
	public void closeSession() {
		disruptor.shutdown();
	}
	
}
