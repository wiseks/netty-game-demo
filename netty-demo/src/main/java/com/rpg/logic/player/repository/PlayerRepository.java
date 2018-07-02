package com.rpg.logic.player.repository;

import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.Resource;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.rpg.framework.util.JavaSerializer;
import com.rpg.logic.config.disruptor.event.DisruptorEvent;
import com.rpg.logic.config.disruptor.event.DisruptorEventTranslator;

@Component
public class PlayerRepository {

	@Autowired
	private Disruptor<DisruptorEvent> disruptor;

	@Autowired
	private AmqpTemplate rabbitTemplate;

	@Resource(name = "taskQueue") // 注入name="taskQueue"的Queue
	private Queue queue;

	AtomicInteger index = new AtomicInteger(0);
	long current = 0;

	public void update(MythInvocation dto) {

		if (current == 0) {
			current = System.currentTimeMillis();
		}
		JavaSerializer ser = new JavaSerializer();
		byte[] bytes = ser.serialize(dto);
		rabbitTemplate.convertAndSend(queue.getName(), bytes); // 队列名称，消息
		int value = index.incrementAndGet();
		if (value == 10000) {
			System.out.println((System.currentTimeMillis() - current)+"ms");
		}

		// final RingBuffer<DisruptorEvent> ringBuffer =
		// disruptor.getRingBuffer();
		// ringBuffer.publishEvent(new DisruptorEventTranslator(), dto);
	}
}
