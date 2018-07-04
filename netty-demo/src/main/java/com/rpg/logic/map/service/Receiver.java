package com.rpg.logic.map.service;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.print.attribute.HashAttributeSet;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import com.rpg.framework.util.JavaSerializer;
import com.rpg.logic.player.repository.MythInvocation;


@Component
public class Receiver {

	@Autowired
	private ApplicationContext applicationContext;
	
	private Map<String,Method> map = new HashMap<>();
	
	@RabbitListener(queues = "account") // //监听器监听指定的Queue
    public void receive(byte[] msg) {
//        System.out.println("receiver: " + msg);
        
        JavaSerializer ser = new JavaSerializer();
        MythInvocation my = ser.deSerialize(msg, MythInvocation.class);
        Object bean = applicationContext.getBean(my.getTargetClass());
        Method[] ms = bean.getClass().getMethods();
        Method mh = map.get(my.getMethodName());
        if(mh==null){
        	for(Method m : ms){
            	if(m.getName().equals(my.getMethodName())){
            		mh = m;
            		map.put(my.getMethodName(), mh);
            		break;
            	}
            }
        }
        if(mh!=null){
//        	ReflectionUtils.invokeMethod(mh, bean, my.getArgs());
        }
//        my.getClass().getp
    }
}
