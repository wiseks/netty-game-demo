package com.rpg.framework.handler.dispatcher.disruptor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.SequenceBarrier;
import com.lmax.disruptor.WorkerPool;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import com.rpg.framework.code.Request;
import com.rpg.framework.config.CoreThreadFactory;
import com.rpg.framework.config.ServerConfig;
import com.rpg.framework.handler.dispatcher.HandlerDispatcherMapping;
import com.rpg.framework.handler.dispatcher.IHandlerDispatcher;
import com.rpg.framework.handler.dispatcher.disruptor.event.DisruptorConsumerEventHandler;
import com.rpg.framework.handler.dispatcher.disruptor.event.DisruptorEvent;
import com.rpg.framework.handler.dispatcher.disruptor.event.DisruptorEventFactory;
import com.rpg.framework.handler.dispatcher.disruptor.event.DisruptorEventHandler;
import com.rpg.framework.handler.dispatcher.disruptor.event.EventExceptionHandler;
import com.rpg.framework.session.SessionHolder;

import io.netty.channel.ChannelHandlerContext;

/**
 * DisruptorDispatcher
 * 
 * @author wudeji
 *
 */
@Service
public class ServerHandlerDisruptorDispatcher implements IHandlerDispatcher, DisposableBean {

	@Autowired
	private ServerConfig serverConfig;

	@Autowired
	private HandlerDispatcherMapping mapping;

	@Autowired
	protected SessionHolder<Object> sessionHolder;

	private Disruptor<DisruptorEvent> disruptor;
	
	private WorkerPool<DisruptorEvent> workerPool;
	
	private ExecutorService service;
	
	private RingBuffer<DisruptorEvent> ringBuffer;

	private void publishEvent(DisruptorEvent event) {
		
//		final RingBuffer<DisruptorEvent> ringBuffer = disruptor.getRingBuffer();
		ringBuffer.publishEvent((event1, seq, arg) -> {
			event1.setContext(event.getContext());
			event1.setRequest(event.getRequest());
		}, event);
		
		//workerPool.halt();
	}

	@Override
	public void dispatch(Request command, ChannelHandlerContext context) {
		DisruptorEvent event = new DisruptorEvent();
		event.setContext(context);
		event.setRequest(command);
		this.publishEvent(event);
	}

	@PostConstruct
	public void init() {
		
		service = Executors.newFixedThreadPool(serverConfig.getThreadNum(),new CoreThreadFactory("GameHandlerPool"));
		int bufferSize = serverConfig.getBufferSize();
		disruptor = new Disruptor<>(new DisruptorEventFactory(), bufferSize, r -> {
			AtomicInteger index = new AtomicInteger(1);
			return new Thread(null, r, "disruptor-thread-" + index.getAndIncrement());
		}, ProducerType.MULTI, new YieldingWaitStrategy());

		ringBuffer = RingBuffer.create(ProducerType.MULTI, new DisruptorEventFactory(),
				bufferSize, new YieldingWaitStrategy());
		SequenceBarrier barriers = ringBuffer.newBarrier();
		
		 // 创建10个消费者来处理同一个生产者发送过来的消息(这10个消费者不重复消费消息)
		DisruptorConsumerEventHandler[] consumers = new DisruptorConsumerEventHandler[50];
        for (int i = 0; i < consumers.length; i++) {
            consumers[i] = new DisruptorConsumerEventHandler(mapping, sessionHolder, serverConfig);
        }
		workerPool = new WorkerPool<DisruptorEvent>(ringBuffer, barriers,
				new EventExceptionHandler(), consumers);

		DisruptorEventHandler eventHandler1 = new DisruptorEventHandler(mapping, sessionHolder, serverConfig);
		DisruptorEventHandler eventHandler2 = new DisruptorEventHandler(mapping, sessionHolder, serverConfig);
		DisruptorEventHandler eventHandler3 = new DisruptorEventHandler(mapping, sessionHolder, serverConfig);
		DisruptorEventHandler eventHandler4 = new DisruptorEventHandler(mapping, sessionHolder, serverConfig);
		DisruptorEventHandler eventHandler5 = new DisruptorEventHandler(mapping, sessionHolder, serverConfig);
//		disruptor.handleEventsWith(eventHandler1, eventHandler2, eventHandler3, eventHandler4, eventHandler5);
//		disruptor.start();
		ringBuffer.addGatingSequences(workerPool.getWorkerSequences());
		workerPool.start(service);
	}

	@Override
	public void destroy() throws Exception {
		disruptor.shutdown();
	}

}
