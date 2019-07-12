package com.rpg.framework.handler.dispatcher;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

import com.google.protobuf.Message;
import com.rpg.framework.annotation.MessageController;
import com.rpg.framework.annotation.MessageRequest;
import com.rpg.framework.config.ServerConfig;
import com.rpg.framework.session.AbstractUserSession;
import com.rpg.framework.session.SessionHolder;

import io.netty.channel.ChannelHandlerContext;

/**
 * 
 * @author wudeji
 *
 */
@Service
public class HandlerDispatcherMapping {

	private final Log log = LogFactory.getLog(this.getClass());
	
	@Autowired
	private ApplicationContext applicationContext;
	
	@Autowired
	private ServerConfig serverConfig;
	
	@Autowired
	protected SessionHolder<Object> sessionHolder;
	
	private static final Map<Short, Message> cmd2Message = new HashMap<Short, Message>();
	private static final Map<Class<?>, Short> messageClass2Cmd = new HashMap<Class<?>, Short>();
	private final Map<Class<?>, CommandHandlerHolder> handlers = new LinkedHashMap<Class<?>, CommandHandlerHolder>();
	private final Map<String, Boolean> handlerCloses = new ConcurrentHashMap<String, Boolean>();
	
	
	
	public Message message(short cmd,byte[] bytes) {
		Message msg = cmd2Message.get(cmd);
		if (msg != null) {
			try {
				msg = msg.getParserForType().parseFrom(bytes);
			} catch (Exception e) {
				e.printStackTrace();
				log.error("error:",e.getCause());
				return null;
			}
		} else {
			log.error("ProtobufDecoder error! cmd:[" + cmd + "] is not exist");
			return null;
		}
		return msg;
	}
	
	public boolean check(ChannelHandlerContext context,Message msg){
		CommandHandlerHolder holder = handlers.get(msg.getClass());
		boolean isClose = handlerCloses.get(msg.getClass().getSimpleName());
		if (null == holder || null == holder.getMethod() || null == holder.getOwner()) {
			log.error("RECIVE|No handler or method for cmd:" + msg);
			return false;
		}
		if(isClose){
			log.error("RECIVE|Close handler or method for cmd:" + msg);
			return false;
		}
		if (!serverConfig.isDebug()) { // 是否调试模式
			if (holder.noCheck()) { // 有些协议不需要如何安全校验，比如登录，看战报
				return true;
			} else {
				if (holder.checkLogin()) {// 是否需要登录后才能操作
					if (!sessionHolder.isOnline(context.channel())) {
						context.channel().close();
						log.warn("no login1...");
						return false;
					}else{
						AbstractUserSession<Object> session = sessionHolder.get(context.channel());
						if(session.getId()==null){
							context.channel().close();
							log.warn("no login2...");
							return false;
						}
					}
				}
			}
		}
		return true;
	}
	
	public CommandHandlerHolder getHolder(Class<?> clazz){
		return handlers.get(clazz);
	}
	
	@PostConstruct
	private void init() {
		
		// 从sping上下文取出所有消息处理器
		Map<String, Object> handlerMap = applicationContext.getBeansWithAnnotation(MessageController.class);
		for (Object obj : handlerMap.values()) {
			Class<?> clazz = obj.getClass();

			// 找到所有处理方法
			Method[] methods = clazz.getMethods();
//			Method[] methods = clazz2.getMethods();
			Map<String,Method> map = new HashMap<>();
			for (Method method : methods) {
				// 判断处理方法是否符合要求
				MessageRequest cmd = method.getAnnotation(MessageRequest.class);
				Class<?>[] paramTypes = method.getParameterTypes();
				if (cmd == null || paramTypes.length == 0) {
					continue;
				}
				map.put(method.getName(), method);
				for(Class<?> paramType : paramTypes){
					if (paramType.getSuperclass() == null) {
						continue;
					}
					
					if (!paramType.getSuperclass().getSimpleName().equals("GeneratedMessage")){
						continue;
					}
					
					String className = paramType.getName();
					try {
						Class<?> clazz1 = ClassUtils.forName(className, ClassUtils.getDefaultClassLoader());
						Method getDefaultInstance = ReflectionUtils.findMethod(clazz1, "getDefaultInstance");
						if (getDefaultInstance != null) {
							String[] params = className.split("_");
							Short shortCmd = Short.valueOf(params[1]);
							Message existMessage = cmd2Message.get(shortCmd);
							if (existMessage != null) {
								throw new IllegalStateException(String.format("Ambiguous message found. "
										+ "Cannot map message: %s onto: %s, There is already message: %s mapped", clazz,
										shortCmd, existMessage.getClass()));
							}
							Message messageLite = (Message) ReflectionUtils.invokeMethod(getDefaultInstance, null);
							cmd2Message.put(shortCmd, messageLite);
							messageClass2Cmd.put(clazz, shortCmd);
							int paramSize = paramTypes.length;
							CommandHandlerHolder holder = new CommandHandlerHolder(obj, method, paramSize, cmd);
							handlers.put(clazz1, holder);
							handlerCloses.put(clazz1.getSimpleName(), false);
							
							log.info(("START|Init HandlerMethod: " + clazz.getSimpleName() + " --> " + method.getName()
							+ " --> cmd:" + className));
						}
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					} catch (LinkageError e) {
						e.printStackTrace();
					}
					
				}
				// 把处理方法加入缓存中
//				Class<?> mappingClass = getMappingForMethod(method, userType);
			}
		}
		log.info("START|Init HandlerMethod Count: " + handlers.size());
	}
	
	
//	@PostConstruct
//	private void init() {
//		
//		// 从sping上下文取出所有消息处理器
//		Map<String, Object> handlerMap = applicationContext.getBeansWithAnnotation(MessageController.class);
//		for (Object obj : handlerMap.values()) {
//			Class<?> clazz = obj.getClass();
//			System.out.println(">>>>>>>>>>>>>>>clazz>>>>>>>>>>>>>>>>"+clazz);
//			ProxyFactory proxyFactory = new ProxyFactory();
//			Object proxyObj = obj;// proxyFactory.getProxyInstance(clazz);
//			
//			Class<?> clazz2 = proxyObj.getClass();
//			Field[] fs = obj.getClass().getDeclaredFields();
//			Field[] proxyFs = proxyObj.getClass().getSuperclass().getDeclaredFields();
//			for(Field f : fs){
//				try {
//					f.setAccessible(true);
//					String name = f.getName();
//					for(Field proxyF : proxyFs){
//						proxyF.setAccessible(true);
//						String proxyFname = proxyF.getName();
//						System.out.println(proxyFname);
//						 Object o = applicationContext.getBean(f.getType());
//						 System.out.println(o);
//						if(name.equals(proxyFname)){
//							//proxyF.set(proxyObj, applicationContext.getBean(f.getType()));
//							break;
//						}
//					}
//				} catch (IllegalArgumentException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				} 
////				catch (IllegalAccessException e) {
////					// TODO Auto-generated catch block
////					e.printStackTrace();
////				} 
//			}
//			
//
//			//final Class<?> userType = ClassUtils.getUserClass(obj.getClass());
//
//			// 找到所有处理方法
//			Method[] methods = clazz.getMethods();
////			Method[] methods = clazz2.getMethods();
//			Map<String,Method> map = new HashMap<>();
//			for (Method method : methods) {
//				// 判断处理方法是否符合要求
//				MessageRequest cmd = method.getAnnotation(MessageRequest.class);
//				Class<?>[] paramTypes = method.getParameterTypes();
//				if (cmd == null || paramTypes.length == 0) {
//					continue;
//				}
//				map.put(method.getName(), method);
//				//System.out.println(clazz.getSimpleName()+","+method.getName());
//				for(Method m : clazz2.getMethods()){
//					if(m.getName().equals(method.getName())){
//						System.out.println(">>>>>>>>>>"+m.getName());
////						map.put(m.getName(), m);
//						break;
//					}
//				}
//				for(Class<?> paramType : paramTypes){
//					if(paramType.getSuperclass()!=null){
//						if(paramType.getSuperclass().getSimpleName().equals("GeneratedMessage")){
//							String className = paramType.getName();
//							try {
//								Class<?> clazz1 = ClassUtils.forName(className, ClassUtils.getDefaultClassLoader());
//								Method getDefaultInstance = ReflectionUtils.findMethod(clazz1, "getDefaultInstance");
//								if (getDefaultInstance != null) {
//									String[] params = className.split("_");
//									Short shortCmd = Short.valueOf(params[1]);
//									Message existMessage = cmd2Message.get(shortCmd);
//									if (existMessage != null) {
//										throw new IllegalStateException(String.format("Ambiguous message found. "
//												+ "Cannot map message: %s onto: %s, There is already message: %s mapped", clazz,
//												shortCmd, existMessage.getClass()));
//									}
//									Message messageLite = (Message) ReflectionUtils.invokeMethod(getDefaultInstance, null);
//									cmd2Message.put(shortCmd, messageLite);
//									messageClass2Cmd.put(clazz, shortCmd);
//									int paramSize = paramTypes.length;
//									Method proxyMethod = map.get(method.getName());
//									CommandHandlerHolder holder = new CommandHandlerHolder(proxyObj, proxyMethod, paramSize, cmd);
//									handlers.put(clazz1, holder);
//									handlerCloses.put(clazz1.getSimpleName(), false);
//									
//									log.info(("START|Init HandlerMethod: " + clazz.getSimpleName() + " --> " + method.getName()
//									+ " --> cmd:" + className));
//								}
//							} catch (ClassNotFoundException e) {
//								e.printStackTrace();
//							} catch (LinkageError e) {
//								e.printStackTrace();
//							}
//						}
//					}
//					
//				}
//				// 把处理方法加入缓存中
////				Class<?> mappingClass = getMappingForMethod(method, userType);
//			}
//		}
//		log.info("START|Init HandlerMethod Count: " + handlers.size());
//	}
}
