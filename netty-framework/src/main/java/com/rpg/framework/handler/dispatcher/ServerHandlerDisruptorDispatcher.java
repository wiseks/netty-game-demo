//package com.rpg.framework.handler.dispatcher;
//
//import java.util.Random;
//import java.util.concurrent.ThreadFactory;
//import java.util.concurrent.atomic.AtomicInteger;
//
//import javax.annotation.PostConstruct;
//
//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;
//import org.springframework.beans.factory.DisposableBean;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import com.lmax.disruptor.YieldingWaitStrategy;
//import com.lmax.disruptor.dsl.Disruptor;
//import com.lmax.disruptor.dsl.ProducerType;
//import com.rpg.framework.code.Request;
//import com.rpg.framework.config.ServerConfig;
//import com.rpg.framework.handler.dispatcher.disruptor.event.DisruptorEventFactory;
//import com.rpg.framework.handler.dispatcher.disruptor.event.DisruptorWorkerEventHandler;
//import com.rpg.framework.session.AbstractUserSession;
//import com.rpg.framework.session.DisruptorUserSession;
//import com.rpg.framework.session.SessionHolder;
//
//import io.netty.channel.ChannelHandlerContext;
//
///**
// * DisruptorDispatcher
// * 
// * @author wudeji
// *
// */
//
//@Service
//public class ServerHandlerDisruptorDispatcher implements IHandlerDispatcher<Object> {
//
//	private final Log log = LogFactory.getLog(this.getClass());
//
//	@Autowired
//	protected SessionHolder<Object> sessionHolder;
//	
//	@Autowired
//	private HandlerDispatcherMapping mapping;
//	
//	@Autowired
//	private ServerConfig serverConfig;
//	
//	private Disruptor<Object>[] disruptors;
//
//	final AtomicInteger index = new AtomicInteger(1);
//
//	ThreadFactory factory = new ThreadFactory() {
//
//		@Override
//		public Thread newThread(Runnable r) {
//			return new Thread(null, r, "disruptor-worker-thread-" + index.getAndIncrement());
//		}
//	};
//
//	@Override
//	public void dispatch(Request command, ChannelHandlerContext context) {
//
//		AbstractUserSession<Object> session = sessionHolder.get(context.channel());
//		if (session != null) {
//			DispatcherEvent event = new DispatcherEvent(context, command);
//			session.execute(event);
//		} else {
//			log.error("session is null,cmd=" + command.getCmd());
//		}
//
//	}
//
////	@Override
////	public void destroy() throws Exception {
////		for(int i=0;i<disruptors.length;i++){
////			disruptors[i].shutdown();
////		}
////	}
//
//	@SuppressWarnings("unchecked")
//	@Override
//	public void channelActive(ChannelHandlerContext channelContext, HandlerDispatcherMapping mapping,
//			SessionHolder<Object> sessionHolder, ServerConfig serverConfig) {
//		AbstractUserSession<Object> session = sessionHolder.get(channelContext.channel());
//		if (session == null) {
//			DisruptorUserSession<Object> session1 = new DisruptorUserSession<Object>(channelContext, mapping, sessionHolder, serverConfig);
//			Random random = new Random();
//			int index1 = random.nextInt(1);
//			int index = channelContext.channel().hashCode()%10;
//			if(index<0){
//				index *= -1;
//			}
//			@SuppressWarnings("rawtypes")
//			Disruptor d = disruptors[index1];
//			session1.setDisruptor(d);
//			sessionHolder.put(session1.getChannel(), session1);
//			
//			
//		}
//	}
//	
//	@PostConstruct
//	public void init(){
//		this.start();
//	}
//	
//	@SuppressWarnings({ "unchecked", "rawtypes" })
//	private void start(){
//		disruptors  = new Disruptor[1];
//		for(int i=0;i<disruptors.length;i++){
//			disruptors[i] = new Disruptor(new DisruptorEventFactory(), 256, factory, ProducerType.MULTI, new YieldingWaitStrategy());
//			
//			DisruptorWorkerEventHandler eventHandler = new DisruptorWorkerEventHandler<Object>(mapping, sessionHolder, serverConfig);
//			disruptors[i].handleEventsWith(eventHandler);
//			disruptors[i].start();
//		}
//	}
//
//}
