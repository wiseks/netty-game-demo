package com.rpg.logic.config;

import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import com.rpg.logic.config.disruptor.event.DisruptorEvent;
import com.rpg.logic.config.disruptor.event.DisruptorEventFactory;
import com.rpg.logic.config.disruptor.event.DisruptorEventHandler;

@Configuration
public class RequestDisruptorConfiguration{


	@Value("${disruptor.ring-buffer-size}")
	private int queueSize;;
	
	@Autowired
	private DisruptorEventHandler handler;

	@Bean
	public Disruptor<DisruptorEvent> disruptor() {

		Disruptor<DisruptorEvent> disruptor = new Disruptor<>(new DisruptorEventFactory(), queueSize, r -> {
			AtomicInteger index = new AtomicInteger(1);
			return new Thread(null, r, "disruptor-thread-" + index.getAndIncrement());
		}, ProducerType.MULTI, new YieldingWaitStrategy());

		disruptor.handleEventsWith(handler);
		disruptor.start();

		return disruptor;
	}

}
