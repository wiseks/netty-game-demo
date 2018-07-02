package com.rpg.logic.config.disruptor.event;

import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.Resource;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lmax.disruptor.EventHandler;
import com.rpg.framework.util.JavaSerializer;

@Component
public class DisruptorEventHandler implements EventHandler<DisruptorEvent>{
	
	@Autowired
    private AmqpTemplate rabbitTemplate;
     
    @Resource(name="taskQueue") //注入name="taskQueue"的Queue
    private Queue queue;
    
    AtomicInteger index = new AtomicInteger(0);
	long current = 0;

	@Override
	public void onEvent(DisruptorEvent event, long sequence, boolean endOfBatch) throws Exception {
//		System.out.println(">>>>>>>>>>>>>>>>>>"+event);
		if(current==0){
			current = System.currentTimeMillis();
		}
		JavaSerializer ser = new JavaSerializer();
		byte[] bytes = ser.serialize(event.getMythInvocation());
		rabbitTemplate.convertAndSend(queue.getName(), bytes); // 队列名称，消息
		int value = index.incrementAndGet();
		if(value==10000){
			System.out.println(System.currentTimeMillis()-current);
		}
	}

}
