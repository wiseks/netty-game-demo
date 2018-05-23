package com.rpg.framework.event;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.rpg.framework.annotation.EventClass;
import com.rpg.framework.annotation.EventMethod;

/**
 * 事件
 * @author wudeji
 *
 */
@Component
public class EventBus {
	
	private class Holdler {
		
		private Object owner;
		private Method method;

		private Holdler(Object owner, Method method) {
			this.owner = owner;
			this.method = method;
		}
	}

	@Autowired
	private ApplicationContext context;
	
	private Map<Class<?>,List<Holdler>> classMap = new ConcurrentHashMap<>();
	
	public void post(Object event){
		if(event==null){
			return;
		}
		Class<?> clazz = event.getClass();
		List<Holdler> holders = classMap.get(clazz);
		if(holders!=null){
			for(Holdler holder : holders){
				try {
					holder.method.invoke(holder.owner, event);
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	@PostConstruct
	private void init(){
		Map<String, Object> beans = context.getBeansWithAnnotation(EventClass.class);
		for(String key : beans.keySet()){
			Object bean = beans.get(key);
			Method[] methods = bean.getClass().getDeclaredMethods();
			for(Method method : methods){
				method.setAccessible(true);
				EventMethod eventMethod = method.getAnnotation(EventMethod.class);
				Class<?>[] paramTypes = method.getParameterTypes();
				if (eventMethod == null || paramTypes.length == 0||paramTypes.length>1) {
					continue;
				}
				Class<?> clazz = paramTypes[0];
				Holdler holder = new Holdler(bean,method);
				List<Holdler> holders = classMap.get(clazz);
				if(holders==null){
					holders = new ArrayList<>();
					classMap.putIfAbsent(clazz, holders);
				}
				holders.add(holder);
			}
		}
	}
}
