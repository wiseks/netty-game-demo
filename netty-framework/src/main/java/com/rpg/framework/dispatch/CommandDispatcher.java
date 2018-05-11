package com.rpg.framework.dispatch;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;

import javax.annotation.PostConstruct;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

import com.google.protobuf.Message;
import com.rpg.framework.annotation.MessageController;
import com.rpg.framework.annotation.MessageRequest;
import com.rpg.framework.code.Request;
import com.rpg.framework.code.Response;
import com.rpg.framework.config.ServerConfig;
import com.rpg.framework.session.SessionHolder;
import com.rpg.framework.session.UserSession;


/**
 * 命令分发器
 */
@Service
public class CommandDispatcher{

	private final Log log = LogFactory.getLog(this.getClass());
	
	private static final Map<Short, Message> cmd2Message = new HashMap<Short, Message>();

	private static final Map<Class<?>, Short> messageClass2Cmd = new HashMap<Class<?>, Short>();

	@Autowired
	private ApplicationContext applicationContext;
	
	
	@Autowired
	protected SessionHolder<Object> sessionHolder;

	@Autowired
	private ServerConfig serverConfig;

	private final Map<Class<?>, CommandHandlerHolder> handlers = new LinkedHashMap<Class<?>, CommandHandlerHolder>();
	private final Map<String, Boolean> handlerCloses = new ConcurrentHashMap<String, Boolean>();
	
	public boolean closeMsg(String msgName){
		Boolean isClose = handlerCloses.get(msgName);
		if(isClose==null){
			return false;
		}
		isClose = true;
		handlerCloses.put(msgName, isClose);
		return true;
	}

	public void dispatch(Request cmd, ChannelHandlerContext context) {
		if (cmd == null)
			return;

		long starttime = System.currentTimeMillis();

		CommandHandlerHolder holder = handlers.get(cmd.getMessage().getClass());
		boolean isClose = handlerCloses.get(cmd.getMessage().getClass().getSimpleName());
		if (null == holder || null == holder.method || null == holder.owner) {
			log.error("RECIVE|No handler or method for cmd:" + cmd);
			return;
		}
		if(isClose){
			log.error("RECIVE|Close handler or method for cmd:" + cmd);
			return;
		}
		if (!serverConfig.isDebug()) { // 是否调试模式
			if (holder.noCheck()) { // 有些协议不需要如何安全校验，比如登录，看战报

			} else {
				if (holder.checkLogin()) {// 是否需要登录后才能操作
					if (!sessionHolder.isOnline(context.getChannel())) {
						context.getChannel().close();
						log.warn("no login1...");
						return;
					}else{
						UserSession<Object> session = sessionHolder.get(context.getChannel());
						if(session.getId()==null){
							context.getChannel().close();
							log.warn("no login2...");
							return;
						}
					}
				}
			}
		}
		
		UserSession<Object> session =  sessionHolder.get(context.getChannel());
		Lock lock = null;
		if(session==null){
			session = new UserSession<Object>(context);
			sessionHolder.put(session.getChannel(),session);
		}
		lock = session.getLock();
//		Lock lock = null;
//		if (context.getAttachment() == null) {
//			Attachment attachment = new Attachment();
//			context.setAttachment(attachment);
//		}
//		lock = ((Attachment) context.getAttachment()).getLock();
		try {
			cmd.setCtx(context);
			lock.lock();
			if (null != context && context.getChannel().isConnected()) {
				Object returnValue = null;
				if (holder.getParamSize() == 1)
					returnValue = holder.method.invoke(holder.owner, cmd.getMessage());
				if (holder.getParamSize() == 2)
					returnValue = holder.method.invoke(holder.owner, session, cmd.getMessage());
				else if (holder.getParamSize() == 3)
					returnValue = holder.method.invoke(holder.owner, context, session, cmd.getMessage());

				if (returnValue != null && returnValue instanceof Response)
					context.getChannel().write(returnValue);
				else {
					if (returnValue != null)
						context.getChannel().close();
				}
			}
		} catch (Exception e) {
			log.error("ERROR|Dispatcher method error occur. head:" + cmd.getCmd(), e);
		} finally {
			if (lock != null)
				lock.unlock();
		}

		long endtime = System.currentTimeMillis();
		long usetime = endtime - starttime;
		if (usetime > 500)
			log.info(">>>>>>>cmd:" + cmd.getCmd() + " use time:" + usetime);
	}

	@PostConstruct
	public void init() {
		// 从sping上下文取出所有消息处理器
		Map<String, Object> handlerMap = applicationContext.getBeansWithAnnotation(MessageController.class);

		for (Object obj : handlerMap.values()) {
			Class<?> clazz = obj.getClass();

			//final Class<?> userType = ClassUtils.getUserClass(obj.getClass());

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
									int paramSize = paramTypes.length;
									CommandHandlerHolder holder = new CommandHandlerHolder(obj, method, paramSize, cmd);
									handlers.put(clazz1, holder);
									handlerCloses.put(clazz1.getSimpleName(), false);
								}
							} catch (ClassNotFoundException e) {
								e.printStackTrace();
							} catch (LinkageError e) {
								e.printStackTrace();
							}
						}
					}
					
				}
				// 把处理方法加入缓存中
//				Class<?> mappingClass = getMappingForMethod(method, userType);
				
				log.info(("START|Init HandlerMethod: " + clazz.getSimpleName() + " --> " + method.getName()
						+ " --> cmd:" + cmd.toString()));
			}
		}
		log.info("START|Init HandlerMethod Count: " + handlers.size());
	}

//	protected Class<?> getMappingForMethod(Method method, Class<?> clazz) {
//		MessageMapping methodAnnotation = AnnotationUtils.findAnnotation(method, MessageMapping.class);
//		if (methodAnnotation != null) {
//			return methodAnnotation.value();
//		}
//		return null;
//	}

	/**
	 * 消息处理方法容器
	 */
	public static class CommandHandlerHolder {

		public CommandHandlerHolder() {
		}

		public CommandHandlerHolder(Object owner, Method m, int paramSize, MessageRequest cmd) {
			this.owner = owner;
			this.method = m;
			this.command = cmd;
			this.paramSize = paramSize;
		}

		private Object owner;
		private Method method;
		private MessageRequest command;
		private int paramSize;

		public int getParamSize() {
			return paramSize;
		}

		/**
		 * 管理员协议
		 */
		public boolean isAdmin() {
			return command.admin();
		}

		/**
		 * 是否需要登錄
		 * 
		 * @return
		 */
		public boolean checkLogin() {
			return command.checkLogin();
		}

		/**
		 * 不需要任何安全验证
		 * 
		 * @return
		 */
		public boolean noCheck() {
			return command.nocheck();
		}
	}

	public static String BinToHex(byte[] buf) {
		final char[] digit = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
		if (buf.length < 1)
			return " ";
		StringBuffer result = new StringBuffer(buf.length * 2);
		for (int i = 0; i < buf.length; i++) {
			result.append(digit[(buf[i] >> 4 & 0x0F)]);
			result.append(digit[(buf[i] & 0x0F)]);
			if ((i + 1) % 4 == 0) {
				result.append(" ");
			}
		}

		return result.toString().toLowerCase();
	}

}
