package com.rpg.framework.event;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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
	
	private Map<Class<?>,Holdler> classMap = new ConcurrentHashMap<Class<?>, Holdler>();
	
	public void post(Object event){
		if(event==null){
			return;
		}
		Class<?> clazz = event.getClass();
		Holdler holder = classMap.get(clazz);
		if(holder!=null){
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
				if(classMap.containsKey(clazz)){
					throw new RuntimeException("class name= ["+bean.getClass().getName()+"], method name= ["+method.getName()+"], event name = ["+clazz.getName()+"] is not only one");
				}
				classMap.putIfAbsent(clazz, holder);
			}
		}
	}
}
