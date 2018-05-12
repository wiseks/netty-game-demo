package com.rpg.framework.test;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

import com.google.protobuf.Message;
import com.rpg.framework.annotation.MessageController;
import com.rpg.framework.annotation.MessageRequest;

@Component
public class ProtobufMapping{
	
//	public void setApplicationContext(ApplicationContext appCtx) throws BeansException {
//		applicationContext = appCtx;
//	}
	
	@Autowired
	private ApplicationContext applicationContext;
	
	public static final int HEAD_SZIE = 9;

	//private static final String DEFATULT_PROTOBUF_PACKAGE = "xn/protobuf";

	private static final char CMD_SEPARATOR = '_';


	private static final Map<Short, Message> cmd2Message = new HashMap<Short, Message>();

	private static final Map<Class<?>, Short> messageClass2Cmd = new HashMap<Class<?>, Short>();

	
	@PostConstruct
	private void init() {
		
		if (!cmd2Message.isEmpty()) {
			return;
		}
		if(applicationContext==null){
			return;
		}
		// 从sping上下文取出所有消息处理器
		Map<String, Object> handlerMap = applicationContext.getBeansWithAnnotation(MessageController.class);

		for (Object obj : handlerMap.values()) {
			Class<?> clazz = obj.getClass();

			// 找到所有处理方法
			Method[] methods = clazz.getMethods();
			for (Method method : methods) {
				// 判断处理方法是否符合要求
				MessageRequest cmd = method.getAnnotation(MessageRequest.class);
				Class<?>[] paramTypes = method.getParameterTypes();
				if (cmd == null || paramTypes.length == 0) {
					continue;
				}
				for(Class<?> paramType : paramTypes){
					if(paramType.getSuperclass()!=null){
						if(paramType.getSuperclass().getSimpleName().equals("GeneratedMessage")){
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
								}
							} catch (ClassNotFoundException e) {
								e.printStackTrace();
							} catch (LinkageError e) {
								e.printStackTrace();
							}
						}
					}
					
				}
			}
		}
	}


	public Message message(short cmd) {
		return cmd2Message.get(cmd);
	}

	public Message message(short cmd, byte[] byteData) throws Exception {
		Message message = cmd2Message.get(cmd);
		if (message != null) {
			return message.getParserForType().parseFrom(byteData);
		} else {
		}
		return message;
	}

	public Short messageCmd(Class<?> clazz) {
		return messageClass2Cmd.get(clazz);
	}

}
